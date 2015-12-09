package com.ghgande.j2mod.modbus.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import gnu.io.CommPort;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusASCIITransport;
import com.ghgande.j2mod.modbus.io.ModbusBINTransport;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransport;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusUDPTransport;
import com.ghgande.j2mod.modbus.util.SerialParameters;

/**
 * Create a <tt>ModbusListener</tt> from an URI-like specifier.
 *
 * @author Julie
 *
 */
public class ModbusMasterFactory {

    public static ModbusTransport createModbusMaster(String address) throws IOException, Exception {
        String parts[] = address.split(":");
        if (parts == null || parts.length < 2) {
            throw new IllegalArgumentException("missing connection information");
        }

        String protocol = parts[0].toLowerCase();
        switch (protocol) {
            case "device":
            case "rtu":
            case "ascii":
            case "bin": {
                /*
                 * Create a ModbusSerialListener with the default Modbus values of
                 * 19200 baud, no parity, using the specified device. If there is an
                 * additional part after the device name, it will be used as the
                 * Modbus unit number.
                 */
                int baudrate = Modbus.DEFAULT_BAUD_RATE;
                if (parts.length > 3) {
                    baudrate = Integer.parseInt(parts[3]);
                } else {
                    if (System.getProperty("com.ghgande.j2mod.modbus.baud") != null) {
                        baudrate = Integer.parseInt(System.getProperty("com.ghgande.j2mod.modbus.baud"));
                    }
                }
                int unitId;
                if (parts.length > 2) {
                    unitId = Integer.parseInt(parts[2]);
                } else {
                    unitId = Modbus.DEFAULT_UNIT_ID;
                }

                SerialParameters parms = new SerialParameters();
                parms.setPortName(parts[1]);
                parms.setBaudRate(baudrate);
                parms.setDatabits(8);
                parms.setEcho(false);
                parms.setParity(SerialPort.PARITY_NONE);
                parms.setFlowControlIn(SerialPort.FLOWCONTROL_NONE);

                ModbusSerialTransport transport;
                switch (protocol) {
                    case "ascii":
                        transport = new ModbusASCIITransport(unitId);
                        break;
                    case "bin":
                        transport = new ModbusBINTransport(unitId);
                        break;
                    default:
                        transport = new ModbusRTUTransport();
                        break;
                }
                CommPort port = new RXTXPort(parms.getPortName());

                transport.setCommPort(port);
                transport.setEcho(false);
                transport.setReceiveTimeout(500);

                return transport;
            }
            case "tcp": {
                /*
                 * Create a ModbusTCPListener with the default interface value. The
                 * second optional value is the TCP port number and the third
                 * optional value is the Modbus unit number.
                 */
                String hostName = parts[1];
                int port = Modbus.DEFAULT_PORT;

                if (parts.length > 2) {
                    port = Integer.parseInt(parts[2]);
                }

                Socket socket = new Socket(hostName, port);
                if (Modbus.debug) {
                    System.err.println("connecting to " + socket);
                }

                ModbusTCPTransport transport = new ModbusTCPTransport(socket);

                return transport;
            }
            case "udp": {
                /*
                 * Create a ModbusUDPListener with the default interface value. The
                 * second optional value is the TCP port number and the third
                 * optional value is the Modbus unit number.
                 */
                String hostName = parts[1];
                int port = Modbus.DEFAULT_PORT;

                if (parts.length > 2) {
                    port = Integer.parseInt(parts[2]);
                }

                UDPMasterTerminal terminal;
                terminal = new UDPMasterTerminal(
                        InetAddress.getByName(hostName));
                terminal.setRemotePort(port);
                terminal.activate();

                ModbusUDPTransport transport = terminal.getModbusTransport();

                return transport;
            }
            default:
                throw new IllegalArgumentException("unknown type " + parts[0]);
        }
    }
}
