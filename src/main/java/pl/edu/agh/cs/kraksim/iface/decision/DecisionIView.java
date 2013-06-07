package pl.edu.agh.cs.kraksim.iface.decision;

import pl.edu.agh.cs.kraksim.core.InvalidClassSetDefException;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleView;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.core.UnsatisfiedContractException;

public class DecisionIView extends ModuleView<CityDecisionIface, NULL, NULL, NULL, NULL, NULL>
{

  public DecisionIView(Module module)
      throws InvalidClassSetDefException,
      UnsatisfiedContractException
  {
    super( module );
  }
}
