package de.hsrm.cs.emu.interton;

import java.awt.Color;

import java.awt.Dimension;

import java.awt.*;

import javax.swing.JPanel;

import de.hsrm.cs.emu.interton.exception.RomAddrNotValidException;

import de.hsrm.cs.emu.interton.exception.RomNotInitializedException;

// encapsulates the GPU memory and functionalities

public class GPU extends JPanel {

	/**
	
	* 
	
	*/

	private static final long serialVersionUID = 1L;

	public static final int ADDR_BACKGROUND_COLOR = 0x1FC6;

	public static final int ADDR_LEFT_SCORE = 0x1FC8;

	public static final int ADDR_RIGHT_SCORE = 0x1FC9;

	public static final int SCALE = 3;

	private static Sprite sprite1 = null;

	private static Sprite sprite2 = null;

	private static Sprite sprite3 = null;

	private static Sprite sprite4 = null;

	private static Sprite spriteScoreLeft = null;

	private static Sprite spriteScoreRight = null;

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

		}

		else if (GPU.isAddrRom(addr)) {

			// error on most ROMs cannot set anything

		}

		else {

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

			}

			else if (GPU.isAddrRom(addr)) {

				// get byte from ROM memory

				return ROM.getByte(addr);

			}

			else {

				// should not happen

				// TODO error

				return 0x00;

			}

		}

		catch (RomAddrNotValidException ex) {

			// TODO error

			return 0x00;

		}

		catch (RomNotInitializedException ex) {

			// TODO error

			return 0x00;

		}

	}

	public static void paint(Graphics g, int xc, int yc) {

		// super.paint(g);

		// draw a line (there is no drawPoint..)

		g.drawLine(xc, yc, xc, yc);

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

		if (!(sprite2.getVc() + 8 < sprite3.getVc() || sprite3.getVc() + 8 < sprite2.getVc()
				|| sprite2.getHc() + 10 < sprite3.getHc() || sprite3.getHc() + 10 < sprite2.getHc())) {

			for (int i = 0; i < 10; i++) {

				for (int j = 0; j < 8; j++) {

					if (sprite2.shape[i][j] != false) {

						checkBitsCollision(sprite2, sprite3, i, j);

					} else {

						continue;

					}

				}

			}

		}

	}

	private static void calcCollisionObject1Object4() {

		// Soner

	}

	// Hilfsfunktion zum prüfen, ob gesetzte Felder sich treffen

	private static boolean checkBitsCollision(Sprite a, Sprite b, int x, int y) {

		//dv = -9 ; dh = -7
		int dv = a.getVc() - b.getVc();
		int dh = a.getHc() - b.getHc();
		if (Math.abs(dh) + x <= 7 && Math.abs(dv) + y <= 9) {
			if (b.shape[Math.abs(dh) + x][Math.abs(dv) + y] == true) {

				return true;

			} else {

				return false;
			}
		}
		return false;
	}

	private static void calcCollisionObject1Object3() {
		
		//Sprite1 vc = 100 ; Sprite1 hc = 100; Sprite3 vc = 109 ; sprite3 hc = 107

		if (!(sprite1.getVc() + 10 < sprite3.getVc() || sprite3.getVc() + 10 < sprite1.getVc()
				|| sprite1.getHc() + 8 < sprite3.getHc() || sprite3.getHc() + 8 < sprite1.getHc())) {

			
			for (int i = 0; i < 10; i++) {

				for (int j = 0; j < 8; j++) {

					if (sprite1.shape[i][j] != false) {
						//i = 6, j = 8
						checkBitsCollision(sprite1, sprite3, i, j);

					} else {

						continue;

					}

				}

			}

		}

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

		for (int i = 0; i < 10; i++) {

			for (int j = 0; j < 8; j++) {

				if (sprite1.shape[i][j] == true) {

					paint(sprite1.getHc() + i, sprite1.getVc() + j, sprite1.getHc() + i, sprite1.getVc() + j);

				} else {

					continue;

				}

			}

		}

	}

	private static void drawSprite2(JPanel p) {

	}

	public static void drawZero(Sprite p, short x, int pos) {

		if (pos == 0) {

			for (int i = 0; i < 3; i++) {

				p.shape[2][i] = true;

				p.shape[7][i] = true;

			}

			for (int i = 2; i < 8; i++) {

				p.shape[i][0] = true;

				p.shape[i][2] = true;

			}

		} else {

			for (int i = 4; i < 7; i++) {

				p.shape[2][i] = true;

				p.shape[7][i] = true;

			}

			for (int i = 2; i < 8; i++) {

				p.shape[i][4] = true;

				p.shape[i][6] = true;

			}

		}

	}

	private static void drawScore(JPanel p) {

		short leftScore = getLeftScoreDez();

		short rightScore = getRightScoreDez();

		// switch(leftScore){

		//

		// case 0: spriteScoreLeft.shape[][]

		// }

		//

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

		boolean[][] result = new boolean[10][8];

		for (int i = 0; i < result.length; i++) {

			byte zeile = (byte) GPU.getByte(0x1F00 + i);

			for (int j = 0; j < 8; j++) {

				if ((zeile & 0x80) == 1) {

					result[i][j] = true;

				}

				else {

					result[i][j] = false;

				}

				zeile = (byte) (zeile << 1);

			}

		}

		sprite1.setShape(result);

		sprite1.setHc(GPU.getByte(0x1F0A));

		sprite1.setHcb(GPU.getByte(0x1F0B));

		sprite1.setVc(GPU.getByte(0x1F0C));

		sprite1.setVcb(GPU.getByte(0x1F0D));

		sprite1.setSize(GPU.getByte(0x1FC0 & 0x03));

		int color_ = GPU.getByte(0x1FC1 & 0x38);

		switch (color_) {

		case 0:

			sprite1.setColor(Color.WHITE);

			break;

		case 1:

			sprite1.setColor(Color.YELLOW);

			break;

		case 2:

			sprite1.setColor(Color.MAGENTA);

			break;

		case 3:

			sprite1.setColor(Color.RED);

		case 4:

			sprite1.setColor(Color.CYAN);

			break;

		case 5:

			sprite1.setColor(Color.GREEN);

			break;

		case 6:

			sprite1.setColor(Color.BLUE);

			break;

		case 7:

			sprite1.setColor(Color.BLACK);

			break;

		}

	}

	private static void calcSprite2() {

		// Soner

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

		}

		else {

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

		}

		else {

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

		}

		else {

			return Short.parseShort(s_rscore, 10); // convert bcd to dez

		}

	}

}
