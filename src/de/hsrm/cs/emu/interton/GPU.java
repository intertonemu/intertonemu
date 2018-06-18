package de.hsrm.cs.emu.interton;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.Graphics;

import com.sun.corba.se.impl.ior.ByteBuffer;

import de.hsrm.cs.emu.interton.exception.RomAddrNotValidException;
import de.hsrm.cs.emu.interton.exception.RomNotInitializedException;

// encapsulates the GPU memory and functionalities
public class GPU {

	public static final int ADDR_BACKGROUND_COLOR = 0x1FC6;
	public static final int ADDR_LEFT_SCORE = 0x1FC8;
	public static final int ADDR_RIGHT_SCORE = 0x1FC9;
	public static final int SCALE = 3;

	private static Sprite sprite1 = null;
	private static Sprite sprite2 = null;
	private static Sprite sprite3 = null;
	private static Sprite sprite4 = null;

	private class Sprite {
		private boolean[][] shape = null; // byte 0 - 9
		private int hc = 0;
		private int hcb = 0;
		private int vc = 0;
		private int vcb = 0; // offset
		private int size = 1; // 1, 2, 4 or 8
		private Color color = null;

		private Sprite() {

		}

		public boolean[][] getShape() {
			return shape;
		}

		public void setShape(boolean[][] shape) {
			this.shape = shape;
		}

		public int getHc() {
			return hc;
		}

		public void setHc(int hc) {
			this.hc = hc;
		}

		public int getHcb() {
			return hcb;
		}

		public void setHcb(int hcb) {
			this.hcb = hcb;
		}

		public int getVc() {
			return vc;
		}

		public void setVc(int vc) {
			this.vc = vc;
		}

		public int getVcb() {
			return vcb;
		}

		public void setVcb(int vcb) {
			this.vcb = vcb;
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}
	};

	// GPU memory
	private static short mem[] = null;

	static {
		// addr bus of GPU is A0-A11 => 12 Bit, 0...4095
		GPU.mem = new short[100000];
	}

	// hide constructor to outside world
	private GPU() {

	}

	// Eingabe-Port D
	public static short getInputD() {
		// TODO Auto-generated method stub
		return 0;
	}

	// is addr video space?
	public static boolean isAddrGpu(int addr) {
		return addr >= 0x1E00;
	}

	// is addr rom space?
	public static boolean isAddrRom(int addr) {
		return !GPU.isAddrGpu(addr);
	}

	// set memory with value
	public static void setMem(int addr, short val) {
		GPU.mem[addr] = val;
	}

	// return memory content
	public static short getMem(int addr) {
		return GPU.mem[addr];
	}

	// set byte
	public static void setByte(int addr, Short val) {
		// video chip checks if byte is from GPU or ROM
		if (GPU.isAddrGpu(addr)) {
			// set byte from GPU memory
			GPU.setMem(addr, val);
		} else if (GPU.isAddrRom(addr)) {
			// error on most ROMs cannot set anything
		} else {
			// should not happen
			// TODO error
		}
	}

	// get byte
	public static short getByte(int addr) {
		// video chip checks if byte is from GPU or ROM
		try {
			if (GPU.isAddrGpu(addr)) {
				// get byte from GPU memory
				return GPU.getMem(addr);
			} else if (GPU.isAddrRom(addr)) {
				// get byte from ROM memory
				return ROM.getByte(addr);
			} else {
				// should not happen
				// TODO error
				return 0x00;
			}
		} catch (RomAddrNotValidException ex) {
			// TODO error
			return 0x00;
		} catch (RomNotInitializedException ex) {
			// TODO error
			return 0x00;
		}
	}

	public static JPanel loop() {
		JPanel p = new JPanel();
		// Skalierung aktuell 3
		p.setPreferredSize(new Dimension(227 * SCALE * 2, 252 * SCALE));

		calcSprite1();
		calcSprite2();
		calcSprite3();
		calcSprite4();

		drawBackground(p);
		// drawGrid();
		drawSprite1(p);
		drawSprite2(p);
		drawSprite3(p);
		drawSprite4(p);
		drawScore(p);

		calcCollision();

		return p;
	}

	private static void calcCollision() {
		// calcCollisionObjectBackground();
		calcCollisionObject1Object2();
		calcCollisionObject1Object3();
		calcCollisionObject1Object4();
		calcCollisionObject2Object3();
		calcCollisionObject2Object4();
		calcCollisionObject3Object4();

		resetCollisionBits();
	}

	private static void resetCollisionBits() {
		// Leo
	}

	private static void calcCollisionObject2Object4() {
		// Tim
	}

	private static void calcCollisionObject3Object4() {
		// Jann
	}

	private static void calcCollisionObject2Object3() {
		// Semih
	}

	private static void calcCollisionObject1Object4() {
		// Soner

		Sprite s1 = sprite1;
		Sprite s4 = sprite4;

		// sind die koordinaten identisch oder liegen sie im bereich
		if ((s1.hc == s4.hc) || (s1.vc == s4.vc) || (s1.hc - s4.hc) < 8 || (s1.vc - s4.vc) < 10) {
			// nachschauen ob bits gesetzt sind (true sind)
			for (int x = 0; x < 10; x++) {
				for (int y = 0; y < 8; y++) {
					if (s1.shape[x][y]) {
						// in s1 ist das bit gesetzt im folgenden noch für s4 schauen

						// nachschauen ob bit in s4 gesetzt ist
						for (int i = 0; x < 10; x++) {
							for (int j = 0; y < 8; y++) {
								if (s4.shape[i][j]) {
									// in s4 gesetzt --> beide sprites treffen sich
									// TODO: Folge?
								}
							}
						}

					}
				}
			}
		}

	}


	private static void calcCollisionObject1Object3() {
		// Tiglat
	}

	private static void calcCollisionObject1Object2() {
		// Leo
	}

	private static void drawSprite4(JPanel p) {
		// Tim
	}

	private static void drawBackground(JPanel p) {
		// Jann
	}

	private static void drawGrid(JPanel p) {
		// TODO
	}

	private static void drawSprite1(JPanel p) {
		// Semih
	}


	private static void drawSprite2(JPanel p) {
		// Soner
		for(int x=0; x<10; x++) {
			for(int y=0;y<8;y++) {
				// wenn true, dann zeichen
				if(sprite2.shape[x][y]) {
					// TODO: zeichnen
				}
			}
		}
	}
		

	private static void drawScore(JPanel p) {
		// Tiglat
	}

	private static void drawSprite3(JPanel p) {
		// Leo
	}

	private static void calcSprite3() {
		// Tim
	}

	private static void calcSprite4() {
		// Jann
	}

	private static void calcSprite1() {
		// Semih
	}

	private static void calcSprite2() {
		// Soner

		int a;
		boolean[][] tmp = new boolean[10][8];

		// schleifen durchgehen und die jeweiligen bytes überprüfen und auf true/ false setzen
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 8; y++) {
				a = GPU.getByte(0x1F20);
				if (a == 1) {
					tmp[x][y] = true;
				} else {
					tmp[x][y] = false;
				}
			}
		}
		
		// setzten der jeweiligen bytes
		sprite2.setShape(tmp);
		sprite2.setHc(GPU.getByte(0x1F2A));
		sprite2.setHcb(GPU.getByte(0x1F2B));
		sprite2.setVc(GPU.getByte(0x1F2C));
		sprite2.setVcb(GPU.getByte(0x1F2D));

	}
	
	// get background color
	public static Color getBackgroundColor() {
		short byt = GPU.getMem(ADDR_BACKGROUND_COLOR);
		boolean background_on = ((byt >> 3) & 0x1) == 0x1; // bit 3 is 1?
		if (true == background_on) {
			short r = (short) (((byt >> 2) & 0x1) * 255); // bit 2 is red value of background color
			short g = (short) (((byt >> 1) & 0x1) * 255); // bit 1 is green value of background color
			short b = (short) (((byt >> 0) & 0x1) * 255); // bit 0 is blue value of background color

			return new Color(r, g, b); // return color with rgb values
		} else {
			// background is disabled => return black
			return Color.BLACK;
		}
	}

	// get left score as dez number
	public static short getLeftScoreDez() {
		short lscore = GPU.getMem(ADDR_LEFT_SCORE); // info: this is bcd value

		// convert to dez
		String s_lscore = String.format("%02X", lscore);
		// check of value contains A-F hex chars?
		if (s_lscore.matches("[A-F]")) {
			return -1; // don't show this number since it is not a valid dez number
		} else {
			return Short.parseShort(s_lscore, 10); // convert bcd to dez
		}
	}

	// get right score as dez number
	public static short getRightScoreDez() {
		short rscore = GPU.getMem(ADDR_RIGHT_SCORE); // info: this is bcd value

		// convert to dez
		String s_rscore = String.format("%02X", rscore);
		// check of value contains A-F hex chars?
		if (s_rscore.matches("[A-F]")) {
			return -1; // don't show this number since it is not a valid dez number
		} else {
			return Short.parseShort(s_rscore, 10); // convert bcd to dez
		}
	}

}
