package pl.edu.agh.cs.kraksim.parser;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import pl.edu.agh.cs.kraksim.KraksimRuntimeException;
import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Core;
import pl.edu.agh.cs.kraksim.core.DuplicateIdentifierException;
import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.InvalidActionException;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.LinkAttachmentException;
import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.core.Phase;
import pl.edu.agh.cs.kraksim.core.PhaseTiming;
import pl.edu.agh.cs.kraksim.core.UnsupportedLinkOperationException;

/**
 * Zmiany 080115: 
 * - dodano parsowanie numberOfLanes (funkcja createLane)
 * @author Lukasz Dziewonski
 *
 */
public class RoadNetXmlHandler extends DefaultHandler {

	private static final Logger logger = Logger.getLogger(Level.class);

	private enum Level {
		INIT, ROADS, ROAD, UPLINK, DOWNLINK, LEFT, CENTER, RIGHT, NODES, INTERSECTIONS, INTERSECTION, ARM_ACTIONS, ACTION, ACTIONRULE, TRAFFIC_LIGHTS_SCHEDULE, PHASE_LEVEL, TIMING_PLAN_LEVEL
	}

	private Level level = Level.INIT;

	Stack<RoadInfo> roadStack = null;
	List<Integer> leftLaneLenTab;
	List<Integer> rightLaneLenTab;
	int mainLaneLen;
	/**
	 * @author Lukasz Dziewonski
	 *
	 */
	int numberOfLanes;

	Core core;
	City city;
	private Phase phase;
	private PhaseTiming phaseTiming;

	private String intersectionName;
	private String armFromName;
	private String direction;
	private String armToName;

	Link il = null;
	List<Lane> lanes = new LinkedList<Lane>();
//	Lane lane = null;
	Link ol = null;
	LinkedList<Lane> ll = new LinkedList<Lane>();
	LinkedList<Phase> phasesSet;
	LinkedList<PhaseTiming> timingPlan;

	private String timingPlanName;
	//  private Locator         locator;
	private int defaultSpeedLimit = 2;

	//  private String      trafficPlanName;

	public RoadNetXmlHandler() {
		super();
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		//  TODO Auto-generated method stub
		//    this.locator = locator;
	}

	/** Start document. */
	@Override
	public void startDocument() {
		// elementStack = new Stack();
		roadStack = new Stack<RoadInfo>();
		leftLaneLenTab = new ArrayList<Integer>();
		rightLaneLenTab = new ArrayList<Integer>();
		core = new Core();

		city = core.getCity();
		// roads = new LinkedHashMap<String, Road>();
		// nodes = new LinkedHashMap<String, Node>();
		// //System.out.println("BEGIN DOCUMENT ");
	}

	private String outAttribs(Attributes attrs) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < attrs.getLength(); i++) {
			sb.append(attrs.getValue(i));
		}
		return sb.toString();

	}

	/** Start element. */

	@Override
	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes attrs) {
		if (logger.isTraceEnabled()) {
			logger.trace(level.toString() + " -> " + localName + " "
					+ outAttribs(attrs));
		}
		switch (level) {
		case ROADS:
			createRoad(rawName, attrs);
			break;

		case ROAD: {

			if (rawName.equals("downlink")) {
				downLink(rawName, attrs);
			}

			if (rawName.equals("uplink")) {
				upLink(rawName, attrs);
			}
		}
			break;

		case UPLINK:
			createLane(rawName, attrs);
			break;

		case DOWNLINK:
			createLane(rawName, attrs);
			break;

		case NODES: {

			if (rawName.equals("gateway")) {
				createGatewayNode(attrs);
			}

			if (rawName.equals("intersection")) {
				createIntersectionNode(attrs);
			}
		}
			break;

		case INTERSECTIONS:
			createIntersectionDescription(rawName, attrs);
			break;

		case INTERSECTION: {

			if (rawName.equals("armActions")) {
				createArmAction(attrs);
			}

			if (rawName.equals("trafficLightsSchedule")) {
				createTrafficLightsSchedule(attrs);
			}
		}
			break;

		case ARM_ACTIONS:
			createAction(rawName, attrs);
			break;

		case ACTION:
			createRule(rawName, attrs);
			break;

		case TRAFFIC_LIGHTS_SCHEDULE:
			if (rawName.equals("plan")) {
				createTrafficPlan(attrs);
			}

			if (rawName.equals("phase")) {
				createPhase(attrs);
			}
			break;

		case TIMING_PLAN_LEVEL:
			if (rawName.equals("phase")) {
				createTrafficPhaseTiming(attrs);
			}
			break;

		case PHASE_LEVEL:
			createInlaneState(attrs);
			break;

		default: {

			// jeszcze nie ustalony stan
			if (rawName.equals("roads")) {
				level = Level.ROADS;
				String speedLimit = attrs.getValue("defaultSpeedLimit");
				defaultSpeedLimit = (speedLimit == null) ? 2 : Integer
						.parseInt(speedLimit);
			}

			if (rawName.equals("nodes")) {
				level = Level.NODES;
			}

			if (rawName.equals("intersectionDescriptions")) {
				level = Level.INTERSECTIONS;
			}

			if (rawName.equals("RoadNet")) {
				// Just the beggining of the Road Network Document 
			}
		}
			break;

		}

	}

	// ===================================================
	// STARTing ELEMENTs
	// ===================================================
	private void createInlaneState(Attributes attrs) {
		String arm = attrs.getValue("arm");
		int lane = Integer.parseInt(attrs.getValue("lane"));
		boolean green = (attrs.getValue("state").equals("green")) ? true
				: false;

		phase.addConfiguration(arm, lane, green);
	}

	private void createTrafficPhaseTiming(Attributes attrs) {
		String name = attrs.getValue("name");
		int phaseId = Integer.parseInt(attrs.getValue("num"));
		int phaseDuration = Integer.parseInt(attrs.getValue("duration"));

		phaseTiming = new PhaseTiming(phaseId, name, phaseDuration);
	}

	private void createTrafficPlan(Attributes attrs) {
		level = Level.TIMING_PLAN_LEVEL;
		String name = attrs.getValue("name");
		timingPlan = new LinkedList<PhaseTiming>();
		timingPlanName = name;
	}

	private void createPhase(Attributes attrs) {
		String name = attrs.getValue("name");
		level = Level.PHASE_LEVEL;
		int phaseId = Integer.parseInt(attrs.getValue("num"));
		// optapo
		String dir = attrs.getValue("syncDir");
		phase = new Phase(name, phaseId, dir);

	}

	private void createTrafficLightsSchedule(Attributes attrs) {
		level = Level.TRAFFIC_LIGHTS_SCHEDULE;
		phasesSet = new LinkedList<Phase>();
	}

	/**
	 * @param rawName
	 * @param attrs
	 */
	private void createRule(String rawName, Attributes attrs) {
		String nodeNme = "";
		try {
			if (rawName.equals("rule")) {

				nodeNme = attrs.getValue("entrance");
				Link ilTmp = city.findLink(nodeNme + intersectionName);

				if (ilTmp == null) {
					logger.error("cannot find Link " + nodeNme
							+ intersectionName);
				}
				int laneNr = Integer.parseInt(attrs.getValue("lane"));
				
				if (laneNr < 0) {
					ll.add(ilTmp.getLeftLane(Math.abs(laneNr) - 1));
				} else if (laneNr > 0) {
					ll.add(ilTmp.getRightLane(laneNr - 1));
				} else if (laneNr == 0) {
					for(int i = 0; i < ilTmp.mainLaneCount(); i++){
						ll.add(ilTmp.getMainLane(i));
					}
				}
				// System.out.println("ACTION " + rawName);
			}
		} catch (Exception e) {
			logger.error(nodeNme + intersectionName, e);
		}
	}

	/**
	 * @param rawName
	 * @param attrs
	 */
	private void createAction(String rawName, Attributes attrs) {
		if (rawName.equals("action")) {
			try {
				level = Level.ACTION;
				armToName = attrs.getValue("exit");
				int laneNr = Integer.parseInt(attrs.getValue("lane"));
				String linkName = intersectionName + armToName;

				ol = city.findLink(linkName);
				lanes.clear();
				if (laneNr < 0) {
//					lane = il.getLeftLane(laneNr + 1);
					lanes.add(il.getLeftLane(Math.abs(laneNr) - 1));
				} else if (laneNr > 0) {
//					lane = il.getRightLane(laneNr - 1);
					lanes.add(il.getRightLane(laneNr - 1));
				} else if (laneNr == 0) {
					// TODO: LDZ WIELEPASOW!!!
//					lane = il.getMainLane(laneNr);
					for(int i = 0; i < il.mainLaneCount(); i++){
						lanes.add(il.getMainLane(i));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println("ARM_ACTIONS_LEVEL " + rawName);
		}
	}

	/**
	 * @param attrs
	 */
	private void createArmAction(Attributes attrs) {
		level = Level.ARM_ACTIONS;
		armFromName = attrs.getValue("arm");// TODO, zmienic
		direction = attrs.getValue("dir");
		String linkName = armFromName + intersectionName;
		// rozbic na X1 x2, i zawsze to ktore jest rowne intersection
		// id,
		// bedzie na kocu, czyli drugie
		il = city.findLink(linkName);
		try{
			il.setDirection(direction);
		}catch(Exception e){
			e.printStackTrace();
		}
		// armFrom = intersection.inboundLinkIterator();
		// il = armFrom.getInboundLink();
		// System.out.println("INTERSECTION " + rawName);
	}

	/**
	 * @param rawName
	 * @param attrs
	 */
	private void createIntersectionDescription(String rawName, Attributes attrs) {
		if (rawName.equals("intersection")) {
			level = Level.INTERSECTION;
			intersectionName = attrs.getValue("id");
			// intersection = (Intersection)
			city.findNode(intersectionName);
			// System.out.println("INTERSECTIONS " + rawName);
		}
	}

	/**
	 * @param attrs
	 */
	private void createIntersectionNode(Attributes attrs) {
		String id = attrs.getValue("id");
		Double x = Double.parseDouble(attrs.getValue("x"));
		Double y = Double.parseDouble(attrs.getValue("y"));
		try {
			// Intersection is =
			city.createIntersection(id, new Point2D.Double(x, y));
		} catch (DuplicateIdentifierException e) {
			e.printStackTrace();
		}

		// level = Level.INTERSECTION_LEVEL;
		// System.out.println("NODES " + rawName);
	}

	/**
	 * @param attrs
	 */
	private void createGatewayNode(Attributes attrs) {
		String id = attrs.getValue("id");
		Double x = Double.parseDouble(attrs.getValue("x"));
		Double y = Double.parseDouble(attrs.getValue("y"));
		try {
			// Gateway gw =
			city.createGateway(id, new Point2D.Double(x, y));
		} catch (DuplicateIdentifierException e) {
			e.printStackTrace();
		}
		// level = Level.GATEWAY_LEVEL;
		// System.out.println("NODES " + rawName);
	}

	/**
	 * @param rawName
	 * @param attrs
	 */
	private void createLane(String rawName, Attributes attrs) {
		if (rawName.equals("main")) {
			mainLaneLen = Integer.parseInt(attrs.getValue("length"));
			String numberOfLanesStr = attrs.getValue("numberOfLanes");
			numberOfLanes = Integer.parseInt(numberOfLanesStr == null ? "1"
					: numberOfLanesStr);
			//      System.out.println( "LINK_LEVEL " + rawName );
			// System.out.println( "numberOfLanes " + numberOfLanes );
		}
		if (rawName.equals("left")) {
			leftLaneLenTab.add(Integer.parseInt(attrs.getValue("length")));
			//      System.out.println( "LINK_LEVEL " + rawName );
		}
		if (rawName.equals("right")) {
			rightLaneLenTab.add(Integer.parseInt(attrs.getValue("length")));
			// System.out.println("LINK_LEVEL " + rawName);
		}
	}

	/**
	 * @param attrs 
	 * @param rawName 
	 */
	private void upLink(String rawName, Attributes attrs) {
		level = Level.UPLINK;
		//System.out.println("ROAD " + rawName);
	}

	/**
	 * @param attrs 
	 * @param rawName 
	 */
	private void downLink(String rawName, Attributes attrs) {
		level = Level.DOWNLINK;
		//System.out.println("ROAD " + rawName);
	}

	/**
	 * @param rawName
	 * @param attrs
	 */
	private void createRoad(String rawName, Attributes attrs) {
		if (rawName.equals("road")) {
			String id = attrs.getValue("id");
			String street = attrs.getValue("street");
			String from = attrs.getValue("from");
			String to = attrs.getValue("to");
			String limit = attrs.getValue("speedLimit");
			String minimalSpeedStr = attrs.getValue("minimalSpeed");

			int speedLimit = (limit == null) ? defaultSpeedLimit : Integer
					.parseInt(limit);
			
			double minimalSpeed = (minimalSpeedStr == null) ? 0.0 : Double.parseDouble(minimalSpeedStr);

			Node fromNode = city.findNode(from);
			Node toNode = city.findNode(to);

			if (fromNode == null) {
				throw new KraksimRuntimeException("Bad Model, node " + from
						+ " not found");
			}

			if (toNode == null) {
				throw new KraksimRuntimeException("Bad Model, node " + to
						+ " not found");
			}

			roadStack.push(new RoadInfo(id, street, fromNode, toNode,
					speedLimit,minimalSpeed));
			level = Level.ROAD;
			// System.out.println("ROADS_LEVEL " + rawName);
		}
	}

	// ===================================================
	// STARTing ELEMENTs - end block
	// ===================================================
	/** Ignorable whitespace. */
	@Override
	public void ignorableWhitespace(char ch[], int start, int length) {
		// characters(ch, start, length);
	}

	/** Characters. */
	@Override
	public void characters(char ch[], int start, int length) {
	} // characters(char[],int,int);

	/** End element. */
	@Override
	public void endElement(String namespaceURI, String localName, String rawName) {
		switch (level) {

		case ROADS:

			if (rawName.equals("roads")) {
				// System.out.println("END ROADS_LEVEL " + rawName);
				level = Level.INIT;
			}
			break;

		case ROAD:

			if (rawName.equals("road")) {
				// System.out.println("END ROAD " + rawName);
				roadStack.pop();
				level = Level.ROADS;
			}
			break;

		case UPLINK:

			if (rawName.equals("uplink")) {
				// System.out.println("END UPLINK " + rawName);

				int[] l = new int[leftLaneLenTab.size()];
				int[] r = new int[rightLaneLenTab.size()];
				for (int i = 0; i < r.length; i++) {
					r[i] = rightLaneLenTab.get(i);
					// //System.out.println("prawe pasy = "+r[i]);
				}
				for (int i = 0; i < l.length; i++) {
					l[i] = leftLaneLenTab.get(i);
				}
				RoadInfo ri = roadStack.peek();
				try {
					// System.out.println(ri.getFrom().getId() +
					// ri.getTo().getId());
					city.createLink(ri.getFrom().getId() + ri.getTo().getId(),
							ri.getFrom(), ri.getTo(), ri.getStreet(), l,
							mainLaneLen, numberOfLanes, r, ri.getSpeedLimit(), ri.getMinimalSpeed());
				} catch (DuplicateIdentifierException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (LinkAttachmentException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				leftLaneLenTab = new ArrayList<Integer>();
				rightLaneLenTab = new ArrayList<Integer>();
				level = Level.ROAD;
			}
			break;

		case DOWNLINK:

			if (rawName.equals("downlink")) {
				// System.out.println("END DOWNLINK " + rawName);

				int[] l = new int[leftLaneLenTab.size()];
				int[] r = new int[rightLaneLenTab.size()];
				for (int i = 0; i < r.length; i++) {
					r[i] = rightLaneLenTab.get(i);
					// //System.out.println("prawe pasy = "+r[i]);
				}
				for (int i = 0; i < l.length; i++) {
					l[i] = leftLaneLenTab.get(i);
				}
				RoadInfo ri = roadStack.peek();
				try {
					// System.out.println(ri.getTo().getId() +
					// ri.getFrom().getId());
					city.createLink(ri.getTo().getId() + ri.getFrom().getId(),
							ri.getTo(), ri.getFrom(), ri.getStreet(), l,
							mainLaneLen, numberOfLanes, r, ri.getSpeedLimit(), ri.getMinimalSpeed());
				} catch (DuplicateIdentifierException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (LinkAttachmentException e) {
					e.printStackTrace();
				}

				leftLaneLenTab = new ArrayList<Integer>();
				rightLaneLenTab = new ArrayList<Integer>();
				level = Level.ROAD;
			}
			break;

		case NODES:

			if (rawName.equals("nodes")) {
				// System.out.println("END NODES " + rawName);
				level = Level.INIT;
			}
			break;

		/*
		 * case Level.GATEWAY_LEVEL: if (rawName.equals("gateway")) {
		 * //System.out.println("END GATEWAY_LEVEL " + rawName); level =
		 * Level.NODES; } break;
		 *//*
		 * case Level.INTERSECTION_LEVEL: if
		 * (rawName.equals("intersection")) { //System.out.println("END
		 * INTERSECTION_LEVEL " + rawName); level =
		 * Level.NODES; } break;
		 */
		case INTERSECTIONS:

			if (rawName.equals("intersectionDescriptions")) {
				// System.out.println("END INTERSECTIONS " +
				// rawName);
				level = Level.INIT;
			}
			break;

		case INTERSECTION:

			if (rawName.equals("intersection")) {
				level = Level.INTERSECTIONS;
			}
			break;

		case TRAFFIC_LIGHTS_SCHEDULE:
			//      if ( rawName.equals( "plan" ) ) {
			//        city.findNode( intersectionName ).addTrafficLightsPlan( trafficPlanName, schedule );
			//        schedule = new LinkedList<Phase>();
			//      }

			if (rawName.equals("trafficLightsSchedule")) {
				level = Level.INTERSECTION;
				city.findNode(intersectionName).addTrafficLightsPhases(
						phasesSet);
				phasesSet = null;

			}
			break;

		case PHASE_LEVEL:
			if (rawName.equals("phase")) {
				level = Level.TRAFFIC_LIGHTS_SCHEDULE;
				phasesSet.add(phase);
			}
			break;

		case TIMING_PLAN_LEVEL:
			if (rawName.equals("phase")) {
				timingPlan.add(phaseTiming);
			}

			if (rawName.equals("plan")) {
				level = Level.TRAFFIC_LIGHTS_SCHEDULE;
				((Intersection) city.findNode(intersectionName))
						.addTimingPlanFor(timingPlan, timingPlanName);
			}
			break;
		case ARM_ACTIONS:

			if (rawName.equals("armActions")) {
				// System.out.println("END ARM_ACTIONS_LEVEL " + rawName);
				level = Level.INTERSECTION;
				il = null;
				armFromName = null;

			}
			break;

		case ACTION:

			if (rawName.equals("action")) {
				try {
					for(Lane lane : lanes){
						Lane priorities[] = getPriorityLanesForLane(lane, ll);
						lane.addAction(ol, priorities);
					}
				} catch (UnsupportedLinkOperationException e) {
					e.printStackTrace();
				} catch (InvalidActionException e) {
					e.printStackTrace();
				}

				// //System.out.println("rozmiarek = "+ll.size());
				ol = null;
				ll = new LinkedList<Lane>();
				// System.out.println("END ACTION " + rawName);
				level = Level.ARM_ACTIONS;
			}
			break;

		case ACTIONRULE:

			if (rawName.equals("rule")) {
				// System.out.println("END ACTIONRULE_LEVEL " + rawName);
				level = Level.ACTION;
			}
			break;

		default:
			// System.out.println("END RoadNet ");
			break;
		}
	} // endElement(String)
	
	/**
	 * This method creates an array of priority lanes for the given lane - only within current
	 * link. The list includes those that are more on right.
	 * @param lane a lane to produce a priority array for
	 * @param outerPriorities list of priorities from other links
	 * @return priority lanes array for the given lane
	 */
	private Lane[] getPriorityLanesForLane(Lane lane, LinkedList<Lane> outerPriorities) {
		Lane[] result = null;
		Link parent = lane.getOwner();
		int laneNo = lane.getAbsoluteNumber();
		int leftmostMainLane = parent.leftLaneCount();
		int rightmostMainLane = parent.leftLaneCount() + parent.mainLaneCount() - 1;
		
		// if the given lane is among main lanes ...
		if ((laneNo >= rightmostMainLane) && (laneNo <= leftmostMainLane) ){
			// ... then we shall add some main lanes to the list.
			
			for (int i = rightmostMainLane; i > laneNo; i--){
				ll.addLast(parent.getLaneAbs(i));
			}
		}
		// now, convert it into an array
		result = ll.toArray(new Lane[0]);
		return result;
	}

	/** End document. */
	@Override
	public void endDocument() {
		// Do nothing...
	} // endDocument()

	/** Warning. */
	@Override
	public void warning(SAXParseException ex) {
		logger.error("[Warning] " + getLocationString(ex) + ": "
				+ ex.getMessage());
	}

	/** Error. */
	@Override
	public void error(SAXParseException ex) {
		logger.error("[Error] " + getLocationString(ex) + ": "
				+ ex.getMessage());
	}

	/** Fatal error. */
	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		logger.error("[Fatal Error] " + getLocationString(ex) + ": "
				+ ex.getMessage());
		throw ex;
	}

	/** Returns a string of the location. */
	private String getLocationString(SAXParseException ex) {
		StringBuffer str = new StringBuffer();

		String systemId = ex.getSystemId();
		if (systemId != null) {
			int index = systemId.lastIndexOf('/');
			if (index != -1) {
				systemId = systemId.substring(index + 1);
			}
			str.append(systemId);
		}
		str.append(':');
		str.append(ex.getLineNumber());
		str.append(':');
		str.append(ex.getColumnNumber());

		return str.toString();
	} // getLocationString(SAXParseException):String

	public Core getCore() {
		return core;
	}
	// TODO: log wielopoziomowy

}
