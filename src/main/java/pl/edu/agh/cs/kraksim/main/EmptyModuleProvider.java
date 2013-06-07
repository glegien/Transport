/**
 * 
 */
package pl.edu.agh.cs.kraksim.main;

import java.util.Iterator;

import pl.edu.agh.cs.kraksim.core.Core;
import pl.edu.agh.cs.kraksim.core.InvalidClassSetDefException;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleCreationException;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;
import pl.edu.agh.cs.kraksim.iface.mon.MonIView;

/**
 * @author SG0891861
 *
 */
public class EmptyModuleProvider implements EvalModuleProvider
{
  private String name;

  public EmptyModuleProvider(String name) {
    this.name = name;
  }

  /* (non-Javadoc)
   * @see pl.edu.agh.cs.kraksim.main.EvalModuleProvider#getAlgorithmCode()
   */
  public String getAlgorithmCode() {
    return name;
  }

  /* (non-Javadoc)
   * @see pl.edu.agh.cs.kraksim.main.EvalModuleProvider#getAlgorithmName()
   */
  public String getAlgorithmName() {
    return name;
  }

  /* (non-Javadoc)
   * @see pl.edu.agh.cs.kraksim.main.EvalModuleProvider#getParamsDescription()
   */
  public Iterator<KeyValPair> getParamsDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see pl.edu.agh.cs.kraksim.main.EvalModuleProvider#provideNew(java.lang.String, pl.edu.agh.cs.kraksim.core.Core, pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView, pl.edu.agh.cs.kraksim.iface.mon.MonIView, pl.edu.agh.cs.kraksim.iface.block.BlockIView, int, int)
   */
  public Module provideNew(String name,
      Core core,
      CarInfoIView carInfoView,
      MonIView monView,
      BlockIView blockView,
      int carStartDelay,
      int carMaxVelocity) throws InvalidClassSetDefException, ModuleCreationException
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see pl.edu.agh.cs.kraksim.main.EvalModuleProvider#setParam(java.lang.String, java.lang.String)
   */
  public void setParam(String key, String val) throws AlgorithmConfigurationException {
  // TODO Auto-generated method stub

  }

}
