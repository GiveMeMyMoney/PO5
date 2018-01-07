import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PathFinder2Test {
    private PathFinder pathFinder;

    @Before
    public void setUp() {
        pathFinder = new PathFinder();
        int[][] lab = new int[][]{
                {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, 1, 1, 4, -1, -1, 2, 1, 3, -1, 2, 1, 2, 1, 0, -1},
                {-1, 2, -1, 1, 2, 1, 1, -1, 1, -1, 1, -1, -1, -1, 1, -1},
                {-1, 1, 2, 1, -1, -1, -1, -1, 2, 2, 3, 1, 2, -1, 2, -1},
                {-1, -1, -1, 3, -1, -1, 2, -1, -1, -1, -1, -1, 1, -1, 1, -1},
                {-1, 0, 1, 2, -1, 1, 1, 2, 2, 1, 3, -1, 1, -1, 2, -1},
                {-1, -1, -1, 3, -1, 2, -1, -1, -1, -1, 2, -1, 3, 2, 1, -1},
                {-1, 2, 1, 1, -1, 1, 1, 2, 1, -1, 3, -1, 1, -1, -1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, 2, -1, 2, -1, 1, -1, -1, -1},
                {-1, 3, 1, 2, 1, 1, 2, 1, 1, -1, 3, 2, 1, -1, -1, -1},
                {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
        };

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 16; ++j) {
                System.out.print(lab[i][j] >= 0 ? '*' : ' ');
            }
            System.out.println();
        }
        Pair<Integer, Integer> start = new Pair<>(5, 1);
        PathFinder.MyClass myClass = new PathFinder.MyClass(lab, start);
        pathFinder.exploreLabyrinth(myClass);
    }


    @Test
    public void getShortestPaths() throws Exception {
        Set<List<LabyrinthInterface.Direction>> actual = this.pathFinder.getShortestPaths();

        List<LabyrinthInterface.Direction> directions = new ArrayList<LabyrinthInterface.Direction>() {{
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.NORTH);
            add(LabyrinthInterface.Direction.NORTH);
            add(LabyrinthInterface.Direction.NORTH);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.NORTH);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.SOUTH);
            add(LabyrinthInterface.Direction.SOUTH);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.NORTH);
            add(LabyrinthInterface.Direction.NORTH);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.EAST);
            add(LabyrinthInterface.Direction.EAST);
        }};
        Set<List<LabyrinthInterface.Direction>> expected = new HashSet<>();
        expected.add(directions);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getCheapestPaths() {
        Set<List<LabyrinthInterface.Direction>> paths = new HashSet<>();

        paths = pathFinder.getCheapestPaths();

        System.out.print(true);
    }

    @Test
    public void getPathsLessExpensiveThan() {

    }

}