/**
 * Java Modbus Library (j2mod)
 * Copyright 2012, Julianne Frances Haugh
 * d/b/a greenHouse Gas and Electric
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.ghgande.j2mod.modbus.cmd;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.ModbusSlaveException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ExceptionResponse;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.msg.ReadFileRecordRequest;
import com.ghgande.j2mod.modbus.msg.ReadFileRecordRequest.RecordRequest;
import com.ghgande.j2mod.modbus.msg.ReadFileRecordResponse;
import com.ghgande.j2mod.modbus.msg.ReadFileRecordResponse.RecordResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

/**
 * RecordText -- Exercise the "READ FILE RECORD" Modbus
 * message.
 * 
 * @author Julie
 * @version 0.96
 */
public class ReadRecordTest {

	/**
	 * usage -- Print command line arguments and exit.
	 */
	private static void usage() {
		System.out.println(
				"Usage: ReadRecordTest address[:port[:unit]] file record registers [count]");
		
		System.exit(1);
	}

	public static void main(String[] args) {
		InetAddress	ipAddress = null;
		int			port = Modbus.DEFAULT_PORT;
		int			unit = 0;
		TCPMasterConnection connection = null;
		ReadFileRecordRequest request = null;
		ReadFileRecordResponse response = null;
		ModbusTransaction	trans = null;
		int			file = 0;
		int			record = 0;
		int			registers = 0;
		int			requestCount = 1;

		/*
		 * Get the command line parameters.
		 */
		if (args.length < 4 || args.length > 5)
			usage();
		
		String serverAddress = args[0];
		String parts[] = serverAddress.split(":");
		String hostName = parts[0];
		
		try {
			/*
			 * Address is of the form
			 * 
			 * hostName:port:unitNumber
			 * 
			 * where
			 * 
			 * hostName -- Standard text host name
			 * port		-- Modbus port, 502 is the default
			 * unit		-- Modbus unit number, 0 is the default
			 */
			if (parts.length > 1) {
				port = Integer.parseInt(parts[1]);
				
				if (parts.length > 2)
					unit = Integer.parseInt(parts[2]);
			}
			ipAddress = InetAddress.getByName(hostName);
			
			file = Integer.parseInt(args[1]);
			record = Integer.parseInt(args[2]);
			registers = Integer.parseInt(args[3]);
			
			if (args.length > 4)
				requestCount = Integer.parseInt(args[4]);
		} catch (NumberFormatException x) {
			System.err.println("Invalid parameter");
			usage();
		} catch (UnknownHostException x) {
			System.err.println("Unknown host: " + hostName);
			System.exit(1);
		} catch (Exception ex) {
			ex.printStackTrace();
			usage();
			System.exit(1);
		}

		try {
			
			/*
			 * Setup the TCP connection to the Modbus/TCP Master
			 */
			connection = new TCPMasterConnection(ipAddress);
			connection.setPort(port);
			connection.connect();
			connection.setTimeout(500);

			if (Modbus.debug)
				System.out.println("Connected to " + ipAddress.toString() + ":"
						+ connection.getPort());

			for (int i = 0; i < requestCount; i++) {
				/*
				 * Setup the READ FILE RECORD request.  The record number
				 * will be incremented for each loop.
				 */
				request = new ReadFileRecordRequest();
				request.setUnitID(unit);
				
				RecordRequest recordRequest =
						request.new RecordRequest(file, record + i, registers);
				request.addRequest(recordRequest);
				
				if (Modbus.debug)
					System.out.println("Request: " + request.getHexMessage());

				/*
				 * Setup the transaction.
				 */
				trans = new ModbusTCPTransaction(connection);
				trans.setRequest(request);

				/*
				 * Execute the transaction.
				 */
				try {
					trans.execute();
				} catch (ModbusSlaveException x) {
					System.err.println("Slave Exception: " +
							x.getLocalizedMessage());
					continue;
				} catch (ModbusIOException x) {
					System.err.println("I/O Exception: " +
							x.getLocalizedMessage());
					continue;					
				} catch (ModbusException x) {
					System.err.println("Modbus Exception: " +
							x.getLocalizedMessage());
					continue;					
				}

				ModbusResponse dummy = trans.getResponse();
				if (dummy == null) {
					System.err.println("No response for transaction " + i);
					continue;
				}
				if (dummy instanceof ExceptionResponse) {
					ExceptionResponse exception = (ExceptionResponse) dummy;

					System.err.println(exception);

					continue;
				} else if (dummy instanceof ReadFileRecordResponse) {
					response = (ReadFileRecordResponse) dummy;

					if (Modbus.debug)
						System.out.println("Response: "
								+ response.getHexMessage());

					int count = response.getRecordCount();
					for (int j = 0;j < count;j++) {
						RecordResponse data = response.getRecord(j);
						short values[] = new short[data.getWordCount()];
						for (int k = 0;k < data.getWordCount();k++)
							values[k] = data.getRegister(k).toShort();
						
						System.out.println("data[" + j + "] = " +
								Arrays.toString(values));
					}
					continue;
				}

				/*
				 * Unknown message.
				 */
				System.out.println(
						"Unknown Response: " + dummy.getHexMessage());
			}
			
			/*
			 * Teardown the connection.
			 */
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
