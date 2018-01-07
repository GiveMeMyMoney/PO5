import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Wojtek on 2017-12-13.
 */
public class MyClassTest {
    private PathFinder.MyClass myClass;

    @Before
    public void setUp() {
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
        myClass = new PathFinder.MyClass(lab, start);
    }


    @Test(expected = LabyrinthInterface.YouShallNotPassException.class)
    public void getCostException() throws Exception {
        myClass.getCost(LabyrinthInterface.Direction.WEST);
    }

    @Test
    public void getCost() throws Exception {
        int x1 = myClass.getCost(LabyrinthInterface.Direction.EAST);
        myClass.move(LabyrinthInterface.Direction.EAST);
        int x2 = myClass.getCost(LabyrinthInterface.Direction.EAST);
        myClass.move(LabyrinthInterface.Direction.EAST);
        int x3 = myClass.getCost(LabyrinthInterface.Direction.NORTH);
        myClass.move(LabyrinthInterface.Direction.NORTH);

        Assert.assertEquals(x1, 1);
        Assert.assertEquals(x2, 2);
        Assert.assertEquals(x3, 3);
    }

    @Test
    public void move() throws Exception {
        boolean actual1 = myClass.move(LabyrinthInterface.Direction.NORTH);
        boolean actual2 = myClass.move(LabyrinthInterface.Direction.EAST);

        Assert.assertEquals(actual1, false);
        Assert.assertEquals(actual2, true);
    }

}