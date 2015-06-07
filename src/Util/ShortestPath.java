/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import IPSSDNmain.IPSmain;
import OpenDaylightUtil.HostSwitchLink;
import OpenDaylightUtil.OfSwitch;
import OpenDaylightUtil.SwitchSwitchLink;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author root
 */
public class ShortestPath {
    
    public ShortestPath(){}
    
    public Queue<OfSwitch> buildShortestPath(String monitorMAC, String receivedMac){
        Queue<OfSwitch> pSwitchList= IPSmain.pOpenDaylightPool.getSwitchList();
        Queue<HostSwitchLink> pHSList = IPSmain.pOpenDaylightPool.getHSLink();
        Queue<SwitchSwitchLink> pSSList = IPSmain.pOpenDaylightPool.getSSLink();
        Queue<DijkstraHelper> pShortestPath = new LinkedList<>();
        Queue<OfSwitch> pathMonitorToReceive = new LinkedList<>();
        OfSwitch monitorSwitch = null, receivedSwitch = null;
        for (HostSwitchLink pHostSwitchLink:pHSList){
            if (pHostSwitchLink.getHost().getHostID().equals(monitorMAC)){
                monitorSwitch = new OfSwitch(pHostSwitchLink.getSwitch().getSwitchID());
            }
        }
       for (HostSwitchLink pHostSwitchLink:pHSList){
            if (pHostSwitchLink.getHost().getHostID().equals(receivedMac)){
                receivedSwitch = new OfSwitch(pHostSwitchLink.getSwitch().getSwitchID());
            }
        }
        DijkstraHelper pDijkstraHelper = new DijkstraHelper();
        pDijkstraHelper.addDijkstraPath(monitorSwitch);
        pDijkstraHelper.setDijkstraSw(monitorSwitch);
        pShortestPath.add(pDijkstraHelper);
        for(OfSwitch pSwitch: pSwitchList){
            for(OfSwitch ppSwitch: pSwitchList){
                if (isConnected(pSwitch, ppSwitch, pSSList)){
                    if (isOnPath(pSwitch, pShortestPath)&&!isOnPath(ppSwitch, pShortestPath)){
                        DijkstraHelper temp = this.getDijkstraHelper(pSwitch, pShortestPath);
                        DijkstraHelper newDijkstraHelper = new DijkstraHelper();
                        newDijkstraHelper.setDijkstraSw(ppSwitch);
                        for (OfSwitch p:temp.getDijkstraPath()){
                            newDijkstraHelper.addDijkstraPath(p);
                        }
                        newDijkstraHelper.addDijkstraPath(ppSwitch);
                        newDijkstraHelper.setDijkstraLong(temp.getDijkstraLong());
                        newDijkstraHelper.addDijkstraLong();
                        pShortestPath.add(newDijkstraHelper);
                    }
                    else if (isOnPath(ppSwitch, pShortestPath)&&!isOnPath(pSwitch, pShortestPath)){
                        DijkstraHelper temp = this.getDijkstraHelper(ppSwitch, pShortestPath);
                        DijkstraHelper newDijkstraHelper = new DijkstraHelper();
                        newDijkstraHelper.setDijkstraSw(pSwitch);
                        for (OfSwitch p:temp.getDijkstraPath()){
                            newDijkstraHelper.addDijkstraPath(p);
                        }
                        newDijkstraHelper.addDijkstraPath(pSwitch);
                        newDijkstraHelper.setDijkstraLong(temp.getDijkstraLong());
                        newDijkstraHelper.addDijkstraLong();
                        pShortestPath.add(newDijkstraHelper);
                    }
                    
                    else if (isOnPath(pSwitch, pShortestPath)&&isOnPath(ppSwitch, pShortestPath)){
                        DijkstraHelper tempPSwitch = this.getDijkstraHelper(pSwitch, pShortestPath);
                        DijkstraHelper tempPpSwitch = this.getDijkstraHelper(ppSwitch, pShortestPath);
                        if (tempPSwitch.getDijkstraLong() > (tempPpSwitch.getDijkstraLong()+1)){
                            tempPSwitch.getDijkstraPath().clear();
                            for (OfSwitch p:tempPpSwitch.getDijkstraPath()){
                                tempPSwitch.addDijkstraPath(p);
                            }
                            tempPSwitch.addDijkstraPath(pSwitch);
                            tempPSwitch.setDijkstraLong(tempPpSwitch.getDijkstraLong());
                            tempPSwitch.addDijkstraLong();
                        }
                        else if (tempPpSwitch.getDijkstraLong() > (tempPSwitch.getDijkstraLong()+1)){
                            tempPpSwitch.getDijkstraPath().clear();
                            for (OfSwitch p:tempPSwitch.getDijkstraPath()){
                                tempPpSwitch.addDijkstraPath(p);
                            }
                            tempPpSwitch.addDijkstraPath(ppSwitch);
                            tempPpSwitch.setDijkstraLong(tempPSwitch.getDijkstraLong());
                            tempPpSwitch.addDijkstraLong();
                        }
                        
                    }
                }
            }
        }
        for(DijkstraHelper p:pShortestPath){
            if (p.getDijkstraSw().getSwitchID().equals(receivedSwitch.getSwitchID())){
                pathMonitorToReceive = p.getDijkstraPath();
            }
        }
        return pathMonitorToReceive;
    }
    
    public boolean isConnected(OfSwitch pOfSwitch1, OfSwitch pOfSwitch2, Queue<SwitchSwitchLink> pSSList){
        boolean isChecked = false;
        for (SwitchSwitchLink pSSLink:pSSList){
            if (pOfSwitch1.getSwitchID().equals(pSSLink.getSwitch1().getSwitchID()) && pOfSwitch2.getSwitchID().equals(pSSLink.getSwitch2().getSwitchID())){
                isChecked = true;
            }
            else if (pOfSwitch1.getSwitchID().equals(pSSLink.getSwitch2().getSwitchID()) && pOfSwitch2.getSwitchID().equals(pSSLink.getSwitch1().getSwitchID())){
                isChecked = true;
            }
        }
        return isChecked;
    }
    public boolean isOnPath (OfSwitch pSwitch, Queue<DijkstraHelper> pShortestPath){
        boolean isChecked = false;
        for (DijkstraHelper pDijkstraHelper: pShortestPath){
            for (OfSwitch pShortestSwitch: pDijkstraHelper.getDijkstraPath()){
                if (pShortestSwitch.getSwitchID().equals(pSwitch.getSwitchID())){
                    isChecked = true;
                }
            }
        }
        return isChecked;
    }
    
    public DijkstraHelper getDijkstraHelper(OfSwitch pOfSwitch, Queue<DijkstraHelper> pShortestPath ){
        DijkstraHelper temp =new DijkstraHelper();
        for (DijkstraHelper p: pShortestPath){
            if (p.getDijkstraSw().getSwitchID().equals(pOfSwitch.getSwitchID())){
                temp = p;
            }
        }
        return temp;
    } 
    
}
