package pl.edu.agh.cs.kraksim.ministat;

import pl.edu.agh.cs.kraksim.core.InvalidClassSetDefException;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleView;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.core.UnsatisfiedContractException;

public class MiniStatEView extends
  ModuleView<CityMiniStatExt, NULL, GatewayMiniStatExt, NULL, LinkMiniStatExt, NULL>
{

  public MiniStatEView(Module module)
      throws InvalidClassSetDefException,
      UnsatisfiedContractException
  {
    super( module );
  }
}
