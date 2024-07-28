package cpu;

public abstract class Register {
	private String registerName;
	private long registerContent;

	public Register(String registerName, long registerContent) {
		this.registerName = registerName;
		this.registerContent = registerContent;
	}
	public Register(String registerName) {
		this.registerName = registerName;
	}
	public Register() { }

	public void infoDump() {
		System.out.println("Register name: " + this.getRegisterName());
		System.out.println("Register content: " + this.getRegisterContent());
	}

	public String getRegisterName() {
		return registerName;
	}
	public long getRegisterContent() {
		return registerContent;
	}
	public void setRegisterName(String registerName) {
		this.registerName = registerName;
	}
	public void setRegisterContent(long registerContent) {
		this.registerContent = registerContent;
	}
}
