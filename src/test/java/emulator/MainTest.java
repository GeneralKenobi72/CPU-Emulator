package emulator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import emulator.*;

public class MainTest {
    private Main main;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStream));  // Redirect System.out to outputStream
		Main.resetContext();
    }

    @Test
    public void givenRegisterArgument_whenOutputValue_thenCorrectOutput() {
        Main.cpu.r0.setRegisterContent(123456);  // Set a value in register r2

        Main.outputValue("r0");  // Call the method to output the value of register r2

        assertEquals("123456\n", outputStream.toString());  // Check the output
    }

    @Test
    public void givenMemoryAddressArgument_whenOutputValue_thenCorrectOutput() {
        Main.mmu.writeLong(100, 789012);  // Set a value at memory address 0x100

        Main.outputValue("[0x100]");  // Call the method to output the value at address 0x100

        assertEquals("789012\n", outputStream.toString());  // Check the output
    }

    @Test
    public void givenInvalidArgument_whenOutputValue_thenNoOutput() {
		String arg = "invalid";
        Main.outputValue(arg);  // Call the method with an invalid argument

        assertEquals("shell: register " + arg + " does not exist\n", outputStream.toString());  // Check that there was no output
    }

    @Test
    public void givenEmptyRegister_whenOutputValue_thenOutputZero() {
        Main.outputValue("r3");  // Assuming r3 is initially 0

        assertEquals("0\n", outputStream.toString());  // Check the output
    }

    @Test
    public void givenEmptyMemory_whenOutputValue_thenOutputZero() {
        Main.outputValue("[0x200]");  // Assuming memory at 0x200 is initially 0

        assertEquals("0\n", outputStream.toString());  // Check the output
    }
}
