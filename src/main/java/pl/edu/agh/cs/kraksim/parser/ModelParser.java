package pl.edu.agh.cs.kraksim.parser;

import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import pl.edu.agh.cs.kraksim.core.Core;

public class ModelParser
{

  public static Core parse(String fileName) throws IOException, ParsingException {
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp = spf.newSAXParser();
      RoadNetXmlHandler rnXmlhandler = new RoadNetXmlHandler();

      sp.parse( fileName, rnXmlhandler );

      return rnXmlhandler.getCore();

    }
    catch (Exception e) {
      throw new ParsingException( "Parsing exceptionr : ", e );
    }
  }
}
