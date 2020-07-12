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
import core.SimError;
import core.SimScenario;
import routing.MessageRouter;
import routing.util.EnergyModel;
import core.UpdateListener;
import util.Tuple;

/**
 * Report information about all delivered messages. Messages created during
 * the warm up period are ignored.
 * For output syntax, see {@link #HEADER}.
 */
public class EgoismReport extends Report implements UpdateListener{


	static private ArrayList<LinkedList<Tuple<Double,Double>>> tempoDeDescarregamento;
	static int hostsSize;
	int egoistLevel;
	static private ArrayList<Double> lastCheck;
	static private Settings settings;
	static ArrayList<Boolean> isEgoist;
	static ArrayList<Integer> egoistDelivery;
	static ArrayList<Double> usedEnergy;
	static ArrayList<Double> tempoDeInicioDeAtivação;
	static LinkedList<Double> energiaRestante;
	static 	ArrayList<Double> activeTime;
	static int initEnergy;
	double simTime;
	static int endTime;
	/**
	 * Constructor.
	 */

	public EgoismReport() {
		super.init();
		
		//Variaveis usadas
		settings = getSettings();
		tempoDeInicioDeAtivação = new ArrayList<Double>();
		tempoDeDescarregamento = new ArrayList<LinkedList<Tuple<Double,Double>>>(hostsSize);
		lastCheck = new ArrayList<Double>(hostsSize);
		egoistDelivery = new ArrayList<Integer>();
		activeTime = new ArrayList<Double>();
		usedEnergy = new ArrayList<Double>();
		
		for(int i =0; i < hostsSize; i++) {
			lastCheck.add(i,0.0);
			tempoDeDescarregamento.add(i,new LinkedList<Tuple<Double,Double>>());
			egoistDelivery.add(0);
			tempoDeInicioDeAtivação.add(0.0);
			usedEnergy.add(0.0);
			activeTime.add(0.0);
		}
		settings.setNameSpace(core.SimScenario.SCENARIO_NS);
		egoistLevel = settings.getInt(core.SimScenario.SELFISH_LV);
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
		if(isEgoist == null) {
			isEgoist = new ArrayList<Boolean>();
			for(DTNHost h : hosts) {
				isEgoist.add(h.getAddress(), h.isEgoist());
			}
			hostsSize = hosts.size();
		}
		
		simTime = getSimTime();
		if (isWarmup()) {
			return; /* warmup period is on */
		}		
		
		for(DTNHost h : hosts) {
			
			Double value = (Double) h.getComBus().getProperty(routing.util.EnergyModel.ENERGY_VALUE_ID);
			if(value == 0.0) {
				if(lastCheck.get(h.getAddress()) != simTime - 1) {
					tempoDeDescarregamento.get(h.getAddress()).add(new Tuple<Double,Double>(tempoDeInicioDeAtivação.get(h.getAddress()),simTime));
				}
				else {
					tempoDeInicioDeAtivação.set(h.getAddress(),simTime);
				}
				lastCheck.set(h.getAddress(),simTime);
			}
			else {
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
			
			}
		}
	}

	@Override
	public void done() {
		int egoistNodesProportion = egoistLevel * hostsSize / 100 ;
		int egoistProportion = ((egoistNodesProportion*100)/hostsSize);
		String header = "Egoism Proportion: " + egoistProportion+ " (~"+this.egoistLevel +"%)\nHosts: "+ this.hostsSize + " ";

		settings.setNameSpace(core.SimScenario.SCENARIO_NS);
		int egoistCount = 0;
		for (Boolean mode : isEgoist) {
			if(mode == true) {
				egoistCount += 1;
			}
		}
		header += "Egoist Nodes: "+ egoistCount;
		
		write(header);			
		
		
		LinkedList <Double> meanConsumeEnergy = new LinkedList<Double>();
		String hostsStatus = "";
		double egoistConsume = 0;
		double meanEgoistConsume = 0;
		double normalConsume = 0;
		double meanNormalConsume = 0;
		double meanRechargeTime = 0;
		double meanEgoistRechargeTime = 0;
		String mode = "";
		
		for(int i = 0; i < this.hostsSize ; i ++) {
			
			
			if(tempoDeDescarregamento.get(i).getLast().getValue() == endTime) {
				usedEnergy.set(i,(double)((tempoDeDescarregamento.get(i).size()-1) * initEnergy) + (initEnergy -energiaRestante.get(i)));
			}
			else {
				usedEnergy.set(i,(double)(tempoDeDescarregamento.get(i).size() * initEnergy));
			}
			
			double activePeriodsSum = 0;
			for(int t = 0; t <tempoDeDescarregamento.get(i).size(); t++){
				activePeriodsSum += tempoDeDescarregamento.get(i).get(t).getValue() - tempoDeDescarregamento.get(i).get(t).getKey();
			}
			
			
			if(isEgoist.get(i) == true) {
				
			}
			else {
				
			}
			meanConsumeEnergy.add(usedEnergy.get(i)/ activePeriodsSum);
		
			if(isEgoist.get(i) == true) {
				mode = "Selfish";
				meanEgoistConsume+=meanConsumeEnergy.get(i);
				egoistConsume += usedEnergy.get(i);
				meanRechargeTime += tempoDeDescarregamento.get(i).size();
			}
			else {
				mode = "Normal ";
				meanNormalConsume+=meanConsumeEnergy.get(i);
				normalConsume +=  usedEnergy.get(i);
				meanEgoistRechargeTime += tempoDeDescarregamento.get(i).size();
			}
			
			
			hostsStatus += ("HOST: "+i+" "+mode+"\n");
			hostsStatus += (meanConsumeEnergy.get(i)+" "+usedEnergy.get(i)+" "+activeTime.get(i)+"\n");

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
			
			
		}
		String legenda = "Host number  -  is Egoist?\n"+
						 "Mean consume energy/s  -  Consumed Energy  -  Ative Time\n"+
						 "Total Recharges and periods with energy\n";
		
		if(egoistCount>0) {
			write("Selfish Hosts | Mean Energy Consume/s: " + meanEgoistConsume/egoistCount);
			write("Selfish Hosts | Energy Consume: " + egoistConsume/egoistCount);
			write("Selfish hosts | Average recharge time : "+meanEgoistRechargeTime);
		}
		else {
			write("No egoist Host Data");
		}
		write("Normal hosts | mean energy consume: " +( meanNormalConsume/(hostsSize - egoistCount)));
		write("Normal hosts | energy consume: " +( normalConsume/(hostsSize - egoistCount)));
		write("Normal hosts | Average recharge time : "+meanEgoistRechargeTime);
		
		
		write(legenda);	
		
		write(hostsStatus);
		
	super.done();
	}
}
