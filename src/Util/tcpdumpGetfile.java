package Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Queue;

import TcpdumpExtractor.TcpdumpDialog;

public class tcpdumpGetfile{
	public static String tempString =null;
	private static String[] tempStringSplit;
	private static tcpdumpPacketFilter newTcpdumpPacketFilter;
	public static Queue<tcpdumpPacketFilter> dumpList = new LinkedList<tcpdumpPacketFilter>();

	public void getFromFile() {
		BufferedReader newfile = null;
		try {
			newfile = new BufferedReader(new FileReader(TcpdumpDialog.identificationsAddress));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
				String temp=null;
				try {
					temp = newfile.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (temp.equals("dm den day la het file")){
					break;
				}
				if(temp.equals(""))
					continue;
				tempStringSplit = temp.split(" ");
				if (tempStringSplit[0].equals("Date:")){
					tempString = tempStringSplit[1];
				}
				if((tempStringSplit[0].equals("Category:")) || (tempStringSplit[0].equals("Start_Time:")) || (tempStringSplit[0].equals("Duration:")) || (tempStringSplit[0].equals("Attacker:"))){
					if(tempStringSplit[1].equals("console") || tempStringSplit[1].equals("hobbes_console") || tempStringSplit[1].equals("r2l")|| tempStringSplit[1].equals("r2l")||tempStringSplit[1].equals("login.session.x.x")||tempStringSplit[1].equals("u2r")){
						try {
							temp = newfile.readLine();
							temp = newfile.readLine();
							temp = newfile.readLine();
							temp = newfile.readLine();
							temp = newfile.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						continue;
					}
					tempString = tempString + "-" + tempStringSplit[1];
				}else if(tempStringSplit[0].equals("Victim:")){
					tempString = tempString + "-" + tempStringSplit[1];
					try {
						newTcpdumpPacketFilter = new tcpdumpPacketFilter(tempString);
					} catch (UnknownHostException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dumpList.add(newTcpdumpPacketFilter);
					newTcpdumpPacketFilter = null;
				}
		}

		
	}
		
}
