package de.hsrm.cs.emu.interton.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.hsrm.cs.emu.interton.CPU;
import de.hsrm.cs.emu.interton.exception.CpuInvalidLengthException;
import de.hsrm.cs.emu.interton.exception.CpuOpcodeInvalidException;

// JUnit tests for the CPU
public class CPUTests {

	@Test
	public void testReset() {
		CPU.reset();
		assertEquals(0, CPU.getPC());
	}
	
	@Test
	public void testOpcode0xCC() {
		
	}
	
	@Test 
	// NOP
	public void testOpcode0xC0() {
		int pc_old = CPU.getPC();
		CPU.process1((short)0xC0);
		int pc_new = CPU.getPC();
		
		assertEquals(pc_old+1, pc_new);
	}
	
	@Test
	// STRZ r1
	public void testOpcode0xC1() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// r1 = r0
		CPU.process1((short)0xC1);
		assertEquals(CPU.getR0(), CPU.getR1());
	}
	
	@Test
	// STRZ r2
	public void testOpcode0xC2() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// r2 = r0
		CPU.process1((short)0xC2);
		assertEquals(CPU.getR0(), CPU.getR2());
	}
	
	@Test
	// STRZ r3
	public void testOpcode0xC3() throws CpuOpcodeInvalidException, CpuInvalidLengthException {
		// r3 = r0
		CPU.process1((short)0xC3);
		assertEquals(CPU.getR0(), CPU.getR3());
	}
	
	@Test
	// I=0 => direct
	// IST=0 => non indexed
	public void testOpcode0xCDI0IST0() {
		CPU.process3((short)0xCD, (short)0x1F, (short)0x0E);
	}
	
	@Test
	// STRA,r1
	// I=0 => direct
	// IST=1 => indexed increment
	public void testOpcode0xCDI0IST1() {
		short r1_old = CPU.getR1();
		CPU.process3((short)0xCD, (short)0x3F, (short)0x0E);
		short r1_new = CPU.getR1();
		
		assertEquals(r1_old+1, r1_new);
	}
	
	@Test
	// I=0 => direct
	// IST=2 => indexed decrement
	public void testOpcode0xCDI0IST2() {
		short r1_old = CPU.getR1();
		CPU.process3((short)0xCD, (short)0x5F, (short)0x0E);
		short r1_new = CPU.getR1();
		
		assertEquals(r1_old-1, r1_new);
	}
	
	@Test
	// I=0 => direct
	// IST=3 => just indexed
	public void testOpcode0xCDI0IST3() {
		short r1_old = CPU.getR1();
		CPU.process3((short)0xCD, (short)0x7F, (short)0x0E);
		short r1_new = CPU.getR1();
		
		assertEquals(r1_old, r1_new);
	}
	
	
	
	//halt
	@Test
	public void testOpcode0x40() {
		int pc_old = CPU.getPC();
		CPU.process1((short)0x40);
		int pc_new = CPU.getPC();
		
		assertEquals(pc_old+1, pc_new);
		
	}
	//r0 = r0 & r1;
	public void testOpcode0x41() {
		short r1_old = CPU.getR1();
		short r0_old = (short) (CPU.getR0() & r1_old) ;
		
		CPU.process1((short)0x41);
		short r0_new = CPU.getR0();
		short r1_new = CPU.getR1();
		
		assertEquals(r1_old, r1_new);
		assertEquals(r0_old, r0_new);
		
	}
	
	public void testOpcode0x42() {
		short r2_old = CPU.getR2();
		short r0_old = (short) (CPU.getR0() & r2_old) ;
		
		CPU.process1((short)0x42);
		short r0_new = CPU.getR0();
		short r2_new = CPU.getR2();
		
		assertEquals(r2_old, r2_new);
		assertEquals(r0_old, r0_new);
		
	}
	
	public void testOpcode0x43() {
		short r3_old = CPU.getR3();
		short r0_old = (short) (CPU.getR0() & r3_old) ;
		
		CPU.process1((short)0x43);
		short r0_new = CPU.getR0();
		short r3_new = CPU.getR3();
		
		assertEquals(r3_old, r3_new);
		assertEquals(r0_old, r0_new);
		
	}
	
	public void testOpcode0x44() {
		
		short param1 = 0x03;
		short r0_old = (short) (CPU.getR0() & param1);
		CPU.process2((short)0x44, param1);
		short r0_new = CPU.getR0();
		
		assertEquals(r0_old,r0_new);
		
		
		
		
	}
	
	public void testOpcode0x45() {
		
		short param1 = 0x03;
		short r1_old = (short) (CPU.getR1() & param1);
		CPU.process2((short)0x45, param1);
		short r1_new = CPU.getR1();
		
		assertEquals(r1_old,r1_new);
		
		
		
		
	}
	
	public void testOpcode0x46() {
		
		short param1 = 0x03;
		short r2_old = (short) (CPU.getR2() & param1);
		CPU.process2((short)0x46, param1);
		short r2_new = CPU.getR2();
		
		assertEquals(r2_old,r2_new);
		
	}
	
	public void testOpcode0x47() {
		
		short param1 = 0x03;
		short r3_old = (short) (CPU.getR3() & param1);
		CPU.process2((short)0x47, param1);
		short r3_new = CPU.getR3();
		
		assertEquals(r3_old,r3_new);
		
		
		}
		public void testOpcode0x48() {
		
		short param1 = 0x03;
		short r0_old = (short) (CPU.getR1() & (param1 & 0x7f));
		CPU.process2((short)0x49, param1);
		short r0_new = CPU.getR1();
		
		assertEquals(r0_old,r0_new);
		
		
		}
	
		public void testOpcode0x49() {
		
		short param1 = 0x03;
		short r1_old = (short) (CPU.getR1() & (param1 & 0x7f));
		CPU.process2((short)0x49, param1);
		short r1_new = CPU.getR1();
		
		assertEquals(r1_old,r1_new);
		
		
		}
		
		public void testOpcode0x4A() {
			
		short param1 = 0x03;
		short r2_old = (short) (CPU.getR2() & (param1 & 0x7f));
		CPU.process2((short)0x50, param1);
		short r2_new = CPU.getR2();
			
		assertEquals(r2_old,r2_new);
			
			
		}
		
		public void testOpcode0x4B() {
			
		short param1 = 0x03;
		short r3_old = (short) (CPU.getR2() & (param1 & 0x7f));
		CPU.process2((short)0x50, param1);
		short r3_new = CPU.getR3();
			
		assertEquals(r3_old,r3_new);
			
				
			}
		
		
		public void testOpcode0xA0() {
			
		short old_r0 = (short) (CPU.getR0() - 0);
		CPU.process1((short) 0xA0);
		short r0_new = CPU.getR0();
		assertEquals(old_r0,r0_new);
		
		
		}
		
		public void testOpcode0xA1() {
			
			short old_r0 = (short) (CPU.getR0() - CPU.getR1());
			CPU.process1((short) 0xA1);
			short r0_new = CPU.getR0();
			assertEquals(old_r0,r0_new);
			
			
			}
		
		public void testOpcode0xA2() {
			
			short old_r0 = (short) (CPU.getR0() - CPU.getR2());
			CPU.process1((short) 0xA2);
			short r0_new = CPU.getR0();
			assertEquals(old_r0,r0_new);
			
			
			}
		
		public void testOpcode0xA3() {
			
			short old_r0 = (short) (CPU.getR0() - CPU.getR3());
			CPU.process1((short) 0xA3);
			short r0_new = CPU.getR0();
			assertEquals(old_r0,r0_new);
			
			
			}
		
		public void testOpcode0xA4() {
			short param1 = 0x04;
			short old_r0 = (short) (CPU.getR0() - param1);
			CPU.process2((short) 0xA4,param1);
			short r0_new = CPU.getR0();
			assertEquals(old_r0,r0_new);
			
			
			}
		public void testOpcode0xA5() {
			short param1 = 0x04;
			short old_r1 = (short) (CPU.getR1() - param1);
			CPU.process2((short) 0xA5,param1);
			short r1_new = CPU.getR1();
			assertEquals(old_r1,r1_new);
			
			
			}
		public void testOpcode0xA6() {
			short param1 = 0x04;
			short old_r2 = (short) (CPU.getR2() - param1);
			CPU.process2((short) 0xA6,param1);
			short r2_new = CPU.getR2();
			assertEquals(old_r2,r2_new);
			
			
			}
		
		public void testOpcode0xA7() {
			short param1 = 0x04;
			short old_r3 = (short) (CPU.getR3() - param1);
			CPU.process2((short) 0xA7, param1);
			short r3_new = CPU.getR3();
			assertEquals(old_r3,r3_new);
			
			
			}
		
		
		
		
		



	
	
	@Test
	public void testOpcode0xCF() {
		
	}
}
