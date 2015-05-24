/**
 * This class define 13 real time IDS feature in each connection.
 */

package FeatureExtractor;

import java.net.InetAddress;

public class RealTimeFeature {

//---------------------13 feature vars-----------------------	
	
	private String protocol;
	private InetAddress srcIp;
	private InetAddress dstIp;
	private int numPackets;
	private int srcPort;
	private int dstPort;
	private String service;
	private long timeStart;
	private String connectionKey;
	
	//-----------------Flags --------------------------------
	private int numTCPFin;
	private int numTCPSyn;
	private int numTCPReset;
	private int numTCPPush;
	private int numTCPAck;
	private int numTCPUrg;
	
	//----------------Num Packet --------------------------
	private int numPktSrc;
	private int numPktDst;
	
	private long srcBytes;
	private long dstBytes;
	private TimeBaseFeature timeBaseFeature;
	
	private String flag; 
	//---------------------------------------KDD Feature not use --------------------------------
//	private int count;
//	private int srv_count;
//	private long serror_rate;
//	private long srv_serror_rate;
//	private long rerror_rate;
//	private long srv_error_rate;
//	private long same_srv_rate;
//	private long diff_srv_rate;
//	private long srv_diff_host_rate;
//	
//	private int dst_host_count;
//	private int dst_host_srv_count;
//	private double dst_host_same_srv_rate;
//	private double dst_host_diff_srv_rate;
//	private double dst_host_same_src_port_rate;
//	private double dst_host_srv_diff_host_rate;
//	private double dst_host_serror_rate ;
//	private double dst_host_srv_serror_rate ;
//	private double dst_host_rerror_rate ;
//	private double dst_host_srv_error_rate ;
	
	//----------------Connection Time ---------------------
	private long duration;

		private long startTime;
		private long stopTime;
		
		private boolean isConnClose;
		
//-------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public RealTimeFeature(){
		numPackets=0;
		srcPort = 0;
		dstPort = 0;
		numTCPFin = 0;
		numTCPSyn = 0;
		numTCPReset = 0;
		numTCPPush = 0;
		numTCPAck = 0;
		numTCPUrg = 0;
		numPktSrc = 0;
		numPktDst = 0;
		srcIp = null;
		dstIp = null;
		srcBytes = 0;
		dstBytes = 0;
		startTime = 0;
		stopTime = 0;
		duration = 0;
		timeBaseFeature = null;
        isConnClose = false;      
        flag = null;
        service = null;
	}
	
//--------------------------Method of class------------------------------------------
	
	//----------------------set value methods -----------------------------------
	public void setNumPackets(int num){this.numPackets = num;}
	public void setProtocol(String protocol){this.protocol = protocol;}
	public void setNumPktSrc(int num){this.numPktSrc = num;}
	public void setNumPktDst(int num){this.numPktDst = num;}
	public void setSrcPort(int srcPort){this.srcPort = srcPort;}
	public void setDstPort(int dstPort){this.dstPort = dstPort;}

	public void setNumTcpFin(int num){this.numTCPFin = num;}
	public void setNumTcpSyn(int num){this.numTCPSyn = num;}
	public void setNumTcpReset(int num){this.numTCPReset = num;}
	public void setNumTcpPush(int num){this.numTCPPush = num;}
	public void setNumTcpAck(int num){this.numTCPAck = num;}
	public void setNumTcpUrg(int num){this.numTCPUrg = num;}
	public void setSrcIp(InetAddress srcIp){this.srcIp = srcIp;}
	public void setDstIp(InetAddress dstIp){this.dstIp = dstIp;}
	
	public void setSrcBytes(long srcBytes){this.srcBytes = srcBytes;}
	public void setDstBytes(long dstBytes){this.dstBytes = dstBytes;}

	public void setStartTime(long sec, long usec){this.startTime = sec*1000000+usec;}
	public void setStopTime(long sec, long usec){this.stopTime = sec*1000000+usec; this.duration = this.stopTime - this.startTime;}
	public void setDuration(long duration){this.duration = duration;}
	
	public void setFinishConn(){this.isConnClose = true;}
	public void setTimeBaseFeature(TimeBaseFeature timeBaseFeature){this.timeBaseFeature = timeBaseFeature;}
	
	public void setConnFlag(String falg){this.flag = falg;}
	
	public void setService(String service){this.service = service;}
	//--------------------------------------------------------------------------
	
	//---------------------get value methods------------------------------------
	public int getNumPackets(){return this.numPackets;}
	public int getNumPktSrc(){return this.numPktSrc;}
	public int getNumPktDst(){return this.numPktDst;}
	public int getSrcPort(){return this.srcPort;}
	public int getDstPort(){return this.dstPort;}
	public int getNumTcpFin(){return this.numTCPFin;}
	public int getNumTcpSyn(){return this.numTCPSyn;}
	public int getNumTcpReset(){return this.numTCPReset ;}
	public int getNumTcpPush(){return this.numTCPPush;}
	public int getNumTcpAck(){return this.numTCPAck;}
	public int getNumTcpUrg(){return this.numTCPUrg;}
	public InetAddress getSrcIp(){return this.srcIp;}
	public InetAddress getDstIp(){return this.dstIp;}
	
	public long getSrcBytes(){return this.srcBytes;}
	public long getDstBytes(){return this.dstBytes;}
	
	public String getProtocol(){return this.protocol;}
	
	public long getStartTime(){return this.startTime;}
	public long getStopTime(){return this.stopTime;}
	public long getDuration(){return this.duration;}
	public String getConnFlag(){return this.flag;}
	public String getService(){return this.service;}
	public TimeBaseFeature getTimeBaseFeature(){return this.timeBaseFeature;}
	//--------------------------------------------------------------------------
	
	//------------------increase value method-----------------------------------------
	public void addNumPackets(){this.numPackets++;}
	public void addNumPktSrc(){this.numPktSrc++;}
	public void addNumPktDst(){this.numPktDst++;}
	public void addNumTcpFin(){this.numTCPFin++;}
	public void addNumTcpSyn(){this.numTCPSyn++;}
	public void addNumTcpReset(){this.numTCPReset++;}
	public void addNumTcpPush(){this.numTCPPush++;}
	public void addNumTcpAck(){this.numTCPAck++;}
	public void addNumTcpUrg(){this.numTCPUrg++;}
	
	public void addSrcBytes(long srcBytes){this.srcBytes += srcBytes;}
	public void addDstBytes(long dstBytes){this.dstBytes += dstBytes;}
	//-------------------------------------------------------------------------------
	public void setKey(String connectionKey){this.connectionKey = connectionKey;}						//set conneciton ID
	public String getKey(){return this.connectionKey;}													//get connection ID
	public void setTimeStart(long time){this.timeStart = time;}											//set start time
	public long getTImeStart(){return this.timeStart;}													//get start time
 
    
	public boolean isConnClose(){return this.isConnClose;}
	
//-----------------------------------------------------------------------------------------------------------------------
}
