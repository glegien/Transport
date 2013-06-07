package pl.edu.agh.cs.kraksim.iface.carinfo;

import pl.edu.agh.cs.kraksim.core.InvalidClassSetDefException;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleView;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.core.UnsatisfiedContractException;

public class CarInfoIView extends ModuleView<NULL, NULL, NULL, NULL, NULL, LaneCarInfoIface>
{

  public CarInfoIView(Module module)
      throws InvalidClassSetDefException,
      UnsatisfiedContractException
  {
    super( module );
  }
}
