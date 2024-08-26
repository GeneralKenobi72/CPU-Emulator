package memory;

public class PageTableEntry {
	private long frameNumber;
	private boolean isPresent;
	private boolean isWritable;
	
	PageTableEntry(long frameNumber, boolean isPresent, boolean isWritable) {
		this.frameNumber = frameNumber;
		this.isPresent = isPresent;
		this.isWritable = isWritable;
	}

	public long getFrameNumber() {
		return frameNumber;
	}

	public boolean getIsPresent() {
		return isPresent;
	}
	
	public boolean getIsWritable() {
		return isWritable;
	}

	public void setIsPresent(boolean isP) {
		this.isPresent = isP;
	}

	public void setIsWritable(boolean isW) {
		this.isWritable = isW;
	}
}
