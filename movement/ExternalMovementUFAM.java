/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;


import input.ExternalMovementUFAMReader;




import java.util.Scanner; // import the Scanner class 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import util.Tuple;
import util.LabelList;

import core.Coord;
import core.DTNHost;
import core.DTNSim;
import core.Settings;
import core.SimClock;

/**
 * Movement model that uses external data of node locations.
 */
public class ExternalMovementUFAM extends MovementModel {
	/** Namespace for settings */
	public static final String EXTERNAL_MOVEMENT_NS = "ExternalMovementUFAM";
	/** external locations file's path -setting id ({@value})*/
	public static final String MOVEMENT_FILE_S = "file";
	/** number of preloaded intervals per preload run -setting id ({@value})*/
	public static final String NROF_PRELOAD_S = "nrofPreload";


	//----------------------------------------------------------------------------
	public static final String FILE_DIRECTORY = "directory";
	private static String directory;
	public static final String FILE_NAME = "fileName";
	public static String file;
	
	public static ArrayList<ExternalMovementUFAMReader> files = new ArrayList<ExternalMovementUFAMReader>();

	
	public static LinkedList<LabelList> meta;
//	public String groupNumber;
//	public String groupStaticNumber;
	public static int lastGroup;
	public static Settings groupSettings;
	public static Map<String,DTNHost> hostMap = new HashMap<String,DTNHost>(); //tem que mapiar os hosts de cada trace pra cada id
	public int intGroupNumber;
	
	public static int nextHost;
	

	//----------------------------------------------------------------------------
	public static Map<Integer, ExternalMovementUFAM> idMapping;
	
	/** default initial location for excess nodes */
	private static final Coord DEF_INIT_LOC = new Coord(0,0);
	//public ExternalMovementUFAMReader reader;
	private static String inputFileName;
	
	/** initial locations for nodes */
	public List<Tuple<String, Coord>> initLocations;
	/** time of the very first location data */
	
	private double initTime;
	/** sampling interval (seconds) of the location data */
	private static double samplingInterval ;
	/** last read time stamp after preloading */
	private static ArrayList<Double> lastPreloadTime= new ArrayList<Double>();
	/** how many time intervals to load on every preload run */
	private static double nrofPreload = 10;
	/** minimum number intervals that should be preloaded ahead of sim time */
	private static final double MIN_AHEAD_INTERVALS = 2;
		
	/** the very first location of the node */
	private Coord intialLocation;
	/** queue of path-start-time, path tuples */
	
	private Queue<Tuple<Double, Path>> pathQueue;
	
	/** when was the path currently under construction started */
	private double latestPathStartTime;
	/** the last location of path waypoint */
	private Coord latestLocation;
	/** the path currently under construction */
	private Path latestPath;
	
	/** is this node active */
	private boolean isActive;
	
	static {
		DTNSim.registerForReset(ExternalMovementUFAM.class.getCanonicalName());
		reset();
	}
	
	/**
	 * Constructor for the prototype. Run once per group.
	 * @param settings Where settings are read from
	 */

	public ExternalMovementUFAM(Settings settings) {
		super(settings);
		nextHost = 0;
		groupSettings = new Settings(settings.getNameSpace());
		//if (idMapping == null) {
			// run these the first time object is created or after reset call
			
			//------------------------------------------------
			String group = groupSettings.getNameSpace();
			
			this.groupNumber = group.substring(group.length() - 1);
			try {
				int i = 1;
				while(Integer.parseInt(this.groupNumber) > -1){
					this.groupNumber = group.substring(group.length() - i);
					i = i+1;
				}
			}
			catch (Exception InputMismatchException) {
				this.groupNumber = this.groupNumber.substring(1, this.groupNumber.length());
			}
			
			//------------------------------------------------
			
			Settings s = new Settings(EXTERNAL_MOVEMENT_NS);

			
			//----------------------------------------------------------
			file = s.getSetting(FILE_NAME);
			directory = s.getSetting(FILE_DIRECTORY);
			//----------------------------------------------------------
			inputFileName = directory+file+groupNumber+".txt";
			ExternalMovementUFAMReader reader = new ExternalMovementUFAMReader(inputFileName);
			files.add(reader);
			meta = files.get(Integer.valueOf(groupNumber)-1).initStatus(group);
			
			if(idMapping == null) {
				idMapping = new HashMap<Integer, ExternalMovementUFAM>();				
			}
			
			//System.out.println(inputFileName);
			
			List<Tuple<String, Coord>> proxHosts = new ArrayList<Tuple<String, Coord>>(files.get(Integer.valueOf(groupNumber)-1).readNextMovementsIgnoreDeactives());
			for(int i = 0; i < proxHosts.size(); i++) {
				protoInitLocations.add(proxHosts.get(i));
			}
			
			initTime = files.get(Integer.valueOf(groupNumber)-1).getLastTimeStamp();
			
			samplingInterval = -1;
			lastPreloadTime.add((double) -1);
			
			s.setNameSpace(EXTERNAL_MOVEMENT_NS);
			if (s.contains(NROF_PRELOAD_S)) {
				nrofPreload = s.getInt(NROF_PRELOAD_S);
				if (nrofPreload <= 0) {
					nrofPreload = 1;
				}
			}

			groupStaticNumber = groupNumber;
		//}
	}
	

	/** 
	 * Copy constructor. Gives out location data for the new node from 
	 * location queue.
	 * @param mm The movement model to copy from
	 */
	private ExternalMovementUFAM(MovementModel mm) {
		super(mm);
		this.groupNumber = groupStaticNumber;
		this.intGroupNumber = Integer.valueOf(groupStaticNumber);
		pathQueue = new LinkedList<Tuple<Double, Path>>();
		
		latestPath = null;
		
		if (protoInitLocations.size() > 0) { // we have location data left
			// gets a new location from the list
			Tuple<String, Coord> initLoc = protoInitLocations.remove(0); 
			this.intialLocation = this.latestLocation = initLoc.getValue();
			this.latestPathStartTime = initTime;
			// puts the new model to model map for later updates
			idMapping.put(Integer.valueOf(initLoc.getKey()), this);
			isActive = true;
		}
		else {
			System.out.println("nos sem loc");
			// no more location data left for the new node -> set inactive
			isActive = false;
		}		
	}
	
	public void setHostMap(DTNHost host) {
		////System.out.println(String.valueOf(groupNumber)+"-"+String.valueOf(nextHost)+ " -> "+host.getAddress());
		String hostId = this.groupNumber+"-"+String.valueOf(nextHost);
		hostMap.put(hostId,host);
		nextHost = nextHost+1;
	}
	
	public String showHostMap() {
		return hostMap.toString();
	}
	
	public LabelList getLabel(DTNHost host) {
		return meta.remove();
	}
	

	
	/**
	 * Checks if more paths should be preloaded and preloads them if
	 * needed.
	 */
	private void checkPathNeed() {
		//System.out.println("PATH NEEDED GROUP:  "+this.groupNumber);
		
		if (samplingInterval == -1) { // first preload
			lastPreloadTime.set((intGroupNumber-1), this.readMorePaths()) ;
			
			//System.out.println("Grupo: "+this.groupNumber+" FIRST preload: "+lastPreloadTime.get(intGroupNumber-1));
		}
		if (!Double.isNaN(lastPreloadTime.get(intGroupNumber-1)) && SimClock.getTime() >= 
				lastPreloadTime.get(intGroupNumber-1) - (samplingInterval * MIN_AHEAD_INTERVALS) ) {
			for (int i=0; i < nrofPreload && 
					!Double.isNaN(lastPreloadTime.get(intGroupNumber-1)); i++) {
				lastPreloadTime.set((intGroupNumber-1),this.readMorePaths());
				//System.out.println("Grupo: "+this.groupNumber+" preload: "+lastPreloadTime.get(intGroupNumber-1));
			}
		}
	}
	
	@Override
	public Coord getInitialLocation() {
		return this.intialLocation;
	}
	
	@Override
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Adds a new location with a time to this model's move pattern. If the
	 * node stayed stationary during the update, the current path is put to the
	 * queue and a new path is started once the node starts moving.
	 * @param loc The location
	 * @param time When should the node be there
	 */
	private void addLocation(Coord loc, double time) {
		assert samplingInterval > 0 : "Non-positive sampling interval!";
		
		if (loc.equals(latestLocation)) { // node didn't move
			if (latestPath != null) {
				// constructing path -> end constructing and put it in the queue
				pathQueue.add(new Tuple<Double, Path>(latestPathStartTime, latestPath));
				latestPath = null;
			}

			this.latestPathStartTime = time;
			return;
		}
		
		if (latestPath == null) {
			latestPath = new Path();			
		}
			
		double speed = loc.distance(this.latestLocation) / samplingInterval;		
		latestPath.addWaypoint(loc, speed);
		//System.out.println(" ANDOU PARA: "+loc.toString());
		this.latestLocation = loc;
	}
	
	/**
	 * Returns a sim time when the next path is available.
	 * @return The sim time when node should ask the next time for a path
	 */
	@Override
	public double nextPathAvailable() {
		if (pathQueue.size() == 0) {
			return latestPathStartTime;
		}
		else {
			return pathQueue.element().getKey();
		}		
	}
	
	@Override
	public Path getPath() {
		//System.out.println("PATH GROUP: "+this.groupNumber);
		Path p;
		
		checkPathNeed(); // check if we should preload more paths		
		//System.out.println("terminou de checar");

		if (SimClock.getTime() < this.nextPathAvailable()) {
			//System.out.println("O tempo de andar esta na frente do tempo atual ");
			return null;
		}		
		
		if (pathQueue.size() == 0) { // nothing in the queue, return latest
			//System.out.println("Nao tem caminho e entao reseta o anterior");
			
			p = latestPath;
			latestPath = null;
		}
		else {	// return first path in the queue
			//System.out.println("pegou o primeiro path disponivel");
			p = pathQueue.remove().getValue();
		}
		//System.out.println("retornou o caminho");		
		return p;
	}
	
	@Override
	public int getMaxX() {
		return (int)(files.get(Integer.valueOf(groupNumber)-1).getMaxX() - files.get(Integer.valueOf(groupNumber)-1).getMinX()) + 1;
	}

	@Override
	public int getMaxY() {
		return (int)(files.get(Integer.valueOf(groupNumber)-1).getMaxY() - files.get(Integer.valueOf(groupNumber)-1).getMinY()) + 1;
	}

	
	@Override
	public MovementModel replicate() {
		return new ExternalMovementUFAM(this);
	}
	
	/**
	 * Reads paths for the next time instance from the reader
	 * @return The time stamp of the reading or Double.NaN if no movements
	 * were read.
	 */
	public double readMorePaths() {
		//System.out.println(this.groupNumber + " - READMOREPATHS");
		//ACTIVE E DEACTIVE
		
		List<Tuple<String,Integer>> activeDeactiveList = files.get(this.intGroupNumber-1).checkActiveness();
		while(activeDeactiveList.size() > 0) {
			Tuple<String,Integer> hostAtual = activeDeactiveList.remove(0);
			if(hostAtual.getKey().equals("deactive")) {				
				//System.out.println("DESATIVADO"+hostAtual.getValue());
				hostMap.get(this.groupNumber+"-"+String.valueOf(hostAtual.getValue())).getInterface(1).deActivate();
			}
			if(hostAtual.getKey().equals("active")) {				
				//System.out.println("ATIVADO"+hostAtual.getValue());
				hostMap.get(this.groupNumber+"-"+String.valueOf(hostAtual.getValue())).getInterface(1).activate();
			}
		}

		//proximos movimentos
		List<Tuple<String, Coord>> list = files.get(this.intGroupNumber-1).readNextMovements();
		double time = files.get(this.intGroupNumber-1).getLastTimeStamp();
		
		if (samplingInterval == -1) {
			samplingInterval = time - this.initTime;
		}
		
	
		
		for (Tuple<String, Coord> t : list) {		
			int hostAddres = hostMap.get(this.groupNumber+"-"+t.getKey()).getAddress();
			ExternalMovementUFAM em = idMapping.get(hostAddres);
			
			
			if (em != null) { // skip unknown IDs, i.e. IDs not mentioned in...
				//System.out.println("HOST "+t.getKey()+" /" +this.groupNumber+"-"+t.getKey() );
		
				// ...init phase or if there are more IDs than nodes
				em.addLocation(t.getValue(), time);
			}
		}
		if (list.size() > 0) {
			return time;
		}
		else {
			return Double.NaN;
		}
	}
	
	/**
	 * Reset state so that next instance will have a fresh state
	 */
	public static void reset() {
		idMapping = null;
	}

}
