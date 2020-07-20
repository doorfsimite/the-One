package report;


import core.ActiveListener;
import core.DTNHost;

public class ActiveReport extends Report implements ActiveListener{
	
	
	public ActiveReport() {
		super.init();
	}

	public void active(DTNHost host) {
			write(getSimTime() + " " + "ACTIVE" + " " + host.getAddress());
		}
		
		public void deactive(DTNHost host){
			write(getSimTime() + " " + "DEACTIVE" + " " + host.getAddress());
		}

		@Override
		public void done() {
			super.done();
		}
}
