package memory;

import emulator.Main;

public class MMU {
	private Memory memory;
	private PageTable pageTable;
	private int nextFreePage = 0;

	public MMU(Memory mem, PageTable pt) {
		memory = mem;
		pageTable = pt;
	}

	public long translateVirtualToPhysical(int virtualAddress) {
		long virtualPageNumber = virtualAddress / Memory.PAGE_SIZE;
        long offset = virtualAddress % Memory.PAGE_SIZE;

        PageTableEntry entry = pageTable.getEntry(virtualPageNumber);
        if (entry == null || !entry.getIsPresent()) {
            entry = handlePageFault(virtualPageNumber);
        }

        long physicalPageNumber = entry.getFrameNumber();
        return (physicalPageNumber * Memory.PAGE_SIZE) + offset;
	}

	private PageTableEntry handlePageFault(long virtualPageNumber) {
		if (nextFreePage * Memory.PAGE_SIZE >= Long.MAX_VALUE) {
			return null;
        }

        memory.allocatePage(nextFreePage);

        PageTableEntry newEntry = new PageTableEntry(nextFreePage, true, true);
        pageTable.setEntry(virtualPageNumber, newEntry);
        nextFreePage++;
        return newEntry;
	}

	public byte readByte(long virtualAddress) {
		long physicalAddress = translateVirtualToPhysical((int)virtualAddress);
        return memory.readByte(physicalAddress);
	}

	public void writeByte(long virtualAddress, byte value) {
		if(Main.usedMemory > virtualAddress) {
			System.out.println("shell: addresses 0x00 through [0x" + Long.toHexString(Main.usedMemory) + "] are used by user program and are not accessible"); 
		}
		long physicalAddress = translateVirtualToPhysical((int)virtualAddress);
        memory.writeByte(physicalAddress, value);
	}

	public long readLong(long virtualAddress) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result |= ((long) readByte(virtualAddress + i) & 0xFF) << (8 * i);
        }
        return result;
    }

    public void writeLong(long virtualAddress, long value) {
        for (int i = 0; i < 8; i++) {
            byte b = (byte) ((value >> (8 * i)) & 0xFF);
            writeByte(virtualAddress + i, b);
        }
    }
}
