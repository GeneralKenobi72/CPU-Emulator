package emulator;

import cpu.*;
import memory.*;
import java.util.Scanner;
import java.io.*;
import java.util.*;

public class Main {
	public static CPU cpu = new CPU();
	public static Memory memory = new Memory();
	public static PageTable pageTable = new PageTable();
	public static MMU mmu = new MMU(memory, pageTable);

	public static boolean on = true;
	public static boolean fileLoaded = false;
	public static ArrayList<String> linesFromFile = new ArrayList<>();
	public static int compareFlag = -2; // -2 = clear flag, -1 = less, 0 = equals, 1 = greater
	public static int errFlag = 0;
	
	public static void main(String args[]) {
		printWelcomeMessage();
		while(on) {
			scanInput("");
			errFlag = 0;
		}
	}

	public static void scanInput(String command) { // TODO: Catch CTRL-C interrupt
		String[] strings;
		if(command.equals("")) {
			System.out.print(">");
			Scanner scan = new Scanner(System.in);
			String scanned;
			scanned = scan.nextLine();
			strings = scanned.split(" ");
		} else
			strings = command.split(" ");
		if(strings.length == 1) {
			if(strings[0].equals("help"))
				printHelp();
			else if(strings[0].equals("poweroff"))
				on = false;
			else if(strings[0].equals("halt"))
				haltCPU();
			else if(strings[0].equals("step") || strings[0].equals("s"))
				nextLineFromFile();
			else if(strings[0].equals("cat"))
				catFile();
			else if(strings[0].equals("exit"))
				resetContext();
			else if(strings[0].equals("run"))
				runEntireProgram();
			else
				System.out.println("shell: Command doesn't exist");
		} else if(strings.length == 2) {
			if(strings[0].equals("fl"))
				loadFileToMemory(strings[1]);
			else if(strings[0].equals("out"))
				outputValue(strings[1]);
			else if(strings[0].equals("in"))
				inputValue(strings[1]);
			else if(strings[0].equals("not"))
				callBitwiseNot(strings[1]);
			else if(strings[0].equals("jmp") ||
					strings[0].equals("je") ||
					strings[0].equals("jne") ||
					strings[0].equals("jg") ||
					strings[0].equals("jge") ||
					strings[0].equals("jle") ||
					strings[0].equals("jl"))
				callJump(strings[0], strings[1]);
			else
				System.out.println("shell: Command doesn't exist");
		} else if(strings.length == 3) {
			if((strings[0].equals("mov") ||
						strings[0].equals("add") ||
						strings[0].equals("sub") ||
						strings[0].equals("mul") ||
						strings[0].equals("div") ||
						strings[0].equals("and") ||
						strings[0].equals("or") ||
						strings[0].equals("cmp") ||
						strings[0].equals("xor"))
						&& strings[1].charAt(strings[1].length()-1) == ',')
				call3ArgCmd(strings[0], strings[1], strings[2]);
			else
			System.out.println("shell: Command doesn't exist");
		} else
			System.out.println("shell: Command doesn't exist");
	}

	public static void loadFileToMemory(String fileName) {
		if(fileLoaded == true) {
			System.out.println("File already loaded, loading new file will overwrite current file and registers will be reset. Are you sure you want to continue?[y/n]");
			Scanner scanYN = new Scanner(System.in);
			String YN = scanYN.nextLine();
			if(YN.equals("y"))
				resetContext();
			else
				return;
		}
		try(BufferedReader br = new BufferedReader(new FileReader("src" + File.separator + "main" + File.separator + "java" + File.separator + "input" + File.separator + fileName))) {
			String line;
			while((line = br.readLine()) != null) {
				linesFromFile.add(line);
			}
		}catch(IOException e) {
			System.out.println("shell: file does not exist");
		}

		long address = Memory.CODE_START_ADDRESS;
		for(String s : linesFromFile) {
			byte OP_CODE = 0x00;
			byte flagArg1 = 0x04, flagArg2 = 0x04;
			long arg1 = 0, arg2 = 0;
			if(s.isBlank() || s.isEmpty())
				continue;
			String[] strings = s.split(" ");
			if(strings.length > 3) {
				System.out.println("shell: too many arguments");
				resetContext();
				return;
			}
			switch(strings[0]) {
				case "mov":
					OP_CODE = InstructionSet.MOV_OPCODE;
					break;
				case "add":
					OP_CODE = InstructionSet.ADD_OPCODE;
					break;
				case "sub":
					OP_CODE = InstructionSet.SUB_OPCODE;
					break;
				case "mul":
					OP_CODE = InstructionSet.MUL_OPCODE;
					break;
				case "div":
					OP_CODE = InstructionSet.DIV_OPCODE;
					break;
				case "and":
					OP_CODE = InstructionSet.AND_OPCODE;
					break;
				case "or":
					OP_CODE = InstructionSet.OR_OPCODE;
					break;
				case "xor":
					OP_CODE = InstructionSet.XOR_OPCODE;
					break;
				case "not":
					OP_CODE = InstructionSet.NOT_OPCODE;
					break;
				case "jmp":
					OP_CODE = InstructionSet.JMP_OPCODE;
					break;
				case "je":
					OP_CODE = InstructionSet.JE_OPCODE;
					break;
				case "jne":
					OP_CODE = InstructionSet.JNE_OPCODE;
					break;
				case "jg":
					OP_CODE = InstructionSet.JG_OPCODE;
					break;
				case "jge":
					OP_CODE = InstructionSet.JGE_OPCODE;
					break;
				case "jl":
					OP_CODE = InstructionSet.JL_OPCODE;
					break;
				case "jle":
					OP_CODE = InstructionSet.JLE_OPCODE;
					break;
				case "cmp":
					OP_CODE = InstructionSet.CMP_OPCODE;
					break;
				case "halt":
					OP_CODE = InstructionSet.HALT_OPCODE;
					break;
				case "in":
					OP_CODE = InstructionSet.IN_OPCODE;
					break;
				case "out":
					OP_CODE = InstructionSet.OUT_OPCODE;
					break;
				case "exit":
					OP_CODE = InstructionSet.EXIT_OPCODE;
					break;
				default:
					System.out.println("shell: Command does not exist, resetting context");
					resetContext();
					return;
			}
			if(OP_CODE == 0x00)
				return;
			if(strings.length == 3) {
				if(strings[2].equals("r0")) {
					flagArg2 = 0x00;
					arg2 = 0x01;
				}
				else if(strings[2].equals("r1")) {
					flagArg2 = 0x00;
					arg2 = 0x02;
				}
				else if(strings[2].equals("r2")) {
					flagArg2 = 0x00;
					arg2 = 0x03;
				}
				else if(strings[2].equals("r3")) {
					flagArg2 = 0x00;
					arg2 = 0x04;
				}
				else if(strings[2].replace("[", "").replace("]", "").equals("r0")) {
					flagArg2 = 0x05;
					arg2 = 0x01;
				}
				else if(strings[2].replace("[", "").replace("]", "").equals("r1")) {
					flagArg2 = 0x05;
					arg2 = 0x02;
				}
				else if(strings[2].replace("[", "").replace("]", "").equals("r2")) {
					flagArg2 = 0x05;
					arg2 = 0x03;
				}
				else if(strings[2].replace("[", "").replace("]", "").equals("r3")) {
					flagArg2 = 0x05;
					arg2 = 0x04;
				}
				else if(strings[2].equals("pc")) {
					System.out.println("shell: register pc cannot be used this way");
					resetContext();
					return;
				}
				else if(strings[2].length() >= 5 && strings[2].substring(0, 3).equals("[0x") && strings[2].charAt(strings[2].length() - 1) == ']') {
					flagArg2 = 0x03;
					long adr;
					try {
						adr = Long.decode(strings[2].replace("[", "").replace("]",""));
						System.out.println(adr);
						arg2 = adr;
					} catch(NumberFormatException e) {
						System.out.println(s);
						System.out.println("shell: Operand not supported");
						resetContext();
						return;
					}
				}
				else if(strings[2].charAt(0) == '\'' && strings[2].charAt(2) == '\'') {
					flagArg2 = 0x02;
					arg2 = strings[2].charAt(1);
				}
				else {
					long val;
					try {
						val = Long.parseLong(strings[2]);
						flagArg2 = 0x01;
						arg2 = val;
					} catch(NumberFormatException e) {
						System.out.println("shell: Operand not supported");
						resetContext();
						return;
					}
				}
			} else
				flagArg2 = 4;
			if(strings.length > 1) {
				String s1WithoutComma = strings[1].replace(",", "");
				if(s1WithoutComma.equals("r0")) {
					flagArg1 = 0x00;
					arg1 = 0x01;
				}
				else if(s1WithoutComma.equals("r1")) {
					flagArg1 = 0x00;
					arg1 = 0x02;
				}
				else if(s1WithoutComma.equals("r2")) {
					flagArg1 = 0x00;
					arg1 = 0x03;
				}
				else if(s1WithoutComma.equals("r3")) {
					flagArg1 = 0x00;
					arg1 = 0x04;
				}
				else if(s1WithoutComma.replace("[", "").replace("]", "").equals("r0")) {
					flagArg1 = 0x05;
					arg1 = 0x01;
				}
				else if(s1WithoutComma.replace("[", "").replace("]", "").equals("r1")) {
					flagArg1 = 0x05;
					arg1 = 0x02;
				}
				else if(s1WithoutComma.replace("[", "").replace("]", "").equals("r2")) {
					flagArg1 = 0x05;
					arg1 = 0x03;
				}
				else if(s1WithoutComma.replace("[", "").replace("]", "").equals("r3")) {
					flagArg1 = 0x05;
					arg1 = 0x04;
				}
				else if(s1WithoutComma.equals("pc")) {
					System.out.println("shell: register pc cannot be used this way");
					resetContext();
					return;
				}
				else if(s1WithoutComma.length() >= 5 && s1WithoutComma.substring(0, 3).equals("[0x") && s1WithoutComma.charAt(s1WithoutComma.length()-1) == ']') {
					flagArg1 = 0x03;
					long adr;
					try {
						adr = Long.decode(s1WithoutComma.replace("[", "").replace("]",""));
						arg1 = adr;
					} catch(NumberFormatException e) {
						System.out.println(s);
						System.out.println("shell: Operand not supported");
						resetContext();
						return;
					}
				}
				else if(s1WithoutComma.charAt(0) == '\'' && s1WithoutComma.charAt(2) == '\'') {
					flagArg1 = 0x02;
					arg1 = s1WithoutComma.charAt(1);
				}
				else {
					long val;
					try {
						val = Long.parseLong(s1WithoutComma);
						flagArg1 = 0x01;
						arg1 = val;
					} catch(NumberFormatException e) {
						System.out.println(s);
						System.out.println("shell: Operand not supported");
						resetContext();
						return;
					}
				}
			}
			memory.writeByte(address, OP_CODE);
			address++;
			memory.writeByte(address, flagArg1);
			address++;
			memory.writeLong(address, arg1);
			address+=8;
			memory.writeByte(address, flagArg2);
			address++;
			memory.writeLong(address, arg2);
			address+=8;
		}
		memory.writeByte(address, InstructionSet.EXIT_OPCODE);
		cpu.pc.setRegisterContent(Memory.CODE_START_ADDRESS);
		fileLoaded = true;
		address = Memory.CODE_START_ADDRESS;
	}

	public static void catFile() {
		if(linesFromFile.isEmpty()) {
			System.out.println("shell: file empty or not loaded");
			errFlag = 1;
			return;
		}
		for(int i=0;i<linesFromFile.size();i++) {
			if(i == ((cpu.pc.getRegisterContent()-Memory.CODE_START_ADDRESS)/19))
				System.out.println(" ==> (" + (i+1) + ") " + linesFromFile.get(i));
			else System.out.println("     (" +  (i+1) + ") " + linesFromFile.get(i));
		}
	}

	public static void callJump(String jump, String line) {
		long lineNum;
		if(!fileLoaded) {
			System.out.println("shell: Invalid use of jump function, no file loaded");
			errFlag = 1;
			return;
		}
		try {
			lineNum = Long.parseLong(line);
		} catch(NumberFormatException e) {
			System.out.println("shell: invalid use of jump function");
			errFlag = 1;
			return;
		}
		int jumpWasSuccessful = 1;
		if(jump.equals("jmp"))
			jumpWasSuccessful = cpu.pc.jumpToInstruction((lineNum-2)*19+Memory.CODE_START_ADDRESS, Memory.MEMORY_SIZE);
		else if(compareFlag !=-2) {
			if(jump.equals("je")) {
				if(compareFlag == 0)
					jumpWasSuccessful = cpu.pc.jumpToInstruction((lineNum-2)*19+Memory.CODE_START_ADDRESS, Memory.MEMORY_SIZE);
			} else if(jump.equals("jne")) {
				if(compareFlag != 0)
					jumpWasSuccessful = cpu.pc.jumpToInstruction((lineNum-2)*19+Memory.CODE_START_ADDRESS, Memory.MEMORY_SIZE);
			} else if(jump.equals("jg")) {
				if(compareFlag == 1)
					jumpWasSuccessful = cpu.pc.jumpToInstruction((lineNum-2)*19+Memory.CODE_START_ADDRESS, Memory.MEMORY_SIZE);
			} else if(jump.equals("jl")) {
				if(compareFlag == -1)
					jumpWasSuccessful = cpu.pc.jumpToInstruction((lineNum-2)*19+Memory.CODE_START_ADDRESS, Memory.MEMORY_SIZE);
			} else if(jump.equals("jge")) {
				if(compareFlag >= 0)
					jumpWasSuccessful = cpu.pc.jumpToInstruction((lineNum-2)*19+Memory.CODE_START_ADDRESS, Memory.MEMORY_SIZE);
			} else if(jump.equals("jle")) {
				if(compareFlag <= 0)
					jumpWasSuccessful = cpu.pc.jumpToInstruction((lineNum-2)*19+Memory.CODE_START_ADDRESS, Memory.MEMORY_SIZE);
			}
		}
		else {
			System.out.println("shell: invalid use of jump function");
			errFlag = 1;
		}
		if(jumpWasSuccessful == 1) {
			errFlag = 1;
		}
	}

	public static void call3ArgCmd(String cmd, String reg11, String reg2) {
		String reg1 = reg11.substring(0, reg11.length() - 1);
		try {
			long num = Long.parseLong(reg2);
			call3ArgCmdSecondLong(cmd, reg1, num);
			return;
		} catch(NumberFormatException e) {
			if(reg2.length() == 3 && reg2.charAt(0) == '\'' && reg2.charAt(2) == '\'') {
				call3ArgCmdSecondChar(cmd, reg1, reg2.charAt(1));
				return;
			}
		}
		GeneralPurposeRegister regFirst = new GeneralPurposeRegister();
		GeneralPurposeRegister regSecond = new GeneralPurposeRegister();
		if(reg1.equals("r0"))
			regFirst = cpu.r0;
		else if(reg1.equals("r1"))
			regFirst = cpu.r1;
		else if(reg1.equals("r2"))
			regFirst = cpu.r2;
		else if(reg1.equals("r3"))
			regFirst = cpu.r3;
		else if(reg1.equals("pc")) {
			System.out.println("shell: pc register can't be modified this way");
			errFlag = 1;
			return;
		}
		else {
			System.out.println("shell: register " + reg1 + " does not exist");
			errFlag = 1;
			return;
		}
		if(reg2.equals("r0"))
			regSecond = cpu.r0;
		else if(reg2.equals("r1"))
			regSecond = cpu.r1;
		else if(reg2.equals("r2"))
			regSecond = cpu.r2;
		else if(reg2.equals("r3"))
			regSecond = cpu.r3;
		else if(reg2.equals("pc")) {
			System.out.println("shell: pc register can't be used for this operation");
			errFlag = 1;
			return;
		}
		else {
			System.out.println("shell: register " + reg2 + " does not exist");
			errFlag = 1;
			return;
		}
		if(cmd.equals("mov"))
			regFirst.movRegister(regSecond);
		else if(cmd.equals("add"))
			regFirst.addRegisters(regSecond);
		else if(cmd.equals("sub"))
			regFirst.subtractRegisters(regSecond);
		else if(cmd.equals("mul"))
			regFirst.multiplyRegisters(regSecond);
		else if(cmd.equals("div"))
			regFirst.divideRegisters(regSecond);
		else if(cmd.equals("and"))
			regFirst.bitwiseANDRegisters(regSecond);
		else if(cmd.equals("or"))
			regFirst.bitwiseORRegisters(regSecond);
		else if(cmd.equals("xor"))
			regFirst.bitwiseXORRegisters(regSecond);
		else if(cmd.equals("cmp"))
			compareFlag = regFirst.cmp(regSecond);
		else {
			System.out.println("shell: Command does not exist");
			errFlag = 1;
			return;
		}
		if(reg1.equals("r0"))
			cpu.r0 = regFirst;
		else if(reg1.equals("r1"))
			cpu.r1 = regFirst;
		else if(reg1.equals("r2"))
			cpu.r2 = regFirst;
		else
			cpu.r3 = regFirst;
	}

	public static void call3ArgCmdSecondLong(String cmd, String reg, long num) {
		GeneralPurposeRegister regFirst = new GeneralPurposeRegister();
		if(reg.equals("r0"))
			regFirst = cpu.r0;
		else if(reg.equals("r1"))
			regFirst = cpu.r1;
		else if(reg.equals("r2"))
			regFirst = cpu.r2;
		else if(reg.equals("r3"))
			regFirst = cpu.r3;
		else if(reg.equals("pc")) {
			System.out.println("shell: pc register can't be modified this way");
			errFlag = 1;
			return;
		}
		else {
			System.out.println("shell: register " + reg + " does not exist");
			errFlag = 1;
			return;
		}
		if(cmd.equals("mov"))
			regFirst.movRegister(num);
		else if(cmd.equals("add"))
			regFirst.addRegisters(num);
		else if(cmd.equals("sub"))
			regFirst.subtractRegisters(num);
		else if(cmd.equals("mul"))
			regFirst.multiplyRegisters(num);
		else if(cmd.equals("div"))
			regFirst.divideRegisters(num);
		else if(cmd.equals("and"))
			regFirst.bitwiseANDRegisters(num);
		else if(cmd.equals("or"))
			regFirst.bitwiseORRegisters(num);
		else if(cmd.equals("xor"))
			regFirst.bitwiseXORRegisters(num);
		else if(cmd.equals("cmp"))
			compareFlag = regFirst.cmp(num);
		else {
			System.out.println("shell: Command does not exist");
			errFlag = 1;
			return;
		}
		if(reg.equals("r0"))
			cpu.r0 = regFirst;
		else if(reg.equals("r1"))
			cpu.r1 = regFirst;
		else if(reg.equals("r2"))
			cpu.r2 = regFirst;
		else
			cpu.r3 = regFirst;
	}

	public static void call3ArgCmdSecondChar(String cmd, String reg, char ch) {
		GeneralPurposeRegister regFirst = new GeneralPurposeRegister();
		if(reg.equals("r0"))
			regFirst = cpu.r0;
		else if(reg.equals("r1"))
			regFirst = cpu.r1;
		else if(reg.equals("r2"))
			regFirst = cpu.r2;
		else if(reg.equals("r3"))
			regFirst = cpu.r3;
		else if(reg.equals("pc")) {
			System.out.println("shell: pc register can't be modified this way");
			errFlag = 1;
			return;
		}
		else {
			System.out.println("shell: register " + reg + " does not exist");
			errFlag = 1;
			return;
		}
		if(cmd.equals("mov"))
			regFirst.movRegister(ch);
		else if(cmd.equals("add"))
			regFirst.addRegisters(ch);
		else if(cmd.equals("sub"))
			regFirst.subtractRegisters(ch);
		else if(cmd.equals("mul"))
			regFirst.multiplyRegisters(ch);
		else if(cmd.equals("div"))
			regFirst.divideRegisters(ch);
		else if(cmd.equals("and"))
			regFirst.bitwiseANDRegisters(ch);
		else if(cmd.equals("or"))
			regFirst.bitwiseORRegisters(ch);
		else if(cmd.equals("xor"))
			regFirst.bitwiseXORRegisters(ch);
		else if(cmd.equals("cmp"))
			compareFlag = regFirst.cmp(ch);
		else {
			System.out.println("shell: Command does not exist");
			return;
		}
		if(reg.equals("r0"))
			cpu.r0 = regFirst;
		else if(reg.equals("r1"))
			cpu.r1 = regFirst;
		else if(reg.equals("r2"))
			cpu.r2 = regFirst;
		else
			cpu.r3 = regFirst;
	}

	public static void callBitwiseNot(String reg) {
		if(reg.equals("r0"))
			cpu.r0.bitwiseNOTRegister();
		else if(reg.equals("r1"))
			cpu.r1.bitwiseNOTRegister();
		else if(reg.equals("r2"))
			cpu.r2.bitwiseNOTRegister();
		else if(reg.equals("r3"))
			cpu.r3.bitwiseNOTRegister();
		else if(reg.equals("pc")) {
			System.out.println("shell: pc register can't be modified this way");
			errFlag = 1;
			return;
		}
		else {
			System.out.println("shell: register " + reg + " does not exist");
			errFlag = 1;
			return;
		}
	}

	public static void runEntireProgram() {
		if(!fileLoaded) {
			System.out.println("shell: File not loaded");
			return;
		}

		while(fileLoaded == true)
			nextLineFromFile();
	}

	public static void nextLineFromFile() {
		if(!fileLoaded) {
			System.out.println("shell: File not loaded");
			return;
		}
		String instruction = "";
		byte OP_CODE = memory.readByte(cpu.pc.getRegisterContent());
		if(OP_CODE == InstructionSet.EXIT_OPCODE) {
			System.out.println("shell: program reached end of execution");
			resetContext();
			return;
		}
		byte flagArg1 = memory.readByte(cpu.pc.getRegisterContent()+1);
		long arg1 = memory.readLong(cpu.pc.getRegisterContent()+2);
		byte flagArg2 = memory.readByte(cpu.pc.getRegisterContent()+10);
		long arg2 = memory.readLong(cpu.pc.getRegisterContent()+11);
		switch(OP_CODE) {
			case InstructionSet.MOV_OPCODE:
				instruction += "mov ";
				break;
			case InstructionSet.ADD_OPCODE:
				instruction += "add ";
				break;
			case InstructionSet.SUB_OPCODE:
				instruction += "sub ";
				break;
			case InstructionSet.MUL_OPCODE:
				instruction += "mul ";
				break;
			case InstructionSet.DIV_OPCODE:
				instruction += "div ";
				break;
			case InstructionSet.AND_OPCODE:
				instruction += "and ";
				break;
			case InstructionSet.OR_OPCODE:
				instruction += "or ";
				break;
			case InstructionSet.XOR_OPCODE:
				instruction += "xor ";
				break;
			case InstructionSet.NOT_OPCODE:
				instruction += "not ";
				break;
			case InstructionSet.IN_OPCODE:
				instruction += "in ";
				break;
			case InstructionSet.OUT_OPCODE:
				instruction += "out ";
				break;
			case InstructionSet.JMP_OPCODE:
				instruction += "jmp ";
				break;
			case InstructionSet.JE_OPCODE:
				instruction += "je ";
				break;
			case InstructionSet.JNE_OPCODE:
				instruction += "jne ";
				break;
			case InstructionSet.JG_OPCODE:
				instruction += "jg ";
				break;
			case InstructionSet.JGE_OPCODE:
				instruction += "jge ";
				break;
			case InstructionSet.JL_OPCODE:
				instruction += "jl ";
				break;
			case InstructionSet.JLE_OPCODE:
				instruction += "jle ";
				break;
			case InstructionSet.CMP_OPCODE:
				instruction += "cmp ";
				break;
			case InstructionSet.HALT_OPCODE:
				instruction += "halt ";
				break;
		}
		if(flagArg1 == 0x00) {
			switch((int)arg1) {
				case 1:
					instruction += "r0";
					break;
				case 2:
					instruction += "r1";
					break;
				case 3:
					instruction += "r2";
					break;
				case 4:
					instruction += "r3";
					break;
			}
		} else if(flagArg1 == 0x03) {
			instruction += "[0x" + Long.toString(arg1) + "]";
		} else if(flagArg1 == 0x01) {
			instruction += Long.toString(arg1);
		} else if(flagArg1 == 0x05) {
			if(arg1 == 0x01)
				instruction += "[0x" + cpu.r0.getRegisterContent() + "]";
			else if(arg1 == 0x02)
				instruction += "[0x" + cpu.r1.getRegisterContent() + "]";
			else if(arg1 == 0x03)
				instruction += "[0x" + cpu.r2.getRegisterContent() + "]";
			else if(arg1 == 0x04)
				instruction += "[0x" + cpu.r3.getRegisterContent() + "]";
			flagArg1 = 3;
		} else if(flagArg1 == 0x04) {

		} else {
			System.out.println("shell: invalid use of operands");
			resetContext();
			return;
		}
		if(flagArg2 == 0x00) {
			switch((int)arg2) {
				case 1:
					instruction += ", r0";
					break;
				case 2:
					instruction += ", r1";
					break;
				case 3:
					instruction += ", r2";
					break;
				case 4:
					instruction += ", r3";
					break;
			}
		} else if(flagArg2 == 0x01) {
			instruction += ", " + Long.toString(arg2);
		} else if(flagArg2 == 0x03) {
			instruction += ", [0x" + Long.toString(arg2) + "]";
		} else if(flagArg2 == 0x02) {
			instruction += ", \'" + (char)arg2 + "\'";
		} else if(flagArg2 == 0x05) {
			if(arg2 == 0x01) {
				instruction += ", [0x" + cpu.r0.getRegisterContent() + "]";
				arg2 = cpu.r0.getRegisterContent();
			}
			else if(arg2 == 0x02) {
				instruction += ", [0x" + cpu.r1.getRegisterContent() + "]";
				arg2 = cpu.r1.getRegisterContent();
			}
			else if(arg2 == 0x03) {
				instruction += ", [0x" + cpu.r2.getRegisterContent() + "]";
				arg2 = cpu.r2.getRegisterContent();
			}
			else if(arg2 == 0x04) {
				instruction += ", [0x" + cpu.r3.getRegisterContent() + "]";
				arg2 = cpu.r3.getRegisterContent();
			}
			flagArg2 = 0x03;
		}
		System.out.println(instruction);
		if(OP_CODE == InstructionSet.MOV_OPCODE) {
			if(flagArg1 == 3) {
				if(flagArg2 == 0) {
					if(arg2 == 1)
						memory.writeLong(arg1, cpu.r0.getRegisterContent());
					else if(arg2 == 2)
						memory.writeLong(arg1, cpu.r1.getRegisterContent());
					else if(arg2 == 3)
						memory.writeLong(arg1, cpu.r2.getRegisterContent());
					else if(arg2 == 4)
						memory.writeLong(arg1, cpu.r3.getRegisterContent());
				}
				else if(flagArg2 == 1) {
					memory.writeLong(arg1, arg2);			
				}
				else if(flagArg2 == 2) {
					memory.writeLong(arg1, arg2);
				}
				else if(flagArg2 == 3) {
					memory.writeLong(arg1, memory.readLong(arg2));
				}
				cpu.pc.nextInstruction(linesFromFile.size());
				return;
			}
			else if(flagArg2 == 3) {
				if(flagArg1 == 0) {
					if(arg1 == 1)
						cpu.r0.setRegisterContent(memory.readLong(arg2));
					else if(arg1 == 2)
						cpu.r1.setRegisterContent(memory.readLong(arg2));
					else if(arg1 == 3)
						cpu.r2.setRegisterContent(memory.readLong(arg2));
					else if(arg1 == 4)
						cpu.r3.setRegisterContent(memory.readLong(arg2));
				}
				cpu.pc.nextInstruction(linesFromFile.size());
				return;
			}
		}

		scanInput(instruction);
		if(errFlag == 1) {
			System.out.println("shell: Error on line " + cpu.pc.getRegisterContent() + " resetting context, exiting file");
		 	resetContext();
		 	return;
		}
		cpu.pc.nextInstruction(Memory.MEMORY_SIZE);
	}

	public static void haltCPU() {
		System.out.println("Halting CPU, enter resume to exist halted state");
		Scanner scanHalt = new Scanner(System.in);
		String resume = "";
		while(!resume.equals("resume"))
			resume = scanHalt.nextLine();
		System.out.println("Exiting halted state");
	}

	public static void resetContext() { // Resets all registers, and loaded lines file.
		cpu.r0.setRegisterContent(0);
		cpu.r1.setRegisterContent(0);
		cpu.r2.setRegisterContent(0);
		cpu.r3.setRegisterContent(0);
		cpu.pc.setRegisterContent(0);
		linesFromFile.clear();
		fileLoaded = false;
	}

	public static void inputValue(String argument) {
		if(argument.substring(0, 3).equals("[0x") && argument.charAt(argument.length()-1) == ']') {
			try {
				long adr = Long.parseLong(argument.replace("[0x", "").replace("]",""));
				Scanner scanReg = new Scanner(System.in);
				String in = scanReg.nextLine();
				try {
					Long val = Long.parseLong(in);
					memory.writeLong(adr, val);
				} catch(NumberFormatException e) {
					System.out.println("shell: value can only be of value long");
					errFlag = -1;
					return;
				}
			} catch(NumberFormatException e) {
				System.out.println("shell: argument not and address");
				errFlag = -1;
				return;
			}
			return;
		}
		if(!argument.equals("r0") && !argument.equals("r1") && !argument.equals("r2") && !argument.equals("r3") && !argument.equals("pc")) {
			System.out.println("shell: register " + argument + " does not exist");
			errFlag = 1;
			return;
		}
		if(argument.equals("pc")) {
			System.out.println("shell: pc register can't be modified this way");
			errFlag = 1;
			return;
		}
		GeneralPurposeRegister rI = new GeneralPurposeRegister();
		Scanner scanReg = new Scanner(System.in);
		String in = scanReg.nextLine();
		try {
			long possibleValue = Long.parseLong(in);
			rI.setRegisterDataType(0);
			rI.setRegisterContent(possibleValue);
		} catch(NumberFormatException e) {
			try {
				Double.parseDouble(in);
				System.out.println("Only Long and Char formats supported");
				errFlag = 1;
				return;
			} catch(NumberFormatException ee) {
				rI.setRegisterDataType(1);
				rI.setRegisterContent(in.charAt(0));
			}
			rI.setRegisterContent(in.charAt(0));
			rI.setRegisterDataType(1);
		}
		if(argument.equals("r0")) {
			cpu.r0.setRegisterDataType(rI.getRegisterDataType());
			cpu.r0.setRegisterContent(rI.getRegisterContent());
		} else if(argument.equals("r1")) {
			cpu.r1.setRegisterDataType(rI.getRegisterDataType());
			cpu.r1.setRegisterContent(rI.getRegisterContent());
		} else if(argument.equals("r2")) {
			cpu.r2.setRegisterDataType(rI.getRegisterDataType());
			cpu.r2.setRegisterContent(rI.getRegisterContent());
		} else if(argument.equals("r3")) {
			cpu.r3.setRegisterDataType(rI.getRegisterDataType());
			cpu.r3.setRegisterContent(rI.getRegisterContent());
		}
	}

	public static void outputValue(String argument) {
		if(argument.equals("r0"))
			cpu.r0.infoDump();
		else if(argument.equals("r1"))
			cpu.r1.infoDump();
		else if(argument.equals("r2"))
			cpu.r2.infoDump();
		else if(argument.equals("r3"))
			cpu.r3.infoDump();
		else if(argument.equals("pc"))
			cpu.pc.infoDump();
		else if(argument.replace("[", "").replace("]", "").equals("r0"))
			System.out.println("Value on address 0x" + cpu.r0.getRegisterContent() + ": " + memory.readLong(cpu.r0.getRegisterContent()));
		else if(argument.replace("[", "").replace("[", "").equals("r1"))
			System.out.println("Value on address 0x" + cpu.r1.getRegisterContent() + ": " + memory.readLong(cpu.r1.getRegisterContent()));
		else if(argument.replace("[", "").replace("[", "").equals("r2"))
			System.out.println("Value on address 0x" + cpu.r2.getRegisterContent() + ": " + memory.readLong(cpu.r2.getRegisterContent()));
		else if(argument.replace("[", "").replace("[", "").equals("r3"))
			System.out.println("Value on address 0x" + cpu.r3.getRegisterContent() + ": " + memory.readLong(cpu.r3.getRegisterContent()));
		else if(argument.substring(0,3).equals("[0x") && argument.charAt(argument.length()-1)==']') {
			try {
				long adr = Long.parseLong(argument.replace("[0x", "").replace("]", ""));
				System.out.println("Value on address 0x" + adr + ": " + memory.readLong(adr));
				return;
			} catch(NumberFormatException e) {
				System.out.println("shell: Address not of long type");
				errFlag = -1;
				return;
			}
		} else {
			System.out.println("shell: register " + argument + " does not exist");
			errFlag = 1;
		}
	}

	public static void printHelp() {
		System.out.println("TODO: Help");
		System.out.println("General commands:");
		System.out.println("\tpoweroff");
		System.out.println("\thalt - halts CPU");
		System.out.println("\tresume - resumes CPU when halted\n");
		System.out.println("File related commands:");
		System.out.println("\tfl - loads file from input directory. e.g. fl test.txt");
		System.out.println("\tcat - prints all instructions from file");
		System.out.println("\tstep(s) - executes instruction program counter points to in file");
		System.out.println("\trun - runs entire user program from file");
		System.out.println("Jumps (can be used only in file, and not directly)");
		System.out.println("\tjmp, je, jne, jg, jge, jl, jle - all take one parametar, line to jump to\n");
		System.out.println("Registers commands:");
		System.out.println("\tout r - prints data about register r. e.g. out r0");
		System.out.println("\t\tor out [0x1000] to print data from address");
		System.out.println("\tin r - writes user input in register r. e.g. int r0");
		System.out.println("\t\tor in [0x1000] to input data in address");
		System.out.println("\tmov r1, r2 - writes content of register r2 into register r1");
		System.out.println("\t\tmov can also use addresses, e.g. mov [0x1000], [r1]");
		System.out.println("\tadd r1, r2 - adds registers r1 and r2, and stores result in r1");
		System.out.println("\tsub r1, r2 - subtracts registers r1 and r2, and stores result in r1");
		System.out.println("\tdiv r1, r2 - divides registers r1 and r2, and stores result in r1");
		System.out.println("\tmul r1, r2 - multiplies registers r1 and r2, and stores result in r1");
		System.out.println("\tand r1, r2 - bitwise and operation on registers r1 and r2, and stores result in r1");
		System.out.println("\tor r1, r2 - bitwise or operation on registers r1 and r2, and stores result in r1");
		System.out.println("\txor r1, r2 - bitwise xor operation on registers r1 and r2, and stores result in r1");
		System.out.println("\tnot r - bitwise not operatio on register r\n");
	}

	public static void printWelcomeMessage() {
		System.out.print("\033[H\033[2J");
		System.out.println();
		System.out.println("\tWELCOME TO");
		System.out.println();
		System.out.println();
		System.out.println("                         ______ .______    __    __                                    ");
        System.out.println("                        /      ||   _  \\  |  |  |  |                                   ");
        System.out.println("                       |  ,----'|  |_)  | |  |  |  |                                   ");
        System.out.println("                       |  |     |   ___/  |  |  |  |                                   ");
        System.out.println("                       |  `----.|  |      |  `--'  |                                   ");
        System.out.println("                        \\______|| _|       \\______/                                    ");
        System.out.println("                                                                                      ");
        System.out.println(" _______ .___  ___.  __    __   __          ___   .___________.  ______   .______      ");
        System.out.println("|   ____||   \\/   | |  |  |  | |  |        /   \\  |           | /  __  \\  |   _  \\     ");
        System.out.println("|  |__   |  \\  /  | |  |  |  | |  |       /  ^  \\ `---|  |----`|  |  |  | |  |_)  |    ");
        System.out.println("|   __|  |  |\\/|  | |  |  |  | |  |      /  /_\\  \\    |  |     |  |  |  | |      /     ");
        System.out.println("|  |____ |  |  |  | |  `--'  | |  `----./  _____  \\   |  |     |  `--'  | |  |\\  \\----.");
        System.out.println("|_______||__|  |__|  \\______/  |_______/__/     \\__\\  |__|      \\______/  | _| `._____|");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("To see available commands enter: help");
	}
}
