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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author root
 */
public class SwitchIdList {
    private Queue<String> switchID = new LinkedList<String>();
    private String adminAccount;
    private String adminPass;
    private String controllerIp;
    public SwitchIdList(String pControllerIp, String pAdminAccount, String pAdminPass){
        this.adminAccount = pAdminAccount;
        this.adminPass = pAdminPass;
        this.controllerIp = pControllerIp;
    }
    
    public Queue<String> getSwitchIdList() {
        Queue<String> switchIDlist = new LinkedList<String>();
        URI controllerURI = UriBuilder.fromUri("http://"+controllerIp+":8181/restconf").build();
        String topoEntity;
        try {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            client.addFilter(new HTTPBasicAuthFilter(adminAccount, adminPass));
            ClientResponse TopoResponse = client.resource(controllerURI).path("operational/network-topology:network-topology").type(javax.ws.rs.core.MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
            int status = TopoResponse.getStatus();
            if (status == 200) {
                topoEntity = TopoResponse.getEntity(String.class);
                JSONParser topoParser = new JSONParser();
                JSONObject topoObject = (JSONObject) topoParser.parse(topoEntity);
                JSONArray topoList = (JSONArray)( (JSONObject)topoObject.get("network-topology")).get("topology");
                for (Object topoList1 : topoList) {
                    JSONArray allnodeList = (JSONArray) ((JSONObject) topoList1).get("node");
                    for (Object allnodeList1 : allnodeList) {
                        String allNodeId = (String) ((JSONObject) allnodeList1).get("node-id");
                        // Just for 
                        String[] switchAndHost = allNodeId.split(":");
                        if (switchAndHost[0].equals("openflow")){
                            switchIDlist.add(allNodeId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return switchIDlist;
        }
}
