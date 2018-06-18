package de.hsrm.cs.emu.interton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;

import de.hsrm.cs.emu.interton.exception.RomAddrNotValidException;
import de.hsrm.cs.emu.interton.exception.RomNotInitializedException;
import de.hsrm.cs.emu.interton.gui.MainFrame;

// encapsulates the GPU memory and functionalities
public class GPU {

	public static final int ADDR_BACKGROUND_COLOR = 0x1FC6;
	public static final int ADDR_LEFT_SCORE = 0x1FC8;
	public static final int ADDR_RIGHT_SCORE = 0x1FC9;
	public static final int SCALE = 3;
	public static JPanel panel = null;

	private static Sprite sprite1 = new Sprite(SCALE);
	private static Sprite sprite2 = new Sprite(SCALE);
	private static Sprite sprite3 = new Sprite(SCALE);
	private static Sprite sprite4 = new Sprite(SCALE);

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
	
	public static void init() {
		panel.removeAll();
		// Skalierung aktuell 3
		panel.setPreferredSize(new Dimension(227 * SCALE * 2, 252 * SCALE));
		
		panel.add(sprite1);
		panel.add(sprite2);
		panel.add(sprite3);
		panel.add(sprite4);
		
		MainFrame.getInstance().getContentPane().add(panel);
		
	}

	public static JPanel loop() {
		calcSprite1();
		calcSprite2();
		calcSprite3();
		calcSprite4();

		//drawBackground(panel);
		// drawGrid();
//		drawSprite1(panel);
//		drawSprite2(panel);
//		drawSprite3(panel);
//		drawSprite4(panel);
		drawScore(panel);
		
		panel.repaint();

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
		Sprite a = sprite2;
		boolean[][] ashape = a.getShape();
		Sprite b = sprite4;
		boolean[][] bshape = b.getShape();

		if(Math.abs(a.getHc()-b.getHc())<8&&Math.abs(a.getVc()-b.getVc())<10){
			for(int ax = 0; ax <ashape.length;ax++){
				for(int ay = 0; ay < ashape[0].length;ay++){
					if(ashape[ax][ay]){
						for(int bx = 0; bx < bshape.length;bx++){
							for(int by = 0; by < bshape[0].length;by++){
								if(bshape[bx][by]){
									int posxa = ax*SCALE*a.getSizeFactor()*2+a.getHc();
									int posya = ay*SCALE*a.getSizeFactor()+a.getVc();
									int posxb = bx*SCALE*b.getSizeFactor()*2+b.getHc();
									int posyb = by*SCALE*b.getSizeFactor()+b.getVc();
									if(Math.abs(posxa-posxb)<=2*SCALE&&Math.abs(posya-posyb)<=SCALE){
										short colbyte = GPU.getByte(0x1FCA);
										GPU.setByte(0x1FCA, (short)(colbyte|(0x1 << 1)));
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
		GPU.setByte(0x1FCA, (short)(colbyte&~(0x1 << 1)));
	}

	private static void calcCollisionObject3Object4() {
		Sprite a = sprite3;
		boolean[][] ashape = a.getShape();
		Sprite b = sprite4;
		boolean[][] bshape = b.getShape();

		if(Math.abs(a.getHc()-b.getHc())<8&&Math.abs(a.getVc()-b.getVc())<10){
			for(int ax = 0; ax <ashape.length;ax++){
				for(int ay = 0; ay < ashape[0].length;ay++){
					if(ashape[ax][ay]){
						for(int bx = 0; bx < bshape.length;bx++){
							for(int by = 0; by < bshape[0].length;by++){
								if(bshape[bx][by]){
									int posxa = ax*SCALE*a.getSizeFactor()*2+a.getHc();
									int posya = ay*SCALE*a.getSizeFactor()+a.getVc();
									int posxb = bx*SCALE*b.getSizeFactor()*2+b.getHc();
									int posyb = by*SCALE*b.getSizeFactor()+b.getVc();
									if(Math.abs(posxa-posxb)<=2*SCALE&&Math.abs(posya-posyb)<=SCALE){
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
		Sprite a = sprite2;
		boolean[][] ashape = a.getShape();
		Sprite b = sprite3;
		boolean[][] bshape = b.getShape();

		if(Math.abs(a.getHc()-b.getHc())<8&&Math.abs(a.getVc()-b.getVc())<10){
			for(int ax = 0; ax <ashape.length;ax++){
				for(int ay = 0; ay < ashape[0].length;ay++){
					if(ashape[ax][ay]){
						for(int bx = 0; bx < bshape.length;bx++){
							for(int by = 0; by < bshape[0].length;by++){
								if(bshape[bx][by]){
									int posxa = ax*SCALE*a.getSizeFactor()*2+a.getHc();
									int posya = ay*SCALE*a.getSizeFactor()+a.getVc();
									int posxb = bx*SCALE*b.getSizeFactor()*2+b.getHc();
									int posyb = by*SCALE*b.getSizeFactor()+b.getVc();
									if(Math.abs(posxa-posxb)<=2*SCALE&&Math.abs(posya-posyb)<=SCALE){
										short colbyte = GPU.getByte(0x1FCA);
										GPU.setByte(0x1FCA, (short)(colbyte|(0x1 << 2)));
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
		GPU.setByte(0x1FCA, (short)(colbyte&~(0x1 << 2)));
	}

	private static void calcCollisionObject1Object4() {
		Sprite a = sprite1;
		boolean[][] ashape = a.getShape();
		Sprite b = sprite4;
		boolean[][] bshape = b.getShape();

		if(Math.abs(a.getHc()-b.getHc())<8&&Math.abs(a.getVc()-b.getVc())<10){
			for(int ax = 0; ax <ashape.length;ax++){
				for(int ay = 0; ay < ashape[0].length;ay++){
					if(ashape[ax][ay]){
						for(int bx = 0; bx < bshape.length;bx++){
							for(int by = 0; by < bshape[0].length;by++){
								if(bshape[bx][by]){
									int posxa = ax*SCALE*a.getSizeFactor()*2+a.getHc();
									int posya = ay*SCALE*a.getSizeFactor()+a.getVc();
									int posxb = bx*SCALE*b.getSizeFactor()*2+b.getHc();
									int posyb = by*SCALE*b.getSizeFactor()+b.getVc();
									if(Math.abs(posxa-posxb)<=2*SCALE&&Math.abs(posya-posyb)<=SCALE){
										short colbyte = GPU.getByte(0x1FCA);
										GPU.setByte(0x1FCA, (short)(colbyte|(0x1 << 3)));
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
		GPU.setByte(0x1FCA, (short)(colbyte&~(0x1 << 3)));
	}

	private static void calcCollisionObject1Object3() {
		Sprite a = sprite1;
		boolean[][] ashape = a.getShape();
		Sprite b = sprite3;
		boolean[][] bshape = b.getShape();

		if(Math.abs(a.getHc()-b.getHc())<8&&Math.abs(a.getVc()-b.getVc())<10){
			for(int ax = 0; ax <ashape.length;ax++){
				for(int ay = 0; ay < ashape[0].length;ay++){
					if(ashape[ax][ay]){
						for(int bx = 0; bx < bshape.length;bx++){
							for(int by = 0; by < bshape[0].length;by++){
								if(bshape[bx][by]){
									int posxa = ax*SCALE*a.getSizeFactor()*2+a.getHc();
									int posya = ay*SCALE*a.getSizeFactor()+a.getVc();
									int posxb = bx*SCALE*b.getSizeFactor()*2+b.getHc();
									int posyb = by*SCALE*b.getSizeFactor()+b.getVc();
									if(Math.abs(posxa-posxb)<=2*SCALE&&Math.abs(posya-posyb)<=SCALE){
										short colbyte = GPU.getByte(0x1FCA);
										GPU.setByte(0x1FCA, (short)(colbyte|(0x1 << 4)));
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
		GPU.setByte(0x1FCA, (short)(colbyte&~(0x1 << 4)));
	}

	private static void calcCollisionObject1Object2() {
		Sprite a = sprite1;
		boolean[][] ashape = a.getShape();
		Sprite b = sprite2;
		boolean[][] bshape = b.getShape();

		if(Math.abs(a.getHc()-b.getHc())<8&&Math.abs(a.getVc()-b.getVc())<10){
			for(int ax = 0; ax <ashape.length;ax++){
				for(int ay = 0; ay < ashape[0].length;ay++){
					if(ashape[ax][ay]){
						for(int bx = 0; bx < bshape.length;bx++){
							for(int by = 0; by < bshape[0].length;by++){
								if(bshape[bx][by]){
									int posxa = ax*SCALE*a.getSizeFactor()*2+a.getHc();
									int posya = ay*SCALE*a.getSizeFactor()+a.getVc();
									int posxb = bx*SCALE*b.getSizeFactor()*2+b.getHc();
									int posyb = by*SCALE*b.getSizeFactor()+b.getVc();
									if(Math.abs(posxa-posxb)<=2*SCALE&&Math.abs(posya-posyb)<=SCALE){
										short colbyte = GPU.getByte(0x1FCA);
										GPU.setByte(0x1FCA, (short)(colbyte|(0x1 << 5)));
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
		GPU.setByte(0x1FCA, (short)(colbyte&~(0x1 << 5)));
	}

	private static void drawBackground(JPanel p) {
		p.setBackground(GPU.getBackgroundColor());
	}

	private static void drawGrid(JPanel p) {
		// TODO
	}

	private static void drawScore(JPanel p) {
		// Tiglat
	}

	// 0xF200 bis 0xF20D
	// Tim
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
		if(GPU.getByte(0x1F49)>0) {
			//debug
			System.out.println("Debug");
		}
		
		boolean shape[][] = new boolean[10][8];
		ByteBuffer buffer = ByteBuffer.allocate(8);
		for (int i = 0; i < shape.length; i++) {
			short s = GPU.getByte(0x1F40 + i);
			if(s > 0) {
				System.out.println("TODO");
			}
//			buffer.putShort(s);
//			buffer.position(0);
			byte line = (byte)s;
			for (int j = 0; j < shape[i].length; j++) {
				shape[i][j] = (line & 0x1) == 0x1;
				line >>>= line;
			}
		}
		sprite4.setShape(shape);

		sprite4.setHc(GPU.getByte(0x1F4A));
		sprite4.setHcb(GPU.getByte(0x1F4B));
		sprite4.setVc(GPU.getByte(0x1F4C));
		sprite4.setVcb(GPU.getByte(0x1F4D));

		byte rgb = (byte) ((GPU.getByte(0x1FC2)) & 0x7);
		sprite4.setColor(
				new Color((rgb & 0x4) == 0x4 ? 255 : 0, (rgb & 0x2) == 0x2 ? 255 : 0, (rgb & 0x1) == 0x1 ? 255 : 0));

	}

	private static void calcSprite1() {
		boolean shape[][] = new boolean[10][8];
		ByteBuffer buffer = ByteBuffer.allocate(8);
		for (int i = 0; i < shape.length; i++) {
			buffer.putShort(GPU.getByte(0x1F00 + i));
			buffer.position(0);
			byte line = buffer.get();
			for (int j = 0; j < shape[i].length; j++) {
				shape[i][j] = (line & 0x1) == 0x1;
				line >>>= line;
			}
		}
		sprite1.setShape(shape);

		sprite1.setHc(GPU.getByte(0x1F0A));
		sprite1.setHcb(GPU.getByte(0x1F0B));
		sprite1.setVc(GPU.getByte(0x1F0C));
		sprite1.setVcb(GPU.getByte(0x1F0D));

		byte rgb = (byte) ((GPU.getByte(0x1FC1) >>> 3) & 0x7);
		sprite1.setColor(
				new Color((rgb & 0x4) == 0x4 ? 255 : 0, (rgb & 0x2) == 0x2 ? 255 : 0, (rgb & 0x1) == 0x1 ? 255 : 0));

	}

	private static void calcSprite2() {
		boolean shape[][] = new boolean[10][8];
		ByteBuffer buffer = ByteBuffer.allocate(8);
		for (int i = 0; i < shape.length; i++) {
			buffer.putShort(GPU.getByte(0x1F10 + i));
			buffer.position(0);
			byte line = buffer.get();
			for (int j = 0; j < shape[i].length; j++) {
				shape[i][j] = (line & 0x1) == 0x1;
				line >>>= line;
			}
		}
		sprite2.setShape(shape);

		sprite2.setHc(GPU.getByte(0x1F1A));
		sprite2.setHcb(GPU.getByte(0x1F1B));
		sprite2.setVc(GPU.getByte(0x1F1C));
		sprite2.setVcb(GPU.getByte(0x1F1D));

		byte rgb = (byte) ((GPU.getByte(0x1FC1)) & 0x7);
		sprite2.setColor(
				new Color((rgb & 0x4) == 0x4 ? 255 : 0, (rgb & 0x2) == 0x2 ? 255 : 0, (rgb & 0x1) == 0x1 ? 255 : 0));

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
