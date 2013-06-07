package pl.edu.agh.cs.kraksim.iface.block;

import pl.edu.agh.cs.kraksim.core.InvalidClassSetDefException;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleView;
import pl.edu.agh.cs.kraksim.core.UnsatisfiedContractException;

/* 
 * Interfaces for gateway and intersection do not define any methods other than in
 * NodeBlockIface. Hence, general NodeBlockIface is used for these two types of nodes.  
 */
public class BlockIView
  extends
  ModuleView<CityBlockIface, NodeBlockIface, NodeBlockIface, NodeBlockIface, LinkBlockIface, LaneBlockIface>
{

  public BlockIView(Module module)
      throws InvalidClassSetDefException,
      UnsatisfiedContractException
  {
    super( module );
  }
}
