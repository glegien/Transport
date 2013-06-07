/**
 * 
 */
package pl.edu.agh.cs.kraksim.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class IOHelper
{

  private IOHelper() {}

  /**
   * Reads whole file to one String if possible.
   * @param file File to read from 
   * @return file contents as String
   * @throws IOException 
   */
  public static String readFile(final File file) throws IOException {
    final StringBuffer buff = new StringBuffer();

    BufferedReader in = new BufferedReader( new FileReader( file ) );
    String str;
    while ( (str = in.readLine()) != null ) {
      buff.append( str ).append( '\n' );
    }
    in.close();

    return buff.toString();
  }

  /**
   * Reads whole reader contents to one String if possible.
   * @param file File to read from 
   * @return file contents as String
   * @throws IOException 
   */
  public static String read(final Reader reader) throws IOException {
    StringBuffer sb = new StringBuffer();

    BufferedReader in = new BufferedReader( reader );
    String str;
    while ( (str = in.readLine()) != null ) {
      sb.append( str ).append( '\n' );
    }
    in.close();

    return sb.toString();

  }

  /**
   * Reads stream until it is closed.
   * @param file File to read from 
   * @return file contents as String
   * @throws FileNotFoundException if file is not found on search path
   */
  public static String readStream(final InputStream is) throws IOException {
    BufferedInputStream in = new BufferedInputStream( is );
    StringBuilder stringBuf = new StringBuilder();

    byte[] buf = new byte[1024];
    int len;
    while ( (len = in.read( buf )) > 0 ) {
      stringBuf.append( new String( buf, 0, len ) );
    }

    return stringBuf.toString();
  }

}
