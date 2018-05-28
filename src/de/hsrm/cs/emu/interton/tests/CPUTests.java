package de.hsrm.cs.emu.interton.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import de.hsrm.cs.emu.interton.CPU;
import de.hsrm.cs.emu.interton.exception.CpuInvalidLengthException;
import de.hsrm.cs.emu.interton.exception.CpuOpcodeInvalidException;

// JUnit tests for the CPU
public class CPUTests {

	@Test
	public void testReset() {
		CPU.reset();
		assertEquals(0, CPU.getPC());
	}
	
	//TODOs
	
	/*************************/
	
	@Test
	public void testOpcode0xCC() {
		
	}
	
	@Test 
	// NOP
	public void testOpcode0xC0() {
		int pc_old = CPU.getPC();
		CPU.process1((short)0xC0);
		int pc_new = CPU.getPC();
		
		assertEquals(pc_old+1, pc_new);
	}
	
	@Test
	// STRZ r1
	public void testOpcode0xC1() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// r1 = r0
		CPU.process1((short)0xC1);
		assertEquals(CPU.getR0(), CPU.getR1());
	}
	
	@Test
	// STRZ r2
	public void testOpcode0xC2() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// r2 = r0
		CPU.process1((short)0xC2);
		assertEquals(CPU.getR0(), CPU.getR2());
	}
	
	@Test
	// STRZ r3
	public void testOpcode0xC3() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// r3 = r0
		CPU.process1((short)0xC3);
		assertEquals(CPU.getR0(), CPU.getR3());
	}
	
	@Test
	// I=0 => direct
	// IST=0 => non indexed
	public void testOpcode0xCDI0IST0() {
		CPU.process3((short)0xCD, (short)0x1F, (short)0x0E);
	}
	
	@Test
	// STRA,r1
	// I=0 => direct
	// IST=1 => indexed increment
	public void testOpcode0xCDI0IST1() {
		short r1_old = CPU.getR1();
		CPU.process3((short)0xCD, (short)0x3F, (short)0x0E);
		short r1_new = CPU.getR1();
		
		assertEquals(r1_old+1, r1_new);
	}
	
	@Test
	// I=0 => direct
	// IST=2 => indexed decrement
	public void testOpcode0xCDI0IST2() {
		short r1_old = CPU.getR1();
		CPU.process3((short)0xCD, (short)0x5F, (short)0x0E);
		short r1_new = CPU.getR1();
		
		assertEquals(r1_old-1, r1_new);
	}
	
	@Test
	// I=0 => direct
	// IST=3 => just indexed
	public void testOpcode0xCDI0IST3() {
		short r1_old = CPU.getR1();
		CPU.process3((short)0xCD, (short)0x7F, (short)0x0E);
		short r1_new = CPU.getR1();
		
		assertEquals(r1_old, r1_new);
	}
	
	@Test
	public void testOpcode0xCE() {
		
	}
	
	@Test
	public void testOpcode0xCF() {		CPU.process2((short) 0x78, CPU.getR0());

		
	}
	/************************************/
	
	//SPSU
	@Test
	public void testOpcode0x12() {
		short r0_old = CPU.getR0();
		CPU.process1((short) 0x12);
		short r0_new = CPU.getR0();
		
		assertEquals(CPU.getPSU(), r0_new);
	}
	
	//SPSL
	@Test
	public void testOpcode0x13() {
		short r0_old = CPU.getR0();
		CPU.process1((short) 0x13);
		short r0_new = CPU.getR0();
		
		assertEquals(CPU.getPSL(), r0_new);
	}
	
	//RETC, eq
	@Test
	public void testOpcode1x04() {
		
	}
	
	//RETC, gt
	@Test
	public void testOpcode0x15() {
		
	}
	
	//RETC, lt
	@Test
	public void testOpcode0x16() {
		
	}
	
	//RETC, un
	@Test
	public void testOpcode0x17() {
		
	}
	
	//BCTR, eq
	@Test
	public void testOpcode0x18() {
		
	}
	
	//BCTR, gt
	@Test
	public void testOpcode0x19() {
		
	}
	
	//BCTR, lt
	@Test
	public void testOpcode0x1A() {
		
	}
	
	//BCTR, un
	@Test
	public void testOpcode0x1B() {
		
	}
	
	//BCTA, eq
	@Test
	public void testOpcode0x1C() {
		
	}
	
	//BCTA, gt
	@Test
	public void testOpcode0x1D() {
		
	}
	
	//BCTA, lt
	@Test
	public void testOpcode0x1E() {
		
	}
	
	//BCTA, un
	@Test
	public void testOpcode0x1F() {
		
	}
	
	/************************************/
	
	//REDD, r0
	@Test
	public void testOpcode0x70() {
		short r0_old = CPU.getR0();
		CPU.process1((short) 0x70);
		short r0_new = CPU.getR0();
		
		assertEquals(/*D-Port*/, r0_new);
	}
	
	//REDD, r1
	@Test
	public void testOpcode0x71() {
		short r1_old = CPU.getR1();
		CPU.process1((short) 0x71);
		short r1_new = CPU.getR1();
		
		assertEquals(/*D-Port*/, r1_new);
	}
	
	//REDD, r2
	@Test
	public void testOpcode0x72() {
		short r2_old = CPU.getR2();
		CPU.process1((short) 0x72);
		short r2_new = CPU.getR2();
		
		assertEquals(/*D-Port*/, r2_new);
	}
	
	//REDD, r3
	@Test
	public void testOpcode0x73() {
		short r3_old = CPU.getR3();
		CPU.process1((short) 0x73);
		short r3_new = CPU.getR3();
		
		assertEquals(/*D-Port*/, r3_new);
	}
	
	//CPSU
	@Test
	public void testOpcode0x74() {
		short psu_old = CPU.getPSU();
		CPU.process2((short) 0x74, NULL);
		short psu_new = CPU.getPSU();

		
		assertEquals(0, psu_new);
	}
	
	//CPSL
	@Test
	public void testOpcode0x75() {
		short psl_old = CPU.getPSL();
		CPU.process2((short) 0x75, NULL);
		short psl_new = CPU.getPSL();

		
		assertEquals(0, psl_new);
	}
	
	//PPSU
	@Test
	public void testOpcode0x76() {
		short psu_old = CPU.getPSU();
		CPU.process2((short) 0x76, NULL);
		short psu_new = CPU.getPSU();

		
		assertEquals(psu_new, psu_old);
	}
	
	//PPSL
	@Test
	public void testOpcode0x77() {
		short psl_old = CPU.getPSL();
		CPU.process2((short) 0x77, NULL);
		short psl_new = CPU.getPSL();

		
		assertEquals(1, psl_new);
	}
	
	//BSNR, r0
	@Test
	public void testOpcode0x78() {
		CPU.process2((short) 0x78, CPU.getR0());
		//stackpointer == adresse

	}
	
	//BSNR, r1
	@Test
	public void testOpcode0x79() {
		CPU.process2((short) 0x79, CPU.getR1());
		//stackpointer == adresse

	}
	
	//BSNR, r2
	@Test
	public void testOpcode0x7A() {
		CPU.process2((short) 0x7A, CPU.getR2());
		//stackpointer == adresse

	}
	
	//BSNR, r3
	@Test
	public void testOpcode0x7B() {
		CPU.process2((short) 0x7B, CPU.getR3());
		//stackpointer == adresse

	}
	
	//BSNA, r0
	@Test
	public void testOpcode0x7C() {
		CPU.process2((short) 0x7C, CPU.getR0());
		//stackpointer == adresse

	}
	
	//BSNA, r1
	@Test
	public void testOpcode0x7D() {
		CPU.process2((short) 0x7D, CPU.getR1());
		//stackpointer == adresse

	}
	
	//BSNA, r2
	@Test
	public void testOpcode0x7E() {
		CPU.process2((short) 0x7E, CPU.getR2());
		//stackpointer == adresse

	}
	
	//BSNA, r3
	@Test
	public void testOpcode0x7F() {
		CPU.process2((short) 0x7F, CPU.getR3());
		//stackpointer == adresse
	}
	
	/************************/
}
