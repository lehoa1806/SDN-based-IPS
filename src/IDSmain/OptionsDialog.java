package IDSmain;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class OptionsDialog extends JDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Controller options
	public String controllerIP;
	public String controllerPort;
	public String controllerAccount;
	public String controllerPass;	
	
	JTextField ipAddrText;
	JTextField ipPort;
	JTextField userContent;
	JPasswordField passwordContent;
	
	static JDialog nOptionsDialog;   
	public OptionsDialog(){
		
	}
	public OptionsDialog(JFrame nframe){
		super(nOptionsDialog,"Options",true);

		this.controllerIP=null;
		this.controllerPort=null;
		this.controllerAccount=null;
		this.controllerPass=null;
		
		
		
		JPanel controllerAddress = new JPanel();
		controllerAddress.setLayout(new BoxLayout(controllerAddress,BoxLayout.X_AXIS));
		JLabel ipAddr = new JLabel("IP");
		JLabel portAddr = new JLabel(":");
		ipAddrText = new JTextField(20);
		ipAddrText.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		ipPort = new JTextField(5);
		ipPort.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		controllerAddress.add(Box.createRigidArea(new Dimension(10,10)));
		controllerAddress.add(ipAddr);
		controllerAddress.add(Box.createRigidArea(new Dimension(10,10)));
		controllerAddress.add(ipAddrText);
		controllerAddress.add(Box.createRigidArea(new Dimension(3,10)));
		controllerAddress.add(portAddr);
		controllerAddress.add(Box.createRigidArea(new Dimension(3,10)));
		controllerAddress.add(ipPort);
		controllerAddress.add(Box.createRigidArea(new Dimension(10,10)));
		controllerAddress.setBorder(BorderFactory.createTitledBorder("Controller address"));
		JPanel controllerAddressBox = new JPanel();
		controllerAddressBox.setLayout(new BoxLayout(controllerAddressBox,BoxLayout.X_AXIS));
		controllerAddressBox.add(Box.createRigidArea(new Dimension(5,5)));
		controllerAddressBox.add(controllerAddress);
		controllerAddressBox.add(Box.createRigidArea(new Dimension(5,5)));

		
		JPanel accountBox = new JPanel();
		accountBox.setLayout(new BoxLayout(accountBox,BoxLayout.X_AXIS));
		JLabel userLabel = new JLabel("Username  ");
		userContent = new JTextField("");
//		userContent.setPreferredSize(new Dimension(30,20));
		accountBox.add(userLabel);
		accountBox.add(userContent);
		accountBox.setAlignmentX(Component.RIGHT_ALIGNMENT);

		
		JPanel passBox = new JPanel();
		passBox.setLayout(new BoxLayout(passBox,BoxLayout.X_AXIS));
		JLabel passwordLabel = new JLabel("Password  ");
		passwordContent = new JPasswordField("");
		passBox.add(passwordLabel);
		passBox.add(passwordContent);
		passBox.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JPanel controllerAccount = new JPanel();
		controllerAccount.setLayout(new BoxLayout(controllerAccount,BoxLayout.Y_AXIS));
		controllerAccount.add(Box.createRigidArea(new Dimension(0,5)));
		controllerAccount.add(accountBox);
		controllerAccount.add(Box.createRigidArea(new Dimension(0,5)));
		controllerAccount.add(passBox);
		controllerAccount.setAlignmentX(Component.RIGHT_ALIGNMENT);
		controllerAccount.setBorder(BorderFactory.createTitledBorder("Controller account"));

		JPanel buttonBox=new JPanel();
		buttonBox.setLayout(new BoxLayout(buttonBox,BoxLayout.Y_AXIS));
		JButton okButton=new JButton("    OK   ");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		JButton cancelButton=new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonBox.add(okButton);
		buttonBox.add(Box.createRigidArea(new Dimension(5,5)));
		buttonBox.add(cancelButton);
		buttonBox.add(Box.createRigidArea(new Dimension(5,5)));


	    JPanel bottomPane=new JPanel();
	    bottomPane.setLayout(new BoxLayout(bottomPane,BoxLayout.X_AXIS));
	    bottomPane.add(Box.createRigidArea(new Dimension(5,5)));
		bottomPane.add(controllerAccount);
		bottomPane.add(Box.createRigidArea(new Dimension(5,5)));
		bottomPane.add(buttonBox);
		bottomPane.add(Box.createRigidArea(new Dimension(5,5)));

            
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		getContentPane().add(controllerAddressBox);
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		getContentPane().add(bottomPane);
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		pack();
		setLocation(IDSmain.frame.getLocation().x+200,IDSmain.frame.getLocation().y+200);
		setVisible(true);
	}
	
    @SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent evt){
    	String cmd=evt.getActionCommand();
		if(cmd.equals("OK")){
        	this.controllerIP = ipAddrText.getText();
        	this.controllerPort = ipPort.getText();
        	this.controllerAccount = userContent.getText();
        	this.controllerPass = passwordContent.getText() ;	
        	System.out.println(this.controllerIP+":"+this.controllerPort + "         " +this.controllerAccount+"____"+this.controllerPass);
        
		}else if(cmd.equals("Cancel")){
			dispose();
		}
	}
	
	public static void getOptionsDialog(JFrame nframe){
		nOptionsDialog =  new OptionsDialog(nframe);
		
	}
}

