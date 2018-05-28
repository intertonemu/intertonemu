package de.hsrm.cs.emu.interton.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.hsrm.cs.emu.interton.CPU;
import de.hsrm.cs.emu.interton.GPU;

// JUnit tests for the CPU
public class CPUTests {

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
		CPU.process1((short) 0x22);
		assertEquals((r0_old ^ r2) & 0xFF, CPU.getR0());

		r0_old = CPU.getR0();
		short r3 = CPU.getR3();
		CPU.process1((short) 0x23);
		assertEquals((r0_old ^ r3) & 0xFF, CPU.getR0());

	}

	// TODO: fill register with random values ??
	/**
	 * Test f�r EORi,rX
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

	/* */

	// TODO: fill register with random values ??
	/**
	 * Test für ADDz,rX
	 */
	@Test
	public void testOpcode0x80_83() {
		short r0 = CPU.getR0();
		CPU.process1((short) 0x80);
		assertEquals(r0 + r0, CPU.getR0());

		r0 = CPU.getR0();
		CPU.process1((short) 0x81);
		assertEquals(r0 + CPU.getR1(), CPU.getR0());

		r0 = CPU.getR0();
		CPU.process1((short) 0x82);
		assertEquals(r0 + CPU.getR2(), CPU.getR0());

		r0 = CPU.getR0();
		CPU.process1((short) 0x83);
		assertEquals(r0 + CPU.getR3(), CPU.getR0());

	}

	// TODO: fill register with random values ??
	/**
	 * Test für ADDi,rX
	 */
	@Test
	public void testOpcode0x84_87() {
		short r0 = CPU.getR0();
		CPU.process2((short) 0x24, (short) 0xA);
		assertEquals((r0 + 0xA) & 0xFF, CPU.getR0());

		short r1 = CPU.getR1();
		CPU.process2((short) 0x25, (short) 0xB);
		assertEquals((r1 + 0xB) & 0xFF, CPU.getR1());

		short r2 = CPU.getR2();
		CPU.process2((short) 0x26, (short) 0xC);
		assertEquals((r2 + 0xC) & 0xFF, CPU.getR2());

		short r3 = CPU.getR3();
		CPU.process2((short) 0x27, (short) 0xD);
		assertEquals((r3 + 0xD) & 0xFF, CPU.getR3());

	}

	// TODO: fill register and memory with random values ??
	/**
	 * Test für ADDr,rX
	 */
	@Test
	public void testOpcode0x88_8B() {
		short r = CPU.getR0();
		short g = GPU.getMem(0x1);
		CPU.process2((short) 0x28, (short) 0x1);
		assertEquals((r + g) & 0xFF, CPU.getR0());

		r = CPU.getR1();
		g = GPU.getMem(0x2);
		CPU.process2((short) 0x29, (short) 0x2);
		assertEquals((r + 0x2) & 0xFF, CPU.getR1());

		r = CPU.getR2();
		g = GPU.getMem(0x3);
		CPU.process2((short) 0x2A, (short) 0x3);
		assertEquals((r + 0x3) & 0xFF, CPU.getR2());

		r = CPU.getR3();
		g = GPU.getMem(0x4);
		CPU.process2((short) 0x2B, (short) 0x4);
		assertEquals((r + 0x4) & 0xFF, CPU.getR3());

	}
}
