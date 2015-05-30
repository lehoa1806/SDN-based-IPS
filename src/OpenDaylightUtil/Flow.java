/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenDaylightUtil;

/**
 *
 * @author root
 */
public class Flow {
    private int tableID;
    private long flowID;
    private String sourceIP;
    private String destIp;
    private String flow;
    private int hardTimeOut;
    private int idleTimeOut;
    private int priority;
    private long timeStamp;
    private String flowKey;
    
    public Flow(int ptableID, long pFlowID) {
        this.timeStamp = System.nanoTime();
        this.flowKey = this.sourceIP + this.destIp;
        this.tableID = ptableID;
        this.flowID = pFlowID;
        this.sourceIP = null;
        this.destIp = null;
        this.hardTimeOut = 1200;
        this.idleTimeOut = 0;
        this.priority = 0;
        this.flow = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><flow xmlns=\"urn:opendaylight:flow:inventory\"><strict>false</strict><instructions><instruction><order>0</order><apply-actions><action><order>0</order><drop-action/></action></apply-actions></instruction></instructions>";
    
    }

    public void setSourceIP(String pSourceIP) {
        this.sourceIP = pSourceIP;
    }

    public void setDestIP(String pDestIP) {
        this.destIp = pDestIP;
    }
    
    public void setHardTimeOut(int pHardTimeOut) {
        this.hardTimeOut = pHardTimeOut;
    }
    
    public void setIdleTimeOut(int pIdleTimeOut) {
        this.idleTimeOut = pIdleTimeOut;
    }
    
    public void setPriority(int pPriority) {
        this.priority = pPriority;
    }

    public String buildDropFlow() {
        this.flow = this.flow + String.format("<table_id>%s</table_id>", this.tableID);
        this.flow = this.flow + String.format("<id>%s</id>", this.flowID);
        this.flow = this.flow + "<cookie_mask>255</cookie_mask><installHw>true</installHw><match><ethernet-match><ethernet-type><type>2048</type></ethernet-type></ethernet-match>";
        this.flow = this.flow + String.format("<ipv4-source>%s</ipv4-source>", this.sourceIP);
        this.flow = this.flow + String.format("<ipv4-destination>%s</ipv4-destination>", this.destIp);
        this.flow = this.flow + String.format("</match><hard-timeout>%s</hard-timeout>", this.hardTimeOut);
//        this.flow = this.flow + String.format("<cookie>%s</cookie>", 10);
        this.flow = this.flow + String.format("<idle-timeout>%s</idle-timeout>", this.idleTimeOut);
        this.flow = this.flow + String.format("<flow-name>%s</flow-name>", this.flowID);
        this.flow = this.flow + String.format("<priority>%s</priority>", this.priority);
        this.flow = this.flow + "<barrier>false</barrier></flow>";
        return(this.flow);
    }
    public String getFlow(){
        return this.flow;
    }
    public long getFlowID(){
        return this.flowID;
    }
    public long gettimeStamp(){
        return this.timeStamp;
    }
    public String getFlowKey(){
        return this.flowKey;
    }
}
