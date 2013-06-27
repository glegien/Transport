package pl.edu.agh.cs.kraksim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import pl.edu.agh.cs.kraksim.parser.PredictionConfigurationXmlHandler;
import pl.edu.agh.cs.kraksim.routing.prediction.DefaultTrafficPredictionSetup;
import pl.edu.agh.cs.kraksim.routing.prediction.ITrafficPredictionSetup;
import pl.edu.agh.cs.kraksim.routing.prediction.TrafficLevelDiscretiser;
import pl.edu.agh.cs.kraksim.routing.prediction.TrafficPredictionFactory;


public class KraksimConfigurator {
	private static final Logger logger = Logger.getLogger(KraksimConfigurator.class);
	private static final String DEFAULT_CONFIG_PATH = getConfigPath();
	
	public static String getConfigPath() {
		Properties properties = new Properties();
		File f = new File("mainConfig.properties");
		try {
			InputStream inStream = new FileInputStream(f);
			properties.load(inStream);
			inStream.close();
		} catch (FileNotFoundException e) {
			logger.error("No file found: " + f.getAbsolutePath());
			System.exit(-1);
		} catch (IOException e) {
			logger.error("Invalid file format: File " + f.getAbsolutePath());
			System.exit(-1);
		}
		return properties.getProperty("configuration");
	}
	
	public static Properties getPropertiesFromFile(String ... args){
		File f = null;
		String configPath = null;
		if (args.length > 0){
			configPath = args[0];
			f = new File(configPath);
			if (f.canRead() == false){
				configPath = DEFAULT_CONFIG_PATH;
			}
		}else{
			configPath = DEFAULT_CONFIG_PATH;
		}
		
		f = new File (configPath);
		if (f.canRead() == false){
			logger.error("The config file " + f.getAbsolutePath() + " cannot be read");
			System.exit(-1);
		}
		
		Properties result = new Properties();
		
		try {
			InputStream inStream = new FileInputStream(f);
			result.load(inStream);
		} catch (FileNotFoundException e) {
			logger.error("No file found: " + f.getAbsolutePath());
			System.exit(-1);
		} catch (IOException e) {
			logger.error("Invalid file format: File " + f.getAbsolutePath());
			System.exit(-1);
		}
		
		return result;
	}

	public static String[] prepareInputParametersForSimulation (Properties params){
		String visualization = params.getProperty("visualization");

		boolean visualize;
		if (visualization.equals("true")) {
			visualize = true;
		} else {
			visualize = false;
		}
		List<String> paramsList = new ArrayList<String>();
		if (visualize) {
			paramsList.add("-v");
		} else {
			paramsList.add("-g");
		}
		
		String minimalSpeedUsingPrediction = params.getProperty("minimalSpeedUsingPrediction");
		if (minimalSpeedUsingPrediction != null && minimalSpeedUsingPrediction.equals("true")) {
		    paramsList.add("-m");
		}
		
        if (params.getProperty("globalUpdateInterval") != null) {
            paramsList.add("-u");
            paramsList.add( params.getProperty("globalUpdateInterval") );
        }

		paramsList.add("-t");
		paramsList.add(params.getProperty("yellowTransition"));
		paramsList.add("-s");
		paramsList.add("975");
		paramsList.add("-S");
		paramsList.add("1298");

		String routing = params.getProperty("dynamicRouting");
		if ((routing != null) && ! (routing.equals(""))) {
			paramsList.add("-r");
			paramsList.add(routing);
			paramsList.add("-d");
			paramsList.add("100");
			paramsList.add("-k");
			paramsList.add("100");
		}
		
		String enablePrediction = params.getProperty("enablePrediction");
		if ((enablePrediction != null) && ! (enablePrediction.equals(""))) {
			paramsList.add("-e");
			paramsList.add(enablePrediction);
		} else {
			paramsList.add("-e");
			paramsList.add("false");
		}
		
		String predictionModule = params.getProperty("predictionModule");
		if ((predictionModule != null) && ! (predictionModule.equals(""))) {
			paramsList.add("-a");
			paramsList.add(predictionModule);
		} else {
			paramsList.add("-a");
			paramsList.add("false");
		}
		
		String statOutFileParam = params.getProperty("statOutFile");
		if ((statOutFileParam != null) && ! (statOutFileParam.equals(""))) {
			paramsList.add("-o");
			paramsList.add(statOutFileParam);
		}

		paramsList.add(params.getProperty("algorithm"));
		paramsList.add(params.getProperty("cityMapFile"));
		paramsList.add(params.getProperty("travelSchemeFile"));

		return paramsList.toArray(new String[0]);
	}
	
	public static void configurePrediction (String configFile){
		if (configFile == null){
			KraksimConfigurator.createDefaultPrediction();
			return;
		}
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			
			sp.parse(configFile, new PredictionConfigurationXmlHandler());
		} catch (ParserConfigurationException e) {
			KraksimConfigurator.disablePrediction();
			e.printStackTrace();
		} catch (SAXException e) {
			KraksimConfigurator.disablePrediction();
			e.printStackTrace();
		} catch (IOException e) {
			KraksimConfigurator.disablePrediction();
			e.printStackTrace();
		}
	}
	
	private static void createDefaultPrediction() {
		ITrafficPredictionSetup predictionSetup = new DefaultTrafficPredictionSetup();
		predictionSetup.setCutOutProbability(0.75);
		predictionSetup.setCutOutMinimumCounter(3);
		predictionSetup.setDiscretiser(KraksimConfigurator.createDefaultDiscretiser());
		predictionSetup.setNumberOfInfluencedLinks(3);
		predictionSetup.setNumberOfInfluencedTimesteps(3);
		TrafficPredictionFactory.setPropertiesForPredictionSetup(predictionSetup);		
	}

	public static void disablePrediction (){
		ITrafficPredictionSetup predictionSetup = new DefaultTrafficPredictionSetup();
		predictionSetup.setCutOutProbability(1.5);
		predictionSetup.setCutOutMinimumCounter(Integer.MAX_VALUE);
		predictionSetup.setDiscretiser(new TrafficLevelDiscretiser());
		predictionSetup.setNumberOfInfluencedLinks(0);
		predictionSetup.setNumberOfInfluencedTimesteps(0);
		TrafficPredictionFactory.setPropertiesForPredictionSetup(predictionSetup);
	}
	
	public static TrafficLevelDiscretiser createDefaultDiscretiser(){
		TrafficLevelDiscretiser result = new TrafficLevelDiscretiser();
		result.populateTrafficLevels();
		return result;
	}
	
	/**
	 * Pobiera z pliku konfiguracyjnego "mainConfig.properties" tryb, w którym mają być wyznaczane
	 * główne węzły w grafie.
	 * @return Wartość property jako String
	 */
	public static String getSNADistanceType() {
		Properties properties = new Properties();
		File f = new File("mainConfig.properties");
		try {
			InputStream inStream = new FileInputStream(f);
			properties.load(inStream);
			inStream.close();
		} catch (FileNotFoundException e) {
			logger.error("No file found: " + f.getAbsolutePath());
			System.exit(-1);
		} catch (IOException e) {
			logger.error("Invalid file format: File " + f.getAbsolutePath());
			System.exit(-1);
		}
		return properties.getProperty("SNADistanceType");
	}
}
