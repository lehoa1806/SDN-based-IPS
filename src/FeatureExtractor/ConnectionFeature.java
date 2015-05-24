/**
 * This class define the IDS Connection Feature 
 * contain 13 real time IDS feature and id of connection.
 */

package FeatureExtractor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ConnectionFeature {
	
	private Queue<RealTimeFeature> ConnectionQueue2Sec = new LinkedList<RealTimeFeature>();
	
	public ConnectionFeature(){}
	
	/**
	 * Construction
	 * @param connectionKey
	 * @param realTimeFeature
	 */

	
//---------------------Method of ConnectionFeature--------------------------------------------------	
	public synchronized boolean isEmpty(){
		return this.ConnectionQueue2Sec.isEmpty();
	}
	
	public synchronized void addConnection(RealTimeFeature realTimeFeature){
		this.ConnectionQueue2Sec.add(realTimeFeature);
//		this.printQueue2s();
		if(this.ConnectionQueue2Sec.size() > 100){
			this.ConnectionQueue2Sec.remove();
		}
	}
	
	public synchronized RealTimeFeature retrieveFeature(){return this.ConnectionQueue2Sec.peek();}
	
	public synchronized void removeFeature(){this.ConnectionQueue2Sec.poll();}
	
	public synchronized List<RealTimeFeature> getListRealTimeFeature(){
		List<RealTimeFeature> realTimeFeatures = new ArrayList<RealTimeFeature>(this.ConnectionQueue2Sec);
		return realTimeFeatures;
	}
	
	public synchronized int getQueueLength(){return this.ConnectionQueue2Sec.size();}
	
//--------------------------------------------------------------------------------------------------

	public synchronized void printQueue2s(){
		System.out.println();
		for(RealTimeFeature e:this.ConnectionQueue2Sec){
			System.out.println("ipSrc: "+ e.getSrcIp()+ " portSrc: " + e.getSrcPort()+ " ipDst:" +e.getDstIp()
								+ " portDst: " + e.getDstPort()
								+ " numSrc: " + e.getTimeBaseFeature().getNumSrc()
								+ " numDst: "+ e.getTimeBaseFeature().getNumDst()
								+ " numSrcPort: " + e.getTimeBaseFeature().getNumSrcSamePort()
								+ " numSrcDiffPort: " + e.getTimeBaseFeature().getNumSrcDiffPort());
		}
	}
}
