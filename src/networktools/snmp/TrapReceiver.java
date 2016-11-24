/**
 * TrapReceiver.java - a SNMP trap receiver interface for creating a listener thread
 * This program contains a compilation of basic 
 * Network Test Tools for verification of NTP, SNMP and Syslog on appliances within the network
 * 
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
 * @source 0.2, 1st January 2014
 */

package networktools.snmp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import networktools.utils.PrintStreamCapturer;

/**
 * TrapReceiver is a runnable class for generating of threads for Trap listener
 *
 * @author Heidir
 * @version 0.2, 1st January 2014
 */
public class TrapReceiver implements Runnable {
    private static SnmpRequest listenRequest;
    private static String hostIP;
    private static int hostPort;
    private static String user;
    private static JTextArea txtArea;
    private static Thread thr;
    private static TrapReceiver tr;
    private static volatile boolean isStopping = false;
    private static String[] msg;
    
    /**
     * TrapReceiver constructor
     * <br />
     * This method initialises TrapReceiver before a thread is created
     *
     * @see TrapReceiver
     * @since version 0.2
     */
    public TrapReceiver() {
        this.hostIP = "";
        this.hostPort = 0;
        this.user = "";
        this.txtArea = null;
    }
    
    /**
     * startReceiver static method
     * <br />
     * This method starts SNMP trap receiver
     *
     * @param host IP address or FQDN of remote host
     * @param port Port used to listen for incoming trap messages
     * @param output JTextArea object for displaying of output
     * @see TrapReceiver
     * @since version 0.2
     */
    public static void startReceiver(String host, int port, JTextArea output) {
        hostIP = host;
        hostPort = port;
        txtArea = output;
        
        System.setOut(new PrintStreamCapturer(txtArea, System.out));
        String msg[] = {"-Ol", hostIP+"/"+String.valueOf(hostPort)};
        
        tr = new TrapReceiver();
        tr.msg = msg;
        thr = new Thread(tr);
        thr.start();
    }

    /**
     * startReceiver static method
     * <br />
     * This method starts SNMP trap receiver with security name defined
     *
     * @param host IP address or FQDN of remote host
     * @param port Port used to listen for incoming trap messages
     * @param uSecurityName Security Name defined
     * @param output JTextArea object for displaying of output
     * @see TrapReceiver
     * @since version 0.2
     */
    public static void startReceiver(String host, int port, String uSecurityName, JTextArea output) {
        hostIP = host;
        hostPort = port;
        txtArea = output;
        user = uSecurityName;
        
        System.setOut(new PrintStreamCapturer(txtArea, System.out));
        String msg[] = {"-u", user, "-Ol", hostIP+"/"+String.valueOf(hostPort)};
        
        tr = new TrapReceiver();
        tr.msg = msg;
        thr = new Thread(tr);
        thr.start();
    }
    
    /**
     * stopReceiver static method
     * <br />
     * This method stops SNMP trap receiver
     *
     * @see TrapReceiver
     * @since version 0.2
     */
    public static void stopReceiver() {
        stop();
    }
    
    /**
     * run method
     * <br />
     * This method starts listening interface. Should not be used
     *
     * @param host IP address or FQDN of remote host
     * @param port Port used to listen for incoming trap messages
     * @param output JTextArea object for displaying of output
     * @see TrapReceiver
     * @since version 0.2
     */
    @Override
    public void run() {
        try {
            listenRequest = new SnmpRequest(tr.msg);
            //isStarted = listenRequest.listen();
            
            listenRequest.listen();
            
            if (isStopping) throw new InterruptedException();
            
        } catch (IOException ex) {
            Logger.getLogger(TrapReceiver.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (InterruptedException ex) {
            //System.out.println("Thread interrupted in TrapReceiver");
            listenRequest.stop();
            
        } catch (NullPointerException ex) {
            //System.out.println(ex.getMessage());
            
        } catch (IllegalArgumentException ex) {
            System.out.println("Unable to accept transport method. Please use IPv4 only.");
        }
    }
    
    /**
     * stop method
     * <br />
     * This method stops listening interface. Should not be used.
     *
     * @see TrapReceiver
     * @since version 0.2
     */
    public static void stop() {
        isStopping = true;
        thr.interrupt();
    }
}
