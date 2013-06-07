package pl.edu.agh.cs.kraksim.parser;

import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.traffic.TravellingScheme;

public class TrafficSchemeParser
{

  public static Collection<TravellingScheme> parse(String fileName, City city)
      throws IOException,
      ParsingException
  {
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp = spf.newSAXParser();
      TrafficDataXmlHandler tdXmlhandler = new TrafficDataXmlHandler( city );

      sp.parse( fileName, tdXmlhandler );

      return tdXmlhandler.getSchemes();

    }
    catch (Exception e) {
      throw new ParsingException( "Parsing exception : ", e );
    }
  }
}
