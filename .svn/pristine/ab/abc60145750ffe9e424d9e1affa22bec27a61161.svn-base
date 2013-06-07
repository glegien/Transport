package pl.edu.agh.cs.kraksim.main;

import java.util.Iterator;

import pl.edu.agh.cs.kraksim.core.Core;
import pl.edu.agh.cs.kraksim.core.InvalidClassSetDefException;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleCreationException;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;
import pl.edu.agh.cs.kraksim.iface.mon.MonIView;
import pl.edu.agh.cs.kraksim.rl.RLModuleCreator;
import pl.edu.agh.cs.kraksim.rl.RLParams;
import pl.edu.agh.cs.kraksim.util.ArrayIterator;

class RLModuleProvider implements EvalModuleProvider
{

  final static float          DEFAULT_DISCOUNT     = 0.9f;
  final static int            DEFAULT_HALVE_PERIOD = 60;

  private float               discount             = DEFAULT_DISCOUNT;
  private int                 halvePeriod          = DEFAULT_HALVE_PERIOD;

  private static KeyValPair[] paramDesc            = new KeyValPair[] {
    new KeyValPair( "discount", "discount factor - gamma in RL equations (default: "
                                + DEFAULT_DISCOUNT + ")" ),
    new KeyValPair(
        "halve",
        "how often halving RL counters takes place; nonpositive value - never halve (default: "
            + DEFAULT_HALVE_PERIOD + ")" )        };

  public String getAlgorithmCode() {
    return "rl";
  }

  public String getAlgorithmName() {
    return "Reinforcement Learning";
  }

  public Iterator<KeyValPair> getParamsDescription() {
    return new ArrayIterator<KeyValPair>( paramDesc );
  }

  public void setParam(String key, String val) throws AlgorithmConfigurationException {
    if ( key.equals( "discount" ) ) {
      try {
        float d = Float.parseFloat( val );
        if ( d <= 0.0f || d >= 1.0f ) throw new NumberFormatException();
        discount = d;
      }
      catch (NumberFormatException e) {
        throw new AlgorithmConfigurationException( "discount must be a float in range (0,1)" );
      }
    }
    else if ( key.equals( "halve" ) ) {
      try {
        halvePeriod = Integer.parseInt( val );
      }
      catch (NumberFormatException e) {
        throw new AlgorithmConfigurationException( "halve period must be an integer" );
      }
    }
    else throw new AlgorithmConfigurationException( "invalid algorithm parameter -- " + key );
  }

  public Module provideNew(String name,
      Core core,
      CarInfoIView carInfoView,
      MonIView monView,
      BlockIView blockView,
      int carStartDelay,
      int carMaxVelocity) throws InvalidClassSetDefException, ModuleCreationException
  {
    return core.newModule( name, new RLModuleCreator( carInfoView, blockView, new RLParams(
        discount, halvePeriod, carStartDelay, carMaxVelocity ) ) );
  }
}
