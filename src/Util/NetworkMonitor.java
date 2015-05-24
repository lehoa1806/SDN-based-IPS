package Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;

import IDSmain.IDSmain;
import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;


public class NetworkMonitor implements Runnable {
	static Queue<Packet> packets = new LinkedList<Packet>();
	public static long throughput;
	public boolean isRunning = true;
	public static float throughputonKbps;
	public NetworkMonitor(){
		this.throughput = 0;
	}
	public static void main(String[] args) {}
		
	@Override
	public void run() {
		while(isRunning){

			List<Packet> tempQackets = IDSmain.packetForMonitor.getRawPacket();
			throughput = 0;
			for (Packet p : tempQackets){
                throughput = throughput + p.len;
            }
			throughputonKbps = (float) (throughput*8/(0.3*1024));
			IDSmain.throughput.addOrUpdate(new Millisecond(), throughputonKbps);

			try {
				Thread.sleep(290);
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
