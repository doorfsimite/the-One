/* 
 * Copyright 2010-2012 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.ArrayList;
import java.util.LinkedList;
/** 
 * Records the average buffer occupancy and its variance with format:
 * <p>
 * [Simulation time] [average buffer occupancy % [0..100] ] [variance]
 * </p>
 * 
 * <p>
 * The occupancy is calculated as an instantaneous snapshot every nth second
 * as defined by the <code>occupancyInterval</code> setting, not as an
 * average over time.
 * </p>
 * 
 * @author	teemuk
 */
import java.util.List;

import core.DTNHost;
import core.Settings;
import core.SimClock;
import core.UpdateListener;

public class BufferOccupancyReport extends Report implements UpdateListener {

	/**
	 * Record occupancy every nth second -setting id ({@value}). 
	 * Defines the interval how often (seconds) a new snapshot of buffer
	 * occupancy is taken
	 */
	public static final String BUFFER_REPORT_INTERVAL = "occupancyInterval";
	/** Default value for the snapshot interval */
	public static final int DEFAULT_BUFFER_REPORT_INTERVAL = 5;
	public ArrayList<LinkedList<Double>> buffers;
	private double lastRecord = Double.MIN_VALUE;
	private LinkedList<Double> records = new LinkedList<Double>();
	private int interval;
	
	/**
	 * Creates a new BufferOccupancyReport instance.
	 */
	public BufferOccupancyReport() {
		super();
		
		Settings settings = getSettings();
		if (settings.contains(BUFFER_REPORT_INTERVAL)) {
			interval = settings.getInt(BUFFER_REPORT_INTERVAL);
		} else {
			interval = -1; /* not found; use default */
		}
		
		if (interval < 0) { /* not found or invalid value -> use default */
			interval = DEFAULT_BUFFER_REPORT_INTERVAL;
		}
	}
	
	public void updated(List<DTNHost> hosts) {
		if (SimClock.getTime() - lastRecord >= interval) {
			if(buffers == null) {
				buffers = new ArrayList<LinkedList<Double>>();
				for (DTNHost h : hosts) {
					buffers.add(new LinkedList<Double>());
				}
			}
			lastRecord = SimClock.getTime();
			records.add(lastRecord);
			for (DTNHost h : hosts) {
				buffers.get(h.getAddress()).add(h.getUsedBuffer());
			}
		}
	}
	/*
	public void lastUpdated(List<DTNHost> hosts) {
		if(buffers == null) {
			buffers = new ArrayList<LinkedList<Double>>();
			for (DTNHost h : hosts) {
				buffers.add(new LinkedList<Double>());
			}
		}
		lastRecord = SimClock.getTime();
		records.add(lastRecord);
		for (DTNHost h : hosts) {
			buffers.get(h.getAddress()).add(h.getUsedBuffer());
		}
	}
	*/
	/**
	 * Prints a snapshot of the average buffer occupancy
	 * @param hosts The list of hosts in the simulation
	 */
	public void done() {
		write("records: "+records.size());
		write("      Record Time");
		write("Host  UsedBuffer\n");
		String records_output = " ";
		for (double rec : records) {
			records_output += " " + rec;
		}
		write(records_output);
		
		for(int i = 0; i < buffers.size(); i++) {	
			
			String output = String.valueOf(i);
			for(int j = 0; j < buffers.get(i).size(); j++) {
				
				output += " " + String.valueOf(buffers.get(i).get(j));
			}
			write(output);
		}
		super.done();
	}
}
