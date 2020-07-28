/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Connection;
import core.ConnectionListener;
import core.ModuleCommunicationBus;
import core.ModuleCommunicationListener;
import core.Settings;
import core.SimClock;
import core.SimError;
import core.SimScenario;
import routing.ActiveRouter;
import routing.MessageRouter;
import routing.util.EnergyModel;
import core.UpdateListener;
import report.Report;
import util.Tuple;

/**
 * Report information about all delivered messages. Messages created during
 * the warm up period are ignored.
 * For output syntax, see {@link #HEADER}.
 */
public class SelfishBatteryReport extends Report implements UpdateListener{


	static private ArrayList<LinkedList<Tuple<Double,Double>>> tempoDeDescarregamento;
	static private ArrayList<LinkedList<Tuple<Double,Double>>> intervalosEgoistas;
	static private ArrayList<Double> lastCheck;
	static private ArrayList<Boolean> lastEgoistCheck;
	static ArrayList<Double> usedEnergy;
	static ArrayList<Double> tempoDeInicioDeAtivação;
	static ArrayList<Double> tempoDeInicioDeEgoismo;
	static ArrayList<LinkedList<Double>> consumoEmPeriodosNormais;
	static ArrayList<LinkedList<Double>> consumoEmPeriodosEgoistas;


	static LinkedList<Double> energiaRestante;
	
	static ArrayList<Integer> bateryLevel;
	static int hostsSize = -1;
	static private Settings settings;
	static int initEnergy;
	double simTime;
	static int endTime;

	
	//refazendo
	static ArrayList <Double> timeOn;
	static ArrayList <Double>  normalConsume;
	static ArrayList <Double>  selfishConsume;
	static ArrayList <Double>  lastEnergyLevel;
	static ArrayList <Double>  totalConsume;
	static ArrayList <Double>  normalTime;
	static ArrayList <Double>  selfishTime;

	static ArrayList <Integer>  discharges;
	
	public SelfishBatteryReport() {
		super.init();
		
		SimScenario scene = SimScenario.getInstance();
		bateryLevel = new ArrayList<Integer>();
		
		List<DTNHost> hosts = scene.getHosts();
		
		for (DTNHost h : hosts) {
			ActiveRouter ac = (ActiveRouter) h.getRouter();
			bateryLevel.add(ac.getMinSelfishBatteryLevel());
		}
		
		
		
		//Variaveis usadas
		settings = getSettings();
		settings.setNameSpace(core.SimScenario.SCENARIO_NS);
		endTime = settings.getInt(core.SimScenario.END_TIME_S);

		//Procura a energia inicial de algum grupo (ultimo setado para todos)
		settings.setNameSpace(core.SimScenario.GROUP_NS+"1");
		initEnergy  = settings.getInt((routing.util.EnergyModel.INIT_ENERGY_S));
	

		//java.util.Scanner myObj = new java.util.Scanner(System.in);
		//String debug = myObj.nextLine();
	}

	
	//UPDATE LISTENER
	public void updated(List<DTNHost> hosts) {
		if(hostsSize < 0) {
			hostsSize = hosts.size();
			tempoDeInicioDeAtivação = new ArrayList<Double>();
			tempoDeInicioDeEgoismo = new ArrayList<Double>();
			tempoDeDescarregamento = new ArrayList<LinkedList<Tuple<Double,Double>>>(hostsSize);
			intervalosEgoistas = new ArrayList<LinkedList<Tuple<Double,Double>>>(hostsSize);
	
			
			lastCheck = new ArrayList<Double>(hostsSize);
			lastEgoistCheck = new ArrayList<Boolean>(hostsSize);
			
			usedEnergy = new ArrayList<Double>();
			
			
			consumoEmPeriodosNormais = new ArrayList<LinkedList<Double>>();
			consumoEmPeriodosEgoistas =  new ArrayList<LinkedList<Double>>();
			
			//refazendo
			timeOn = new ArrayList<Double>();
			normalConsume = new ArrayList<Double>();
			selfishConsume = new ArrayList<Double>();
			totalConsume = new ArrayList<Double>();
			normalTime = new ArrayList<Double>();
			selfishTime = new ArrayList<Double>();
			lastEnergyLevel= new ArrayList<Double>();
			discharges= new ArrayList<Integer>();
			
			for(int i =0; i < hostsSize; i++) {
				lastCheck.add(i,0.0);
				lastEgoistCheck.add(false);
				tempoDeDescarregamento.add(i,new LinkedList<Tuple<Double,Double>>());
				intervalosEgoistas.add(i,new LinkedList<Tuple<Double,Double>>());
				tempoDeInicioDeAtivação.add(0.0);
				tempoDeInicioDeEgoismo.add(0.0);
				usedEnergy.add(0.0);
				consumoEmPeriodosNormais.add(i,new LinkedList<Double>());
				consumoEmPeriodosEgoistas.add(i,new LinkedList<Double>());
				
				
				//refazendo
				timeOn.add(0.0);
				normalConsume.add(0.0);
				selfishConsume.add(0.0);
				totalConsume.add(0.0);
				normalTime.add(0.0);
				selfishTime.add(0.0);
				lastEnergyLevel.add((double) initEnergy);
				discharges.add(0);
			}
		}
		
		simTime = getSimTime();
		if (isWarmup()) {
			return; /* warmup period is on */
		}		
		
		for(DTNHost h : hosts) {
			Double currentEnergy = (Double) h.getComBus().getProperty(routing.util.EnergyModel.ENERGY_VALUE_ID);
			if(h.getInterface(1).isActive()) {
				timeOn.set(h.getAddress(),timeOn.get(h.getAddress()) + 1);
				double usedEnergy = Math.abs(currentEnergy - lastEnergyLevel.get(h.getAddress()));
			/*/	if(simTime % 1000 == 0) {
					
					System.out.println(simTime);
					System.out.println(h.getAddress()  + " "+h.isEgoist()+" "+((double) h.getComBus().getProperty(routing.util.EnergyModel.ENERGY_VALUE_PERCENT)));
					System.out.println(currentEnergy + " "+ lastEnergyLevel.get(h.getAddress())+" "+ usedEnergy +"\n");
				
					
				//}
*/
				totalConsume.set(h.getAddress(),totalConsume.get(h.getAddress()) + usedEnergy);

				
				if(h.isEgoist()) {
					selfishTime.set(h.getAddress(),selfishTime.get(h.getAddress()) + 1.0);
					selfishConsume.set(h.getAddress(),selfishConsume.get(h.getAddress()) + usedEnergy);
				}else {
					normalTime.set(h.getAddress(),normalTime.get(h.getAddress()) + 1.0);
					normalConsume.set(h.getAddress(),normalConsume.get(h.getAddress()) + usedEnergy);
				}
				
				
			}
			if(currentEnergy == 0.0) { //Se nao tiver energia anota o primeiro tempo
				discharges.set(h.getAddress(),discharges.get(h.getAddress()) + 1);
			}
			lastEnergyLevel.set(h.getAddress(), currentEnergy);
		}

		
			/*if(lastCheck.get(h.getAddress()) != simTime - 1) {
					tempoDeDescarregamento.get(h.getAddress()).add(new Tuple<Double,Double>(tempoDeInicioDeAtivação.get(h.getAddress()),simTime));
					intervalosEgoistas.get(h.getAddress()).add(new Tuple<Double,Double>(tempoDeInicioDeEgoismo.get(h.getAddress()),simTime));
					lastEgoistCheck.set(h.getAddress(),false);
				}
				else {
					tempoDeInicioDeAtivação.set(h.getAddress(),simTime);
				}
				lastCheck.set(h.getAddress(),simTime);
			}
			else {//caso tenha energia verifica se está egoista ou nao
				if(h.isEgoist()) {
					if(lastEgoistCheck.get(h.getAddress()) == false) {  
						tempoDeInicioDeEgoismo.set(h.getAddress(),simTime);
						lastEgoistCheck.set(h.getAddress(),true);  
						consumoEmPeriodosNormais.get(h.getAddress()).add(initEnergy - value); 
						consumoEmPeriodosEgoistas.get(h.getAddress()).add(value);
					}
				}
				
				usedEnergy.set(h.getAddress(),initEnergy - value);                                
			}			
		} 
	*/
	}
/*
	public void lastUpdated(List<DTNHost> hosts) {//falta adicionar o ultimo periodo sem descarregar
		double lastTime = getSimTime();
		energiaRestante = new LinkedList<Double>();
		
		for(DTNHost h : hosts) {
			double energiaRemanecente = (double) h.getComBus().getProperty(routing.util.EnergyModel.ENERGY_VALUE_ID);
			energiaRestante.add(energiaRemanecente);
			
			double rechargeInterval = h.getRechargeInterval();
			if( tempoDeDescarregamento.get(h.getAddress()).size() == 0) {
				consumoEmPeriodosNormais.get(h.getAddress()).add( (double) ( -1*((bateryLevel.get(h.getAddress())/100)-1) )  * initEnergy);
				if(h.isEgoist()) {
					intervalosEgoistas.get(h.getAddress()).add(new Tuple<Double,Double>(tempoDeInicioDeEgoismo.get(h.getAddress()),lastTime));
					consumoEmPeriodosEgoistas.get(h.getAddress()).offerLast(((-1*((bateryLevel.get(h.getAddress())/100)-1) )*initEnergy)-(initEnergy-energiaRemanecente));			
				}
				
				continue;}
			
			double lastReportedInterval = tempoDeDescarregamento.get(h.getAddress()).getLast().getValue();
			
			
			if(lastReportedInterval + rechargeInterval < lastTime ) {//se ficou ativo antes de terminar a simulacao
				//periodo ativo
				tempoDeDescarregamento.get(h.getAddress()).add(new Tuple<Double,Double>(lastReportedInterval+rechargeInterval,lastTime));
				
				if(h.isEgoist()) {
					intervalosEgoistas.get(h.getAddress()).add(new Tuple<Double,Double>(tempoDeInicioDeEgoismo.get(h.getAddress()),lastTime));
					consumoEmPeriodosNormais.get(h.getAddress()).add( (double) ( -1*((bateryLevel.get(h.getAddress())/100)-1) )  * initEnergy);
					
					consumoEmPeriodosEgoistas.get(h.getAddress()).removeLast();
					consumoEmPeriodosEgoistas.get(h.getAddress()).offerLast(((-1*((bateryLevel.get(h.getAddress())/100)-1) )*initEnergy)-(initEnergy-energiaRemanecente));
					
				}
				else{
					consumoEmPeriodosNormais.get(h.getAddress()).add(initEnergy - energiaRemanecente);	
				}
				
			}
		}
	}
*/
	@Override
	public void done() {
		String header = "Selfish Battery Level: " + bateryLevel+ "\nHosts: "+ hostsSize;
		write(header);			
		
		String [] hostsStatusList = new String[hostsSize];
		
		String hostsStatus = "";
		double geralMeanEgoistPeriodConsume = 0;
		double geralMeanNormalPeriodConsume = 0;
		double geralMeanAllPeriodsConsume = 0;
		
		double geralEgoistPeridConsume = 0;
		double geralNormalPeridConsume = 0;
		double geralallPeriodsConsume = 0;
		
		
		double varGeralMeanEgoistPeriodConsume = 0;
		double varGeralMeanNormalPeriodConsume = 0;
		double varGeralMeanAllPeriodsConsume = 0;
		
		double varGeralEgoistPeridConsume = 0;
		double varGeralNormalPeridConsume = 0;
		double varGeralallPeriodsConsume = 0;
		
		
		double totalEgoistConsume = 0;
		double totalNormalConsume = 0;
		double totalAllPeridosConsume = 0;


		
		for(int i = 0; i < hostsSize ; i ++) {
			hostsStatus = "";
		
			double egoistPeriodConsume = selfishConsume.get(i);
			double normalPeriodConsume = normalConsume.get(i);
			double allPeriodsConsume = totalConsume.get(i);

			double meanEgoistPeriodConsume =  selfishTime.get(i) == 0.0 ? 0 : egoistPeriodConsume /selfishTime.get(i);
			double meanNormalPeriodConsume = normalTime.get(i) == 0.0 ? 0 : normalPeriodConsume/normalTime.get(i);
			double meanAllPeriodsConsume = timeOn.get(i) == 0.0 ? 0 : allPeriodsConsume/timeOn.get(i);
			
			
			geralEgoistPeridConsume += egoistPeriodConsume;
			geralNormalPeridConsume += normalPeriodConsume;
			geralallPeriodsConsume += allPeriodsConsume;
			
			geralMeanEgoistPeriodConsume += meanEgoistPeriodConsume;
			geralMeanNormalPeriodConsume += meanNormalPeriodConsume;
			geralMeanAllPeriodsConsume += meanAllPeriodsConsume;
			
			
			hostsStatus += ("HOST: "+i+"\n");
			hostsStatus += (meanAllPeriodsConsume+" "+meanNormalPeriodConsume+" "+meanEgoistPeriodConsume+"\n");
			hostsStatus += (allPeriodsConsume+" "+normalPeriodConsume+" "+egoistPeriodConsume+"\n");
			hostsStatus += (timeOn.get(i) +" " +normalTime.get(i)+" " +selfishTime.get(i) + "\n");
			hostsStatus += discharges.get(i) + "\n";			
			//tempo e consumo total em cada intevalo
			
			hostsStatus += "\n";
			
			
			hostsStatusList[i] = hostsStatus;
		}
		write("Hosts Average Energy Consume/s: " + geralMeanAllPeriodsConsume/hostsSize);
		write("Hosts Average normal Energy Consume/s: "+geralMeanNormalPeriodConsume/hostsSize);
		write("Hosts Average  Selfish Period Consume/s: "+ geralMeanEgoistPeriodConsume/hostsSize);
		write("");
		write("Hosts Average Energy Consume: "+ geralallPeriodsConsume/hostsSize);
		write("Hosts Average Normal Energy Consume: "+ geralNormalPeridConsume/hostsSize);
		write("Hosts Average Selfish Energy Consume: "+ geralEgoistPeridConsume/hostsSize);
		write("");
		
			
		String legenda = "Description:\n"+
						 "host number\n"+
				         "average energy consumption of all periods  - average energy consumption of Normal periods  -  average energy consumption of all selfish periods\n"+
				         "energy consumption of all periods  -  energy consumption of Normal periods  -  energy consumption of selfish periods\n"+
				         "Active total time  -  Normal Active Time  -  Selfish Active Time\n"+
				         "Normal proportion of time  -  Selfish proportion of time\n"+
				         "Number of Recharges \n\n";
		write(legenda);	
		
		for(String s : hostsStatusList) {
			write(s);			
		}
	super.done();
	}
}
