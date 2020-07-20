package input;

import core.DTNHost;
import core.World;
import routing.ActiveRouter;

public class ReduceEnergyEvent extends ExternalEvent {
	int hostAddress;
	double consumedEnergy;
	
	public ReduceEnergyEvent(double time,int hostAddress, double consumedEnergy) {
		super(time);
		this.hostAddress = hostAddress;
		this.consumedEnergy = consumedEnergy;
		return;
	}
	
	@Override
	public void processEvent(World world) {
		DTNHost host = world.getNodeByAddress(this.hostAddress);
		ActiveRouter router = (ActiveRouter) host.getRouter();
		router.ConsumeEnergy(this.consumedEnergy);
	}

}
