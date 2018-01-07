import java.util.List;
import java.util.Map;
import java.util.Set;


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
     * @param direction
     *            kierunek, dla ktorego wykonywany jest test kosztu.
     * @return dodatni koszt zajmowania nastepnej pozycji lub 0 jesli nastepna
     *         pozycja to start lub meta.
     * @throws YouShallNotPassException
     *             zakaz ruchu w danym kierunku (sciana).
     */
    int getCost(Direction direction) throws YouShallNotPassException;

    /**
     * Metoda zleca wykonanie pojedynczego kroku w zadanym kierunku.
     *
     * @param direction
     *            kierunek ruchu
     * @return true - udalo sie zmienic aktualna pozycje, false - pozycja nie
     *         zostala zmieniona, bo pozycja docelowa jest sciana.
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
 * @author oramus
 *
 */
interface PathFinderInterface {
    /**
     * Metoda przekazuje labirynt, ktĂłry naleĹźy zbadac.
     *
     * @param labyrinth
     *            labirynt do rozpoznania.
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
     * @param maxCost
     *            maksymalny koszt, trasy umieszczone w wyniku maja byc od niego
     *            tansze
     * @return trasy tansze od maxCost
     */
    public Map<Integer, Set<List<LabyrinthInterface.Direction>>> getPathsLessExpensiveThan(int maxCost);
}

class PathFinder implements PathFinderInterface {
    LabyrinthInterface labyrinth;

    Set<List<LabyrinthInterface.Direction>> shortestPaths;
    Set<List<LabyrinthInterface.Direction>> cheapestPaths;


    public PathFinder() {
    }

    //region PathFinderInterface

    @Override
    public void exploreLabyrinth(LabyrinthInterface labyrinth) {
        this.labyrinth = labyrinth;
        //TODO change labyrinth into graph = krawedzie - korytarze, Node - punkty przecięcia
        //znajdywanie wszystkich ścieżek + sumowanie kosztow przejscia tego graphu i zapisywanie do struktury
        //ogarniecie max 1x w pętli
    }

    @Override
    public Set<List<LabyrinthInterface.Direction>> getShortestPaths() {
        return null;
    }

    @Override
    public Set<List<LabyrinthInterface.Direction>> getCheapestPaths() {
        return null;
    }

    @Override
    public Map<Integer, Set<List<LabyrinthInterface.Direction>>> getPathsLessExpensiveThan(int maxCost) {
        return null;
    }

    //endregion

}

