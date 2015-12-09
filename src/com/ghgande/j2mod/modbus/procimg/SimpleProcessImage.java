//License
/**
 * *
 * Java Modbus Library (jamod) Copyright (c) 2002-2004, jamod development team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. *
 */
/**
 * *
 * Java Modbus Library (j2mod) Copyright 2012, Julianne Frances Haugh d/b/a
 * greenHouse Gas and Electric All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. *
 */
package com.ghgande.j2mod.modbus.procimg;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Class implementing a simple process image to be able to run unit tests or
 * handle simple cases.
 *
 * <p>
 * The image has a simple linear address space for, analog, digital and file
 * objects. Holes may be created by adding a object with a reference after the
 * last object reference of that type.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Julie Added support for files of records.
 */
public class SimpleProcessImage implements ProcessImageImplementation {

    // instance attributes
    protected final NavigableMap<Integer, DigitalIn> m_DigitalInputs;
    protected final NavigableMap<Integer, DigitalOut> m_DigitalOutputs;
    protected final NavigableMap<Integer, InputRegister> m_InputRegisters;
    protected final NavigableMap<Integer, Register> m_Registers;
    protected final NavigableMap<Integer, ModbusFile> m_Files;
    protected final NavigableMap<Integer, FIFO> m_FIFOs;
    protected boolean m_Locked = false;
    protected int m_Unit = 0;

    /**
     * Constructs a new <tt>SimpleProcessImage</tt> instance.
     */
    public SimpleProcessImage() {
        m_DigitalInputs = new TreeMap<>();
        m_DigitalOutputs = new TreeMap<>();
        m_InputRegisters = new TreeMap<>();
        m_Registers = new TreeMap<>();
        m_Files = new TreeMap<>();
        m_FIFOs = new TreeMap<>();
    }

    /**
     * Constructs a new <tt>SimpleProcessImage</tt> instance having a
     * (potentially) non-zero unit ID.
     *
     * @param unit
     */
    public SimpleProcessImage(int unit) {
        m_DigitalInputs = new TreeMap<>();
        m_DigitalOutputs = new TreeMap<>();
        m_InputRegisters = new TreeMap<>();
        m_Registers = new TreeMap<>();
        m_Files = new TreeMap<>();
        m_FIFOs = new TreeMap<>();
        m_Unit = unit;
    }

    /**
     * The process image is locked to prevent changes.
     *
     * @return whether or not the process image is locked.
     */
    public synchronized boolean isLocked() {
        return m_Locked;
    }

    /**
     * setLocked -- lock or unlock the process image. It is an error (false
     * return value) to attempt to lock the process image when it is already
     * locked.
     *
     * <p>
     * Compatability Note: jamod did not enforce this restriction, so it is
     * being handled in a way which is backwards compatible. If you wish to
     * determine if you acquired the lock, check the return value. If your code
     * is still based on the jamod paradigm, you will ignore the return value
     * and your code will function as before.
     *
     * @param locked
     * @return
     */
    public synchronized boolean setLocked(boolean locked) {
        if (m_Locked && locked) {
            return false;
        }

        m_Locked = locked;
        return true;
    }

    @Override
    public int getUnitID() {
        return m_Unit;
    }

    @Override
    public void addDigitalIn(DigitalIn di) {
        if (!isLocked()) {
            int newRef = 0;
            if (!m_DigitalInputs.isEmpty()) {
                newRef = m_DigitalInputs.lastKey() + 1;
            }
            m_DigitalInputs.put(newRef, di);
        }
    }

    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void addDigitalIn(int ref, DigitalIn d1) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            synchronized (m_DigitalInputs) {
                m_DigitalInputs.put(ref, d1);
            }
        }
    }

    @Override
    public void removeDigitalIn(DigitalIn di) {
        if (!isLocked()) {
            m_DigitalInputs.remove(getKeyByValue(m_DigitalInputs, di));
        }
    }

    @Override
    public void setDigitalIn(int ref, DigitalIn di) throws IllegalAddressException {
        if (!isLocked()) {
            if (!m_DigitalInputs.containsKey(ref)) {
                throw new IllegalAddressException();
            }

            m_DigitalInputs.replace(ref, di);
        }
    }

    @Override
    public DigitalIn getDigitalIn(int ref) throws IllegalAddressException {
        DigitalIn result = m_DigitalInputs.get(ref);
        if (result == null) {
            throw new IllegalAddressException();
        }

        return result;
    }

    @Override
    public int getDigitalInCount() {
        return m_DigitalInputs.size();
    }

    @Override
    public DigitalIn[] getDigitalInRange(int ref, int count) {
        DigitalIn[] dins = new DigitalIn[count];
        for (int i = 0; i < dins.length; i++) {
            dins[i] = getDigitalIn(ref + i);
        }
        return dins;
    }

    @Override
    public void addDigitalOut(DigitalOut _do) {
        if (!isLocked()) {
            int newRef = 0;
            if (!m_DigitalOutputs.isEmpty()) {
                newRef = m_DigitalOutputs.lastKey() + 1;
            }
            m_DigitalOutputs.put(newRef, _do);
        }
    }

    @Override
    public void addDigitalOut(int ref, DigitalOut dout) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            m_DigitalOutputs.put(ref, dout);
        }
    }

    @Override
    public void removeDigitalOut(DigitalOut _do) {
        if (!isLocked()) {
            m_DigitalOutputs.remove(getKeyByValue(m_DigitalOutputs, _do));
        }
    }

    @Override
    public void setDigitalOut(int ref, DigitalOut _do) throws IllegalAddressException {
        if (!isLocked()) {
            if (!m_DigitalOutputs.containsKey(ref)) {
                throw new IllegalAddressException();
            }

            m_DigitalOutputs.replace(ref, _do);
        }
    }

    @Override
    public DigitalOut getDigitalOut(int ref) throws IllegalAddressException {
        DigitalOut result = m_DigitalOutputs.get(ref);
        if (result == null) {
            throw new IllegalAddressException();
        }

        return result;
    }

    @Override
    public int getDigitalOutCount() {
        return m_DigitalOutputs.size();
    }

    @Override
    public DigitalOut[] getDigitalOutRange(int ref, int count) {
        DigitalOut[] douts = new DigitalOut[count];
        for (int i = 0; i < douts.length; i++) {
            douts[i] = getDigitalOut(ref + i);
        }
        return douts;
    }

    @Override
    public void addInputRegister(InputRegister reg) {
        if (!isLocked()) {
            int newRef = 0;
            if (!m_InputRegisters.isEmpty()) {
                newRef = m_InputRegisters.lastKey() + 1;
            }
            m_InputRegisters.put(newRef, reg);
        }
    }

    @Override
    public void addInputRegister(int ref, InputRegister inReg) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            m_InputRegisters.put(ref, inReg);
        }
    }

    @Override
    public void removeInputRegister(InputRegister reg) {
        if (!isLocked()) {
            m_InputRegisters.remove(getKeyByValue(m_InputRegisters, reg));
        }
    }

    @Override
    public void setInputRegister(int ref, InputRegister reg) throws IllegalAddressException {
        if (!isLocked()) {
            if (!m_InputRegisters.containsKey(ref)) {
                throw new IllegalAddressException();
            }

            m_InputRegisters.replace(ref, reg);
        }
    }

    @Override
    public InputRegister getInputRegister(int ref) throws IllegalAddressException {
        InputRegister result = m_InputRegisters.get(ref);
        if (result == null) {
            throw new IllegalAddressException();
        }
        return result;
    }

    @Override
    public int getInputRegisterCount() {
        if (m_InputRegisters.isEmpty()) {
            return 0;
        } else {
            return m_InputRegisters.lastKey() + 1;
        }
    }

    @Override
    public InputRegister[] getInputRegisterRange(int ref, int count) {
        InputRegister[] iregs = new InputRegister[count];
        for (int i = 0; i < iregs.length; i++) {
            iregs[i] = getInputRegister(ref + i);
        }

        return iregs;
    }

    @Override
    public void addRegister(Register reg) {
        if (!isLocked()) {
            int newRef = 0;
            if (!m_Registers.isEmpty()) {
                newRef = m_Registers.lastKey() + 1;
            }
            m_Registers.put(newRef, reg);
        }
    }

    @Override
    public void addRegister(int ref, Register reg) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            m_Registers.put(ref, reg);
        }
    }

    @Override
    public void removeRegister(Register reg) {
        if (!isLocked()) {
            m_Registers.remove(getKeyByValue(m_Registers, reg));
        }
    }

    @Override
    public void setRegister(int ref, Register reg)
            throws IllegalAddressException {
        if (!isLocked()) {
            if (!m_Registers.containsKey(ref)) {
                throw new IllegalAddressException();
            }
            m_Registers.replace(ref, reg);
        }
    }

    @Override
    public Register getRegister(int ref) throws IllegalAddressException {
        Register result = m_Registers.get(ref);
        if (result == null) {
            throw new IllegalAddressException();
        }

        return result;
    }

    @Override
    public int getRegisterCount() {
        if (m_Registers.isEmpty()) {
            return 0;
        } else {
            return m_Registers.lastKey() + 1;
        }
    }

    public Integer[] getRegisterRefs() {
        return m_Registers.keySet().toArray(new Integer[0]);
    }

    @Override
    public Register[] getRegisterRange(int ref, int count) {
        Register[] iregs = new Register[count];
        for (int i = 0; i < iregs.length; i++) {
            iregs[i] = getRegister(ref + i);
        }
        return iregs;
    }

    @Override
    public void addFile(ModbusFile newFile) {
        if (!isLocked()) {
            int newRef = 0;
            if (!m_Files.isEmpty()) {
                newRef = m_Files.lastKey() + 1;
            }
            m_Files.put(newRef, newFile);
        }
    }

    @Override
    public void addFile(int ref, ModbusFile newFile) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            m_Files.put(ref, newFile);
        }
    }

    @Override
    public void removeFile(ModbusFile oldFile) {
        if (!isLocked()) {
            m_Files.remove(getKeyByValue(m_Files, oldFile));
        }
    }

    @Override
    public void setFile(int fileNumber, ModbusFile file) {
        if (!isLocked()) {
            if (!m_Files.containsKey(fileNumber)) {
                throw new IllegalAddressException();
            }

            m_Files.replace(fileNumber, file);
        }
    }

    @Override
    public ModbusFile getFile(int fileNumber) {
        ModbusFile result = m_Files.get(fileNumber);
        if (result == null) {
            throw new IllegalAddressException();
        }

        return result;
    }

    @Override
    public int getFileCount() {
        if (m_Files.isEmpty()) {
            return 0;
        } else {
            return m_Files.lastKey() + 1;
        }
    }

    @Override
    public ModbusFile getFileByNumber(int ref) {
        if (ref < 0 || ref >= 10000 || m_Files == null) {
            throw new IllegalAddressException();
        }

        synchronized (m_Files) {
            for (ModbusFile file : m_Files.values()) {
                if (file.getFileNumber() == ref) {
                    return file;
                }
            }
        }

        throw new IllegalAddressException();
    }

    @Override
    public void addFIFO(FIFO fifo) {
        if (!isLocked()) {
            int newRef = 0;
            if (!m_FIFOs.isEmpty()) {
                newRef = m_FIFOs.lastKey() + 1;
            }
            m_FIFOs.put(newRef, fifo);
        }
    }

    @Override
    public void addFIFO(int ref, FIFO newFIFO) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            m_FIFOs.put(ref, newFIFO);
        }
    }

    @Override
    public void removeFIFO(FIFO oldFIFO) {
        if (!isLocked()) {
            m_FIFOs.remove(getKeyByValue(m_FIFOs, oldFIFO));
        }
    }

    @Override
    public void setFIFO(int fifoNumber, FIFO fifo) {
        if (!isLocked()) {
            if (!m_FIFOs.containsKey(fifoNumber)) {
                throw new IllegalAddressException();
            }

            m_FIFOs.replace(fifoNumber, fifo);
        }
    }

    @Override
    public FIFO getFIFO(int fifoNumber) {
        FIFO result = m_FIFOs.get(fifoNumber);
        if (result == null) {
            throw new IllegalAddressException();
        }

        return result;
    }

    @Override
    public int getFIFOCount() {
        if (m_FIFOs.isEmpty()) {
            return 0;
        } else {
            return m_FIFOs.lastKey() + 1;
        }
    }

    @Override
    public FIFO getFIFOByAddress(int ref) {
        for (FIFO fifo : m_FIFOs.values()) {
            if (fifo.getAddress() == ref) {
                return fifo;
            }
        }

        return null;
    }

}
