package de.hsrm.cs.emu.interton.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.hsrm.cs.emu.interton.CPU;
import de.hsrm.cs.emu.interton.GPU;
import de.hsrm.cs.emu.interton.exception.CpuInvalidLengthException;
import de.hsrm.cs.emu.interton.exception.CpuOpcodeInvalidException;

// JUnit tests for the CPU
public class CPUTests {

	@Test
	public void testReset() {
		CPU.reset();
		assertEquals(0, CPU.getPC());
	}

	// TODOs

	/*************************/

	@Test
	public void testOpcode0xCC() {

	}

	@Test
	// NOP
	public void testOpcode0xC0() {
		int pc_old = CPU.getPC();
		CPU.process1((short) 0xC0);
		int pc_new = CPU.getPC();

		assertEquals(pc_old + 1, pc_new);
	}

	@Test
	// STRZ r1
	public void testOpcode0xC1() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// r1 = r0
		CPU.process1((short) 0xC1);
		assertEquals(CPU.getR0(), CPU.getR1());
	}

	@Test
	// STRZ r2
	public void testOpcode0xC2() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// r2 = r0
		CPU.process1((short) 0xC2);
		assertEquals(CPU.getR0(), CPU.getR2());
	}

	@Test
	// STRZ r3
	public void testOpcode0xC3() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// r3 = r0
		CPU.process1((short) 0xC3);
		assertEquals(CPU.getR0(), CPU.getR3());
	}

	@Test
	// I=0 => direct
	// IST=0 => non indexed
	public void testOpcode0xCDI0IST0() {
		CPU.process3((short) 0xCD, (short) 0x1F, (short) 0x0E);
	}

	@Test
	// STRA,r1
	// I=0 => direct
	// IST=1 => indexed increment
	public void testOpcode0xCDI0IST1() {
		short r1_old = CPU.getR1();
		CPU.process3((short) 0xCD, (short) 0x3F, (short) 0x0E);
		short r1_new = CPU.getR1();

		assertEquals(r1_old + 1, r1_new);
	}

	@Test
	// I=0 => direct
	// IST=2 => indexed decrement
	public void testOpcode0xCDI0IST2() {
		short r1_old = CPU.getR1();
		CPU.process3((short) 0xCD, (short) 0x5F, (short) 0x0E);
		short r1_new = CPU.getR1();

		assertEquals(r1_old - 1, r1_new);
	}

	@Test
	// I=0 => direct
	// IST=3 => just indexed
	public void testOpcode0xCDI0IST3() {
		short r1_old = CPU.getR1();
		CPU.process3((short) 0xCD, (short) 0x7F, (short) 0x0E);
		short r1_new = CPU.getR1();

		assertEquals(r1_old, r1_new);
	}

	// TODO: fill register with random values ??
	/**
	 * Test für EORz,rX
	 */
	@Test
	public void testOpcode0x20_23() {
		short r0_old = CPU.getR0();
		CPU.process1((short) 0x20);
		assertEquals(r0_old, CPU.getR0());

		r0_old = CPU.getR0();
		short r1 = CPU.getR1();
		CPU.process1((short) 0x21);
		assertEquals((r0_old ^ r1) & 0xFF, CPU.getR0());

		r0_old = CPU.getR0();
		short r2 = CPU.getR2();
		CPU.process1((short) 0x21);
		assertEquals((r0_old ^ r2) & 0xFF, CPU.getR0());

		r0_old = CPU.getR0();
		short r3 = CPU.getR3();
		CPU.process1((short) 0x21);
		assertEquals((r0_old ^ r3) & 0xFF, CPU.getR0());

	}

	// TODO: fill register with random values ??
	/**
	 * Test für EORi,rX
	 */
	@Test
	public void testOpcode0x24_27() {
		short r0 = CPU.getR0();
		CPU.process2((short) 0x24, (short) 0xA);
		assertEquals((r0 ^ 0xA) & 0xFF, CPU.getR0());

		short r1 = CPU.getR1();
		CPU.process2((short) 0x25, (short) 0xB);
		assertEquals((r1 ^ 0xB) & 0xFF, CPU.getR1());

		short r2 = CPU.getR2();
		CPU.process2((short) 0x26, (short) 0xC);
		assertEquals((r2 ^ 0xC) & 0xFF, CPU.getR2());

		short r3 = CPU.getR3();
		CPU.process2((short) 0x27, (short) 0xD);
		assertEquals((r3 ^ 0xD) & 0xFF, CPU.getR3());

	}

	// TODO: fill register and memory with random values ??
	/**
	 * Test für EORr,rX
	 */
	@Test
	public void testOpcode0x28_2B() {
		short r = CPU.getR0();
		short g = GPU.getMem(0x1);
		CPU.process2((short) 0x28, (short) 0x1);
		assertEquals((r ^ g) & 0xFF, CPU.getR0());

		r = CPU.getR1();
		g = GPU.getMem(0x2);
		CPU.process2((short) 0x29, (short) 0x2);
		assertEquals((r ^ 0x2) & 0xFF, CPU.getR1());

		r = CPU.getR2();
		g = GPU.getMem(0x3);
		CPU.process2((short) 0x2A, (short) 0x3);
		assertEquals((r ^ 0x3) & 0xFF, CPU.getR2());

		r = CPU.getR3();
		g = GPU.getMem(0x4);
		CPU.process2((short) 0x2B, (short) 0x4);
		assertEquals((r ^ 0x4) & 0xFF, CPU.getR3());

	}

	@Test
	public void testOpcode0xCE() {

	}

	@Test
	public void testOpcode0xCF() {

	}

	/*************************/
}
