import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TraverserTreeTest {
    private PathFinder.MyClass lab;
    private PathFinder.TraverserTree traverserTree;
    private Set<PathFinder.Traverser> traversers = new HashSet<>();

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
        this.lab = new PathFinder.MyClass(lab, start);
    }

    @Test
    public void addChild() {
//        traverserTree = new PathFinder.TraverserTree(lab);
//        PathFinder.TraverserTree.TreeNode root = traverserTree.getRoot();
//        nextStep(traverserTree, root);
//        traversAndAdd(root);
        System.out.print(true);
    }



}