package IDSmain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Intruder {

	private Object[] intruderInfo = new Object[IntruderTable.columnNames.length];

	private long index;
	private long timeStamp;
	private String dstIP;
	private String srcIP;
	private String atkClass;
	
	private static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS MMM dd, yyyy");


	public Intruder(long curIndex, long timeStamp, String dstIPonString,  String srcIPonString, String atkType){
		this.index = curIndex;
		this.timeStamp = timeStamp;
		this.dstIP = dstIPonString;
		this.srcIP = srcIPonString;
		this.atkClass = atkType;
		
		intruderInfo[0] = this.index;
		intruderInfo[1] = timeFormat.format(new Date(this.timeStamp/1000));
		intruderInfo[2] = this.getSrcIP();
		intruderInfo[3] = this.getDstIP();
		intruderInfo[4] = this.getAtkClass();
	}
	
	public long getIndex(){
		return this.index;
	}
	public long getTimeStamp(){
		return this.timeStamp;
	}
	public String getDstIP(){
		return this.dstIP;
	}
	public String getSrcIP(){
		return this.srcIP;
	}
	public String getAtkClass(){
		return this.atkClass;
	}
	
	
	public void setIndex(long index){
		this.index = index;
	}
	public void setTimeStamp(long timeStamp){
		this.timeStamp = timeStamp;
	}
	public void setDstIP(String dstIP){
		this.dstIP = dstIP;
	}
	public void setSrcIP(String srcIP){
		this.srcIP = srcIP;

	}
	public void setAtkClass(String atkClass){
		this.atkClass = atkClass;
	}
	public Object getRowData(int columnIndex) {
		return intruderInfo[columnIndex];
	}
}
