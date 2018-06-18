package de.hsrm.cs.emu.interton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.nio.ByteBuffer;

import javax.swing.JPanel;

import de.hsrm.cs.emu.interton.exception.RomAddrNotValidException;
import de.hsrm.cs.emu.interton.exception.RomNotInitializedException;
import de.hsrm.cs.emu.interton.gui.Sprite;

// encapsulates the GPU memory and functionalities
public class GPU {

	public static final int ADDR_BACKGROUND_COLOR = 0x1FC6;
	public static final int ADDR_LEFT_SCORE = 0x1FC8;
	public static final int ADDR_RIGHT_SCORE = 0x1FC9;
	public static final int SCALE = 3;
	public static JPanel panel = null;

	private static Sprite sprite1 = null;
	private static Sprite sprite2 = null;
	public static Sprite sprite3 = new Sprite();
	private static Sprite sprite4 = null;

	// GPU memory
	private static short mem[] = null;

	static {
		// addr bus of GPU is A0-A11 => 12 Bit, 0...4095
		GPU.mem = new short[100000];
		panel = new JPanel();
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
		panel.removeAll();
		// Skalierung aktuell 3
		panel.setPreferredSize(new Dimension(227 * SCALE * 2, 252 * SCALE));

		calcSprite1();
		calcSprite2();
		calcSprite3();
		calcSprite4();

		drawBackground(panel);
		// drawGrid();
		drawSprite1(panel);
		drawSprite2(panel);
		drawSprite3(panel);
		drawSprite4(panel);
		drawScore(panel);

		calcCollision();

		return panel;
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
		boolean doBoxesIntersect = !(sprite4.getHc() > sprite2.getHc() + sprite2.width
				|| sprite4.getHc() + sprite4.width < sprite2.getHc()
				|| sprite4.getVc() > sprite2.getVc() + sprite2.height
				|| sprite4.getVc() + sprite4.height < sprite2.getVc());

		if (doBoxesIntersect) {
			// TODO: 'pixel' /block-check

			int leftX = Math.max(sprite2.getHc(), sprite4.getHc());
			int rightX = Math.min(sprite2.getHc() + sprite2.width, sprite4.getHc() + sprite4.width);
			int topY = Math.max(sprite2.getVc(), sprite4.getVc());
			int bottomY = Math.min(sprite2.getVc() + sprite2.height, sprite4.getVc() + sprite2.height);

			new Rectangle(leftX, topY, rightX - leftX, bottomY - topY);

			for (int i = 0; i < (bottomY - topY); i++) {
				int sp2VC = topY - sprite2.getVc() + i;
				int sp4VC = topY - sprite4.getVc() + i;
				for (int j = 0; j < (rightX - leftX); j++) {
					int sp2HC = leftX - sprite2.getHc() + i;
					int sp4HC = leftX - sprite4.getHc() + i;

					if (sprite2.getShape()[sp2VC][sp2HC] && sprite4.getShape()[sp4VC][sp4HC]) {
						// TODO: set collision byte

					}

				}

			}

		}
	}

	private static void calcCollisionObject3Object4() {
		Sprite a = sprite3;
		Sprite b = sprite4;
		int ax, ay, bx, by;
		// TODO Jann
	}

	private static void calcCollisionObject2Object3() {
		// Semih
	}

	private static void calcCollisionObject1Object4() {
		// Soner
	}

	private static void calcCollisionObject1Object3() {
		// Tiglat
	}

	private static void calcCollisionObject1Object2() {
		// Leo
	}

	private static void drawSprite4(JPanel p) {

		Graphics2D g2 = (Graphics2D) p.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(sprite4.getColor());

		// Shape ssprite4 = new Rectangle(8 * 2 * SCALE, 10 * SCALE);
		// Area a = new Area(ssprite4);
		// g2.draw(a);

		for (int i = 0; i < sprite4.getShape().length; i++) {
			for (int j = 0; j < sprite4.getShape()[i].length; j++) {
				if (sprite4.getShape()[i][j])
					g2.drawRect(sprite4.getVc() + j, sprite4.getHc() + i, 2, 1); // TODO SCALE
			}
		}
		p.repaint();
	}

	private static void drawBackground(JPanel p) {
		p.setBackground(GPU.getBackgroundColor());
	}

	private static void drawGrid(JPanel p) {
		// TODO
	}

	private static void drawSprite1(JPanel p) {
		// Semih
	}

	private static void drawSprite2(JPanel p) {
		// Soner
	}

	private static void drawScore(JPanel p) {
		// Tiglat
	}

	private static void drawSprite3(JPanel p) {
		// Leo
	}

	// 0xF200 bis 0xF20D
	// Tim Herold
	public static void calcSprite3() {
		boolean shape[][] = new boolean[10][8];
		ByteBuffer buffer = ByteBuffer.allocate(8);
		for (int i = 0; i < shape.length; i++) {
			buffer.putShort(GPU.getByte(0x1F20 + i));
			buffer.position(0);
			byte line = buffer.get();
			for (int j = 0; j < shape[i].length; j++) {
				shape[i][j] = (line & 0x1) == 0x1;
				line >>>= line;
			}
		}
		sprite3.setShape(shape);

		sprite3.setHc(GPU.getByte(0x1F2A));
		sprite3.setHcb(GPU.getByte(0x1F2B));
		sprite3.setVc(GPU.getByte(0x1F2C));
		sprite3.setVcb(GPU.getByte(0x1F2D));

		byte rgb = (byte) ((GPU.getByte(0x1FC2) >>> 3) & 0x7);
		sprite3.setColor(
				new Color((rgb & 0x4) == 0x4 ? 255 : 0, (rgb & 0x2) == 0x2 ? 255 : 0, (rgb & 0x1) == 0x1 ? 255 : 0));

	}

	private static void calcSprite4() {
		// Jann
	}

	private static void calcSprite1() {
		// Semih
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
