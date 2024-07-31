package cpu;

public class GeneralPurposeRegister extends Register {
	private int dataType; // 0 - long, 1 - char

	public GeneralPurposeRegister(String registerName, long registerContent) {
		super(registerName, registerContent);
	}
	public GeneralPurposeRegister(String registerName) {
		super(registerName);	
	}
	public GeneralPurposeRegister() { 
		super();	
	}

	public int getRegisterDataType() {
		return dataType;
	}

	public final void infoDump() {
		super.infoDump();
		if(dataType == 0)
			System.out.println("Data type: long");
		else if(dataType == 1)
			System.out.println("Data type: char");
	}

	public void setRegisterDataType(int dataType) {
		this.dataType = dataType;
	}

	public long addRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() + otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long addRegisters(long num) {
		this.setRegisterContent(this.getRegisterContent() + num);
		return this.getRegisterContent();
	}
	public long addRegisters(char ch) {
		this.setRegisterContent(this.getRegisterContent() + ch);
		return this.getRegisterContent();
	}

	public long subtractRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() - otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long subtractRegisters(long num) {
		this.setRegisterContent(this.getRegisterContent() - num);
		return this.getRegisterContent();
	}
	public long subtractRegisters(char ch) {
		this.setRegisterContent(this.getRegisterContent() - ch);
		return this.getRegisterContent();
	}

	public long multiplyRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() * otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long multiplyRegisters(long num) {
		this.setRegisterContent(this.getRegisterContent() * num);
		return this.getRegisterContent();
	}
	public long multiplyRegisters(char ch) {
		this.setRegisterContent(this.getRegisterContent() * ch);
		return this.getRegisterContent();
	}

	public long divideRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() / otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long divideRegisters(long num) {
		this.setRegisterContent(this.getRegisterContent() / num);
		return this.getRegisterContent();
	}
	public long divideRegisters(char ch) {
		this.setRegisterContent(this.getRegisterContent() / ch);
		return this.getRegisterContent();
	}

	public long bitwiseANDRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() & otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long bitwiseANDRegisters(long num) {
		this.setRegisterContent(this.getRegisterContent() & num);
		return this.getRegisterContent();
	}
	public long bitwiseANDRegisters(char ch) {
		this.setRegisterContent(this.getRegisterContent() & ch);
		return this.getRegisterContent();
	}

	public long bitwiseORRegisters(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(this.getRegisterContent() | otherRegister.getRegisterContent());
		return this.getRegisterContent();
	}
	public long bitwiseORRegisters(long num) {
		this.setRegisterContent(this.getRegisterContent() | num);
		return this.getRegisterContent();
	}
	public long bitwiseORRegisters(char ch) {
		this.setRegisterContent(this.getRegisterContent() | ch);
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
	public long bitwiseXORRegisters(long num) {
		this.setRegisterContent(this.getRegisterContent() ^ num);
		return this.getRegisterContent();
	}
	public long bitwiseXORRegisters(char ch) {
		this.setRegisterContent(this.getRegisterContent() ^ ch);
		return this.getRegisterContent();
	}

	public long movRegister(GeneralPurposeRegister otherRegister) {
		this.setRegisterContent(otherRegister.getRegisterContent());	
		this.setRegisterDataType(otherRegister.getRegisterDataType());
		return this.getRegisterContent();
	}
	public long movRegister(long num) {
		this.setRegisterContent(num);	
		this.setRegisterDataType(0);
		return this.getRegisterContent();
	}
	public long movRegister(char ch) {
		this.setRegisterContent(ch);	
		this.setRegisterDataType(1);
		return this.getRegisterContent();
	}

	public int cmp(GeneralPurposeRegister otherRegister) { // -1 if this is less, 0 if equals, 1 if this is greater
		if(this.getRegisterContent() < otherRegister.getRegisterContent())
			return -1;
		else if(this.getRegisterContent() == otherRegister.getRegisterContent())
			return 0;
		else return 1;
	}
	public int cmp(long otherNumber) { // -1 if this is less, 0 if equals, 1 if this is greater
		if(this.getRegisterContent() < otherNumber)
			return -1;
		else if(this.getRegisterContent() == otherNumber)
			return 0;
		else return 1;
	}
}
