package pl.edu.agh.cs.kraksim.parser;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import pl.edu.agh.cs.kraksim.routing.prediction.DefaultTrafficPredictionSetup;
import pl.edu.agh.cs.kraksim.routing.prediction.ITrafficPredictionSetup;
import pl.edu.agh.cs.kraksim.routing.prediction.TrafficLevel;
import pl.edu.agh.cs.kraksim.routing.prediction.TrafficLevelDiscretiser;
import pl.edu.agh.cs.kraksim.routing.prediction.TrafficPredictionException;
import pl.edu.agh.cs.kraksim.routing.prediction.TrafficPredictionFactory;

public class PredictionConfigurationXmlHandler extends DefaultHandler {
	private static final Logger logger = Logger.getLogger(PredictionConfigurationXmlHandler.class);
	
	private enum Level{
		INIT, PREDICTION, TRAFFIC_CONF, LEVEL
	}
	private Level level = Level.INIT;
	
	private TrafficLevelDiscretiser discretiser = null;
	private ITrafficPredictionSetup setup = null;
	
	public void startDocument(){
		this.setup = new DefaultTrafficPredictionSetup();
		this.discretiser = new TrafficLevelDiscretiser();
		this.setup.setDiscretiser(this.discretiser);
	}
	
	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes attrs){

		switch(this.level){
		case INIT:
			if (rawName.equals("prediction")){
				this.level = Level.PREDICTION;
				this.configurePrediction(rawName, attrs);
			}
			break;
		case PREDICTION:
			if (rawName.equals("trafficLevels")){
				this.level = Level.TRAFFIC_CONF;
			}
			break;
		case TRAFFIC_CONF:
			if (rawName.equals("level")){
				this.level = Level.LEVEL;
				this.appendLevel (rawName, attrs);
			}
			break;
		}
	}
	
	private void appendLevel(String rawName, Attributes attrs) {
		TrafficLevel trLvl = new TrafficLevel();
		String description = attrs.getValue("description"); 
		String lowerBound = attrs.getValue("lowerBound"); 
		String upperBound = attrs.getValue("upperBound");
		String influence = attrs.getValue("influence");
		String prevDescription = attrs.getValue("prevDescription");
		String nextDescription = attrs.getValue("nextDescription");
		
		double lBound = Double.parseDouble(lowerBound);
		double uBound = Double.parseDouble(upperBound);
		double maxInfluence = Double.parseDouble(influence);
		
		trLvl.setDescription(description);
		trLvl.setLowerLevel(lBound);
		trLvl.setUpperLevel(uBound);
		trLvl.setMaxInfluence(maxInfluence);
		
		try {
			this.discretiser.addTrafficLevel(trLvl);
		} catch (TrafficPredictionException e) {
			logger.error(e);
			return;
		}
		
		try{
			TrafficLevel temp = this.discretiser.getLevelByName(prevDescription);
			temp.setProceeder(trLvl);
			trLvl.setPredecessor(temp);
		}catch (TrafficPredictionException ex){	}

		try{
			TrafficLevel temp = this.discretiser.getLevelByName(nextDescription);
			trLvl.setProceeder(trLvl);
			temp.setPredecessor(temp);
		}catch (TrafficPredictionException ex){	}
	}
	
	private void configurePrediction(String rawName, Attributes attrs) {
		String cop = attrs.getValue("cutOutProbability");
		String comn = attrs.getValue("cutOutMinimumNumber");
		String r = attrs.getValue("neighborhoodSize");
		String h = attrs.getValue("influencedTimesteps");
		String age = attrs.getValue("ageingRate");
		
		double cutOutProp = Double.parseDouble(cop);
		int cutOutMinNo = Integer.parseInt(comn);
		int ngbSize = Integer.parseInt(r);
		int deltaT = Integer.parseInt(h);
		double ageingRate = 1.;
		try{
			ageingRate = Double.parseDouble(age);
		}catch(Throwable t){}
		
		this.setup.setCutOutProbability(cutOutProp);
		this.setup.setCutOutMinimumCounter(cutOutMinNo);
		this.setup.setNumberOfInfluencedLinks(ngbSize);
		this.setup.setNumberOfInfluencedTimesteps(deltaT);
		this.setup.setAgeingRate(ageingRate);
	}
	
	/** Ignorable whitespace. */
	@Override
	public void ignorableWhitespace(char ch[], int start, int length) {
	}

	/** Characters. */
	@Override
	public void characters(char ch[], int start, int length) {
	}

	/** End element. */
	@Override
	public void endElement(String namespaceURI, String localName, String rawName) {
		
		switch (level) {
		case INIT:
			break;
		case PREDICTION:
			if (rawName.equals("prediction")){
				this.level = Level.INIT;
			}
			break;
		case TRAFFIC_CONF:
			if (rawName.equals("trafficLevels")){
				this.level=Level.PREDICTION;
				this.setup.setDiscretiser(this.discretiser);
			}
			break;
		case LEVEL:
			if (rawName.equals("level")){
				this.level = Level.TRAFFIC_CONF;
			}
			break;
		}
	}

	/** End document. */
	@Override
	public void endDocument() {
		if (this.discretiser.getNumberOfLevels() < 1){
			this.discretiser.populateTrafficLevels();
		}
		this.setup.setDiscretiser(this.discretiser);
		TrafficPredictionFactory.setPropertiesForPredictionSetup(this.setup);
	} 

	/** Warning. */
	@Override
	public void warning(SAXParseException ex) {
		logger.error("[Warning]: "
				+ ex.getMessage());
	}

	/** Error. */
	@Override
	public void error(SAXParseException ex) {
		logger.error("[Error]: "
				+ ex.getMessage());
	}

	/** Fatal error. */
	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		logger.error("[Fatal Error]: "
				+ ex.getMessage());
		throw ex;
	}
}
