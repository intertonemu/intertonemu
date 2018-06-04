package de.hsrm.cs.emu.interton.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import de.hsrm.cs.emu.interton.CPU;
import de.hsrm.cs.emu.interton.GPU;
import de.hsrm.cs.emu.interton.exception.CpuInvalidRegisterException;

public class TestCPU_Tim {

	@Before
	public void initCPURegisters() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Method method = CPU.class.getDeclaredMethod("setRegister", int.class, short.class);
		method.setAccessible(true);
		for (int i = 0; i < 3; i++) {
			method.invoke(null, i, (short) ((i + 3) & 0xFF));
		}
	}

	/**
	 * Test für EORz,rX
	 * 
	 * @throws CpuInvalidRegisterException
	 */
	@Test
	public void testOpcode0x20_23() throws CpuInvalidRegisterException {
		short r0, r, r0new, cc;
		for (int i = 0; i < 3; i++) {
			r0 = CPU.getR0();
			r = CPU.getRegister(i);
			r0new = (short) (r0 ^ r);

			CPU.process1((short) (0x20 + i));

			// TODO: check ConditionCode
			cc = CPU.getCC();

			// CC zero
			if (r0new == 0)
				assertEquals("xor zero CondidtionCode | r0 = r0 ^ r" + i + " = " + r0 + "^" + r, (short) r0new,
						(short) cc);

			// CC negative
			if (r0new > 0)
				assertEquals("xor negative CondidtionCode | r0 = r0 ^ r" + i + " = " + r0 + "^" + r, (short) 0x2,
						(short) cc);

			// CC positive
			if (r0new < 0)
				assertEquals("xor positive CondidtionCode | r0 = r0 ^ r" + i + " = " + r0 + "^" + r, (short) 0x1,
						(short) cc);

			r0new &= 0xFF;
			assertEquals("Test: r0 = r0 ^ r" + i + " = " + r0 + "^" + r, r0new, CPU.getR0());
		}

	}

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

	/**
	 * Test für ADDr,rX
	 */
	@Test
	public void testOpcode0x88_8B() {
		short r = CPU.getR0();
		short g = GPU.getMem(0x1);
		CPU.process2((short) 0x88, (short) 0x1);
		assertEquals((r + g) & 0xFF, CPU.getR0());

		r = CPU.getR1();
		g = GPU.getMem(0x2);
		CPU.process2((short) 0x89, (short) 0x2);
		assertEquals((r + 0x2) & 0xFF, CPU.getR1());

		r = CPU.getR2();
		g = GPU.getMem(0x3);
		CPU.process2((short) 0x8A, (short) 0x3);
		assertEquals((r + 0x3) & 0xFF, CPU.getR2());

		r = CPU.getR3();
		g = GPU.getMem(0x4);
		CPU.process2((short) 0x8B, (short) 0x4);
		assertEquals((r + 0x4) & 0xFF, CPU.getR3());

	}
}
