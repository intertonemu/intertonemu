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
	/************************************/
	
	//SPSU
	@Test
	public void testOpcode1x02() {
		short r0_old = CPU.getR0();
		CPU.process1x02();
		short r0_new = CPU.getR0();
		
		assertEquals(r0_new, r0_old);
	}
	
	//SPSL
	@Test
	public void testOpcode1x03() {
		short r0_old = CPU.getR0();
		CPU.process1x03();
		short r0_new = CPU.getR0();
		
		assertEquals(r0_new, r0_old);
	}
	
	//RETC, eq
	@Test
	public void testOpcode1x04() {
		
	}
	
	//RETC, gt
	@Test
	public void testOpcode1x05() {
		
	}
	
	//RETC, lt
	@Test
	public void testOpcode1x06() {
		
	}
	
	//RETC, un
	@Test
	public void testOpcode1x07() {
		
	}
	
	/*************************/
}
