package de.hsrm.cs.emu.interton.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.hsrm.cs.emu.interton.GPU;
import de.hsrm.cs.emu.interton.Sprite;

public class SpritsTests {

	//@formatter:off
	public static boolean  _ = Boolean.FALSE;
	public static boolean O = Boolean.TRUE;
	public static boolean shape[][] =
			{ {_,_,_,_,_,_,_,_},
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

	@Before
	public void initGPU() {

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
		Sprite s = new Sprite(3);
		GPU.calcSprite(s, (short) 0x1F20, ((GPU.getByte(0x1FC2) >>> 3) & 0x7));
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

		for (int i = 0; i < s.getShape().length; i++) {
			for (int j = 0; j < s.getShape()[i].length; j++) {
				if (s.getShape()[i][j]) {
					assertTrue(shape[i][j]);
				}
			}
		}

	}

}