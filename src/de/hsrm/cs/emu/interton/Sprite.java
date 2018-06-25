package de.hsrm.cs.emu.interton;

import java.awt.Color;
import java.awt.Graphics;

public class Sprite {
	private boolean[][] shape = new boolean[0][0]; // byte 0 - 9
	private int hc = 0;
	private int hcb = 0;
	private int vc = 0;
	private int vcb = 0; // offset
	private int sizeFactor = 1; // 1, 2, 4 or 8
	private Color color = null;
	private int SCALE = 1;

	public Sprite(int SCALE) {
		this.SCALE = SCALE;
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

	public int getSizeFactor() {
		return sizeFactor;
	}

	public void setSizeFactor(int sizeFactor) {
		this.sizeFactor = sizeFactor;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void paintComponent(Graphics g) {

		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(this.getColor());

		//g.fillRect(10, 10, 100, 100);

		// Shape ssprite4 = new Rectangle(8 * 2 * SCALE, 10 * SCALE);
		// Area a = new Area(ssprite4);
		// g2.draw(a);

		for (int i = 0; i < this.getShape().length; i++) {
			for (int j = 0; j < this.getShape()[i].length; j++) {
				if (this.getShape()[i][j])
					g.fillRect((this.getHc() + j * 2) * SCALE * this.getSizeFactor(),
							(this.getVc() + i) * SCALE * this.getSizeFactor(), 2 * SCALE * this.getSizeFactor(),
							1 * SCALE * this.getSizeFactor());
			}
		}
	}

};