package pl.edu.agh.cs.kraksim.stat;

import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.carcounter.LinkCarCounterIface;
import pl.edu.agh.cs.kraksim.iface.mon.CarDriveHandler;
import pl.edu.agh.cs.kraksim.iface.mon.LinkMonIface;
import pl.edu.agh.cs.kraksim.iface.mon.MonIView;

class LinkStatExt implements LinkCarCounterIface
{

  private int carCount;

  LinkStatExt(final Link link, MonIView monView) {
    carCount = 0;

    LinkMonIface l = monView.ext( link );
    l.installInductionLoops( 0, new CarDriveHandler() {

      public void handleCarDrive(int velocity, Object driver) {
        carCount++;
        StatCollector.getInstance().addLinkEnterStatistic(
            link.getId(), driver.toString(), velocity );

        //System.out.print(velocity);
        //link.
        //driver do costam, zapisz informacje
      }
    } );
    l.installInductionLoops( link.getLength(), new CarDriveHandler() {

      public void handleCarDrive(int velocity, Object driver) {
        carCount--;
        StatCollector.getInstance().addLinkLeaveStatistic(
            link.getId(), driver.toString(), velocity );
        //System.out.print(velocity);
        //driver do costam, zapisz informacje
      }
    } );
  }

  public int getCarCount() {
    return carCount;
  }
}
