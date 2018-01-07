import javafx.util.Pair;

import java.util.*;

class PathFinder2 implements PathFinderInterface {
    private LabyrinthInterface labyrinth;
    private PathFinder.TraverserTree traverserTree;
    private Set<PathFinder.Traverser> allPaths = new HashSet<>();

    public static void main(String[] args) {
        // write your code here
    }

    /**
     * Metoda przekazuje labirynt, ktĂłry naleĹźy zbadac.
     *
     * @param labyrinth labirynt do rozpoznania.
     */
    @Override
    public void exploreLabyrinth(LabyrinthInterface labyrinth) {
        this.labyrinth = labyrinth;
        traverserTree = new PathFinder.TraverserTree(labyrinth);
        PathFinder.TraverserTree.TreeNode root = traverserTree.getRoot();
        nextStep(traverserTree, root);
        traversAndAdd(root);

    }

    private void traversAndAdd(PathFinder.TraverserTree.TreeNode parentNode) {
        for (PathFinder.TraverserTree.TreeNode children : parentNode.getChildren()) {
            nextStep(traverserTree, children);
            traversAndAdd(children);
        }
    }

    private void nextStep(PathFinder.TraverserTree traverserTree, PathFinder.TraverserTree.TreeNode children) {
        LabyrinthInterface currentLabyrinth = children.getData().getLabyrinthInterface();
        List<LabyrinthInterface.Direction> directions = PathFinder.tryMove(currentLabyrinth);
        PathFinder.Traverser traverser = children.getData().getTraverser();
        for (LabyrinthInterface.Direction direction : directions) {
            if (isOpositDirection(children, direction)) {
                continue;
            }
            PathFinder.Traverser traverserCopy = new PathFinder.Traverser(traverser);
            try {
                int cost = currentLabyrinth.getCost(direction);
                if (traverserCopy.addNewPosition(direction, cost)) {
                    LabyrinthInterface labCopy = currentLabyrinth.clone();
                    if (cost == 0) {
                        allPaths.add(traverserCopy);
                    }
                    labCopy.move(direction);
                    traverserTree.addChild(children, new PathFinder.Data(cost, direction, labCopy, traverserCopy));
                }
            } catch (LabyrinthInterface.YouShallNotPassException e) {
                //ignore this exception should never happen
            }
        }
    }

    private boolean isOpositDirection(TraverserTree.TreeNode node, LabyrinthInterface.Direction direction) {
        if (node == null) {
            return false;
        }
        LabyrinthInterface.Direction parentDirection = node.getData().getDirection();
        return parentDirection == LabyrinthInterface.Direction.NORTH && direction == LabyrinthInterface.Direction.SOUTH ||
                parentDirection == LabyrinthInterface.Direction.SOUTH && direction == LabyrinthInterface.Direction.NORTH ||
                parentDirection == LabyrinthInterface.Direction.EAST && direction == LabyrinthInterface.Direction.WEST ||
                parentDirection == LabyrinthInterface.Direction.WEST && direction == LabyrinthInterface.Direction.EAST;
    }

    /**
     * Metoda zwraca zbior tras, ktore sa najkrotsze (wymagaja najmniejszej liczby
     * krokow do przejscia).
     *
     * @return zbior najkrotszych tras.
     */
    @Override
    public Set<List<LabyrinthInterface.Direction>> getShortestPaths() {
        Set<List<LabyrinthInterface.Direction>> result = new HashSet<>();
        Iterator<PathFinder.Traverser> iterator = allPaths.iterator();
        Integer min = null;

        while (iterator.hasNext()) {
            List<LabyrinthInterface.Direction> directions = iterator.next().getDirections();
            int pathLength = directions.size();
            if (min == null) {
                result.add(directions);
                min = directions.size();
            } else {
                if (min == pathLength) {
                    result.add(directions);
                } else if (min > pathLength) {
                    result.clear();
                    min = pathLength;
                    result.add(directions);
                }
            }
        }

        return result;
    }

    static List<LabyrinthInterface.Direction> tryMove(LabyrinthInterface labyrinth) {
        List<LabyrinthInterface.Direction> avaiable = new ArrayList<>(4);
        for (LabyrinthInterface.Direction direction : LabyrinthInterface.Direction.values()) {
            try {
                labyrinth.getCost(direction);
                avaiable.add(direction);
            } catch (LabyrinthInterface.YouShallNotPassException e) {
                //ignore
            }
        }
        return avaiable;
    }

    /**
     * Metoda zwraca zbior najtanszych tras, czyli trasy, ktore prowadza przez
     * zajtansze pozycje w labiryncie.
     *
     * @return zbior najtanszych tras.
     */
    @Override
    public Set<List<LabyrinthInterface.Direction>> getCheapestPaths() {
        Set<List<LabyrinthInterface.Direction>> result = new HashSet<>();
        Iterator<PathFinder.Traverser> iterator = allPaths.iterator();
        Integer min = null;

        while (iterator.hasNext()) {
            PathFinder.Traverser traverser = iterator.next();
            int totalCost = traverser.getTotalCost();
            List<LabyrinthInterface.Direction> directions = traverser.getDirections();
            if (min == null) {
                result.add(directions);
                min = traverser.getTotalCost();
            } else {
                if (min == totalCost) {
                    result.add(directions);
                } else if (min > totalCost) {
                    result.clear();
                    min = totalCost;
                    result.add(directions);
                }
            }
        }

        return result;
    }

    /**
     * Metoda zwraca mape zawierajaca zbiory tras o koszcie mniejszym niĹź podane.
     * Kluczem w mapie jest koszt trasy.
     *
     * @param maxCost maksymalny koszt, trasy umieszczone w wyniku maja byc od niego
     *                tansze
     * @return trasy tansze od maxCost
     */
    @Override
    public Map<Integer, Set<List<LabyrinthInterface.Direction>>> getPathsLessExpensiveThan(int maxCost) {
        Map<Integer, Set<List<LabyrinthInterface.Direction>>> result = new LinkedHashMap<>();
        List<List<LabyrinthInterface.Direction>> lists = new ArrayList<>(result.size());
        for (Traverser t : allPaths) {
            if (!lists.isEmpty()) {
                List<LabyrinthInterface.Direction> directions = lists.get(lists.size() - 1);
                List<LabyrinthInterface.Direction> toAdd = t.getDirections();
                int size = Math.min(directions.size(), toAdd.size());
                int repeats = 0;
                for (int i = 0; i < size; ++i) {
                    if (directions.get(i) == toAdd.get(i)) {
                        ++repeats;
                    }
                }
                if (repeats >= size * 2 / 3) {
                    continue;
                }
            }
            if (t.getTotalCost() < maxCost) {
                result.put(t.getTotalCost(), new HashSet<List<LabyrinthInterface.Direction>>() {{
                    lists.add(t.getDirections());
                    add(t.getDirections());
                }});
            }
        }
        return result;
    }

    static class Traverser {
        Set<Pair<Integer, Integer>> visited;
        Map<Pair<Integer, Integer>, Integer> visited2;
        List<LabyrinthInterface.Direction> directions;
        int totalCost;
        Pair<Integer, Integer> currentPosition;
        boolean hasDuplication = false;

        Traverser() {
            Pair<Integer, Integer> startPosition = new Pair<>(0, 0);
            visited2 = new HashMap<>();
            visited2.put(startPosition, 1);
            visited = new HashSet<>();
            visited.add(startPosition);
            directions = new LinkedList<>();
            totalCost = 0;
            currentPosition = startPosition;
        }

        Traverser(Traverser traverser) {
            this.visited = new HashSet<>(traverser.visited);
            this.directions = new LinkedList<>(traverser.directions);
            this.visited2 = new HashMap<>(traverser.visited2);
            this.totalCost = traverser.totalCost;
            this.currentPosition = new Pair<>(traverser.currentPosition.getKey(), traverser.currentPosition.getValue());
        }

        boolean addNewPosition(LabyrinthInterface.Direction direction, int cost) {
            Pair<Integer, Integer> position = toNewPosition(direction);
            Integer timesPosition = visited2.get(position);

            if (timesPosition == null) {
                visited2.put(position, 1);
            } else if (timesPosition == 1) {
                boolean hasLoop = visited2.values().contains(2);
                if (hasLoop) {
                    return false;
                }
                visited2.put(position, 2);
                hasDuplication = true;
            } else {
                return false;
            }

            directions.add(direction);
            totalCost += cost;
            currentPosition = position;
            return true;
        }

        public void setHasDuplication(boolean hasDuplication) {
            this.hasDuplication = hasDuplication;
        }

        private Pair<Integer, Integer> toNewPosition(LabyrinthInterface.Direction direction) {
            Pair<Integer, Integer> updatedPosition = null;
            switch (direction) {
                case NORTH:
                    updatedPosition = new Pair<>(currentPosition.getKey() - 1, currentPosition.getValue());
                    break;
                case SOUTH:
                    updatedPosition = new Pair<>(currentPosition.getKey() + 1, currentPosition.getValue());
                    break;
                case WEST:
                    updatedPosition = new Pair<>(currentPosition.getKey(), currentPosition.getValue() - 1);
                    break;
                case EAST:
                    updatedPosition = new Pair<>(currentPosition.getKey(), currentPosition.getValue() + 1);
                    break;
            }
            return updatedPosition;
        }

        public Set<Pair<Integer, Integer>> getVisited() {
            return visited;
        }

        public List<LabyrinthInterface.Direction> getDirections() {
            return directions;
        }

        public int getTotalCost() {
            return totalCost;
        }

        public Pair<Integer, Integer> getCurrentPosition() {
            return currentPosition;
        }
    }


    static class MyClass implements LabyrinthInterface {
        private int[][] lab;
        private Pair<Integer, Integer> currentPosition;

        MyClass(int[][] lab, Pair<Integer, Integer> start) {
            this.lab = lab;
            this.currentPosition = start;
        }

        MyClass(MyClass myClass) {
            this.lab = myClass.lab;
            this.currentPosition = new Pair<>(myClass.currentPosition.getKey(), myClass.currentPosition.getValue());
        }

        /**
         * Metoda zwraca koszt zajecia pozycji znajdujacej sie w direction od aktualnej.
         *
         * @param direction kierunek, dla ktorego wykonywany jest test kosztu.
         * @return dodatni koszt zajmowania nastepnej pozycji lub 0 jesli nastepna
         * pozycja to start lub meta.
         * @throws YouShallNotPassException zakaz ruchu w danym kierunku (sciana).
         */
        @Override
        public int getCost(Direction direction) throws YouShallNotPassException {
            int result = -1;
            switch (direction) {
                case NORTH:
                    result = lab[currentPosition.getKey() - 1][currentPosition.getValue()];
                    break;
                case SOUTH:
                    result = lab[currentPosition.getKey() + 1][currentPosition.getValue()];
                    break;
                case WEST:
                    result = lab[currentPosition.getKey()][currentPosition.getValue() - 1];
                    break;
                case EAST:
                    result = lab[currentPosition.getKey()][currentPosition.getValue() + 1];
                    break;
            }

            if (result == -1) {
                throw new YouShallNotPassException();
            } else {
                return result;
            }
        }

        /**
         * Metoda zleca wykonanie pojedynczego kroku w zadanym kierunku.
         *
         * @param direction kierunek ruchu
         * @return true - udalo sie zmienic aktualna pozycje, false - pozycja nie
         * zostala zmieniona, bo pozycja docelowa jest sciana.
         */
        @Override
        public boolean move(Direction direction) {
            try {
                getCost(direction);
                switch (direction) {
                    case NORTH:
                        currentPosition = new Pair<>(currentPosition.getKey() - 1, currentPosition.getValue());
                        break;
                    case SOUTH:
                        currentPosition = new Pair<>(currentPosition.getKey() + 1, currentPosition.getValue());
                        break;
                    case WEST:
                        currentPosition = new Pair<>(currentPosition.getKey(), currentPosition.getValue() - 1);
                        break;
                    case EAST:
                        currentPosition = new Pair<>(currentPosition.getKey(), currentPosition.getValue() + 1);
                        break;
                }
                return true;
            } catch (YouShallNotPassException e) {
                return false;
            }
        }

        @Override
        public MyClass clone() {
            return new MyClass(this);
        }
    }


    static class Data {
        private final int cost;
        private final LabyrinthInterface.Direction direction;
        private final LabyrinthInterface labyrinthInterface;
        private final Traverser traverser;

        Data(int cost, LabyrinthInterface.Direction direction, LabyrinthInterface labyrinthInterface, Traverser traverser) {
            this.cost = cost;
            this.direction = direction;
            this.labyrinthInterface = labyrinthInterface;
            this.traverser = traverser;
        }

        public int getCost() {
            return cost;
        }

        LabyrinthInterface.Direction getDirection() {
            return direction;
        }

        LabyrinthInterface getLabyrinthInterface() {
            return labyrinthInterface;
        }

        Traverser getTraverser() {
            return traverser;
        }
    }

    static class TraverserTree {
        private TreeNode root;

        TreeNode getRoot() {
            return root;
        }

        TraverserTree(LabyrinthInterface lab) {
            PathFinder.Traverser traverser = new PathFinder.Traverser();
            PathFinder.Data rootData = new PathFinder.Data(0, null, lab, traverser);
            root = new TreeNode();
            root.data = rootData;
            root.children = new ArrayList<>();
        }

        static class TreeNode {
            private Data data;
            private TreeNode parent;
            private List<TreeNode> children;

            Data getData() {
                return data;
            }

            public TreeNode getParent() {
                return parent;
            }

            List<TreeNode> getChildren() {
                return children;
            }
        }

        void addChild(TreeNode currentNode, Data data) {
            TreeNode childNode = new TreeNode();
            childNode.parent = currentNode;
            childNode.data = data;
            childNode.children = new LinkedList<>();
            currentNode.children.add(childNode);
        }
    }
}
