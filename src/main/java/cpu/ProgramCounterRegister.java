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
}
