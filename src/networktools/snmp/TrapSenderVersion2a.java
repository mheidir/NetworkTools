/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package networktools.snmp;

import java.util.Date;
import static networktools.snmp.TrapSenderVersion2.Oid;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 *
 * @author Muhammad Heidir
 * @description Extends the original class by Jitendra
 */
public class TrapSenderVersion2a extends TrapSenderVersion2 {
    public void sendTrap_Version2(String community, String oid, String ipAddress, int port) {
        try {
            // Create Transport Mapping
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target
            CommunityTarget cTarget = new CommunityTarget();
            cTarget.setCommunity(new OctetString(community));
            cTarget.setVersion(SnmpConstants.version2c);
            cTarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            cTarget.setRetries(2);
            cTarget.setTimeout(5000);

            // Create PDU for V2
            PDU pdu = new PDU();

            // need to specify the system up time
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new OctetString(new Date().toString())));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(Oid)));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(ipAddress)));

            pdu.add(new VariableBinding(new OID(Oid), new OctetString("Major")));
            pdu.setType(PDU.NOTIFICATION);

            // Send the PDU
            Snmp snmp = new Snmp(transport);
            System.out.println("Sending V2 Trap... Check Wheather NMS is Listening or not? ");
            snmp.send(pdu, cTarget);
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
