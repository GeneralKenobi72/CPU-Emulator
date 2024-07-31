package cpu;

public class ProgramCounterRegister extends Register {
	public ProgramCounterRegister(String registerName, long registerContent) {
		super(registerName, registerContent);
	}
	public ProgramCounterRegister(String registerName) {
		super(registerName);	
	}
	public ProgramCounterRegister() { 
		super();	
	}
	public int nextInstruction(long numberOfInstructions) { // return 0 if jump was successful
		if(this.getRegisterContent() == numberOfInstructions) {
			System.out.println("shell: No more instructions to load");
			return 1;
		}
		else this.setRegisterContent(this.getRegisterContent() + 19);
		return 0;
	}
	public int jumpToInstruction(long instructionToJumpTo, long numberOfInstructions) { // return 0 if jump was successful
		if(instructionToJumpTo > numberOfInstructions) {
			System.out.println("shell: Instruction out of reach");
			return 1;
		}
		else this.setRegisterContent(instructionToJumpTo);
		return 0;
	}
}
