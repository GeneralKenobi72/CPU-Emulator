package memory;

import java.util.HashMap;
import java.util.Map;

public class PageTable {
    private final Map<Long, PageTableEntry> entries;

    public PageTable() {
        entries = new HashMap<>();
    }

    public void addEntry(long pageNumber, PageTableEntry entry) {
        entries.put(pageNumber, entry);
    }

    public PageTableEntry getEntry(long pageNumber) {
        return entries.get(pageNumber);
    }

	public void setEntry(long virtualPageNumber, PageTableEntry entry) {
        entries.put(virtualPageNumber, entry);
    }
}
