package pl.edu.agh.cs.kraksim.rl;

import pl.edu.agh.cs.kraksim.core.InvalidClassSetDefException;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleView;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.core.UnsatisfiedContractException;

class RLEView extends ModuleView<CityRLExt, NULL, NULL, NULL, NULL, LaneRLExt>
{

  RLEView(Module module) throws InvalidClassSetDefException, UnsatisfiedContractException {
    super( module );
  }
}
