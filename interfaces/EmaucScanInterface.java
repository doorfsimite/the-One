/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import core.CBRConnection;
import core.Connection;
import core.NetworkInterface;
import core.Settings;
import core.SimClock;
import core.Settings;
import util.Tuple;

/**
 * A simple Network Interface that provides a constant bit-rate service, where
 * one transmission can be on at a time.
 */
public class EmaucScanInterface extends NetworkInterface {

	
	//** namespaces **//
	public static final String MAUC_SCAN_DELTA = "mauc.delta";
	public static final String MAUC_MOVING_THRESHOLD = "mauc.movingThreshold";
	public static final String MAUC_K = "mauc.k";
	public static final String MAUC_RSSI_THRESHOLD = "mauc.rssiThreshold";
	public static final String EMAUC_SCAN_CLOCK= "emauc.scanClock";
	public static final String EMAUC_SCAN_DURATION = "mauc.scanDuration";
	
	
	//** variaveis lidas das settings**//
	
	Settings interfaceSetting = new Settings();
	double delta = interfaceSetting.getDouble(MAUC_SCAN_DELTA);
	double movingThreshold = interfaceSetting.getDouble(MAUC_MOVING_THRESHOLD);	
	double k = interfaceSetting.getDouble(MAUC_K);	
	double rssiThreshold = interfaceSetting.getDouble(MAUC_RSSI_THRESHOLD);	
	double scanClock = interfaceSetting.getDouble(EMAUC_SCAN_CLOCK);	
	double scanDuration = interfaceSetting.getDouble(EMAUC_SCAN_DURATION);	

		
	//** variaveis setadas **//
	static int tempoDeDeslocamento = 5; //confere a cada 5 segundos
	boolean moving = false;
	double tScan = 0;
	double oldTscan = 0;
	double duracao = delta;
	boolean insideScanInterval = false;
	double backoff = 1;
	double lastWeakRssiTime = 0;
	boolean weakRssi = false;
	boolean recevedNotification = false;
	public boolean scanFail = true;
	double remainingScanTime = -1;
	boolean recentStop = false; //se parou entao escaneia
	List<Double> notificationsTimes= new LinkedList<Double>();
	
	
	/**
	 * Reads the interface settings from the Settings file
	 */
	public EmaucScanInterface(Settings s)	{
		super(s);
	}
		
	/**
	 * Copy constructor
	 * @param ni the copied network interface object
	 */
	public EmaucScanInterface(EmaucScanInterface ni) {
		super(ni);
	}

	public NetworkInterface replicate()	{
		return new EmaucScanInterface(this);
	}

	/**
	 * Tries to connect this host to another host. The other host must be
	 * active and within range of this host for the connection to succeed. 
	 * @param anotherInterface The interface to connect to
	 */
	public void connect(NetworkInterface anotherInterface) {
		if (isScanning2()  
				&& anotherInterface.getHost().isRadioActive() 
				&& isWithinRange(anotherInterface) 
				&& !isConnected(anotherInterface)
				&& (this != anotherInterface) 
				&& anotherInterface.getInterfaceState() == 1){ //E estiver no estado idle
			// new contact within range
			// connection speed is the lower one of the two speeds 
			int conSpeed = anotherInterface.getTransmitSpeed(this);
			if (conSpeed > this.transmitSpeed) {
				conSpeed = this.transmitSpeed; 
			}
			Connection con = new CBRConnection(this.host, this, 
					anotherInterface.getHost(), anotherInterface, conSpeed);
			connect(con,anotherInterface);
			
			this.remainingScanTime = scanDuration;
			this.scanFail = false;
			
			EmaucScanInterface ei = (EmaucScanInterface) anotherInterface;
			ei.remainingScanTime = scanDuration;
			ei.scanFail = false;
			
			if(moving) {sendNotification(anotherInterface);}
		}
	}
	
	public void sendNotification(NetworkInterface anotherInterface) {
		EmaucScanInterface ei = (EmaucScanInterface) anotherInterface;
		ei.recevedNotification= true;
		ei.notificationsTimes.add(SimClock.getTime());
	}
	
	/**Calcula o RSSI da conexão com outra interface e deve ser chamada quando a outra interface está conectada**/
	public double rssi(NetworkInterface anotherInterface) {
		return (100*this.getLocation().distance(anotherInterface.getLocation()))/this.transmitRange;
	}
	
	
	
	/**
	 * Updates the state of current connections (i.e. tears down connections
	 * that are out of range and creates new ones).
	 */
	public void update() {
		double simTime = SimClock.getTime();

		/* printado o tempo no world pra n esquecer */
		if (optimizer == null) {
			return; /* nothing to do */
		}
		setMovementStatus();

		// First break the old ones
		optimizer.updateLocation(this);
		for (int i=0; i<this.connections.size(); ) {
			Connection con = this.connections.get(i);
			NetworkInterface anotherInterface = con.getOtherInterface(this);
			if(moving && (lastWeakRssiTime + delta <= simTime) ) {
				lastWeakRssiTime = simTime;
				if(rssi(anotherInterface) > rssiThreshold) {
					sendNotification(anotherInterface);
					weakRssi = true;
				}
			}
				// all connections should be up at this stage
			assert con.isUp() : "Connection " + con + " was down!";

			if (!isWithinRange(anotherInterface)) {
				disconnect(con,anotherInterface);
				connections.remove(i);
			}
			else {
				i++;
			}
		}
		// Then find new possible connections
		Collection<NetworkInterface> interfaces =
			optimizer.getNearInterfaces(this);
		

		//Verifica se esta dentro do tempo de scan global (poderia ser executado apenas uma vez pq serve pra geral maaaas o the one n deixa)
		insideScanInterval = (simTime < (scanDuration  + simTime - (simTime % scanClock))) ? true : false;
		
		if(moving) {//se estiver se movimentando
			if(oldTscan <= simTime &&  simTime == (simTime - (simTime % scanClock))) {
				remainingScanTime = 2;
			}
		}
		else {//se estiver parado
			if(this.recevedNotification) {
				if( simTime == (simTime - (simTime % scanClock)) && ((double)this.notificationsTimes.get(0) + delta <= simTime )) {
					remainingScanTime = 2;
					this.notificationsTimes.remove(0);
					if(this.notificationsTimes.size() == 0) {
						this.recevedNotification = false;
					}
				}
			}
			this.intefaceState = (simTime < (2  + simTime - (simTime % scanClock))) ? 1 : 0 ; //Se esta no inicio do intervalo de scan entao esta no estado idle caso contrario sleep
			if(this.intefaceState == 1 && this.recentStop == true){
				remainingScanTime = 2;
				recentStop = false;
			}
		}
		for (NetworkInterface i : interfaces) {
			connect(i);
		}
		if(moving) {//Moving nodes
			oldTscan = tScan;
			if(scanFail == false) {
				duracao = duracao/k;
				backoff = 1;			
				tScan = simTime + duracao;
			}
			else {
				if(simTime >= tScan && insideScanInterval) {
					duracao = duracao * Math.pow(Math.E,backoff);
					backoff = backoff + 1;
					tScan = simTime + duracao;
				}
			}
			if(weakRssi && insideScanInterval) {
				duracao = delta;
				tScan = simTime + delta;
				lastWeakRssiTime = simTime;
				weakRssi = false;
			}			
		}
		this.remainingScanTime -= 1;
		scanFail = true;	
	}

	public double getNextScanIntervalAfterTscan() {
		return (tScan%scanClock == 0) ? tScan : tScan + (scanClock - tScan%scanClock);
	}
	/** 
	 * Creates a connection to another host. This method does not do any checks
	 * on whether the other node is in range or active 
	 * @param anotherInterface The interface to create the connection to
	 */
	public void createConnection(NetworkInterface anotherInterface) {
		if (!isConnected(anotherInterface) && (this != anotherInterface)) {    			
			// connection speed is the lower one of the two speeds 
			int conSpeed = anotherInterface.getTransmitSpeed(this);
			if (conSpeed > this.transmitSpeed) {
				conSpeed = this.transmitSpeed; 
			}

			Connection con = new CBRConnection(this.host, this, 
					anotherInterface.getHost(), anotherInterface, conSpeed);
			connect(con,anotherInterface);
		}
	}

	/**
	 * Returns a string representation of the object.
	 * @return a string representation of the object.
	 */
	public String toString() {
		return "SimpleBroadcastInterface " + super.toString();
	}
	
	/**Define o estado de movimento do nó e obriga um scan caso o nó mude pare de se mover**/
	public void setMovementStatus() { 
		if(this.host.getDestination() == null) {
			this.moving = false;
			return;
		}
		if(isMoving()) { //caso esteja se movimentado
			if(!moving) {//Se ira comecar a se mover entao reseta os paramentros do descobrimento em movimento
				tScan = 0;
				oldTscan = 0;
				duracao = delta;
				backoff = 1;				
				this.intefaceState = 0; //interface alterna entre sleep e scaning
				this.moving = true;
			
			}
		}
		else { //ou parado
			if(moving) {//se estava se movendo antes entao reseta todos os parametros relacionados ao descobrimento em estatico
				this.intefaceState = 1; //a interface varia entre idle e sleep
				this.recentStop = true;
				this.moving = false; 
			}
		}
	}
	
	public boolean isMoving() {//Por enquanto printando
		if(SimClock.getTime() > tempoDeDeslocamento) {
			return movingThreshold < this.host.getCurrentLocation().distance(this.host.getLastLocation()) ? true : false; 			
		}
		return false;
		
	}
	
	public boolean isScanning() {/**Escolhe entre scan statico ou em movimento**/
		if (!isActive()) { //se esta desligado não escaneia
			return false;
		}
		return moving ? movingScan() : staticScan();
	}
	public boolean isScanning2() {/**Corrige a verificacao de scan no tempo errado**/
		if (!isActive()) { //se esta desligado não escaneia
			return false;
		}
		return moving ? movingScan2() : staticScan2();
	}
	
	//PULANDO LINHA NO DTNHOST PRA FICAR LEGAL
	
	protected boolean movingScan() {/**Se o tempo for maior ou igual ao oldtScan escaneia**/
		
		if(remainingScanTime + 1 > 0) {//verifica o scan time e se esta coordenado com o scan global (+1 por causa de updates difererentes)
			return true;
		}
		return false;
	}	
	protected boolean staticScan() {
		if(remainingScanTime + 1 > 0) {
			return true;			
		}
		return false;
	}
	protected boolean movingScan2() {/**Se o tempo for maior ou igual ao tScan escaneia**/
		if(remainingScanTime > 0) {//verifica se ficou durante toda a duração de scan
			return true;
		}
		return false;
	}	
	protected boolean staticScan2() {
		if(remainingScanTime > 0) {//verifica o scan time e se esta coordenado com o scan global
			return true;			
		}
		return false;
	}
}
