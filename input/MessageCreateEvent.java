/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import core.DTNHost;
import core.Message;
import core.NetworkInterface;
import core.World;

/**
 * External event for creating a message.
 */
public class MessageCreateEvent extends MessageEvent {
	private int size;
	private int responseSize;
	private boolean multicast;
	static ArrayList<Integer> statictoHosts;
	private ArrayList<Integer> toHosts;
	static Random rnd;
	
	/**
	 * Creates a message creation event with a optional response request
	 * @param from The creator of the message
	 * @param to Where the message is destined to
	 * @param id ID of the message
	 * @param size Size of the message
	 * @param responseSize Size of the requested response message or 0 if
	 * no response is requested
	 * @param time Time, when the message is created
	 */
	public MessageCreateEvent(int from, int to, String id, int size,
			int responseSize, double time) {
		super(from,to, id, time);
		this.size = size;
		this.responseSize = responseSize;
		this.multicast = false;
	}

	
	public static void setMultiplexSettings(Random rand, ArrayList<Integer> th) {
		rnd = rand;
		statictoHosts = th;
	}
	
	public MessageCreateEvent(int from, int to, String id, int size,
			int responseSize, double time,boolean multicast, ArrayList<Integer> destinyHosts) {
		super(drawFromHostAddress(),drawToHostAddress(destinyHosts), id, time);
		if(statictoHosts == null) {
			toHosts = destinyHosts;
			rnd = new Random();
		}else {
			toHosts = statictoHosts;
		}
		this.size = size;
		this.responseSize = responseSize;
		this.multicast = true;
	}

	//ASSUMI QUE SEMPRE AVERA ALGUEM LIGADO ENTRE 9 E 21 HORAS
	static int drawFromHostAddress() {
		DTNHost sortedFrom = NetworkInterface.activeNodes.get(rnd.nextInt(NetworkInterface.activeNodes.size()));//Qualquer no ativo 
		return sortedFrom.getAddress();			
	}
	
	static int drawToHostAddress(ArrayList<Integer> destinyHosts) {
		List<Integer> activeToHosts = new ArrayList<Integer>();
		for(int i =0 ; i< NetworkInterface.activeNodes.size(); i ++) {
			activeToHosts.add(NetworkInterface.activeNodes.get(i).getAddress());
		}
		activeToHosts.retainAll(destinyHosts);
		
		return activeToHosts.get(rnd.nextInt(activeToHosts.size()));
	}
	
	
	/**
	 * Creates the message this event represents. 
	 */
	@Override
	public void processEvent(World world) {
		DTNHost to = world.getNodeByAddress(this.toAddr);
		DTNHost from = world.getNodeByAddress(this.fromAddr);			
		
		Message m;
		if(this.multicast) {
			
			ArrayList<DTNHost> mToList = new ArrayList<DTNHost>();
			for (int address : this.toHosts) {
				mToList.add(world.getNodeByAddress(address));
			}
			m = new Message(from, to, this.id, this.size,mToList);	
			System.out.println("setou multicast pra mensagem: "+m.getId()+"\n"+m.getToList()+"\n\n");

		}
		else {
			m = new Message(from, to, this.id, this.size,null);
		}
		m.setResponseSize(this.responseSize);
		from.createNewMessage(m);
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + fromAddr + "->" + toAddr + "] " +
		"size:" + size + " CREATE";
	}
}
