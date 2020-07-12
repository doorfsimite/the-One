package report;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import core.ConnectionListener;
import core.DTNHost;
import core.Message;
import interfaces.EmaucScanInterface;
import core.Settings;
import core.SimError;
import core.UpdateListener;
import input.StandardEventsReader;

public class ScanReport extends Report implements UpdateListener,ConnectionListener{
	static private Settings networkSettings;
	
	static ArrayList<Double> staticEnergy;
	static ArrayList<Double> staticTime;
	
	static ArrayList<Double> movingEnergy;
	static ArrayList<Double> movingTime;
	
	static ArrayList<Double> timeON;
	
	static double scanEnergy;
	static ArrayList<Double> movingScanTime;
	static ArrayList<Double> staticScanTime;
	
	static double idleEnergy;
	static ArrayList<Double> movingIdleTime;
	static ArrayList<Double> staticIdleTime;
	
	static double sleepEnergy;
	static ArrayList<Double> movingSleepTime;
	static ArrayList<Double> staticSleepTime;
	
	static double hostsSize;
	
	static String path;
	static PrintWriter log;
	
	static ArrayList<Double> lastEnergyCheck;//usado pra registrar diferenca de energia do ultimo contato
	
	public ScanReport() {
		networkSettings  = getSettings();
		path = networkSettings.getSetting("scanLog");
		String outFileName = networkSettings.valueFillString(path);
		File outFile = new File(outFileName);
		
		
		try {
			log = new PrintWriter(new FileWriter(outFileName));
		} catch (IOException e) {
			throw new SimError("Couldn't open file '" + outFileName + 
					"' for report output\n" + e.getMessage(), e);	
		}	
		

		networkSettings.setNameSpace(core.SimScenario.GROUP_NS+"1");
		
		scanEnergy  = networkSettings.getDouble(routing.util.EnergyModel.SCAN_ENERGY_S);
		idleEnergy  = networkSettings.getDouble(routing.util.EnergyModel.IDLE_ENERGY_S);
		sleepEnergy  = networkSettings.getDouble(routing.util.EnergyModel.SLEEP_ENERGY_S);
		
		super.init();
	}
	
	public void escrever(String txt) {
		log.println(txt);
	}
	//UPDATE LISTENER
		public void updated(List<DTNHost> hosts) {
			if(staticEnergy == null) { //lazy instaciation
				timeON = new ArrayList<Double>();
				
				staticEnergy = new ArrayList<Double>();
				staticTime = new ArrayList<Double>();
				staticScanTime = new ArrayList<Double>();
				staticIdleTime = new ArrayList<Double>();
				staticSleepTime = new ArrayList<Double>();
				
				movingEnergy = new ArrayList<Double>();
				movingTime = new ArrayList<Double>();
				movingScanTime = new ArrayList<Double>();
				movingIdleTime = new ArrayList<Double>();
				movingSleepTime = new ArrayList<Double>();
				
				lastEnergyCheck = new ArrayList<Double>();
				
				
				for(DTNHost h : hosts) {
					staticEnergy.add(0.0);
					staticTime.add(0.0);
					
					movingEnergy.add(0.0);
					movingTime.add(0.0);
					
					timeON.add(0.0);
					
					movingScanTime.add(0.0);
					movingIdleTime.add(0.0);
					movingSleepTime.add(0.0);
					
					staticScanTime.add(0.0);
					staticIdleTime.add(0.0);
					staticSleepTime.add(0.0);
					lastEnergyCheck.add(0.0);
				}
				hostsSize = hosts.size();				
			}
			
			if (isWarmup()) {
				return; /* warmup period is on */
			}		
			
			for(DTNHost h : hosts) {
				EmaucScanInterface ei = (EmaucScanInterface) h.getInterface(1);
				if(ei.isActive()) {
					timeON.set(h.getAddress(), timeON.get(h.getAddress()) + 1);
				}
				if(ei.isMoving()) {//moving
					movingTime.set(h.getAddress(), movingTime.get(h.getAddress()) + 1);
					if(ei.isScanning()) {
						
						movingEnergy.set(h.getAddress(), movingEnergy.get(h.getAddress()) + scanEnergy);
						movingScanTime.set(h.getAddress(), movingScanTime.get(h.getAddress()) + 1);
						
					}
					else {
						if(ei.getInterfaceState() == 1) {//idle NO CASO DO DESCOBRIMENTO EM MOVIMENTO O ESTADO IDLE NAO FUNCIONA
							movingEnergy.set(h.getAddress(), movingEnergy.get(h.getAddress()) + idleEnergy);
							movingIdleTime.set(h.getAddress(), movingIdleTime.get(h.getAddress()) + 1);
						}
						else {//sleep
							movingEnergy.set(h.getAddress(), movingEnergy.get(h.getAddress()) + sleepEnergy);
							movingSleepTime.set(h.getAddress(), movingSleepTime.get(h.getAddress()) + 1);
						}
					}
				}
				else {//static
					staticTime.set(h.getAddress(), staticTime.get(h.getAddress()) + 1);
					if(ei.isScanning()) {
						staticEnergy.set(h.getAddress(), staticEnergy.get(h.getAddress()) + scanEnergy);						
						staticScanTime.set(h.getAddress(), staticScanTime.get(h.getAddress()) + 1);
					}
					else {
						//IDEALMENTE O IDLE DEVERIA ACONTECER POR 1 SEGUNDO MAS O ONE PRECISA DE 2 UPDATES PARA ALTERNAR O ESTADO
						// DE TODAS AS INTERFACES. DAI, QUANDO ACONTECER DA INTERFACE ESTAR EM IDLE. A ENERGIA E O TEMPO DEVE SER A METADE.
						// NESSE EXPERIMENTO O ESTADO IDLE É USADO APENAS POR NOS ESTATICOS E POR APENAS 2 SEGUNDOS.
						//ENTAO DIVIDE POR 2 ENQUANTO N ESTA SE MOVENDO
						if(ei.getInterfaceState() == 1) {//idle
							staticEnergy.set(h.getAddress(), staticEnergy.get(h.getAddress()) + idleEnergy/2);
							staticIdleTime.set(h.getAddress(), staticIdleTime.get(h.getAddress()) + 1/2);
							//O RESTO DO TEMPO E ENERGIA É ADICIONADO AO ESTADO SLEEP
							staticEnergy.set(h.getAddress(), staticEnergy.get(h.getAddress()) + sleepEnergy/2);
							staticSleepTime.set(h.getAddress(), staticSleepTime.get(h.getAddress()) + 1/2);
						}
						else {//sleep
							staticEnergy.set(h.getAddress(), staticEnergy.get(h.getAddress()) + sleepEnergy);
							staticSleepTime.set(h.getAddress(), staticSleepTime.get(h.getAddress()) + 1);
						}
					}
				}
			}
		}
		public double discoveryEnergy(int address) {
			double usedEnergy  =  (staticEnergy.get(address) + movingEnergy.get(address))- lastEnergyCheck.get(address);
			lastEnergyCheck.set(address, (staticEnergy.get(address) + movingEnergy.get(address)));
			return usedEnergy;
		}
		
		private void processEvent(DTNHost host1,DTNHost host2,String extra) {
			escrever(getSimTime() + " " + "CONN" + " " + host1.getAddress() +" " + host2.getAddress() +" " + extra + " " + discoveryEnergy(host1.getAddress()) + " " + discoveryEnergy(host2.getAddress()));
		}
		
		public void hostsConnected(DTNHost host1, DTNHost host2) {
			processEvent(host1, host2, StandardEventsReader.CONNECTION_UP);
		}

		public void hostsDisconnected(DTNHost host1, DTNHost host2) {
			processEvent(host1, host2,StandardEventsReader.CONNECTION_DOWN);
		}
		
//		public void lastUpdated(List<DTNHost> hosts) {//falta adicionar o ultimo periodo sem descarregar}

		@Override
		public void done() {
	
			String header = "HOST   TimeON  | Moving: Time Energy ScanTime  idleTime sleepTime "+
										   "| Static: Time Energy ScanTime  idleTime sleepTime ";
			write(header);			
			for(int i = 0; i < this.hostsSize ; i ++) {
				String hostStatus = String.valueOf(i) +" "+timeON.get(i)+" "+movingTime.get(i) + " " +movingEnergy.get(i)+" "+ movingScanTime.get(i) + " " + 
										movingIdleTime.get(i)+" "+movingSleepTime.get(i) + " "+
						
										staticTime.get(i) + " " +staticEnergy.get(i)+" "+ staticScanTime.get(i) + " " + 
										staticIdleTime.get(i)+" "+staticSleepTime.get(i);
				write(hostStatus);
			}
			log.close();
			super.done();
		}
}
