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
	public void nextInstruction(long numberOfInstructions) {
		if(this.getRegisterContent() == numberOfInstructions) {
			System.out.println("No more instructions to load");
			return;
		}
		else this.setRegisterContent(this.getRegisterContent() + 1);
	}
	public void jumpToInstruction(long instructionToJumpTo, long numberOfInstructions) {
		if(instructionToJumpTo > numberOfInstructions) {
			System.out.println("Instruction out of reach");
			return;
		}
		else this.setRegisterContent(instructionToJumpTo);
	}
}
