package de.hsrm.cs.emu.interton.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.hsrm.cs.emu.interton.CPU;

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
	// I=0 => direct
	// IST=0 => non indexed
	public void testOpcode0xCDI0IST0() {
		CPU.process3((short)0xCD, (short)0x1F, (short)0x0E);
	}
	
	@Test
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
