package pl.edu.agh.cs.kraksim.dsyncdecision;

import pl.edu.agh.cs.kraksim.AssumptionNotSatisfiedException;
import pl.edu.agh.cs.kraksim.KraksimException;
import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.ExtensionCreationException;
import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleCreator;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.ministat.MiniStatEView;

public class DsyncDecisionModuleCreator
  extends
  ModuleCreator<CityDsyncDecisionExt, NULL, NULL, IntersectionDsyncDecisionExt, NULL, NULL>
{

  //  private final EvalIView    evalView;
  private final MiniStatEView statView;
  private final BlockIView    blockView;

  private DSyncDecisionEView  ev;
  Clock                       clock;
  private final int           transitionDuration;
  private boolean             dynamic = false;

  public DsyncDecisionModuleCreator(
  //      EvalIView evalView,
      MiniStatEView monView,
      BlockIView blockView,
      Clock clock,
      int transitionDuration,
      boolean dynamic)
  {
    //    this.evalView = evalView;
    this.blockView = blockView;
    this.statView = monView;

    this.clock = clock;
    this.transitionDuration = transitionDuration;
    this.dynamic = dynamic;
  }

  @Override
  public void setModule(Module module) {
    try {
      ev = new DSyncDecisionEView( module );
    }
    catch (KraksimException e) {
      throw new AssumptionNotSatisfiedException( e );
    }
  }

  @Override
  public CityDsyncDecisionExt createCityExtension(City city)
      throws ExtensionCreationException
  {
    return new CityDsyncDecisionExt( city, clock, ev, statView, dynamic );
  }

  @Override
  public IntersectionDsyncDecisionExt createIntersectionExtension(Intersection intersection)
      throws ExtensionCreationException
  {
    return new IntersectionDsyncDecisionExt(
        intersection, blockView, clock, transitionDuration );
  }
}
