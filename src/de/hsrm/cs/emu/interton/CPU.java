package de.hsrm.cs.emu.interton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.hsrm.cs.emu.interton.exception.CpuInvalidCompareModeException;
import de.hsrm.cs.emu.interton.exception.CpuInvalidLengthException;
import de.hsrm.cs.emu.interton.exception.CpuInvalidRegisterException;
import de.hsrm.cs.emu.interton.exception.CpuOpcodeInvalidException;
import de.hsrm.cs.emu.interton.exception.CpuStackPointerMismatchException;

/**
 * A class which holds the functionalities of the Signetics 2650 CPU
 */
public class CPU {

	// Abbreviations:
	// ACC = Accumulator (same as R0)
	// C = Carry Bit in PSW
	// CC = Condition Code in PSW
	// COM = Compare Bit in PSW
	// F = Flag Bit in PSW
	// IAR = Instruction Address Register (similar to PC, but holds the address of
	// the next register)
	// IDC = Intermediate Carry Bit in PSW
	// II = Interrupt Inhibit Bit in PSW
	// IR = Instruction Register
	// OVF = Overflow Bit in PSW
	// PC = Program Counter
	// PSL = Program Status Lower
	// PSU = Program Status Upper
	// PSW = Program Status Word (16 Bit register of PSU combined with PSL
	// R0 = Register 0
	// R1 = Register 1
	// R2 = Register 2
	// R3 = Register 3
	// R4 = Register 4
	// R5 = Register 5
	// R6 = Register 6
	// RS = Register Select Bit in PSW
	// S = Sense Bit in PSW
	// SP = Stack Pointer in PSW
	// WC = With/Without Carry Bit in PSW

	// == BEGIN INSTANCE VARIBALES ==

	/**
	 * PC = Program Counter.
	 */
	private static int pc = 0;

	/**
	 * R0 = Register 0. This is the ACC = Accumulator. This register is always
	 * available nevertheless the RS bit in the PSW.
	 */
	private static short r0 = 0;

	/**
	 * R1 = Register 1. This register can only be accessed if RS=0.
	 */
	private static short r1 = 0;

	/**
	 * R2 = Register 2. This register can only be accessed if RS=0.
	 */
	private static short r2 = 0;

	/**
	 * R3 = Register 3. This register can only be accessed if RS=0.
	 */
	private static short r3 = 0;

	/**
	 * R4 = Register 4. This register can only be accessed if RS=1.
	 */
	private static short r4 = 0;

	/**
	 * R5 = Register 5. This register can only be accessed if RS=1.
	 */
	private static short r5 = 0;

	/**
	 * R6 = Register 6. This register can only be accessed if RS=1.
	 */
	private static short r6 = 0;

	/**
	 * Upper 8 Bit of the PSW, also called PSU.
	 */
	private static short psu = 0x00; // Sense Bit is connected to VRST of 2621. So we have to set this when we do the
										// VBLANK

	/**
	 * Lower 8 Bit of the PSW, also called PSL.
	 */
	private static short psl = 0;

	// == END INSTANCE VARIABLES ==

	// == BEGIN CONSTRUCTOR ==

	/**
	 * a hidden constructor which never will be used
	 */
	private CPU() {

	}

	// == END CONSTRUCTOR ==

	// == BEGIN GETTER ==

	/**
	 * return value of register given by i
	 * 
	 * @param i
	 *            to specify the register (valid are values from 0 to 3)
	 * @return the value of the register given by i
	 * @throws CpuInvalidRegisterException
	 *             if an invalid i is given
	 */
	public static short getRegister(int i) throws CpuInvalidRegisterException {
		switch (i) {
		case 0:
			return CPU.getR0();
		case 1:
			return CPU.isRsSet() ? CPU.getR4() : CPU.getR1();
		case 2:
			return CPU.isRsSet() ? CPU.getR5() : CPU.getR2();
		case 3:
			return CPU.isRsSet() ? CPU.getR6() : CPU.getR3();
		default:
			throw new CpuInvalidRegisterException();
		}
	}

	/**
	 * if the Sense Bit is set in the PSU register
	 * 
	 * @return if the Sense Bit is set in the PSU register
	 */
	public static boolean isSSet() {
		return 0x1 == ((CPU.psu >> 7) & 0x1);
	}

	/**
	 * if the Flag Bit is set in the PSU register
	 * 
	 * @return if the Flag Bit is set in the PSU register
	 */
	public static boolean isFSet() {
		return 0x1 == ((CPU.psu >> 6) & 0x1);
	}

	/**
	 * if the Interrupt Inhibit Bit is set in the PSU register
	 * 
	 * @return if the Interrupt Inhibit Bit is set in the PSU register
	 */
	public static boolean isIiSet() {
		return 0x1 == ((CPU.psu >> 5) & 0x1);
	}

	/**
	 * returns the current stack pointer (stored in PSU register)
	 * 
	 * @return the current stack pointer
	 */
	public static short getSP() {
		return (short) (CPU.psu & 0x7);
	}

	/**
	 * returns the current Condition Code (sotred in PSL register)
	 * 
	 * @return the current Condition Code
	 */
	public static short getCC() {
		return (short) ((CPU.psl >> 6) & 0x3);
	}

	/**
	 * if the Intermediate Carry Bit is set in the PSL register
	 * 
	 * @return if the Intermediate Carry Bit is set in the PSL register
	 */
	public static boolean isIdcSet() {
		return 0x1 == ((CPU.psl >> 5) & 0x1);
	}

	/**
	 * if the Register Select Bit is set in the PSL register
	 * 
	 * @return if the Register Select Bit is set in the PSL register
	 */
	public static boolean isRsSet() {
		return 0x1 == ((CPU.psl >> 4) & 0x1);
	}

	/**
	 * if the With Carry Bit is set in the PSL register
	 * 
	 * @return if the With Carry Bit is set in the PSL register
	 */
	public static boolean isWcSet() {
		return 0x1 == ((CPU.psl >> 3) & 0x1);
	}

	/**
	 * if the Overflow Bit is set in the PSL register
	 * 
	 * @return if the Overflow Bit is set in the PSL register
	 */
	public static boolean isOvfSet() {
		return 0x1 == ((CPU.psl >> 2) & 0x1);
	}

	/**
	 * if the Compare Bit is set in the PSL register
	 * 
	 * @return if the Compare Bit is set in the PSL register
	 */
	public static boolean isComSet() {
		return 0x1 == ((CPU.psl >> 1) & 0x1);
	}

	/**
	 * if the Carry Bit is set in the PSL register
	 * 
	 * @return if the Carry Bit is set in the PSL register
	 */
	public static boolean isCSet() {
		return 0x1 == (CPU.getPSL() & 0x1);
	}

	// == END GETTER ==

	// == BEGIN SETTER ==

	/**
	 * set the value of the register specified by i to the given value val
	 * 
	 * @param i
	 *            to specify the register (valid are values from 0 to 3)
	 * @param val
	 *            the value which should be set
	 * @throws CpuInvalidRegisterException
	 *             if the given i is invalid
	 */
	private static void setRegister(int i, short val) throws CpuInvalidRegisterException {
		val = (short) (val & 0xff);

		switch (i) {
		case 0:
			CPU.setR0(val);
			break;
		case 1:
			if (CPU.isRsSet()) {
				CPU.setR4(val);
			} else {
				CPU.setR1(val);
			}
			break;
		case 2:
			if (CPU.isRsSet()) {
				CPU.setR5(val);
			} else {
				CPU.setR2(val);
			}
			break;
		case 3:
			if (CPU.isRsSet()) {
				CPU.setR6(val);
			} else {
				CPU.setR3(val);
			}
			break;
		default:
			throw new CpuInvalidRegisterException();
		}
	}

	/**
	 * Setzt CC in Abhaenigkeit von <code>value</code>. Equvivalent zu
	 * <cc>CPU.setCC(value, 0, 1)</cc> -> {@link CPU#setCC(short, short, short)}
	 * 
	 * @param value
	 *            Wert,der mit 0 vergleichen wird.
	 * @throws CpuInvalidCompareModeException
	 *             <i>kommt nicht vor</i>
	 */
	public static void setCC(short value) throws CpuInvalidCompareModeException {
		CPU.setCC(value, (short) 1);
	}

	/**
	 * 
	 * * Setzt CC in Abhaenigkeit von <code>value</code>. Equvivalent zu
	 * <cc>CPU.setCC(value, 0, com)</cc> -> {@link CPU#setCC(short, short, short)}
	 * 
	 * @param value
	 *            Wert,der mit 0 vergleichen wird.
	 * @param com
	 *            Vergleichs-Modus COM=1 (logical mode) COM=0 (arithmetic mode)
	 * @throws CpuInvalidCompareModeException
	 *             <i>kommt nicht vor</i>
	 */
	public static void setCC(short value, short com) throws CpuInvalidCompareModeException {
		CPU.setCC(value, (short) 0, com);
	}

	/**
	 * Setzt CC in abhaenigkeit von value und value2 mit dem Modus com
	 * 
	 * @param value
	 *            Wert 1
	 * @param value2
	 *            Wert 2
	 * @param com
	 *            Vergleichs-Modus COM=1 (logical mode) COM=0 (arithmetic mode)
	 * @throws CpuInvalidCompareModeException
	 *             Wenn Vergleichs-Modus keinen gueltigen Wert enthaelt
	 * 
	 */
	public static void setCC(short value, short value2, short com) throws CpuInvalidCompareModeException {
		if (com == 1) {
			// equal to zero?
			if (value == value2) {
				// CC = %00
				CPU.psl &= ~(1 << 6);
				CPU.psl &= ~(1 << 7);
				// negative
			} else if (value < value2) {
				// CC = %10
				CPU.psl &= ~(1 << 6);
				CPU.psl |= 1 << 7;
				// positive
			} else {
				// CC = %01
				CPU.psl |= 1 << 6;
				CPU.psl &= ~(1 << 7);
			}

		} else if (com == 0) {

			boolean beideNegativ = (value & 0x80) == 0x80 && (value2 & 0x80) == 0x80;
			boolean beidePositiv = (value & 0x80) == 0x00 && (value2 & 0x80) == 0x00;
			if (value == value2) {
				CPU.psl &= ~(1 << 6);
				CPU.psl &= ~(1 << 7);
			} else if ((value & 0x80) < (value2 & 0x80) || beideNegativ && value < value2
					|| beidePositiv && value2 > value2) {
				CPU.psl &= ~(1 << 7);
				CPU.psl |= 1 << 6;

			} else {
				CPU.psl &= ~(1 << 6);
				CPU.psl |= 1 << 7;
			}

		} else {
			throw new CpuInvalidCompareModeException();
		}

	}

	// == END SETTER ==

	// == BEGIN HELPER METHODS ==

	/**
	 * returns if the given opcode is invalid for the cpu
	 * 
	 * @param opcode
	 *            the opcode which should be tested
	 * @return if the given opcode is invalid
	 */
	public static boolean isOpcodeInvalid(short opcode) {
		opcode = (short) (opcode & 0xFF);

		// see page 53 of Bernstein et al for the invalid opcodes
		return 0x00 == opcode || 0x10 == opcode || 0x11 == opcode || 0x90 == opcode || 0x91 == opcode || 0xB6 == opcode
				|| 0xB7 == opcode || 0xC4 == opcode || 0xC5 == opcode || 0xC6 == opcode || 0xC7 == opcode;
	}

	/**
	 * returns if the given opcode is valid for the cpu
	 * 
	 * @param opcode
	 *            the opcode which should be tested
	 * @return if the given opcode is valid
	 */
	public static boolean isOpcodeValid(short opcode) {
		return !isOpcodeInvalid((short) (opcode & 0xFF));
	}

	/**
	 * returns the length of the given opcode
	 * 
	 * @param opcode
	 *            the opcode which length should be returned
	 * @return the length in byte of the given instruction (can only be 1 [byte], 2
	 *         [byte] or 3 [byte])
	 */
	public static short getByteLengthForOpcode(short opcode) {
		opcode = (short) (opcode & 0xFF);

		// see page 53 of Bernstein et al for the byte length of the instructions
		if ( // this is the first column in the diagram
		(opcode & 0x0F) <= 0x03
				// this is the first exception (RETC) in the second column (only 1 byte)
				|| (opcode & 0xF0) == 0x10 && (opcode & 0x0F) >= 0x04 && (opcode & 0x0F) <= 0x07
				// this is the second exception (RETE) in the second column (only 1 byte)
				|| (opcode & 0xF0) == 0x30 && (opcode & 0x0F) >= 0x04 && (opcode & 0x0F) <= 0x07
				// this is the third exception (DAR) in the second column (only 1 byte)
				|| (opcode & 0xF0) == 0x90 && (opcode & 0x0F) >= 0x04 && (opcode & 0x0F) <= 0x07) {
			return 1; // 1 byte
		} else if ((opcode & 0x0F) >= 0x04 && (opcode & 0x0F) <= 0x0B) {
			// rest of second and third column is always 2 byte
			return 2; // 2 byte
		} else if ((opcode & 0x0F) >= 0x0C) {
			// fourth column is always 3 byte
			return 3; // 3 byte
		} else {
			// should not happen
			return -1; // -1 signalizes an error
		}
	}

	public static void toggleSense() {
		CPU.psu = (short) (CPU.psu ^ 0x80);
	}

	// == END HELPER METHODS ==

	// == BEGIN OPCODE METHODS ==

	/**
	 * LODZ
	 * 
	 * @param opcode
	 * @throws CpuInvalidRegisterException
	 * @throws CpuInvalidCompareModeException
	 */
	public static void process0x00_0x03(short opcode)
			throws CpuInvalidRegisterException, CpuInvalidCompareModeException {
		// get source register
		short r = CPU.getLast2Bits(opcode);

		// source register valid?
		if (r > 0 && r <= 3) {
			// get content of source register
			short result = CPU.getRegister(r);
			// set content of source register to register 0
			CPU.setRegister(0, result);
			// adjust CC in PSW accordingly
			CPU.setCC(result);
		} else {
			// LODZ r0 (0x00) is illegal opcode
			throw new CpuInvalidRegisterException();
		}

		CPU.jumped = false;
	}

	/**
	 * LODI
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 * @throws CpuInvalidCompareModeException
	 */
	public static void process0x04_0x07(short opcode, short param1)
			throws CpuInvalidRegisterException, CpuInvalidCompareModeException {
		// get target register
		short r = CPU.getLast2Bits(opcode);
		// get byte which should be set
		short result = param1;
		// set content of target register to second byte of instruction
		CPU.setRegister(r, result);
		// adjust CC in PSW accordingly
		CPU.setCC(result);

		CPU.jumped = false;
	}

	/**
	 * LODR
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 * @throws CpuInvalidCompareModeException
	 */
	public static void process0x08_0x0B(short opcode, short param1)
			throws CpuInvalidRegisterException, CpuInvalidCompareModeException {
		// get target register
		short r = getLast2Bits(opcode);
		// check if indirect addressing
		boolean indirekt = (param1 & 0x80) == 0x80;
		// parse address out of second byte
		param1 = (short) (param1 & 0x7F);
		// correct signed value
		if (param1 > 63)
			param1 = (short) (param1 - 128);
		short result = 0;
		if (!indirekt) {
			result = GPU.getByte(CPU.pc + CPU.getByteLengthForOpcode(opcode) + param1);
		} else {
			// indirect addressing
			param1 = (short) ((GPU.getByte(CPU.pc + CPU.getByteLengthForOpcode(opcode) + param1) << 8)
					| (GPU.getByte(CPU.pc + CPU.getByteLengthForOpcode(opcode) + param1 + 1) & 0xFF));
			result = GPU.getByte(param1);
		}
		// set content of target register to calculated result
		CPU.setRegister(r, result);
		// adjust CC in PSW accordingly
		CPU.setCC(result);

		CPU.jumped = false;
	}

	/**
	 * LODA
	 * 
	 * @param opcode
	 * @param param1
	 * @param param2
	 * @throws CpuInvalidRegisterException
	 * @throws CpuOpcodeInvalidException
	 * @throws CpuInvalidCompareModeException
	 */
	public static void process0x0C_0x0F(short opcode, short param1, short param2)
			throws CpuInvalidRegisterException, CpuOpcodeInvalidException, CpuInvalidCompareModeException {
		// get target register or index register
		short rx = CPU.getLast2Bits(opcode);
		// get if indirect addressing is used
		short i = CPU.getIndirectAddressing(param1);
		// get index control
		short ic = CPU.getIndexControl(param1);
		// get upper address part
		short addr_u = CPU.getAddrUpper(param1);
		// get lower address part
		short addr_l = CPU.getAddrLower(param2);

		// combine address to full address
		int addr = CPU.getAddr(addr_u, addr_l);
		if (i == 0x1) {
			// indirect addressing
			// get value at address and save as new address
			addr_u = GPU.getByte(addr);
			addr_l = GPU.getByte(addr + 1);
			addr = CPU.getAddr(addr_u, addr_l);
		}

		short result = 0;
		switch (ic) {
		case 0:
			// non-indexed
			result = GPU.getByte(addr);
			CPU.setRegister(rx, result);
			break;
		case 1:
			// indexed increment
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) + 1));
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setRegister(0, result);
			break;
		case 2:
			// indexed decrement
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) - 1));
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setRegister(0, result);
			break;
		case 3:
			// just indexed
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setRegister(0, result);
			break;
		default:
			throw new CpuOpcodeInvalidException();
		}

		// adjust CC in PSW accordingly
		CPU.setCC(result);

		CPU.jumped = false;
	}

	// opcode 0x10 and opcode 0x11 are invalid

	/**
	 * SPSU (Store Program Status, Upper)
	 * 
	 * Lade das Register RO mit dem Inhalt des oberen Programmstatus-Registers PSU.
	 * 
	 * @param opcode
	 * @throws CpuInvalidCompareModeException
	 */
	public static void process0x12(short opcode) throws CpuInvalidCompareModeException {
		short value = CPU.getPSU();
		value = (short) (value & ~0x18); // ensure that bit 3 and bit 4 is 0
		CPU.setR0(value);

		// adjust CC in PSW accordingly
		CPU.setCC(value);

		CPU.jumped = false;
	}

	/**
	 * SPSL (Store Program Status, Lower)
	 * 
	 * Lade das Register RO mit dem Inhalt des unteren Programmstatus-Registers PSL.
	 * 
	 * @param opcode
	 * @throws CpuInvalidCompareModeException
	 */
	public static void process0x13(short opcode) throws CpuInvalidCompareModeException {
		short value = CPU.getPSL();
		CPU.setR0(value);

		// adjust CC in PSW accordingly
		CPU.setCC(value);

		CPU.jumped = false;
	}

	/**
	 * RETC (Return From Subroutine, Conditional)
	 * 
	 * Springe an die Speicheradresse, die zuletzt im Stapelzeiger abgelegt worden
	 * ist, wenn Bit 0 und 1 mit den Statusbit CCO, CC1 �bereinstimmen und
	 * dekrementiere den Stapelzeiger. Springe unbedingt, wenn Bit 8 und 9 auf 1
	 * gesetzt sind.
	 * 
	 * @param opcode
	 * @throws CpuStackPointerMismatchException
	 * 
	 */
	public static void process0x14_0x17(short opcode) throws CpuStackPointerMismatchException {
		if ((opcode & 0x03) == 3 || ((CPU.getPSL() & 0xC0) >> 6) == (opcode & 0x03)) {
			CPU.pc = CPU.popStackAddr();
			CPU.jumped = true;
		} else {
			CPU.jumped = false;
		}
	}

	/****************************/

	/**
	 * BCTR (Branch On Condition True Relative)
	 * 
	 * Springe an die mit Bit 0 bis 6 errechnete Speicheradresse, wenn Bit 8 und 9
	 * mit den Statusbit CC1 und CCO �bereinstimmen. Springe unbedingt, wenn Bit 8
	 * und 9 auf 1 geseszt sind.
	 * 
	 * @param opcode
	 * @param param1
	 *            Untern 7 Bit = Relative Sprung Adresse (range: -63 and +63). Bit 8
	 *            = Flag f�r indirekte Adressierung
	 */
	public static void process0x18_0x1B(short opcode, short param1) {
		// (opcode & 0x03) => (bit 8 & 9)
		if (CPU.instruction == 773) {
			System.out.println("");
		}

		short opcodeConditionCode = (short) (opcode & 0x03);
		short programmstatusConditionCode = (short) ((CPU.getPSL() & 0xC0) >> 6);
		if (opcodeConditionCode == 3 || programmstatusConditionCode == opcodeConditionCode) {
			boolean indirekt = (param1 & 0x80) == 0x80;
			param1 = (short) (param1 & 0x7F);
			if (param1 > 63)
				param1 = (short) (param1 - 128);
			if (!indirekt) {
				CPU.pc = CPU.pc + param1;
			} else {
				// indirekte Adressierung
				CPU.pc = CPU.pc + (short) ((GPU.getByte(param1) << 8) | (GPU.getByte(param1 + 1) & 0xFF));
			}
		}
		jumped = false;
	}

	/**
	 * BCTA (Branch On Condition True Absolute)
	 * 
	 * Springe an die mit Bit 0 bis 14 definierte Speicheradresse, wenn Bit 16 und
	 * 17 mitden Statusbit CCO, CC1 �bereinstimmen. Springe unbedingt, wenn Bit 8
	 * und 9 auf 1 gesetzt sind.
	 * 
	 * @param opcode
	 * @param param1
	 * @param param2
	 */
	public static void process0x1C_0x1F(short opcode, short param1, short param2) {
		short opcodeCC = (short) ((opcode & 0x03) & 0xFF);
		short cc = (short) (getCC() & 0xFF);
		if (opcodeCC == 3 || cc == opcodeCC) {
			if (!((param1 & 0x80) == 0x80)) { // TODO: methode check indirekt addressing bit -> CPU.isIndirekt(param1)
				CPU.pc = (short) (((param1 & 0x7F) << 8) | (param2 & 0xFF));
			} else {
				// indirekte Adressierung
				CPU.pc = (short) GPU.getByte((short) (((param1 & 0x7F) << 8) | (param2 & 0xFF)));
			}
			CPU.jumped = true;
		}
	}

	// EORZ
	public static void process0x20_0x23(short opcode) throws CpuInvalidRegisterException {
		short rx = CPU.getLast2Bits(opcode);
		CPU.setRegister(0, (short) (CPU.getRegister(0) ^ getRegister(rx)));
	}

	// EORI
	public static void process0x24_0x27(short opcode, short param1) throws CpuInvalidRegisterException {
		short rx = CPU.getLast2Bits(opcode);
		CPU.setRegister(rx, (short) (getRegister(rx) ^ param1));
	}

	// EORR
	public static void process0x28_0x2B(short opcode, short param1) throws CpuInvalidRegisterException {
		short rx = CPU.getLast2Bits(opcode);
		short i = CPU.getIndirectAddressing(param1);
		short a = (short) (param1 & 0x7F);
		int addr = CPU.getPC();
		if ((a & 0x3F) != 0) {
			addr += ~(a + 1);
		}
		short b = GPU.getByte(addr);
		if (i == 0x1) {
			short b1 = GPU.getByte(addr + 1);
			addr = CPU.getAddr(b, b1);
			b = GPU.getByte(addr);
		}
		CPU.setRegister(rx, (short) (CPU.getRegister(rx) ^ b));
	}

	// EORA
	public static void process0x2C_0x2F(short opcode, short param1, short param2)
			throws CpuInvalidRegisterException, CpuOpcodeInvalidException {
		short rx = CPU.getLast2Bits(opcode);
		short i = CPU.getIndirectAddressing(param1);
		short ist = CPU.getIndexControl(param1);
		short addr_u = CPU.getAddrUpper(param1);
		short addr_l = CPU.getAddrLower(param2);

		int addr = CPU.getAddr(addr_u, addr_l);
		if (i == 0x1) {
			// indirect adressing
			// get value at address and save as new address
			addr_u = GPU.getByte(addr);
			addr_l = GPU.getByte(addr + 1);
			addr = CPU.getAddr(addr_u, addr_l);
		}
		short result = 0;
		switch (ist) {
		case 0:
			// non-indexed
			result = GPU.getByte(addr);
			CPU.setRegister(rx, (short) (result ^ CPU.getRegister(rx)));
			break;
		case 1:
			// indexed increment
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) + 1));
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setRegister(0, (short) (result ^ CPU.getRegister(rx)));
			break;
		case 2:
			// indexed decrement
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) - 1));
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setRegister(0, (short) (result ^ CPU.getRegister(rx)));
			break;
		case 3:
			// just indexed
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setRegister(0, (short) (result ^ CPU.getRegister(rx)));
			break;
		default:
			throw new CpuOpcodeInvalidException();
		}
	}

	public static void process0x30_0x33(short opcode) {
		// not used by PONG
	}

	public static void process0x34_0x37(short opcode) {
		// not used by PONG
	}

	/**
	 * BSTR
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuStackPointerMismatchException
	 */
	public static void process0x38_0x3B(short opcode, short param1) throws CpuStackPointerMismatchException {
		CPU.pushStackAddr((short) (CPU.pc + CPU.getByteLengthForOpcode(opcode)));
		short v = CPU.getLast2Bits(opcode);
		short CC = CPU.getCC();
		if ((v == 0x03) || (CC == v)) {
			boolean indirekt = (param1 & 0x80) == 0x80;
			param1 = (short) (param1 & 0x7F);
			if (param1 > 63)
				param1 = (short) (param1 - 128);
			if (!indirekt) {
				CPU.pc = CPU.pc + param1;
			} else {
				// indirekte Adressierung
				CPU.pc = CPU.pc + (short) ((GPU.getByte(param1) & 0x7F));
			}
			jumped = false;
		} else {
			jumped = false;
		}

	}

	/**
	 * 
	 * BSTA (Branch To Subroutine On Condition True, Absolute)
	 * 
	 * @param opcode
	 * @param param1
	 * @param param2
	 * @throws CpuStackPointerMismatchException
	 */
	public static void process0x3C_0x3F(short opcode, short param1, short param2)
			throws CpuStackPointerMismatchException {
		CPU.pushStackAddr((short) (CPU.pc + 3));
		short v = CPU.getLast2Bits(opcode);
		short CC = (short) (CPU.getPSL() >> 6);
		if ((v == 0x03) || (CC == v)) {
			boolean indirekt = (param1 & 0x80) == 0x80;
			if (!indirekt) {
				CPU.pc = CPU.getAddr((short) (param1 & 0x7F), param2);
			} else {
				// indirekte Adressierung
				CPU.pc = (short) ((GPU.getByte((short) (param1 & 0x7F)) << 8) | (GPU.getByte(param2) & 0xFF));
			}
			jumped = true;
		} else {
			jumped = false;
		}

	}

	public static void process0x40(short opcode) {
		// not used by PONG
	}

	/* ########## 0x40 - 0x4F ########## */
	// ANDZ bit 0 und 1 mit register R0 verunden
	public static void process0x40_0x43(short opcode) throws CpuInvalidRegisterException {
		short bit0_1 = (short) (opcode & 0x3);
		short tmp = (short) (CPU.getRegister(0) & bit0_1);

		CPU.setRegister(CPU.getRegister(0), tmp);
	}

	/**
	 * ANDI
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 * @throws CpuInvalidCompareModeException
	 */
	public static void process0x44_0x47(short opcode, short param1)
			throws CpuInvalidRegisterException, CpuInvalidCompareModeException {
		short r = CPU.getLast2Bits(opcode);
		short v = param1;

		short rvalue = CPU.getRegister(r);
		short result = (short) ((rvalue & v) & 0xFF);
		CPU.setRegister(r, result);

		CPU.setCC(result);
	}

	// ANDR bit0-6 mit bit8-9 verunden (IN WELCHES REG SPEICHERN?)
	public static void process0x48_0x4B(short opcode) throws CpuInvalidRegisterException {
		short bit0_6 = (short) (opcode & 0x7F);
		short bit8_9 = (short) (opcode & 0x300);
		short tmp = (short) (bit0_6 & bit8_9);

		CPU.setRegister(bit0_6, tmp);
	}

	// ANDA bit0-7 mit bit16-17 verunden (IN WELCHES REG SPEICHERN?)
	public static void process0x4C_0x4F(short opcode) throws CpuInvalidRegisterException {
		short bit0_12 = (short) (opcode & 0x1FFF);
		short bit16_17 = (short) (opcode & 0x30000);
		short tmp = (short) (bit0_12 & bit16_17);

		CPU.setRegister(bit0_12, tmp);

	}

	public static void process0x50_0x53(short opcode) throws CpuInvalidRegisterException {

		short r = CPU.getLast2Bits(opcode);
		short result = CPU.getRegister(r);
		short bit_zero = (short) (result & 0x01);
		short bit_six = (short) (result & 0x40);

		result = (short) (result >> 1);

		if (!(CPU.isWcSet())) {
			if (bit_zero == 1) {
				result = (short) (result | 0x80);
			}
			if (bit_zero == 0) {
				result = (short) (result & 0x7F);
			}
		}

		if (CPU.isWcSet()) {
			if (isCSet()) {
				result = (short) (result | 0x80);
			}
			if (!isCSet()) {
				result = (short) (result & 0x7F);
			}
			if (bit_zero == 1) {
				CPU.setCarry(true);
				result = (short) (result | 0x80);
			}
			if (bit_zero == 0) {
				CPU.setCarry(false);
			}
			if (bit_six == 1) {
				CPU.setInterDigitCarry(true);
			}
			if (bit_six == 0) {
				CPU.setInterDigitCarry(false);
			}

		}

		short new_last_bit = (short) (result & 0x80);

		if (new_last_bit != bit_zero) {
			CPU.setOverflow(true);
		}
		CPU.setRegister(r, result);
	}

	public static void process0x54_0x57(short opcode, short param1) {
		// not used by PONG
	}

	/**
	 * BRNR
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 */
	public static void process0x58_0x5B(short opcode, short param1) throws CpuInvalidRegisterException {
		short register = CPU.getLast2Bits(opcode);
		if (CPU.getRegister(register) != 0) {
			boolean indirekt = (param1 & 0x80) == 0x80;
			param1 = (short) (param1 & 0x7F);
			if (param1 > 63)
				param1 = (short) (param1 - 128);
			if (!indirekt) {
				CPU.pc = CPU.pc + param1;
			} else {
				// indirekte Adressierung
				CPU.pc = CPU.pc + (short) ((GPU.getByte(param1) << 8) | (GPU.getByte(param1 + 1) & 0xFF));
			}
			CPU.jumped = false;
		}
	}

	public static void process0x5C_0x5F(short opcode, short param1, short param2) throws CpuInvalidRegisterException {
		// not used by PONG
	}

	// IORZ (load logic OR of r and r0 into r0)
	public static void process0x60_0x63(short opcode) throws CpuInvalidRegisterException {
		short r = getLast2Bits(opcode);

		setRegister(0, (short) (r & CPU.getR0()));
	}

	// IORI (load logic OR of r and value param1 into defined r)
	public static void process0x64_0x67(short opcode, short param1) throws CpuInvalidRegisterException {
		short r = getLast2Bits(opcode);

		setRegister(r, (short) (r & param1));
	}

	// IORR (load logic OR of r and value of calculated register (bit 0..6) into
	// defined r)
	public static void process0x68_0x6B(short opcode, short param1) throws CpuInvalidRegisterException {
		short r = getLast2Bits(opcode);

		setRegister(r, (short) (r & ((param1 & 0xFFFF00))));
	}

	// IORA
	public static void process0x6C_0x6F(short opcode, short param1, short param2)
			throws CpuInvalidRegisterException, CpuOpcodeInvalidException {
		short rx = CPU.getLast2Bits(opcode);
		short i = CPU.getIndirectAddressing(param1);
		short ist = CPU.getIndexControl(param1);
		short addr_u = CPU.getAddrUpper(param1);
		short addr_l = CPU.getAddrLower(param2);

		int addr = CPU.getAddr(addr_u, addr_l);
		if (i == 0x1) {
			// indirect adressing
			// get value at address and save as new address
			addr_u = GPU.getByte(addr);
			addr_l = GPU.getByte(addr + 1);
			addr = CPU.getAddr(addr_u, addr_l);
		}
		short result = 0;
		switch (ist) {
		case 0:
			// non-indexed
			result = GPU.getByte(addr);
			CPU.setRegister(rx, (short) (result | CPU.getRegister(rx)));
			break;
		case 1:
			// indexed increment
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) + 1));
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setRegister(0, (short) (result | CPU.getRegister(rx)));
			break;
		case 2:
			// indexed decrement
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) - 1));
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setRegister(0, (short) (result | CPU.getRegister(rx)));
			break;
		case 3:
			// just indexed
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setRegister(0, (short) (result | CPU.getRegister(rx)));
			break;
		default:
			throw new CpuOpcodeInvalidException();
		}

	}

	/**
	 * REDD (Read Data)
	 * 
	 * Hole den Inhalt des Eingabe-Port D in das mit Bit 0 und 1 definierte Register
	 * 
	 * @param opcode
	 * @throws CpuInvalidRegisterException
	 */
	public static void process0x70_0x73(short opcode) throws CpuInvalidRegisterException {
		CPU.setRegister(opcode & 0x3, GPU.getInputD());
	}

	/**
	 * CPSU (Clear Program Status, Upper, Masked)
	 * 
	 * Loesche jedes Bit des oberen Programmstatuswortes, dessen �quivalentes Bit
	 * 0 bis 7 eine 1 enth�lt.
	 * 
	 * @param opcode
	 * @param param1
	 */
	public static void process0x74(short opcode, short param1) {
		CPU.setPSU((short) (CPU.getPSU() & ~param1));
	}

	/**
	 * CPSL (Clear Program Status, Lower, Masked)
	 * 
	 * Loesche jedes Bit des unteren Programmstatuswortes, dessen quivalentes Bit 0
	 * bis 7 eine 1 enthlt.
	 * 
	 * @param opcode
	 * @param param1
	 */
	public static void process0x75(short opcode, short param1) {
		CPU.setPSU((short) (CPU.getPSL() & ~param1));
	}

	/**
	 * PPSU (Preset Program Status, Upper, Masked)
	 * 
	 * Setze jedes Bit des oberen Programmstatuswortes auf 1, dessen quivalentes Bit
	 * 0 bis 7 eine 1 enthlt.
	 * 
	 * @param opcode
	 * @param param1
	 */
	public static void process0x76(short opcode, short param1) {
		CPU.setPSU((short) (CPU.getPSU() | param1));
	}

	/**
	 * PPSL (Preset Program Status, Lower, Masked)
	 * 
	 * Setze jedes Bit des unteren Programmstatuswortes auf 1, dessen �quivalentes
	 * Bit 0 bis 7 eine 1 enth�lt.
	 * 
	 * @param opcode
	 * @param param1
	 */
	public static void process0x77(short opcode, short param1) {
		CPU.setPSU((short) (CPU.getPSL() | param1));
	}

	/**
	 * BSNR (Branch To Subroutine On Non-Zero Register, Relative)
	 * 
	 * Springe an die mit Bit 0 bis 6 errechnete Speicheradresse, wenn der Inhalt
	 * des mit Bit 8 und 9 definierten Registers nicht 0 ist. Bringe vor dem Sprung
	 * in das Unterprogramm die derzeitige Befehlsadresse in den Stapelzeiger.
	 * 
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 * @throws CpuStackPointerMismatchException
	 */
	public static void process0x78_0x7B(short opcode, short param1)
			throws CpuInvalidRegisterException, CpuStackPointerMismatchException {
		short register = CPU.getLast2Bits(opcode);
		if (CPU.getRegister(register) != 0) {
			CPU.pushStackAddr((short) CPU.pc);
			boolean indirekt = (param1 & 0x80) == 0x80;
			param1 = (short) (param1 & 0x7F);
			if (param1 > 63)
				param1 = (short) (param1 - 128);
			if (!indirekt) {
				CPU.pc = CPU.pc + param1;
			} else {
				// indirekte Adressierung
				CPU.pc = CPU.pc + (short) ((GPU.getByte(param1) << 8) | (GPU.getByte(param1 + 1) & 0xFF));
			}
			CPU.jumped = true;
		}
	}

	/**
	 * BSNA (Branch To Subroutine On Non-Zero Register, Absolute)
	 * 
	 * Springe an die mit Bit 0 bis 14 definierte Speicheradresse, wenn der Inhalt
	 * des mit Bit 16 und 17 definierten Registers nicht 0 ist. Bringe vor dem
	 * Sprung in das Unterprogramm die derzeitige Befehlsadresse in den
	 * Stapelzeiger.
	 * 
	 * @param opcode
	 * @param param1
	 * @param param2
	 * @throws CpuInvalidRegisterException
	 * @throws CpuStackPointerMismatchException
	 */
	public static void process0x7C_0x7F(short opcode, short param1, short param2)
			throws CpuInvalidRegisterException, CpuStackPointerMismatchException {
		short register = CPU.getLast2Bits(opcode);
		if (CPU.getRegister(register) != 0) {
			CPU.pushStackAddr((short) CPU.pc);
			boolean indirekt = (param1 & 0x80) == 0x80;
			if (!indirekt) {
				CPU.pc = (short) ((param1 << 8) | (param2 & 0xFF));
			} else {
				// indirekte Adressierung
				CPU.pc = (short) ((GPU.getByte((param1 & ~0x80)) << 8) | (GPU.getByte(param1 + 1) & 0xFF));
			}
			CPU.jumped = true;
		}
	}

	/**
	 * ADDZ
	 * 
	 * 
	 * @param opcode
	 * @throws CpuInvalidRegisterException
	 */
	public static void process0x80_0x83(short opcode) throws CpuInvalidRegisterException {
		short r = CPU.getLast2Bits(opcode);
		short lvalue = CPU.getR0();
		short rvalue = CPU.getRegister(r);
		short result = (short) (lvalue + rvalue);
		CPU.setR0((short) (result & 0xFF));
		CPU.setCarry(result >= 0x100);
		CPU.setOverflow(result >= 0x100);
		CPU.setInterDigitCarry((rvalue & 0x0F) > (result & 0x0F));

		if (result == 0) {
			CPU.psl &= ~(1 << 6);
			CPU.psl &= ~(1 << 7);
		} else if (result > 63) {
			CPU.psl &= ~(1 << 6);
			CPU.psl |= 1 << 7;
		} else {
			CPU.psl &= ~(1 << 7);
			CPU.psl |= 1 << 6;
		}
	}

	/**
	 * ADDI
	 * 
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 */
	public static void process0x84_0x87(short opcode, short param1) throws CpuInvalidRegisterException {
		short r = CPU.getLast2Bits(opcode);
		short rvalue = CPU.getRegister(r);
		short rvalue_origi = CPU.getRegister(r);
		rvalue = (short) (rvalue + param1);

		CPU.setRegister(r, (short) (rvalue & 0xFF));
		CPU.setCarry(rvalue >= 0x100);
		CPU.setOverflow(rvalue >= 0x100);
		CPU.setInterDigitCarry((rvalue_origi & 0x0F) > (rvalue & 0x0F));

		if (rvalue == 0) {
			CPU.psl &= ~(1 << 6);
			CPU.psl &= ~(1 << 7);
		} else if (rvalue > 63) {
			CPU.psl &= ~(1 << 6);
			CPU.psl |= 1 << 7;
		} else {
			CPU.psl &= ~(1 << 7);
			CPU.psl |= 1 << 6;
		}
	}

	/**
	 * ADDR
	 * 
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 * @throws CpuInvalidCompareModeException
	 */
	public static void process0x88_0x8B(short opcode, short param1)
			throws CpuInvalidRegisterException, CpuInvalidCompareModeException {
		short rx = CPU.getLast2Bits(opcode);
		short i = CPU.getIndirectAddressing(param1);
		short a = (short) (param1 & 0x7F);
		int addr = CPU.getPC();
		if (a > 63) {
			a -= 128;
		}
		addr += a;
		short b = GPU.getByte(addr);
		if (i == 0x1) {
			short b1 = GPU.getByte(addr + 1);
			addr = CPU.getAddr(b, b1);
			b = GPU.getByte(addr);
		}
		short rvalue = CPU.getRegister(rx);
		short result = (short) (rvalue + b);
		CPU.setRegister(rx, result);

		CPU.setCarry(result >= 0x100);
		CPU.setOverflow(result >= 0x100);
		CPU.setInterDigitCarry((rvalue & 0x0F) > (result & 0x0F));
		CPU.setCC(result);
	}

	//
	public static void process0xD0_0xD3(short opcode) throws CpuInvalidRegisterException {

		short bit0_1 = (short) (opcode & 0x03);
		short result = CPU.getRegister((int) bit0_1);
		short first_bit = (short) (result & 0x01);
		short last_bit = (short) (result & 0x8000);
		short idc_x = (short) (result & 0x0100);
		if (isWcSet()) {

			if (idc_x == 0) {
				CPU.setInterDigitCarry(false);
			} else {
				CPU.setInterDigitCarry(true);
			}

			if (last_bit == 0) {
				result = (short) (result << 1);
				CPU.setCarry(false);
				if (CPU.isCSet()) {
					result = (short) (result | 0x01);
				} else {
				}
			} else {
				result = (short) (result << 1);
				CPU.setCarry(true);
				if (CPU.isCSet()) {
					result = (short) (result | 0x01);
				} else {
				}
			}
		} else {
			if (last_bit == 0) {
				result = (short) (result << 1);
			} else {
				result = (short) (result << 1);
				result = (short) (result | 0x01);
			}
		}
		short new_first_bit = (short) (result & 0x01);
		if (new_first_bit != first_bit) {
			CPU.setOverflow(true);
		}

		CPU.setRegister(bit0_1, result);

	}

	// ADDA
	public static void process0x8C_0x8F(short opcode, short param1, short param2) {
		short i = CPU.getIndirectAddressing(param1);
		short ic = CPU.getIndexControl(param1);
		short alow = param2;
		short ahigh = CPU.getAddrUpper(param1);

		int addr = CPU.getAddr(ahigh, alow);
		short b = GPU.getByte(addr);

		short bit0_12 = (short) (opcode & 0x1FFF);
		short rx = CPU.getLast2Bits(opcode);
		if (isWcSet()) {
			rx = (short) (rx + bit0_12);
			CPU.setR0(rx);
		}
	}

	// opcode 0x90 and 0x91 are invalid

	// LPSU --Lade PSU mit inhalt von RO
	public static void process0x92(short opcode) {
		CPU.psu = CPU.getR0();
	}

	// LPSL ----Lade PSl mit inhalt von RO
	public static void process0x93(short opcode) {
		CPU.psl = CPU.getR0(); // done
	}

	// DAR ---
	public static void process0x94_0x97(short opcode) throws CpuInvalidRegisterException {
		short r = CPU.getLast2Bits(opcode);
		short result = CPU.getRegister(r);
		short leftSide = (short) (result >> 4);
		short rightSide = (short) (result & 0xF);
		short zweierKomplement = (short) (((~leftSide) & 0xF) + 1); // zweierKomplement bestimmen zur Berechnung

		if (!isCSet() && !isIdcSet()) {// wenn Carrybit und IDC nicht gesetzt sind
			leftSide = (short) (leftSide - zweierKomplement);
			rightSide = (short) (rightSide - zweierKomplement);
			result = (short) (leftSide << 4 + rightSide);
		} else if (!isCSet() && isIdcSet()) {
			leftSide = (short) (leftSide - zweierKomplement);
			result = (short) (leftSide << 4 + rightSide);
		} else if (isCSet() && !isIdcSet()) {
			rightSide = (short) (rightSide - zweierKomplement);
			result = (short) (leftSide << 4 + rightSide);
		}
		CPU.setRegister(r, result);

	}

	public static void process0x98_0x9B(short opcode, short param1) throws CpuInvalidRegisterException {
		// (opcode & 0x03) => (bit 8 & 9)
		short opcodeConditionCode = (short) (opcode & 0x03);
		short programmstatusConditionCode = (short) ((CPU.getPSL() & 0xC0) >> 6);
		if (opcodeConditionCode == 3) {
			throw new CpuInvalidRegisterException();
		}
		if (programmstatusConditionCode != opcodeConditionCode) {
			boolean indirekt = (param1 & 0x80) == 0x80;
			param1 = (short) (param1 & 0x7F);
			if (param1 > 63)
				param1 = (short) (param1 - 128);
			if (!indirekt) {
				CPU.pc = CPU.pc + param1;
			} else {
				// indirekte Adressierung
				CPU.pc = CPU.pc + (short) ((GPU.getByte(param1) << 8) | (GPU.getByte(param1 + 1) & 0xFF));
			}
		}
		jumped = false;
	}

	public static void process0x9B(short opcode, short param1) {
		// not used by PONG
	}

	/**
	 * BCFA (Branch On Condition False Absolute)
	 * 
	 * 
	 * Springe an die mit Bit 0 bis 14 definierte Speicheradresse, wenn Bit 16 und
	 * 17 mit den Statusbit CCO, CC1 nicht uebereinstimmen.
	 * 
	 * @param opcode
	 * @param param1
	 *            bit 7: indirekt , bit 6..0: high order
	 * @param param2
	 *            bit 7..0: low order
	 */
	public static void process0x9C_0x9F(short opcode, short param1, short param2) {
		short opcodeCC = (short) ((opcode & 0x03) & 0xFF);
		short cc = (short) (getCC() & 0xFF);
		if (cc != opcodeCC) {
			if (!((param1 & 0x80) == 0x80)) { // TODO: methode check indirekt addressing bit -> CPU.isIndirekt(param1)
				// direkte addressierung
				CPU.pc = (short) (((param1 & 0x7F) << 8) | (param2 & 0xFF));
			} else {
				// indirekte Adressierung
				CPU.pc = (short) GPU.getByte((short) (((param1 & 0x7F) << 8) | (param2 & 0xFF)));
			}
			CPU.jumped = true;
		}
	}

	public static void process0x9F(short opcode, short param1, short param2) {
		// not used by PONG
	}

	/* ####### 0xA0 - 0xAF ######## */
	// SUBZ addiere bit 0 und bit 1 auf register 0
	public static void process0xA0_0xA3(short opcode) throws CpuInvalidRegisterException {
		short bit0 = (short) (opcode & 0x1);
		short bit1 = (short) (opcode & 0x2);
		short tmp = (short) (bit0 + bit1);

		CPU.setRegister(CPU.getRegister(0), tmp);
	}

	// SUBI addiere bit 0-7 auf bit 8 und bit 9
	public static void process0xA4_0xA7(short opcode, short param1) throws CpuInvalidRegisterException {
		short bit0_7 = (short) (opcode & 0xFF); // 255
		short bit8 = (short) (opcode & 0x100); // 256
		short bit9 = (short) (opcode & 0x101); // 257

		short ergBit8 = (short) (bit0_7 + bit8);
		short ergBit9 = (short) (bit0_7 + bit9);

		CPU.setRegister(bit8, ergBit8);
		CPU.setRegister(bit9, ergBit9);
	}

	// SUBR addiere bit 0-6 auf bit 8 und 9
	public static void process0xA8_0xAB(short opcode, short param1) throws CpuInvalidRegisterException {
		short bit0_6 = (short) (opcode & 0x7F); // 127
		short bit8 = (short) (opcode & 0x100); // 256
		short bit9 = (short) (opcode & 0x101); // 257

		short ergBit8 = (short) (bit0_6 + bit8);
		short ergBit9 = (short) (bit0_6 + bit9);

		CPU.setRegister(bit8, ergBit8);
		CPU.setRegister(bit9, ergBit9);
	}

	// SUBA addiere bit 0-12 in bit 16 und 17
	public static void process0xAC_0xAF(short opcode, short param1, short param2) throws CpuInvalidRegisterException {
		short bit0_12 = (short) (opcode & 0x1FFF); // 8191(12 bit)
		short bit16_17 = (short) (opcode & 0x30000); // (16 bit)
		// short bit17 = (short) (opcode & 0x20000);

		short ergBit16 = (short) (bit0_12 + bit16_17);

		CPU.setRegister(bit16_17, ergBit16);
	}

	public static void process0xB0_0xB3(short opcode) {
		// not used by PONG
	}

	/**
	 * TPSU
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 */
	public static void process0xB4(short opcode, short param1) throws CpuInvalidRegisterException {
		short bitmask = param1;
		boolean testResult = true;

		for (int i = 0; i <= 7; i++) {
			if (((bitmask >> i) & 0x01) == 0x01) {
				// i. bit is set, so we need to test the i. bit in psu
				if (((CPU.psu >> i) & 0x01) != 0x01) {
					testResult = false;
				}
			}
		}

		if (testResult) {
			// CC = 00
			CPU.psl &= ~(1 << 6);
			CPU.psl &= ~(1 << 7);
		} else {
			// CC = 10
			CPU.psl &= ~(1 << 6);
			CPU.psl |= 1 << 7;
		}
	}

	/**
	 * TPSL
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 */
	public static void process0xB5(short opcode, short param1) throws CpuInvalidRegisterException {
		short bitmask = param1;
		boolean testResult = true;

		for (int i = 0; i <= 7; i++) {
			if (((bitmask >> i) & 0x01) == 0x01) {
				// i. bit is set, so we need to test the i. bit in psu
				if (((CPU.psl >> i) & 0x01) != 0x01) {
					testResult = false;
				}
			}
		}

		if (testResult) {
			// CC = 00
			CPU.psl &= ~(1 << 6);
			CPU.psl &= ~(1 << 7);
		} else {
			// CC = 10
			CPU.psl &= ~(1 << 6);
			CPU.psl |= 1 << 7;
		}
	}

	// opcode 0xB6 and opcode 0xB7 are invalid

	/**
	 * BSFR
	 * 
	 * @param opcode
	 * @throws CpuStackPointerMismatchException
	 * @throws CpuInvalidRegisterException
	 */
	public static void process0xB8_0xBB(short opcode, short param1)
			throws CpuStackPointerMismatchException, CpuInvalidRegisterException {
		CPU.pushStackAddr((short) (CPU.pc + CPU.getByteLengthForOpcode(opcode)));
		short v = CPU.getLast2Bits(opcode);
		short CC = CPU.getCC();

		if (v == 0x03) {
			throw new CpuInvalidRegisterException();
		}

		if ((CC != v)) {
			boolean indirekt = (param1 & 0x80) == 0x80;
			param1 = (short) (param1 & 0x7F);
			if (param1 > 63)
				param1 = (short) (param1 - 128);
			if (!indirekt) {
				CPU.pc = CPU.pc + param1;
			} else {
				// indirekte Adressierung
				CPU.pc = CPU.pc + (short) ((GPU.getByte(param1) & 0x7F));
			}
			jumped = false;
		} else {
			jumped = false;
		}
	}

	public static void process0xBB(short opcode, short param1) throws CpuInvalidRegisterException {
		// not used by PONG
	}

	/**
	 * BSFA
	 * 
	 * @param opcode
	 * @param param1
	 * @param param2
	 * @throws CpuInvalidRegisterException
	 */
	public static void process0xBC_0xBF(short opcode, short param1, short param2)
			throws CpuStackPointerMismatchException {
		short cond = (short) (CPU.getLast2Bits(opcode) & 0x3);
		short cc = CPU.getCC();
		short addr_u = (short) (param1 & 0x7F);
		short addr_l = CPU.getAddrLower(param2);
		int addr = CPU.getAddr(addr_u, addr_l).shortValue();
		if (cond != cc) {
			short pc = (short) CPU.getPC();
			CPU.pushStackAddr(pc);
			CPU.pc = addr;
		}
	}

	public static void process0xBF(short opcode, short param1, short param2) throws CpuInvalidRegisterException {
		// not used by PONG
	}

	// NOP
	public static void process0xC0(short opcode) {
		// done
	}

	// STRZ
	public static void process0xC0_0xC3(short opcode) throws CpuOpcodeInvalidException, CpuInvalidRegisterException {
		short r = CPU.getLast2Bits(opcode);

		CPU.setRegister(r, CPU.getRegister(0));
	}

	// opcodes 0xC4 - 0xC7 are invalid

	/**
	 * STRR
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 */
	public static void process0xC8_0xCB(short opcode, short param1) throws CpuInvalidRegisterException {
		short rx = CPU.getLast2Bits(opcode);

		boolean indirekt = (param1 & 0x80) == 0x80;
		param1 = (short) (param1 & 0x7F);
		if (param1 > 63)
			param1 = (short) (param1 - 128);
		if (!indirekt) {
			GPU.setByte(CPU.pc + CPU.getByteLengthForOpcode(opcode) + param1, CPU.getRegister(rx));
		} else {
			// indirekte Adressierung
			GPU.setByte(
					CPU.pc + CPU.getByteLengthForOpcode(opcode)
							+ (short) ((GPU.getByte(param1) << 8) | (GPU.getByte(param1 + 1) & 0xFF)),
					CPU.getRegister(rx));
		}
		jumped = false;
	}

	// STRA
	public static void process0xCC_0xCF(short opcode, short param1, short param2) throws CpuInvalidRegisterException {
		// see page 23 in Bernstein et al
		short rx = CPU.getLast2Bits(opcode);
		short i = CPU.getIndirectAddressing(param1);
		short ist = CPU.getIndexControl(param1);
		short addr_u = CPU.getAddrUpper(param1);
		short addr_l = CPU.getAddrLower(param2);

		int addr = CPU.getAddr(addr_u, addr_l);
		if (i == 0x1) {
			// indirect adressing
			// get value at address and save as new address
			addr_u = GPU.getByte(addr);
			addr_l = GPU.getByte(addr + 1);
			addr = CPU.getAddr(addr_u, addr_l);
		}

		switch (ist) {
		case 0:
			// non-indexed
			GPU.setByte(addr, CPU.getRegister(rx));
			break;
		case 1:
			// indexed increment
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) + 1));
			GPU.setByte(addr + CPU.getRegister(rx), CPU.getRegister(0));
			break;
		case 2:
			// indexed decrement
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) - 1));
			GPU.setByte(addr + CPU.getRegister(rx), CPU.getRegister(0));
			break;
		case 3:
			// just indexed
			GPU.setByte(addr + CPU.getRegister(rx), CPU.getRegister(0));
			break;
		default:
			throw new CpuInvalidRegisterException();
		}
	}

	public static void process0xD4_0xD7(short opcode, short param1) {
		// not used by PONG
	}

	public static void process0xD8_0xDB(short opcode, short param1) {
		// not used by PONG
	}

	public static void process0xDC_0xDF(short opcode, short param1, short param2) {
		// not used by PONG
	} 

	// COMZ Compare to Register Zero Arithmetic/Logical
	public static void process0xE0_0xE3(short opcode)
			throws CpuInvalidRegisterException, CpuInvalidCompareModeException {
		short rx = CPU.getLast2Bits(opcode);
		short com = (short) (CPU.psl & 0x2);

		// vergleicht logisch oder arithmetisch je nach com
		CPU.setCC(rx, CPU.getR0(), com);

		/*
		 * if ((CPU.psl & 0x2) < 1) { // arithmetisch // two's complement if
		 * (rx.shortValue() > 63) { rx = (short) (rx.shortValue() - 128); rx = (short)
		 * (rx.shortValue() + 1); } if (CPU.getR0() > 63) { r0 -= 128; r0 += 1; } //
		 * Compare CPU.setCC(rx, CPU.getR0());
		 * 
		 * } else if ((CPU.psl & 0x2) > 0) { // logisch // Compare CPU.setCC(rx,
		 * CPU.getR0()); }
		 * 
		 */
	}

	/**
	 * COMi
	 * 
	 * @param opcode
	 * @param v
	 * @throws CpuInvalidRegisterException
	 * @throws CpuInvalidCompareModeException
	 */
	public static void process0xE4_0xE7(short opcode, short v)
			throws CpuInvalidRegisterException, CpuInvalidCompareModeException {
		short r = CPU.getLast2Bits(opcode);
		short rvalue = CPU.getRegister(r);
		// boolean com = (CPU.getPSL() & 0x2) == 0x2;
		short com = (short) (CPU.getPSL() & 0x2);

		CPU.setCC(rvalue, v, com);

		// if (com) {
		//
		// if (rvalue == v) {
		// CPU.psl &= ~(1 << 6);
		// CPU.psl &= ~(1 << 7);
		// } else if (rvalue < v) {
		// CPU.psl &= ~(1 << 6);
		// CPU.psl |= 1 << 7;
		// } else {
		// CPU.psl &= ~(1 << 7);
		// CPU.psl |= 1 << 6;
		// }
		//
		// } else {
		//
		// boolean beideNegativ = (rvalue & 0x80) == 0x80 && (v & 0x80) == 0x80;
		// boolean beidePositiv = (rvalue & 0x80) == 0x00 && (v & 0x80) == 0x00;
		//
		// if (rvalue == v) {
		// CPU.psl &= ~(1 << 6);
		// CPU.psl &= ~(1 << 7);
		//
		// } else if ((rvalue & 0x80) < (v & 0x80) || beideNegativ && r < v /* dann ist
		// v kleiner r, da negativ */
		// || beidePositiv && r > v /* dann ist r groesser v */) {
		// // r > v
		// CPU.psl &= ~(1 << 7);
		// CPU.psl |= 1 << 6;
		//
		// } else {
		// CPU.psl &= ~(1 << 6);
		// CPU.psl |= 1 << 7;
		// }
		//
		// }

	}

	public static void process0xE8_0xEB(short opcode, short param1) {
		// not used by PONG
	}

	/**
	 * COMA (Compare Absolute Arithmetic/Logical)
	 * 
	 * @param opcode
	 * @param param1
	 * @param param2
	 * @throws CpuInvalidRegisterException
	 * @throws CpuOpcodeInvalidException
	 * @throws CpuInvalidCompareModeException
	 */
	public static void process0xEC_0xEF(short opcode, short param1, short param2)
			throws CpuInvalidRegisterException, CpuOpcodeInvalidException, CpuInvalidCompareModeException {
		// get target register or index register
		short rx = CPU.getLast2Bits(opcode);
		// get if indirect addressing is used
		short i = CPU.getIndirectAddressing(param1);
		// get index control
		short ic = CPU.getIndexControl(param1);
		// get upper address part
		short addr_u = CPU.getAddrUpper(param1);
		// get lower address part
		short addr_l = CPU.getAddrLower(param2);

		// get Compare Mode; COM=1 (logical mode) COM=0 (arithmetic mode)
		// boolean com = (CPU.getPSL() & 0x2) == 0x2;
		short com = (short) (CPU.getPSL() & 0x2);

		// combine address to full address
		int addr = CPU.getAddr(addr_u, addr_l);
		if (i == 0x1) {
			// indirect addressing
			// get value at address and save as new address
			addr_u = GPU.getByte(addr);
			addr_l = GPU.getByte(addr + 1);
			addr = CPU.getAddr(addr_u, addr_l);
		}

		short result = 0;
		switch (ic) {
		case 0:
			// non-indexed
			result = GPU.getByte(addr);
			CPU.setCC(CPU.getRegister(rx), result, com);
			break;
		case 1:
			// indexed increment
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) + 1));
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setCC(CPU.getR0(), result, com);
			break;
		case 2:
			// indexed decrement
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) - 1));
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setCC(CPU.getR0(), result, com);
			break;
		case 3:
			// just indexed
			result = (short) (0xFF & GPU.getByte(addr + CPU.getRegister(rx)));
			CPU.setCC(CPU.getR0(), result, com);
			break;
		default:
			throw new CpuOpcodeInvalidException();
		}

		CPU.jumped = false;
	}

	public static void process0xF0_0xF3(short opcode) throws CpuInvalidRegisterException {
		// not used by PONG
	}

	public static void process0xF4_0xF7(short opcode, short param1) {
		// not used by PONG
	}

	// BDRR
	public static void process0xF8_0xFB(short opcode, short param1) throws CpuInvalidRegisterException {
		if (CPU.instruction == 741) {
			System.out.println("Debug");
		}
		// (opcode & 0x03) => (bit 8 & 9)
		short r = (short) (opcode & 0x03);
		CPU.setRegister(r, (short) (CPU.getRegister(r) - 1));
		if ((short) (CPU.getRegister(r) & 0xFF) != 0) {
			boolean indirekt = (param1 & 0x80) == 0x80;
			param1 = (short) (param1 & 0x7F);
			if (param1 > 63)
				param1 = (short) (param1 - 128);
			if (!indirekt) {
				CPU.pc = CPU.pc + param1;
			} else {
				// indirekte Adressierung
				CPU.pc = CPU.pc + (short) ((GPU.getByte(param1) << 8) | (GPU.getByte(param1 + 1) & 0xFF));
			}
		}
		jumped = false;
	}

	public static void process0xFC_0xFF(short opcode, short param1, short param2) {
		// not used by PONG
	}

	// == END OPCODE METHODS ==

	// == BEGIN SIMULATION METHODS ==

	// == END SIMULATION METHODS ==

	// R�cksprungadre�speicher
	// stack
	private static short[] subroutineStack = new short[8];
	// private static Stack<Integer> returnAddrStack = new Stack<Integer>();

	private static boolean jumped = false;

	private static int instruction = 1;

	// returns if there is only one variant of the opcode
	// e.g. opcode=0xC0 => true since there is only one variant of this opcode
	// e.g. opcode=0x08 => false since there are four verions of this opcode
	// 0x08 for r0
	// 0x09 for r1
	// 0x0A for r2
	// 0x0B for r3
	public static boolean isOpcodeUnique(short opcode) {
		return 0x9B == opcode || 0x9F == opcode || 0xBB == opcode || 0xBF == opcode || 0x40 == opcode || 0xC0 == opcode
				|| 0x12 == opcode || 0x13 == opcode || 0x92 == opcode || 0x93 == opcode || 0x74 == opcode
				|| 0x75 == opcode || 0x76 == opcode || 0x77 == opcode || 0xB4 == opcode || 0xB5 == opcode;
	}

	public static boolean isOpcodeNonUnique(short opcode) {
		return !CPU.isOpcodeUnique(opcode);
	}

	public static void process(short opcode) throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// check if opcode is valid
		boolean op_valid = isOpcodeValid(opcode);
		if (true == op_valid) {
			// determine length of opcode
			int length = CPU.getByteLengthForOpcode(opcode);

			switch (length) {
			case 1:
				CPU.process1(opcode);
				break;
			case 2:
				CPU.process2(opcode, GPU.getByte(CPU.getPC() + 1));
				break;
			case 3:
				CPU.process3(opcode, GPU.getByte(CPU.getPC() + 1), GPU.getByte(CPU.getPC() + 2));
				break;
			default:
				throw new CpuInvalidLengthException();
			}
		} else {
			throw new CpuOpcodeInvalidException();
		}
	}

	// process 1 byte opcode
	public static void process1(short opcode) {
		try {
			// analyze opcode
			boolean op_unique = CPU.isOpcodeUnique(opcode);

			String methodName = "";
			if (op_unique) {
				methodName = "process0x" + String.format("%02X", opcode);
			} else {
				short opcode_base = (short) (opcode & 0xFC);
				methodName = "process0x" + String.format("%02X", opcode_base) + "_0x"
						+ String.format("%02X", opcode_base + 3);
			}

			Method method = CPU.class.getMethod(methodName, short.class);
			method.invoke(null, opcode);

			if (!jumped) {
				CPU.pc = CPU.pc + 1;
			} else {
				jumped = false;
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	// process 2 byte opcode with 1 param byte
	public static void process2(short opcode, short param1) {
		try {
			// analyze opcode
			boolean op_unique = CPU.isOpcodeUnique(opcode);

			String methodName = "";
			if (op_unique) {
				methodName = "process0x" + String.format("%02X", opcode);
			} else {
				short opcode_base = (short) (opcode & 0xFC);
				methodName = "process0x" + String.format("%02X", opcode_base) + "_0x"
						+ String.format("%02X", opcode_base + 3);
			}

			Method method = CPU.class.getMethod(methodName, short.class, short.class);
			method.invoke(null, opcode, param1);

			if (!jumped) {
				CPU.pc = CPU.pc + 2;
			} else {
				jumped = false;
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	// process 3 byte opcode with 2 param bytes
	public static void process3(short opcode, short param1, short param2) {
		try {
			// analyze opcode
			boolean op_unique = CPU.isOpcodeUnique(opcode);

			String methodName = "";
			if (op_unique) {
				methodName = "process0x" + String.format("%02X", opcode);
			} else {
				short opcode_base = (short) (opcode & 0xFC);
				methodName = "process0x" + String.format("%02X", opcode_base) + "_0x"
						+ String.format("%02X", opcode_base + 3);
			}

			Method method = CPU.class.getMethod(methodName, short.class, short.class, short.class);
			method.invoke(null, opcode, param1, param2);

			if (!jumped) {
				CPU.pc = CPU.pc + 3;
			} else {
				jumped = false;
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	// return the r/x value of the opcode
	public static short getLast2Bits(short opcode) {
		short rx = (short) (opcode & 0x3);
		return (short) (rx + ((isRsSet() && rx != 0) ? 3 : 0));
	}

	public static short getIndirectAddressing(short param1) {
		return (short) ((param1 >> 7) & 0x1);
	}

	public static short getIndexControl(short param1) {
		return (short) ((param1 >> 5) & 0x3);
	}

	public static short getAddrUpper(short param1) {
		return (short) (param1 & 0x1F);
	}

	public static short getAddrLower(short param2) {
		return (short) param2;
	}

	// convert two byte addr to integer
	public static Integer getAddr(short addr_u, short addr_l) {
		String s_addr_u = String.format("%02X", addr_u);
		String s_addr_l = String.format("%02X", addr_l);

		String s_addr = s_addr_u + s_addr_l;
		int addr = Integer.parseInt(s_addr, 16);

		return addr;
	}

	/**
	 * Set Carry in PSL
	 * 
	 * @param set
	 * 
	 */
	private static void setCarry(boolean set) {
		short psl = CPU.psl;
		psl = (short) (set ? (psl | 0x1) : (psl & ~0x1)); // ~0x1 = 0xFE
		CPU.psl = psl;
	}

	private static void setOverflow(boolean set) {
		short psl = CPU.psl;
		psl = (short) (set ? (psl | 0x4) : (psl & ~0x4));
		CPU.psl = psl;
	}

	private static void setInterDigitCarry(boolean set) {
		short psl = CPU.psl;
		psl = (short) (set ? (psl | 0x20) : (psl & ~0x20));
		CPU.psl = psl;
	}

	private static short popStackAddr() throws CpuStackPointerMismatchException {
		int sp = CPU.getPSU() & 0x7;
		if (1 <= sp && sp < 8) {
			// decrease stackpointer
			CPU.setPSU((short) (CPU.getPSU() - 1));
			return CPU.subroutineStack[sp];
		} else
			throw new CpuStackPointerMismatchException();
	}

	/**
	 * @param pc
	 * @throws CpuStackPointerMismatchException
	 */
	private static void pushStackAddr(short pc) throws CpuStackPointerMismatchException {
		int sp = CPU.getPSU() & 0x7;
		if (0 <= sp && sp < 7) {
			CPU.subroutineStack[sp + 1] = pc;
			// increase stackpointer
			CPU.setPSU((short) (CPU.getPSU() + 1));
		} else
			throw new CpuStackPointerMismatchException();
	}

	// reset the cpu
	public static void reset() {
		CPU.pc = 0;
	}

	// get program counter
	public static int getPC() {
		return CPU.pc;
	}

	// set register 0
	private static void setR0(short val) {
		CPU.r0 = val;
	}

	// set register 1
	private static void setR1(short val) {
		CPU.r1 = val;
	}

	// set register 2
	private static void setR2(short val) {
		CPU.r2 = val;
	}

	// set register 3
	private static void setR3(short val) {
		CPU.r3 = val;
	}

	// set register 4
	private static void setR4(short val) {
		CPU.r4 = val;
	}

	// set register 5
	private static void setR5(short val) {
		CPU.r5 = val;
	}

	// set register 6
	private static void setR6(short val) {
		CPU.r6 = val;
	}

	// get register 0
	public static short getR0() {
		return CPU.r0;
	}

	// get register 1
	public static short getR1() {
		return CPU.r1;
	}

	// get register 2
	public static short getR2() {
		return CPU.r2;
	}

	// get register 3
	public static short getR3() {
		return CPU.r3;
	}

	// get register 4
	public static short getR4() {
		return CPU.r4;
	}

	// get register 5
	public static short getR5() {
		return CPU.r5;
	}

	// get register 6
	public static short getR6() {
		return CPU.r6;
	}

	// get psu
	public static short getPSU() {
		CPU.psu = (short) (CPU.psu & ~0x18);
		return CPU.psu;
	}

	// set psu
	private static void setPSU(short psu) {
		psu = (short) (psu & ~0x18);
		CPU.psu = psu;
	}

	// get psl
	public static short getPSL() {
		return CPU.psl;
	}

	public static void step() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		short byt = GPU.getByte(CPU.getPC());
		CPU.process(byt);
		CPU.instruction++;
		if (CPU.instruction == 1000)
			System.exit(1);
	}

	public static void start() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		while (true) {
			CPU.dumpStatus();
			Clock.waitForNextCycle();
			CPU.step();
			// 312 lines (PAL) => TV signal
			// 42 lines => VBLANK

			// TODO implement that in 312/354*17746 instructions in 0
			// and in 42/354*17746 instructions is 1
			if (CPU.instruction % 1000 == 0) {
				CPU.toggleSense();
			}

		}
	}

	public static void dumpStatus() {
		System.out.printf("%d ", CPU.instruction);
		System.out.printf("%04X ", CPU.pc);
		System.out.printf("R0: %02X ", CPU.getR0());
		System.out.printf("R1: %02X ", CPU.getR1());
		System.out.printf("R2: %02X ", CPU.getR2());
		System.out.printf("R3: %02X ", CPU.getR3());
		System.out.printf("PSL: %02X ", CPU.getPSL());
		System.out.printf("$1FC6: %02X ", GPU.getByte(0x1FC6));
		System.out.printf("$1F00: %02X ", GPU.getByte(0x1F00));
		System.out.printf("$1F01: %02X ", GPU.getByte(0x1F01));
		System.out.printf("$1F0C: %02X ", GPU.getByte(0x1F0C));
		System.out.printf("$1F0A: %02X ", GPU.getByte(0x1F0A));
		System.out.printf("$1FC1: %02X ", GPU.getByte(0x1FC1));
		System.out.print("\n");
	}

}
