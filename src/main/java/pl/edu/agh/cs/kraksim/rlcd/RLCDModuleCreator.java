package pl.edu.agh.cs.kraksim.rlcd;

import pl.edu.agh.cs.kraksim.AssumptionNotSatisfiedException;
import pl.edu.agh.cs.kraksim.KraksimException;
import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.ExtensionCreationException;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Module;
import pl.edu.agh.cs.kraksim.core.ModuleCreator;
import pl.edu.agh.cs.kraksim.core.NULL;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;

public class RLCDModuleCreator extends
	ModuleCreator<CityRLCDExt, NULL, NULL, NULL, NULL, LaneRLCDExt>{

	private CarInfoIView carInfoView;
	private BlockIView   blockView;
	private RLCDParams     params;

	private RLCDEView      ev;

	public RLCDModuleCreator(CarInfoIView carInfoView, BlockIView blockView,
			RLCDParams params) {
		this.carInfoView = carInfoView;
	    this.blockView = blockView;
	    this.params = params;
	}

	@Override
	public void setModule(Module module) {
		try {
		      ev = new RLCDEView( module );
		    }
		    catch (KraksimException e) {
		      throw new AssumptionNotSatisfiedException( e );
		    }
	}
	
	@Override
	public CityRLCDExt createCityExtension(City city) throws ExtensionCreationException
	{
	  return new CityRLCDExt( city, ev, params );
	}
	
	@Override
	public LaneRLCDExt createLaneExtension(Lane lane) throws ExtensionCreationException
	{
	  return new LaneRLCDExt( lane, ev, carInfoView, blockView, params );
	}

}
