/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package core;

import interfaces.TitForTatInterface;
import routing.MessageRouter;

/**
 * A constant bit-rate connection between two DTN nodes.
 */
public class TFTConnection extends Connection {
	private int speed;
	//Diferenca de trafego entre os hosts ( se negativo o nó destino enviou mais bytes do que o no de origem e se positivo entao o no de origem
	//mandou mais bytes
	private double traffic;
	private double transferDoneTime;
	
	private boolean insentiveFrom;
	private boolean insentiveTo;

	/**
	 * Creates a new connection between nodes and sets the connection
	 * state to "up".
	 * @param fromNode The node that initiated the connection
	 * @param fromInterface The interface that initiated the connection
	 * @param toNode The node in the other side of the connection
	 * @param toInterface The interface in the other side of the connection
	 * @param connectionSpeed Transfer speed of the connection (Bps) when 
	 *  the connection is initiated
	 */
	public TFTConnection(DTNHost fromNode, NetworkInterface fromInterface, 
			DTNHost toNode,	NetworkInterface toInterface, int connectionSpeed) {
		super(fromNode, fromInterface, toNode, toInterface);
		this.speed = connectionSpeed;
		this.transferDoneTime = 0;
		this.traffic = 0;

	}

	/**
	 * Sets a message that this connection is currently transferring. If message
	 * passing is controlled by external events, this method is not needed
	 * (but then e.g. {@link #finalizeTransfer()} and 
	 * {@link #isMessageTransferred()} will not work either). Only a one message
	 * at a time can be transferred using one connection.
	 * @param from The host sending the message
	 * @param m The message
	 * @return The value returned by 
	 * {@link MessageRouter#receiveMessage(Message, DTNHost)}
	 */
	public int startTransfer(DTNHost from, Message m) { //Alterado para suportar generosidade e contrição
		assert this.msgOnFly == null : "Already transferring " + 
			this.msgOnFly + " from " + this.msgFromNode + " to " + 
			this.getOtherNode(this.msgFromNode) + ". Can't " + 
			"start transfer of " + m + " from " + from;

		boolean enviaMensagem = false;
		boolean HostCooperativo = false;


		//verifica o tit for tat da conexao
		
		
		if(!from.isEgoist()) {//Se a origem nao for egoista entao esta disposta a enviar mensagens normalmente
			if(getOtherNode(from).isEgoist()) { //Se o destino for egoista entao o remetente envia mensagem se tiver generosidade

				double reciprocalMessagesSize = haveMessagesToSender(getOtherNode(from),from);
				/** Se o no egoista tiver mensagens para o FROM e nao tiver feito serviço suficiente
				 * então está incentivado a dar generosidade*/
				if(!checkIfPerformedWork(getOtherNode(from),reciprocalMessagesSize)) {
					if(from == this.fromNode) { //Reconhece o sentido do trafego
						if(this.traffic + m.getSize() > TitForTatInterface.e) {//se o trafego de mensagens se tornar maior do que o limite de generosidade
							//chegou ao limite da generosidade do receptor
							enviaMensagem = false;//ENTAO NAO ENVIA A MENSAGEM
						}
						else {//Se ainda resta generosidade, então adiciona no trafego os bytes usados e envia a mensagem
							enviaMensagem = true;//ENVIA MENSAGEM
						}	
					}
					else {
						if(this.traffic - m.getSize() < TitForTatInterface.e*-1) {//se o trafego de mensagens se tornar maior do que o limite de generosidade
							//chegou ao limite da generosidade do receptor
							enviaMensagem = false;//ENTAO NAO ENVIA A MENSAGEM
						}
						else {//Se ainda resta generosidade, então adiciona no trafego os bytes usados e envia a mensagem
							enviaMensagem = true;//ENVIA MENSAGEM
						}
					}
				}
			}
			else {//Se ambos nao forem egoista entao ignora generosidade e contrição e envia a mensagem
				enviaMensagem = true;
				HostCooperativo= true;
			}
		}

		/**Toda vez que um no egoista entrega mensagem entao ele fica contrito e volta ao trafico normal**/
		else {//Se o emissor for egoista então vai querer enviar apenas as mensagens que possui no buffer
			if(getOtherNode(from).isEgoist()) { //Se o destino for egoista entao o remetente envia mensagem se tiver generosidade
				double reciprocalMessagesSize = haveMessagesToSender(getOtherNode(from),from);
				/** Se o destinatario egoista tiver mensagens para o FROM e nao tiver feito serviço suficiente
				 * então está incentivado a dar generosidade*/
				if( !checkIfPerformedWork(getOtherNode(from),reciprocalMessagesSize)) {
					if(from == this.fromNode) { //Reconhece o sentido do trafego
						if(this.traffic + m.getSize() > TitForTatInterface.e) {//se o trafego de mensagens se tornar maior do que o limite de generosidade
							//chegou ao limite da generosidade do receptor
							enviaMensagem = false;//ENTAO NAO ENVIA A MENSAGEM
						}
						else {//Se ainda resta generosidade, então adiciona no trafego os bytes usados e envia a mensagem
							enviaMensagem = true;//ENVIA MENSAGEM
						}	
					}
					else {
						if(this.traffic - m.getSize() < TitForTatInterface.e*-1) {//se o trafego de mensagens se tornar maior do que o limite de generosidade
							//chegou ao limite da generosidade do receptor
							enviaMensagem = false;//ENTAO NAO ENVIA A MENSAGEM
						}
						else {//Se ainda resta generosidade, então adiciona no trafego os bytes usados e envia a mensagem
							enviaMensagem = true;//ENVIA MENSAGEM
						}
					}
				}
			}
			else {//Se o no de origem for egoista e o destino for normal entao ele so recebe as mensagens do no egoista
				  //e o tráfico e contado
				if(from == this.fromNode) { //Reconhece o sentido do trafego
					if(this.traffic + m.getSize() > TitForTatInterface.e) {//se o trafego de mensagens se tornar maior do que o limite de generosidade
						//chegou ao limite da generosidade do receptor
						enviaMensagem = false;//ENTAO NAO ENVIA A MENSAGEM
					}
					else {//Se ainda resta generosidade, então adiciona no trafego os bytes usados e envia a mensagem
						enviaMensagem = true;//ENVIA MENSAGEM
					}	
				}
				else {
					if(this.traffic - m.getSize() < TitForTatInterface.e*-1) {//se o trafego de mensagens se tornar maior do que o limite de generosidade
						//chegou ao limite da generosidade do receptor
						enviaMensagem = false;//ENTAO NAO ENVIA A MENSAGEM
					}
					else {//Se ainda resta generosidade, então adiciona no trafego os bytes usados e envia a mensagem
						enviaMensagem = true;//ENVIA MENSAGEM
					}
				}
			}
			
		}

		if(enviaMensagem) {//Apenas tenta por no buffer se realmente for mandar
			this.msgFromNode = from;
			Message newMessage = m.replicate();
			int retVal = getOtherNode(from).receiveMessage(newMessage, from);
			if (retVal == MessageRouter.RCV_OK) {
				if(!HostCooperativo) {
					HostCooperativo = false;
					if(from == this.fromNode) { //Reconhece o sentido do trafego
						this.traffic += m.getSize();							
					}
					else {
						this.traffic -= m.getSize();							
					}
				}
				this.msgOnFly = newMessage;
				this.transferDoneTime = SimClock.getTime() + 
				(1.0*m.getSize()) / this.speed;
			}

				
			return retVal;
		}

		return -10;//Nao enviou porque o destino é egoista e nao quis aceitar a mensagem

	}
	
	/**No de origem (egoista) verifica se trabalhou o suficiente para n exigir generosidade dele caso tenha trabalhado mais.
	 * Entao verifica se o trabalho feito foi suficiente para satisfazer a demanda da mensagem que precisa enviar  **/
	public boolean checkIfPerformedWork(DTNHost from, double mSize) {

		if(from == this.fromNode) {
			return this.traffic + mSize <= 0.0;
		}
		else {
			return this.traffic -mSize >= 0.0;
		}
	}
	
	//Zera o trafego e consequentemente reseta a generosidade de ambos os nos
	public void contrition() {
		this.traffic = 0.0;
	}

	public double haveMessagesToSender(DTNHost destiny, DTNHost sender){
		return destiny.haveDeliverableMessages(sender);
	}

	/**
	 * Aborts the transfer of the currently transferred message.
	 */
	public void abortTransfer() {
		assert msgOnFly != null : "No message to abort at " + msgFromNode;
		getOtherNode(msgFromNode).messageAborted(this.msgOnFly.getId(),
				msgFromNode,getRemainingByteCount());
		clearMsgOnFly();
		this.transferDoneTime = 0;
	}

	/**
	 * Gets the transferdonetime
	 */
	public double getTransferDoneTime() {
		return transferDoneTime;
	}
	
	/**
	 * Returns true if the current message transfer is done.
	 * @return True if the transfer is done, false if not
	 */
	public boolean isMessageTransferred() {
		return getRemainingByteCount() == 0;
	}

	/**
	 * returns the current speed of the connection
	 */
	public double getSpeed() {
		return this.speed;
	}

	/**
	 * Returns the amount of bytes to be transferred before ongoing transfer
	 * is ready or 0 if there's no ongoing transfer or it has finished
	 * already
	 * @return the amount of bytes to be transferred
	 */
	public int getRemainingByteCount() {
		int remaining;

		if (msgOnFly == null) {
			return 0;
		}

		remaining = (int)((this.transferDoneTime - SimClock.getTime()) 
				* this.speed);

		return (remaining > 0 ? remaining : 0);
	}

	/**
	 * Returns a String presentation of the connection.
	 */
	public String toString() {
		return super.toString() + (isTransferring() ?  
				" until " + String.format("%.2f", this.transferDoneTime) : "");
	}

}
