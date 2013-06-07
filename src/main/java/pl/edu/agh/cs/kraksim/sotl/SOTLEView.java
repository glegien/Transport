package pl.edu.agh.cs.kraksim.sotl;

import pl.edu.agh.cs.kraksim.core.InvalidClassSetDefException;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleView;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.core.UnsatisfiedContractException;

class SOTLEView extends ModuleView<CitySOTLExt, NULL, NULL, NULL, NULL, LaneSOTLExt>
{
  SOTLEView(Module module) throws InvalidClassSetDefException, UnsatisfiedContractException {
    super( module );
  }
}
