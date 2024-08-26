package memory;

public class Memory {
	public static final long DATA_START_ADDRESS = 0x00000000L;
	public static final long CODE_START_ADDRESS = 0x1F400000L;
	public static final long MEMORY_SIZE = 0x20000000L;
	public static final long PAGE_SIZE = 4096;

	private byte[] physicalMemory;

	public Memory() {
		physicalMemory = new byte[(int)PAGE_SIZE];
	}

	public void writeByte(long physicalAddress, byte value) {
        if (physicalAddress >= physicalMemory.length) {
            throw new RuntimeException("Accessing unallocated memory: " + physicalAddress);
        }
        physicalMemory[(int)physicalAddress] = value;
    }

	public byte readByte(long physicalAddress) {
		if (physicalAddress >= physicalMemory.length) {
            throw new RuntimeException("Accessing unallocated memory: " + physicalAddress);
        }
        return physicalMemory[(int)physicalAddress];
	}

	public void allocatePage(int physicalPageNumber) {
        long requiredSize = (physicalPageNumber + 1) * PAGE_SIZE;
        if (requiredSize > physicalMemory.length) {
            expandMemory(requiredSize);
        }
    }

	private void expandMemory(long newSize) {
        byte[] newMemory = new byte[(int)newSize];
        System.arraycopy(physicalMemory, 0, newMemory, 0, physicalMemory.length);
        physicalMemory = newMemory;
    }

	public long readLong(long virtualAddress) {
		long value = 0;
		for(int i=0;i<8;i++) {
			byte b = readByte(virtualAddress + i);
			value |= ((long)b & 0xFF) << (i*8);
		}
		return value;
	}

	public void writeLong(long virtualAddress, long value) {
		for (int i = 0; i < 8; i++) {
            byte b = (byte) (value >>> (i * 8));
            writeByte(virtualAddress + i, b);
        }
    }
}
