package cpu;

/* Instructions are loaded into memory in following format:
 * 
 * 1st byte is opcode of instruction
 * 2nd byte is flag describing first argument
 * 		0x000 - first argument is register and only takes up 8 bytes (only lower 2 bits are actually used - padded to long)
 * 		0x001 - first argument is of long value and takes up 8 bytes(long)
 * 		0x010 - first argument is of char and takes up 8 bytes(for padding)
 * 		0x011 - first argument is an address and takes up 8 bytes(long)
 * 		0x100 - no arguments (filled with zeros, for padding)
 * 		0x101 - address from register
 * 		
 * following bytes are determined by 2nd byte
 * next byte is 2nd flab byte, same as previous flag but describing second argument
 * second flag byte is followed by second argument
 */

public class InstructionSet {
	public static final byte MOV_OPCODE = 0x01;
	public static final byte ADD_OPCODE = 0x02;
	public static final byte SUB_OPCODE = 0x03;
	public static final byte MUL_OPCODE = 0x04;
	public static final byte DIV_OPCODE = 0x05;
	public static final byte AND_OPCODE = 0x06;
	public static final byte OR_OPCODE = 0x07;
	public static final byte XOR_OPCODE = 0x08;
	public static final byte NOT_OPCODE = 0x09;
	public static final byte IN_OPCODE = 0x0A;
	public static final byte OUT_OPCODE = 0x0B;
	public static final byte JMP_OPCODE = 0x0C;
	public static final byte JE_OPCODE = 0x0D;
	public static final byte JNE_OPCODE = 0x0E;
	public static final byte JG_OPCODE = 0x0F;
	public static final byte JGE_OPCODE = 0x10;
	public static final byte JL_OPCODE = 0x11;
	public static final byte JLE_OPCODE = 0x12;
	public static final byte CMP_OPCODE = 0x13;
	public static final byte HALT_OPCODE = 0x14;
	public static final byte EXIT_OPCODE = 0x7F;
}
