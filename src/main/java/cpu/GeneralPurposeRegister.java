package cpu;

public class GeneralPurposeRegister extends Register {
	public GeneralPurposeRegister(String registerName, long registerContent) {
		super(registerName, registerContent);
	}
	public GeneralPurposeRegister(String registerName) {
		super(registerName);	
	}
	public GeneralPurposeRegister() { 
		super();	
	}

	public long addRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() + otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long subtractRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() - otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long multiplyRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() * otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long divideRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() / otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long bitwiseANDRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() & otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long bitwiseORRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() | otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long bitwiseNOTRegister() {
		this.setRegisterContent(~this.getRegisterContent());
		return this.getRegisterContent();
	}
	public long bitwiseXORRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() ^ otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
}
