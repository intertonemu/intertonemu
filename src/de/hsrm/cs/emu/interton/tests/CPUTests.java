package de.hsrm.cs.emu.interton.tests;

import static org.junit.Assert.assertEquals;

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
	
	/* ########## RRR ########## */
	
	@Test
	// RRR r0
	public void testOpcode0x50() {
		short r0_old = CPU.getR0();
		CPU.process1((short)0x50);
		short r0_new = CPU.getR0();
		r0_old = (short) (r0_old >> 1);
		
		assertEquals(r0_old , r0_new);
	}
	
	@Test
	// RRR r1
	public void testOpcode0x51() {
		short r1_old = CPU.getR1();
		CPU.process1((short)0x51);
		short r1_new = CPU.getR1();
		r1_old = (short) (r1_old >> 1);
		
		assertEquals(r1_old , r1_new);
	}
	
	@Test
	// RRR r2
	public void testOpcode0x52() {
		short r2_old = CPU.getR2();
		CPU.process1((short)0x52);
		short r2_new = CPU.getR2();
		r2_old = (short) (r2_old >> 1);
		
		assertEquals(r2_old , r2_new);
	}
	
	@Test
	// RRR r3
	public void testOpcode0x53() {
		short r3_old = CPU.getR3();
		CPU.process1((short)0x53);
		short r3_new = CPU.getR3();
		r3_old = (short) (r3_old >> 1);
		
		assertEquals(r3_old , r3_new);
	}
	
	/* ######### REDE ####### */
	@Test
	// REDE r0
	public void testOpcode0x54() {
		short r0_old = CPU.getR0();
		CPU.process1((short)0x54);
		short r0_new = CPU.getR0();
		
		assertEquals(r0_new , r0_old);
	}
	
	@Test
	// REDE r1
	public void testOpcode0x55() {
		short r1_old = CPU.getR1();
		CPU.process1((short)0x55);
		short r1_new = CPU.getR1();
		
		assertEquals(r1_new , r1_old);
	}
	
	@Test
	// REDE r2
	public void testOpcode0x56() {
		short r2_old = CPU.getR2();
		CPU.process1((short)0x56);
		short r2_new = CPU.getR2();
		
		assertEquals(r2_new , r2_old);
	}
	
	@Test
	// REDE r3
	public void testOpcode0x57() {
		short r3_old = CPU.getR3();
		CPU.process1((short)0x57);
		short r3_new = CPU.getR3();
		
		assertEquals(r3_new , r3_old);
	}
	
	/* ########## BRNR ######### */
	@Test
	// BRNR r0
	public void testOpcode0x58() {
		short r0_old = CPU.getR0();
		CPU.process1((short)0x58);
		short r0_new = CPU.getR0();
		
		// adresse mit bit 8 und 9 != 0 ?
		assertEquals(r0_old , r0_new);
	}
	
	@Test
	// BRNR r1
	public void testOpcode0x59() {
		short r1_old = CPU.getR1();
		CPU.process1((short)0x59);
		short r1_new = CPU.getR1();
		
		// adresse mit bit 8 und 9 != 0 ?
		assertEquals(r1_old , r1_new);
	}
	
	@Test
	// BRNR r2
	public void testOpcode0x5A() {
		short r3_old = CPU.getR3();
		CPU.process1((short)0x5A);
		short r3_new = CPU.getR3();
		
		// adresse mit bit 8 und 9 != 0 ?
		assertEquals(r3_old , r3_new);
	}
	
	@Test
	// BRNR r3
	public void testOpcode0x5B() {
		short r3_old = CPU.getR3();
		CPU.process1((short)0x5B);
		short r3_new = CPU.getR3();
		
		// adresse mit bit 8 und 9 != 0 ?
		assertEquals(r3_old , r3_new);
	}
	
	/* ##### BRNA ##### */
	@Test
	// BRNA r0
	public void testOpcode0x5C() {
		short r0_old = CPU.getR0();
		CPU.process1((short)0x5C);
		short r0_new = CPU.getR0();
		
		// adresse mit bit 16 und 17 != 0 ?
		assertEquals(r0_old , r0_new);
	}
	
	@Test
	// BRNA r1
	public void testOpcode0x5D() {
		short r1_old = CPU.getR1();
		CPU.process1((short)0x5D);
		short r1_new = CPU.getR1();
		
		// adresse mit bit 16 und 17 != 0 ?
		assertEquals(r1_old , r1_new);
	}
	
	@Test
	// BRNA r2
	public void testOpcode0x5E() {
		short r2_old = CPU.getR2();
		CPU.process1((short)0x5E);
		short r2_new = CPU.getR2();
		
		// adresse mit bit 16 und 17 != 0 ?
		assertEquals(r2_old , r2_new);
	}
	
	@Test
	// BRNA r3
	public void testOpcode0x5F() {
		short r3_old = CPU.getR3();
		CPU.process1((short)0x5F);
		short r3_new = CPU.getR3();
		
		// adresse mit bit 16 und 17 != 0 ?
		assertEquals(r3_old , r3_new);
	}
	
	/* ###### WRTC ####### */
	@Test
	//WRTC r0
	public void testOpcode0xB0() {
		// ausgabe port == bit 0 und 1
	}
	@Test
	//WRTC r1
	public void testOpcode0xB1() {
		// ausgabe port == bit 0 und 1
	}
	@Test
	//WRTC r2
	public void testOpcode0xB2() {
		// ausgabe port == bit 0 und 1
	}
	@Test
	//WRTC r3
	public void testOpcode0xB3() {
		// ausgabe port == bit 0 und 1
	}
	
	/* #### TPSU ###### */
	@Test
	public void testOpcode0xB4() {
		// WAS ?
	}
	
	/* #### TPSL #### */
	@Test
	public void testOpcode0xB5() {
		// WAS ?
	}
	
	/* #### BSFR ##### */
	@Test
	//BSFR r0
	public void testOpcode0xB8() {
		//  ??

	}
	
	@Test
	//BSFR r1
	public void testOpcode0xB9() {
		//  ??

	}
	
	@Test
	//BSFR r2
	public void testOpcode0xBA() {
		// ??
	}
	
	@Test
	//BSFR r3
	public void testOpcode0xBB() {
		//  ??

	}
	
	/* #### BSFA ##### */
	@Test
	// BSFA r0
	public void testOpcode0xBC() {
		// ??

	}
	
	@Test
	// BSFA r1
	public void testOpcode0xBD() {
		// ??

	}
	
	@Test
	// BSFA r2
	public void testOpcode0xBE() {
		// ??

	}
	
	@Test
	// BSFA r3
	public void testOpcode0xBF() {
		//  ??

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
	public void testOpcode0xCF() {
		
	}
	
	/*************************/
}
