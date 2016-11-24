/**
 * NToolGUI - a GUI interface for Network Tools  
 * This program contains a compilation of basic 
 * Network Test Tools for verification of NTP, SNMP and Syslog on appliances within the network
 * 
 * NTP Test Tool - A Java implementation of NTP Client
 * SNMP Test Tool - A Java implementation of SNMP Polling
 * Syslog Test Tool - A Java implementation for sending Syslog messages or receive Syslog messages
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

package networktools;

import networktools.utils.PrintStreamCapturer;
import networktools.snmp.SnmpRequest;
import networktools.snmp.TrapReceiver;
import networktools.utils.ArrayUtils;
import networktools.utils.Utils;
import networktools.utils.SnmpUtils;
import networktools.syslog.SyslogUtils;
import networktools.ntp.SntpClient;
import networktools.ntp.NtpServer;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * NToolGUI provides user-friendly interface and event-driven methods to process user input
 *
 * @author Heidir
 * @version 0.2, 1st January 2014
 */
public class NToolGUI {
    
    private int syslogSeverity;
    private int selectedSnmpVersion=0;
    private final int SYSDESCR = 0;
    private final int SYSUPTIME = 1;
    private final int SYSCONTACT = 2;
    private final int SYSNAME = 3;
    private final int SYSLOCATION = 4;
    private final int CUSTOMOID = 5;
    private int ntpPort = 123;
    private NtpServer ntp;
    private SyslogUtils syslogUtils;
    
    private JLabel lblNTPServer = new JLabel("NTP Server:");
    private JLabel lblSetTimeOut = new JLabel("Set TimeOut:");
    private JLabel lblNTPPort = new JLabel("Port:");
    private JLabel lblNtpListeningIP = new JLabel("Listening IP:");
    private JLabel lblNtpListeningPort = new JLabel("Listening Port:");
    private JLabel lblMilliSeconds = new JLabel("milliseconds (Default: 5000ms)");
    private JLabel lblSyslogHost = new JLabel("Syslog Host:");
    private JLabel lblSyslogMessage = new JLabel("Message:");
    private JLabel lblSyslogPort = new JLabel("Port:");
    private JLabel lblSyslogSeverity = new JLabel("Severity:");
    private JLabel lblSyslogListeningIP = new JLabel("Listening IP:");
    private JLabel lblSyslogListeningPort = new JLabel("Listening Port:");
    private JLabel lblDeviceIP = new JLabel("Device IP/Port:");
    private JLabel lblSNMPVersion = new JLabel("SNMP Version:");
    private JLabel lblCommunity = new JLabel("Community:");
    private JLabel lblAuthPassphrase = new JLabel("V3 Authentication/Password:");
    private JLabel lblPrivPassphrase = new JLabel("V3 Encryption/Key:");
    private JLabel lblLogo = new JLabel();
    private JLabel lblSnmpRetries = new JLabel("Retries:");
    private JLabel lblSnmpTimeout = new JLabel("Timeout:");
    private JLabel lblSnmpTrapListenerIPPort = new JLabel("Listening IP Address/Port:");
    private JLabel lblSnmpTrapListenerSecurityName = new JLabel("Security Name:");
    private JLabel lblSnmpTrapHostIPPort = new JLabel("Host IP/Port:");
    private JLabel lblSnmpTrapVersion = new JLabel("Trap Version:");
    private JLabel lblSnmpTrapSecurityName = new JLabel("Security Name:");
    
    private JTextField txtNTPServer = new JTextField();
    private JTextField txtSetTimeOut = new JTextField();
    private JTextField txtNTPPort = new JTextField();
    private JTextField txtNtpListeningPort = new JTextField();
    private JTextField txtSyslogHost = new JTextField();
    private JTextField txtSyslogPort = new JTextField();
    private JTextField txtSyslogMessage = new JTextField();
    private JTextField txtSyslogListeningPort = new JTextField();
    private JTextField txtDeviceIP = new JTextField();
    private JTextField txtDevicePort = new JTextField();
    private JTextField txtCommunity = new JTextField();
    private JTextField txtAuthPassphrase = new JTextField();
    private JTextField txtPrivPassphrase = new JTextField();
    private JTextField txtCustomOID = new JTextField();
    private JTextField txtSnmpRetries = new JTextField();
    private JTextField txtSnmpTimeout = new JTextField();
    private JTextField txtSnmpTrapListenerPort = new JTextField();
    private JTextField txtSnmpTrapListenerSecurityName = new JTextField();
    private JTextField txtSnmpTrapHostIP = new JTextField();
    private JTextField txtSnmpTrapHostPort = new JTextField();
    private JTextField txtSnmpTrapSecurityName = new JTextField();
    
    private JButton btnNTPRequest = new JButton("NTP Request");
    private JButton btnNtpListen = new JButton("Start NTP server");
    private JButton btnSyslogSend = new JButton("Send Syslog Message");
    private JButton btnSyslogListen = new JButton("Start Syslog Listener");
    private JButton btnSNMPGet = new JButton("3. >> Start");
    private JButton btnSNMPClearResults = new JButton("Clear Results");
    private JButton btnSnmpTrapListenerStart = new JButton("Start Trap Listener");
    private JButton btnSnmpTrapSenderStart = new JButton("Send Trap");
    
    private String[] severityLevels = {"0=Emergency", "1=Alert", "2=Critical", "3=Error", "4=Warning", "5=Notice", "6=Informational", "7=Debug"};
    private JComboBox cboSyslogSeverity = new JComboBox(severityLevels);
    
    private String[] snmpVersion = {"SNMPv1", "SNMPv2c", "SNMPv3"};
    private JComboBox cboSNMPVersion = new JComboBox(snmpVersion);
    private String[] v3Authentication = {"MD5", "SHA"};
    private JComboBox cbov3Authentication = new JComboBox(v3Authentication);
    private String[] v3Encryption = {"DES", "3DES", "AES128", "AES192", "AES256"};
    private JComboBox cbov3Encryption = new JComboBox(v3Encryption);
    private String[] snmpOIDs = {"Read System Description - " + SnmpUtils.snmp_sysDescr, 
                                "Read System Uptime - " + SnmpUtils.snmp_sysUptime,
                                "Read System Contact - " + SnmpUtils.snmp_sysContact,
                                "Read System Name - " + SnmpUtils.snmp_sysName,
                                "Read System Location - " + SnmpUtils.snmp_sysLocation,
                                "Read Custom OID"};
    private JComboBox cboSnmpOIDs = new JComboBox(snmpOIDs);
    
    private String selectedAuthProtocol = SnmpUtils.MD5;
    private String selectedPrivProtocol = SnmpUtils.DES;
    private int selectedOID = 1;
    
    private JComboBox cboSyslogListeningIP = new JComboBox();
    private String syslogListeningIP;
    
    private JComboBox cboNtpListeningIP = new JComboBox();
    private String ntpListeningIP;
    
    private JComboBox cboSnmpTrapListenerIP = new JComboBox();
    private String snmpTrapListenerIP;
    
    private String[] trapVersion = {"Version 2c", "Version 3"};
    private JComboBox cboSnmpTrapVersion = new JComboBox(trapVersion);
    
    private JTextArea txtNTPResults = new JTextArea();
    private JTextArea txtSyslogResults = new JTextArea();
    private JTextArea txtSNMPResults = new JTextArea();
    private JTextArea txtSnmpTrapResults = new JTextArea();
    private JTextArea txtAbout = new JTextArea();
    
    private JTabbedPane tabPane;
    private JPanel pnlNtp;
    private JPanel pnlSyslog;
    private JPanel pnlSnmp;
    private JPanel pnlSnmpTrap;
    private JPanel pnlAbout;
    
    private String imgLogo = "R0lGODlhLAF1APQAAAAAABERESIiIjMzM0RERFVVVWZmZnd3dwCOnxGWpiKerDOls0StuVW0v2a8xnfDzIiIiJmZmaqqqru7u4jL05nS2ara37vh5szMzN3d3czp7N3w8+7u7u74+QAAAAAAACH5BAEAAB4ALAAAAAAsAXUAAAX+oCeOZGmeaKqubOu+cCzPdG3feK7vfO//wKBwSCwaj8ikcslsOp/QqHRKrVqv2Kx2y+16v+CweEwum8/otHrNbrvfswdi0YHb79QNYn/B+/9LFXsIfYCGh0EOg4iMjToJewqOk5QwGoMMlZqbJxSDD5yhnAyDFaKnlIOEqKyIFqqtsYByewuyt3cLgw24vW16n77CaYKDFsPIZQ2qGsk8GBPR0tEZJxzT1ajX09EYMZCLK9Dc5ORMGeXZWQcA7e7tBtbv6qIZ7+4Hlqq2KhwB9wADMpkQEMIWdgEDoHBXwEcGb2c4BJQAg9YeBysiBNzobsDAgBCzEOAYwQTBdhT+bxggIOBdvjMSAnKAQSqYigEcORJgAgGgwi05G5bo2Q6HPYAT0CB85/FFB1WrUnCIME0jwAHcSi4pADCelpMcZ5IYCWDnDav30uC899LFK1UbYKB1KeXfPa3rGAbEKwKfCA4H1uo0kM1ATrZL3xkU0TJgUg9LBUwtIHijgAIhPQAkwMFA43YFJAJM6QEDW7EjLO6JYRhg5hERCny2bAC1hwwHCNjVuViEaYDZIOg+3K5tEbIE5jIkAdZbht3EA1SbfVhCYndir78bwRVAAAzac34fcbVyuwgxAYrFsFvA4xIKVGWCAb3dzxLdibcbsL7+4bZEvSOACK3pB4BxRLj+A4Fo6o2A0E9kGQiAAb8ZCNlmvnH0E1gQgCWhUAwehttVIkSwW2gnADMIRi9U+I5QJCin32IRSghRfu7E46F+7xHRnAc4uoMXWQ256A5pAbozYIk+naDdS+a9Y9ZaOxWoZDadNelBeocNGOUBHOQXAF8lFDMIBTAkqaAJUXImQgZt3hZQWxP4p9VeHljZjlkmYBABPUM8yCRAMK6pJgBLinBoU0ASekKNxV24Z1ceBDgdQMbJ+FN4B0xzAJhzzvZaCcuoUogLQbbzWohHkqDdTpqWYKcHO7ZzKUACEKDrrgsmsRaIG1XzY6peiZAqjP71RgJ1AEhw1IGQHvhcpM/+vtOjnmWJ8OUJMoKkAjiD1PGCf/eNwOU7tm2LLYxyuiapgO0ayC4RDPaGLQAGCeqBf8QtZqSqJzhGlkf+SdBdU92WwKxBrKJ0wr2UonCJKgnA8C8A877bEQkNN+vBwiQcCiGl3eYEqBBchnTuxr+WJuE7EIl8wsUBejOnOxCtS8LF3qxc1AnMJpSCagjM58Kh+LKJaYwC8UzCsX2NVunLAPQYqH2ygrRmeF1y56hJCUVaq3eRMgaQsjLniaEJ1VaHQk02oRoQoB1fi2HaIyRLq0wuv4xEyyTcG2E10RbLgt4mcA1AUz4ruZ63Xt8jFLMIDvqOdFEaPsJTUKE5Lq7+JiS87NJQj+C0dowqvnYR9ZbQuJJR30OaOJCXoPhjt8OmZd5nx2vtw47iTcJbpr4wtuYa70lC2+0khbiiWm5regSfVn+Af5UHgdbJ/MIjgn8GlCMNacdXlVS0ZSfvl+73BCCBNOFVI7oJIDMPAJkeKAIVDOHhr5170sDWT8A3DWbFo2MUQY/4GjeqIHSnXCOA2P0IRDXjdO8dFAmaZEaAvg2OwH6HMUuq+MQcx4ggWiQUQXxUIYkXRAkAJytZTl4iQY54o1szQZ9OjvCsFIrgdTB80wXnRgIgYkczJhxB0GYnAtX5pBr2y5jaGuSBbvVIRZgw3kZQ8EKO8EeIL4P+kkD2JaHxGEE4upKiB3bFRgKwiwNobKMcCVC5CeRmjgSIBwbmiCA+oiCOeExjryrlxxJwAI8QOaQcjeMJqIDCBXeUo21GAJhAstEAZMLASiypq06JYAJ4PCEnBTlJZ5yhVOAyhSlXqQVw6IIPLdCAuFhJyyJcABPhSMEGHAAOBjSjlsAEgkVKxY8U6a9zwUwmD17JgJrwwgQbIBpUGjBLZVpTBpxDgEUeOQILHBMTFijVHhKgymua0wQdqMADHvBLDxDvnepkwANWqAoHtLMC4EKAAh5ggQtUEwUbuIBAB0rQgp7qnJTQwDcRQE0PoPKW44SKfA4qgg28UqIYzaj+RqGigH8idAxw26hIN9oAaVLMARQtAQXyOdKWYjSlHwXDxFy6UXpCpQILTUAzG1ABj54gnQxgKU1H2tGYmkGoQ2VoHzQQUgRg5KKwrEEHDErVqloVpkYFw1SvytW4DG+d/hQBVIyW1bLeYKZ7KKdZ1zoD4umTrXCNgyq4Gde6skCcCPCqXfeagos+k6+ANYEq1BrYwM60YoVNrAfMxCLFBtYix3BsYPXXQskC9i2EtaxdL9BOzXr2s6ANrWhHS9rSmva0qE2talfL2ta69rWwja1sZ0vb2tr2trjNrW53y9ve+va3wA2ucIdL3OIa97jITa5yl8vc5jr3udCNrnQgp0vd6lr3utjNrna3y93ueve74A2veMdL3vKa97xSCAEAOw==";
    private String credits = 
            "Network Tools software is designed and developed by Muhammad Heidir for networking\n" +
            "enthusiast and as a network test platform for verifying of services in the network. Any \n" +
            "comments or feedback is greatly appreciated.\n\n" +
            "Network Tools is released under GNU General Public License, version 2.\n" +
            "This program is free software; you can redistribute it and/or modify it under the terms of the \n" + 
            "GNU General Public License as published by the Free Software Foundation; version 2 of the\n" +
            "License.\n" +
            "This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; \n" + 
            "without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR\n" + 
            "PURPOSE. See the GNU General Public License for more details.\n" +
            "You should have received a copy of the GNU General Public License along with this program;\n" + 
            "if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, \n" +
            "MA 02110-1301, USA.\n" +
            "\nSources:\nhttp://www.base64-image.de/\n" +
            "http://ben-bai.blogspot.sg/2012/08/java-convert-image-to-base64-string-and.html\n" + 
            "http://stackoverflow.com/questions/299495/java-swing-how-to-add-an-image-to-a-jpanel\n" +
            "http://www.jopenbusiness.com/mediawiki/index.php/Syslog4j\n" +
            "http://syslog4j.org/\n" +
            "http://support.ntp.org/bin/view/Support/JavaSntpClient\n" +
            "http://www.snmp4j.org/\n" +
            "http://verticalhorizons.in/how-to-perform-snmp-setget-using-snmp4j/\n" +
            "http://www.jguru.com/faq/view.jsp?EID=703977\n" +
            "http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/BorderDemoProject/src/components/BorderDemo.java" +
            "http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/RadioButtonDemoProject/src/components/RadioButtonDemo.java\n" +
            "http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/layout/GridBagLayoutDemoProject/src/layout/GridBagLayoutDemo.java\n" +
            "http://shivasoft.in/blog/java/snmp/create-snmp-client-in-java-using-snmp4j/\n" +
            "http://svn.hyperic.org/projects/hq/trunk/src/org/hyperic/snmp/SNMPSession_v3.java\n" +
            "http://read.pudn.com/downloads55/sourcecode/windows/network/189149/NTPServer/NTPServer.java__.htm\n" +
            "";
    
    private final String APPVERSION = "0.3";
    private final String APPNAME = "Network Tools";
    private final String APPRELEASEDATE = "3rd January 2014";
    
    /**
     * createAndShowUI method
     * <br />
     * This method creates and displays the graphical user interface of Network Tools
     *
     * @see NToolGUI
     * @since version 0.1
     */
    public void createAndShowUI() {
        JFrame frameUI = new JFrame("Network Tools");
        JFrame.setDefaultLookAndFeelDecorated(false);
        frameUI.setResizable(false);
        frameUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
        frameUI.setSize(600, 600);
        frameUI.setLocationRelativeTo(null);
		
        frameUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
	System.exit(0);
                }
            });
		
        Container contentPane = frameUI.getContentPane();
        addComponentsToPane(contentPane);
        
        addActionListeners();

        frameUI.setVisible(true);
    }
    
    /**
     * addComponentsToPane
     * <br />
     * This method adds graphical objects into Container
     *
     * @param contentPane Container where all objects will reside in
     * @see NToolGUI
     * @since version 0.2
     */
    private void addComponentsToPane(Container contentPane) {
        //contentPane.setLayout(null);
        contentPane.setSize(600, 600);
        
        createPane1();
        createPane2();
        createPane3();
        createPane4();
        createPane5();
        
        tabPane = new JTabbedPane();
        tabPane.addTab( "NTP", pnlNtp );
        tabPane.addTab( "Syslog", pnlSyslog );
        tabPane.addTab( "SNMP", pnlSnmp );
        tabPane.addTab("SNMP Trap", pnlSnmpTrap);
        tabPane.addTab("About", pnlAbout);
        
        contentPane.add( tabPane, BorderLayout.CENTER);
    }
    
    /**
     * addActionListeners
     * <br />
     * This method performs the action when specific objects are being triggered
     *
     * @see NToolGUI
     * @since version 0.2
     */
    private void addActionListeners() {
        btnNTPRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(btnNTPRequest))
                    processNTPrequest();
            }
        });
        
        btnSyslogSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(btnSyslogSend)) {
                    //sendSyslogMessage();
                    String syslogHost, syslogMessage;
                    int syslogPort = 514;
                    
                    if (Utils.validateFieldIsNotEmpty(txtSyslogHost))
                        syslogHost = txtSyslogHost.getText().toString();
                    else
                        syslogHost = "127.0.0.1";
        
                    if (Utils.validateFieldIsNotEmpty(txtSyslogMessage))
                        syslogMessage = txtSyslogMessage.getText().toString();
                    else
                        syslogMessage = "The default syslog message is used since it has not been defined";
       
                    if (Utils.validateFieldasInt(txtSyslogPort))
                        syslogPort = Utils.getIntFromField(txtSyslogPort);
                    
                    Utils.addMessage(txtSyslogResults, SyslogUtils.sendSyslogMessage(syslogHost, syslogPort, syslogSeverity, syslogMessage));
                }
            }
        });
        
        btnNtpListen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(btnNtpListen) && btnNtpListen.getText().equals("Start NTP server")) {
                    ntp = new NtpServer();
                    
                    if (Utils.validateFieldasInt(txtNtpListeningPort))
                        ntpPort = Utils.getIntFromField(txtNtpListeningPort);
                    
                    //txtNTPResults.setText(null);
                    try {
                        ntp.start(ntpPort);
                        
                        Utils.addMessage(txtNTPResults, "Listening for NTP requests on " + ntpListeningIP + ":" + ntpPort);
                        btnNtpListen.setText("Stop NTP server");
                        cboNtpListeningIP.setEnabled(false);
                        txtNtpListeningPort.setEnabled(false);
                        
                    } catch (SocketException ex) {
                        Utils.addMessage(txtNTPResults, "Failed to listen for NTP requests on " + ntpListeningIP + ":" + ntpPort);
                        ntp.stop();
                        
                    } catch (IOException ex) {
                        //Utils.addMessage(txtNTPResults, "Failed to lister for NTP requests on " + ntpListeningIP + ":" + ntpPort);
                        System.out.println(ex.getMessage());
                    }
                    
                    
                } else if (event.getSource().equals(btnNtpListen) && btnNtpListen.getText().equals("Stop NTP server")) {
                    ntp.stop();
                    
                    btnNtpListen.setText("Start NTP server");
                    Utils.addMessage(txtNTPResults, "NTP server stopped.");
                    cboNtpListeningIP.setEnabled(true);
                    txtNtpListeningPort.setEnabled(true);
                }
            }
        });
        
        btnSyslogListen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(btnSyslogListen) && "Start Syslog Listener".equals(btnSyslogListen.getText())) {
                    int syslogPort = 514;
        
                    if (Utils.validateFieldasInt(txtSyslogListeningPort))
                        syslogPort = Utils.getIntFromField(txtSyslogListeningPort);
                    
                    syslogUtils = new SyslogUtils();
                    
                    syslogUtils.startSyslogListener(syslogListeningIP, syslogPort, txtSyslogResults);
                    
                    if (Utils.isPortUp(syslogPort)) {
                        btnSyslogListen.setText("Stop Syslog Listener");
                        txtSyslogResults.setText(null);
                        Utils.addMessage(txtSyslogResults, "Listening for incoming syslog messages on " + syslogListeningIP + ":" + syslogPort);
                        cboSyslogListeningIP.setEnabled(false);
                        txtSyslogListeningPort.setEnabled(false);
                    } else {
                        Utils.addMessage(txtSyslogResults, "Unable to start syslog listener on " + syslogListeningIP + ":" + syslogPort);
                        cboSyslogListeningIP.setEnabled(true);
                        txtSyslogListeningPort.setEnabled(true);
                    }
                    
                } else {
                    syslogUtils.stopSyslogListener();
                    
                    btnSyslogListen.setText("Start Syslog Listener");
                    Utils.addMessage(txtSyslogResults, "Syslog listener stopped.");
                    cboSyslogListeningIP.setEnabled(true);
                    txtSyslogListeningPort.setEnabled(true);
                }
            }
        });
        
        btnSNMPGet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(btnSNMPGet)) {
                    switch (selectedSnmpVersion) {
                        case 0: processSNMPv12c(selectedSnmpVersion);
                            break;
                        case 1: processSNMPv12c(selectedSnmpVersion);
                            break;
                        case 2: processSNMPv3();
                            break;
        }
                }
                        
            }
        });
        
        btnSNMPClearResults.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(btnSNMPClearResults))
                        clearSNMPResults();
            }
        });
        
        btnSnmpTrapListenerStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int port = 162;
                
                TrapReceiver trapRec = new TrapReceiver();
                
                if (event.getSource().equals(btnSnmpTrapListenerStart) && btnSnmpTrapListenerStart.getText().equals("Start Trap Listener")) {                    
                    
                    if ((!Utils.validateFieldIsNotEmpty(txtSnmpTrapListenerPort)) || (!Utils.validateFieldasInt(txtSnmpTrapListenerPort))) {
                        txtSnmpTrapResults.append("Default port (162) is used\n");
                        
                    } else if (!Utils.validatePort(txtSnmpTrapListenerPort)) {
                        txtSnmpTrapResults.append("Invalid port number. Please use between 162 to 65535\n");
                        return;
                    } else
                        port = Utils.getIntFromField(txtSnmpTrapListenerPort);
                
                    if (Utils.validateFieldIsNotEmpty(txtSnmpTrapListenerSecurityName))
                        trapRec.startReceiver(snmpTrapListenerIP, port, txtSnmpTrapListenerSecurityName.getText().toString(), txtSnmpTrapResults);
                    else
                        trapRec.startReceiver(snmpTrapListenerIP, port, txtSnmpTrapResults);
                    
                    btnSnmpTrapListenerStart.setText("Stop Trap Listener");
                    txtSnmpTrapResults.append("Trap receiver starting...\n");
                    cboSnmpTrapListenerIP.setEnabled(false);
                    txtSnmpTrapListenerPort.setEnabled(false);
                    txtSnmpTrapListenerSecurityName.setEnabled(false);
                    
                } else {
                    trapRec.stopReceiver();
                    btnSnmpTrapListenerStart.setText("Start Trap Listener");
                    //txtSnmpTrapResults.append("Trap receiver stopping...\n");
                    cboSnmpTrapListenerIP.setEnabled(true);
                    txtSnmpTrapListenerPort.setEnabled(true);
                    txtSnmpTrapListenerSecurityName.setEnabled(true);
                }
                
                if (!Utils.isPortUp(port)) {                    
                    btnSnmpTrapListenerStart.setText("Start Trap Listener");
                    //txtSnmpTrapResults.append("Trap receiver stopping...\n");
                    cboSnmpTrapListenerIP.setEnabled(true);
                    txtSnmpTrapListenerPort.setEnabled(true);
                    txtSnmpTrapListenerSecurityName.setEnabled(true);
                }
            }
        });
        
        btnSnmpTrapSenderStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(btnSnmpTrapSenderStart)) {
                    String host = "";
                    String securityName = "";
                    int port = 162;
                    String snmpUptime = "1.3.6.1.2.1.1.3.0={t}0";
                    String snmpTrapOID = "1.3.6.1.6.3.1.1.4.1.0={o}1.3.6.1.6.3.1.1.5.1";
                    String snmpSysDescription = "1.3.6.1.2.1.1.1.0={s}Network Tools, Version " + APPVERSION;
                    ArrayUtils list = new ArrayUtils();
                    
                    list.addString("-p");
                    list.addString("TRAP");
                    
                    System.setOut(new PrintStreamCapturer(txtSnmpTrapResults, System.out));
                    
                    
                    if (Utils.validateFieldIsNotEmpty(txtSnmpTrapSecurityName)) {
                        list.addString("-u");
                        list.addString(txtSnmpTrapSecurityName.getText().toString());
                    }
                    
                    list.addString("-v");
                    switch (cboSnmpTrapVersion.getSelectedIndex()) {
                        case 0: list.addString("2c");
                            break;
                        case 1: list.addString("3");
                            break;
                    }
                    
                    if (Utils.validateFieldIsNotEmpty(txtSnmpTrapHostIP))
                        host = txtSnmpTrapHostIP.getText().toString();
                    else {
                        System.out.println("Host IP address is empty.");
                        return;
                    }
                    
                    if ((Utils.validateFieldIsNotEmpty(txtSnmpTrapHostPort)) || (Utils.validateFieldasInt(txtSnmpTrapHostPort))) {
                        if (Utils.validatePort(txtSnmpTrapHostPort)) {
                            port = Utils.getIntFromField(txtSnmpTrapHostPort);
                        } else
                            System.out.println("Default port (162) is used:"+port);
                        
                        list.addString(host+"/"+port);
                    }
                    
                    switch (cboSnmpTrapVersion.getSelectedIndex()) {
                        case 0: list.addString(snmpTrapOID);
                            break;
                        default: list.addString(snmpUptime);
                            list.addString(snmpTrapOID);
                            list.addString(snmpSysDescription);
                            break;
                    }
                    
                    String msg[] = list.getStringArray();
                    //String msg[] = {"-p", "TRAP", "-v", version, "-u", securityName, host+"/"+port, one, two, three};
                    SnmpRequest.request(msg);
                } 
            }
        });
        
        cboNtpListeningIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(cboNtpListeningIP))
                    ntpListeningIP = cboNtpListeningIP.getSelectedItem().toString();
            }
        });
        
        cboSyslogSeverity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(cboSyslogSeverity))
                    syslogSeverity = cboSyslogSeverity.getSelectedIndex();
            }
        });
        
        cboSyslogListeningIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(cboSyslogListeningIP))
                    syslogListeningIP = cboSyslogListeningIP.getSelectedItem().toString();
            }
        });
        
        cboSnmpOIDs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(cboSnmpOIDs))
                    switch (cboSnmpOIDs.getSelectedIndex()) {
                        case 0: selectedOID = SYSDESCR;
                            txtCustomOID.setEnabled(false);
                            break;
                        case 1: selectedOID = SYSUPTIME;
                            txtCustomOID.setEnabled(false);
                            break;
                        case 2: selectedOID = SYSCONTACT;
                            txtCustomOID.setEnabled(false);
                            break;
                        case 3: selectedOID = SYSNAME;
                            txtCustomOID.setEnabled(false);
                            break;
                        case 4: selectedOID = SYSLOCATION;
                            txtCustomOID.setEnabled(false);
                            break;
                        case 5: selectedOID = CUSTOMOID;
                            txtCustomOID.setEnabled(true);
                            break;
                    }
            }
        });
        
        cboSNMPVersion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(cboSNMPVersion))
                    switch (cboSNMPVersion.getSelectedIndex()) {
                        case 0:
                            txtCommunity.setEnabled(true);
                            lblCommunity.setText("Community: ");
                            cbov3Authentication.setEnabled(false);
                            txtAuthPassphrase.setEnabled(false);
                            cbov3Encryption.setEnabled(false);
                            txtPrivPassphrase.setEnabled(false);
                            selectedSnmpVersion = 0;
                            break;
                        case 1: 
                            txtCommunity.setEnabled(true);
                            lblCommunity.setText("Community: ");
                            cbov3Authentication.setEnabled(false);
                            txtAuthPassphrase.setEnabled(false);
                            cbov3Encryption.setEnabled(false);
                            txtPrivPassphrase.setEnabled(false);
                            selectedSnmpVersion = 1;
                            break;
                        case 2:
                            txtCommunity.setEnabled(true);
                            lblCommunity.setText("Username: ");
                            cbov3Authentication.setEnabled(true);
                            txtAuthPassphrase.setEnabled(true);
                            cbov3Encryption.setEnabled(true);
                            txtPrivPassphrase.setEnabled(true);
                            selectedSnmpVersion = 2;
                            break;
                    }
            }
        });
        
        cbov3Authentication.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(cbov3Authentication)) {
                    switch (cbov3Authentication.getSelectedIndex()) {
                        case 0: selectedAuthProtocol = SnmpUtils.MD5;
                            break;
                        case 1: selectedAuthProtocol = SnmpUtils.SHA;
                            break;
                    }
                }
            }
        });

        cbov3Encryption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(cbov3Encryption)) {
                    switch (cbov3Encryption.getSelectedIndex()) {
                        case 0: selectedPrivProtocol = SnmpUtils.DES;
                            break;
                        case 1: selectedPrivProtocol = SnmpUtils.DESEDE;
                            break;
                        case 2: selectedPrivProtocol = SnmpUtils.AES128;
                            break;
                        case 3: selectedPrivProtocol = SnmpUtils.AES192;
                            break;
                        case 4: selectedPrivProtocol = SnmpUtils.AES256;
                            break;
                    }
                }
            }
        });
        
        cboSnmpTrapListenerIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource().equals(cboSnmpTrapListenerIP)) {
                    snmpTrapListenerIP = cboSnmpTrapListenerIP.getSelectedItem().toString();
                }
            }
        });
    }    
    
    private void processNTPrequest() {
        boolean err = false;
        String ntpServer;
        int ntpPort = 123, ntpTimeout;
        
        if (Utils.validateFieldIsNotEmpty(txtNTPServer)) {
            ntpServer = txtNTPServer.getText().toString();
        } else {
            Utils.addMessage(txtNTPResults, "No NTP server defined");
            Utils.addMessage(txtNTPResults, "------------------------------------------");

            return;
        }
        
        if (Utils.validateFieldIsNotEmpty(txtSetTimeOut)) {
            ntpTimeout = Utils.getIntFromField(txtSetTimeOut);
        } else {
            Utils.addMessage(txtNTPResults, "\nDefault TimeOut will be used: 5000ms");
            Utils.addMessage(txtNTPResults, "------------------------------------------");
            ntpTimeout = 5000;
        }
        
        if (Utils.validateFieldIsNotEmpty(txtNTPPort)) {
            if (Utils.validateFieldasInt(txtNTPPort))
                ntpPort = Utils.getIntFromField(txtNTPPort);
        } else {
            Utils.addMessage(txtNTPResults, "\nInvalid NTP port number. Default port will be used: 123");
            Utils.addMessage(txtNTPResults, "------------------------------------------");
        }
        
        //Utils.addMessage(txtNTPResults, NTPUtils.requestNTP(ntpServer, ntpTimeout));
        Utils.addMessage(txtNTPResults, "Sending request to server...");
        Utils.addMessage(txtNTPResults, SntpClient.sendRequest(ntpServer, ntpPort, ntpTimeout));
        
    }
    
    /**
     * processSNMPv12c
     * <br />
     * This method processes SNMPv1 and SNMPv2c requests when triggered by action listener
     *
     * @param version Integer value of 0(SNMPv1) or 1(SNMPv2c)
     * @see NToolGUI
     * @since version 0.2
     */
    private void processSNMPv12c(int version) {
        String hostIP = "";
        int hostPort = 0;
        String community = "";
        String customOID = "";
        int retry = 2;
        int timeout = 5000;
        int err = 0;
        
        if (Utils.validateFieldIsNotEmpty(txtDeviceIP))
            hostIP = txtDeviceIP.getText().toString();
        else {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_NODEVICEIP);
            err = 1;
        }
        
        if (Utils.validateFieldasInt(txtDevicePort))
            hostPort = Utils.getIntFromField(txtDevicePort);
        else {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_NOPORT);
            err = 1;
        }
        
        if (Utils.validateFieldIsNotEmpty(txtCommunity))
            community = txtCommunity.getText().toString();
        else {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_NOCOMMUNITY);
            err = 1;
        }
        
        if (selectedOID == 5 && !Utils.validateFieldIsNotEmpty(txtCustomOID)) {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_NOCUSTOMOID);
            err = 1;
        }
        else
            customOID = txtCustomOID.getText().toString();
        
        if (Utils.validateFieldIsNotEmpty(txtSnmpRetries))
            try {
                retry = Integer.parseInt(txtSnmpRetries.getText().toString());
            } catch (Exception e) {
                Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_DEFAULTRETRY);
            }
        else {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_DEFAULTRETRY);
        }
        
        if (Utils.validateFieldIsNotEmpty(txtSnmpTimeout))
            try {
                timeout = Integer.parseInt(txtSnmpTimeout.getText().toString());
            } catch (Exception e) {
                Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_DEFAULTTIMEOUT);
            }
        else {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_DEFAULTTIMEOUT);
        }
        
        if (err == 1) {
            return;
        }
        
        txtSNMPResults.append("***************** New Test *****************\nNetwork Tools v" + APPVERSION + "\n");
        txtSNMPResults.append(SnmpUtils.currentDate() + ": Device: " + hostIP + "\n");
        
        String snmpVer = "";
        switch (version) {
            case 0: snmpVer = SnmpUtils.SNMPv1;
                txtSNMPResults.append(SnmpUtils.currentDate() + ": Version: SNMPv1\n");
                switch (selectedOID) {
                    case 0: SnmpUtils.getSnmpVersion1(hostIP, hostPort, community, timeout, retry, SnmpUtils.snmp_sysDescr, txtSNMPResults);
                        break;
                    case 1: SnmpUtils.getSnmpVersion1(hostIP, hostPort, community, timeout, retry, SnmpUtils.snmp_sysUptime, txtSNMPResults);
                        break;
                    case 2: SnmpUtils.getSnmpVersion1(hostIP, hostPort, community, timeout, retry, SnmpUtils.snmp_sysContact, txtSNMPResults);
                        break;
                    case 3: SnmpUtils.getSnmpVersion1(hostIP, hostPort, community, timeout, retry, SnmpUtils.snmp_sysName, txtSNMPResults);
                        break;
                    case 4: SnmpUtils.getSnmpVersion1(hostIP, hostPort, community, timeout, retry, SnmpUtils.snmp_sysLocation, txtSNMPResults);
                        break;
                    case 5: SnmpUtils.getSnmpVersion1(hostIP, hostPort, community, timeout, retry, customOID, txtSNMPResults);
                        break;
                }
                break;
                
            case 1: snmpVer = SnmpUtils.SNMPv2c;
                txtSNMPResults.append(SnmpUtils.currentDate() + ": Version: SNMPv2c\n");
                switch (selectedOID) {
                    case 0: SnmpUtils.getSnmpVersion2c(hostIP, hostPort, community, timeout, retry, SnmpUtils.snmp_sysDescr, txtSNMPResults);
                        break;
                    case 1: SnmpUtils.getSnmpVersion2c(hostIP, hostPort, community, timeout, retry, SnmpUtils.snmp_sysUptime, txtSNMPResults);
                        break;
                    case 2: SnmpUtils.getSnmpVersion2c(hostIP, hostPort, community, timeout, retry, SnmpUtils.snmp_sysContact, txtSNMPResults);
                        break;
                    case 3: SnmpUtils.getSnmpVersion2c(hostIP, hostPort, community, timeout, retry, SnmpUtils.snmp_sysName, txtSNMPResults);
                        break;
                    case 4: SnmpUtils.getSnmpVersion2c(hostIP, hostPort, community, timeout, retry, SnmpUtils.snmp_sysLocation, txtSNMPResults);
                        break;
                    case 5: SnmpUtils.getSnmpVersion2c(hostIP, hostPort, community, timeout, retry, customOID, txtSNMPResults);
                        break;
                }
                break;
        }
        
        txtSNMPResults.append(SnmpUtils.currentDate() + ": Done\n\n");        
    }
    
    /**
     * processSNMPv3
     * <br />
     * This method processes SNMPv3 requests when triggered by action listener
     *
     * @see NToolGUI
     * @since version 0.2
     */
    private void processSNMPv3() {
        int err = 0, hostPort=161, retry=0, timeout=0;
        String hostIP="", customOID="";
        String authPassphrase = "", privPassphrase = "", user = "";
        
        if (Utils.validateFieldIsNotEmpty(txtDeviceIP))
            hostIP = txtDeviceIP.getText().toString();
        else {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_NODEVICEIP);
            err = 1;
        }
        
        if (Utils.validateFieldasInt(txtDevicePort))
            hostPort = Utils.getIntFromField(txtDevicePort);
        else {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_NOPORT);
            err = 1;
        }
        
        if (Utils.validateFieldIsNotEmpty(txtCommunity))
            user = txtCommunity.getText().toString();
        else {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_NOCOMMUNITY);
            err = 1;
        }
        
        if (Utils.validateFieldIsNotEmpty(txtPrivPassphrase))
            privPassphrase = txtPrivPassphrase.getText().toString();
        else {
            //addMessage(txtSNMPResults, ERR_NOPRIVPHRASE);
            //err = 1;
            privPassphrase = null;
        }
        
        if (Utils.validateFieldIsNotEmpty(txtAuthPassphrase))
            authPassphrase = txtAuthPassphrase.getText().toString();
        else {
            authPassphrase = null;
            //addMessage(txtSNMPResults, ERR_NOAUTHPHRASE);
            //err = 1;
        }
        
        if (Utils.validateFieldIsNotEmpty(txtSnmpRetries))
            try {
                retry = Integer.parseInt(txtSnmpRetries.getText().toString());
            } catch (Exception e) {
                Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_DEFAULTRETRY);
            }
        else {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_DEFAULTRETRY);
        }
        
        if (Utils.validateFieldIsNotEmpty(txtSnmpTimeout))
            try {
                timeout = Integer.parseInt(txtSnmpTimeout.getText().toString());
            } catch (Exception e) {
                Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_DEFAULTTIMEOUT);
            }
        else {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_DEFAULTTIMEOUT);
        }
        
        if (selectedOID == 5 && !Utils.validateFieldIsNotEmpty(txtCustomOID)) {
            Utils.addMessage(txtSNMPResults, SnmpUtils.ERR_NOCUSTOMOID);
            err = 1;
        }
        else
            customOID = txtCustomOID.getText().toString();
        
        if (err == 1) {
            return;
        }
        
        txtSNMPResults.append("***************** New Test *****************\nNetwork Tools v" + APPVERSION + "\n");
        txtSNMPResults.append(SnmpUtils.currentDate() + ": Device: " + hostIP + "\n");
        txtSNMPResults.append(SnmpUtils.currentDate() + ": Version: SNMPv3\n");
        
        switch (selectedOID) {
            case 0: SnmpUtils.getSnmpVersion3(hostIP, hostPort, user,timeout, retry, selectedPrivProtocol, privPassphrase, selectedAuthProtocol, authPassphrase, SnmpUtils.snmp_sysDescr, txtSNMPResults);
                break;
            case 1: SnmpUtils.getSnmpVersion3(hostIP, hostPort, user,timeout, retry, selectedPrivProtocol, privPassphrase, selectedAuthProtocol, authPassphrase, SnmpUtils.snmp_sysUptime, txtSNMPResults);
                break;
            case 2: SnmpUtils.getSnmpVersion3(hostIP, hostPort, user,timeout, retry, selectedPrivProtocol, privPassphrase, selectedAuthProtocol, authPassphrase, SnmpUtils.snmp_sysContact, txtSNMPResults);
                break;
            case 3: SnmpUtils.getSnmpVersion3(hostIP, hostPort, user,timeout, retry, selectedPrivProtocol, privPassphrase, selectedAuthProtocol, authPassphrase, SnmpUtils.snmp_sysName, txtSNMPResults);
                break;
            case 4: SnmpUtils.getSnmpVersion3(hostIP, hostPort, user,timeout, retry, selectedPrivProtocol, privPassphrase, selectedAuthProtocol, authPassphrase, SnmpUtils.snmp_sysLocation, txtSNMPResults);
                break;
            case 5: SnmpUtils.getSnmpVersion3(hostIP, hostPort, user,timeout, retry, selectedPrivProtocol, privPassphrase, selectedAuthProtocol, authPassphrase, customOID, txtSNMPResults);
                break;
        }
        
        txtSNMPResults.append(SnmpUtils.currentDate() + ": Done\n\n");
    }
    
    /**
     * clearSNMPResults
     * <br />
     * This method clears contents of JTextArea used for SNMP results
     *
     * @see NToolGUI
     * @since version 0.2
     */
    private void clearSNMPResults() {
        txtSNMPResults.setText(null);
    }
    
    /**
     * addSNMPMessage
     * <br />
     * This method appends string array into JTextArea for SNMP results
     *
     * @param msg String array containing strings of SNMP results
     * @see NToolGUI
     * @since version 0.2
     */
    private void addSNMPMessage(String[] msg) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String formattedDate = sdf.format(date);
        
        txtSNMPResults.append("***************** New Test *****************\nNetwork Tools v" + APPVERSION + "\n");
        for (int i=0; i<msg.length; i++) {
            txtSNMPResults.append(formattedDate + ": " + msg[i] + "\n");
        }        
        txtSNMPResults.append(formattedDate + ": Done\n\n");
    }
    
    /**
     * addIPAddresses
     * <br />
     * This method adds detected local host IP addresses into JComboBox
     *
     * @param contentPane Container where all objects will reside in
     * @throws SocketException when IP addresses could not be detected
     * @see NToolGUI
     * @since version 0.2
     */
    private void addIPAddresses(JComboBox comboBox) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        String inetAddressStrip = inetAddress.toString().substring(1);
                        comboBox.addItem(inetAddressStrip);
                    }
        }
    }
    
    /**
     * createPane1
     * <br />
     * This method generates GUI for NTP tab
     *
     * @see NToolGUI
     * @since version 0.2
     */
    private void createPane1() {
        pnlNtp = new JPanel();
        pnlNtp.setLayout(null);
        
        Rectangle rectNTPServer = new Rectangle(30, 20, 80, 10);
        lblNTPServer.setBounds(rectNTPServer);
        pnlNtp.add(lblNTPServer);
        
        rectNTPServer = new Rectangle(115, 15, 310, 20);
        txtNTPServer.setBounds(rectNTPServer);
        txtNTPServer.setText("1.sg.pool.ntp.org");
        pnlNtp.add(txtNTPServer);
        
        Rectangle rectNTPPort = new Rectangle(450, 15, 80, 20);
        lblNTPPort.setBounds(rectNTPPort);
        pnlNtp.add(lblNTPPort);
        
        rectNTPPort = new Rectangle(480, 15, 50, 20);
        txtNTPPort.setBounds(rectNTPPort);
        txtNTPPort.setText("123");
        pnlNtp.add(txtNTPPort);
        
        Rectangle rectSetTimeOut = new Rectangle(30, 20, 90, 70);
        lblSetTimeOut.setBounds(rectSetTimeOut);
        pnlNtp.add(lblSetTimeOut);
        
        rectSetTimeOut = new Rectangle(115, 45, 50, 20);
        txtSetTimeOut.setBounds(rectSetTimeOut);
        txtSetTimeOut.setText("5000");
        pnlNtp.add(txtSetTimeOut);
        
        rectSetTimeOut = new Rectangle(165, 50, 200, 10);
        lblMilliSeconds.setBounds(rectSetTimeOut);
        pnlNtp.add(lblMilliSeconds);
        
        Rectangle rectNTPRequest = new Rectangle(380, 45, 150, 25);
        btnNTPRequest.setBounds(rectNTPRequest);
        pnlNtp.add(btnNTPRequest);
        
        txtNTPResults = new JTextArea(8, 40);
        JScrollPane scrollPane = new JScrollPane(txtNTPResults);
        txtNTPResults.setLineWrap(true);
        txtNTPResults.setBackground(new Color(250,250,250));
        txtNTPResults.setFont(new Font("Courier",Font.PLAIN,12));
        txtNTPResults.setEditable(false);
        scrollPane.setBounds(10, 80, 558, 380);
        pnlNtp.add(scrollPane);
        
        Rectangle rectNtpListener = new Rectangle(70, 470, 100, 25);
        lblNtpListeningIP.setBounds(rectNtpListener);
        pnlNtp.add(lblNtpListeningIP);
        
        try {
            addIPAddresses(cboNtpListeningIP);
            
        } catch (SocketException ex) {
            Logger.getLogger(NToolGUI.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            rectNtpListener = new Rectangle(165, 470, 330, 25);
            cboNtpListeningIP.setBounds(rectNtpListener);
            cboNtpListeningIP.setEditable(false);
            cboNtpListeningIP.setSelectedIndex(0);
            ntpListeningIP = cboNtpListeningIP.getSelectedItem().toString();
            pnlNtp.add(cboNtpListeningIP);
        }
        
        rectNtpListener = new Rectangle(70, 500, 100, 25);
        lblNtpListeningPort.setBounds(rectNtpListener);
        pnlNtp.add(lblNtpListeningPort);
        
        rectNtpListener = new Rectangle(165, 500, 50, 25);
        txtNtpListeningPort.setBounds(rectNtpListener);
        txtNtpListeningPort.setText("123");
        pnlNtp.add(txtNtpListeningPort);
        
        rectNtpListener = new Rectangle(315, 500, 180, 25);
        btnNtpListen.setBounds(rectNtpListener);
        pnlNtp.add(btnNtpListen);
    }
    
    /**
     * createPane2
     * <br />
     * This method generates GUI for Syslog tab
     *
     * @see NToolGUI
     * @since version 0.2
     */
    private void createPane2() {
        pnlSyslog = new JPanel();
        pnlSyslog.setLayout(null);
        
        Rectangle rectSyslogHost = new Rectangle(30, 15, 80, 20);
        lblSyslogHost.setBounds(rectSyslogHost);
        pnlSyslog.add(lblSyslogHost);
        
        rectSyslogHost = new Rectangle(115, 15, 300, 20);
        txtSyslogHost.setBounds(rectSyslogHost);
        txtSyslogHost.setText("127.0.0.1");
        pnlSyslog.add(txtSyslogHost);
        
        Rectangle rectSyslogPort = new Rectangle(450, 15, 50, 20);
        lblSyslogPort.setBounds(rectSyslogPort);
        pnlSyslog.add(lblSyslogPort);
        
        rectSyslogPort = new Rectangle(480, 15, 50, 20);
        txtSyslogPort.setBounds(rectSyslogPort);
        txtSyslogPort.setText("514");
        pnlSyslog.add(txtSyslogPort);
        
        Rectangle rectSyslogMsg = new Rectangle(30, 20, 90, 70);
        lblSyslogMessage.setBounds(rectSyslogMsg);
        pnlSyslog.add(lblSyslogMessage);
        
        rectSyslogMsg = new Rectangle(115, 45, 415, 20);
        txtSyslogMessage.setBounds(rectSyslogMsg);
        txtSyslogMessage.setText("This is a syslog test message. All message will use Local0 Facility");
        pnlSyslog.add(txtSyslogMessage);
        
        Rectangle rectSyslogSev = new Rectangle(30, 50, 100, 70);
        lblSyslogSeverity.setBounds(rectSyslogSev);
        pnlSyslog.add(lblSyslogSeverity);
        
        rectSyslogSev = new Rectangle(115, 75, 200, 25);
        cboSyslogSeverity.setBounds(rectSyslogSev);
        cboSyslogSeverity.setEditable(false);
        cboSyslogSeverity.setSelectedIndex(6);
        pnlSyslog.add(cboSyslogSeverity);
        
        syslogSeverity = 6;
        
        Rectangle rectSyslogSend = new Rectangle(350, 74, 180, 25);
        btnSyslogSend.setBounds(rectSyslogSend);
        pnlSyslog.add(btnSyslogSend);
        
        txtSyslogResults = new JTextArea(8, 40);
        JScrollPane scrollPane = new JScrollPane(txtSyslogResults);
        txtSyslogResults.setLineWrap(true);
        txtSyslogResults.setBackground(new Color(250,250,250));
        txtSyslogResults.setFont(new Font("Courier",Font.PLAIN,12));
        txtSyslogResults.setEditable(false);
        scrollPane.setBounds(10, 110, 560, 350);
        pnlSyslog.add(scrollPane);
        
        Rectangle rectSyslogListeningIP = new Rectangle(70, 470, 100, 25);
        lblSyslogListeningIP.setBounds(rectSyslogListeningIP);
        pnlSyslog.add(lblSyslogListeningIP);
        
        try {
            addIPAddresses(cboSyslogListeningIP);
            
        } catch (SocketException ex) {
            Logger.getLogger(NToolGUI.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            rectSyslogListeningIP = new Rectangle(165, 470, 330, 25);
            cboSyslogListeningIP.setBounds(rectSyslogListeningIP);
            cboSyslogListeningIP.setEditable(false);
            cboSyslogListeningIP.setSelectedIndex(0);
            syslogListeningIP = cboSyslogListeningIP.getSelectedItem().toString();
            pnlSyslog.add(cboSyslogListeningIP);
        }
        
        Rectangle rectSyslogListener = new Rectangle(70, 500, 100, 25);
        lblSyslogListeningPort.setBounds(rectSyslogListener);
        pnlSyslog.add(lblSyslogListeningPort);
        
        rectSyslogListener = new Rectangle(165, 500, 50, 25);
        txtSyslogListeningPort.setBounds(rectSyslogListener);
        txtSyslogListeningPort.setText("514");
        pnlSyslog.add(txtSyslogListeningPort);
        
        rectSyslogListener = new Rectangle(315, 500, 180, 25);
        btnSyslogListen.setBounds(rectSyslogListener);
        pnlSyslog.add(btnSyslogListen);
        
    }
    
    /**
     * createPane3
     * <br />
     * This method generates GUI for SNMP tab
     *
     * @see NToolGUI
     * @since version 0.2
     */
    private void createPane3() {
        pnlSnmp = new JPanel();
        pnlSnmp.setLayout(null);
        
        JPanel pnlSnmpSettings = new JPanel();
        TitledBorder ttlHost = BorderFactory.createTitledBorder("1. Set SNMP Settings");
        ttlHost.setTitleFont(new Font("sans serif", Font.BOLD, 12));
        pnlSnmpSettings.setBorder(ttlHost);
        pnlSnmpSettings.setLayout(null);
        Rectangle rectHost = new Rectangle(5, 5, 565, 230);
        pnlSnmpSettings.setBounds(rectHost);
        pnlSnmp.add(pnlSnmpSettings);
        
        Rectangle lblSNMP = new Rectangle(10, 30, 100, 25);
        lblDeviceIP.setBounds(lblSNMP);
        pnlSnmpSettings.add(lblDeviceIP);
        
        lblSNMP = new Rectangle(10, 60, 100, 25);
        lblSNMPVersion.setBounds(lblSNMP);
        pnlSnmpSettings.add(lblSNMPVersion);
        
        lblSNMP = new Rectangle(10, 90, 100, 25);
        lblCommunity.setBounds(lblSNMP);
        pnlSnmpSettings.add(lblCommunity);
        
        lblSNMP = new Rectangle(10, 120, 200, 25);
        lblAuthPassphrase.setBounds(lblSNMP);
        pnlSnmpSettings.add(lblAuthPassphrase);
        
        lblSNMP = new Rectangle(10, 150, 200, 25);
        lblPrivPassphrase.setBounds(lblSNMP);
        pnlSnmpSettings.add(lblPrivPassphrase);
        
        lblSNMP = new Rectangle(10, 180, 100, 25);
        lblSnmpRetries.setBounds(lblSNMP);
        pnlSnmpSettings.add(lblSnmpRetries);
        
        lblSNMP = new Rectangle(215, 180, 100, 25);
        lblSnmpTimeout.setBounds(lblSNMP);
        pnlSnmpSettings.add(lblSnmpTimeout);
        
        lblSNMP = new Rectangle(340, 180, 200, 25);
        lblMilliSeconds.setBounds(lblSNMP);
        pnlSnmpSettings.add(lblMilliSeconds);
        
        Rectangle txtSNMP = new Rectangle(210, 30, 200, 25);
        txtDeviceIP.setBounds(txtSNMP);
        txtDeviceIP.setText("127.0.0.1");
        pnlSnmpSettings.add(txtDeviceIP);
        
        txtSNMP = new Rectangle(410, 30, 40, 25);
        txtDevicePort.setBounds(txtSNMP);
        txtDevicePort.setText("161");
        pnlSnmpSettings.add(txtDevicePort);
        
        txtSNMP = new Rectangle(210, 60, 200, 25);
        cboSNMPVersion.setBounds(txtSNMP);
        cboSNMPVersion.setEditable(false);
        cboSNMPVersion.setSelectedIndex(0);
        pnlSnmpSettings.add(cboSNMPVersion);
        
        txtSNMP = new Rectangle(210, 90, 100, 25);
        txtCommunity.setBounds(txtSNMP);
        txtCommunity.setText("public");
        pnlSnmpSettings.add(txtCommunity);
        
        txtSNMP = new Rectangle(210, 120, 120, 25);
        cbov3Authentication.setBounds(txtSNMP);
        cbov3Authentication.setEditable(false);
        cbov3Authentication.setEnabled(false);
        cbov3Authentication.setSelectedIndex(0);
        pnlSnmpSettings.add(cbov3Authentication);
        
        txtSNMP = new Rectangle(330, 120, 150, 25);
        txtAuthPassphrase.setBounds(txtSNMP);
        txtAuthPassphrase.setEnabled(false);
        pnlSnmpSettings.add(txtAuthPassphrase);
        
        txtSNMP = new Rectangle(210, 150, 120, 25);
        cbov3Encryption.setBounds(txtSNMP);
        cbov3Encryption.setEditable(false);
        cbov3Encryption.setEnabled(false);
        cbov3Encryption.setSelectedIndex(0);
        pnlSnmpSettings.add(cbov3Encryption);
        
        txtSNMP = new Rectangle(330, 150, 150, 25);
        txtPrivPassphrase.setBounds(txtSNMP);
        txtPrivPassphrase.setEnabled(false);
        pnlSnmpSettings.add(txtPrivPassphrase);
        
        txtSNMP = new Rectangle(70, 180, 30, 25);
        txtSnmpRetries.setBounds(txtSNMP);
        txtSnmpRetries.setText("2");
        pnlSnmpSettings.add(txtSnmpRetries);
        
        txtSNMP = new Rectangle(280, 180, 50, 25);
        txtSnmpTimeout.setBounds(txtSNMP);
        txtSnmpTimeout.setText("5000");
        pnlSnmpSettings.add(txtSnmpTimeout);
        
        GridBagConstraints gBag = new GridBagConstraints();
        gBag.fill = GridBagConstraints.HORIZONTAL;
        JPanel pnlSnmpRequest = new JPanel();
        TitledBorder ttlSNMPv12c = BorderFactory.createTitledBorder("2. Select Request Type");
        ttlSNMPv12c.setTitleFont(new Font("sans serif", Font.BOLD, 12));
        pnlSnmpRequest.setBorder(ttlSNMPv12c);
        pnlSnmpRequest.setLayout(new GridBagLayout());
        rectHost = new Rectangle(5, 240, 565, 80);
        pnlSnmpRequest.setBounds(rectHost);
        pnlSnmp.add(pnlSnmpRequest);
        
        cboSnmpOIDs.setEditable(false);
        cboSnmpOIDs.setSelectedIndex(SYSUPTIME);
        
        gBag.gridx = 0;
        gBag.gridy = 1;
        pnlSnmpRequest.add(cboSnmpOIDs, gBag);
        
        gBag.gridx = 1;
        gBag.gridy = 1;
        gBag.weightx = 80;
        Rectangle rectCustomOID = new Rectangle(0, 0, 80, 25);
        txtCustomOID.setBounds(rectCustomOID);
        txtCustomOID.setEnabled(false);
        pnlSnmpRequest.add(txtCustomOID, gBag);
        
        Rectangle rectSNMPGet = new Rectangle(420, 330, 150, 25);
        btnSNMPGet.setBounds(rectSNMPGet);
        pnlSnmp.add(btnSNMPGet);
        
        Rectangle rectSNMPResults = new Rectangle(5, 330, 150, 25);
        btnSNMPClearResults.setBounds(rectSNMPResults);
        pnlSnmp.add(btnSNMPClearResults);
        
        txtSNMPResults = new JTextArea(5,10);
        JScrollPane scrollPane = new JScrollPane(txtSNMPResults);
        txtSNMPResults.setLineWrap(true);
        txtSNMPResults.setBackground(new Color(250,250,250));
        txtSNMPResults.setFont(new Font("Courier",Font.PLAIN,12));
        txtSNMPResults.setEditable(false);
        scrollPane.setBounds(10, 360, 555, 160);
        pnlSnmp.add(scrollPane);
    }
    
    /**
     * createPane4
     * <br />
     * This method generates GUI for SNMP Trap tab
     *
     * @see NToolGUI
     * @since version 0.2
     */
    private void createPane4() {
        pnlSnmpTrap = new JPanel();
        pnlSnmpTrap.setLayout(null);
        
        JPanel pnlSnmpTrapListener = new JPanel();
        TitledBorder ttlSnmpTrapListener = BorderFactory.createTitledBorder("Set SNMP Trap Listener Settings");
        ttlSnmpTrapListener.setTitleFont(new Font("sans serif", Font.BOLD, 12));
        pnlSnmpTrapListener.setBorder(ttlSnmpTrapListener);
        pnlSnmpTrapListener.setLayout(null);
        Rectangle rectHost = new Rectangle(5, 5, 565, 100);
        pnlSnmpTrapListener.setBounds(rectHost);
        pnlSnmp.add(pnlSnmpTrapListener);
        
        Rectangle rectSnmpTrapListener = new Rectangle(10, 20, 180, 25);
        lblSnmpTrapListenerIPPort.setBounds(rectSnmpTrapListener);
        pnlSnmpTrapListener.add(lblSnmpTrapListenerIPPort);
        
        try {
            addIPAddresses(cboSnmpTrapListenerIP);
            
        } catch (SocketException ex) {
            Logger.getLogger(NToolGUI.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            rectSnmpTrapListener = new Rectangle(180, 20, 320, 25);
            cboSnmpTrapListenerIP.setBounds(rectSnmpTrapListener);
            cboSnmpTrapListenerIP.setEditable(false);
            cboSnmpTrapListenerIP.setSelectedIndex(0);
            snmpTrapListenerIP = cboSnmpTrapListenerIP.getSelectedItem().toString();
            pnlSnmpTrapListener.add(cboSnmpTrapListenerIP);
        }
        
        rectSnmpTrapListener = new Rectangle(500, 20, 50, 25);
        txtSnmpTrapListenerPort.setBounds(rectSnmpTrapListener);
        txtSnmpTrapListenerPort.setText("162");
        pnlSnmpTrapListener.add(txtSnmpTrapListenerPort);
        
        rectSnmpTrapListener = new Rectangle(10, 55, 180, 25);
        lblSnmpTrapListenerSecurityName.setBounds(rectSnmpTrapListener);
        pnlSnmpTrapListener.add(lblSnmpTrapListenerSecurityName);
        
        rectSnmpTrapListener = new Rectangle(180, 55, 150, 25);
        txtSnmpTrapListenerSecurityName.setBounds(rectSnmpTrapListener);
        pnlSnmpTrapListener.add(txtSnmpTrapListenerSecurityName);
        
        rectSnmpTrapListener = new Rectangle(400, 55, 150, 25);
        btnSnmpTrapListenerStart.setBounds(rectSnmpTrapListener);
        pnlSnmpTrapListener.add(btnSnmpTrapListenerStart);
        
        pnlSnmpTrap.add(pnlSnmpTrapListener);
        
        JPanel pnlSnmpTrapSender = new JPanel();
        TitledBorder ttlSnmpTrapSender = BorderFactory.createTitledBorder("Set SNMP Trap Sender Settings");
        ttlSnmpTrapSender.setTitleFont(new Font("sans serif", Font.BOLD, 12));
        pnlSnmpTrapSender.setBorder(ttlSnmpTrapSender);
        pnlSnmpTrapSender.setLayout(null);
        Rectangle rectSnmpTrapSender = new Rectangle(5, 110, 565, 100);
        pnlSnmpTrapSender.setBounds(rectSnmpTrapSender);
        pnlSnmpTrap.add(pnlSnmpTrapSender);
        
        rectSnmpTrapSender = new Rectangle(10, 20, 100, 25);
        lblSnmpTrapHostIPPort.setBounds(rectSnmpTrapSender);
        pnlSnmpTrapSender.add(lblSnmpTrapHostIPPort);
        
        rectSnmpTrapSender = new Rectangle(110, 20, 170, 25);
        txtSnmpTrapHostIP.setBounds(rectSnmpTrapSender);
        pnlSnmpTrapSender.add(txtSnmpTrapHostIP);
        
        rectSnmpTrapSender = new Rectangle(280, 20, 50, 25);
        txtSnmpTrapHostPort.setBounds(rectSnmpTrapSender);
        txtSnmpTrapHostPort.setText("162");
        pnlSnmpTrapSender.add(txtSnmpTrapHostPort);
        
        rectSnmpTrapSender = new Rectangle(340, 20, 100, 25);
        lblSnmpTrapVersion.setBounds(rectSnmpTrapSender);
        pnlSnmpTrapSender.add(lblSnmpTrapVersion);
        
        rectSnmpTrapSender = new Rectangle(430, 20, 120, 25);
        cboSnmpTrapVersion.setBounds(rectSnmpTrapSender);
        pnlSnmpTrapSender.add(cboSnmpTrapVersion);
        
        rectSnmpTrapSender = new Rectangle(10, 55, 100, 25);
        lblSnmpTrapSecurityName.setBounds(rectSnmpTrapSender);
        pnlSnmpTrapSender.add(lblSnmpTrapSecurityName);
        
        rectSnmpTrapSender = new Rectangle(110, 55, 170, 25);
        txtSnmpTrapSecurityName.setBounds(rectSnmpTrapSender);
        pnlSnmpTrapSender.add(txtSnmpTrapSecurityName);
        
        rectSnmpTrapSender = new Rectangle(400, 60, 150, 25);
        btnSnmpTrapSenderStart.setBounds(rectSnmpTrapSender);
        pnlSnmpTrapSender.add(btnSnmpTrapSenderStart);
        
        txtSnmpTrapResults = new JTextArea(5,10);
        JScrollPane scrollPane = new JScrollPane(txtSnmpTrapResults);
        txtSnmpTrapResults.setLineWrap(true);
        txtSnmpTrapResults.setBackground(new Color(250,250,250));
        txtSnmpTrapResults.setFont(new Font("Courier",Font.PLAIN,12));
        txtSnmpTrapResults.setEditable(false);
        scrollPane.setBounds(10, 220, 555, 300);
        pnlSnmpTrap.add(scrollPane);
    }
    
    /**
     * createPane5
     * <br />
     * This method generates GUI for About tab
     *
     * @see NToolGUI
     * @since version 0.2
     */
    private void createPane5() {
        pnlAbout = new JPanel();
        pnlAbout.setLayout(null);
        
        BufferedImage bfImage = Utils.decodeToImage(imgLogo);
        
        Rectangle rectLogo = new Rectangle(130, 20, 300, 80);
        lblLogo = new JLabel(new ImageIcon(Utils.decodeToImage(imgLogo)));
        lblLogo.setBounds(rectLogo);
        pnlAbout.add(lblLogo);
        
        txtAbout = new JTextArea(8,40);
        JScrollPane scrollPane = new JScrollPane(txtAbout);
        txtAbout.setLineWrap(true);
        txtAbout.setBackground(new Color(250,250,250));
        txtAbout.setFont(new Font("Sans Serif",Font.PLAIN,12));
        txtAbout.setEditable(false);
        scrollPane.setBounds(10, 100, 558, 425);
        pnlAbout.add(scrollPane);
        
        txtAbout.append(APPNAME + ": " +APPVERSION + "\n");
        txtAbout.append("Date: " + APPRELEASEDATE + "\n\n");
        txtAbout.append(credits);
        
    }
}