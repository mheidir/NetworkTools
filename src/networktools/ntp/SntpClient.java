/**
 * SntpClient.java - NTP client for requesting time from time server through the network
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
 */

package networktools.ntp;

import networktools.ntp.NtpMessage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

// Also depends on class NtpMessage

/**
  * SNTP algorithm specified in RFC 2030.
  * 
  * The code is based on the Java implementation of an SNTP client
  * copyrighted under the terms of the GPL by Adam Buckley in 2004.
  *
  * Lots of information at the home page of David L. Mills:
  * http://www.eecis.udel.edu/~mills
  * Source: http://cs.fit.edu/~ryan/java/programs/net/SntpClient-java.html
  */
  
 /*
       Timestamp Name          ID   When Generated
       ------------------------------------------------------------
       Originate Timestamp     T1   time request sent by client
       Receive Timestamp       T2   time request received by server
       Transmit Timestamp      T3   time reply sent by server
       Destination Timestamp   T4   time reply received by client
 
    The roundtrip delay d and local clock offset t are defined as follows:
 
       delay = (T4 - T1) - (T3 - T2)   offset = ((T2 - T1) + (T3 - T4)) / 2
 
  */
 
 /*
   RFS 1305 timestamp format: seconds relative to 0h on 1 January 1900.
  */

/**
 * The SNTP client class to perform NTP requests via the network
 *
 * @author Heidir
 * @version 0.1, 24th December 2013
 */
public class SntpClient {
  
    //private static final int PORT = 123;  // SNTP UDP port
    /**
     *
     * sendRequest method
     * <br />
     * This static method generate a request based on the parameters required
     *
     * @param host IP address of the remote time server, can be in FQDN or decimal notation
     * @param port Default NTP port is 123, due to OS restrictions on Linux and OSX, alternative port number
     * above 1024 can be defined
     * @param timeout Value must be in milliseconds. Default timeout is 5000 milliseconds. Timeout can be 
     * defined in cases where high latency is expected
     * @return String[] array is returned containing the output such as below:<br />
     * NTP server: 1.sg.pool.ntp.org/103.4.109.106<br />
     * Round-trip delay: -2208988799883.04 ms<br />
     * Local clock offset: +1104494398740.60 ms<br />
     * Leap indicator: 0<br />
     * Version: 3<br />
     * Mode: 4<br />
     * Stratum: 4<br />
     * Poll: 0<br />
     * Precision: -22 (2.4E-7 seconds)<br />
     * Root delay: 140.40 ms<br />
     * Root dispersion: 73.27 msv<br />
     * Reference identifier: 54.251.61.122<br />
     * Reference timestamp: 03-Jan-2014 06:17:18.155597<br />
     * Originate timestamp: 03-Jan-2014 06:47:02.823000<br />
     * Receive timestamp:   03-Jan-2014 06:47:01.622086<br />
     * Transmit timestamp:  03-Jan-2014 06:47:01.622123<br />
     * @since version 0.1 24th December 2013
     */
    public static String[] sendRequest(String host, int port, int timeout) {
        String serverName = host;
        String[] msgResults = new String[4];
        
        try {
            final DatagramSocket socket = new DatagramSocket();
            final InetAddress address = InetAddress.getByName(serverName);
            final byte[] buffer = new NtpMessage().toByteArray();
 
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.setSoTimeout(timeout);
            socket.send(packet);
 
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
 	
            // Immediately record the incoming timestamp
            final double destinationTimestamp = System.currentTimeMillis()/1000.0;//NtpMessage.now();
            final NtpMessage msg = new NtpMessage(packet.getData());
            socket.close();

            /* Presumably, msg.orginateTimestamp unchanged by server. */
            if (msg == null) {
                
            }
 
            // Formula for delay according to the RFC2030 errata
            final double roundTripDelay = (destinationTimestamp - msg.originateTimestamp) -
                (msg.transmitTimestamp - msg.receiveTimestamp);
 		
            // The amount the server is ahead of the client
            final double localClockOffset = ((msg.receiveTimestamp - msg.originateTimestamp) +
                (msg.transmitTimestamp - destinationTimestamp)) / 2;
            
            String addressParsed = "";
            if (address.toString().startsWith("/"))
                addressParsed = address.toString().substring(1);
            else
                addressParsed = address.toString();
            
            // Display response
            msgResults[0] = String.format("NTP server: %s", addressParsed);
            msgResults[1] = String.format("Round-trip delay: %+9.2f ms", 1000*roundTripDelay);
            msgResults[2] = String.format("Local clock offset: %+9.2f ms", 1000*localClockOffset);
            
            msgResults[3] = msg.toString();
            
            return msgResults;
            
        } catch (SocketTimeoutException ex) {
            String[] msgResult = {"Timeout reached. No response from host."};
                return msgResult;
                
        } catch (Exception ex) {
            //Logger.getLogger(NToolGUI.class.getName()).log(Level.SEVERE, null, ex);
            return msgResults;
        }
    }
 }
