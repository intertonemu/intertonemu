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
}
