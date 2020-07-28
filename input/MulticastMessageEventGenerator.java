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

import core.Settings;
import core.SettingsError;
import core.SimScenario;
import core.DTNHost;
import core.NetworkInterface;
import core.SimClock;
/**
 * Message creation -external events generator. Creates uniformly distributed
 * message creation patterns whose message size and inter-message intervals can
 * be configured.
 */
public class MulticastMessageEventGenerator extends MessageEventGenerator{
	public static final String MULTICAST_DESTINY = "multcastDestinyProportion";

	private ArrayList<Integer> toHosts;
	
	private SimScenario scene;
	
	/**
	 * Constructor, initializes the interval between events, 
	 * and the size of messages generated, as well as number 
	 * of hosts in the network.
	 * @param s Settings for this generator.
	 */
	public MulticastMessageEventGenerator(Settings s){
		super(s);
		double toHostProportion = s.getDouble(MULTICAST_DESTINY);
		int toHostSize = (int)((toHostProportion/100.0)*((hostRange[1]-hostRange[0])+1));
		List<Integer> auxToHostList = new LinkedList<Integer>();
		for (int i = 0 ; i < ((hostRange[1]-hostRange[0])+1); i++) {
			if(i != 458) {
				auxToHostList.add(i);
			}
		}
		toHosts = new ArrayList<Integer>();
		for (int i = 0 ; i < toHostSize; i++) {
			int index = rng.nextInt(auxToHostList.size()-1);
			toHosts.add(auxToHostList.remove(index));
		}
//		System.out.println(toHosts);
		MessageCreateEvent.setMultiplexSettings(rng,toHosts);
	}
	
	

	
	/**
	 * Generates a (random) message size
	 * @return message size
	 */
	protected int drawMessageSize() {
		int sizeDiff = sizeRange[0] == sizeRange[1] ? 0 : 
			rng.nextInt(sizeRange[1] - sizeRange[0]);
		return sizeRange[0] + sizeDiff;
	}
	
	/**
	 * Generates a (random) time difference between two events
	 * @return the time difference
	 */
	protected int drawNextEventTimeDiff() {
		int timeDiff = msgInterval[0] == msgInterval[1] ? 0 : 
			rng.nextInt(msgInterval[1] - msgInterval[0]);
		return msgInterval[0] + timeDiff;
	}
	

	
	/** 
	 * Returns the next message creation event
	 * @see input.EventQueue#nextEvent()
	 */
	public ExternalEvent nextEvent() {
		int responseSize = 0; /* zero stands for one way messages */
		int msgSize;
		int interval;
		
		
		msgSize = drawMessageSize();
		interval = drawNextEventTimeDiff();

		/**from e to hosts devem ser definidos em tempo de execucao pois podem estar desativados**/
		MessageCreateEvent mmce = new MessageCreateEvent(-1,-1,this.getID(),msgSize,responseSize,this.nextEventsTime,true,toHosts);
		
		/* Create event and advance to next event */
//		MessageCreateEvent mce = new MessageCreateEvent(from, to, this.getID(), msgSize, responseSize, this.nextEventsTime);
		
		this.nextEventsTime += interval;	
		
		if (this.msgTime != null && this.nextEventsTime > this.msgTime[1]) {
			/* next event would be later than the end time */
			this.nextEventsTime = Double.MAX_VALUE;
		}
		
		return mmce;
	}

	/**
	 * Returns next message creation event's time
	 * @see input.EventQueue#nextEventsTime()
	 */
	public double nextEventsTime() {
		return this.nextEventsTime;
	}
	
	/**
	 * Returns a next free message ID
	 * @return next globally unique message ID
	 */
	protected String getID(){
		this.id++;
		return idPrefix + this.id;
	}	
}
