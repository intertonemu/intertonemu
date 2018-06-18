package de.hsrm.cs.emu.interton;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.JPanel;

import de.hsrm.cs.emu.interton.exception.RomAddrNotValidException;
import de.hsrm.cs.emu.interton.exception.RomNotInitializedException;

// encapsulates the GPU memory and functionalities
public class GPU {
	
	public static final int ADDR_BACKGROUND_COLOR = 0x1FC6;
	public static final int ADDR_LEFT_SCORE = 0x1FC8;
	public static final int ADDR_RIGHT_SCORE = 0x1FC9;
	public static final int SCALE = 3;
	public static JPanel panel = null;
	
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
		panel = new JPanel();
	}
	
	// hide constructor to outside world
	private GPU() {
		
	}
	
	//Eingabe-Port D
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
		if(GPU.isAddrGpu(addr)) {
			// set byte from GPU memory
			GPU.setMem(addr, val);
		}
		else if(GPU.isAddrRom(addr)) {
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
			if(GPU.isAddrGpu(addr)) {
				// get byte from GPU memory
				return GPU.getMem(addr);
			}
			else if(GPU.isAddrRom(addr)) {
				// get byte from ROM memory
				return ROM.getByte(addr);
			}
			else {
				// should not happen
				// TODO error
				return 0x00;
			}
		}
		catch(RomAddrNotValidException ex) {
			// TODO error
			return 0x00;
		}
		catch(RomNotInitializedException ex) {
			// TODO error
			return 0x00;
		}
	}
	
	public static JPanel loop() {
		panel.removeAll();
		// Skalierung aktuell 3
		panel.setPreferredSize(new Dimension(227*SCALE*2,252*SCALE));
		
		calcSprite1();
		calcSprite2();
		calcSprite3();
		calcSprite4();
		
		drawBackground(panel);
		//drawGrid();
		drawSprite1(panel);
		drawSprite2(panel);
		drawSprite3(panel);
		drawSprite4(panel);
		drawScore(panel);
		
		calcCollision();
		
		return panel;
	}
	
	private static void calcCollision() {
		//calcCollisionObjectBackground();
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
		Sprite a = sprite3;
		boolean[][] ashape = a.getShape();
		Sprite b = sprite4;
		boolean[][] bshape = b.getShape();

		if(Math.abs(a.hc-b.hc)<10&&Math.abs(a.vc-b.vc)<8){
			for(int ax = 0; ax <ashape.length;ax++){
				for(int ay = 0; ay < ashape[0].length;ay++){
					if(ashape[ax][ay]){
						for(int bx = 0; bx < bshape.length;bx++){
							for(int by = 0; by < bshape[0].length;by++){
								if(bshape[bx][by]){
									int posxa = ax*SCALE*a.size*2+a.hc;
									int posya = ay*SCALE*a.size+a.vc;
									int posxb = bx*SCALE*b.size*2+b.hc;
									int posyb = by*SCALE*b.size+b.vc;
									if(Math.abs(posxa-posya)<=2*SCALE&&Math.abs(posxb-posyb)<=SCALE){
										short colbyte = GPU.getByte(0x1FCA);
										GPU.setByte(0x1FCA, (short)(colbyte|0x1));
										return;
									}
								}
							}
						}
					}
				}
			}
		}
		short colbyte = GPU.getByte(0x1FCA);
		GPU.setByte(0x1FCA, (short)(colbyte&~0x1));
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
		// Tim	
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
	}
	
	// get background color
	public static Color getBackgroundColor() {
		short byt = GPU.getMem(ADDR_BACKGROUND_COLOR);
		boolean background_on = ((byt >> 3) & 0x1) == 0x1; // bit 3 is 1?
		if(true==background_on) {
			short r = (short) (((byt >> 2) & 0x1)*255); // bit 2 is red value of background color
		    short g = (short) (((byt >> 1) & 0x1)*255); // bit 1 is green value of background color
		    short b = (short) (((byt >> 0) & 0x1)*255); // bit 0 is blue value of background color
		    
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
		if(s_lscore.matches("[A-F]")) {
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
		if(s_rscore.matches("[A-F]")) {
			return -1; // don't show this number since it is not a valid dez number
		}
		else {
			return Short.parseShort(s_rscore, 10); // convert bcd to dez
		}
	}
	
}
