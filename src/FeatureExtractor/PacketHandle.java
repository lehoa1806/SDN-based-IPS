/**
 * Class handle packets.
 */

package FeatureExtractor;

import IDSmain.IDSmain;
import TcpdumpExtractor.TcpdumpDialog;
import TcpdumpExtractor.TcpdumpExtractor;
import Util.tcpdumpGetfile;
import Util.tcpdumpPacketFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.mina.filter.firewall.Subnet;









import javax.swing.JFrame;
import javax.swing.JOptionPane;

import weka.classifiers.trees.J48;
import weka.core.FastVector;
import weka.core.Instances;
import jpcap.JpcapCaptor;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

public class PacketHandle implements Runnable {

//------------------------var----------------------------------------	
	private Map<String, TempConn> tempConnMap;
	Thread tCheckConn;
	private int timeoutTCP = 2000;
	private int timeoutUDPICMP = 3000;
	
	private boolean isRunning = false;
    public static RawPacket TcpdumpSet;
    private FastVector IDSRealtimeAttributeSet;

    public static String atktype;
    
    public static DateFormat timeFormat = new SimpleDateFormat("HH-mm-ss-MMM-dd-yyyy");
    
    public static J48 tree;
	private TcpdumpExtractor tcpdumpExtractor;
	private IDSFeatureEntry featureEntries;

	
//-------------------------------------------------------------------	

	private int checknotUDP=0;
//------------------------------------------------------------------	
	
	/**
	 * Constructor
	 * @param packets
	 * @param timeStart
	 * @throws UnknownHostException 
	 */
	public PacketHandle() throws UnknownHostException {
		this.tempConnMap = new HashMap<String, TempConn>();
		tcpdumpExtractor = new TcpdumpExtractor();
		featureEntries  = IDSmain.featureEntries;
        IDSRealtimeAttributeSet = IDSmain.IDSRealtimeAttributeSet;
        isRunning = true;

	}
	
	
	@Override
	public void run() {
		while(isRunning){
			if((IDSmain.runmode == 0) || (IDSmain.runmode == 1)){
				if(Thread.interrupted()){
					tCheckConn.interrupt();
					break;
				}
				List<Packet> packets = IDSmain.rawPacket.getRawPacket();
				//--------------parse packet info----------------------
				for(Packet p: packets){
					praseIPPacketInfo(p);
				}
				
				this.checkConn();
	//			//------------------------------------------------------
				try {
					Thread.sleep((long) 100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if(IDSmain.runmode == 3){
		    	 File f = new File("Train.arff");
		         if (f.exists()) {
		            BufferedReader train_file;
					try {
						train_file = new BufferedReader(new FileReader("Train.arff"));
					    IDSmain.IDSInstances = new Instances(train_file);
					    train_file.close();	  
					    BufferedWriter writer ;
					    writer = new BufferedWriter(new FileWriter("Train-" + timeFormat.format(new Date((System.currentTimeMillis()))) + ".arff"));
						writer.write(IDSmain.IDSInstances.toString());
						writer.close();        
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		         }
		    	JpcapCaptor captor;
				try {
					captor = JpcapCaptor.openFile(TcpdumpDialog.fileAddress);
					long sys_time = 0;
					@SuppressWarnings("resource")
					PrintWriter log_f = new PrintWriter("logfile.txt", "UTF-8");
					tcpdumpPacketFilter tcpinstace = tcpdumpGetfile.dumpList.peek();
					String newString4test = tcpinstace.newkeyString;
					String newString4test1 = tcpinstace.newkeyString;
					while(tcpdumpGetfile.dumpList.size()>0){
						Packet packet=captor.getPacket();
					    if(Thread.interrupted())
							break;
					   
					    if(packet==null || packet==Packet.EOF) {
					    	System.out.println("EOF");
					    	this.checkConnTCPdump(sys_time + 3000);
					    	Thread.sleep(2000);
 					    	if (featureEntries.getSize() != 0) {
					    		tcpdumpExtractor.runTcpdump(tcpinstace.atkType);
					    	}
					        try{	
				                BufferedWriter writer ;
				                writer = new BufferedWriter(new FileWriter("Train.arff"));
//				                System.out.println("print IDS instances");
								writer.write(IDSmain.IDSInstances.toString());
								writer.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					        tcpdumpGetfile.dumpList.clear();
					    	break;
					    }
					    else{
							if (packet.sec > tcpinstace.stopTime){
								while ((packet.sec > tcpinstace.stopTime) && (tcpdumpGetfile.dumpList.size() > 0)){
									tcpinstace = tcpdumpGetfile.dumpList.poll();
								}
								if ((tcpdumpGetfile.dumpList.size() == 0) && (packet.sec > tcpinstace.stopTime)){
									break;
								}
							}
							
							
							if (packet.sec<tcpinstace.startTime){
								continue;
							}
							else if ((packet.sec >= tcpinstace.startTime) && (packet.sec <= tcpinstace.stopTime)){
								if(packet instanceof IPPacket){
									if (!newString4test.equals(tcpinstace.newkeyString)){
										newString4test = tcpinstace.newkeyString;
										log_f.println(newString4test);
									}
									String key1 = ((IPPacket) packet).src_ip.toString() + ((IPPacket) packet).dst_ip.toString();
									String key2 = ((IPPacket) packet).dst_ip.toString() +((IPPacket) packet).src_ip.toString();
									if(tcpinstace.key.equals(key1)||tcpinstace.key.equals(key2)){
										sys_time = (packet.sec*1000000+packet.usec)/1000;
								    	praseIPPacketInfo(packet);
								    	this.checkConnTCPdump(sys_time);
								    	if (!newString4test1.equals(tcpinstace.newkeyString)){
											newString4test1 = tcpinstace.newkeyString;
											log_f.println(newString4test1);
										}
			 					    	if (featureEntries.getSize() != 0) {
								    		tcpdumpExtractor.runTcpdump(tcpinstace.atkType);
			 					    	}
									}
								}
								else {
									continue;
								}
								
							}
					    }
					}
					captor.close();
			    	JFrame mframe = new JFrame();
			    	JOptionPane.showMessageDialog(mframe,"Extraction complete !");
			    	if(!mframe.isActive()){
				    	if(TcpdumpDialog.mTcpdumpThread != null) TcpdumpDialog.mTcpdumpThread.interrupt();
//				    	TcpdumpDialog.nTcpdumpDialog.dispose();
				    	TcpdumpDialog.mTcpdumpThread = null;
				    	isRunning = false;
				    	break;

			    	}

				} catch (IOException | InterruptedException  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else {

		    	 File f = new File("Train.arff");
		    	 if (f.exists()) {
		            BufferedReader train_file;
					try {
						train_file = new BufferedReader(new FileReader("Train.arff"));
					    IDSmain.IDSInstances = new Instances(train_file);
					    train_file.close();	  
					    BufferedWriter writer ;
					    writer = new BufferedWriter(new FileWriter("Train-" + timeFormat.format(new Date((System.currentTimeMillis()))) + ".arff"));
						writer.write(IDSmain.IDSInstances.toString());
						writer.close();        
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		         }
		         		    	
		        JpcapCaptor captor;
				try {
					captor = JpcapCaptor.openFile(TcpdumpDialog.fileAddress);
					long sys_time = 0;
					while(true){
					    Packet packet=captor.getPacket();
		                System.out.println(packet.sec);

					    if(Thread.interrupted())
							break;
					    if(packet==null || packet==Packet.EOF) {
					    	this.checkConnTCPdump(sys_time + 3000);
					    	Thread.sleep(2000);
					    	if (featureEntries.getSize() != 0) {
					    		tcpdumpExtractor.runTcpdump(IDSmain.atktype);
					    	}
					        try{	
				                BufferedWriter writer ;
				                writer = new BufferedWriter(new FileWriter("Train.arff"));
				                System.out.println("print IDS instances");
								writer.write(IDSmain.IDSInstances.toString());
								writer.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    	break;
					    }
					    else {
					    	sys_time = (packet.sec*1000000+packet.usec)/1000;
					    	praseIPPacketInfo(packet);
					    	this.checkConnTCPdump(sys_time);
					    	if (featureEntries.getSize() != 0) {
					    		tcpdumpExtractor.runTcpdump(IDSmain.atktype);
					    	}
					    }           
					}
					captor.close();
			    	JFrame mframe = new JFrame();
			    	JOptionPane.showMessageDialog(mframe,"Extraction complete !");
			    	if(!mframe.isActive()){
				    	if(TcpdumpDialog.mTcpdumpThread != null) TcpdumpDialog.mTcpdumpThread.interrupt();
//				    	TcpdumpDialog.nTcpdumpDialog.dispose();
				    	TcpdumpDialog.mTcpdumpThread = null;
				    	isRunning = false;
				    	break;

			    	}

				} catch (IOException | InterruptedException  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Parse packet to 13 IDS feature.
	 * @param tcp
	 */
	private void praseIPPacketInfo(Packet p){	
		long dataLength = (long) p.len;
		if(p instanceof IPPacket){
			IPPacket packet = (IPPacket)p;
			if(packet.protocol == IPPacket.IPPROTO_TCP){
				TCPPacket tcp = (TCPPacket)packet;
				String key1 = tcp.src_ip.toString() + "/" + tcp.src_port + tcp.dst_ip.toString()  + "/" + tcp.dst_port;
				String key2 = tcp.dst_ip.toString() + "/" + tcp.dst_port + tcp.src_ip.toString() +  "/" + tcp.src_port;
				String key = null;
				TempConn tempConnection = null;
				RealTimeFeature realTimeFeature;
				
				if(isContainKey(key1)||isContainKey(key2)){
					//---------------------Connection Exist---------------------------------
					if(isContainKey(key1)){
						tempConnection = getKey(key1);
	//					realTimeFeature = tempConnQueue.get(key1).realTimeFeature;
						key = key1;
						tempConnection.realTimeFeature.addSrcBytes(dataLength);
						tempConnection.realTimeFeature.addNumPktSrc();
						this.removeConnection(key);
					}
					else{ 
						tempConnection = getKey(key2);
	//					realTimeFeature = tempConnQueue.get(key2).realTimeFeature;
						key = key2;
						tempConnection.realTimeFeature.addDstBytes(dataLength);
						this.removeConnection(key);
						tempConnection.realTimeFeature.addNumPktDst();
					}
					
				}
				else{  
					
					//---------------------New Connection ----------------------------------
					
						realTimeFeature = new RealTimeFeature();
						realTimeFeature.setStartTime(tcp.sec, tcp.usec);
						realTimeFeature.addSrcBytes(dataLength);
						realTimeFeature.setSrcPort(tcp.src_port);
						realTimeFeature.setDstPort(tcp.dst_port);
						realTimeFeature.setProtocol("TCP");
						realTimeFeature.setSrcIp(tcp.src_ip);
						realTimeFeature.setDstIp(tcp.dst_ip);
						realTimeFeature.addNumPktSrc();
						String service = IDSmain.mapPortService.mapService(tcp.dst_port);
						if (service!= null)
							realTimeFeature.setService(service);
						else{
							realTimeFeature.setService("tcp");
//							System.out.println(key1);
						}
						key = key1;
						realTimeFeature.setKey(key);
						tempConnection = new TempConn((tcp.sec*1000000 + tcp.usec)/1000, realTimeFeature);
					
				}
				tempConnection.realTimeFeature.addNumPackets();
				if(tcp.fin) tempConnection.realTimeFeature.addNumTcpFin();
				if(tcp.ack) tempConnection.realTimeFeature.addNumTcpAck();
				if(tcp.urg) tempConnection.realTimeFeature.addNumTcpUrg();
				if(tcp.rst) tempConnection.realTimeFeature.addNumTcpReset();
				if(tcp.psh) tempConnection.realTimeFeature.addNumTcpPush();
				if(tcp.syn) tempConnection.realTimeFeature.addNumTcpSyn();
							
				if(tempConnection.realTimeFeature.getNumTcpFin()==2){
					tempConnection.realTimeFeature.setFinishConn();
					tempConnection.realTimeFeature.setStopTime(tcp.sec, tcp.usec);
					tempConnection.realTimeFeature.setConnFlag("SF");
					Thread tConnHandle = new Thread(new ConnectionHandle(tempConnection.realTimeFeature));
					tConnHandle.start();
				}
				else if(tcp.rst){
					if(tempConnection.realTimeFeature.getNumTcpSyn()==0){
						tempConnection.realTimeFeature.setConnFlag("SH");
					}
					else if(tcp.src_ip.equals(tempConnection.realTimeFeature.getSrcIp())){
						tempConnection.realTimeFeature.setConnFlag("RSTO");
	
					}
					else{
						tempConnection.realTimeFeature.setConnFlag("RSTR");
	
					}
					tempConnection.realTimeFeature.setDuration(0);
					tempConnection.realTimeFeature.setFinishConn();
					Thread tConnHandle = new Thread(new ConnectionHandle(tempConnection.realTimeFeature));
					tConnHandle.start();
				}
				this.addConnection(key, tempConnection);
			}
			else if(packet.protocol == IPPacket.IPPROTO_UDP){
				if (!(packet instanceof UDPPacket)){
					System.out.println("Packet time " + packet.sec +" " +checknotUDP++);
				}
				else {
					UDPPacket udp = (UDPPacket) packet;

			
				
				String key1 = udp.src_ip.toString()  + "/" + udp.src_port + udp.dst_ip.toString() + "/" + udp.dst_port;
				String key2 = udp.dst_ip.toString()  + "/" + udp.dst_port + udp.src_ip.toString() + "/" + udp.src_port;
				String key;
				TempConn tempConnection;
				RealTimeFeature realTimeFeature;
				if(isContainKey(key1)||isContainKey(key2)){
					if(isContainKey(key1)){
						tempConnection = getKey(key1);
						key = key1;
						tempConnection.realTimeFeature.addSrcBytes(dataLength);
						tempConnection.realTimeFeature.addNumPktSrc();
						this.removeConnection(key);
					}
					else{ 
						tempConnection = getKey(key2);
						key = key2;
						tempConnection.realTimeFeature.addDstBytes(dataLength);
						tempConnection.realTimeFeature.addNumPktDst();
						this.removeConnection(key);
					}
				}
				else{
					realTimeFeature = new RealTimeFeature();
					realTimeFeature.setStartTime(udp.sec, udp.usec);
					realTimeFeature.addSrcBytes(dataLength);
					realTimeFeature.setSrcPort(udp.src_port);
					realTimeFeature.setDstPort(udp.dst_port);
					realTimeFeature.setProtocol("UDP");
					realTimeFeature.setSrcIp(udp.src_ip);
					realTimeFeature.setDstIp(udp.dst_ip);
					realTimeFeature.addNumPktSrc();
					String service = IDSmain.mapPortService.mapService(udp.dst_port);
					if (service!= null)
						realTimeFeature.setService(service);
					else 
						realTimeFeature.setService("udp");
					key = key1;
					tempConnection = new TempConn((udp.sec*1000000 + udp.usec)/1000, realTimeFeature);
				}
				tempConnection.realTimeFeature.addNumPackets();
				tempConnection.realTimeFeature.setConnFlag("SF");
				tempConnection.realTimeFeature.setStopTime(udp.sec, udp.usec);
				tempConnection.timeHandle = (udp.sec*1000000 + udp.usec)/1000;
				this.addConnection(key, tempConnection);
	
				}
			}
			else if (packet.protocol == IPPacket.IPPROTO_ICMP){
//				System.out.println("ICMP packets");
	//			ICMPPacket icmp = (ICMPPacket)packet;
				String key1 = packet.src_ip.toString() + packet.dst_ip.toString();
				String key2 = packet.dst_ip.toString() + packet.src_ip.toString();
				String key;
				TempConn tempConnection;
				RealTimeFeature realTimeFeature;
				if(isContainKey(key1)||isContainKey(key2)){
					if(isContainKey(key1)){
						tempConnection = getKey(key1);
						key = key1;
						tempConnection.realTimeFeature.addSrcBytes(dataLength);
						tempConnection.realTimeFeature.addNumPktSrc();
						this.removeConnection(key);
					}
					else{ 
						tempConnection = getKey(key2);
						key = key2;
						tempConnection.realTimeFeature.addDstBytes(dataLength);
						tempConnection.realTimeFeature.addNumPktDst();
						this.removeConnection(key);
					}
				}
				else{
					realTimeFeature = new RealTimeFeature();
					realTimeFeature.setStartTime(packet.sec, packet.usec);
					realTimeFeature.addSrcBytes(dataLength);
					realTimeFeature.setProtocol("ICMP");
					realTimeFeature.setSrcIp(packet.src_ip);
					realTimeFeature.setDstIp(packet.dst_ip);
					realTimeFeature.addNumPktSrc();
					key = key1;
					realTimeFeature.setService("icmp");
					tempConnection = new TempConn((packet.sec*1000000 + packet.usec)/1000, realTimeFeature);
				}
				tempConnection.realTimeFeature.addNumPackets();
				tempConnection.realTimeFeature.setConnFlag("SF");
				tempConnection.realTimeFeature.setStopTime(packet.sec, packet.usec);
				tempConnection.timeHandle = (packet.sec*1000000 + packet.usec)/1000;
				this.addConnection(key, tempConnection);
			}
		}
	}

	public synchronized void addConnection(String key,TempConn value){
		tempConnMap.put(key, value);
	}
	
	public synchronized void removeConnection(String key){
		tempConnMap.remove(key);
	}
	
	public synchronized Set<String> getKeySet(){
		return tempConnMap.keySet();
	}
	
	public synchronized TempConn getKey(String key){
		return tempConnMap.get(key);
	}
	
	public synchronized boolean isContainKey(String key){
		return tempConnMap.containsKey(key);
	}
	
	private synchronized void checkConn(){
		Object[]keys = null;
		keys = getKeySet().toArray();
		int i;
		for(i = 0; i < keys.length ; i ++){
			TempConn tempConn = getKey(keys[i].toString());
			if(tempConn.realTimeFeature.isConnClose())
				removeConnection(keys[i].toString());
			else if(tempConn.realTimeFeature.getProtocol().equals("TCP")){
				this.checkTCP(tempConn, keys[i].toString(), System.currentTimeMillis());
			}
			else{
				this.checkUDPICMP(tempConn, keys[i].toString(), System.currentTimeMillis());
			}
		}
	}
	
	private synchronized void checkTCP(TempConn tempConn, String key, long sys_time){
		RealTimeFeature realTimeFeature = tempConn.realTimeFeature;
		long time = sys_time - tempConn.timeHandle;
		if(realTimeFeature.getNumTcpFin()==2){
			realTimeFeature.setFinishConn();
			realTimeFeature.setConnFlag("SF");
			realTimeFeature.setDuration(5*100000);
			Thread tConnHandle = new Thread(new ConnectionHandle(realTimeFeature));
			tConnHandle.start();
			removeConnection(key);
		}
		else if(realTimeFeature.getNumTcpReset()!=0 && realTimeFeature.getNumTcpSyn() == 1){
			realTimeFeature.setFinishConn();
			realTimeFeature.setConnFlag("REJ");
			realTimeFeature.setDuration(0);
			Thread tConnHandle = new Thread(new ConnectionHandle(realTimeFeature));
			tConnHandle.start();
			removeConnection(key);

		}
		else if(realTimeFeature.getNumTcpSyn() == 1 && time >= timeoutTCP && realTimeFeature.getNumTcpAck() == 0){
			realTimeFeature.setFinishConn();
			realTimeFeature.setConnFlag("S0");
			realTimeFeature.setDuration(0);
			Thread tConnHandle = new Thread(new ConnectionHandle(realTimeFeature));
			tConnHandle.start();
			removeConnection(key);

		}
		else if(realTimeFeature.getNumTcpSyn() == 2 && time >= timeoutTCP && realTimeFeature.getNumTcpPush() == 0){
			realTimeFeature.setFinishConn();
			realTimeFeature.setConnFlag("S1");
			realTimeFeature.setDuration(0);
			Thread tConnHandle = new Thread(new ConnectionHandle(realTimeFeature));
			tConnHandle.start();
			removeConnection(key);
		}
		else if(realTimeFeature.getNumTcpSyn() == 2 && time >= timeoutTCP && realTimeFeature.getNumTcpPush() != 0){
			realTimeFeature.setFinishConn();
			realTimeFeature.setConnFlag("S3");
			realTimeFeature.setDuration(0);
			Thread tConnHandle = new Thread(new ConnectionHandle(realTimeFeature));
			tConnHandle.start();
			removeConnection(key);
		}
		else if(realTimeFeature.getNumTcpSyn() == 1 && time >= timeoutTCP && realTimeFeature.getNumTcpAck() == 1){
			realTimeFeature.setFinishConn();
			realTimeFeature.setConnFlag("S4");
			realTimeFeature.setDuration(0);
			Thread tConnHandle = new Thread(new ConnectionHandle(realTimeFeature));
			tConnHandle.start();
			removeConnection(key);
		}
	}
	
	private synchronized void checkUDPICMP(TempConn tempConn, String key, long sys_time){
		long time = sys_time;
		if(time - tempConn.timeHandle >= this.timeoutUDPICMP){
			tempConn.realTimeFeature.setConnFlag("SF");
			tempConn.realTimeFeature.setFinishConn();
			Thread tConnHandle = new Thread(new ConnectionHandle(tempConn.realTimeFeature));
			tConnHandle.start();
			removeConnection(key);
		}
	}
	
	private synchronized void checkConnTCPdump(long sys_time){
		Object[]keys = null;
		keys = getKeySet().toArray();
		int i;
		for(i = 0; i < keys.length ; i ++){
			TempConn tempConn = getKey(keys[i].toString());
			if(tempConn.realTimeFeature.isConnClose())
				removeConnection(keys[i].toString());
			else if(tempConn.realTimeFeature.getProtocol().equals("TCP")){
				this.checkTCP(tempConn, keys[i].toString() , sys_time);
			}
			else{
				this.checkUDPICMP(tempConn, keys[i].toString(), sys_time);
			}
		}
	} 
}
