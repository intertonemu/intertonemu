package de.hsrm.cs.emu.interton;

// this will implement the clock. We need to find a way that this class will wait for the next clock of the target platform
public class Clock {

	// wait until next cycle should be started
	public static void waitForNextCycle() {
		//TODO find a good way to get the 0,887 MHz of the Interton VC4000
		// or better find a good way to get the same speed because we will have some overhead
		// during simulation
		
		//info: nanoTime only available in Java 9
		return;
	}
}
