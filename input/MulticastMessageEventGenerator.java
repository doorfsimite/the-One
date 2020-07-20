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
public class MulticastMessageEventGenerator implements EventQueue {
	/** Message size range -setting id ({@value}). Can be either a single
	 * value or a range (min, max) of uniformly distributed random values.
	 * Defines the message size (bytes). */
	public static final String MESSAGE_SIZE_S = "size";
	/** Message creation interval range -setting id ({@value}). Can be either a 
	 * single value or a range (min, max) of uniformly distributed 
	 * random values. Defines the inter-message creation interval (seconds). */
	public static final String MESSAGE_INTERVAL_S = "interval";
	/** Sender/receiver address range -setting id ({@value}). 
	 * The lower bound is inclusive and upper bound exclusive. */
	public static final String HOST_RANGE_S = "hosts";
	/** (Optional) receiver address range -setting id ({@value}). 
	 * If a value for this setting is defined, the destination hosts are 
	 * selected from this range and the source hosts from the 
	 * {@link #HOST_RANGE_S} setting's range.   
	 * The lower bound is inclusive and upper bound exclusive. */
	
	//public static final String TO_HOST_RANGE_S = "tohosts";

	/** Message ID prefix -setting id ({@value}). The value must be unique 
	 * for all message sources, so if you have more than one message generator,
	 * use different prefix for all of them. The random number generator's
	 * seed is derived from the prefix, so by changing the prefix, you'll get
	 * also a new message sequence. */
	public static final String MESSAGE_ID_PREFIX_S = "prefix";
	/** Message creation time range -setting id ({@value}). Defines the time
	 * range when messages are created. No messages are created before the first
	 * and after the second value. By default, messages are created for the 
	 * whole simulation time. */
	public static final String MESSAGE_TIME_S = "time";
	
	public static final String MULTICAST_DESTINY = "multcastDestiny";
	
	/** Time of the next event (simulated seconds) */
	protected double nextEventsTime = 0;
	/** Range of host addresses that can be senders or receivers */
	protected int[] hostRange = {0, 0};
	/** Range of host addresses that can be receivers */
	protected int[] toHostRange = null;
	/** Next identifier for a message */
	private int id = 0;
	/** Prefix for the messages */
	protected String idPrefix;
	/** Size range of the messages (min, max) */
	private int[] sizeRange;
	/** Interval between messages (min, max) */
	private int[] msgInterval;
	/** Time range for message creation (min, max) */
	protected double[] msgTime;

	private ArrayList<Integer> toHosts;
	/** Random number generator for this Class */
	protected Random rng;
	
	private SimScenario scene;
	
	/**
	 * Constructor, initializes the interval between events, 
	 * and the size of messages generated, as well as number 
	 * of hosts in the network.
	 * @param s Settings for this generator.
	 */
	public MulticastMessageEventGenerator(Settings s){
		this.sizeRange = s.getCsvInts(MESSAGE_SIZE_S);
		this.msgInterval = s.getCsvInts(MESSAGE_INTERVAL_S);
		this.hostRange = s.getCsvInts(HOST_RANGE_S, 2);
		this.idPrefix = s.getSetting(MESSAGE_ID_PREFIX_S);
		
		if (s.contains(MESSAGE_TIME_S)) {
			this.msgTime = s.getCsvDoubles(MESSAGE_TIME_S, 2);
		}
		else {
			this.msgTime = null;
		}
		
		/* if prefix is unique, so will be the rng's sequence */
		this.rng = new Random(idPrefix.hashCode());
	
		int toHostProportion = s.getInt(MULTICAST_DESTINY);
		int toHostSize = (int)((toHostProportion/100)*(hostRange[1]+1));
		
		List<Integer> auxToHostList = new LinkedList<Integer>();
		
		
		for (int i = 0 ; i < toHostSize; i++) {
			auxToHostList.add(i);
		}
		
		
		toHosts = new ArrayList<Integer>();
		
		for (int i = 0 ; i < toHostSize; i++) {
			toHosts.add(auxToHostList.remove(rng.nextInt(auxToHostList.size())));
		}
		
		
		
		
		if (this.sizeRange.length == 1) {
			/* convert single value to range with 0 length */
			this.sizeRange = new int[] {this.sizeRange[0], this.sizeRange[0]};
		}
		else {
			s.assertValidRange(this.sizeRange, MESSAGE_SIZE_S);
		}
		if (this.msgInterval.length == 1) {
			this.msgInterval = new int[] {this.msgInterval[0], 
					this.msgInterval[0]};
		}
		else {
			s.assertValidRange(this.msgInterval, MESSAGE_INTERVAL_S);
		}
		s.assertValidRange(this.hostRange, HOST_RANGE_S);
		
		if (this.hostRange[1] - this.hostRange[0] < 2) {
			if (this.toHostRange == null) {
				throw new SettingsError("Host range must contain at least two " 
						+ "nodes unless toHostRange is defined");
			}
			else if (toHostRange[0] == this.hostRange[0] && 
					toHostRange[1] == this.hostRange[1]) {
				// XXX: teemuk: Since (X,X) == (X,X+1) in drawHostAddress()
				// there's still a boundary condition that can cause an
				// infinite loop.
				throw new SettingsError("If to and from host ranges contain" + 
						" only one host, they can't be the equal");
			}
		}
		
		/* calculate the first event's time */
		this.nextEventsTime = (this.msgTime != null ? this.msgTime[0] : 0) 
			+ msgInterval[0] + 
			(msgInterval[0] == msgInterval[1] ? 0 : 
			rng.nextInt(msgInterval[1] - msgInterval[0]));
		

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
		System.out.println("passou em :" +nextEventsTime);

		int responseSize = 0; /* zero stands for one way messages */
		int msgSize;
		int interval;
		int from;
		int to;
			
		msgSize = drawMessageSize();
		interval = drawNextEventTimeDiff();
		
		MulticastMessageCreateEvent mmce = new MulticastMessageCreateEvent(rng, toHosts,this.getID(),msgSize,responseSize,this.nextEventsTime);
		
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
