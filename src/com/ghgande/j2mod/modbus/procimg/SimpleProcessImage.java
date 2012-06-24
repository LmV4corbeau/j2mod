//License
/***
 * Java Modbus Library (jamod)
 * Copyright (c) 2002-2004, jamod development team
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
 ***/
package com.ghgande.j2mod.modbus.procimg;

import java.util.Vector;

/**
 * Class implementing a simple process image to be able to run unit tests or
 * handle simple cases.
 * 
 * <p>
 * The image has a simple linear address space for both analog and digital
 * objects. There are no "holes" between objects in this model.
 * 
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class SimpleProcessImage implements ProcessImageImplementation {

	// instance attributes
	protected Vector<DigitalIn> m_DigitalInputs;
	protected Vector<DigitalOut> m_DigitalOutputs;
	protected Vector<InputRegister> m_InputRegisters;
	protected Vector<Register> m_Registers;
	protected boolean m_Locked = false;
	protected int m_Unit = 0;

	/**
	 * Constructs a new <tt>SimpleProcessImage</tt> instance.
	 */
	public SimpleProcessImage() {
		m_DigitalInputs = new Vector<DigitalIn>();
		m_DigitalOutputs = new Vector<DigitalOut>();
		m_InputRegisters = new Vector<InputRegister>();
		m_Registers = new Vector<Register>();
	}

	/**
	 * Constructs a new <tt>SimpleProcessImage</tt> instance having a
	 * (potentially) non-zero unit ID.
	 */
	public SimpleProcessImage(int unit) {
		m_DigitalInputs = new Vector<DigitalIn>();
		m_DigitalOutputs = new Vector<DigitalOut>();
		m_InputRegisters = new Vector<InputRegister>();
		m_Registers = new Vector<Register>();
		m_Unit = unit;
	}

	/**
	 * The process image is locked to prevent changes.
	 * 
	 * @return whether or not the process image is locked.
	 */
	public boolean isLocked() {
		return m_Locked;
	}

	public void setLocked(boolean locked) {
		m_Locked = locked;
	}

	public int getUnitID() {
		return m_Unit;
	}

	public void addDigitalIn(DigitalIn di) {
		if (!isLocked()) {
			m_DigitalInputs.addElement(di);
		}
	}

	public void removeDigitalIn(DigitalIn di) {
		if (!isLocked()) {
			m_DigitalInputs.removeElement(di);
		}
	}

	public void setDigitalIn(int ref, DigitalIn di)
			throws IllegalAddressException {
		if (!isLocked()) {
			try {
				m_DigitalInputs.setElementAt(di, ref);
			} catch (IndexOutOfBoundsException ex) {
				throw new IllegalAddressException();
			}
		}
	}

	public DigitalIn getDigitalIn(int ref) throws IllegalAddressException {
		try {
			return m_DigitalInputs.elementAt(ref);
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalAddressException();
		}
	}

	public int getDigitalInCount() {
		return m_DigitalInputs.size();
	}

	public DigitalIn[] getDigitalInRange(int ref, int count) {
		// ensure valid reference range
		if (ref < 0 || ref + count > m_DigitalInputs.size()) {
			throw new IllegalAddressException();
		} else {
			DigitalIn[] dins = new DigitalIn[count];
			for (int i = 0; i < dins.length; i++) {
				dins[i] = getDigitalIn(ref + i);
			}
			return dins;
		}
	}

	public void addDigitalOut(DigitalOut _do) {
		if (!isLocked()) {
			m_DigitalOutputs.addElement(_do);
		}
	}

	public void removeDigitalOut(DigitalOut _do) {
		if (!isLocked()) {
			m_DigitalOutputs.removeElement(_do);
		}
	}

	public void setDigitalOut(int ref, DigitalOut _do)
			throws IllegalAddressException {
		if (!isLocked()) {
			try {
				m_DigitalOutputs.setElementAt(_do, ref);
			} catch (IndexOutOfBoundsException ex) {
				throw new IllegalAddressException();
			}
		}
	}

	public DigitalOut getDigitalOut(int ref) throws IllegalAddressException {
		try {
			return (DigitalOut) m_DigitalOutputs.elementAt(ref);
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalAddressException();
		}
	}

	public int getDigitalOutCount() {
		return m_DigitalOutputs.size();
	}

	public DigitalOut[] getDigitalOutRange(int ref, int count) {
		// ensure valid reference range
		if (ref < 0 || ref + count > m_DigitalOutputs.size()) {
			throw new IllegalAddressException();
		} else {
			DigitalOut[] douts = new DigitalOut[count];
			for (int i = 0; i < douts.length; i++) {
				douts[i] = getDigitalOut(ref + i);
			}
			return douts;
		}
	}

	public void addInputRegister(InputRegister reg) {
		if (!isLocked()) {
			m_InputRegisters.addElement(reg);
		}
	}

	public void removeInputRegister(InputRegister reg) {
		if (!isLocked()) {
			m_InputRegisters.removeElement(reg);
		}
	}

	public void setInputRegister(int ref, InputRegister reg)
			throws IllegalAddressException {
		if (!isLocked()) {
			try {
				m_InputRegisters.setElementAt(reg, ref);
			} catch (IndexOutOfBoundsException ex) {
				throw new IllegalAddressException();
			}
		}
	}

	public InputRegister getInputRegister(int ref)
			throws IllegalAddressException {

		try {
			return m_InputRegisters.elementAt(ref);
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalAddressException();
		}
	}

	public int getInputRegisterCount() {
		return m_InputRegisters.size();
	}

	public InputRegister[] getInputRegisterRange(int ref, int count) {
		// ensure valid reference range
		if (ref < 0 || ref + count > m_InputRegisters.size())
			throw new IllegalAddressException();

		InputRegister[] iregs = new InputRegister[count];
		for (int i = 0; i < iregs.length; i++)
			iregs[i] = getInputRegister(ref + i);

		return iregs;
	}

	public void addRegister(Register reg) {
		if (!isLocked()) {
			m_Registers.addElement(reg);
		}
	}

	public void removeRegister(Register reg) {
		if (!isLocked()) {
			m_Registers.removeElement(reg);
		}
	}

	public void setRegister(int ref, Register reg)
			throws IllegalAddressException {
		if (!isLocked()) {
			try {
				m_Registers.setElementAt(reg, ref);
			} catch (IndexOutOfBoundsException ex) {
				throw new IllegalAddressException();
			}
		}
	}

	public Register getRegister(int ref) throws IllegalAddressException {
		try {
			return (Register) m_Registers.elementAt(ref);
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalAddressException();
		}
	}

	public int getRegisterCount() {
		return m_Registers.size();
	}

	public Register[] getRegisterRange(int ref, int count) {
		if (ref < 0 || ref + count > m_Registers.size()) {
			throw new IllegalAddressException();
		} else {
			Register[] iregs = new Register[count];
			for (int i = 0; i < iregs.length; i++) {
				iregs[i] = getRegister(ref + i);
			}
			return iregs;
		}
	}
}
