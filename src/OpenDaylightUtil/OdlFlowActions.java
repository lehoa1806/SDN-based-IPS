/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenDaylightUtil;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


public class OdlFlowActions {
    private final String controllerIP;
    private final String adminAccount;
    private final String adminPass;
    private Queue<FlowPool> poolFlow = new LinkedList<>();
    private long flowID;
    public OdlFlowActions(String controllerIP, String pAdminAccount, String pAdminPass){
        this.controllerIP = controllerIP;
        this.adminAccount = pAdminAccount;
        this.adminPass = pAdminPass;
        this.flowID = 0;
    }
    
    public void sendDroptoSW(String switchID, Flow dropFlow){
        this.flowID = this.flowID+1;
        FlowPool newFlowPool = new FlowPool(switchID, dropFlow);
        newFlowPool.setFlowAction("Drop");
        Queue<FlowPool> newFlowonPool = sameFlowonPool(dropFlow, switchID);
        while (newFlowonPool.size()>0){
            FlowPool tempPool = newFlowonPool.poll();
            sendDeletetoSW(tempPool.switchID, tempPool.getFlow());
        }
        this.poolFlow.add(newFlowPool);
        URI controllerURI = UriBuilder.fromUri("http://"+controllerIP+":8080/restconf").build();
        try {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            client.addFilter(new HTTPBasicAuthFilter(adminAccount, adminPass));
            ClientResponse checkDropCMD = client.resource(controllerURI).path(String.format("config/opendaylight-inventory:nodes/node/%s/table/0/flow/%s", switchID, dropFlow.getFlowID())).type(javax.ws.rs.core.MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
            if (checkDropCMD.getStatus() == 404){
                ClientResponse sendDropCMD = client.resource(controllerURI).path(String.format("config/opendaylight-inventory:nodes/node/%s/table/0/flow/%s", switchID, dropFlow.getFlowID())).type(javax.ws.rs.core.MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).put(ClientResponse.class, dropFlow.getFlow());
                int status = sendDropCMD.getStatus();
                if (status == 200) {
                    System.out.println("OK");
                }
            }
            else
                System.out.println("ERROR");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    } 
    public void sendDeletetoSW(String switchID, Flow dropFlow){
        URI controllerURI = UriBuilder.fromUri("http://"+controllerIP+":8080/restconf").build();
        try {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            client.addFilter(new HTTPBasicAuthFilter(adminAccount, adminPass));
            ClientResponse checkDropCMD = client.resource(controllerURI).path(String.format("config/opendaylight-inventory:nodes/node/%s/table/0/flow/%s", switchID, dropFlow.getFlowID())).type(javax.ws.rs.core.MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
            if (checkDropCMD.getStatus() == 200){
                ClientResponse sendDropCMD = client.resource(controllerURI).path(String.format("config/opendaylight-inventory:nodes/node/%s/table/0/flow/%s", switchID, dropFlow.getFlowID())).type(javax.ws.rs.core.MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class, dropFlow.getFlow());
                int status = sendDropCMD.getStatus();
                if (status == 200) {
                    System.out.println("OK");
                }
            }
            else
                System.out.println("ERROR");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    } 
    /*
    * 
    * Delete a Flow after clearTime s
    */
    public void deleteFlowOnTime(int clearTime){
        if (((System.nanoTime() - this.poolFlow.peek().getFlow().gettimeStamp())/1000000000) > clearTime){
            while (!this.poolFlow.isEmpty()){
                if (((System.nanoTime() - this.poolFlow.peek().getFlow().gettimeStamp())/1000000000) > clearTime){
                    FlowPool tempPool = this.poolFlow.poll();
                    sendDeletetoSW(tempPool.switchID, tempPool.flow);
                }
            }
        }
    }
    public long getFlowID(){
        return this.flowID;
    }
    public Queue<FlowPool> getFlowPool(){
        return this.poolFlow;
    }
    
    private Queue<FlowPool> sameFlowonPool(Flow pflow, String pSwitchID){
        FlowPool pFlowPool;
        Queue<FlowPool> pFlowonPool=null;
        Queue<FlowPool> pPoolFlow = new LinkedList<>();
        while(!this.poolFlow.isEmpty()){
            pFlowPool = this.poolFlow.poll();
            if(pflow.getFlowKey().equals(pFlowPool.getFlow().getFlowKey())&&(pSwitchID.equals(pFlowPool.switchID))){
                pFlowonPool.add(pFlowPool);
            }
            else{
                pPoolFlow.add(pFlowPool);
            }
        }
        this.poolFlow = pPoolFlow;
        return pFlowonPool;
    }
    
    public class FlowPool{
        private Flow flow;
        private String switchID;
        private String flowAction;
        public FlowPool(String pSwitchID, Flow pflow){
            this.flow = pflow;
            this.switchID = pSwitchID;
            this.flowAction = null;
        }
        public void setFlowAction(String pFlowAction){
            this.flowAction = pFlowAction;
        }
        public Flow getFlow(){
            return this.flow;
        }
        public String getSwitchID(){
            return this.switchID;
        }
    }
}
