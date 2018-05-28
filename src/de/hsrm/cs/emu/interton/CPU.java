package de.hsrm.cs.emu.interton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.hsrm.cs.emu.interton.exception.CpuInvalidLengthException;
import de.hsrm.cs.emu.interton.exception.CpuInvalidRegisterException;
import de.hsrm.cs.emu.interton.exception.CpuOpcodeInvalidException;
import de.hsrm.cs.emu.interton.exception.CpuStackPointerMismatchException;

//test Se
public class CPU {

	// Program counter
	private static int pc = 0;

	// Register 0 (ACC)
	private static short r0 = 0;

	// Register 1
	private static short r1 = 0;

	// Register 2
	private static short r2 = 0;

	// Register 3
	private static short r3 = 0;

	// Register 4
	private static short r4 = 0;

	// Register 5
	private static short r5 = 0;

	// Register 6
	private static short r6 = 0;

	// Upper Program Status Register
	private static short psu = 0;

	// Lower Program Status Register
	private static short psl = 0;

	// Stack pointer (maybe not needed, should be in PSU(0..2))
	private static short sp = 0;

	// hide constructor
	private CPU() {

	}

	// Rücksprungadreßspeicher
	// stack
	private static short[] subroutineStack = new short[8];
	// private static Stack<Integer> returnAddrStack = new Stack<Integer>();

	private static boolean jumped = false;

	private static int instruction = 1;

	// is opcode invalid?
	public static boolean isOpcodeInvalid(short opcode) {
		// TODO probably easier to find a similarity on bit level
		return 0x00 == opcode || 0x10 == opcode || 0x11 == opcode || 0x90 == opcode || 0x91 == opcode || 0xB6 == opcode
				|| 0xB7 == opcode || 0xC4 == opcode || 0xC5 == opcode || 0xC6 == opcode || 0xC7 == opcode;
	}

	// is opcode valid?
	public static boolean isOpcodeValid(short opcode) {
		return !isOpcodeInvalid(opcode);
	}

	public static short getByteLengthForOpcode(short opcode) {
		if ((opcode & 0xF) <= 0x3 || /* linke spalte im diagramm */
				(opcode & 0xF0) == 0x10 && (opcode & 0xF) >= 0x4 && (opcode & 0xF) <= 0x7
				|| (opcode & 0xF0) == 0x30 && (opcode & 0xF) >= 0x4 && (opcode & 0xF) <= 0x7
				|| (opcode & 0xF0) == 0x90 && (opcode & 0xF) >= 0x4 && (opcode & 0xF) <= 0x7) {
			return 1;
		} else if ((opcode & 0xF) >= 0x4 && (opcode & 0xF) <= 0xB) {
			return 2;
		} else if ((opcode & 0xF) >= 0xC) {
			return 3;
		} else {
			return -1;
		}
	}

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

	// check if the Register-Select-Bit in PSL is set
	public static boolean isRsSet() {
		return 0x1 == ((CPU.getPSL() >> 4) & 0x1);
	}

	// return the r/x value of the opcode
	public static short getRX(short opcode) {
		short rx = (short) (opcode & 0x3);
		return (short) (rx + ((isRsSet() && rx != 0) ? 3 : 0));
	}

	public static short getI(short param1) {
		return (short) ((param1 >> 7) & 0x1);
	}

	public static short getIST(short param1) {
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

	// set register determined by i to val
	private static void setRegister(int i, short val) throws CpuInvalidRegisterException {
		val = (short) (val & 0xff);
		
		switch (i) {
		case 0:
			CPU.setR0(val);
			break;
		case 1:
			CPU.setR1(val);
			break;
		case 2:
			CPU.setR2(val);
			break;
		case 3:
			CPU.setR3(val);
			break;
		case 4:
			CPU.setR4(val);
			break;
		case 5:
			CPU.setR5(val);
			break;
		case 6:
			CPU.setR6(val);
			break;
		default:
			throw new CpuInvalidRegisterException();
		}
	}

	// get register by index i
	public static short getRegister(int i) throws CpuInvalidRegisterException {
		switch (i) {
		case 0:
			return CPU.getR0();
		case 1:
			return CPU.getR1();
		case 2:
			return CPU.getR2();
		case 3:
			return CPU.getR3();
		case 4:
			return CPU.getR4();
		case 5:
			return CPU.getR5();
		case 6:
			return CPU.getR6();
		default:
			throw new CpuInvalidRegisterException();
		}
	}

	// LPSU --Lade PSU mit inhalt von RO
	public static void process0x92(short opcode) {
		CPU.psu = CPU.getR0();
	}

	// LPSL ----Lade PSl mit inhalt von RO
	public static void process0x93(short opcode) {
		CPU.psl = CPU.getR0(); // done
	}

	public static void process0x98_0x9B(short opcode, short param1) throws CpuInvalidRegisterException {
		// (opcode & 0x03) => (bit 8 & 9)
		short opcodeConditionCode = (short) (opcode & 0x03);
		short programmstatusConditionCode = (short) (CPU.getPSL() & 0xC0);
		if(opcodeConditionCode == 3) {
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

	/**
	 * BSTR
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuStackPointerMismatchException
	 */
	public static void process0x38_0x3B(short opcode, short param1) throws CpuStackPointerMismatchException {
		CPU.pushStackAddr((short) (CPU.pc + 3));
		short v = CPU.getRX(opcode);
		short CC = (short) (CPU.getPSL() >> 6);
		if ((v == 0x03) || (CC == v)) {

			pushStackAddr((short) (CPU.pc + 2));

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

			jumped = true;
		} else {
			jumped = false;
		}

	}

	// BSTR gt
	public static void process0x39(short opcode, short param1) {
		short r = CPU.getRX(opcode);
		short CC = (short) (CPU.getPSL() >> 6);
		if ((r == 0x03) || ((r != 0x03) && (CC != r))) {
			CPU.pc = getPC() + 2;
		}

	}

	// BSTR lt
	public static void process0x3A(short opcode, short param1) {
		short r = CPU.getRX(opcode);
		short CC = (short) (CPU.getPSL() >> 6);
		if ((r == 0x03) || ((r != 0x03) && (CC != r))) {
			CPU.pc = getPC() + 2;
		}
	}

	// BSTR un
	public static void process0x3B(short opcode, short param1) {
		short r = CPU.getRX(opcode);
		short CC = (short) (CPU.getPSL() >> 6);
		if ((r == 0x03) || ((r != 0x03) && (CC != r))) {
			CPU.pc = getPC() + 2;
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
		short v = CPU.getRX(opcode);
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

	/**
	 * STRR
	 * 
	 * @param opcode
	 * @param parambbbbbbbbbbbbb1
	 */
	public static void process0xC8_0xCB(short opcode, short param1) {
		// TODO: STRR
		// siehe vgl absolut process0xCC_0xCF....
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
		short r = CPU.getRX(opcode);
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

	/**
	 * COMi
	 * 
	 * @param opcode
	 * @param v
	 * @throws CpuInvalidRegisterException
	 */
	public static void process0xE4_0xE7(short opcode, short v) throws CpuInvalidRegisterException {
		short r = CPU.getRX(opcode);
		short rvalue = CPU.getRegister(r);
		boolean com = (CPU.getPSL() & 0x2) == 0x2;

		if (com) {

			if (rvalue == v) {
				CPU.psl &= ~(1 << 6);
				CPU.psl &= ~(1 << 7);
			} else if (rvalue < v) {
				CPU.psl &= ~(1 << 6);
				CPU.psl |= 1 << 7;
			} else {
				CPU.psl &= ~(1 << 7);
				CPU.psl |= 1 << 6;
			}

		} else {

			boolean beideNegativ = (rvalue & 0x80) == 0x80 && (v & 0x80) == 0x80;
			boolean beidePositiv = (rvalue & 0x80) == 0x00 && (v & 0x80) == 0x00;

			if (rvalue == v) {
				CPU.psl &= ~(1 << 6);
				CPU.psl &= ~(1 << 7);

			} else if ((rvalue & 0x80) < (v & 0x80) || beideNegativ && r < v /* dann ist v kleiner r, da negativ */
					|| beidePositiv && r > v /* dann ist r groesser v */) {
				// r > v
				CPU.psl &= ~(1 << 7);
				CPU.psl |= 1 << 6;

			} else {
				CPU.psl &= ~(1 << 6);
				CPU.psl |= 1 << 7;
			}

		}

	}

	/* ########## 0x40 - 0x4F ########## */
	// ANDZ bit 0 und 1 mit register R0 verunden
	public static void process0x41_0x43(short opcode) throws CpuInvalidRegisterException {
		short bit0_1 = (short) (opcode & 0x3);
		short tmp = (short) (CPU.getRegister(0) & bit0_1);

		CPU.setRegister(CPU.getRegister(0), tmp);
	}

	// ANDI bit0-7 mit bit8und9 verunden (IN WELCHES REG SPEICHERN?)
	public static void process0x44_0x47(short opcode) throws CpuInvalidRegisterException {
		short bit0_7 = (short) (opcode & 0xFF); // 255
		short bit8_9 = (short) (opcode & 0x300); // bit8 und 9
		short tmp = (short) (bit8_9 & bit0_7);

		CPU.setRegister(bit0_7, tmp); // welches reg??
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

	/* ####### 0xA0 - 0xAF ######## */
	// SUBZ addiere bit 0 und bit 1 auf register 0
	public static void process0xA0_0xA3(short opcode) throws CpuInvalidRegisterException {
		short bit0 = (short) (opcode & 0x1);
		short bit1 = (short) (opcode & 0x2);
		short tmp = (short) (bit0 + bit1);

		CPU.setRegister(CPU.getRegister(0), tmp);
	}

	// TODO ...
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
	public static void process0xA8_0xAB(short opcode) throws CpuInvalidRegisterException {
		short bit0_6 = (short) (opcode & 0x7F); // 127
		short bit8 = (short) (opcode & 0x100); // 256
		short bit9 = (short) (opcode & 0x101); // 257

		short ergBit8 = (short) (bit0_6 + bit8);
		short ergBit9 = (short) (bit0_6 + bit9);

		CPU.setRegister(bit8, ergBit8);
		CPU.setRegister(bit9, ergBit9);
	}

	// SUBA addiere bit 0-12 in bit 16 und 17
	public static void process0xAC_0xAF(short opcode) throws CpuInvalidRegisterException {
		short bit0_12 = (short) (opcode & 0x1FFF); // 8191(12 bit)
		short bit16_17 = (short) (opcode & 0x30000); // (16 bit)
		// short bit17 = (short) (opcode & 0x20000);

		short ergBit16 = (short) (bit0_12 + bit16_17);

		CPU.setRegister(bit16_17, ergBit16);
	}

	/* tim */
	/* 0x10 bis 0x1F */
	/**
	 * SPSU (Store Program Status, Upper)
	 * 
	 * Lade das Register RO mit dem Inhalt des oberen Programmstatus-Registers PSU.
	 * 
	 * @param opcode
	 */
	public static void process0x12(short opcode) {
		CPU.setR0(CPU.getPSU());
	}

	/**
	 * SPSL (Store Program Status, Lower)
	 * 
	 * Lade das Register RO mit dem Inhalt des unteren Programmstatus-Registers PSL.
	 * 
	 * @param opcode
	 */
	public static void process0x13(short opcode) {
		CPU.setR0(CPU.getPSL());
	}

	/**
	 * RETC (Return From Subroutine, Conditional)
	 * 
	 * Springe an die Speicheradresse, die zuletzt im Stapelzeiger abgelegt worden
	 * ist, wenn Bit 0 und 1 mit den Statusbit CCO, CC1 übereinstimmen und
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
		}
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

	/**
	 * BCTR (Branch On Condition True Relative)
	 * 
	 * Springe an die mit Bit 0 bis 6 errechnete Speicheradresse, wenn Bit 8 und 9
	 * mit den Statusbit CC1 und CCO übereinstimmen. Springe unbedingt, wenn Bit 8
	 * und 9 auf 1 geseszt sind.
	 * 
	 * @param opcode
	 * @param param1
	 *            Untern 7 Bit = Relative Sprung Adresse (range: -63 and +63). Bit 8
	 *            = Flag für indirekte Adressierung
	 */
	public static void process0x18_0x1B(short opcode, short param1) {
		// (opcode & 0x03) => (bit 8 & 9)
		short opcodeConditionCode = (short) (opcode & 0x03);
		short programmstatusConditionCode = (short) (CPU.getPSL() & 0xC0);
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
	 * BCT (Branch On Condition True Absolute)
	 * 
	 * Springe an die mit Bit 0 bis 14 definierte Speicheradresse, wenn Bit 16 und
	 * 17 mitden Statusbit CCO, CC1 übereinstimmen. Springe unbedingt, wenn Bit 8
	 * und 9 auf 1 gesetzt sind.
	 * 
	 * @param opcode
	 * @param param1
	 * @param param2
	 */
	public static void process0x1C_0x1F(short opcode, short param1, short param2) {
		short opcodeConditionCode = (short) (opcode & 0x03);
		short programmstatusConditionCode = (short) (CPU.getPSU() & 0xC0);
		if (opcodeConditionCode == 3 || programmstatusConditionCode == opcodeConditionCode) {
			boolean indirekt = (param1 & 0x80) == 0x80;
			if (!indirekt) {
				CPU.pc = (short) ((param1 << 8) | (param2 & 0xFF));
			} else {
				// indirekte Adressierung
				CPU.pc = (short) ((GPU.getByte(param1) << 8) | (GPU.getByte(param1 + 1) & 0xFF));
			}
			CPU.jumped = true;
		}
	}

	/* 0x70 bis 0x7F */

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
	 * Lösche jedes Bit des oberen Programmstatuswortes, dessen äquivalentes Bit 0
	 * bis 7 eine 1 enthält.
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
	 * Lösche jedes Bit des unteren Programmstatuswortes, dessen äquivalentes Bit 0
	 * bis 7 eine 1 enthält.
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
	 * Setze jedes Bit des oberen Programmstatuswortes auf 1, dessen äquivalentes
	 * Bit 0 bis 7 eine 1 enthält.
	 * 
	 * @param opcode
	 * @param param1
	 */
	public static void process0x76(short opcode, short param1) {
		CPU.setPSU((short) (CPU.getPSU() | param1));
	}

	/**
	 * PPSL (Preset Program· Status, Lower, Masked)
	 * 
	 * Setze jedes Bit des unteren Programmstatuswortes auf 1, dessen äquivalentes
	 * Bit 0 bis 7 eine 1 enthält.
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
		short register = CPU.getRX(opcode);
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
		short register = CPU.getRX(opcode);
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

	// BDRR
	public static void process0xF8_0xFB(short opcode, short param1) throws CpuInvalidRegisterException {
		if(CPU.instruction==741) {
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

	// DAR ---
	public static void process0x94_0x97(short opcode) throws CpuInvalidRegisterException {
		short r = CPU.getRX(opcode);
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

	// EORZ
	public static void process0x20_0x23(short opcode) throws CpuInvalidRegisterException {
		short rx = CPU.getRX(opcode);
		CPU.setRegister(0, (short) (CPU.getRegister(0) ^ getRegister(rx)));
	}

	// EORI
	public static void process0x24_0x27(short opcode, short param1) throws CpuInvalidRegisterException {
		short rx = CPU.getRX(opcode);
		CPU.setRegister(rx, (short) (getRegister(rx) ^ param1));
	}

	// EORR
	public static void process0x28_0x2B(short opcode, short param1) throws CpuInvalidRegisterException {
		short rx = CPU.getRX(opcode);
		short i = CPU.getI(param1);
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
	public static void process0x2C_0x2F(short opcode, short param1, short param2) throws CpuInvalidRegisterException {
		short rx = CPU.getRX(opcode);
		short i = CPU.getI(param1);
		short ist = CPU.getIST(param1);
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
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) ^ GPU.getByte(addr)));
			break;
		case 1:
			// indexed increment
			CPU.r1++;
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) ^ GPU.getByte(addr + CPU.r1)));
			break;
		case 2:
			// indexed decrement
			CPU.r1--;
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) ^ GPU.getByte(addr + CPU.r1)));
			break;
		case 3:
			// just indexed
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) ^ GPU.getByte(addr + CPU.r1)));
			break;
		default:
		}
	}

	// LODZ (load content of r into r0)
	public static void process0x00_0x03(short opcode) throws CpuInvalidRegisterException {
		short r = getRX(opcode);

		setRegister(0, r);
	}

	// LODI (load value param1 into defined r)
	public static void process0x04_0x07(short opcode, short param1) throws CpuInvalidRegisterException {
		short r = getRX(opcode);

		setRegister(r, param1);
	}

	// LODR (load value of calculated register (bit 0..6) into defined r(7..8))
	public static void process0x08_0x0B(short opcode, short param1) throws CpuInvalidRegisterException {
		short r = getRX(opcode);

		setRegister(r, (short) (param1 & 0xFFFF00));
	}

	// LODA
	public static void process0x0C_0x0F(short opcode, short param1, short param2) throws CpuInvalidRegisterException, CpuOpcodeInvalidException {
		short rx = CPU.getRX(opcode);
		short i = CPU.getI(param1);
		short ist = CPU.getIST(param1);
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
			CPU.setRegister(rx, GPU.getByte(addr));
			break;
//		case 1:
//			// indexed increment
//			CPU.setRegister(rx, (short) (CPU.getRegister(rx) + 1));
//			GPU.setByte(addr + CPU.getRegister(rx), CPU.getRegister(0));
//			break;
		case 2:
			// indexed decrement
			CPU.setRegister(rx, (short) (CPU.getRegister(rx) - 1));
			CPU.setRegister(0, (short) (0xff & GPU.getByte(addr + CPU.getRegister(rx))));
			break;
		case 3:
			// just indexed
			CPU.setRegister((short)0, (short) (0xff & GPU.getByte(addr + CPU.getRegister(rx))));
			break;
		default:
			throw new CpuOpcodeInvalidException();
		}
	}

	// IORZ (load logic OR of r and r0 into r0)
	public static void process0x60_0x63(short opcode) throws CpuInvalidRegisterException {
		short r = getRX(opcode);

		setRegister(0, (short) (r & CPU.getR0()));
	}

	// IORI (load logic OR of r and value param1 into defined r)
	public static void process0x60_0x67(short opcode, short param1) throws CpuInvalidRegisterException {
		short r = getRX(opcode);

		setRegister(r, (short) (r & param1));
	}

	// IORR (load logic OR of r and value of calculated register (bit 0..6) into
	// defined r)
	public static void process0x68_0x6B(short opcode, short param1) throws CpuInvalidRegisterException {
		short r = getRX(opcode);

		setRegister(r, (short) (r & ((param1 & 0xFFFF00))));
	}

	// IORA
	public static void process0x6C_0x6F(short opcode, short param1, short param2) throws CpuInvalidRegisterException {
		short r = getRX(opcode);

		setRegister(r, (short) (r & ((getAddrUpper(param1) & getAddrLower(param2)))));
	}

	/*************************/

	// STRZ
	public static void process0xC0_0xC3(short opcode) throws CpuOpcodeInvalidException, CpuInvalidRegisterException {
		short r = CPU.getRX(opcode);

		CPU.setRegister(r, CPU.getRegister(0));
	}

	// NOP
	public static void process0xC0(short opcode) {
		// done
	}

	// STRA
	public static void process0xCC_0xCF(short opcode, short param1, short param2) throws CpuInvalidRegisterException {
		// see page 23 in Bernstein et al
		short rx = CPU.getRX(opcode);
		short i = CPU.getI(param1);
		short ist = CPU.getIST(param1);
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
			// TODO error
		}
	}

	/************************/

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
		return CPU.psu;
	}

	// check if the Carry-Bit in PSL is set
	public static boolean isCSet() {
		return 0x1 == (CPU.getPSL() & 0x1);
	}

	// check if the Interdigit Carry in PSL is set
	public static boolean isIdcSet() {
		return 0x1 == ((CPU.getPSL() >> 5) & 0x1);
	}

	// set psu
	private static void setPSU(short psu) {
		CPU.psu = psu;
	}

	// get psl
	public static short getPSL() {
		return CPU.psl;
	}

	// get stack pointer
	public static int getSP() {
		return CPU.sp;
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
		System.out.print("\n");
	}

	// public static void dumpStatus() {
	// System.out.printf("==\nPC: %04X ", CPU.getPC());
	// System.out.printf("# INS: %04d\n", CPU.instruction);
	//
	// }

}
