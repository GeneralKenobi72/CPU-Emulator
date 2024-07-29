package cpu;

public class CPU {
	public GeneralPurposeRegister r0;
	public GeneralPurposeRegister r1;
	public GeneralPurposeRegister r2;
	public GeneralPurposeRegister r3;
	public ProgramCounterRegister pc;

	public CPU() { 
		r0 = new GeneralPurposeRegister("r0");
		r1 = new GeneralPurposeRegister("r1");
		r2 = new GeneralPurposeRegister("r2");
		r3 = new GeneralPurposeRegister("r3");
		pc = new ProgramCounterRegister("pc", 0);
	}
}
