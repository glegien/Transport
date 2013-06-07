package pl.edu.agh.cs.kraksim.stat;

import pl.edu.agh.cs.kraksim.core.ExtensionCreationException;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleCreator;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.iface.mon.MonIView;

public class StatModuleCreator extends
  ModuleCreator<NULL, NULL, NULL, NULL, LinkStatExt, NULL>
{

  private MonIView monView;

  public StatModuleCreator(MonIView monView) {
    this.monView = monView;
  }

  @Override
  public void setModule(Module module)
  {}

  @Override
  public LinkStatExt createLinkExtension(Link link) throws ExtensionCreationException
  {
    return new LinkStatExt( link, monView );
  }

}
