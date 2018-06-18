package de.hsrm.cs.emu.interton.gui;

import java.awt.Color;

public class Sprite {
	private boolean[][] shape = null; // byte 0 - 9
	private int hc = 0;
	private int hcb = 0;
	private int vc = 0;
	private int vcb = 0; // offset
	private int size = 1; // 1, 2, 4 or 8
	private Color color = null;
	
	public static final int width = 8;
	public static final int height = 10;

	public Sprite() {

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
