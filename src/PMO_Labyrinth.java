import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PMO_Labyrinth implements LabyrinthInterface {

    private Position currentPosition;
    private ProgrammableLabyrinth labyrinth;
    private Set<Position> required;


    public PMO_Labyrinth() {
        labyrinth = new ProgrammableLabyrinth();
        required = new HashSet<>();
    }

    private PMO_Labyrinth(Position currentPosition, ProgrammableLabyrinth labyrinth) {
        this.currentPosition = currentPosition;
        this.labyrinth = labyrinth;
    }

    private class Position {

        final int row;
        final int col;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return row == position.row &&
                    col == position.col;
        }

        @Override
        public int hashCode() {

            return Objects.hash(row, col);
        }

        Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        Position next(Direction direction) {
            switch (direction) {
                case NORTH: {
                    return new Position(row + 1, col);
                }
                case SOUTH: {
                    return new Position(row - 1, col);
                }
                case EAST: {
                    return new Position(row, col + 1);
                }
                case WEST: {
                    return new Position(row, col - 1);
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "[ " + col + ", " + row + " ]";
        }
    }

    private class ProgrammableLabyrinth {
        private Integer[][] labyrinth;
        private boolean[][] crossRoad;
        private Position start;
        private Position finish;
        private List<String> programm;
        private int rows;
        private int cols;
        private Set<Position> requiredShortest;
        private Set<Position> requiredCheapest;
        private Set<Position> requiredCheapestThan;

        private Integer getValue(Position pos) throws YouShallNotPassException {
            Integer value = labyrinth[pos.col][pos.row];
            if (value == null) throw new YouShallNotPassException();
            return value;
        }

        {
            programm = new ArrayList<>();
        }

        public void createLabyrinth() {
            rows = programm.size();
            cols = programm.get(0).length();
            Collections.reverse(programm);
            labyrinth = new Integer[cols][rows];
            crossRoad = new boolean[cols][rows];
            requiredCheapest = new HashSet<>();
            requiredCheapestThan = new HashSet<>();
            requiredShortest = new HashSet<>();

            for (int row = 0; row < rows; row++)
                for (int col = 0; col < cols; col++) {
                    switch (programm.get(row).charAt(col)) {
                        case '#': {
                            continue;
                        }
                        case 'S': {
                            start = new Position(row, col);
                            labyrinth[col][row] = 0;
                            continue;
                        }
                        case 'F': {
                            finish = new Position(row, col);
                            labyrinth[col][row] = 0;
                            continue;
                        }
                        case '+': {
                            labyrinth[col][row] = 1;
                            crossRoad[col][row] = true;
                            continue;
                        }
                        case 'c': {
                            labyrinth[col][row] = 1;
                            requiredCheapest.add(new Position(row, col));
                            continue;
                        }
                        case 's': {
                            labyrinth[col][row] = 1;
                            requiredShortest.add(new Position(row, col));
                            continue;
                        }
                        case 't': {
                            labyrinth[col][row] = 1;
                            requiredCheapestThan.add(new Position(row, col));
                            continue;
                        }
                        default: {
                            labyrinth[col][row]
                                    = Integer.valueOf(programm.get(row).substring(col, col + 1));
                        }
                    }
                }
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            programm.forEach(s -> sb.append(s + "\n"));
            return sb.toString();
        }
    }

    public void addRow(String row) {
        labyrinth.programm.add(row);
    }

    public void createLabyrinth() {
        labyrinth.createLabyrinth();
        currentPosition = labyrinth.start;
    }

    @Override
    public LabyrinthInterface clone() {
        return new PMO_Labyrinth(currentPosition, labyrinth);
    }

    @Override
    public int getCost(Direction direction) throws YouShallNotPassException {

        Position next = currentPosition.next(direction);

        return labyrinth.getValue(next);
    }

    @Override
    public boolean move(Direction direction) {
        Position next = currentPosition.next(direction);

        try {
            labyrinth.getValue(next);
        } catch (YouShallNotPassException e) {
            return false;
        }
        currentPosition = next;
        return true;
    }

    public void pathTest(List<Direction> path) {
        int test[][] = new int[labyrinth.cols][labyrinth.rows];
        currentPosition = labyrinth.start;

        System.out.println("TRASA> " + path);

        path.forEach(d -> {
            move(d);
            if (required.contains(currentPosition)) {
                PMO_SystemOutRedirect.println("OK: trasa przechodzi przez oczekiwane pole " + currentPosition);
                required.remove(currentPosition);
            }
            int visits = ++test[currentPosition.col][currentPosition.row];
            if (visits > 1) {
                if (!labyrinth.crossRoad[currentPosition.col][currentPosition.row]) {
                    org.junit.Assert.fail("Blad - wykryto powtorne odwiedzenie lokalizacji, ktora nie jest skrzyzowaniem");
                }
            }
        });

        assertEquals("Pozycja koncowa nie jest pozycja Finish", labyrinth.finish, currentPosition);
    }

    private void testPaths(Set<List<Direction>> paths) {
        testPaths(paths, true);
    }

    private void testPaths(Set<List<Direction>> paths, boolean test) {
        paths.forEach(p -> pathTest(p));

        if (test)
            assertEquals("Nie wszystkie wymagane trasy sa w rozwiazaniu", 0, required.size());
    }

    public void cheapestPathsTest(Set<List<Direction>> paths) {
        required.clear();
        required.addAll(labyrinth.requiredCheapest);

        testPaths(paths);
    }

    public void cheapestThenPathsTest(Map<Integer, Set<List<Direction>>> pathsMap, int... costs) {

        System.out.println( "MAPA: " + pathsMap );

        required.clear();
        required.addAll(labyrinth.requiredCheapestThan);
        required.addAll(labyrinth.requiredCheapest);

        for (int cost : costs) {
            assertTrue("Oczekiwano tras dla kosztu " + cost, pathsMap.containsKey(cost));
            testPaths(pathsMap.get(cost), false);
        }
        assertTrue("Nie wszystkie wymagane trasy sa w rozwiazaniu", required.size() == 0);
    }

    public void shortestPathsTest(Set<List<Direction>> paths) {
        required.clear();
        required.addAll(labyrinth.requiredShortest);

        testPaths(paths);
    }

}
