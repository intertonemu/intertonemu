package de.hsrm.cs.emu.interton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;

import de.hsrm.cs.emu.interton.exception.CpuInvalidLengthException;
import de.hsrm.cs.emu.interton.exception.CpuInvalidRegisterException;
import de.hsrm.cs.emu.interton.exception.CpuOpcodeInvalidException;
import de.hsrm.cs.emu.interton.exception.CpuStackPointerMismatch;

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

	// Rücksprungadreßspeicher
	// stack
	private static short[] subroutineStack = new short[8];
	// private static Stack<Integer> returnAddrStack = new Stack<Integer>();

	private static boolean jumped = false;

	// hide constructor
	private CPU() {

	}

	// is opcode invalid?
	public static boolean isOpcodeInvalid(short opcode) {
		// TODO probably easier to find a similarity on bit level
		return 0x00 == opcode || 0x10 == opcode || 0x11 == opcode || 0x90 == opcode || 0x91 == opcode || 0xB6 == opcode || 0xB7 == opcode || 0xC4 == opcode
				|| 0xC5 == opcode || 0xC6 == opcode || 0xC7 == opcode;
	}

	// is opcode valid?
	public static boolean isOpcodeValid(short opcode) {
		return !isOpcodeInvalid(opcode);
	}

	public static short getByteLengthForOpcode(short opcode) {
		if ((opcode & 0xF) <= 0x3 || (opcode & 0xF0) == 1 || (opcode & 0xF0) == 3 || (opcode & 0xF0) == 9) {
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
		return 0x9B == opcode || 0x9F == opcode || 0xBB == opcode || 0xBF == opcode || 0x40 == opcode || 0xC0 == opcode || 0x12 == opcode || 0x13 == opcode
				|| 0x92 == opcode || 0x93 == opcode || 0x74 == opcode || 0x75 == opcode || 0x76 == opcode || 0x77 == opcode || 0xB4 == opcode || 0xB5 == opcode;
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
				methodName = "process0x" + String.format("%02X", opcode_base) + "_0x" + String.format("%02X", opcode_base + 3);
			}

			Method method = CPU.class.getMethod(methodName, short.class);
			method.invoke(null, opcode);

			if (!jumped) {
				CPU.pc = CPU.pc + 3;
			} else {
				jumped = false;
			}

		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
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
				methodName = "process0x" + String.format("%02X", opcode_base) + "_0x" + String.format("%02X", opcode_base + 3);
			}

			Method method = CPU.class.getMethod(methodName, short.class, short.class);
			method.invoke(null, opcode, param1);

			if (!jumped) {
				CPU.pc = CPU.pc + 3;
			} else {
				jumped = false;
			}

		} catch (NoSuchMethodException ex) {
			// TODO
		} catch (InvocationTargetException ex) {
			// TODO
		} catch (IllegalAccessException ex) {
			// TODO
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
				methodName = "process0x" + String.format("%02X", opcode_base) + "_0x" + String.format("%02X", opcode_base + 3);
			}

			Method method = CPU.class.getMethod(methodName, short.class, short.class, short.class);
			method.invoke(null, opcode, param1, param2);

			if (!jumped) {
				CPU.pc = CPU.pc + 3;
			} else {
				jumped = false;
			}
		} catch (NoSuchMethodException ex) {
			// TODO
		} catch (InvocationTargetException ex) {
			// TODO
		} catch (IllegalAccessException ex) {
			// TODO
		}
	}

	// return the r/x value of the opcode
	public static short getRX(short opcode) {
		return (short) (opcode & 0x3);
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

	/*************************/

	// LODR
	public static void process0x08_0x0B(short opcode, short param1) {

	}

	/* tim */
	/* 0x10 bis 0x1F */
	/**
	 * SPSU (Store Program Status, Upper)
	 * 
	 * Lade das Register RO mit dem Inhalt des oberen Programmstatus-Registers
	 * PSU.
	 * 
	 * @param opcode
	 */
	public static void process0x12(short opcode) {
		CPU.setR0(CPU.getPSU());
	}

	/**
	 * SPSL (Store Program Status, Lower)
	 * 
	 * Lade das Register RO mit dem Inhalt des unteren Programmstatus-Registers
	 * PSL.
	 * 
	 * @param opcode
	 */
	public static void process0x13(short opcode) {
		CPU.setR0(CPU.getPSL());
	}

	/**
	 * RETC (Return From Subroutine, Conditional)
	 * 
	 * Springe an die Speicheradresse, die zuletzt im Stapelzeiger abgelegt
	 * worden ist, wenn Bit 0 und 1 mit den Statusbit CCO, CC1 übereinstimmen
	 * und dekrementiere den Stapelzeiger. Springe unbedingt, wenn Bit 8 und 9
	 * auf 1 gesetzt sind.
	 * 
	 * @param opcode
	 * @throws CpuStackPointerMismatch
	 * 
	 */
	public static void process0x14_0x17(short opcode) throws CpuStackPointerMismatch {
		if ((opcode & 0x03) == 3 || (CPU.getPSU() & 0xC0) == (opcode & 0x03)) {
			CPU.pc = CPU.popStackAddr();
			CPU.jumped = true;
		}
	}

	private static short popStackAddr() throws CpuStackPointerMismatch {
		short returnAddr = -1;
		int sp = CPU.getPSU() & 0x7;
		if (0 <= sp && sp < 8) {
			returnAddr = CPU.subroutineStack[sp];

			// decrease stackpointer
			CPU.setPSU((short) (CPU.getPSU() - 1));
		}

		if (-1 == returnAddr)
			throw new CpuStackPointerMismatch();
		return returnAddr;
	}

	/**
	 * @param pc
	 * @throws CpuStackPointerMismatch
	 */
	private static void pushStackAddr(short pc) throws CpuStackPointerMismatch {
		int sp = CPU.getPSU() & 0x7;
		CPU.subroutineStack[sp + 1] = pc;
		// increase stackpointer
		CPU.setPSU((short) (CPU.getPSU() + 1));
	}

	/**
	 * BCTR (Branch On Condition True Relative)
	 * 
	 * Springe an die mit Bit 0 bis 6 errechnete Speicheradresse, wenn Bit 8 und
	 * 9 mit den Statusbit CC1 und CCO übereinstimmen. Springe unbedingt, wenn
	 * Bit 8 und 9 auf 1 geseszt sind.
	 * 
	 * @param opcode
	 * @param param1
	 *            Untern 7 Bit = Relative Sprung Adresse (range: -63 and +63).
	 *            Bit 8 = Flag für indirekte Adressierung
	 */
	public static void process0x18_0x1B(short opcode, short param1) {
		// (opcode & 0x03) => (bit 8 & 9)
		short opcodeConditionCode = (short) (opcode & 0x03);
		short programmstatusConditionCode = (short) (CPU.getPSU() & 0xC0);
		if (opcodeConditionCode == 3 || programmstatusConditionCode == opcodeConditionCode) {
			boolean indirekt = (param1 & 0x80) == 0x80;
			param1 = (short) (param1 & 0x7F);
			if (param1 > 63)
				param1 = (short) -(param1 & 0x6F);
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
	 * BCT (Branch On Condition True Absolute)
	 * 
	 * Springe an die mit Bit 0 bis 14 definierte Speicheradresse, wenn Bit 16
	 * und 17 mitden Statusbit CCO, CC1 übereinstimmen. Springe unbedingt, wenn
	 * Bit 8 und 9 auf 1 gesetzt sind.
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
	 * Hole den Inhalt des Eingabe-Port D in das mit Bit 0 und 1 definierte
	 * Register
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
	 * Lösche jedes Bit des oberen Programmstatuswortes, dessen äquivalentes Bit
	 * 0 bis 7 eine 1 enthält.
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
	 * Lösche jedes Bit des unteren Programmstatuswortes, dessen äquivalentes
	 * Bit 0 bis 7 eine 1 enthält.
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
	 * Setze jedes Bit des oberen Programmstatuswortes auf 1, dessen
	 * äquivalentes Bit 0 bis 7 eine 1 enthält.
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
	 * Setze jedes Bit des unteren Programmstatuswortes auf 1, dessen
	 * äquivalentes Bit 0 bis 7 eine 1 enthält.
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
	 * Springe an die mit Bit 0 bis 6 errechnete Speicheradresse, wenn der
	 * Inhalt des mit Bit 8 und 9 definierten Registers nicht 0 ist. Bringe vor
	 * dem Sprung in das Unterprogramm die derzeitige Befehlsadresse in den
	 * Stapelzeiger.
	 * 
	 * 
	 * @param opcode
	 * @param param1
	 * @throws CpuInvalidRegisterException
	 * @throws CpuStackPointerMismatch
	 */
	public static void process0x78_0x7B(short opcode, short param1) throws CpuInvalidRegisterException, CpuStackPointerMismatch {
		short register = CPU.getRX(opcode);
		if (CPU.getRegister(register) != 0) {
			CPU.pushStackAddr((short) CPU.pc);
			boolean indirekt = (param1 & 0x80) == 0x80;
			param1 = (short) (param1 & 0x7F);
			if (param1 > 63)
				param1 = (short) -(param1 & 0x6F);
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
	 * Springe an die mit Bit 0 bis 14 definierte Speicheradresse, wenn der
	 * Inhalt des mit Bit 16 und 17 definierten Registers nicht 0 ist. Bringe
	 * vor dem Sprung in das Unterprogramm die derzeitige Befehlsadresse in den
	 * Stapelzeiger.
	 * 
	 * @param opcode
	 * @param param1
	 * @param param2
	 * @throws CpuInvalidRegisterException
	 * @throws CpuStackPointerMismatch
	 */
	public static void process0x7C_0x7F(short opcode, short param1, short param2) throws CpuInvalidRegisterException, CpuStackPointerMismatch {
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

	// EORZ
	public static void process0x20_0x23(short opcode) {

	}

	// STRZ
	public static void process0xC0_0xC3(short opcode) throws CpuOpcodeInvalidException, CpuInvalidRegisterException {
		short r = (short) (opcode & 0x3);

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
			CPU.r1++;
			GPU.setByte(addr + CPU.getRegister(rx), CPU.getRegister(0));
			break;
		case 2:
			// indexed decrement
			CPU.r1--;
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

	// set psu
	private static void setPSU(short psu) {
		CPU.psu = psu;
	}

	// get psl
	public static short getPSL() {
		return CPU.psl;
	}

	/**
	 * Gibt den Stackpointer zurück.
	 * 
	 * @return
	 */
	public static short getSP() {
		return CPU.sp;
	}

	public static void step() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		short byt = GPU.getByte(CPU.getPC());
		CPU.process(byt);
	}

	public static void start() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		while (true) {
			CPU.dumpStatus();
			Clock.waitForNextCycle();
			CPU.step();
		}
	}

	public static void dumpStatus() {
		System.out.println("==\nPC:" + CPU.getPC());
	}

}
