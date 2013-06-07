package pl.edu.agh.cs.kraksim.main;

// on 7/15/07 3:41 PM

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Core;
import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.eval.EvalIView;
import pl.edu.agh.cs.kraksim.iface.sim.Route;
import pl.edu.agh.cs.kraksim.iface.sim.TravelEndHandler;
import pl.edu.agh.cs.kraksim.parser.ModelParser;
import pl.edu.agh.cs.kraksim.parser.ParsingException;
import pl.edu.agh.cs.kraksim.parser.TrafficSchemeParser;
import pl.edu.agh.cs.kraksim.routing.NoRouteException;
import pl.edu.agh.cs.kraksim.routing.prediction.TrafficPredictionFactory;
import pl.edu.agh.cs.kraksim.sna.GraphVisualizator;
import pl.edu.agh.cs.kraksim.sna.SnaConfigurator;
import pl.edu.agh.cs.kraksim.traffic.TravellingScheme;

public class Simulation implements Clock, TravelEndHandler, Controllable {
	private static final Logger logger = Logger.getLogger(Simulation.class);

	// run arguments
	private final StartupParameters params = new StartupParameters();
	private final SampleModuleConfiguration modules = new SampleModuleConfiguration();
	private PrintWriter statWriter;
	private PrintWriter summaryStatWriter;
	private PrintWriter linkStatWriter;
	private SimulationVisualizator visualizator;
	private StatsUtil.LinkStat linkStat;
	private StatsUtil.LinkStat linkRidingStat;
	
	//do grafu
	private GraphVisualizator graphVisualizator;
	
	
	private Collection<TravellingScheme> trafficScheme;

	private int turn;
	private int activeDriverCount;
	private PriorityQueue<SimpleDriver> departureQueue;
	private DecisionHelper isDriverRoutingHelper;

	private volatile boolean continousMode = false;
	private boolean stepMode = false;

	private OptionsPanel controler;

	private final PrintWriter console = new PrintWriter(System.out);

	private long startTime;

	private void error(final String text, final Throwable error) {
		logger.error(text + "\n  Details: " + error.getMessage());
		System.exit(1);
	}

	private void error(final String text) {
		logger.error(text);
		System.exit(1);
	}

	public static void main(final String[] args) {
		new Simulation(args).run();
	}

	public Simulation(String[] args) {
		// logger.info( "STARTING SIMULATION with params: " + Arrays.toString(
		// args ) );
		departureQueue = new PriorityQueue<SimpleDriver>();

		params.parseOptions(args, console);
		final EvalModuleProvider evalProvider = getEvaluationProvider();
		final Core core = createCore(params.getModelFile(), params
				.getTrafficSchemeFile());
		setUpStatictics();
		visualizator = modules.setUpModules(core, evalProvider, this, params);
		console.println("");

		isDriverRoutingHelper = new DecisionHelper(params.getDriverRoutingRg(),
				params.getDriverRoutingTh());

		if (params.isCommandLineMode()) {
			doRun();
		}
	}

	private void setUpStatictics() {
		if (params.getStatFileName() != null) {
			try {
				statWriter = new PrintWriter(new BufferedOutputStream(
						new FileOutputStream(params.getStatFileName())));
				summaryStatWriter = new PrintWriter(
						new BufferedOutputStream(new FileOutputStream(params
								.getStatFileName()
								+ ".sum")));
        linkStatWriter = new PrintWriter(
            new BufferedOutputStream(new FileOutputStream(params
                .getStatFileName()
                + ".link")));
        
			} catch (FileNotFoundException e) {
				error("Error: statistics file cannot be created -- "
						+ params.getStatFileName());
			}
		} else {
			statWriter = new PrintWriter(System.out);
			linkStatWriter = new PrintWriter(System.out);
		}
		linkStat = new StatsUtil.LinkStat();
		linkRidingStat = new StatsUtil.LinkStat();
	}

	private EvalModuleProvider getEvaluationProvider() {
		EvalModuleProvider evalProvider = null;
		try {
			evalProvider = configureAlgorithm(params.getAlgorithmName());
			console.print(", alg=" + params.getAlgorithmName());
		} catch (AlgorithmConfigurationException e) {
			error("Error: ", e);
		}
		return evalProvider;
	}

	private Core createCore(final String modelFile,
			final String trafficSchemeFile) {
		Core core = null;
		String file = null;
		try {
			file = modelFile;
			core = ModelParser.parse(file);
			console.print(", model=" + file);

			file = trafficSchemeFile;
			trafficScheme = TrafficSchemeParser.parse(file, core.getCity());
			console.print(", scheme=" + file);
		} catch (FileNotFoundException e) {
			error("Error: cannot open file: " + file, e);
		} catch (IOException e) {
			error("Error: An I/O error occured while parsing file: " + file, e);
		} catch (ParsingException e) {
			e.printStackTrace();
			error("Error: Data error while parsing file: " + file, e);
		}

		return core;
	}

	private static EvalModuleProvider configureAlgorithm(final String algConf)
			throws AlgorithmConfigurationException {
		// System.out.println( algConf );
		EvalModuleProvider[] providers = StartupParameters.getEvalProviders();

		int colonIndex = algConf.indexOf(':');
		String algCode = null;
		if (colonIndex == -1) {
			algCode = algConf;
		} else {
			algCode = algConf.substring(0, colonIndex);
		}

		EvalModuleProvider modProvider = null;
		for (int i = 0; i < providers.length; i++) {
			if (providers[i].getAlgorithmCode().equals(algCode)) {
				modProvider = providers[i];
				break;
			}
		}

		if (modProvider == null) {
			throw new AlgorithmConfigurationException("algorithm " + algCode
					+ " not found");
		}

		if (colonIndex != -1) {
			String algParams = algConf.substring(colonIndex + 1);
			String[] params = algParams.split(",");
			for (int i = 0; i < params.length; i++) {
				String parameter = params[i];
				int y = parameter.indexOf('=');
				if (y == -1) {
					throw new AlgorithmConfigurationException(
							"algorithm configuration syntax error");
				}
				modProvider.setParam(parameter.substring(0, y), parameter
						.substring(y + 1));
			}
		}

		return modProvider;
	}

	public void run() {
		modules.getSimView().ext(modules.getCity()).setCommonTravelEndHandler(
				this);

		for (int i = 0; i < params.getLearnPhaseCount(); i++) {
			visualizator.startLearningPhase(i);
			runPhase();
			visualizator.endPhase();
		}
		startTime = System.currentTimeMillis();
		visualizator.startTestingPhase();
		// ===================================================================================
		// --- INITIALIZE TEST RUN
		// ===================================================================================
		turn = 0;
		modules.getStatView().ext(modules.getCity()).clear();

		generateDrivers();
		modules.getDecisionView().ext(modules.getCity()).initialize();
		// ===================================================================================
		// START TEST RUN
		// ===================================================================================
		StatsUtil.statHeader(modules.getCity(), statWriter);
		boolean isRunning = true;
		while (isRunning) {

			if (continousMode) {
				stepMode = true;
			}

			if (stepMode) {

				// if ( turn > 43200 ) {
				// isRunning = false;
				// }
				// else
				if (activeDriverCount > 0) {
					step();
				} else {
					isRunning = false;
				}
				stepMode = false;
			} else {
				try {
					// that way we are not wasting CPU time, when in PAUSE
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// I don't know when and how can this thread be interrupted,
					// so this Exception is IGNORED,
					// but I think this should be fixed for application safety
					// and stability
					logger.error(e);
				}
			}
		}
		// ===================================================================================
		// FINILIZE TEST RUN
		// ===================================================================================
		long elapsed = System.currentTimeMillis() - startTime;
		visualizator.endPhase();
		visualizator.end(elapsed);

		if (summaryStatWriter != null) {
			StatsUtil.dumpStats(modules.getCity(), modules.getStatView(), turn,
					summaryStatWriter);
		}
		if (linkStatWriter != null) {
		    StatsUtil.dumpLinkStats(modules.getCity(),
		            linkStatWriter, linkStat, linkRidingStat);
		}
		cleanUp(summaryStatWriter);
		cleanUp(statWriter);
		cleanUp(linkStatWriter);
		cleanUp(console);

		if (controler != null) {
			controler.end();
		}
	}

	private void cleanUp(final Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				logger.warn("Exception while closing ", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.cs.kraksim.main.Controllable#doStep()
	 */
	public synchronized void doStep() {
		TrafficPredictionFactory.setCityForPredictionSetup(modules.getCity());
		continousMode = false;
		stepMode = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.cs.kraksim.main.Controllable#doRun()
	 */
	public final synchronized void doRun() {
		TrafficPredictionFactory.setCityForPredictionSetup(modules.getCity());
		continousMode = true;
		stepMode = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.cs.kraksim.main.Controllable#doPause()
	 */
	public synchronized void doPause() {
		continousMode = false;
		stepMode = false;
	}

	/*
	 * ONE simulation step, one turn.
	 */
	private void step() {
		try {
			doDepartures();
		} catch (NoRouteException e) {
			error("Error: There is no route for a travelling scheme", e);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("======== Simulation Module - TURN: " + turn
					+ ". ========");
		}
		modules.getSimView().ext(modules.getCity()).simulateTurn();

		if (logger.isTraceEnabled()) {
			logger
					.trace("======== TURN ENDED - Notifying Evaluation Module.========");
		}
		EvalIView eV = modules.getEvalView();
		if (eV != null) {
			eV.ext(modules.getCity()).turnEnded();
		}

		if (logger.isTraceEnabled()) {
			logger
					.trace("======== TURN ENDED - Notifying Decision Module.========");
		}
		modules.getDecisionView().ext(modules.getCity()).turnEnded();

		modules.getWekaPrediction().turnEnded();
		turn++;
		
		//do grafu
		if(turn % SnaConfigurator.getSnaRefreshInterval() == 0){
			refreshGraph();
		}


		visualizator.update(turn);
		StatsUtil.dumpCarStats(modules.getCity(), modules.getStatView(), turn,
				statWriter);
		
		
		StatsUtil.collectLinkStats(modules.getCity(), modules.getCarInfoView(), modules.getBlockView(), modules.getStatView(), turn, linkStat, linkRidingStat);
	}

	private void runPhase() {
		turn = 0;
		modules.getStatView().ext(modules.getCity()).clear();

		generateDrivers();
		modules.getDecisionView().ext(modules.getCity()).initialize();

		while (activeDriverCount > 0) {
			step();
		}

	}

	private void generateDrivers() {
		activeDriverCount = 0;

		for (TravellingScheme travelScheme : trafficScheme) {
			for (int i = 0; i < travelScheme.getCount(); i++) {
				boolean isDriverReRoutingDecision = isDriverRoutingHelper
						.decide();
				if (isDriverReRoutingDecision) {
					SimpleDriver driver = new SimpleDriver(activeDriverCount++,
							travelScheme, modules.getDynamicRouter(),
							new DecisionHelper(params.getDecisionRg(), params
									.getRouteDecisionTh()));
					driver.setDepartureTurn(params.getGenRg());
					departureQueue.add(driver);
				} else {
					SimpleDriver driver = new SimpleDriver(activeDriverCount++,
							travelScheme, null, null);
					driver.setDepartureTurn(params.getGenRg());
					departureQueue.add(driver);
				}

			}
		}
	}

	private void doDepartures() throws NoRouteException {

		while (true) {
			SimpleDriver simpleDriver = departureQueue.peek();
			if (simpleDriver == null || simpleDriver.getDepartureTurn() > turn) {
				break;
			}

			departureQueue.poll();
			Gateway ggg = simpleDriver.srcGateway();
			Link l234 = ggg.getOutboundLink();
			Gateway g234 = simpleDriver.destGateway();
			Route route = modules.getRouter().getRoute(
					l234,
					g234);
//			Route route = modules.getRouter().getRoute(
//					simpleDriver.srcGateway().getOutboundLink(),
//					simpleDriver.destGateway());

			modules.getSimView().ext(modules.getCity()).insertTravel(
					simpleDriver, route, params.isRerouting());
		}
	}

	public void handleTravelEnd(final Object driver) {
		SimpleDriver simpleDriver = (SimpleDriver) driver;
		if (simpleDriver.nextTravel()) {
			simpleDriver.setDepartureTurn(params.getGenRg());
			departureQueue.add(simpleDriver);
		} else {
			activeDriverCount--;
		}

	}

	public int getTurn() {
		return turn;
	}

	public SimulationVisualizator getVisualizator() {
		return visualizator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.cs.kraksim.main.Controllable#setControler(pl.edu.agh.cs.kraksim.main.OptionsPanel)
	 */
	public void setControler(final OptionsPanel panel) {
		controler = panel;
	}

	@Override
	public void setGraphVisualizator(GraphVisualizator graphVisualizator) {
		this.graphVisualizator = graphVisualizator;		
	}
	
	private void refreshGraph(){
		this.graphVisualizator.refreshGraph();
	}
	
	public SampleModuleConfiguration getModules() {
		return modules;
	}
}
