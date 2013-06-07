package pl.edu.agh.cs.kraksim.iface.carcounter;

import pl.edu.agh.cs.kraksim.core.InvalidClassSetDefException;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleView;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.core.UnsatisfiedContractException;

public class CarCounterIView extends
  ModuleView<NULL, NULL, NULL, NULL, LinkCarCounterIface, NULL>
{

  public CarCounterIView(Module module)
      throws InvalidClassSetDefException,
      UnsatisfiedContractException
  {
    super( module );
  }
}
