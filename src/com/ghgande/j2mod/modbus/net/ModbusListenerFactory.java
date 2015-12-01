package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.Modbus;
import gnu.io.SerialPort;

import com.ghgande.j2mod.modbus.util.SerialParameters;

/**
 * Create a <tt>ModbusListener</tt> from an URI-like specifier.
 *
 * @author Julie
 *
 */
public class ModbusListenerFactory {

    public static ModbusListener createModbusListener(String address) {
        String parts[] = address.split(":");
        if (parts == null || parts.length < 2) {
            throw new IllegalArgumentException("missing connection information");
        }

        String protocol = parts[0].toLowerCase();
        switch (protocol) {
            case "device":
            case "rtu":
            case "ascii": {
                /*
                 * Create a ModbusSerialListener with the default Modbus
                 * values of 19200 baud, no parity, using the specified
                 * device.  If there is an additional part after the
                 * device name, it will be used as the Modbus unit number.
                 */
                int baudrate = Modbus.DEFAULT_BAUD_RATE;
                if (parts.length > 3) {
                    baudrate = Integer.parseInt(parts[3]);
                }
                SerialParameters parms = new SerialParameters();
                parms.setPortName(parts[1]);
                parms.setBaudRate(baudrate);
                parms.setDatabits(8);
                parms.setEcho(false);
                parms.setParity(SerialPort.PARITY_NONE);
                parms.setFlowControlIn(SerialPort.FLOWCONTROL_NONE);

                ModbusSerialListener listener = new ModbusSerialListener(parms);
                if ((parts.length > 2) && (parts[2].length() > 0)) {
                    int unit = Integer.parseInt(parts[2]);
                    if (unit < 0 || unit > 248) {
                        throw new IllegalArgumentException("illegal unit number");
                    }

                    listener.setUnit(unit);
                }
                listener.setListening(true);

                Thread result = new Thread(listener);
                result.start();

                return listener;
            }
            case "tcp": {
                /*
                 * Create a ModbusTCPListener with the default interface
                 * value.  The second optional value is the TCP port number
                 * and the third optional value is the Modbus unit number.
                 */
                ModbusTCPListener listener = new ModbusTCPListener(5);
                if (parts.length > 2) {
                    int port = Integer.parseInt(parts[2]);
                    listener.setPort(port);

                    if (parts.length > 3) {
                        int unit = Integer.parseInt(parts[3]);
                        listener.setUnit(unit);
                    }
                }
                listener.setListening(true);

                Thread result = new Thread(listener);
                result.start();

                return listener;
            }
            case "udp": {
                /*
                 * Create a ModbusUDPListener with the default interface
                 * value.  The second optional value is the TCP port number
                 * and the third optional value is the Modbus unit number.
                 */
                ModbusUDPListener listener = new ModbusUDPListener();
                if (parts.length > 2) {
                    int port = Integer.parseInt(parts[2]);
                    listener.setPort(port);

                    if (parts.length > 3) {
                        int unit = Integer.parseInt(parts[3]);
                        listener.setUnit(unit);
                    }
                }
                listener.setListening(true);

                Thread result = new Thread(listener);
                result.start();

                return listener;
            }
            default:
                throw new IllegalArgumentException("unknown type " + parts[0]);
        }
    }
}
