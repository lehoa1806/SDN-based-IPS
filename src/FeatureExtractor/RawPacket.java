/**
 * This class define a Queue contain all of packet that captured in IDS class.
 */
package FeatureExtractor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import jpcap.packet.Packet;

public class RawPacket {
	
	//------------------Raw Packet Queue--------------------------
	private Queue<Packet> packets = new LinkedList<Packet>();
	
	/**
	 * Constructor
	 */
	public RawPacket(){}
	
	/**
	 * add packet to packet queue.
	 * @param packet
	 */
	public synchronized void addRawPacket(Packet packet){
		this.packets.add(packet);
	}

	/**
	 * get all of packets in packet queue and clear queue.
	 * @return List<Packet>
	 */
	public synchronized List<Packet> getRawPacket(){
		List<Packet> packet = new ArrayList<Packet>(this.packets);
		this.packets.clear();
		return packet;
	}
	public synchronized Queue<Packet> getthisRawPacket(){
		return packets;
	}
}	
