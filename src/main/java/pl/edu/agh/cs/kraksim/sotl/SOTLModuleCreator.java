package pl.edu.agh.cs.kraksim.sotl;

import pl.edu.agh.cs.kraksim.AssumptionNotSatisfiedException;
import pl.edu.agh.cs.kraksim.KraksimException;
import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.ExtensionCreationException;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleCreator;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.iface.mon.MonIView;

public class SOTLModuleCreator extends
  ModuleCreator<CitySOTLExt, NULL, NULL, NULL, NULL, LaneSOTLExt>
{

  private MonIView   monView;

  private BlockIView blockView;

  private SOTLEView  ev;

  private SOTLParams params;

  public SOTLModuleCreator(MonIView monView, BlockIView blockView, SOTLParams params) {
    this.monView = monView;
    this.blockView = blockView;

    this.params = params;
  }

  @Override
  public void setModule(Module module)
  {
    try {
      ev = new SOTLEView( module );
    }
    catch (KraksimException e) {
      throw new AssumptionNotSatisfiedException( e );
    }
  }

  @Override
  public CitySOTLExt createCityExtension(City city) throws ExtensionCreationException
  {
    return new CitySOTLExt( city, ev );
  }

  @Override
  public LaneSOTLExt createLaneExtension(Lane lane) throws ExtensionCreationException
  {
    return new LaneSOTLExt( lane, monView, blockView, params );
  }
}
