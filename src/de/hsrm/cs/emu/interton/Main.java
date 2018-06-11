package de.hsrm.cs.emu.interton;

import de.hsrm.cs.emu.interton.exception.CpuInvalidLengthException;
import de.hsrm.cs.emu.interton.exception.CpuOpcodeInvalidException;
import de.hsrm.cs.emu.interton.gui.MainFrame;

public class Main {

	public static void main(String[] args) {
		MainFrame.getInstance();
		try {
			CPU.reset();
			CPU.start();
		}
		catch(CpuOpcodeInvalidException ex) {
			ex.printStackTrace();
		}
		catch(CpuInvalidLengthException ex) {
			ex.printStackTrace();
		}
	}
}
