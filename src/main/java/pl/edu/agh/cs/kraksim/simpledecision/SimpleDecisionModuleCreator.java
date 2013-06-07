package pl.edu.agh.cs.kraksim.simpledecision;

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
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;
import pl.edu.agh.cs.kraksim.iface.eval.EvalIView;
import pl.edu.agh.cs.kraksim.ministat.MiniStatEView;
import pl.edu.agh.cs.kraksim.routing.ITimeTable;
import pl.edu.agh.cs.kraksim.weka.WekaPredictionModuleHandler;

public class SimpleDecisionModuleCreator extends
//  ModuleCreator<CitySimpleDecisionExt, NULL, NULL, IntersectionSimpleDecisionExt, NULL, NULL>
ModuleCreator<CitySimpleDecisionExt, NULL, NULL, IntersectionSimpleDecisionExt, NULL, NULL>
{

  private final EvalIView     evalView;

  private final BlockIView    blockView;
  
  private final CarInfoIView carInfoView;
  
  private final MiniStatEView statView;
  
  private final ITimeTable timeTable;

  private SimpleDecisionEView ev;
  
  private WekaPredictionModuleHandler wekaPredictionHandler;

  private Clock                       clock;

  private final int           transitionDuration;


  public SimpleDecisionModuleCreator(
      EvalIView evalView,
      BlockIView blockView,
      CarInfoIView carInfoView,
      MiniStatEView statView,
      ITimeTable timeTable,
      WekaPredictionModuleHandler wekaPredictionHandler, 
      Clock clock,
      int transitionDuration)
  {
    this.evalView = evalView;
    this.blockView = blockView;
    this.carInfoView = carInfoView;
    this.statView = statView;
    this.timeTable = timeTable;
	this.wekaPredictionHandler = wekaPredictionHandler;
    this.clock = clock;
    this.transitionDuration = transitionDuration;
  }

  @Override
  public void setModule(Module module)
  {
    try {
      ev = new SimpleDecisionEView( module );
    }
    catch (KraksimException e) {
      throw new AssumptionNotSatisfiedException( e );
    }
  }

  @Override
  public CitySimpleDecisionExt createCityExtension(City city)
      throws ExtensionCreationException
  {
    return new CitySimpleDecisionExt( city, ev );
  }

  @Override
  public IntersectionSimpleDecisionExt createIntersectionExtension(Intersection intersection)
      throws ExtensionCreationException
  {
    return new IntersectionSimpleDecisionExt(
        intersection, evalView, blockView, carInfoView, statView, wekaPredictionHandler, this.timeTable, clock, transitionDuration );
  }
}
