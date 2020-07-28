/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import core.DTNHost;
import core.Message;
import core.NetworkInterface;
import core.SimClock;
import core.SimScenario;
import core.World;

/**
 * External event for creating a message.
 */
public class MulticastMessageCreateEvent extends  ExternalEvent{
	private int size;
	private int responseSize;
	private String id;
	public static ArrayList<Integer> toHosts;
	public static Random rnd;
	
	protected int drawFromHostAddress() {
		if(NetworkInterface.activeNodes.size() > 0) {
			DTNHost sortedFrom = NetworkInterface.activeNodes.get(rnd.nextInt(NetworkInterface.activeNodes.size()));//Qualquer no ativo 
			return sortedFrom.getAddress();			
		}
		return -1;	
	}
	
	protected int drawToHostAddress() {
		if(NetworkInterface.activeNodes.size() > 0) {
			List<Integer> activeToHosts = new ArrayList<Integer>();
			for(int i =0 ; i< NetworkInterface.activeNodes.size(); i ++) {
				activeToHosts.add(NetworkInterface.activeNodes.get(i).getAddress());
			}
			activeToHosts.retainAll(toHosts);
			
			if(activeToHosts.size() > 0) {
				return activeToHosts.get(rnd.nextInt(activeToHosts.size()));
			}
		}
		return -1;
	}
	
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
	
	public MulticastMessageCreateEvent(ArrayList<Integer> destinyHosts, String id, int msgSize, int responseSize, double time) {
		super(time);
		if(toHosts == null) {
			toHosts = destinyHosts;
			rnd = new Random();
		}
		
		this.size = msgSize;
		this.responseSize = responseSize;
		this.id = id;
		
	}

	
	/**
	 * Creates the message this event represents. 
	 */
	@Override
	public void processEvent(World world) {
		int fromAddress = drawFromHostAddress();
		int toAddress = drawToHostAddress();
		if(fromAddress < 0 || toAddress < 0) {
			return;
		}else {
			DTNHost to = world.getNodeByAddress(toAddress);
			DTNHost from = world.getNodeByAddress(fromAddress);			
			
			Message m = new Message(from, to, this.id, this.size);
			m.setResponseSize(this.responseSize);
			from.createNewMessage(m);
	
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + drawFromHostAddress() + "->" + drawToHostAddress() + "] " +
		"size:" + size + " CREATE";
	}
}
