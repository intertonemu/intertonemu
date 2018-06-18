package de.hsrm.cs.emu.interton.tests;

import static org.junit.Assert.assertEquals;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import de.hsrm.cs.emu.interton.GPU;

public class SpritsTests {

	@Before
	public void initGPU() {

		//@formatter:off
		boolean _ = Boolean.FALSE;
		boolean O = Boolean.TRUE;
		boolean shape[][] = { {_,_,_,_,_,_,_,_},
							  {_,O,O,O,O,O,O,_},
							  {_,O,_,_,_,_,_,_},
							  {_,O,_,_,_,_,_,_},
							  {_,O,_,_,_,_,_,_},
							  {_,O,_,_,_,_,_,_},
							  {_,O,_,_,_,_,_,_},
							  {_,O,_,_,_,_,_,_},
							  {_,O,O,O,O,O,O,_},
							  {_,_,_,_,_,_,_,_} };
		//@formatter:on

		for (int i = 0; i < shape.length; i++) {
			short val = 0x0;
			for (int j = 0; j < shape[i].length; j++) {
				val = (short) ((val << 1) + (shape[i][j] ? 1 : 0));
			}
			GPU.setByte(0x1F20 + i, val);
		}
	}

	@Test
	public void testSprite3() {
		GPU.calcSprite3();
		assertEquals("", 0x00, GPU.getByte(0x1F20));
		assertEquals("", 0x7E, GPU.getByte(0x1F21));
		assertEquals("", 0x40, GPU.getByte(0x1F22));
		assertEquals("", 0x40, GPU.getByte(0x1F23));
		assertEquals("", 0x40, GPU.getByte(0x1F24));
		assertEquals("", 0x40, GPU.getByte(0x1F25));
		assertEquals("", 0x40, GPU.getByte(0x1F26));
		assertEquals("", 0x40, GPU.getByte(0x1F27));
		assertEquals("", 0x7E, GPU.getByte(0x1F28));
		assertEquals("", 0x00, GPU.getByte(0x1F29));

	}

}
