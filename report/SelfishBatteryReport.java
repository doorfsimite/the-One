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
	
	static int bateryLevel;
	static int hostsSize = -1;
	static private Settings settings;
	static int initEnergy;
	double simTime;
	static int endTime;

	
	public SelfishBatteryReport() {
		super.init();
		
		//Variaveis usadas
		settings = getSettings();
		settings.setNameSpace(core.SimScenario.SCENARIO_NS);
		bateryLevel = settings.getInt(core.SimScenario.SELFISH_BY_BATTERY);
		endTime = settings.getInt(core.SimScenario.END_TIME_S);

		//Procura a energia inicial de algum grupo (ultimo setado para todos)
		settings.setNameSpace(core.SimScenario.GROUP_NS);
		try {
			if(settings.contains(routing.util.EnergyModel.INIT_ENERGY_S)) {
				initEnergy  = settings.getInt((routing.util.EnergyModel.INIT_ENERGY_S));
			}
			int groupNumber= 1;
			while(true) {
				settings.setNameSpace(core.SimScenario.GROUP_NS+String.valueOf(groupNumber));
				if(settings.contains(routing.util.EnergyModel.INIT_ENERGY_S)) {
					initEnergy = settings.getInt(routing.util.EnergyModel.INIT_ENERGY_S);					
				}
				else {
					break;
				}
				
			}
		}catch (Exception e) {}



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
				
			}
		}
		
		simTime = getSimTime();
		if (isWarmup()) {
			return; /* warmup period is on */
		}		
		
		for(DTNHost h : hosts) {
			Double value = (Double) h.getComBus().getProperty(routing.util.EnergyModel.ENERGY_VALUE_ID);
			if(value == 0.0) { //Se nao tiver energia anota o primeiro tempo
				if(lastCheck.get(h.getAddress()) != simTime - 1) {
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
	
	}

	public void lastUpdated(List<DTNHost> hosts) {//falta adicionar o ultimo periodo sem descarregar
		double lastTime = getSimTime();
		energiaRestante = new LinkedList<Double>();
		
		for(DTNHost h : hosts) {
			double energiaRemanecente = (double) h.getComBus().getProperty(routing.util.EnergyModel.ENERGY_VALUE_ID);
			energiaRestante.add(energiaRemanecente);
			
			double rechargeInterval = h.getRechargeInterval();
			double lastReportedInterval = tempoDeDescarregamento.get(h.getAddress()).getLast().getValue();
			
			
			if(lastReportedInterval + rechargeInterval < lastTime ) {//se ficou ativo antes de terminar a simulacao
				//periodo ativo
				tempoDeDescarregamento.get(h.getAddress()).add(new Tuple<Double,Double>(lastReportedInterval+rechargeInterval,lastTime));
				
				if(h.isEgoist()) {
					intervalosEgoistas.get(h.getAddress()).add(new Tuple<Double,Double>(tempoDeInicioDeEgoismo.get(h.getAddress()),lastTime));
					consumoEmPeriodosNormais.get(h.getAddress()).add( (double) ( -1*((bateryLevel/100)-1) )  * initEnergy);
					
					consumoEmPeriodosEgoistas.get(h.getAddress()).removeLast();
					consumoEmPeriodosEgoistas.get(h.getAddress()).offerLast(((-1*((bateryLevel/100)-1) )*initEnergy)-(initEnergy-energiaRemanecente));
					
				}
				else{
					consumoEmPeriodosNormais.get(h.getAddress()).add(initEnergy - energiaRemanecente);	
				}
				
			}
		}
	}

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
			if(tempoDeDescarregamento.get(i).getLast().getValue() == endTime) {
				usedEnergy.set(i,(double)((tempoDeDescarregamento.get(i).size()-1) * initEnergy) + (initEnergy -energiaRestante.get(i)));
			}
			else {
				usedEnergy.set(i,(double)(tempoDeDescarregamento.get(i).size() * initEnergy));
			}
			double egoistPeriodsSum = 0;
			for(int t = 0; t <intervalosEgoistas.get(i).size(); t++){
				egoistPeriodsSum += intervalosEgoistas.get(i).get(t).getValue() - intervalosEgoistas.get(i).get(t).getKey();
			}
			
			double activePeriodsSum = 0;
			for(int t = 0; t <tempoDeDescarregamento.get(i).size(); t++){
				activePeriodsSum += tempoDeDescarregamento.get(i).get(t).getValue() - tempoDeDescarregamento.get(i).get(t).getKey();
			}

			
			double selfishCosume = 0;
			for(int t = 0; t <consumoEmPeriodosEgoistas.get(i).size(); t++){
				selfishCosume += consumoEmPeriodosEgoistas.get(i).get(t) ;
			}

			double egoistPeriodConsume = selfishCosume;
			double normalPeriodConsume = usedEnergy.get(i) - selfishCosume;
			double allPeriodsConsume = usedEnergy.get(i);

			double meanEgoistPeriodConsume = egoistPeriodConsume /egoistPeriodsSum;
			double meanNormalPeriodConsume = normalPeriodConsume/(activePeriodsSum - egoistPeriodsSum);
			double meanAllPeriodsConsume = allPeriodsConsume/activePeriodsSum;
			
			
			geralEgoistPeridConsume += egoistPeriodConsume;
			geralNormalPeridConsume += normalPeriodConsume;
			geralallPeriodsConsume += allPeriodsConsume;
			
			varGeralEgoistPeridConsume += egoistPeriodConsume;
			varGeralNormalPeridConsume += normalPeriodConsume;
			varGeralallPeriodsConsume += allPeriodsConsume;
			
			geralMeanEgoistPeriodConsume += meanEgoistPeriodConsume;
			geralMeanNormalPeriodConsume += meanNormalPeriodConsume;
			geralMeanAllPeriodsConsume += meanAllPeriodsConsume;
			
			double meanNormalActiveTime = (activePeriodsSum-egoistPeriodsSum)/activePeriodsSum;
			double meanSelfishTime = egoistPeriodsSum / activePeriodsSum;
			
			hostsStatus += ("HOST: "+i+"\n");
			hostsStatus += (meanAllPeriodsConsume+" "+meanNormalPeriodConsume+" "+meanEgoistPeriodConsume+"\n");
			hostsStatus += (allPeriodsConsume+" "+normalPeriodConsume+" "+egoistPeriodConsume+"\n");
			hostsStatus += (activePeriodsSum +" " +(activePeriodsSum-egoistPeriodsSum)+" " + egoistPeriodsSum + "\n");
			hostsStatus += (meanNormalActiveTime + " " + meanSelfishTime+"\n");
			
			//tempo e consumo total em cada intevalo
			
			hostsStatus += tempoDeDescarregamento.get(i).size() +" ";
			
			for(int t = 0; t < tempoDeDescarregamento.get(i).size(); t++) {
				if(tempoDeDescarregamento.get(i).get(t).getValue() == endTime) {
					hostsStatus += initEnergy - energiaRestante.get(i) + " ";
				}
				else {
					hostsStatus += initEnergy + " ";
				}
				
				hostsStatus += tempoDeDescarregamento.get(i).get(t) + " ";
			}
			hostsStatus += "\n";
			
			//Tempo e consumo total de cada intervalo no modo normal de funcionamento
			
			hostsStatus += tempoDeDescarregamento.get(i).size() +" ";
			
			for(int t = 0; t < tempoDeDescarregamento.get(i).size(); t++) {
				hostsStatus += consumoEmPeriodosNormais.get(i).get(t) + " ";
				try {
					hostsStatus += tempoDeDescarregamento.get(i).get(t).getKey()+":"+intervalosEgoistas.get(i).get(t).getKey() + " ";
				}catch(Exception e) {
					hostsStatus += tempoDeDescarregamento.get(i).get(t) +" ";
				}
				
			}
			hostsStatus += "\n";
			
			//Tempo e consumo total de cada intervalo no modo egoista de funcionamento
			
			hostsStatus += intervalosEgoistas.get(i).size() +" ";
			for(int t = 0; t < intervalosEgoistas.get(i).size(); t++) {
				hostsStatus += consumoEmPeriodosEgoistas.get(i).get(t) + " ";
				hostsStatus += intervalosEgoistas.get(i).get(t) + " ";
			}
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
				         "Total Recharges and consumed energy for each active time\n"+
				         "Total Recharges and consumed energy for each normal active time\n"+
				         "Total Recharges and consumed energy for each selfish active time\n";

		write(legenda);	
		
		for(String s : hostsStatusList) {
			write(s);			
		}
	super.done();
	}
}
