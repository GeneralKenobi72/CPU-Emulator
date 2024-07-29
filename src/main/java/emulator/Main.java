package emulator;

import cpu.*;
import java.util.Scanner;
import java.io.*;
import java.util.*;

public class Main {
	public static CPU cpu = new CPU();

	public static ProgramCounterRegister pc = new ProgramCounterRegister("pc", 0);

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
			else if(strings[0].equals("run"))
				runCodeFromFile();
			else if(strings[0].equals("step") || strings[0].equals("s"))
				nextLineFromFile();
			else if(strings[0].equals("cat"))
				catFile();
			else
				System.out.println("shell: Command doesn't exist");
		} else if(strings.length == 2) {
			if(strings[0].equals("fl"))
				loadFile(strings[1]);
			else if(strings[0].equals("out"))
				outputRegister(strings[1]);
			else if(strings[0].equals("in"))
				inputRegister(strings[1]);
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

	public static void loadFile(String fileName) {
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
			pc.setRegisterContent(pc.getRegisterContent() + 1);
			fileLoaded = true;
		}catch(IOException e) {
			System.out.println("shell: file does not exist");
		}
	}

	public static void catFile() {
		if(linesFromFile.isEmpty()) {
			System.out.println("shell: file empty or not loaded");
			errFlag = 1;
			return;
		}
		for(int i=0;i<linesFromFile.size();i++) {
			if(i == pc.getRegisterContent()-1)
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
			jumpWasSuccessful = pc.jumpToInstruction(lineNum, linesFromFile.size());
		else if(compareFlag !=-2) {
			if(jump.equals("je")) {
				if(compareFlag == 0)
					jumpWasSuccessful = pc.jumpToInstruction(lineNum, linesFromFile.size());
			} else if(jump.equals("jne")) {
				if(compareFlag != 0)
					jumpWasSuccessful = pc.jumpToInstruction(lineNum, linesFromFile.size());
			} else if(jump.equals("jg")) {
				if(compareFlag == 1)
					jumpWasSuccessful = pc.jumpToInstruction(lineNum, linesFromFile.size());
			} else if(jump.equals("jl")) {
				if(compareFlag == -1)
					jumpWasSuccessful = pc.jumpToInstruction(lineNum, linesFromFile.size());
			} else if(jump.equals("jge")) {
				if(compareFlag >= 0)
					jumpWasSuccessful = pc.jumpToInstruction(lineNum, linesFromFile.size());
			} else if(jump.equals("jle")) {
				if(compareFlag <= 0)
					jumpWasSuccessful = pc.jumpToInstruction(lineNum, linesFromFile.size());
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

	public static void nextLineFromFile() {
		if(!fileLoaded) {
			System.out.println("shell: File not loaded");
			return;
		}
		scanInput(linesFromFile.get((int)pc.getRegisterContent() - 1));
		if(errFlag == 1) {
			System.out.println("shell: Error on line " + pc.getRegisterContent() + " resetting context, exiting file");
			resetContext();
			return;
		}
		pc.nextInstruction(linesFromFile.size());
	}

	public static void haltCPU() {
		System.out.println("Halting CPU, enter resume to exist halted state");
		Scanner scanHalt = new Scanner(System.in);
		String resume = "";
		while(!resume.equals("resume"))
			resume = scanHalt.nextLine();
		System.out.println("Exiting halted state");
	}

	public static void runCodeFromFile() {
		System.out.println("TODO: Run code from file");
	}

	public static void resetContext() { // Resets all registers, and loaded lines file.
		cpu.r0.setRegisterContent(0);
		cpu.r1.setRegisterContent(0);
		cpu.r2.setRegisterContent(0);
		cpu.r3.setRegisterContent(0);
		pc.setRegisterContent(0);
		linesFromFile.clear();
		fileLoaded = false;
	}

	public static int scanForErrorsInFile() { // Returns -1 if there are no errors, else line in which error occured
		System.out.println("TODO: Scan for errors in file");
		return -1;
	}

	public static void inputRegister(String inputRegister) {
		if(!inputRegister.equals("r0") && !inputRegister.equals("r1") && !inputRegister.equals("r2") && !inputRegister.equals("r3") && !inputRegister.equals("pc")) {
			System.out.println("shell: register " + inputRegister + " does not exist");
			errFlag = 1;
			return;
		}
		if(inputRegister.equals("pc")) {
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
		if(inputRegister.equals("r0")) {
			cpu.r0.setRegisterDataType(rI.getRegisterDataType());
			cpu.r0.setRegisterContent(rI.getRegisterContent());
		} else if(inputRegister.equals("r1")) {
			cpu.r1.setRegisterDataType(rI.getRegisterDataType());
			cpu.r1.setRegisterContent(rI.getRegisterContent());
		} else if(inputRegister.equals("r2")) {
			cpu.r2.setRegisterDataType(rI.getRegisterDataType());
			cpu.r2.setRegisterContent(rI.getRegisterContent());
		} else if(inputRegister.equals("r3")) {
			cpu.r3.setRegisterDataType(rI.getRegisterDataType());
			cpu.r3.setRegisterContent(rI.getRegisterContent());
		}
	}

	public static void outputRegister(String outputRegister) {
		if(outputRegister.equals("r0"))
			cpu.r0.infoDump();
		else if(outputRegister.equals("r1"))
			cpu.r1.infoDump();
		else if(outputRegister.equals("r2"))
			cpu.r2.infoDump();
		else if(outputRegister.equals("r3"))
			cpu.r3.infoDump();
		else if(outputRegister.equals("pc"))
			pc.infoDump();
		else {
			System.out.println("shell: register " + outputRegister + " does not exist");
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
		System.out.println("\trun - executes whole program from file");
		System.out.println("Jumps (can be used only in file, and not directly)");
		System.out.println("\tjmp, je, jne, jg, jge, jl, jle - all take one parametar, line to jump to\n");
		System.out.println("Registers commands:");
		System.out.println("\tout r - prints data about register r. e.g. out r0");
		System.out.println("\tin r - writes user input in register r. e.g. int r0");
		System.out.println("\tmov r1, r2 - writes content of register r2 into register r1");
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
