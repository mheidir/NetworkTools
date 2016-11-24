/**
* SystemOutSyslogServerEventHandler provides a simple example implementation
* of the SyslogServerEventHandlerIF which writes the events to System.out.
* 
* <p>Syslog4j is licensed under the Lesser GNU Public License v2.1.  A copy
* of the LGPL license is available in the META-INF folder in all
* distributions of Syslog4j and in the base directory of the "doc" ZIP.</p>
* 
* @author &lt;syslog4j@productivity.org&gt;
* @version $Id: PrintStreamSyslogServerEventHandler.java,v 1.7 2010/11/28 22:07:57 cvs Exp $
*/

package networktools.syslog;

import java.io.PrintStream;
import java.net.SocketAddress;
import java.util.Date;
import javax.swing.JTextArea;

import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.util.SyslogUtility;


public class OutPrintStreamSyslogServerEventHandler implements SyslogServerSessionEventHandlerIF {
    private static final long serialVersionUID = 6036415838696050746L;
	
    protected PrintStream stream = null;
    protected JTextArea txtArea = null;
	
    public OutPrintStreamSyslogServerEventHandler(PrintStream stream) {
        this.stream = stream;
    }
        
    public OutPrintStreamSyslogServerEventHandler(JTextArea txtArea) {
        this.txtArea = txtArea;
    }
        
    @Override
    public void initialize(SyslogServerIF syslogServer) {
        //
    }

    @Override
    public Object sessionOpened(SyslogServerIF syslogServer, SocketAddress socketAddress) {
        return null;
    }

    @Override
    public void event(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, SyslogServerEventIF event) {
        String date = (event.getDate() == null ? new Date() : event.getDate()).toString();
        String facility = SyslogUtility.getFacilityString(event.getFacility());
        String level = SyslogUtility.getLevelString(event.getLevel());
		
        //this.stream.println("{" + facility + "} " + date + " " + level + " " + event.getMessage());
        txtArea.append("{" + facility + "} " + date + " " + level + " " + event.getMessage() + "\n");
    }

    @Override
    public void exception(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, Exception exception) {
        //
    }

    @Override
    public void sessionClosed(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, boolean timeout) {
        //
    }

    @Override
    public void destroy(SyslogServerIF syslogServer) {
        //
    }
}
