package de.hsrm.cs.emu.interton;

// this will implement the clock. We need to find a way that this class will wait for the next clock of the target platform
public class Clock {

	// wait until next cycle should be started
	public static void waitForNextCycle() {
		long elapsed;
		final long startTime = System.nanoTime();
		do {
			elapsed = System.nanoTime() - startTime;
		} while (elapsed < 1127);
			return;
	}
}
