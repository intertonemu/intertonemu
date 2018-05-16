package de.hsrm.cs.emu.interton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.hsrm.cs.emu.interton.exception.CpuInvalidLengthException;
import de.hsrm.cs.emu.interton.exception.CpuInvalidRegisterException;
import de.hsrm.cs.emu.interton.exception.CpuOpcodeInvalidException;
//test Se
public class CPU {
	
	// Program counter
	private static int pc=0;
	
	// Register 0 (ACC)
	private static short r0=0;
	
	// Register 1
	private static short r1=0;
	
	// Register 2
	private static short r2=0;
	
	// Register 3
	private static short r3=0;
	
	// Register 4
	private static short r4=0;
	
	// Register 5
	private static short r5=0;
	
	// Register 6
	private static short r6=0;
	
	// Upper Program Status Register
	private static short psu=0;
	
	// Lower Program Status Register
	private static short psl=0;
	
	// Stack pointer (maybe not needed, should be in PSU(0..2))
	private static short sp=0;
	
	// hide constructor
	private CPU() {
		
	}
	
	// is opcode invalid?
	public static boolean isOpcodeInvalid(short opcode) {
		return 0x00==opcode
			|| 0x10==opcode
			|| 0x11==opcode
			|| 0x90==opcode
			|| 0x91==opcode
			|| 0xB6==opcode
			|| 0xB7==opcode
			|| 0xC4==opcode
			|| 0xC5==opcode
			|| 0xC6==opcode
			|| 0xC7==opcode;
	}
	
	// is opcode valid?
	public static boolean isOpcodeValid(short opcode) {
		return !isOpcodeInvalid(opcode);
	}
	
	public static short getByteLengthForOpcode(short opcode) {
		if((opcode & 0xF) <=0x3
				|| (opcode & 0xF0) == 1
				|| (opcode & 0xF0) == 3
				|| (opcode & 0xF0) == 9) {
			return 1;
		}
		else if((opcode & 0xF) >= 0x4 && (opcode & 0xF) <= 0xB) {
			return 2;
		}
		else if((opcode & 0xF) >= 0xC) {
			return 3;
		}
		else {
			return -1;
		}
	}
	
	// returns if there is only one variant of the opcode
	// e.g. opcode=0xC0 => true since there is only one variant of this opcode
	// e.g. opcode=0x08 => false since there are four verions of this opcode
	//             0x08 for r0
	//             0x09 for r1
	//             0x0A for r2
	//             0x0B for r3
	public static boolean isOpcodeUnique(short opcode) {
		return 0x9B==opcode
			|| 0x9F==opcode
			|| 0xBB==opcode
			|| 0xBF==opcode
			|| 0x40==opcode 
			|| 0xC0==opcode
			|| 0x12==opcode
			|| 0x13==opcode
			|| 0x92==opcode
			|| 0x93==opcode
			|| 0x74==opcode
			|| 0x75==opcode
			|| 0x76==opcode
			|| 0x77==opcode
			|| 0xB4==opcode
			|| 0xB5==opcode;
	}
	
	public static boolean isOpcodeNonUnique(short opcode) {
		return !CPU.isOpcodeUnique(opcode);
	}
	
	public static void process(short opcode) throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// check if opcode is valid
		boolean op_valid = isOpcodeValid(opcode);
		if(true==op_valid) {
			// determine length of opcode
			int length = CPU.getByteLengthForOpcode(opcode);
			
			switch(length) {
				case 1:
					CPU.process1(opcode);
					break;
				case 2:
					CPU.process2(opcode, GPU.getByte(CPU.getPC()+1));
					break;
				case 3:
					CPU.process3(opcode, GPU.getByte(CPU.getPC()+1), GPU.getByte(CPU.getPC()+2));
					break;
			    default:
			    	throw new CpuInvalidLengthException();
			}
		}
		else {
			throw new CpuOpcodeInvalidException();
		}
	}
	
	// process 1 byte opcode
	public static void process1(short opcode) {
		try {
			// analyze opcode
			boolean op_unique = CPU.isOpcodeUnique(opcode);
			
			String methodName = "";
			if(op_unique) {
				methodName = "process0x"+String.format("%02X", opcode);
			} 
			else {
				short opcode_base = (short) (opcode & 0xFC);
				methodName = "process0x"+String.format("%02X", opcode_base)+"_0x"+String.format("%02X", opcode_base+3);
			}
			
			Method method = CPU.class.getMethod(methodName, short.class);
			method.invoke(null, opcode);
			
			if(true//no jump
					) {
			CPU.pc = CPU.pc + 1;
			}
		}
		catch(NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		catch(InvocationTargetException ex) {
			ex.printStackTrace();
		}
		catch(IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}
	
	// process 2 byte opcode with 1 param byte
	public static void process2(short opcode, short param1) {
		try {
			// analyze opcode
			boolean op_unique = CPU.isOpcodeUnique(opcode);
			
			String methodName = "";
			if(op_unique) {
				methodName = "process0x"+String.format("%02X", opcode);
			} 
			else {
				short opcode_base = (short) (opcode & 0xFC);
				methodName = "process0x"+String.format("%02X", opcode_base)+"_0x"+String.format("%02X", opcode_base+3);
			}
			
			Method method = CPU.class.getMethod(methodName, short.class, short.class);
			method.invoke(null, opcode, param1);
			
			if(true
					) {
			CPU.pc = CPU.pc + 2;
			}
		}
		catch(NoSuchMethodException ex) {
		}
		catch(InvocationTargetException ex) {
		}
		catch(IllegalAccessException ex) {
		}
	}
	
	// process 3 byte opcode with 2 param bytes
	public static void process3(short opcode, short param1, short param2) {
		try {
			// analyze opcode
			boolean op_unique = CPU.isOpcodeUnique(opcode);
			
			String methodName = "";
			if(op_unique) {
				methodName = "process0x"+String.format("%02X", opcode);
			} 
			else {
				short opcode_base = (short) (opcode & 0xFC);
				methodName = "process0x"+String.format("%02X", opcode_base)+"_0x"+String.format("%02X", opcode_base+3);
			}
			
			Method method = CPU.class.getMethod(methodName, short.class, short.class, short.class);
			method.invoke(null, opcode, param1, param2);
			
			if(true//no jump
					) {
			CPU.pc = CPU.pc + 3;
			}
		}
		catch(NoSuchMethodException ex) {
		}
		catch(InvocationTargetException ex) {}
		catch(IllegalAccessException ex) {
		}
	}
	
	// check if the Register-Select-Bit in PSL is set
	public static boolean isRsSet() {
		return 0x1 == ((CPU.getPSL() >> 4) & 0x1);
	}
	
	// return the r/x value of the opcode
	public static short getRX(short opcode) {
		short rx = (short) (opcode & 0x3);
		return (short) (rx + ((isRsSet() && rx!=0) ? 3 : 0));
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
		switch(i) {
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
		switch(i) {
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

	// test kommentar
	
	// TODOs
	
	// LODR
	public static void process0x08_0x0B(short opcode, short param1) {

	}
	
	// EORZ
	public static void process0x20_0x23(short opcode) throws CpuInvalidRegisterException {
		short rx = CPU.getRX(opcode);
		CPU.setRegister(0,(short)(CPU.getRegister(0) ^ getRegister(rx)));
	}
	
	// EORI
	public static void process0x24_0x27(short opcode, short param1) throws CpuInvalidRegisterException {
			short rx = CPU.getRX(opcode);
			CPU.setRegister(rx,(short)(getRegister(rx) ^ param1));
	}

	// EORR
	public static void process0x28_0x2B(short opcode, short param1) throws CpuInvalidRegisterException {
		short rx = CPU.getRX(opcode);
		short i = CPU.getI(param1);
		short a = (short)(param1 & 0x7F);
		int addr;

		if(i==0x0){
			
		}
		else if(i==0x1){
		}
	}

	// EORA
	public static void process0x2C_0x2F(short opcode, short param1, short param2) {
		
	}

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
		if(i==0x1) {
			//indirect adressing
			// get value at address and save as new address
			addr_u = GPU.getByte(addr);
			addr_l = GPU.getByte(addr+1);
			addr = CPU.getAddr(addr_u, addr_l);
		}
		
		switch(ist) {
			case 0:
				// non-indexed
				GPU.setByte(addr, CPU.getRegister(rx));
				break;
			case 1:
				// indexed increment
				CPU.r1++;
				GPU.setByte(addr+CPU.getRegister(rx), CPU.getRegister(0));
				break;
			case 2:
				// indexed decrement
				CPU.r1--;
				GPU.setByte(addr+CPU.getRegister(rx), CPU.getRegister(0));
				break;
			case 3:
				// just indexed
				GPU.setByte(addr+CPU.getRegister(rx), CPU.getRegister(0));
				break;
			default:
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
	}
	
	public static void start() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		while(true) {
			CPU.dumpStatus();
			Clock.waitForNextCycle();
			CPU.step();
		}
	}
	
	public static void dumpStatus() {
		System.out.println("==\nPC:"+CPU.getPC());
	}
	
}
