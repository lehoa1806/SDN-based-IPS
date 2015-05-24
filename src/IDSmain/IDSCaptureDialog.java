/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDSmain;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

/**
 *
 * @author Hoa
 */
public class IDSCaptureDialog extends JDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6184676600381778268L;

	/**
	 * 
	 */
	static JpcapCaptor jpcap=null;
	
	NetworkInterface[] devicelist;
	
	JComboBox<Object> devicelistBox;
    JComboBox<String> classChooser;
    
	JRadioButton detectCheck,trainCheck;
    
	public IDSCaptureDialog(JFrame CaptureDialog){
		super(CaptureDialog,"Capture dialog",true);
		devicelist=JpcapCaptor.getDeviceList();
		if(devicelist!=null){
			String[] devicenames=new String[devicelist.length];
			for(int i=0;i<devicenames.length;i++)
				devicenames[i]=(devicelist[i].description);
			devicelistBox=new JComboBox<Object>(devicenames);
		}
		JPanel devicelistPane=new JPanel();
		devicelistPane.add(devicelistBox);
		devicelistPane.setBorder(BorderFactory.createTitledBorder("Choose network interface"));
		devicelistPane.setAlignmentX(Component.LEFT_ALIGNMENT);

	
		JPanel buttonBox=new JPanel();
		buttonBox.setLayout(new BoxLayout(buttonBox,BoxLayout.X_AXIS));
		JButton okButton=new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		JButton cancelButton=new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonBox.add(okButton);
		buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
		buttonBox.add(cancelButton);
		buttonBox.add(Box.createRigidArea(new Dimension(10,0)));
		buttonBox.setAlignmentX(Component.RIGHT_ALIGNMENT);

		
        //==============================================================
        JPanel runmodePane=new JPanel();
        runmodePane.setLayout(new BoxLayout(runmodePane,BoxLayout.Y_AXIS));
        detectCheck=new JRadioButton("Detecting mode");
		detectCheck.setSelected(true);
		detectCheck.setActionCommand("Detecting");
		detectCheck.addActionListener(this);
		trainCheck=new JRadioButton("Training mode");
		trainCheck.setActionCommand("Training");
		trainCheck.addActionListener(this);
        ButtonGroup runmodegroup=new ButtonGroup();
		runmodegroup.add(detectCheck);
		runmodegroup.add(trainCheck);
        runmodePane.add(detectCheck);
		runmodePane.add(trainCheck);
        runmodePane.setAlignmentX(Component.LEFT_ALIGNMENT);
        runmodePane.setBorder(BorderFactory.createTitledBorder("Running mode"));

	    JPanel inclassPane=new JPanel();
	    inclassPane.setLayout(new BoxLayout(inclassPane,BoxLayout.Y_AXIS));
	    inclassPane.add(Box.createRigidArea(new Dimension(0,10)));
	    String[] trainingClass = { "normal", "DOS", "Probe", "R2L", "U2R" };
	    classChooser = new JComboBox<String>(trainingClass);
	    classChooser.setSelectedIndex(0);
	    classChooser.setActionCommand("Choose");
	    classChooser.addActionListener(this);
	    classChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
	    inclassPane.add(classChooser);
        JLabel attacktype = new JLabel("Using in training mode");
        inclassPane.add(attacktype);
		attacktype.setEnabled(false);
                
        JPanel classPane=new JPanel();
        classPane.setLayout(new BoxLayout(classPane,BoxLayout.X_AXIS));
        classPane.add(Box.createRigidArea(new Dimension(10,10)));
        classPane.add(inclassPane);
        classPane.setBorder(BorderFactory.createTitledBorder("Attack type"));

        JPanel topPane=new JPanel();
        topPane.setLayout(new BoxLayout(topPane,BoxLayout.X_AXIS));
		topPane.add(Box.createRigidArea(new Dimension(15,15)));
		topPane.add(runmodePane);
		topPane.add(Box.createRigidArea(new Dimension(10,10)));
		topPane.add(classPane);
        topPane.setAlignmentX(Component.CENTER_ALIGNMENT);
      	topPane.add(Box.createRigidArea(new Dimension(5,5)));

        JPanel bottomPane=new JPanel();
        bottomPane.setLayout(new BoxLayout(bottomPane,BoxLayout.X_AXIS));
		bottomPane.add(Box.createRigidArea(new Dimension(10,10)));
		bottomPane.add(devicelistPane);
		bottomPane.add(Box.createRigidArea(new Dimension(10,10)));
		bottomPane.add(buttonBox);

                
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		getContentPane().add(topPane);
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		getContentPane().add(bottomPane);
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		pack();
		setLocation(CaptureDialog.getLocation().x+100,CaptureDialog.getLocation().y+100);
		setVisible(true);
	}
	
    @Override
	public void actionPerformed(ActionEvent evt){
		String cmd=evt.getActionCommand();
        if(trainCheck.isSelected()){
    		IDSmain.runmode = 1;
    		IDSmain.atktype = (String) classChooser.getSelectedItem();
        }
        else if (detectCheck.isSelected()){
        	IDSmain.runmode = 0;
        }
        if(cmd.equals("Choose")){
        	IDSmain.atktype = (String) classChooser.getSelectedItem();
        }
		if(cmd.equals("OK")){
	        File f = new File("Train.arff");
	        if(!f.exists() && (IDSmain.runmode == 0)){
	            JFrame frame = new JFrame();
	            JOptionPane.showMessageDialog(frame,"Training set not existed, please set up new file");
	        } 
	        else {
                try {
					jpcap=JpcapCaptor.openDevice(devicelist[devicelistBox.getSelectedIndex()],12500000,true , 50);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					dispose();
				}
	        }
		}else if(cmd.equals("Cancel")){
			dispose();
		}
	}
	
	public static JpcapCaptor getJpcap(JFrame mFrame){
		JDialog IDSCaptureDialog =  new IDSCaptureDialog(mFrame);
		if (jpcap == null) IDSCaptureDialog.dispose();
		return jpcap;
	}
}
