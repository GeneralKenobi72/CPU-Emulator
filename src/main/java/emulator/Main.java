package emulator;

import cpu.*;
import java.util.Scanner;

public class Main {
	public static GeneralPurposeRegister r0 = new GeneralPurposeRegister("r0", 0);
	public static GeneralPurposeRegister r1 = new GeneralPurposeRegister("r1", 0);
	public static GeneralPurposeRegister r2 = new GeneralPurposeRegister("r2", 0);
	public static GeneralPurposeRegister r3 = new GeneralPurposeRegister("r3", 0);

	public static ProgramCounterRegister pc = new ProgramCounterRegister("pc", 0);

	public static boolean on = true;
	public static boolean fileLoaded = false;
	public static Scanner scan = new Scanner(System.in);
	
	public static void main(String args[]) {
		printWelcomeMessage();
		while(on) {
			scanInput();
		}
		scan.close();
	}

	public static void scanInput() { // TODO: Catch CTRL-C interrupt
		System.out.print(">");

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
			else if(strings[0].equals("continue") || strings[0].equals("c"))
				nextLineFromFile();
			else
				System.out.println("shell: Command doesn't exist");
		} else if(strings.length == 2) {
			System.out.println("shell: Command doesn't exist");
		} else if(strings.length == 3) {
			if(strings[0].equals("load") && strings[1].equals("file"))
				loadFile(strings[2]);
			else
				System.out.println("shell: Command doesn't exist");
		} else
			System.out.println("shell: Command doesn't exist");
	}

	public static void printHelp() {
		System.out.println("TODO: Help");
	}

	public static void loadFile(String fileName) {
		System.out.println("TODO: Load file");
	}

	public static void nextLineFromFile() {
		System.out.println("TODO: Load next line from file");
	}

	public static void haltCPU() {
		System.out.println("TODO: Halt CPU");
	}

	public static void runCodeFromFile() {
		System.out.println("TODO: Run code from file");
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
