package TcpdumpExtractor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import FeatureExtractor.PacketHandle;
import IDSmain.IDSmain;
import Util.tcpdumpGetfile;
import jpcap.JpcapCaptor;

/**
 *
 * @author Hoa
 */
public class TcpdumpDialog extends JDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5246498805974256938L;
	/**
	 * 
	 */
	static JpcapCaptor jpcap=null;
	JFileChooser chooser = new JFileChooser();
	JFileChooser chooser2 = new JFileChooser();
	public static String fileAddress=null;
	public static String identificationsAddress=null;

	JRadioButton multiClass,oneClass;
    JTextField filelocation;
    JTextField identificationslocation;

    JComboBox<String> classChooser;
    public static TcpdumpDialog nTcpdumpDialog;
    public static Thread mTcpdumpThread;
    JButton okButton, identificationsButton;
    JButton cancelButton;
    
    
    //--------------------var test------------------
    private tcpdumpGetfile atk_identifications =   new tcpdumpGetfile();
    
    //----------------------------------------------
    
	public TcpdumpDialog(){
		super(nTcpdumpDialog,"Pcap extractor",true);
		JPanel openBox = new JPanel();
		openBox.setLayout(new BoxLayout(openBox,BoxLayout.X_AXIS));
		openBox.add(Box.createRigidArea(new Dimension(5,5)));
		JButton openButton=new JButton("Open");
		openButton.setActionCommand("Open");
		openButton.addActionListener(this);
		openBox.add(openButton);
		openBox.add(Box.createRigidArea(new Dimension(10,10)));
	    filelocation=new JTextField("Pcap file's localtion");
	    filelocation.setMaximumSize(new Dimension(Short.MAX_VALUE,25));
		openBox.add(filelocation);
		openBox.add(Box.createRigidArea(new Dimension(5,5)));
		openBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		openBox.setBorder(BorderFactory.createTitledBorder("Pcap file localtion"));

		JPanel identificationsBox = new JPanel();
		identificationsBox.setLayout(new BoxLayout(identificationsBox,BoxLayout.X_AXIS));
		identificationsBox.add(Box.createRigidArea(new Dimension(5,5)));
		identificationsButton=new JButton("Open");
		identificationsButton.setActionCommand("Identifications");
		identificationsButton.addActionListener(this);
		identificationsButton.setEnabled(false);
		identificationsBox.add(identificationsButton);
		identificationsBox.add(Box.createRigidArea(new Dimension(10,10)));
		identificationslocation=new JTextField("Attack identifications file's localtion");
		identificationslocation.setMaximumSize(new Dimension(Short.MAX_VALUE,25));
		identificationslocation.setEnabled(false);
	    identificationsBox.add(identificationslocation);
		identificationsBox.add(Box.createRigidArea(new Dimension(5,5)));
	    identificationsBox.setAlignmentX(Component.CENTER_ALIGNMENT);
	    identificationsBox.setBorder(BorderFactory.createTitledBorder("Identifications file localtion"));

		
		
		JPanel buttonBox=new JPanel();
		buttonBox.setLayout(new BoxLayout(buttonBox,BoxLayout.X_AXIS));
		okButton=new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		cancelButton=new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonBox.add(okButton);
		buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
		buttonBox.add(cancelButton);
		buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
		buttonBox.setAlignmentX(Component.RIGHT_ALIGNMENT);

		
        //==============================================================
        JPanel runmodePane=new JPanel();
        runmodePane.setLayout(new BoxLayout(runmodePane,BoxLayout.Y_AXIS));
        multiClass=new JRadioButton("Multiple classes");
        multiClass.setActionCommand("Multi");
        multiClass.addActionListener(this);
		oneClass=new JRadioButton("One class");
		oneClass.setActionCommand("One");
		oneClass.setSelected(true);
		oneClass.addActionListener(this);
        ButtonGroup runmodegroup=new ButtonGroup();
		runmodegroup.add(oneClass);
		runmodegroup.add(multiClass);
		runmodePane.add(oneClass);
        runmodePane.add(multiClass);
        runmodePane.setAlignmentX(Component.LEFT_ALIGNMENT);
        runmodePane.setBorder(BorderFactory.createTitledBorder("Training mode"));

	    JPanel inclassPane=new JPanel();
	    inclassPane.setLayout(new BoxLayout(inclassPane,BoxLayout.Y_AXIS));
	    inclassPane.add(Box.createRigidArea(new Dimension(0,10)));
	    String[] trainingClass = { "normal", "DOS", "Probe", "R2L", "U2R" };
	    classChooser = new JComboBox<String>(trainingClass);
	    classChooser.setSelectedIndex(0);
	    classChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
        inclassPane.add(classChooser);
        JLabel classChooserCom = new JLabel("Attack type in One class mode");
        inclassPane.add(classChooserCom);
        classChooserCom.setEnabled(false);
                
        JPanel classPane=new JPanel();
        classPane.setLayout(new BoxLayout(classPane,BoxLayout.X_AXIS));
        classPane.add(Box.createRigidArea(new Dimension(5,5)));
        classPane.add(inclassPane);
        classPane.add(Box.createRigidArea(new Dimension(5,5)));
        classPane.setBorder(BorderFactory.createTitledBorder("Attack type"));
        
        
        JPanel midPaneTop=new JPanel();
        midPaneTop.setLayout(new BoxLayout(midPaneTop,BoxLayout.X_AXIS));
        midPaneTop.add(Box.createRigidArea(new Dimension(5,5)));
        midPaneTop.add(openBox);
        midPaneTop.add(Box.createRigidArea(new Dimension(5,5)));
        
        JPanel midPaneBottom=new JPanel();
        midPaneBottom.setLayout(new BoxLayout(midPaneBottom,BoxLayout.X_AXIS));
        midPaneBottom.add(Box.createRigidArea(new Dimension(5,5)));
        midPaneBottom.add(identificationsBox);
        midPaneBottom.add(Box.createRigidArea(new Dimension(5,5)));

        
        JPanel topPane=new JPanel();
        topPane.setLayout(new BoxLayout(topPane,BoxLayout.X_AXIS));
        topPane.add(Box.createRigidArea(new Dimension(5,5)));
        topPane.add(runmodePane);
        topPane.add(Box.createRigidArea(new Dimension(10,10)));
        topPane.add(classPane);
        topPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPane.add(Box.createRigidArea(new Dimension(5,5)));

        JPanel bottomPane=new JPanel();
        bottomPane.setLayout(new BoxLayout(bottomPane,BoxLayout.X_AXIS));
		bottomPane.add(Box.createRigidArea(new Dimension(5,5)));
		bottomPane.add(buttonBox);

                
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		getContentPane().add(Box.createRigidArea(new Dimension(5,5)));
		getContentPane().add(topPane);
		getContentPane().add(Box.createRigidArea(new Dimension(5,5)));
		getContentPane().add(midPaneTop);
		getContentPane().add(Box.createRigidArea(new Dimension(5,5)));
		getContentPane().add(midPaneBottom);
		getContentPane().add(Box.createRigidArea(new Dimension(5,5)));
		getContentPane().add(bottomPane);
		getContentPane().add(Box.createRigidArea(new Dimension(5,5)));
		pack();
		setLocation(IDSmain.frame.getLocation().x+200,IDSmain.frame.getLocation().y+200);
		setVisible(true);
	}
	
    @Override
	public void actionPerformed(ActionEvent evt){
		String cmd=evt.getActionCommand();
        if(multiClass.isSelected()){
        	IDSmain.runmode = 3;
        	classChooser.setEnabled(false);
        	identificationsButton.setEnabled(true);
        	identificationslocation.setEnabled(true);
        }
        else if (oneClass.isSelected()){
        	IDSmain.runmode = 2;
        	classChooser.setEnabled(true);
        	identificationsButton.setEnabled(false);
        	identificationslocation.setEnabled(false);
        }
        if (cmd.equals("Open")){
			int returnVal = chooser.showOpenDialog(nTcpdumpDialog);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				fileAddress = chooser.getSelectedFile().getAbsolutePath();
				filelocation.setText(fileAddress);
			} else if (returnVal == JFileChooser.CANCEL_OPTION)
				return;
        } else if (cmd.equals("Identifications")){
			int returnVal = chooser2.showOpenDialog(nTcpdumpDialog);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				identificationsAddress = chooser2.getSelectedFile().getAbsolutePath();
				identificationslocation.setText(identificationsAddress);
			} else if (returnVal == JFileChooser.CANCEL_OPTION)
				return;
        }
        else if(cmd.equals("OK")){
        	File f = new File("Train.arff");
	        if(!f.exists() && (IDSmain.runmode == 0)){
	            JFrame frame = new JFrame();
	            JOptionPane.showMessageDialog(frame,"Training set not existed, please set up new file");
	        } 
	        else if (fileAddress!=null && (IDSmain.runmode == 2)){
	        	IDSmain.atktype = "normal";
	        	IDSmain.atktype = (String) classChooser.getSelectedItem();
	        	JFrame mframe = new JFrame();
	            JOptionPane.showMessageDialog(mframe,"Pcap file's being extracted, please wait until extraction complete dialog appear !");
	        	try {
	        		PacketHandle packetHandle = new PacketHandle();
					mTcpdumpThread = new Thread (packetHandle);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	mTcpdumpThread.start();
	//
//        	okButton.setVisible(false);
//        	cancelButton.setVisible(false);	        
	        }
	        else if (fileAddress!=null && (IDSmain.runmode == 3)){
	        	if (identificationsAddress!=null){
	        		atk_identifications.getFromFile();
		        	JFrame mframe = new JFrame();
		            JOptionPane.showMessageDialog(mframe,"Pcap file's being extracted, please wait until extraction complete dialog appear !");
		        	try {
		        		PacketHandle packetHandle = new PacketHandle();
						mTcpdumpThread = new Thread (packetHandle);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	mTcpdumpThread.start();
	        	}
	//
//        	okButton.setVisible(false);
//        	cancelButton.setVisible(false);	        
	        }

        
		}else if(cmd.equals("Cancel")){
			dispose();
		}
	}
	
	public static void getTcpdumpDialog(){
		nTcpdumpDialog =  new TcpdumpDialog();
	}
}
