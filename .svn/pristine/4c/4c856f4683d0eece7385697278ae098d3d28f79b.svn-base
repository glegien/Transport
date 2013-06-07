package pl.edu.agh.cs.kraksim.parser;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.traffic.Distribution;
import pl.edu.agh.cs.kraksim.traffic.NormalDistribution;
import pl.edu.agh.cs.kraksim.traffic.PointDistribution;
import pl.edu.agh.cs.kraksim.traffic.TravellingScheme;
import pl.edu.agh.cs.kraksim.traffic.UniformDistribution;

public class TrafficDataXmlHandler extends DefaultHandler
{

  private static final Logger logger        = Logger.getLogger( TrafficDataXmlHandler.class );

  public static final int     TRAFFIC_LEVEL = 0;
  public static final int     SCHEME_LEVEL  = 2;
  public static final int     GATEWAY_LEVEL = 3;

  private int                 level         = 0;

  City                        c;
  TravellingScheme            ts            = null;

  ArrayList<Gateway>          gateways;
  ArrayList<Distribution>     departureDists;
  ArrayList<TravellingScheme> schemes       = null;
  private int                 count;
  private Color               driverColor = null;

  public TrafficDataXmlHandler() {
    super();
  }

  public TrafficDataXmlHandler(City c) {
    super();
    this.c = c;
  }

  /** Start document. */
  public void startDocument() {
    //System.out.println("BEGIN DOCUMENT ");
    schemes = new ArrayList<TravellingScheme>();

  }

  /** Start element. */

  public void startElement(String namespaceURI,
      String localName,
      String rawName,
      Attributes attrs)
  {
    switch ( level )
    {
    case TrafficDataXmlHandler.GATEWAY_LEVEL:
      if ( rawName.equals( "point" ) ) {

        float y = Float.parseFloat( attrs.getValue( "y" ) );
        PointDistribution pd = new PointDistribution( y );
        departureDists.add( pd );
        //System.out.println("GATEWAY_LEVEL -> " + rawName);
      }
      if ( rawName.equals( "uniform" ) ) {
        float a = Float.parseFloat( attrs.getValue( "a" ) );
        float b = Float.parseFloat( attrs.getValue( "b" ) );
        UniformDistribution ud = new UniformDistribution( a, b );
        departureDists.add( ud );
        //System.out.println("GATEWAY_LEVEL -> " + rawName);
      }
      if ( rawName.equals( "normal" ) ) {
        float dev = Float.parseFloat( attrs.getValue( "dev" ) );
        float y = Float.parseFloat( attrs.getValue( "y" ) );
        NormalDistribution nd = new NormalDistribution( y, dev );
        departureDists.add( nd );
        //System.out.println("GATEWAY_LEVEL -> " + rawName);
      }
      break;

    case TrafficDataXmlHandler.SCHEME_LEVEL:
      if ( rawName.equals( "gateway" ) ) {
        String id = attrs.getValue( "id" );
        gateways.add( (Gateway) c.findNode( id ) );
        // check for errors
        //System.out.println("SCHEME_LEVEL -> " + rawName);
        level = TrafficDataXmlHandler.GATEWAY_LEVEL;
      }
      break;

    default:
      // jeszcze nie ustalony stan
      if ( rawName.equals( "scheme" ) ) {
        //System.out.println("BEGIN SCHEME_LEVEL " + rawName);
        gateways = new ArrayList<Gateway>();
        departureDists = new ArrayList<Distribution>();
        count = Integer.parseInt( attrs.getValue( "count" ) );
        driverColor = parseColor ( attrs.getValue("driver_color") );
        
        level = TrafficDataXmlHandler.SCHEME_LEVEL;
      }
      if ( rawName.equals( "traffic" ) ) {
        //System.out.println("BEGIN traffic: " + rawName);
        // level = TrafficDataXmlHandler.SCHEME_LEVEL;
        level = 0;
      }
      break;
    }

  }

  /**
   * Zamienia łańcuch znaków na Color.
   * Na razie obsługuje tylko format #RRGGBB.
   */
  private Color parseColor(String colorStr) {
    
      if (colorStr == null) {
          return null;
      }
      
      int r,g,b;
      try {
          if (colorStr.length() != 7 || !colorStr.startsWith("#")) {
              throw new RuntimeException();
          }
          r = Integer.parseInt(colorStr.substring(1, 3),16);
          g = Integer.parseInt(colorStr.substring(3, 5),16);
          b = Integer.parseInt(colorStr.substring(5, 7),16);
      }
      catch (Exception e) {
          String error = String.format("[ERROR] cannot parse string '%s' as color",colorStr);
          logger.error(error); 
          return null;
      }
      return new Color(r,g,b);
}

/** Ignorable whitespace. */
  public void ignorableWhitespace(char ch[], int start, int length) {
  // characters(ch, start, length);
  }

  /** Characters. */
  public void characters(char ch[], int start, int length) {} // characters(char[],int,int);

  /** End element. */
  public void endElement(String namespaceURI, String localName, String rawName) {
    switch ( level )
    {

    case TrafficDataXmlHandler.SCHEME_LEVEL:

      if ( rawName.equals( "scheme" ) ) {
        //System.out.println("END SCHEME_LEVEL " + rawName);
        Gateway[] gws = new Gateway[0];
        Distribution[] ds = new Distribution[0];

        gws = gateways.toArray( gws );
        ds = departureDists.toArray( ds );

        //System.out.println(ds.length +" === "+gws.length);
        ts = new TravellingScheme( count, gws, ds, driverColor );
        schemes.add( ts );
        level = 0;// traffic
      }
      break;

    case TrafficDataXmlHandler.GATEWAY_LEVEL:
      if ( rawName.equals( "gateway" ) ) {
        //System.out.println("END GATEWAY_LEVEL " + rawName);
        level = TrafficDataXmlHandler.SCHEME_LEVEL;
      }
      break;
    default:
      //System.out.println("END traffic ");
      break;
    }
  } // endElement(String)

  /** End document. */
  public void endDocument() {
  // Do nothing...
  } // endDocument()

  /** Warning. */
  public void warning(SAXParseException ex) {
    logger.error( "[Warning] " + getLocationString( ex ) + ": " + ex.getMessage() );
  }

  /** Error. */
  public void error(SAXParseException ex) {
    logger.error( "[Error] " + getLocationString( ex ) + ": " + ex.getMessage() );
  }

  /** Fatal error. */
  public void fatalError(SAXParseException ex) throws SAXException {
    logger.error( "[Fatal Error] " + getLocationString( ex ) + ": " + ex.getMessage() );
    throw ex;
  }

  /** Returns a string of the location. */
  private String getLocationString(SAXParseException ex) {
    StringBuffer str = new StringBuffer();

    String systemId = ex.getSystemId();
    if ( systemId != null ) {
      int index = systemId.lastIndexOf( '/' );
      if ( index != -1 ) systemId = systemId.substring( index + 1 );
      str.append( systemId );
    }
    str.append( ':' );
    str.append( ex.getLineNumber() );
    str.append( ':' );
    str.append( ex.getColumnNumber() );

    return str.toString();
  } // getLocationString(SAXParseException):String

  /*
   * ArrayList<TravellingScheme> getSchemes() { schemes.toArray(new
   * TravellingScheme[0]);
   * 
   * return schemes; }
   */
  public Collection<TravellingScheme> getSchemes() {
    return schemes;
  }

  // TravellingScheme getScheme() {
  // return ts;
  // }
  // TODO: log wielopoziomowy
  // TODO: levelUp
  // TODO: levelDown
  // TODO: checkLevel
  // TODO: setLevel
  // TODO: profilig
}
