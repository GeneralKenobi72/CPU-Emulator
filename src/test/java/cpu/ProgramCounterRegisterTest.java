package cpu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProgramCounterRegisterTest {
	private ProgramCounterRegister pc;

	@BeforeEach
	public void setup() {
		pc = new ProgramCounterRegister("pcTest");
		pc.setRegisterContent(0);
	}

	// Test nextInstruction method - success case
    @Test
    public void givenValidInstructionCount_whenNextInstruction_thenProgramCounterIncrementsCorrectly() {
        long initialPC = pc.getRegisterContent();  // Assuming getCurrentInstruction() returns the current PC value
        long result = pc.nextInstruction(10);
        long expectedPC = initialPC + 19;

        assertEquals(0, result);  // Check that the method returned success
        assertEquals(expectedPC, pc.getRegisterContent());  // Check that the PC incremented correctly
    }

    // Test nextInstruction method - failure case
    @Test
    public void givenInvalidInstructionCount_whenNextInstruction_thenProgramCounterUnchanged() {
        long initialPC = pc.getRegisterContent();
        long result = pc.nextInstruction(0);  // Example of an invalid scenario

        assertEquals(1, result);  // Check that the method returned failure
        assertEquals(initialPC, pc.getRegisterContent());  // Ensure the PC hasn't changed
    }

    // Test jumpToInstruction method - success case
    @Test
    public void givenValidJumpTarget_whenJumpToInstruction_thenProgramCounterJumpsCorrectly() {
        int targetInstruction = 10;
        int result = pc.jumpToInstruction(targetInstruction, 50);  // Assuming valid jump

        assertEquals(0, result);  // Check that the method returned success
        assertEquals(targetInstruction, pc.getRegisterContent());  // Ensure the PC has jumped to the correct instruction
    }

    // Test jumpToInstruction method - failure case
    @Test
    public void givenInvalidJumpTarget_whenJumpToInstruction_thenProgramCounterUnchanged() {
        long initialPC = pc.getRegisterContent();
        long result = pc.jumpToInstruction(-1, 5);  // Example of an invalid jump target

        assertEquals(1, result);  // Check that the method returned failure
        assertEquals(initialPC, pc.getRegisterContent());  // Ensure the PC hasn't changed
    }
}
