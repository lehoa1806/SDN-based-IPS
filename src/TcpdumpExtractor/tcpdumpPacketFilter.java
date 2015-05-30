package TcpdumpExtractor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class tcpdumpPacketFilter {
	public String newkeyString;
	public long startTime;
	public long stopTime;
	public InetAddress atkIP;
	public InetAddress victimIp;
	public String atkType;
	public String key;
	private DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
	public tcpdumpPacketFilter(String tempString) throws ParseException, UnknownHostException{
		format.setTimeZone(TimeZone.getDefault());
		this.newkeyString = tempString;
		String ptempString[] = tempString.split("-");
		String tempDate = ptempString[0]+" "+ ptempString[2];		
		this.startTime = format.parse(tempDate).getTime()/ 1000;
		String pDurationtime[] = ptempString[3].split(":");
		this.stopTime = this.startTime + Integer.parseInt(pDurationtime[0])*3600 + Integer.parseInt(pDurationtime[1])*60 +Integer.parseInt(pDurationtime[2]);
		this.atkIP = InetAddress.getByName(ptempString[4]);
		this.victimIp = InetAddress.getByName(ptempString[5]);
		this.atkType = ptempString[1];
		this.key = this.atkIP.toString() + this.victimIp.toString();
	}	
}
