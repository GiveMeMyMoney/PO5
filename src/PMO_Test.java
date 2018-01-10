import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class PMO_Test {

	//////////////////////////////////////////////////////////////////////////
	private static final Map<String, Double> tariff = new HashMap<>();

	static {
		tariff.put("simpleLabyrinth", 2.0);
		/*tariff.put("simple2Labyrinth", 2.0);
		tariff.put("simple4Labyrinth", 2.0);
		tariff.put("nonsymmetricalLabyrinth", 2.0);
		tariff.put("nonsymmetrical2Labyrinth", 2.0);
		tariff.put("nonsymmetrical3Labyrinth", 1.0);
		tariff.put("Q689A697",0.2);
		tariff.put("loopTest",0.2);*/
	}

	public static double getTariff(String testName) {
		return tariff.get(testName);
	}
	//////////////////////////////////////////////////////////////////////////

	private PMO_Labyrinth labyrinth;
	private PathFinderInterface pathFinder;
	private Set<List<LabyrinthInterface.Direction>> shortestsPaths;
	private Set<List<LabyrinthInterface.Direction>> cheapestPaths;
	private Map<Integer, Set<List<LabyrinthInterface.Direction>>> pathsLessExpensiveThan;

	@Before
	public void preparation() {
		pathFinder = (PathFinderInterface) PMO_GeneralPurposeFabric.fabric("PathFinder", "PathFinderInterface");
		labyrinth = new PMO_Labyrinth();
	}

	private void setLabyrinth() {
		PMO_TestHelper.tryExecute(() -> {
			pathFinder.exploreLabyrinth(labyrinth);
		}, "exploreLabyrinth");
	}

	private void getShortestsPaths() {
		shortestsPaths = PMO_TestHelper.tryExecute(() -> {
			return pathFinder.getShortestPaths();
		}, "getShortestsPaths");
		assertNotNull("Zbior najkrotszych tras nie powinien byc null", shortestsPaths);
	}

	private void getCheapestPaths() {
		cheapestPaths = PMO_TestHelper.tryExecute(() -> {
			return pathFinder.getCheapestPaths();
		}, "getCheapestPaths");
		assertNotNull("Zbior najtanszych tras nie powinien byc null", cheapestPaths);
	}

	private void getCheapestThanPaths(int limit) {
		pathsLessExpensiveThan = PMO_TestHelper.tryExecute(() -> {
			return pathFinder.getPathsLessExpensiveThan(limit);
		}, "pathsLessExpensiveThan");
		assertNotNull("Zbior tras tanszych od " + limit + " nie powinien byc null", cheapestPaths);
	}

	@Test(timeout = 500)
	public void simpleLabyrinth() {
		labyrinth.addRow("################");
		labyrinth.addRow("#1111###2cs11###");
		labyrinth.addRow("#S##2###2###2###");
		labyrinth.addRow("####2###2###22F#");
		labyrinth.addRow("####211cs#######");
		labyrinth.addRow("################");
		labyrinth.createLabyrinth();
		setLabyrinth();

		getCheapestPaths();
		getShortestsPaths();

		assertEquals("Jest tylko jedna najkrotsza trasa ", 1, shortestsPaths.size());
		assertEquals("Jest tylko jedna najtansza trasa ", 1, cheapestPaths.size());

		labyrinth.cheapestPathsTest(cheapestPaths);
		labyrinth.shortestPathsTest(shortestsPaths);
	}

	@Test(timeout = 500)
	public void simple2Labyrinth() {
		labyrinth.addRow("################");
		labyrinth.addRow("##2321##1cs11###");
		labyrinth.addRow("##1##2421###1###");
		labyrinth.addRow("#S+#########+1F#");
		labyrinth.addRow("##1##2421###1###");
		labyrinth.addRow("##2321##1cs11###");
		labyrinth.addRow("################");
		labyrinth.createLabyrinth();
		setLabyrinth();

		getCheapestPaths();
		getShortestsPaths();

		assertEquals("Bledna liczba najkrotszych tras", 2, shortestsPaths.size());
		assertEquals("Bladna liczba najtanszych tras", 2, cheapestPaths.size());

		labyrinth.cheapestPathsTest(cheapestPaths);
		labyrinth.shortestPathsTest(shortestsPaths);
	}

	@Test(timeout = 500)
	public void simple4Labyrinth() {
		labyrinth.addRow("################");
		labyrinth.addRow("#####c42s#######");
		labyrinth.addRow("##232+##+cs11###");
		labyrinth.addRow("##1##c42s###1###");
		labyrinth.addRow("#S+#########+1F#");
		labyrinth.addRow("##1##c42s###1###");
		labyrinth.addRow("##232+##+cs11###");
		labyrinth.addRow("#####c42s#######");
		labyrinth.addRow("################");
		labyrinth.createLabyrinth();
		setLabyrinth();

		getCheapestPaths();
		getShortestsPaths();

		assertEquals("Bledna liczba najkrotszych tras", 4, shortestsPaths.size());
		assertEquals("Bladna liczba najtanszych tras", 4, cheapestPaths.size());

		labyrinth.cheapestPathsTest(cheapestPaths);
		labyrinth.shortestPathsTest(shortestsPaths);
	}

	@Test(timeout = 500)
	public void nonsymmetricalLabyrinth() {
		labyrinth.addRow("###################");
		labyrinth.addRow("###################");
		labyrinth.addRow("##ccccccccccccccc##");
		labyrinth.addRow("##1#############1##");
		labyrinth.addRow("##1#############1##");
		labyrinth.addRow("##1#############1##");
		labyrinth.addRow("#S+#############+F#");
		labyrinth.addRow("##999999ss9999999##");
		labyrinth.addRow("###################");
		labyrinth.createLabyrinth();
		setLabyrinth();

		getCheapestPaths();
		getShortestsPaths();

		assertEquals("Bledna liczba najkrotszych tras", 1, shortestsPaths.size());
		assertEquals("Bladna liczba najtanszych tras", 1, cheapestPaths.size());

		labyrinth.cheapestPathsTest(cheapestPaths);
		labyrinth.shortestPathsTest(shortestsPaths);
	}

	@Test(timeout = 500)
	public void nonsymmetrical2Labyrinth() {
		labyrinth.addRow("###################");
		labyrinth.addRow("######1####1#######");
		labyrinth.addRow("##ccccccccccccccc##");
		labyrinth.addRow("##1#####1####1##1##");
		labyrinth.addRow("##1##1#######1##1##");
		labyrinth.addRow("##1##1111111####1##");
		labyrinth.addRow("#S+########1####+F#");
		labyrinth.addRow("##999999ss9999999##");
		labyrinth.addRow("###################");
		labyrinth.createLabyrinth();
		setLabyrinth();

		getCheapestPaths();
		getShortestsPaths();
		getCheapestThanPaths( 27 );

		assertEquals("Bledna liczba najkrotszych tras", 1, shortestsPaths.size());
		assertEquals("Bladna liczba najtanszych tras", 1, cheapestPaths.size());
		assertTrue("Za mało tras tańszych od 27, powinna być co najmniej 1", pathsLessExpensiveThan.size() > 0 );

		labyrinth.cheapestPathsTest(cheapestPaths);
		labyrinth.shortestPathsTest(shortestsPaths);
		labyrinth.cheapestThenPathsTest(pathsLessExpensiveThan, 23 );
	}

	@Test(timeout = 500)
	public void nonsymmetrical3Labyrinth() {
		labyrinth.addRow("###################");
		labyrinth.addRow("######tttttt#######");
		labyrinth.addRow("######1####1#######");
		labyrinth.addRow("##cccc+cccc+ccccc##");
		labyrinth.addRow("##1#####1####1##1##");
		labyrinth.addRow("##1##1#######1##1##");
		labyrinth.addRow("##1##1111111####1##");
		labyrinth.addRow("#S+########1####+F#");
		labyrinth.addRow("##999999ss9999999##");
		labyrinth.addRow("###################");
		labyrinth.createLabyrinth();
		setLabyrinth();

		getCheapestPaths();
		getShortestsPaths();
		getCheapestThanPaths( 30 );

		assertEquals("Bledna liczba najkrotszych tras", 1, shortestsPaths.size());
		assertEquals("Bladna liczba najtanszych tras", 1, cheapestPaths.size());
		assertTrue("Za mało tras tańszych od 30, powinny być co najmniej 2", pathsLessExpensiveThan.size() > 1 );

		labyrinth.cheapestPathsTest(cheapestPaths);
		labyrinth.shortestPathsTest(shortestsPaths);
		labyrinth.cheapestThenPathsTest(pathsLessExpensiveThan, 23, 27 );
	}

	@Test(timeout = 500)
	public void Q689A697() {
		labyrinth.addRow("###################");
		labyrinth.addRow("#############1tt1##");
		labyrinth.addRow("#############1##1##");
		labyrinth.addRow("###1111cccc11+111##");
		labyrinth.addRow("#F1+#########1#####");
		labyrinth.addRow("###9#########1111S#");
		labyrinth.addRow("###99999s99999#####");
		labyrinth.addRow("###################");
		labyrinth.createLabyrinth();
		setLabyrinth();

		getCheapestPaths();
		getShortestsPaths();
		getCheapestThanPaths( 30 );

		assertEquals("Bledna liczba najkrotszych tras", 2, shortestsPaths.size());
		assertEquals("Bladna liczba najtanszych tras", 1, cheapestPaths.size());
		assertTrue("Za mało tras tańszych od 30, powinny być co najmniej 2", pathsLessExpensiveThan.size() > 1 );

		labyrinth.cheapestPathsTest(cheapestPaths);
		labyrinth.shortestPathsTest(shortestsPaths);
		labyrinth.cheapestThenPathsTest(pathsLessExpensiveThan, 18, 28 );
	}

	@Test(timeout = 500)
	public void loopTest() {
		labyrinth.addRow("###################");
		labyrinth.addRow("##111sct11#11t11###");
		labyrinth.addRow("##1######1#1###1###");
		labyrinth.addRow("#S+######11+tsc+1F#");
		labyrinth.addRow("##1########1#######");
		labyrinth.addRow("##111tsc1111#######");
		labyrinth.addRow("###################");
		labyrinth.createLabyrinth();
		setLabyrinth();

		getCheapestPaths();
		getShortestsPaths();
		getCheapestThanPaths( 30 );

		assertEquals("Bledna liczba najkrotszych tras", 2, shortestsPaths.size());
		assertEquals("Bladna liczba najtanszych tras", 2, cheapestPaths.size());
		assertTrue("Za mało tras tańszych od 30, powinny być co najmniej 2", pathsLessExpensiveThan.size() > 1 );

		labyrinth.cheapestPathsTest(cheapestPaths);
		labyrinth.shortestPathsTest(shortestsPaths);
		labyrinth.cheapestThenPathsTest(pathsLessExpensiveThan, 19, 23 );
	}

}