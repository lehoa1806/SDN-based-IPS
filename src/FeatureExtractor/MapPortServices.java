package FeatureExtractor;

import java.util.HashMap;
import java.util.Map;

public class MapPortServices {
	private Map<Integer, String> Port2Service =  new HashMap<Integer, String>();
	
	public MapPortServices() {
		// TODO Auto-generated constructor stub
		InitMap();
	}
	
	private void InitMap(){
		int i;
		for(i = 0; i < 7000; i ++){
			switch (i){
			case 5:
				Port2Service.put(i, "rje");
				break;
			case 7:
				Port2Service.put(i, "echo");
				break;
			case 9:
				Port2Service.put(i, "discard");
				break;
			case 11:
				Port2Service.put(i, "systat");
				break;
			case 13:
				Port2Service.put(i, "daytime");
				break;
			case 15:
				Port2Service.put(i, "netstat");
				break;
			case 20:
				Port2Service.put(i, "ftp_data");
				break;
			case 21:
				Port2Service.put(i, "ftp_cmd");
				break;
			case 22:
				Port2Service.put(i, "ssh");
				break;
			case 23:
				Port2Service.put(i, "telnet");
				break;	
			case 24:
				Port2Service.put(i, "priv_mail");
				break;
			case 25:
				Port2Service.put(i, "smtp");
				break;
			case 35:
				Port2Service.put(i, "printer");
				break;
			case 37:
				Port2Service.put(i, "time");
				break;
			case 43:
				Port2Service.put(i, "whois");
				break;
			case 52:
				Port2Service.put(i, "xns");
				break;
			case 53:
				Port2Service.put(i, "domain");
				break;
			case 56:
				Port2Service.put(i, "auth");
				break;
			case 57:
				Port2Service.put(i, "priv_term");
				break;
			case 67:
				Port2Service.put(i, "bootp_s");
				break;
			case 68:
				Port2Service.put(i, "bootp_c");
				break;
			case 69:
				Port2Service.put(i, "tftp");
				break;
			case 70:
				Port2Service.put(i, "gopher");
				break;
			case 79:
				Port2Service.put(i, "finger");
				break;
			case 80:
				Port2Service.put(i, "http");
				break;
			case 88:
				Port2Service.put(i, "kerberos");
				break;
			case 95:
				Port2Service.put(i, "sundup");
				break;
			case 109:
				Port2Service.put(i, "pop_2");
				break;
			case 110:
				Port2Service.put(i, "pop_3");
				break;
			case 111:
				Port2Service.put(i, "sunrpc");
				break;
			case 113:
				Port2Service.put(i, "auth");
				break;
			case 115:
				Port2Service.put(i, "sftp");
				break;
			case 118:
				Port2Service.put(i, "sql");
				break;
			case 137:
				Port2Service.put(i, "netbios_ns");
				break;
			case 138:
				Port2Service.put(i, "netbios_dgm");
				break;
			case 139:
				Port2Service.put(i, "netbios_ssn");
				break;
			case 143:
				Port2Service.put(i, "imap");
				break;
			case 161:
				Port2Service.put(i, "smnp");
				break;
			case 175:
				Port2Service.put(i, "vmnet");
				break;
			case 210:
				Port2Service.put(i, "Z39_50");
				break;
			case 370:
				Port2Service.put(i, "auth");
				break;
			case 389:
				Port2Service.put(i, "ldap");
				break;
			case 751:
				Port2Service.put(i, "auth");
				break;

			case 179:
				Port2Service.put(i, "bgp");
				break;
			case 105:
				Port2Service.put(i, "csnet_ns");
				break;
			case 84:
				Port2Service.put(i, "ctf");
				break;
			case 530:
				Port2Service.put(i, "courier");
				break;

			case 443:
				Port2Service.put(i, "https");
				break;
			case 512:
				Port2Service.put(i, "rexec");
				break;
			case 513:
				Port2Service.put(i, "rlogin");
				break;
			case 514:
				Port2Service.put(i, "shell");
				break;
			case 543:
				Port2Service.put(i, "klogin");
				break;
			case 544:
				Port2Service.put(i, "kshell");
				break;
			case 554:
				Port2Service.put(i, "rtsp");
				break;
			case 531:
				Port2Service.put(i, "aol");
				break;
			case 1501:
				Port2Service.put(i, "auth");
				break;
			case 1645:
				Port2Service.put(i, "auth");
				break;
			case 8001:
				Port2Service.put(i, "http_8001");
				break;
			}
			
			if(i >= 6000 && i <= 6063)
				Port2Service.put(i, "x11");
			if(i >= 6665 && i <= 6669)
				Port2Service.put(i, "irc");
		}
	}

	public String mapService(int sercivePort){
		if(this.Port2Service.containsKey(sercivePort))
			return this.Port2Service.get(sercivePort);
		else 
			return null;
	}
}
