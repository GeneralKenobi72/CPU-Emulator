package memory;

public class Memory {
	public static final long DATA_START_ADDRESS = 0x00000000L;
	public static final long CODE_START_ADDRESS = 0x1F400000L;
	public static final long MEMORY_SIZE = 0x20000000L;

	private byte[] memory;

	public Memory() {
		memory = new byte[(int) MEMORY_SIZE];
	}

	public byte readByte(long address) {
		if(address < 0 || address >= memory.length) 
			throw new ArrayIndexOutOfBoundsException("Address out of bounds");
		return memory[(int)address];
	}

	public long readLong(long address) {
		if(address < 0 || address+7 >= memory.length) 
			throw new ArrayIndexOutOfBoundsException("Address out of bounds");
		long value = 0;
		for(int i=0;i<8;i++) {
			value |= (memory[(int) (address+i)] & 0xFFL) << (i*8);
		}
		return value;
	}

	public void writeByte(long address, byte value) {
		if(address < 0 || address >= memory.length) 
			throw new ArrayIndexOutOfBoundsException("Address out of bounds");
		memory[(int)address] = value;
	}

	public void writeLong(long address, long value) {
        if (address < 0 || address + 7 >= memory.length) {
            throw new ArrayIndexOutOfBoundsException("Address out of bounds");
        }
        for (int i = 0; i < 8; i++) {
            memory[(int) (address + i)] = (byte) (value >> (i * 8));
        }
    }
}
