package memory;

public class MMU {
	private Memory memory;
	private PageTable pageTable;
	private int nextFreePage = 0;

	public MMU(Memory mem, PageTable pt) {
		memory = mem;
		pageTable = pt;
	}

	public long translateVirtualToPhysical(int virtualAddress) throws Exception {
		long virtualPageNumber = virtualAddress / Memory.PAGE_SIZE;
        long offset = virtualAddress % Memory.PAGE_SIZE;

        PageTableEntry entry = pageTable.getEntry(virtualPageNumber);
        if (entry == null || !entry.getIsPresent()) {
            entry = handlePageFault(virtualPageNumber);
        }

        long physicalPageNumber = entry.getFrameNumber();
        return (physicalPageNumber * Memory.PAGE_SIZE) + offset;
	}

	private PageTableEntry handlePageFault(long virtualPageNumber) throws Exception {
		if (nextFreePage * Memory.PAGE_SIZE >= Integer.MAX_VALUE) {
            throw new Exception("Out of physical memory!");
        }

        memory.allocatePage(nextFreePage);

        PageTableEntry newEntry = new PageTableEntry(nextFreePage, true, true);
        pageTable.setEntry(virtualPageNumber, newEntry);
        nextFreePage++;
        return newEntry;
	}

	public byte readByte(int virtualAddress) throws Exception {
		long physicalAddress = translateVirtualToPhysical(virtualAddress);
        return memory.readByte(physicalAddress);
	}

	public void writeByte(int virtualAddress, byte value) throws Exception {
		long physicalAddress = translateVirtualToPhysical(virtualAddress);
        memory.writeByte(physicalAddress, value);
	}

	public long readLong(int virtualAddress) throws Exception {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result |= ((long) readByte(virtualAddress + i) & 0xFF) << (8 * i);
        }
        return result;
    }

    public void writeLong(int virtualAddress, long value) throws Exception {
        for (int i = 0; i < 8; i++) {
            byte b = (byte) ((value >> (8 * i)) & 0xFF);
            writeByte(virtualAddress + i, b);
        }
    }
}
