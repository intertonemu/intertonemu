package de.hsrm.cs.emu.interton.gui;

import java.awt.Color;

public class Sprite {
	private boolean[][] shape = null; // byte 0 - 9
	public int hc = 0;
	public int hcb = 0;
	public int vc = 0;
	public int vcb = 0; // offset
	public int size = 1; // 1, 2, 4 or 8
	public Color color = null;

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