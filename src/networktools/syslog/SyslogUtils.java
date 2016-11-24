/**
 * SyslogUtils.java - a syslog utility for receiving and sending messages
 * 
 * This code is copyright (c) Muhammad Heidir 2013
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) 
 * any later version.  A HTML version of the GNU General Public License can be
 * seen at http://www.gnu.org/licenses/gpl.html
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 *  
 * @author Muhammad Heidir
 * @version 0.2, 1st January 2014
 */

package networktools.syslog;

import java.util.List;
import networktools.syslog.SyslogOutServerEventHandler;
import javax.swing.JTextArea;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerEventHandlerIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;

/**
 * SyslogUtils class to Start and Stop listener<br />
 * Static method for sending of syslog messages included
 *
 * @author Heidir
 * @version 0.2, 1st January 2014
 */
public class SyslogUtils {
    private SyslogServerConfigIF syslogServerConf;
    private SyslogServerIF syslogServer;
    private String syslogListeningIP;
    private final String syslogProtocol = "udp";
    private SyslogServerEventHandlerIF eventHandler;
    
    /**
     * SyslogUtils constructor
     * <br />
     * This method initialises SyslogUtils class
     *
     * @see SyslogUtils
     * @since version 0.2
     */
    public SyslogUtils() {
        this.syslogServer = SyslogServer.getInstance(syslogProtocol);
        this.syslogServerConf = syslogServer.getConfig();
    }
    
    /**
     * startSyslogListener method
     * <br />
     * This method starts syslog listener to receive messages on the designated IP address
     *
     * @param syslogListeningIP IP address of FQDN of host
     * @param syslogPort TCP/IP port for syslog messages to listen on
     * @param syslogResults JTextArea where output of the messages are displayed
     * @see SyslogUtils
     * @since version 0.2
     */
    public void startSyslogListener(String syslogListeningIP, int syslogPort, JTextArea syslogResults) {
        this.syslogListeningIP = syslogListeningIP;
        
        // Syslog Listening Information
        /*
        try {
            syslogServer = SyslogServer.getInstance(syslogProtocol);
        } catch (Exception e) {
            syslogServer = SyslogServer.createInstance(syslogProtocol, syslogServerConf);
        } finally {
            syslogServer = SyslogServer.createInstance(syslogProtocol, syslogServerConf);
        }*/
        
        //syslogServerConf = syslogServer.getConfig();
        
        syslogServerConf.setHost(syslogListeningIP);
        syslogServerConf.setPort(syslogPort);
        
        eventHandler = new SyslogOutServerEventHandler(syslogResults);
        syslogServerConf.addEventHandler(eventHandler);
        
        SyslogServer.getThreadedInstance(syslogProtocol);
    }
    
    /**
     * stopSyslogListener method
     * <br />
     * This method stops syslog listener
     *
     * @see SyslogUtils
     * @since version 0.2
     */
    public void stopSyslogListener() {
        syslogServerConf.removeEventHandler(eventHandler);
        eventHandler.destroy(syslogServer);
        //SyslogServer.destroyInstance(syslogProtocol);
        syslogServer.shutdown();
    }
    
    /**
     * startSyslogListener static method
     * <br />
     * This method sends syslog messages to remote host
     *
     * @param syslogHost IP address of FQDN of host
     * @param syslogPort TCP/IP port for syslog messages to listen on
     * @param syslogSeverity integer value from 0 to 7 ranging from Emergency, Alert, Critical, Error, Warning,
     * <br />Notice, Informational, Debug
     * @param syslogMessage String of the syslog message to be sent
     * @see SyslogUtils
     * @since version 0.2
     */
    public static String sendSyslogMessage(String syslogHost, int syslogPort, int syslogSeverity, String syslogMessage) {
        SyslogConfigIF syslogConf = new UDPNetSyslogConfig(syslogHost, syslogPort);
        syslogConf.setFacility(Syslog.FACILITY_LOCAL0);
        
        SyslogIF syslogClient = Syslog.createInstance(syslogHost, syslogConf);
        
        switch (syslogSeverity) {
            case 0:
                syslogClient.emergency(syslogMessage);
                break;
            case 1:
                syslogClient.alert(syslogMessage);
                break;
            case 2:
                syslogClient.critical(syslogMessage);
                break;
            case 3:
                syslogClient.error(syslogMessage);
                break;
            case 4:
                syslogClient.warn(syslogMessage);
                break;
            case 5:
                syslogClient.notice(syslogMessage);
                break;
            case 6:
                syslogClient.info(syslogMessage);
                break;
            case 7:
                syslogClient.debug(syslogMessage);
                break;
        }
       syslogClient.shutdown();
       Syslog.destroyInstance(syslogClient);
        
       return "\nSyslog sessage sent to >> " + syslogHost + ":" + syslogPort;
    }
}
