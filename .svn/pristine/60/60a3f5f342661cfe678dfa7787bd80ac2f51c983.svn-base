package pl.edu.agh.cs.kraksim.visual;

import java.awt.geom.Point2D;

public final class GeometryHelper
{

  private GeometryHelper() {}

  /**
   * oblicza pare wektorow
   * 
   * @param start
   * @param end
   * @return tablice czterech double
   *         <ul>
   *         <li>pierwsze 2 to wspolrzedne wektora jednostkowego o zwrocie z
   *         punktu <code> start </code> do <code> end </code> i rownoleglego
   *         do prostej na ktorej leza punkty
   *         <li>kolejne 2 to wspolrzedne wektora jednostkowego prostopadlego
   *         do pierwszego wyznaczajacego kierunek i zwrot przesuwania
   *         kolejnych pasow od osi jezdni
   *         </ul>
   */
  public static double[] computeVectors(final Point2D start, final Point2D end) {
    final double deltaX = end.getX() - start.getX();
    final double deltaY = end.getY() - start.getY();
    final double distance = start.distance( end );
    // WEKTORY
    // uklad wsp jak w Graphics czyli gorny lewy rog ma wsp (0,0)
    // a prawy dolny ma wsp (width,height)

    // do przeuwania pasow od osi jezdni
    //double abs_dx = Math.abs(dx);
    //double[] downVec = new double[] { -dy / distance, abs_dx / distance };
    //double[] upperVec = new double[] { dy / distance, -abs_dx / distance };

    // wektro prostopadly do osi jezdni
    //double[] vectorOrtogonal;
    // wektor jednostkowy wyznaczający kierunek i zwrot ze start do end
    // do przesuwania pasow poza obszar skrzyzowania
    final double[] vectorAB = new double[] {
      deltaX / distance, deltaY / distance };

    // [START]Wyznaczenie wektora vectorOrtogonal
    /*
     Boolean startLowerThanEnd = null;
     Boolean startNearThanEnd = null;

     if (start.getY() > end.getY())
     startLowerThanEnd = true;
     else if (start.getY() < end.getY())
     startLowerThanEnd = false;

     if (start.getX() < end.getX())
     startNearThanEnd = true;
     else if (start.getX() > end.getX())
     startNearThanEnd = false;

     if (startNearThanEnd == null) {
     if (startLowerThanEnd == null)
     throw new IllegalArgumentException(
     "Poczatek i koniec drogi to ten sam punkt !!!");

     if (startLowerThanEnd) {
     // przesuwamy pasy w prawo
     if (upperVec[0] > 0)
     vectorOrtogonal = upperVec;
     else
     vectorOrtogonal = downVec;
     } else {
     // przesuwamy pasy w lewo
     if (upperVec[0] < 0)
     vectorOrtogonal = upperVec;
     else
     vectorOrtogonal = downVec;

     }

     } else {
     if (startNearThanEnd)
     vectorOrtogonal = downVec;
     else
     vectorOrtogonal = upperVec;
     }
     // [END]Wyznaczenie wektorow vectorOrtogonal i vectorAB
     * 
     */
    return new double[] {
      vectorAB[0], vectorAB[1] };

  }

}
