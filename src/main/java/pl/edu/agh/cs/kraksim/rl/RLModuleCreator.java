package pl.edu.agh.cs.kraksim.rl;

import pl.edu.agh.cs.kraksim.AssumptionNotSatisfiedException;
import pl.edu.agh.cs.kraksim.KraksimException;
import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.ExtensionCreationException;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleCreator;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;

public class RLModuleCreator extends
  ModuleCreator<CityRLExt, NULL, NULL, NULL, NULL, LaneRLExt>
{
  private CarInfoIView carInfoView;
  private BlockIView   blockView;
  private RLParams     params;

  private RLEView      ev;

  public RLModuleCreator(CarInfoIView carInfoView, BlockIView blockView, RLParams params) {
    this.carInfoView = carInfoView;
    this.blockView = blockView;
    this.params = params;
  }

  @Override
  public void setModule(Module module)
  {
    try {
      ev = new RLEView( module );
    }
    catch (KraksimException e) {
      throw new AssumptionNotSatisfiedException( e );
    }
  }

  @Override
  public CityRLExt createCityExtension(City city) throws ExtensionCreationException
  {
    return new CityRLExt( city, ev, params );
  }

  @Override
  public LaneRLExt createLaneExtension(Lane lane) throws ExtensionCreationException
  {
    return new LaneRLExt( lane, ev, carInfoView, blockView, params );
  }
}
