/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntruderDetector;

import FeatureExtractor.IDSFeatureEntry;
import FeatureExtractor.RealTimeFeature;
import IDSmain.Intruder;
import OpenDaylightUtil.Flow;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

/**
 *
 * @author Hoa
 */
public class DecisionTree implements Runnable {
    private boolean isRunning;

    private FastVector IDSRealtimeAttributeSet;
    private IDSFeatureEntry featureEntries;
    private int curIndex;

    J48 tree = new J48();
    public double classLabel;
    private int numSwitch;
    private String pAttackType;
    public DecisionTree(){
        this.IDSRealtimeAttributeSet = IDSmain.IDSmain.IDSRealtimeAttributeSet;
        this.featureEntries = IDSmain.IDSmain.featureEntries;
        this.tree = IDSmain.IDSmain.tree;
        this.isRunning = true;
        this.numSwitch = IDSmain.IDSmain.switchID.size();
        this.curIndex=0;

    }
    
    @Override
    public void run() {
    // TODO Auto-generated catch block
        while(this.isRunning){
            if(Thread.interrupted()){
                break;
            }
            if (featureEntries.getSize() != 0) {
                RealTimeFeature newRealTimeFeature = this.featureEntries.getFetureEntry();
                addIDSInstance(newRealTimeFeature);
                if (IDSmain.IDSmain.runmode == 0){
                    this.curIndex ++;
                    try {
                        classLabel=tree.classifyInstance(IDSmain.IDSmain.IDSInstances.firstInstance());
                        if (classLabel != 0){
                        // Detection
                        // Action
                            if (classLabel == 1){
                                pAttackType = "DOS";
                            }
                            else {
                                pAttackType = "Probe";
                            }
                            String sourceIP = newRealTimeFeature.getSrcIp().toString().split("/")[1] + "/32";
                            String destIP = newRealTimeFeature.getDstIp().toString().split("/")[1] + "/32";
                            Intruder newIntruder = new Intruder(curIndex, System.currentTimeMillis(), sourceIP, destIP, pAttackType);
                            addIntruder(newIntruder);
//                            if (intruderTable.getRowCount()>0){
//                            intruderTable.getValueAt((intruderTable.getRowCount()-1),(intruderTable.getColumnCount()-1)); 
//                            }
                            Flow newFlow = new Flow(0, IDSmain.IDSmain.newAction.getFlowID());
                            newFlow.setSourceIP(sourceIP);
                            newFlow.setDestIP(destIP);
                            newFlow.buildDropFlow(); 
                            Queue<String> pSwitchID = IDSmain.IDSmain.switchID;
                            for(int i=0; i < numSwitch; i++){
                                String SwitchID = pSwitchID.poll();                             
                                IDSmain.IDSmain.newAction.sendDroptoSW(SwitchID, newFlow);
                            }
                            
                            
                            
//                            JFrame frame = new JFrame();
//                            JOptionPane.showMessageDialog(frame,"Intruder detected \nAttack type : DOS\nConnection : "+ newRealTimeFeature.getKey());
//
//                        }
//                        else if (classLabel == 2) {
//                            JFrame frame = new JFrame();
//                            JOptionPane.showMessageDialog(frame,"Intruder detected \nAttack type : Probe\nConnection : "+ newRealTimeFeature.getKey());
                        }
                        IDSmain.IDSmain.IDSInstances.delete(0);
                    } catch (Exception ex) {
                        Logger.getLogger(DecisionTree.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else {
                try {
                    Thread.sleep((long) 100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DecisionTree.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void addIDSInstance(RealTimeFeature newRealTimeFeature){
        Instance NewInstance = new Instance(26);
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(0), newRealTimeFeature.getDuration());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(1), newRealTimeFeature.getProtocol());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(2), newRealTimeFeature.getConnFlag());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(3), newRealTimeFeature.getNumPackets());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(4), newRealTimeFeature.getService());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(5), newRealTimeFeature.getNumTcpFin());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(6), newRealTimeFeature.getNumTcpSyn());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(7), newRealTimeFeature.getNumTcpReset());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(8), newRealTimeFeature.getNumTcpPush());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(9), newRealTimeFeature.getNumTcpAck());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(10), newRealTimeFeature.getNumTcpUrg());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(11), newRealTimeFeature.getNumPktSrc());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(12), newRealTimeFeature.getNumPktDst());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(13), newRealTimeFeature.getSrcBytes());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(14), newRealTimeFeature.getDstBytes());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(15), newRealTimeFeature.getTimeBaseFeature().getNumSrc());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(16), newRealTimeFeature.getTimeBaseFeature().getNumSrcSamePort());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(17), newRealTimeFeature.getTimeBaseFeature().getNumSrcDiffPort());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(18), newRealTimeFeature.getTimeBaseFeature().getNumSYNSameSrc());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(19), newRealTimeFeature.getTimeBaseFeature().getNumRSTSameSrc());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(20), newRealTimeFeature.getTimeBaseFeature().getNumDst());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(21), newRealTimeFeature.getTimeBaseFeature().getNumDstSamePort());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(22), newRealTimeFeature.getTimeBaseFeature().getNumDstDiffPort());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(23), newRealTimeFeature.getTimeBaseFeature().getNumSYNSameDst());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(24), newRealTimeFeature.getTimeBaseFeature().getNumRSTSameDst());
        NewInstance.setValue((Attribute)IDSRealtimeAttributeSet.elementAt(25), IDSmain.IDSmain.atktype);
        IDSmain.IDSmain.IDSInstances.add(NewInstance);
//        System.out.println(NewInstance.toString());
    }
    private synchronized void addIntruder(Intruder newIntruder) {
        IDSmain.IDSmain.intruderData.addIntruder(newIntruder);
    }
}
