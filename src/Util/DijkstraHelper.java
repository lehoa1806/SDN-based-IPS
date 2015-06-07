/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import OpenDaylightUtil.OfSwitch;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author root
 */
public class DijkstraHelper {
    private OfSwitch pDijkstraSw;
    private long pDijkstraLong;
    private Queue<OfSwitch> pDijkstraPath;
    
    public DijkstraHelper(){
        this.pDijkstraSw = null;
        this.pDijkstraLong = 0;
        this.pDijkstraPath = new LinkedList<>();
    }
    public void setDijkstraSw(OfSwitch pDijkstraSw){
        this.pDijkstraSw = pDijkstraSw;
    }
    public void setDijkstraLong(long pLong){
        this.pDijkstraLong = pLong;
    }
    public void setDijkstraPath(Queue<OfSwitch> pPath){
        this.pDijkstraPath = pPath;
    }
    public void addDijkstraLong(){
        this.pDijkstraLong++;
    }
    public void addDijkstraPath(OfSwitch pOfSwitch){
        this.pDijkstraPath.add(pOfSwitch);
    }
    
    public long getDijkstraLong(){
        return this.pDijkstraLong;
    }
    public Queue<OfSwitch> getDijkstraPath(){
        return this.pDijkstraPath;
    }
    public OfSwitch getDijkstraSw(){
        return this.pDijkstraSw;
    }
    
}
