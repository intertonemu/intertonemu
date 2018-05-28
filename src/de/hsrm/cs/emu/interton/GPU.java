package de.hsrm.cs.emu.interton;

import java.awt.Color;

import de.hsrm.cs.emu.interton.exception.RomAddrNotValidException;
import de.hsrm.cs.emu.interton.exception.RomNotInitializedException;

// encapsulates the GPU memory and functionalities
public class GPU {
	
	public static final int ADDR_BACKGROUND_COLOR = 0x1FC6;
	public static final int ADDR_LEFT_SCORE = 0x1FC8;
	public static final int ADDR_RIGHT_SCORE = 0x1FC9;

	// GPU memory
	private static short mem[] = null;
	
	static {
		// addr bus of GPU is A0-A11 => 12 Bit, 0...4095
		GPU.mem = new short[4096];
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
		//TODO the GPU will tell the ROM that the Addr is meant for him
		// so we need to add a logic here
		return false;
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
