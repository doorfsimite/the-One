package input;

import core.DTNHost;
import core.Message;
import core.World;

public class ActiveEvent extends ExternalEvent {
	int hostAddress;
	boolean active;
	public ActiveEvent(double time,int h,boolean active) {
		super(time);
		this.hostAddress = h;
		this.active = active;
	}
	
	
	public void processEvent(World world) {
		if(active) {
			world.getNodeByAddress(this.hostAddress).getInterface(1).activate();
		}else {
			world.getNodeByAddress(this.hostAddress).getInterface(1).deActivate();
		}
	}
}
