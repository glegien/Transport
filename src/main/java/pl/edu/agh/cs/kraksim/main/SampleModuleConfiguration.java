package pl.edu.agh.cs.kraksim.main;

import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.uci.ics.jung.graph.Graph;

import pl.edu.agh.cs.kraksim.KraksimConfigurator;
import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Core;
import pl.edu.agh.cs.kraksim.core.InvalidClassSetDefException;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleCreationException;
import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.dsyncdecision.DsyncDecisionModuleCreator;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;
import pl.edu.agh.cs.kraksim.iface.decision.DecisionIView;
import pl.edu.agh.cs.kraksim.iface.eval.EvalIView;
import pl.edu.agh.cs.kraksim.iface.mon.MonIView;
import pl.edu.agh.cs.kraksim.iface.sim.SimIView;
import pl.edu.agh.cs.kraksim.ministat.MiniStatEView;
import pl.edu.agh.cs.kraksim.ministat.MiniStatModuleCreator;
import pl.edu.agh.cs.kraksim.real.RealModuleCreator;
import pl.edu.agh.cs.kraksim.real.RealSimulationParams;
import pl.edu.agh.cs.kraksim.routing.ITimeTable;
import pl.edu.agh.cs.kraksim.routing.Router;
import pl.edu.agh.cs.kraksim.routing.StaticRouter;
import pl.edu.agh.cs.kraksim.routing.TimeBasedRouter;
import pl.edu.agh.cs.kraksim.routing.TimeTable;
import pl.edu.agh.cs.kraksim.routing.TimeTableRules;
import pl.edu.agh.cs.kraksim.simpledecision.SimpleDecisionEView;
import pl.edu.agh.cs.kraksim.simpledecision.SimpleDecisionModuleCreator;
import pl.edu.agh.cs.kraksim.sna.centrality.CentrallityCalculator;
import pl.edu.agh.cs.kraksim.sna.centrality.MeasureType;
import pl.edu.agh.cs.kraksim.weka.WekaPredictionModule;
import pl.edu.agh.cs.kraksim.weka.WekaPredictionModuleHandler;

public class SampleModuleConfiguration {
	private static final Logger logger = Logger.getLogger(SampleModuleConfiguration.class);
	private City city;
	private Router router;
	private TimeBasedRouter dynamicRouter;
	private WekaPredictionModuleHandler wekaPredictionHandler = new WekaPredictionModuleHandler();
	private SimIView simView;
	private MiniStatEView statView;
	private EvalIView evalView;
	private DecisionIView decisionView;
	private CarInfoIView carInfoView;
	private BlockIView blockView;

	
	private Graph<Node, Link> graph;
	
	/**
	 * This is the place where all the binding between modules is done.
	 * 
	 * @param core
	 * @param evalProvider
	 */
	public SimulationVisualizator setUpModules(final Core core, final EvalModuleProvider evalProvider, Clock clock,
			StartupParameters params) {
		SimulationVisualizator visualizator = null;
		city = core.getCity();
		
		graph = CentrallityCalculator.cityToGraph(city);

		try {
			// this is Nagel-Schreckenberg Simulation Module
			final Module physModule = core.newModule("phys",
					new RealModuleCreator(new RealSimulationParams(params.getModelRg())));
			simView = new SimIView(physModule);

			carInfoView = new CarInfoIView(physModule);
			final MonIView monView = new MonIView(physModule);
			blockView = new BlockIView(physModule);
			
			//TMP
			CentrallityCalculator.carInfoView = carInfoView;
			Iterator<Link> it = city.linkIterator();
			while(it.hasNext())
				it.next().calculateWeight(0);
			CentrallityCalculator.calculateCentrallity(graph, MeasureType.PageRank,3);
			//tylko do wypisywania
			//CentrallityCalculator.calculateCentrallity(city, MeasureType.PageRank);
			//END TMP

			final Module statModule = core.newModule("stat", new MiniStatModuleCreator(monView, clock));
			statView = new MiniStatEView(statModule);

			ITimeTable timeTable = null;
			if (params.isRerouting()) {
				if (params.getPredictionModule().equals("weka") && params.isEnablePrediction()) {
					WekaPredictionModule predictionModule = new WekaPredictionModule(city, statView, carInfoView, clock);
					wekaPredictionHandler.setPredictionModule(predictionModule);
					timeTable = new TimeTableRules(city, clock, predictionModule);
				} else {
					timeTable = new TimeTable(city, statView, clock, params.getGlobalInforUpdateInterval());
				}
				dynamicRouter = new TimeBasedRouter(city, timeTable);
				router = dynamicRouter;
			} else {
				router = new StaticRouter(city);

				if (params.getPredictionModule().equals("weka") && params.isEnablePrediction()) {
					WekaPredictionModule predictionModule = new WekaPredictionModule(city, statView, carInfoView, clock);
					wekaPredictionHandler.setPredictionModule(predictionModule);
					timeTable = new TimeTableRules(city, clock, predictionModule);
					logger.debug("Prediction configured");
				}
			}

			final Module evalModule = evalProvider.provideNew("eval", core, carInfoView, monView, blockView, 2,
					RealSimulationParams.DEFAULT_MAX_VELOCITY);
			if (evalModule != null) {
				evalView = new EvalIView(evalModule);

				ITimeTable timeTableToPass = null;
				if (params.isMinimalSpeedUsingPrediction()) {
					timeTableToPass = timeTable;
				}

				final Module decisionModule = core.newModule("decision",
						new SimpleDecisionModuleCreator(evalView, blockView, carInfoView, statView, timeTableToPass,
								wekaPredictionHandler, clock, params.getTransitionDuration()));
				decisionView = new DecisionIView(decisionModule);
				SimpleDecisionEView simpleDecisionView = new SimpleDecisionEView(decisionModule);
				wekaPredictionHandler.setEvalView(evalView);
				wekaPredictionHandler.setSimpleDecisionView(simpleDecisionView);
			} else {

				final Module newDecisionModule = core.newModule("newDecision", new DsyncDecisionModuleCreator(statView,
						blockView, clock, params.getTransitionDuration(), evalProvider.getAlgorithmCode()
								.equals("sync")));
				decisionView = new DecisionIView(newDecisionModule);
			}

			if (params.isVisualization()) {
				visualizator = new GUISimulationVisualizator(city, carInfoView, blockView, statView);
				if (!params.isOpanel()) {
					((GUISimulationVisualizator) visualizator).createWindow();
				}
			} else {
				visualizator = new ConsoleSimulationVisualizator(city, statView);
			}
		} catch (InvalidClassSetDefException e) {
			error("Internal error", e);
		} catch (ModuleCreationException e) {
			error("Error while creating module", e);
		}

		return visualizator;

	}

	private void error(final String text, final Throwable error) {
		logger.error(text + "\n  Details: " + error.getMessage());
		System.exit(1);
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Router getRouter() {
		return router;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public TimeBasedRouter getDynamicRouter() {
		return dynamicRouter;
	}

	public void setDynamicRouter(TimeBasedRouter dynamicRouter) {
		this.dynamicRouter = dynamicRouter;
	}

	public SimIView getSimView() {
		return simView;
	}

	public void setSimView(SimIView simView) {
		this.simView = simView;
	}

	public MiniStatEView getStatView() {
		return statView;
	}

	public void setStatView(MiniStatEView statView) {
		this.statView = statView;
	}

	public EvalIView getEvalView() {
		return evalView;
	}

	public void setEvalView(EvalIView evalView) {
		this.evalView = evalView;
	}

	public DecisionIView getDecisionView() {
		return decisionView;
	}

	public void setDecisionView(DecisionIView decisionView) {
		this.decisionView = decisionView;
	}

	public CarInfoIView getCarInfoView() {
		return this.carInfoView;
	}

	public BlockIView getBlockView() {
		return this.blockView;
	}

	public WekaPredictionModuleHandler getWekaPrediction() {
		return wekaPredictionHandler;
	}
	
	public Graph<Node, Link> getGraph() {
		return graph;
	}

}
