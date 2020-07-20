/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package core;

public interface ActiveListener {

	public void active(DTNHost host);

	public void deactive(DTNHost host);	
}
