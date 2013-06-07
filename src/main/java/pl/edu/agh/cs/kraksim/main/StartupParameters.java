package pl.edu.agh.cs.kraksim.main;

import gnu.getopt.Getopt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;

public class StartupParameters {
	private static final Logger logger = Logger.getLogger(StartupParameters.class);

	private final static int DEFAULT_TRANSITION_DURATION = 8;
	private final static int DEFAULT_LEARN_REP_COUNT = 0;
	public final static String PROG_NAME = "kraksim";

	private String statFileName = null;
	private boolean visualization = false;
	private int transitionDuration;
	private String algorithmName = null;
	private String modelFile = null;
	private String trafficSchemeFile = null;
	private int learnPhaseCount;

	private boolean opanel = true;
	private boolean enablePrediction = false;
	private String predictionModule;
	private boolean rerouting = false;
	private boolean commandLineMode = false;
	private boolean minimalSpeedUsingPrediction = false;

	private long modelSeed;
	private long genSeed;
	private final Random modelRg;
	private final Random genRg;

	private int routeDecisionTh = 0;
	private Random decisionRg;
	private Random isDriverRoutingRg;
	private int isDriverRoutingTh = 0;

	private long globalUpdateInterval = 300;



	public StartupParameters() {

		decisionRg = new Random(909090);
		isDriverRoutingRg = new Random(919191);
		transitionDuration = DEFAULT_TRANSITION_DURATION;
		// modelSeed = System.currentTimeMillis();
		modelSeed = 121212;
		genSeed = 31 * modelSeed;

		modelRg = new Random(modelSeed);
		genRg = new Random(genSeed);
		learnPhaseCount = DEFAULT_LEARN_REP_COUNT;
	}

	// ===================================================================================
	// --- SET UP METHODS
	// ===================================================================================

	public void parseOptions(final String[] args, PrintWriter console) {
		final Getopt optionsHolder = new Getopt(PROG_NAME, args, "+:hmvt:s:S:l:o:pr:gd:k:u:e:a:");
		optionsHolder.setOpterr(true);

		int option;
		while ((option = optionsHolder.getopt()) != -1) {
			switch (option) {
			case 'h': {
				printUsage(console);
				System.exit(0);
			}
			case 'p': {
				opanel = true;
				break;
			}
			case 'v': {
				visualization = true;
				break;
			}
			case 'm': {
				minimalSpeedUsingPrediction = true;
				break;
			}
			case 't': {
				try {
					transitionDuration = Integer.parseInt(optionsHolder.getOptarg());
					if (transitionDuration < 0) {
						throw new NumberFormatException();
					}
					console.print("tr=" + transitionDuration);
				} catch (NumberFormatException e) {
					error("Error: invalid transition duration - must be a nonnegative number", e);
				}
				break;
			}
			case 'd': {
				try {
					routeDecisionTh = Integer.parseInt(optionsHolder.getOptarg());
					if (routeDecisionTh < 0) {
						throw new NumberFormatException();
					}
					System.err.print("dec=" + routeDecisionTh);
					console.print(" dec=" + routeDecisionTh);
				} catch (NumberFormatException e) {
					error("Error: invalid routeDecisionTh - must be a nonnegative number", e);
				}
				break;
			}
			case 'k': {
				try {
					isDriverRoutingTh = Integer.parseInt(optionsHolder.getOptarg());
					if (isDriverRoutingTh < 0) {
						throw new NumberFormatException();
					}
					System.err.print(", drv=" + isDriverRoutingTh + " ");
					console.print(", drv=" + isDriverRoutingTh);
				} catch (NumberFormatException e) {
					error("Error: invalid isDriverRoutingTh - must be a nonnegative number", e);
				}
				break;
			}
			case 'u': {
				try {
					globalUpdateInterval = Integer.parseInt(optionsHolder.getOptarg());
					if (globalUpdateInterval < 0) {
						throw new NumberFormatException();
					}
					System.err.print(", gui=" + globalUpdateInterval + " ");
					console.print(", gui=" + globalUpdateInterval);
				} catch (NumberFormatException e) {
					error("Error: invalid isDriverRoutingTh - must be a nonnegative number", e);
				}
				break;
			}
			case 's': {
				try {
					setModelSeed(Long.parseLong(optionsHolder.getOptarg()));
				} catch (NumberFormatException e) {
					error("Error: invalid model seed - must be a number", e);
				}
				break;
			}
			case 'S': {
				try {
					setGenSeed(Long.parseLong(optionsHolder.getOptarg()));
				} catch (NumberFormatException e) {
					error("Error: invalid traffic generation seed - must be a number", e);
				}
				break;
			}
			case 'l': {
				try {
					learnPhaseCount = Integer.parseInt(optionsHolder.getOptarg());
					if (learnPhaseCount < 0) {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException e) {
					error("Error: invalid learning phase count - must be a nonnegative number", e);
				}
				break;
			}
			case 'o': {
				statFileName = optionsHolder.getOptarg();
				break;
			}
			case 'r': {
				String routing = optionsHolder.getOptarg();
				if ("true".equals(routing)) {
					rerouting = true;
				}

				break;
			}
			case 'e': {
				enablePrediction = Boolean.parseBoolean(optionsHolder.getOptarg());
				break;
			}
			case 'a': {
				predictionModule = optionsHolder.getOptarg();
				break;
			}
			case 'g': {
				commandLineMode = true;
				break;
			}
			case '?': {
				error("Error: invalid option -- " + new Character((char) option));
			}
			case ':': {
				error("Error: option given without an obligatory option argument -- " + option);
			}
			default: {
				printUsage(console);
				System.exit(1);
			}
			} // switch
		}

		int optIndex = optionsHolder.getOptind();
		if (optIndex + 3 != args.length) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			printUsage(pw);
			error("Invalid number of arguments" + sw.toString());
		}
		algorithmName = args[optIndex];
		modelFile = args[++optIndex];
		trafficSchemeFile = args[++optIndex];
	}

	private static void printUsage(final PrintWriter writer) {
		writer.println("usage: " + StartupParameters.PROG_NAME + " [options] algConf modelFile trafficFile");
		writer.println();
		writer.println("\talgConf selects and configures traffic light system driver.\n"
				+ "\tsyntax: algConf = algName[:param1=val,param2=var,...]\n"
				+ "\tFor the list of algorithms and their parameters see below.");
		writer.println();
		writer.println("options:\n" + "\t-v                    : turns on visualization (default: off)\n"
				+ "\t-t transitionDuration : sets the duration of traffic lights'\n"
				+ "\t                        transitional state (default: 8)\n"
				+ "\t-h                    : shows help\n"
				+ "\t-s modelSeed          : sets the seed of the traffic simulator RNG\n"
				+ "\t                        (default: based on the system clock)\n"
				+ "\t-S genSeed            : sets the seed of the traffic generator RNG\n"
				+ "\t                        (default: based on system clock)\n"
				+ "\t-l learnPhaseCount    : number of learning phases (default: 0)\n"
				+ "\t-o statFile           : statistics file name\n"
				+ "\t                        (default: no statistics are generated)");
		writer.println();
		writer.println("algorithms:");

		//TODO rewrite it - options has changed
//		final EvalModuleProvider[] providers = getEvalProviders();
//		for (int i = 0; i < providers.length; i++) {
//			EvalModuleProvider provider = providers[i];
//			writer.println("\t" + provider.getAlgorithmCode() + ": " + provider.getAlgorithmName());
//			writer.println("\t parameters:");
//
//			Iterator<KeyValPair> iter = provider.getParamsDescription();
//			while (iter.hasNext()) {
//				KeyValPair pair = iter.next();
//				writer.println("\t\t" + pair.getKey() + ": " + pair.getVal());
//			}
//
//		}
	}

	public static EvalModuleProvider[] getEvalProviders() {
		return new EvalModuleProvider[] { new SOTLModuleProvider(), new RLModuleProvider(), new RLCDModuleProvider(),
				new EmptyModuleProvider("sync"), new EmptyModuleProvider("static") };
	}

	// ===================================================================================
	// --- ACCESSOR METHODS - GET/SET
	// ===================================================================================

	public String getStatFileName() {
		return statFileName;
	}

	public void setStatFileName(String statFileName) {
		this.statFileName = statFileName;
	}

	public boolean isRerouting() {
		return rerouting;
	}

	public void setRerouting(boolean rerouting) {
		this.rerouting = rerouting;
	}

	public boolean isCommandLineMode() {
		return commandLineMode;
	}

	public void setCommandLineMode(boolean commandLineMode) {
		this.commandLineMode = commandLineMode;
	}

	public boolean isOpanel() {
		return opanel;
	}

	public void setOpanel(boolean opanel) {
		this.opanel = opanel;
	}

	public boolean isVisualization() {
		return visualization;
	}

	public void setVisualization(boolean visualization) {
		this.visualization = visualization;
	}

	public boolean isMinimalSpeedUsingPrediction() {
		return minimalSpeedUsingPrediction;
	}

	public void setMinimalSpeedUsingPrediction(boolean minimalSpeedUsingPrediction) {
		this.minimalSpeedUsingPrediction = minimalSpeedUsingPrediction;
	}

	public int getTransitionDuration() {
		return transitionDuration;
	}

	public void setTransitionDuration(int transitionDuration) {
		this.transitionDuration = transitionDuration;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public String getModelFile() {
		return modelFile;
	}

	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}

	public String getTrafficSchemeFile() {
		return trafficSchemeFile;
	}

	public void setTrafficSchemeFile(String trafficSchemeFile) {
		this.trafficSchemeFile = trafficSchemeFile;
	}

	public long getModelSeed() {
		return modelSeed;
	}

	public void setModelSeed(long modelSeed) {
		this.modelSeed = modelSeed;
		this.modelRg.setSeed(modelSeed);
	}

	public long getGenSeed() {
		return genSeed;
	}

	public void setGenSeed(long genSeed) {
		this.genSeed = genSeed;
		this.genRg.setSeed(genSeed);
	}

	public Random getModelRg() {
		return modelRg;
	}

	public Random getGenRg() {
		return genRg;
	}

	public int getLearnPhaseCount() {
		return learnPhaseCount;
	}

	public void setLearnPhaseCount(int learnPhaseCount) {
		this.learnPhaseCount = learnPhaseCount;
	}

	// ===================================================================================
	// --- HELPER ERROR HANDLING
	// ===================================================================================

	private void error(final String text, final Throwable error) {
		logger.error(text + "\n  Details: " + error.getMessage());

		System.exit(1);
	}

	private void error(final String text) {
		logger.error(text);
		System.exit(1);
	}

	public int getRouteDecisionTh() {
		return routeDecisionTh;
	}

	public Random getDecisionRg() {
		return decisionRg;
	}

	public int getDriverRoutingTh() {
		return isDriverRoutingTh;
	}

	public Random getDriverRoutingRg() {
		return isDriverRoutingRg;
	}

	public long getGlobalInforUpdateInterval() {
		return globalUpdateInterval;
	}

	public boolean isEnablePrediction() {
		return enablePrediction;
	}

	public String getPredictionModule() {
		return predictionModule;
	}
}
