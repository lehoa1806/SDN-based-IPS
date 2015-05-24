/**
 * This class define Queue containing IDS Feature Entry before they are processed in Decision Tree.
 */
package FeatureExtractor;

import java.util.LinkedList;
import java.util.Queue;

public class IDSFeatureEntry {
	
	/**
	 * Queue of IDS Feature Entry
	 */
	private Queue<RealTimeFeature> connectionFeatures = new LinkedList<RealTimeFeature>();
	/**
	 * Constructor
	 */
	public IDSFeatureEntry(){}
	
	/**
	 * Add Feature Entry to Queue.
	 * @param connectionFeature
	 */
	public synchronized void addFeatureEntry(RealTimeFeature feature){
		this.connectionFeatures.add(feature);
	}

	/**
	 * pop one element from Connection Queue
	 * @return ConnectionFeature
	 */
	public synchronized RealTimeFeature getFetureEntry(){
		RealTimeFeature featureEntry = this.connectionFeatures.poll();
		return featureEntry;
	}
	
	/**
	 * Function for Test only
	 */
//	public synchronized void printQueue(){
//		for(ConnectionFeature connF : this.connectionFeatures){
//			System.out.println(connF.getKey());
//			RealTimeFeature f = connF.getFeature();
//			System.out.println("Num Packets: " + f.getNumPackets());
//		}
//	}
        public synchronized int getSize(){
               return this.connectionFeatures.size();
        }   
}       
