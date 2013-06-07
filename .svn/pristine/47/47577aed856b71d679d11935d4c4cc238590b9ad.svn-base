package pl.edu.agh.cs.kraksim.sotl;

import java.util.Iterator;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.eval.CityEvalIface;

class CitySOTLExt implements CityEvalIface
{

  private final City      city;

  private final SOTLEView ev;

  CitySOTLExt(City city, SOTLEView ev) {
    this.city = city;
    this.ev = ev;
  }

  public void turnEnded() {
    for (Iterator<Link> linkIter = city.linkIterator(); linkIter.hasNext();) {
      Link link = linkIter.next();
      for (Iterator<Lane> laneIter = link.laneIterator(); laneIter.hasNext();) {
        Lane lane = laneIter.next();
        ev.ext( lane ).turnEnded();
      }
    }
  }

}
