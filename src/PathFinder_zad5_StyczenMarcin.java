import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;


interface LabyrinthInterface {
    /**
     * Typ wyliczeniowy reprezentujacy dostepne kierunki przemieszczania sie w
     * labiryncie.
     */
    public enum Direction {
        SOUTH, NORTH, WEST, EAST;
    }

    /**
     * Wyjatek informujacy o braku mozliwosci przejscia w ta strone.
     */
    class YouShallNotPassException extends Exception {
        private static final long serialVersionUID = 4399365499586330374L;

    }

    /**
     * Metoda zwraca koszt zajecia pozycji znajdujacej sie w direction od aktualnej.
     *
     * @param direction kierunek, dla ktorego wykonywany jest test kosztu.
     * @return dodatni koszt zajmowania nastepnej pozycji lub 0 jesli nastepna
     * pozycja to start lub meta.
     * @throws YouShallNotPassException zakaz ruchu w danym kierunku (sciana).
     */
    int getCost(Direction direction) throws YouShallNotPassException;

    /**
     * Metoda zleca wykonanie pojedynczego kroku w zadanym kierunku.
     *
     * @param direction kierunek ruchu
     * @return true - udalo sie zmienic aktualna pozycje, false - pozycja nie
     * zostala zmieniona, bo pozycja docelowa jest sciana.
     */
    boolean move(Direction direction);

    /**
     * Metoda zwraca doglebna kopie obiektu implementujacego LabyrinthInterface
     * w stanie z chwili wykonania clone()
     *
     * @return kopia obiektu
     */
    public LabyrinthInterface clone();
}

/**
 * Interfejs systemu przeszukiwania labiryntu.
 *
 * @author oramus
 */
interface PathFinderInterface {
    /**
     * Metoda przekazuje labirynt, ktĂłry naleĹźy zbadac.
     *
     * @param labyrinth labirynt do rozpoznania.
     */
    public void exploreLabyrinth(LabyrinthInterface labyrinth);

    /**
     * Metoda zwraca zbior tras, ktore sa najkrotsze (wymagaja najmniejszej liczby
     * krokow do przejscia).
     *
     * @return zbior najkrotszych tras.
     */
    public Set<List<LabyrinthInterface.Direction>> getShortestPaths();

    /**
     * Metoda zwraca zbior najtanszych tras, czyli trasy, ktore prowadza przez
     * zajtansze pozycje w labiryncie.
     *
     * @return zbior najtanszych tras.
     */
    public Set<List<LabyrinthInterface.Direction>> getCheapestPaths();

    /**
     * Metoda zwraca mape zawierajaca zbiory tras o koszcie mniejszym niĹź podane.
     * Kluczem w mapie jest koszt trasy.
     *
     * @param maxCost maksymalny koszt, trasy umieszczone w wyniku maja byc od niego
     *                tansze
     * @return trasy tansze od maxCost
     */
    public Map<Integer, Set<List<LabyrinthInterface.Direction>>> getPathsLessExpensiveThan(int maxCost);
}

class Path {
    private int cost;
    private LabyrinthInterface labyrinth;
    private Map<Pair<Integer, Integer>, Integer> xyVisitedMap;
    private List<LabyrinthInterface.Direction> directionList;

    private Pair<Integer, Integer> xyPosition;

    //region GETTER & SETTER
    public void addCost(int cost) {
        this.cost += cost;
    }

    public int getCost() {
        return cost;
    }

    public LabyrinthInterface getLabyrinth() {
        return labyrinth;
    }

    public Map<Pair<Integer, Integer>, Integer> getXyVisitedMap() {
        return xyVisitedMap;
    }

    public List<LabyrinthInterface.Direction> getDirectionList() {
        return directionList;
    }

    public void addDirectionList(LabyrinthInterface.Direction direction) {
        this.directionList.add(direction);
    }

    public int getDirectionListSize() {
        return directionList.size();
    }

    public void updateXyPosition(Pair<Integer, Integer> xyPosition) {
        this.xyPosition = xyPosition;
        //ile razy ta instanjca Path byla juz na takiej pozycji:
        int visited = xyVisitedMap.getOrDefault(xyPosition, 0);
        xyVisitedMap.put(xyPosition, ++visited);
    }

    public Pair<Integer, Integer> getXyPosition() {
        return xyPosition;
    }

    //endregion

    //poczatek sciezki
    public Path(LabyrinthInterface labyrinth) {
        directionList = new ArrayList<>();
        xyVisitedMap = new HashMap<>();
        this.labyrinth = labyrinth;
        Pair<Integer, Integer> startPosition = new Pair<>(0, 0); //odnośnik osi xy (0 : 0) to punkt Startowy
        xyVisitedMap.put(startPosition, 1);
        xyPosition = startPosition;
        cost = 0;
    }

    public Path(Path path) {
        this.cost = path.getCost();
        this.xyVisitedMap = new HashMap<>(path.getXyVisitedMap());
        this.directionList = new ArrayList<>(path.getDirectionList());
        this.xyPosition = new Pair<>(path.getXyPosition().getKey(), path.getXyPosition().getValue());
        LabyrinthInterface labCopy = path.getLabyrinth().clone();
        this.labyrinth = labCopy;
    }

    private LabyrinthInterface.Direction getLastDirection() {
        if (directionList.isEmpty()) {
            return null;
        }
        return directionList.get(directionList.size() - 1);
    }

    //zwraca czy sie cofamy
    private boolean isOppositeDirection(LabyrinthInterface.Direction newDirection) {
        boolean opposite;
        LabyrinthInterface.Direction lastDirection = getLastDirection();
        if (lastDirection == null) {
            opposite = false;
        } else {
            opposite = lastDirection == LabyrinthInterface.Direction.NORTH && newDirection == LabyrinthInterface.Direction.SOUTH ||
                    lastDirection == LabyrinthInterface.Direction.SOUTH && newDirection == LabyrinthInterface.Direction.NORTH ||
                    lastDirection == LabyrinthInterface.Direction.EAST && newDirection == LabyrinthInterface.Direction.WEST ||
                    lastDirection == LabyrinthInterface.Direction.WEST && newDirection == LabyrinthInterface.Direction.EAST;
        }
        return opposite;
    }

    private boolean tryMove(LabyrinthInterface.Direction newDirection) {
        Pair<Integer, Integer> newXYPosition = goToNewPosition(newDirection);
        Integer howManyTimes = xyVisitedMap.getOrDefault(newXYPosition, 0);
        //dla notNode moze byc tylko 1 raz
        if (howManyTimes == 0) {
            return true;
        } else {
            //jezeli NODE
            //TODO ew. optymalizacja .size();
            LabyrinthInterface copyNodeTestLab = getLabyrinth().clone();
            if (!copyNodeTestLab.move(newDirection)) {
                return false;
            }
            List<LabyrinthInterface.Direction> directions = allNotWallDirection(copyNodeTestLab);
            if (directions.size() > 1 && directions.contains(newDirection)) { //TODO sprawdzic czy to contains dobrze zadziala
                return true;
            }
        }
        return false;
    }

    public Pair<Integer, Integer> goToNewPosition(LabyrinthInterface.Direction direction) {
        Pair<Integer, Integer> updatedPosition = null;
        if (direction == LabyrinthInterface.Direction.NORTH) {
            updatedPosition = new Pair<>(xyPosition.getKey() + 1, xyPosition.getValue());
        } else if (direction == LabyrinthInterface.Direction.SOUTH) {
            updatedPosition = new Pair<>(xyPosition.getKey() - 1, xyPosition.getValue());
        } else if (direction == LabyrinthInterface.Direction.WEST) {
            updatedPosition = new Pair<>(xyPosition.getKey(), xyPosition.getValue() - 1);
        } else if (direction == LabyrinthInterface.Direction.EAST) {
            updatedPosition = new Pair<>(xyPosition.getKey(), xyPosition.getValue() + 1);
        }
        return updatedPosition;
    }

    private List<LabyrinthInterface.Direction> allNotWallDirection(LabyrinthInterface labyrinth) {
        List<LabyrinthInterface.Direction> availableDirectionList = new ArrayList<>();
        for (LabyrinthInterface.Direction direction : LabyrinthInterface.Direction.values()) {
            try {
                labyrinth.getCost(direction);
                boolean oppositeDirection = isOppositeDirection(direction);
                if (!oppositeDirection) {
                    availableDirectionList.add(direction);
                }
            } catch (LabyrinthInterface.YouShallNotPassException e) {
                //sciana
            }
        }
        return availableDirectionList;
    }

    //zwraca wszystkie dostepny ruchy z aktualnej pozycji
    public List<LabyrinthInterface.Direction> availableDirection(/*LabyrinthInterface labyrinth*/) {
        List<LabyrinthInterface.Direction> availableDirectionList = new ArrayList<>();
        //LabyrinthInterface.Direction lastDirection = getLastDirection();
        //availableDirectionList = allNotWallDirection(); //TODO ew. optyamlziacja
        for (LabyrinthInterface.Direction direction : LabyrinthInterface.Direction.values()) {
            try {
                this.labyrinth.getCost(direction);
                boolean oppositeDirection = isOppositeDirection(direction); //jezeli jest opposite to nie dodajemy ruchu, chyba ze jest to slepy zauek o size = 1 tzn. petla o size == 1
                if (!oppositeDirection) {
                    //czy nie bylismy juz na pozycji
                    boolean notVisitedNotNode = tryMove(direction);
                    if (notVisitedNotNode) {
                        availableDirectionList.add(direction);
                    }
                }
            } catch (LabyrinthInterface.YouShallNotPassException e) {
                //sciana
            }
        }
        //chyba ze jest to slepy zauek o size = 1 tzn. petla o size == 1, wtedy mozemy sie cofanac do Node
        //TODO na razie rezygnuje
        /*if (availableDirectionList.isEmpty()) {
            List<LabyrinthInterface.Direction> availableDirectionsOneSizeLoop = allNotWallDirection(this.labyrinth);
            if (availableDirectionsOneSizeLoop.size() == 1) {
                availableDirectionList.add(availableDirectionsOneSizeLoop.get(0));
            }
        }*/
        return availableDirectionList;
    }

}

class PathFinder implements PathFinderInterface {
    LabyrinthInterface labyrinth;
    Set<Path> allPathSet;

    public PathFinder() {
        allPathSet = new HashSet<>();
    }

    //region PathFinderInterface

    //Komponując trasę kilkukrotnie można przejść tylko te położenia, które są skrzyżowaniami
    @Override
    public void exploreLabyrinth(LabyrinthInterface labyrinth) {
        this.labyrinth = labyrinth;
        Path path = new Path(labyrinth); // tworze poczatek nowej sciezki (w tym przypadku od START)
        nextMove(path);

        //TODO change labyrinth into graph = krawedzie - korytarze, Node - punkty przecięcia
        //kopiowac Path na skrzyzowaniach - TYLKO jesli w tym momencie ma wiecej niz 1 dozwolony ruch
        //znajdywanie wszystkich ścieżek + sumowanie kosztow przejscia tego graphu i zapisywanie do struktury
        //ogarniecie max 1x w pętli // UWAGA petla moze byc 1-elementowa
    }

    private void nextMove(Path path) {
        List<LabyrinthInterface.Direction> availableDirections = path.availableDirection();
        //KONIEC GRY
        if (availableDirections.isEmpty()) {
            //path = null;
        } //nie NODE
        else if (availableDirections.size() == 1) {
            LabyrinthInterface.Direction direction = availableDirections.get(0);
            move(path, direction);
        } //NODE
        else {
            availableDirections.forEach(direction -> {
                Path pathCopy = new Path(path);
                move(pathCopy, direction);
            });
        }
    }

    private void move(Path path, LabyrinthInterface.Direction direction) {
        try {
            int oneCost = path.getLabyrinth().getCost(direction);
            if (path.getLabyrinth().move(direction)) {
                Pair<Integer, Integer> newXYPosition = path.goToNewPosition(direction);
                path.updateXyPosition(newXYPosition);
                path.addCost(oneCost);
                path.addDirectionList(direction);

                //Finish
                if (oneCost == 0) {
                    allPathSet.add(path);
                } else {
                    nextMove(path);
                }
            }
        } catch (LabyrinthInterface.YouShallNotPassException e) {
            //sciana ktora tutaj nie powinna wystapic
            System.out.println("Sciana: " + e.getMessage());
        }
    }


    @Override
    public Set<List<LabyrinthInterface.Direction>> getShortestPaths() {
        Path shortestPath = allPathSet.stream()
                .min(Comparator.comparingInt(Path::getDirectionListSize))
                .get();

        Set<Path> shortestPaths = allPathSet.stream()
                .filter(path -> path.getDirectionListSize() == shortestPath.getDirectionListSize())
                .collect(Collectors.toSet());

        Set<List<LabyrinthInterface.Direction>> shortestPathsReturn = new HashSet<>();
        shortestPaths.forEach(path -> shortestPathsReturn.add(path.getDirectionList()));

        return shortestPathsReturn;
    }

    @Override
    public Set<List<LabyrinthInterface.Direction>> getCheapestPaths() {
        Path cheapestPath = allPathSet.stream()
                .min(Comparator.comparingInt(Path::getCost))
                .get();

        Set<Path> cheapestPaths = allPathSet.stream()
                .filter(path -> path.getCost() == cheapestPath.getCost())
                .collect(Collectors.toSet());

        Set<List<LabyrinthInterface.Direction>> cheapestPathsReturn = new HashSet<>();
        cheapestPaths.forEach(path -> cheapestPathsReturn.add(path.getDirectionList()));

        return cheapestPathsReturn;

    }

    @Override
    public Map<Integer, Set<List<LabyrinthInterface.Direction>>> getPathsLessExpensiveThan(int maxCost) {
        Set<Path> pathsLessExpensiveThanMaxCost = allPathSet.stream()
                .filter(path -> path.getCost() < maxCost)
                .collect(Collectors.toSet());

        Map<Integer, Set<Path>> costPathMap = new HashMap<>();
        pathsLessExpensiveThanMaxCost.forEach(path -> {
            Set<Path> paths = costPathMap.getOrDefault(path.getCost(), new HashSet<>());
            paths.add(path);
            costPathMap.put(path.getCost(), paths);
        });

        Map<Integer, Set<List<LabyrinthInterface.Direction>>> returnMap = new HashMap<>();
        costPathMap.forEach((cost, pathsSet) -> {
            Set<List<LabyrinthInterface.Direction>> directionsSet = new HashSet<>();
            pathsSet.forEach(path -> directionsSet.add(path.getDirectionList()));
            returnMap.put(cost, directionsSet);
        });

        return returnMap;
    }

    //endregion
}