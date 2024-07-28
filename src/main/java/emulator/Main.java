package emulator;

import cpu.*;
import java.util.Scanner;
import java.io.*;
import java.util.*;

public class Main {
	public static GeneralPurposeRegister r0 = new GeneralPurposeRegister("r0", 0);
	public static GeneralPurposeRegister r1 = new GeneralPurposeRegister("r1", 0);
	public static GeneralPurposeRegister r2 = new GeneralPurposeRegister("r2", 0);
	public static GeneralPurposeRegister r3 = new GeneralPurposeRegister("r3", 0);

	public static ProgramCounterRegister pc = new ProgramCounterRegister("pc", 0);

	public static boolean on = true;
	public static boolean fileLoaded = false;
	public static ArrayList<String> linesFromFile = new ArrayList<>();

	
	public static void main(String args[]) {
		printWelcomeMessage();
		while(on) {
			scanInput();
		}
	}

	public static void scanInput() { // TODO: Catch CTRL-C interrupt
		System.out.print(">");
		Scanner scan = new Scanner(System.in);
		String scanned;
		scanned = scan.nextLine();
		String[] strings = scanned.split(" ");
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
			int errorOnLine = scanForErrorsInFile();
			if(errorOnLine != -1) {
				System.out.println("Error on line" + errorOnLine + " File can't be opened");
				return;
			}
			pc.setRegisterContent(pc.getRegisterContent() + 1);
		}catch(IOException e) {
			System.out.println("File does not exist");
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
			regFirst = r0;
		else if(reg1.equals("r1"))
			regFirst = r1;
		else if(reg1.equals("r2"))
			regFirst = r2;
		else if(reg1.equals("r3"))
			regFirst = r3;
		else {
			System.out.println("Register " + reg1 + " does not exist");
			return;
		}
		if(reg2.equals("r0"))
			regSecond = r0;
		else if(reg2.equals("r1"))
			regSecond = r1;
		else if(reg2.equals("r2"))
			regSecond = r2;
		else if(reg2.equals("r3"))
			regSecond = r3;
		else {
			System.out.println("Register " + reg2 + " does not exist");
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
		else {
			System.out.println("shell: Command does not exist");
			return;
		}
		if(reg1.equals("r0"))
			r0 = regFirst;
		else if(reg1.equals("r1"))
			r1 = regFirst;
		else if(reg1.equals("r2"))
			r2 = regFirst;
		else
			r3 = regFirst;
	}

	public static void call3ArgCmdSecondLong(String cmd, String reg, long num) {
		GeneralPurposeRegister regFirst = new GeneralPurposeRegister();
		if(reg.equals("r0"))
			regFirst = r0;
		else if(reg.equals("r1"))
			regFirst = r1;
		else if(reg.equals("r2"))
			regFirst = r2;
		else if(reg.equals("r3"))
			regFirst = r3;
		else {
			System.out.println("Register " + reg + " does not exist");
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
		else {
			System.out.println("shell: Command does not exist");
			return;
		}
		if(reg.equals("r0"))
			r0 = regFirst;
		else if(reg.equals("r1"))
			r1 = regFirst;
		else if(reg.equals("r2"))
			r2 = regFirst;
		else
			r3 = regFirst;
	}

	public static void call3ArgCmdSecondChar(String cmd, String reg, char ch) {
		GeneralPurposeRegister regFirst = new GeneralPurposeRegister();
		if(reg.equals("r0"))
			regFirst = r0;
		else if(reg.equals("r1"))
			regFirst = r1;
		else if(reg.equals("r2"))
			regFirst = r2;
		else if(reg.equals("r3"))
			regFirst = r3;
		else {
			System.out.println("Register " + reg + " does not exist");
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
		else {
			System.out.println("shell: Command does not exist");
			return;
		}
		if(reg.equals("r0"))
			r0 = regFirst;
		else if(reg.equals("r1"))
			r1 = regFirst;
		else if(reg.equals("r2"))
			r2 = regFirst;
		else
			r3 = regFirst;
	}

	public static void callBitwiseNot(String reg) {
		if(reg.equals("r0"))
			r0.bitwiseNOTRegister();
		else if(reg.equals("r1"))
			r1.bitwiseNOTRegister();
		else if(reg.equals("r2"))
			r2.bitwiseNOTRegister();
		else if(reg.equals("r3"))
			r3.bitwiseNOTRegister();
		else {
			System.out.println("Register " + reg + " does not exist");
			return;
		}
	}

	public static void nextLineFromFile() {
		System.out.println("TODO: Load next line from file");
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
		r0.setRegisterContent(0);
		r1.setRegisterContent(0);
		r2.setRegisterContent(0);
		r3.setRegisterContent(0);
		pc.setRegisterContent(0);
		linesFromFile.clear();
		fileLoaded = false;
	}

	public static int scanForErrorsInFile() { // Returns -1 if there are no errors, else line in which error occured
		System.out.println("TODO: Scan for errors in file");
		return -1;
	}

	public static void inputRegister(String inputRegister) {
		if(!inputRegister.equals("r0") && !inputRegister.equals("r1") && !inputRegister.equals("r2") && !inputRegister.equals("r3")) {
			System.out.println("Register " + inputRegister + " does not exist");
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
			} catch(NumberFormatException ee) {
				return;
			}
			rI.setRegisterContent(in.charAt(0));
			rI.setRegisterDataType(1);
		}
		if(inputRegister.equals("r0")) {
			r0.setRegisterDataType(rI.getRegisterDataType());
			r0.setRegisterContent(rI.getRegisterContent());
		} else if(inputRegister.equals("r1")) {
			r1.setRegisterDataType(rI.getRegisterDataType());
			r1.setRegisterContent(rI.getRegisterContent());
		} else if(inputRegister.equals("r2")) {
			r2.setRegisterDataType(rI.getRegisterDataType());
			r2.setRegisterContent(rI.getRegisterContent());
		} else if(inputRegister.equals("r3")) {
			r3.setRegisterDataType(rI.getRegisterDataType());
			r3.setRegisterContent(rI.getRegisterContent());
		}
	}

	public static void outputRegister(String outputRegister) {
		if(outputRegister.equals("r0"))
			r0.infoDump();
		else if(outputRegister.equals("r1"))
			r1.infoDump();
		else if(outputRegister.equals("r2"))
			r2.infoDump();
		else if(outputRegister.equals("r3"))
			r3.infoDump();
		else
			System.out.println("Register " + outputRegister + " does not exist");
	}

	public static void printHelp() {
		System.out.println("TODO: Help");
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
