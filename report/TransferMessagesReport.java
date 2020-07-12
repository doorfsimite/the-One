package report;

import java.util.ArrayList;
import java.util.List;

import core.ConnectionListener;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.UpdateListener;

public class TransferMessagesReport extends Report implements MessageListener, UpdateListener{

	int [] transfered;
	int [] receved;
	int [] directDelivery;
	int [] directReceved;
	int totalMessages = 0;
	public TransferMessagesReport() {
		super.init();
	}
	
	public void updated(List<DTNHost> hosts) {
		if (transfered == null) {
			transfered = new int[hosts.size()];
			receved = new int[hosts.size()];
			directDelivery = new int[hosts.size()];
			directReceved = new int[hosts.size()];
		}
	}

	public void newMessage(Message m) {
		this.totalMessages +=1;
	}
	
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {}
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {}
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {}
	public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
		transfered[from.getAddress()] += 1;
		receved[to.getAddress()] += 1 ;
		if(firstDelivery == true) {
			directDelivery[from.getAddress()] += 1;
			directReceved[to.getAddress()] += 1;
		}
	}
	
	public void done() {
		write("Number of messages: " + this.totalMessages);
		write("Host  receved_Messages Transfered_Messages  receved_messages(destiny) direct_Delivery");
		for(int i = 0 ; i < transfered.length;i++) {
			write(i + " " + receved[i]+" "+transfered[i]+" "+directReceved[i]+" "+directDelivery[i]);
		}
		super.done();
	}
}
