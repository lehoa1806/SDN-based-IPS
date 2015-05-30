package IDSmain;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;

import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import FeatureExtractor.ConnectionFeature;
import FeatureExtractor.ConnectionHandle;
import FeatureExtractor.IDSFeatureEntry;
import FeatureExtractor.MapPortServices;
import FeatureExtractor.PacketHandle;
import FeatureExtractor.RawPacket;
import IntruderDetector.DecisionTree;
import OpenDaylightUtil.OdlFlowActions;
import OpenDaylightUtil.SwitchIdList;
import TcpdumpExtractor.TcpdumpDialog;
import Util.NetworkMonitor;
import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;


public class IDSmain extends JApplet implements ActionListener, WindowListener{
    public static short runmode;
    public static JFrame frame;
    public static IntruderTable intruderData;
    protected JTable intruderTable;
    
    private String helpContents = "Need";
    private String aboutString = "Intrusion prevention system using flow management \ntechnologies to protect Software-Defined Network\n                                 May 2015";
    public static DateFormat timeFormat = new SimpleDateFormat("HH-mm-ss-MMM-dd-yyyy");
    public static String atktype;
    private Thread captureThread;
    JFileChooser chooser = new JFileChooser();
    JpcapCaptor jpcap = null;
    
	
    public static TimeSeries throughput = new TimeSeries("Network Throughput", Millisecond.class);

  //------------------OpenDaylight config-------------------------------------------
    public static Queue<String> switchID = null;
    public static SwitchIdList switchIDlist = null;
    public static OdlFlowActions newAction = null;
    
    public static String controllerIP = null;
    public static String adminAccount = null;
    public static String adminPass = null;
    
  //------------------FeatureExtractior added-------------------------------------------	
    //Instance of RawPacket which is used by other Threads.
    public static RawPacket rawPacket;
    //Instance of IDSFeatureEntry which is used by other Threads.
    public static IDSFeatureEntry featureEntries;
    public static PacketHandle packetHandle;
    public static ConnectionFeature connFeature;
    private static Thread tPacketHandle;
    
    public static ConnectionHandle connHandle;
    
    public static RawPacket packetForMonitor;
    public static MapPortServices mapPortService;
    
//---------------------------------------------------------------------------------------
    // Declare IDSRealtimeAttributeSet vector
    public static FastVector IDSRealtimeAttributeSet;
    public static Instances IDSInstances;
//----------------------------------------------------------------------
    // Application configuration
    private Instances train;
    public static J48 tree;
    double classLabel;
    private Thread DTProcessing;
    private Thread pNetMonitor;
    
    
    /*
     * 
     */
	public IDSmain() {
		IDSmain.runmode = 0;
        IDSmain.tree  = new J48();

    // Declare 13 real-time attributes (numeric)
        Attribute RealtimeAttribute00 = new Attribute("duration");
        Attribute RealtimeAttribute03 = new Attribute("numPackets");
        Attribute RealtimeAttribute05 = new Attribute("numTCPFin");
        Attribute RealtimeAttribute06 = new Attribute("numTCPSyn");
        Attribute RealtimeAttribute07 = new Attribute("numTCPReset");
        Attribute RealtimeAttribute08 = new Attribute("numTCPPush");
        Attribute RealtimeAttribute09 = new Attribute("numTCPAck");
        Attribute RealtimeAttribute10 = new Attribute("numTCPUrg");

        Attribute RealtimeAttribute11 = new Attribute("numPktSrc");
        Attribute RealtimeAttribute12 = new Attribute("numPktDst");
        Attribute RealtimeAttribute13 = new Attribute("srcBytes");
        Attribute RealtimeAttribute14 = new Attribute("dstBytes");       
        
        Attribute RealtimeAttribute15 = new Attribute("numSrc");
        Attribute RealtimeAttribute16 = new Attribute("numSrcSamePort");
        Attribute RealtimeAttribute17 = new Attribute("numSrcDiffPort");
        Attribute RealtimeAttribute18 = new Attribute("numSYNSameSrc");
        Attribute RealtimeAttribute19 = new Attribute("numRSTSameSrc");
        
        Attribute RealtimeAttribute20 = new Attribute("numDst");
        Attribute RealtimeAttribute21 = new Attribute("numDstSamePort");        
        Attribute RealtimeAttribute22 = new Attribute("numDstDiffPort");
        Attribute RealtimeAttribute23 = new Attribute("numSYNSameDst");       
        Attribute RealtimeAttribute24 = new Attribute("numRSTSameDst");            
        // Declare "Class" attribute of "feature entry"
        // ClassAttributeSet vector values
        FastVector ClassAttributeSet = new FastVector(5);
        ClassAttributeSet.addElement("normal");
        ClassAttributeSet.addElement("DOS");
        ClassAttributeSet.addElement("Probe");
        ClassAttributeSet.addElement("R2L");
        ClassAttributeSet.addElement("U2R");
        Attribute ClassAttribute = new Attribute("Class", ClassAttributeSet);
        
        
        FastVector protocolType = new FastVector(5);
        protocolType.addElement("TCP");
        protocolType.addElement("UDP");
        protocolType.addElement("ICMP");
        protocolType.addElement("IP");
        protocolType.addElement("ARP");
        Attribute protocolAttribute = new Attribute("protocolType", protocolType);
        
        FastVector flag = new FastVector(8);
        flag.addElement("SF");
        flag.addElement("REJ");
        flag.addElement("S0");
        flag.addElement("S1");
        flag.addElement("S2");
        flag.addElement("S3");
        flag.addElement("S4");
        flag.addElement("RSTO");
        flag.addElement("RSTR");
        flag.addElement("SS");
        flag.addElement("SH");
        flag.addElement("SHR");
        flag.addElement("OOS1");
        flag.addElement("OOS2");
        Attribute flagAttribute = new Attribute("flag", flag);
        
        
        FastVector service = new FastVector(58);
        service.addElement("rje");
        service.addElement("echo");
        service.addElement("discard");
        service.addElement("systat");
        service.addElement("daytime");
        service.addElement("netstat");
        service.addElement("ftp_data");
        service.addElement("ftp_cmd");
        service.addElement("ssh");
        service.addElement("telnet");
        service.addElement("priv_mail");
        service.addElement("smtp");
        service.addElement("printer");
        service.addElement("time");
        service.addElement("whois");
        service.addElement("xns");
        service.addElement("domain");
        service.addElement("priv_term");
        service.addElement("bootp_s");
        service.addElement("bootp_c");
        service.addElement("tftp");
        service.addElement("gopher");
        service.addElement("finger");
        service.addElement("http");
        service.addElement("kerberos");
        service.addElement("sundup");
        service.addElement("pop_2");
        service.addElement("pop_3");
        service.addElement("sunrpc");
        service.addElement("sftp");
        service.addElement("sql");
        service.addElement("netbios_ns");
        service.addElement("netbios_dgm");
        service.addElement("netbios_ssn");
        service.addElement("imap");
        service.addElement("smnp");
        service.addElement("vmnet");
        service.addElement("auth");
        service.addElement("ldap");
        
        service.addElement("bgp");
        service.addElement("csnet_ns");
        service.addElement("ctf");
        service.addElement("courier");
        service.addElement("https");
        service.addElement("rexec");
        service.addElement("rlogin");
        service.addElement("shell");
        service.addElement("klogin");
        service.addElement("kshell");
        service.addElement("rtsp");
        service.addElement("aol");
        service.addElement("http_8001");
        service.addElement("x11");
        service.addElement("tcp");
        service.addElement("udp");
        service.addElement("icmp");
        service.addElement("irc");
        service.addElement("Z39_50");
        Attribute serviceAtt = new Attribute("Service", service);
        
        // IDSRealtimeAttributeSet vector
        IDSmain.IDSRealtimeAttributeSet = new FastVector(26);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute00);
        IDSRealtimeAttributeSet.addElement(protocolAttribute);
        IDSRealtimeAttributeSet.addElement(flagAttribute);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute03);
        IDSRealtimeAttributeSet.addElement(serviceAtt);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute05);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute06);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute07);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute08);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute09);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute10);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute11);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute12);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute13);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute14);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute15);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute16);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute17);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute18);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute19);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute20);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute21);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute22);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute23);
        IDSRealtimeAttributeSet.addElement(RealtimeAttribute24);
        IDSRealtimeAttributeSet.addElement(ClassAttribute);		
		
        // New empty DT test Instances
        IDSInstances = new Instances("IDS", IDSRealtimeAttributeSet, 0);       

        IDSInstances.setClassIndex(25);
        
        
	}
	
	
	public void init() {
		Container contentPane = this.getContentPane();
		contentPane.repaint();
		String frameName = "IDS";
		mapPortService = new MapPortServices();
		packetForMonitor = new RawPacket();
		rawPacket = new RawPacket();
		featureEntries = new IDSFeatureEntry();
		connFeature = new ConnectionFeature();
		makeframe(frameName);
	}

	private boolean isJPcapExist(boolean jpcapExisted) {
		try {
			Class jpcapCaptor = Class.forName("jpcap.JpcapCaptor");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	private void makeframe(String frameName){
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		file.setMnemonic('F');
		
		JMenuItem openMenu = new JMenuItem("Open");
		openMenu.setMnemonic('O');
		openMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openMenu.addActionListener(this);
		file.add(openMenu);
		JMenuItem saveMenu = new JMenuItem("Save");
		saveMenu.setMnemonic('S');
		saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenu.addActionListener(this);
		file.add(saveMenu);
		
		JMenuItem clearMenu = new JMenuItem("Clear");
		clearMenu.setMnemonic('C');
		clearMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		clearMenu.addActionListener(this);
		file.add(clearMenu);
		
		JMenuItem exitMenu = new JMenuItem("Exit");
		exitMenu.setMnemonic('x');
		exitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		exitMenu.addActionListener(this);
		file.add(exitMenu);

		JMenu capture = new JMenu("Capture");
		capture.setMnemonic('C');
		JMenuItem startCap = new JMenuItem("Start Capturing");
		startCap.setMnemonic('S');
		startCap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		startCap.addActionListener(this);
		capture.add(startCap);
		JMenuItem endCap = new JMenuItem("Stop Capturing");
		endCap.setMnemonic('t');
		endCap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,	ActionEvent.CTRL_MASK));
		endCap.addActionListener(this);
		capture.add(endCap);

		JMenu tools = new JMenu("Tools");
		capture.setMnemonic('T');
		JMenuItem options = new JMenuItem("Options");
		options.setMnemonic('p');
		options.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		options.addActionListener(this);
		tools.add(options);
		JMenuItem pcapfile = new JMenuItem("Pcap extractor");
		pcapfile.setMnemonic('e');
		pcapfile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,	ActionEvent.CTRL_MASK));
		pcapfile.addActionListener(this);
		tools.add(pcapfile);
		
		JMenu help = new JMenu("Help");
		help.setMnemonic('H');
		JMenuItem why = new JMenuItem("Help contents");
		why.addActionListener(this);
		help.add(why);
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(this);
		help.add(about);

		menuBar.add(file);
		menuBar.add(capture);
		menuBar.add(tools);
		menuBar.add(help);
		
		
		
        intruderData = new IntruderTable();
        intruderTable = new JTable(intruderData);
//        intruderTable.getColumnModel().getColumn(0).setPreferredWidth(300);
     
        JScrollPane scrollIntruderTable = new JScrollPane(intruderTable);
        
        scrollIntruderTable.setPreferredSize(intruderTable.getPreferredSize());

        TimeSeriesCollection throughputData = new TimeSeriesCollection(throughput);

        JFreeChart chart = ChartFactory.createTimeSeriesChart("Network throughput", "Time", "Mbps", throughputData, true, true, false);
        final XYPlot plot = chart.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);
        JPanel throughputGrahp = new JPanel();
        ChartPanel throughputLabel = new ChartPanel(chart);
        throughputGrahp.add(throughputLabel);
		
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
		JSplitPane top = new JSplitPane(JSplitPane.VERTICAL_SPLIT, throughputGrahp,	scrollIntruderTable);
//		JSplitPane bottem = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top,
//				scroll13);

		top.setDividerLocation(425);
//		bottem.setDividerLocation(600);
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
		frame = new JFrame(frameName);
		
		frame.setJMenuBar(menuBar);

		frame.getContentPane().add(top); // bug fix for mac
		frame.setSize(710, 900);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.addWindowListener(this);
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		startCap.setEnabled(false);
		endCap.setEnabled(false);
		
		if (isJPcapExist(false)) {
			startCap.setEnabled(true);
			endCap.setEnabled(true);
		}
		
		
		
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof JMenuItem) {
			JMenuItem menu = (JMenuItem) source;
			if (menu.getText().equals("Open")) {
				int returnVal = chooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: "+ chooser.getSelectedFile().getName());
				} else if (returnVal == JFileChooser.CANCEL_OPTION)
					return;
				/*
				 try {
				 jpcap = JpcapCaptor.openFile(chooser.getSelectedFile().toString());
				 startCaptureThread();
				 } catch (IOException e1) {
				 // TODO Auto-generated catch block
				 e1.printStackTrace();
				 }
				 */
/***************************Doc file*****************************************
				
//				try {
//					List<EtherealPacket> packets = Parser.read(chooser
//							.getSelectedFile());
//					if (packets != null) {
//						for (EtherealPacket i : packets) {
//							addPacket(i);
//						}
//					} else {
//						JOptionPane
//								.showMessageDialog(
//										this,
//										"The file you selected: \""
//												+ chooser.getSelectedFile()
//														.getName()
//												+ "\" couldn't be parsed. It is either corupted or not a pcap file. Please choose another file.");
//					}
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					JOptionPane.showMessageDialog(frame,
//							"Can't open file: " + chooser.getSelectedFile().getPath() + "\n\n"
//									+ e.toString());
//					e.printStackTrace();
//				}
 * 
 ***********************************************************************************
 */
		
			} else if (menu.getText().equals("Save")) {
				int ret = chooser.showSaveDialog(frame);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					if (file.exists()) {
						if (JOptionPane.showConfirmDialog(frame, "Overwrite " + file.getName() + "?", "Overwrite?",	JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
							return;
						}
					}
					
/***********************************************************************************					
					try {
						Parser.write(model.getPackets(), file);
						
//						JpcapWriter writer = JpcapWriter.openDumpFile(jpcap,
//								file.getPath());
//
//						ArrayList<EtherealPacket> packets = model.getPackets();
//						for (EtherealPacket p : packets)
//							writer.writePacket(p.getJPCapPacket());
//
//						writer.close();
						
						
					} catch (java.io.IOException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(frame,
								"Can't save file: " + file.getPath() + "\n\n"
										+ e.toString());
					}
*************************************************************************************
*/
				}
				
				
			} else if (menu.getText().equals("Clear")) {
				clear();
			} else if (menu.getText().equals("Exit")) {
				stopCaptureThread();
				IDSmain.frame.dispose();
			} else if (menu.getText().equals("Start Capturing")) {

				try {
					jpcap = IDSCaptureDialog.getJpcap(frame);
					//Class c = Class.forName("jpcap.JpcapCaptor");
				//} catch (ClassNotFoundException e) {
					//JOptionPane.showMessageDialog(frame, errorString);
					//return;
				} catch (UnsatisfiedLinkError e) {
//					JOptionPane.showMessageDialog(frame, errorString);
					return;
				}

				if (jpcap != null)
					try {
						startCaptureThread();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			} else if (menu.getText().equals("Stop Capturing")) {
				if (IDSmain.runmode == 1){
					try{	
		                BufferedWriter writer ;
		                writer = new BufferedWriter(new FileWriter("Train.arff"));
						writer.write(IDSInstances.toString());
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
				stopCaptureThread();
			} else if (menu.getText().equals("Options")) {
				try {
					OptionsDialog.getOptionsDialog(frame);
				} catch (UnsatisfiedLinkError e) {
//					JOptionPane.showMessageDialog(frame, errorString);
					return;
				}

				if (jpcap != null)
					try {
						startCaptureThread();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
			} else if (menu.getText().equals("Pcap extractor")) {
				try {
//					JFrame nframe = new JFrame();
					TcpdumpDialog.getTcpdumpDialog();
					//Class c = Class.forName("jpcap.JpcapCaptor");
				//} catch (ClassNotFoundException e) {
					//JOptionPane.showMessageDialog(frame, errorString);
					//return;
				} catch (UnsatisfiedLinkError e) {
//					JOptionPane.showMessageDialog(frame, errorString);
					return;
				}

				if (jpcap != null)
					try {
						startCaptureThread();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				
			}else if (menu.getText().equals("Help contents")) {
				JOptionPane.showMessageDialog(frame, helpContents);
			} else if (menu.getText().equals("About")) {
				JOptionPane.showMessageDialog(frame, aboutString);
			}
		}
	}
	
	private void startCaptureThread() throws Exception{

        // System run mode
        File f = new File("Train.arff");
        if (IDSmain.runmode == 0) {
        
            BufferedReader train_file = new BufferedReader(new FileReader("Train.arff"));
            train = new Instances(train_file);
            train.setClassIndex(train.numAttributes()-1);
            train_file.close();	
            tree.buildClassifier(train);
            train.delete();
        }
        else if (IDSmain.runmode == 1 && f.exists()) {
            BufferedReader train_file = new BufferedReader(new FileReader("Train.arff"));
            IDSInstances = new Instances(train_file);
            IDSInstances.setClassIndex(IDSInstances.numAttributes()-1);
            train_file.close();	  
            BufferedWriter writer ;
            writer = new BufferedWriter(new FileWriter("Train-" + timeFormat.format(new Date((System.currentTimeMillis()))) + ".arff"));
			writer.write(IDSInstances.toString());
			writer.close();
            
        }

		
		if (captureThread != null)
			return;
		captureThread = new Thread(new Runnable() {
			//body of capture thread
			public void run() {
				while (captureThread != null) {
					if (jpcap.processPacket(1, handler) == 0)
						//stopCaptureThread();
						Thread.yield();
				}
				System.out.println("Exiting Capture Thread");
				jpcap.breakLoop();
			}
		});
		captureThread.setPriority(Thread.MIN_PRIORITY);
		captureThread.start();
		
		
//---------------------------------Code Added---------------------------------------
//Start processing collected packet thread.
		packetHandle = new PacketHandle();
		tPacketHandle = new Thread(packetHandle);
		tPacketHandle.start();
        //Start DT thread.
        DTProcessing = new Thread (new DecisionTree());
        DTProcessing.start();
        pNetMonitor = new Thread(new NetworkMonitor());
        pNetMonitor.start();
	
	}
	
	void stopCaptureThread() {
		captureThread = null;
		if(tPacketHandle != null) tPacketHandle.interrupt();
		tPacketHandle = null;
        if(DTProcessing != null) DTProcessing.interrupt();
        DTProcessing = null;
        if(pNetMonitor != null) pNetMonitor.interrupt();
        pNetMonitor = null;
	}
	
	private PacketReceiver handler = new PacketReceiver() {
		public void receivePacket(Packet packet) {
			addRawPacket(packet);
			packetForMonitor.addRawPacket(packet);
//			System.out.println(packetsForThroughput.size());

			
			
			//*************code tạm cho graph
//			curIndex++;
//			if (packet instanceof TCPPacket){
//				TCPPacket p = (TCPPacket)packet;
//				
//				
//			}
		}
	};


	public synchronized void addRawPacket(Packet packet){
		rawPacket.addRawPacket(packet);
	}
	
	
	public static void main(String[] args) {
		new IDSmain().init();		
	}
	
	
	public void clear() {
		intruderData.clear();
	}
	
	
	
	
	
	
	
	
	
	
	

	public void setName(String name) {
		super.setName(name);
		frame.setTitle(name);
	}

	public boolean isModifying() {
		return false;
	}

	public void showWindow() {
		frame.setVisible(true);
	}


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
