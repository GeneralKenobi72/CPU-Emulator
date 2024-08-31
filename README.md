# CPU EMULATOR

### Processor Architecture Emulator (College project)

This project implements an emulator for a custom processor architecture. The processor's programming model includes the following features:

    🔹 Four 64-bit general-purpose registers
    🔹 A program counter
    🔹 A 64-bit address space
    🔹 1-byte length for the content of each memory address

### Instruction Set

The instruction set of the emulator includes:

    Basic Arithmetic Operations:
        🔹 ADD (Addition)
        🔹 SUB (Subtraction)
        🔹 MUL (Multiplication)
        🔹 DIV (Division)

    Basic Bitwise Logical Operations:
        🔹 AND (Bitwise AND)
        🔹 OR (Bitwise OR)
        🔹 NOT (Bitwise NOT)
        🔹 XOR (Bitwise XOR)

    Data Transfer Instructions:
        🔹 MOV (Move) with support for both direct and indirect addressing

    Branching Instructions:
        🔹 JMP (Unconditional Jump)
        🔹 JE (Jump if Equal)
        🔹 JNE (Jump if Not Equal)
        🔹 JGE (Jump if Greater or Equal)
        🔹 JL (Jump if Less)
        🔹 CMP (Compare) with support for both direct and indirect branching

    I/O Routines:
        🔹 Read a character from the keyboard into a register
        🔹 Output a character from a register to the screen

    Processor Control:
        🔹 HALT (Stop Processor)
        🔹 RESUME (Resume Processor)

### Example of usage

You can write text file that utilizes commands described above and save it to src/java/main/input directory. There are 2 example files already. You can than load them in application using file load command:
```
fl file_name.txt
```
Now you can step through each line of code using step(s) command, or just run entire program using run command.

Using help command, you can see other useful commands.
