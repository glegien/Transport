package pl.edu.agh.cs.kraksim.ministat;

import pl.edu.agh.cs.kraksim.AssumptionNotSatisfiedException;
import pl.edu.agh.cs.kraksim.KraksimException;
import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.ExtensionCreationException;
import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleCreator;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.mon.MonIView;

public class MiniStatModuleCreator extends
  ModuleCreator<CityMiniStatExt, NULL, GatewayMiniStatExt, NULL, LinkMiniStatExt, NULL>
{

  private MonIView      monView;
  private Clock         clock;
  private StatHelper    helper;
  private MiniStatEView ev;

  public MiniStatModuleCreator(MonIView monView, Clock clock) {
    this.monView = monView;
    this.clock = clock;

    helper = new StatHelper();
  }

  @Override
  public void setModule(Module module)
  {
    try {
      ev = new MiniStatEView( module );
    }
    catch (KraksimException e) {
      throw new AssumptionNotSatisfiedException( e );
    }
  }

  @Override
  public CityMiniStatExt createCityExtension(City city) throws ExtensionCreationException
  {
    return new CityMiniStatExt( city, ev, helper );
  }

  @Override
  public GatewayMiniStatExt createGatewayExtension(Gateway gateway)
      throws ExtensionCreationException
  {
    return new GatewayMiniStatExt( gateway, monView, clock, helper );
  }

  @Override
  public LinkMiniStatExt createLinkExtension(Link link) throws ExtensionCreationException
  {
    return new LinkMiniStatExt( link, monView, clock, helper );
  }
}
