/**
 * SnmpUtils.java - a SNMP utility to generate a string for processing with SnmpRequest.java
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

package networktools.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;
import networktools.snmp.SnmpRequest;

/**
 * SNMP utility to translate request for processing on SnmpRequest.java<br />
 * does not require any constructor
 *
 * @author Heidir
 * @version 0.2, 1st January 2014
 */
public class SnmpUtils {
    /*
     * Standard SNMP templates containing default OIDs
     */
    public static final String snmp_sysDescr = "1.3.6.1.2.1.1.1.0";
    public static final String snmp_sysUptime = "1.3.6.1.2.1.1.3.0";
    public static final String snmp_sysContact = "1.3.6.1.2.1.1.4.0";
    public static final String snmp_sysName = "1.3.6.1.2.1.1.5.0";
    public static final String snmp_sysLocation = "1.3.6.1.2.1.1.6.0";
    
    /*
     * Error ID used when errors occur
     */
    public static final String ERR_NODEVICEIP = "No host specified.";
    public static final String ERR_NOPORT = "No device port set. Default SNMP port is 161.";
    public static final String ERR_NOCOMMUNITY = "No community string set.";
    public static final String ERR_NOCUSTOMOID = "No Object ID Specified.";
    public static final String ERR_TIMEOUT = "Request Failed: Error: Request Timed Out To Remote Host.";
    public static final String ERR_INVALIDOID = "Invalid OID Format.";
    public static final String ERR_DEFAULTRETRY = "Default retry value used: 2";
    public static final String ERR_DEFAULTTIMEOUT = "Default timeout value used: 5 seconds / 5000 milliseconds";
    public static final String ERR_NOPRIVPHRASE = "Privacy Passphrase not defined";
    public static final String ERR_NOAUTHPHRASE = "Authentication Passphrase not defined";
    
    /*
     * SNMP Versions supported
     */
    public static final String SNMPv1 = "1";
    public static final String SNMPv2c = "2c";
    public static final String SNMPv3 = "3";
    
    /*
     * Authentication Protocol
     */
    public static final String MD5 = "MD5";
    public static final String SHA = "SHA";
    
    /*
     * Encryption protocol
     */
    public static final String DES = "DES";
    public static final String DESEDE = "DESEDE";
    public static final String AES128 = "AES128";
    public static final String AES192 = "AES192";
    public static final String AES256 = "AES256";
    
    /**
     *
     * getSnmpVersion1 method
     * <br />
     * This method prepares SNMPv1 request with System Uptime OID used for processing via SnmpRequest.java
     *
     * @see SnmpUtils
     * @param host IP address or FQDN of remote host
     * @param port Port on remote host. Default is 161
     * @param community Community string used to poll request
     * @param timeout Timeout set to wait for request to be replied
     * @param retries Number of retries to be performed when no reply is received
     * @param output JTextArea object to be used to display the output. SnmpRequest only provides output via
     * System.out.println(), this will be captured and redirected to JTextArea
     * @since version 0.2
     */
    public static void getSnmpVersion1(String host, int port, String community, int timeout, int retries, JTextArea output) {
        System.setOut(new PrintStreamCapturer(output, System.out));
        String[] msg = {"-r", String.valueOf(retries), "-t", String.valueOf(timeout), "-p", "GET", "-c", community, "-v", "1", "udp:"+host+"/"+String.valueOf(port), snmp_sysUptime};
        SnmpRequest.request(msg);
    }
    
    /**
     *
     * getSnmpVersion2c method
     * <br />
     * This method prepares SNMPv2c request with System Uptime OID used for processing via SnmpRequest.java
     *
     * @see SnmpUtils
     * @param host IP address or FQDN of remote host
     * @param port Port on remote host. Default is 161
     * @param community Community string used to poll request
     * @param timeout Timeout set to wait for request to be replied
     * @param retries Number of retries to be performed when no reply is received
     * @param output JTextArea object to be used to display the output. SnmpRequest only provides output via
     * System.out.println(), this will be captured and redirected to JTextArea
     * @since version 0.2
     */
    public static void getSnmpVersion2c(String host, int port, String community, int timeout, int retries, JTextArea output) {
        System.setOut(new PrintStreamCapturer(output, System.out));
        String[] msg = {"-r", String.valueOf(retries), "-t", String.valueOf(timeout), "-p", "GET", "-c", community, "-v", "2c", "udp:"+host+"/"+String.valueOf(port), snmp_sysUptime};
        SnmpRequest.request(msg);
    }
    
    /**
     *
     * getSnmpVersion1 method
     * <br />
     * This method prepares SNMPv1 request with custom OID for processing via SnmpRequest.java
     *
     * @see SnmpUtils
     * @param host IP address or FQDN of remote host
     * @param port Port on remote host. Default is 161
     * @param community Community string used to poll request
     * @param timeout Timeout set to wait for request to be replied
     * @param retries Number of retries to be performed when no reply is received
     * @param customOID SNMP OID that is used to perform request
     * @param output JTextArea object to be used to display the output. SnmpRequest only provides output via
     * System.out.println(), this will be captured and redirected to JTextArea
     * @since version 0.2
     */
    public static void getSnmpVersion1(String host, int port, String community, int timeout, int retries, String customOid, JTextArea output) {
        System.setOut(new PrintStreamCapturer(output, System.out));
        String[] msg = {"-r", String.valueOf(retries), "-t", String.valueOf(timeout), "-p", "GET", "-c", community, "-v", "1", "udp:"+host+"/"+String.valueOf(port), customOid};
        SnmpRequest.request(msg);
    }
    
    /**
     *
     * getSnmpVersion2c method
     * <br />
     * This method prepares SNMPv2c request with custom OID for processing via SnmpRequest.java
     *
     * @see SnmpUtils
     * @param host IP address or FQDN of remote host
     * @param port Port on remote host. Default is 161
     * @param community Community string used to poll request
     * @param timeout Timeout set to wait for request to be replied
     * @param retries Number of retries to be performed when no reply is received
     * @param customOID SNMP OID that is used to perform request
     * @param output JTextArea object to be used to display the output. SnmpRequest only provides output via
     * System.out.println(), this will be captured and redirected to JTextArea
     * @since version 0.2
     */
    public static void getSnmpVersion2c(String host, int port, String community, int timeout, int retries, String customOid, JTextArea output) {
        System.setOut(new PrintStreamCapturer(output, System.out));
        String[] msg = {"-r", String.valueOf(retries), "-t", String.valueOf(timeout), "-p", "GET", "-c", community, "-v", "2c", "udp:"+host+"/"+String.valueOf(port), customOid};
        SnmpRequest.request(msg);
    }
    
    /**
     *
     * getSnmpVersion3 method
     * <br />
     * This method prepares SNMPv3 request with custom OID for processing via SnmpRequest.java
     *
     * @see SnmpUtils
     * @param host IP address or FQDN of remote host
     * @param port Port on remote host. Default is 161
     * @param user Security Name used to poll request
     * @param timeout Timeout set to wait for request to be replied
     * @param retries Number of retries to be performed when no reply is received
     * @param privProtocol Encryption protocol used in the request, DES, DESEDE(3DES), AES128, AES192, 
     * AES256
     * @param privPassword Key used to encrypt the data portion of the message being sent
     * @param authProtocol Authentication protocol used in the request MD5, SHA
     * @param authPassword Key used to sign the message sent
     * @param output JTextArea object to be used to display the output. SnmpRequest only provides output via
     * System.out.println(), this will be captured and redirected to JTextArea
     * @since version 0.2
     */
    public static void getSnmpVersion3(String host, int port, String user, int timeout, int retries, String privProtocol, String privPassword, String authProtocol, String authPassword, JTextArea output) {
        System.setOut(new PrintStreamCapturer(output, System.out));
        String[] msg = {"-a", authProtocol, "-A", authPassword, "-x", privProtocol ,"-X", privPassword, "-u", user, "-p", "GET", host+"/"+String.valueOf(port), snmp_sysUptime};
        SnmpRequest.request(msg);
    }
    
    /**
     *
     * getSnmpVersion3 method
     * <br />
     * This method prepares SNMPv3 request with system Uptime OID as default for processing via SnmpRequest.java
     *
     * @see SnmpUtils
     * @param host IP address or FQDN of remote host
     * @param port Port on remote host. Default is 161
     * @param user Security Name used to poll request
     * @param timeout Timeout set to wait for request to be replied
     * @param retries Number of retries to be performed when no reply is received
     * @param privProtocol Encryption protocol used in the request, DES, DESEDE(3DES), AES128, AES192, 
     * AES256
     * @param privPassword Key used to encrypt the data portion of the message being sent
     * @param authProtocol Authentication protocol used in the request MD5, SHA
     * @param authPassword Key used to sign the message sent
     * @param customOID SNMP OID that is used to perform request
     * @param output JTextArea object to be used to display the output. SnmpRequest only provides output via
     * System.out.println(), this will be captured and redirected to JTextArea
     * @since version 0.2
     */
    public static void getSnmpVersion3(String host, int port, String user, int timeout, int retries, String privProtocol, String privPassword, String authProtocol, String authPassword, String customOid, JTextArea output) {
        System.setOut(new PrintStreamCapturer(output, System.out));
        String[] msg = {"-a", authProtocol, "-A", authPassword, "-x", privProtocol ,"-X", privPassword, "-u", user, "-p", "GET", host+"/"+String.valueOf(port), customOid};
        SnmpRequest.request(msg);
    }
    
    /**
     *
     * currentDate method
     * <br />
     * This method returns the current date and time in simple format as a string
     *
     * @see SnmpUtils
     * @return String representation of the current data and time<br />
     * Example: 30.12.2013 14:00:01  // dd.MM.yyyy HH:mm:ss
     * @since version 0.2
     */
    public static String currentDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(date);
    }
}