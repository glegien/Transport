/**
 * 
 */
package pl.edu.agh.cs.kraksim.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * @author Bartosz Rybacki
 *
 */
public class CityMapGenerator
{

  private final static String X_JUNCTION_TEMPLATE = "model-x.xml";
  private final static String T_JUNCTION_TEMPLATE = "model-t.xml";

  //  private final static String X_LIGHTS_TEMPLATE   = "model-x-tl.xml";
  //  private final static String T_LIGHTS_TEMPLATE   = "model-t-tl.xml";

  /**
   * @param args
   */
  public static void main(String[] args) {
    //    try {
    //      PrintWriter out = new PrintWriter( new File( "c:/temp/newmodel.xml" ) );
    PrintWriter out = new PrintWriter( System.out );
    try {
      //        String model = genXJunction( "E2", "N3", "S3", "E3", "X2" );
      //        out.println( model );

      String model = genXJunction( "X2", "N2", "S2", "E2", "X1" );
      //        out.println( model );
      //        String model = genTJunction( "Isection", "N", "S", "E" );
      model = genTJunction( "X5", "X8", "X1", "G6" );
      out.println( model );
      out.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    //      try {
    //        String model = genTJunction( "Isection", "N", "S", "E" );
    //        System.out.println( model );
    //      }
    //      catch (IOException e) {
    //        e.printStackTrace();
    //      }
    //    }
    //    catch (FileNotFoundException e1) {
    //      // TODO Auto-generated catch block
    //      e1.printStackTrace();
    //    }
  }

  private static String genXJunction(String name,
      String north,
      String south,
      String east,
      String west) throws IOException
  {
    InputStream is = CityMapGenerator.class.getResourceAsStream( X_JUNCTION_TEMPLATE );
    if ( is == null ) {
      throw new FileNotFoundException( "" );
    }
    String model = IOHelper.readStream( is );
    model = model.replaceAll( "X1", name );
    model = model.replaceAll( "Nroad", north );
    model = model.replaceAll( "Sroad", south );
    model = model.replaceAll( "Wroad", west );
    model = model.replaceAll( "Eroad", east );

    return model;
  }

  private static String genTJunction(String name, String north, String south, String east)
      throws IOException
  {

    InputStream is = CityMapGenerator.class.getResourceAsStream( T_JUNCTION_TEMPLATE );
    if ( is == null ) {
      throw new FileNotFoundException( "" );
    }
    String model = IOHelper.readStream( is );
    model = model.replaceAll( "X1", name );
    model = model.replaceAll( "Nroad", north );
    model = model.replaceAll( "Sroad", south );
    model = model.replaceAll( "Eroad", east );

    return model;
  }

}
