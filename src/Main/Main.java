package Main;
// Java implementation to store

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// blocks in an ArrayList

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import Blocks.Block;
import Blocks.PatientBlock;
import Blocks.VisitBlock;
import Enums.Gender;
import Enums.Reason;
import Security.ZeroKnowledge;
import Security.crypt;

public class Main {
	
	private static ArrayList<ArrayList<Block>> blockchains = new ArrayList<ArrayList<Block>>();
	private static ArrayList<Block> blockchain = new ArrayList<Block>();
	private static ArrayList<String> usernames = new ArrayList<String>();
	private static ArrayList<String> passwords = new ArrayList<String>();
	private static ArrayList<HealthcareProfessional> professionals = new ArrayList<HealthcareProfessional>();
	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		try {
			loadUsers();
			loadBlockchain();
			checkBlockchains();
			while (true) {
				System.out.println("WELCOME TO THE Electronic Healthcare Records BLOCK CHAIN");
				System.out.println(""); // more data
				System.out.println("Please sign in by entering your username");
				String username1 = scanner.nextLine();
				System.out.println("Enter your password");
				String password1 = scanner.nextLine();

				String username2 = crypt.sha256(username1);
				String password2 = crypt.sha256(password1);
				while (userExists(username2, password2) == -1) {
					System.out.println("You have entered an incorrect login");
					System.out.println("Please re-enter your username");
					username1 = scanner.nextLine();
					System.out.println("Please re-enter your password");
					password1 = scanner.nextLine();
					username2 = crypt.sha256(username1);
					password2 = crypt.sha256(password1);
				}

				while (true) {
					HealthcareProfessional currentUser = professionals.get(userExists(username2, password2));
					System.out.println();
					System.out.println("Hello " + currentUser.getName() + ", If you want to:");
					System.out.println("	Register a new professional,		ENTER 1");
					System.out.println("	Register a new patient,			ENTER 2");
					System.out.println("	Register a new visit,			ENTER 3");
					System.out.println("	View info about a patient,		ENTER 4");
					System.out.println("	Log out,				ENTER 5");
					int in = takeInt("");
					if (in == 1) {
						System.out.println("Please enter your desired username");
						String username = scanner.nextLine();
						System.out.println("Please enter your desired password");
						String password = scanner.nextLine();
						System.out.println("Please enter your name");
						String name = scanner.nextLine();
						int age = takeInt("Please enter your age");
						System.out.println("Please enter your Social Security Number");
						String SSN = scanner.nextLine();
						System.out.println("Please enter the name of the hospital you currently work in");
						String hospital = scanner.nextLine();

						String secureUsername = crypt.sha256(username);
						String securePassword = crypt.sha256(password);

						if (usernameFound(secureUsername))
							System.out.println("ERROR: Username already found.");
						else {
							System.out.println("User registered successfuly");
							usernames.add(secureUsername);
							passwords.add(securePassword);
							HealthcareProfessional prof = new HealthcareProfessional(username, password, name, age, SSN,
									hospital);
							professionals.add(prof);
							writeNewProfessional(secureUsername, securePassword);
						}
						System.out.println();

					} else if (in == 2) {// ADDING NEW PATIENT
						int patientID =  takeInt("Enter the patient's ID");
						System.out.println("Enter the patient's Name");
						String name = scanner.nextLine();
						int age = takeInt("Enter the patient's age");
						double weight = takeDouble("Enter the patient's weight");
						double height = takeDouble("Enter the patient's height");
						String genderString;
						Gender gender = null;
						do {
							System.out.println("Enter the patient's gender, if male enter 1, if female enter 2");
							genderString = scanner.next();
							if (genderString.equals("1"))
								gender = Gender.MALE;
							else if (genderString.equals("2"))
								gender = Gender.FEMALE;
						} while (!genderString.equals("1") && !genderString.equals("2"));

						int pulse =  takeInt("Enter the patient's pulse rate");
						int oxygen =  takeInt("Enter the patient's oxygen level");
						int glucose =  takeInt("Enter the patient's glucose level");

						Block block = null;
						byte[] signature = null;
						if (blockchain.size() == 0) {
							signature = currentUser.getRsa().encryptMessage("" + patientID);
							block = new PatientBlock(blockchain.size() + 1, "0", patientID, name, age, weight, height,
									gender, pulse, oxygen, glucose, signature,currentUser.getRsa());
						} else {
							signature = currentUser.getRsa().encryptMessage("" + patientID);
							block = new PatientBlock(blockchain.size() + 1, blockchain.get(blockchain.size() - 1).hash,
									patientID, name, age, weight, height, gender, pulse, oxygen, glucose, signature,currentUser.getRsa());
						}

						for (int j = 0; j < blockchain.size(); j++) {
							int x = blockchain.get(j).uid;
							ZeroKnowledge zk = new ZeroKnowledge(x, blockchain.size() + 1);
							if (!zk.verify()) {
								System.out.println();
								System.out.println("The details of the patient already exists");
							}
						}
						insertInChain(block, currentUser);
						writeBlockChain();
					} else if (in == 3) {

						int patientID = takeInt("Enter the patient's ID");

						Reason reason = null;
						String reasonString = "";
						do {
							System.out.println(
									"Enter the reason for the visit, for checkup enter 1, for case management enter 2, for a complaint enter 3");
							reasonString = scanner.nextLine();
							if (reasonString.equals("1"))
								reason = Reason.CHECKUP;
							if (reasonString.equals("2"))
								reason = Reason.CASE_MANAGEMMENT;
							if (reasonString.equals("3"))
								reason = Reason.COMPLAINT;
						} while (!reasonString.equals("1") && !reasonString.equals("2") && !reasonString.equals("3"));

						System.out.println("Enter the patient's diagnosis");
						String diagnosis = scanner.nextLine();
						System.out.println("Enter the patient's prescription");
						String prescription = scanner.nextLine();

						byte[] signature = null;
						signature = currentUser.getRsa().encryptMessage("" + patientID);

						if (blockchain.size() == 0) {
							System.out.println(
									"ERROR: There are no registered patients in the blockchain, therefore you cannot add a visit");
						} else {
							insertInChain(
									new VisitBlock(blockchain.size() + 1, blockchain.get(blockchain.size() - 1).hash,
											patientID, reason, diagnosis, prescription, signature,currentUser.getRsa()),
									currentUser);
							writeBlockChain();
						}
					} else if (in == 4) {
						
						int patientID = takeInt("Enter the patient's ID");

						int n = checkIfMyPatient(patientID, currentUser);

						if (n == 0) {
							System.out.println("There is no patient with this ID");
						} else if (n == 1) {
							getVisits(patientID);
						} else {
							System.out.println(
									"This user was not registered by you, therefore you do not have the access to check his record");
						}

					} else if (in == 5) {
						System.out.println("Log Out Successful");
						System.out.println();
						currentUser = null;
						break;
					} else {
						System.out.println("ERROR: you can only choose 1, 2, 3, 4 or 5.");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}
	
	private static void checkBlockchains() {
		if(blockchains.size()>0) {
			blockchain = blockchains.get(0);	
		}
		for(int i=0; i<blockchains.size(); i++) {
			int count = 0;
			for(int j=0;j<blockchains.size(); j++) {
				if(i!=j) {
					if(blockchains.get(i).equals(blockchains.get(j))) {
						count ++;
					}
				}
			}
			if(count >= 5) {
				blockchain = blockchains.get(i);
			}
			else {
				blockchains.set(i, blockchain);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadBlockchain() {
		File file = new File("blockchain1.txt");

		try {
			if (file.createNewFile()) {
				for(int i=2;i<=10;i++) {
					File fileTemp = new File("blockchain"+i+".txt");
					fileTemp.createNewFile();
					ArrayList<Block> blockchainTemp = new ArrayList<Block>();
					blockchains.add(blockchainTemp);
				}
				blockchains.add(new ArrayList<Block>());
			} else {
				for(int i=1;i<=10;i++) {
					FileInputStream fis = new FileInputStream("blockchain"+i+".txt");
					@SuppressWarnings("resource")
					ObjectInputStream file3 = new ObjectInputStream(fis);
					blockchains.add((ArrayList<Block>) file3.readObject());	
				}
			}
		} catch (IOException e) {

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private static void writeNewProfessional(String secureUsername, String securePassword) {
		try {
			File file = new File("usernames.txt");
			FileWriter writer = new FileWriter(file, true);
			writer.write(System.getProperty("line.separator"));
			writer.write(secureUsername);
			writer.close();

			File file2 = new File("passwords.txt");
			FileWriter writer2 = new FileWriter(file2, true);
			writer2.write(System.getProperty("line.separator"));
			writer2.write(securePassword);
			writer2.close();

			FileOutputStream fos = new FileOutputStream("professionals.txt");
			ObjectOutputStream file3 = new ObjectOutputStream(fos);
			file3.writeObject(professionals);
			file3.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeBlockChain() {
		try {
			for(int i=1;i<=10;i++) {
				FileOutputStream fos = new FileOutputStream("blockchain"+i+".txt");
				ObjectOutputStream file3 = new ObjectOutputStream(fos);
				file3.writeObject(blockchain);
				file3.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private static void loadUsers() throws IOException {

		File file = new File("usernames.txt");
		File file2 = new File("passwords.txt");

		if (file.createNewFile()) {
			FileWriter writer = new FileWriter(file);
			String secureUsername = crypt.sha256("Nadeem");
			usernames.add(secureUsername);
			writer.write(secureUsername);
			writer.close();

			FileWriter writer2 = new FileWriter(file2);
			String securePassword = crypt.sha256("Admin");
			passwords.add(securePassword);
			writer2.write(securePassword);
			writer2.close();

			HealthcareProfessional prof = new HealthcareProfessional("Nadeem", "Admin", "Nadeem", 23, "26242901",
					"El gawy");
			professionals.add(prof);
			FileOutputStream fos = new FileOutputStream("professionals.txt");
			ObjectOutputStream file3 = new ObjectOutputStream(fos);
			file3.writeObject(professionals);
			file3.close();

			System.out.println("This is the first time this system is used, please enter the following");
			System.out.println("Username: Nadeem");
			System.out.println("Password: Admin");
			System.out.println();
		} else {
			Scanner x = new Scanner(file); // usernames
			while (x.hasNextLine()) {
				String data = x.nextLine();
				usernames.add(data);
			}
			x.close();

			Scanner x2 = new Scanner(file2); // passwords
			while (x2.hasNextLine()) {
				String data2 = x2.nextLine();
				passwords.add(data2);
			}
			x2.close();
			

			try {
				FileInputStream fis = new FileInputStream("professionals.txt");
				@SuppressWarnings("resource")
				ObjectInputStream file3 = new ObjectInputStream(fis);
				professionals = (ArrayList<HealthcareProfessional>) file3.readObject();
			} catch (Exception e) {
				// do nothing - this is the normal exit mechanism
			}
		}
	}

	public static void getVisits(int patientID) {

		Block currentBlock;
		int visitNumber = 1;
		for (int i = 0; i < blockchain.size(); i++) { // to check if the insertion is valid
			currentBlock = blockchain.get(i);
			if (currentBlock.getPatientID() == patientID && currentBlock instanceof VisitBlock) {
				VisitBlock v = (VisitBlock) currentBlock;
				System.out.println("Visit number " + visitNumber + ": On " + new Date(currentBlock.getTimeStamp())
						+ ". Reason: " + v.getReason() + ". diagnosis: " + v.getDiagnosis() + ". Prescription: "
						+ v.getPrescription());
				visitNumber++;
			}

		}
		if (visitNumber == 1)
			System.out.println("This patient does not have any visits yet.");
	}

	public static int checkIfMyPatient(int patientID, HealthcareProfessional currentUser) throws Exception {

		Block currentBlock;
		Block previousBlock;

		for (int i = 1; i < blockchain.size(); i++) { // to check if the chain is valid
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				throw new Exception("Hashes are not equal");
			}
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				throw new Exception("Previous Hashes are not equal");
			}
		}

		for (int i = 0; i < blockchain.size(); i++) { // to check if the insertion is valid
			currentBlock = blockchain.get(i);
			if (patientID == currentBlock.getPatientID()) {
				String s = currentUser.getRsa().decryptMessage(currentBlock.getSignature());

				if (s.equals("" + patientID)) {
					PatientBlock x = (PatientBlock) currentBlock;
					System.out.println("Patient's name is: " + x.getName() + ". Age: " + x.getAge()
							+ " years old. Weight: " + x.getWeight() + " kgs. Height: " + x.getHeight());

					return 1; // my patient
				} else {
					return 2; // not my patient
				}
			}
		}
		return 0; // patient ID doesnot exist
	}

	public static void insertInChain(Block block, HealthcareProfessional currentUser) throws Exception {

		Block currentBlock;
		Block previousBlock;

		for (int i = 1; i < blockchain.size(); i++) { // to check if the chain is valid
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				throw new Exception("Hashes are not equal");
			}
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				throw new Exception("Previous Hashes are not equal");
			}
		}

		for (int i = 0; i < blockchain.size(); i++) { // to check if the insertion is valid
			currentBlock = blockchain.get(i);
			if (block.getPatientID() == currentBlock.getPatientID()) {
				if (block instanceof PatientBlock && currentBlock instanceof PatientBlock) {
					System.out.println("There already exists a patient with this ID");
					return;
				} else if (block instanceof VisitBlock && currentBlock instanceof PatientBlock) { // la2eet el patient
					String s = currentUser.getRsa().decryptMessage(currentBlock.getSignature());

					if (s.equals("" + block.getPatientID())) {
						blockchain.add(block);
						System.out.println("Visit recorded successfully");
						return;
					} else {
						System.out.println("ERROR: you can only add a visit to a patient that you registered yourself");
						return;
					}
				}
			}
		}
		if (block instanceof PatientBlock) {
			blockchain.add(block);
			System.out.println("Patient added successfully");
			return;
		} else if (block instanceof VisitBlock) {
			System.out.println("Cannot insert this visit, you must enter a patient ID of an already registered patient");
			return;
		}
	}

	private static int userExists(String username1, String password1) {

		for (int i = 0; i < usernames.size(); i++) {
			if (username1.equals(usernames.get(i)) && password1.equals(passwords.get(i))) {
				return i;
			}
		}
		return -1;
	}

	public static boolean usernameFound(String x) {
		for (int i = 0; i < usernames.size(); i++) {
			if (usernames.get(i).equals(x))
				return true;
		}
		return false;
	}

	public static int takeInt(String x) {
		System.out.println(x);
		do {
			String in = scanner.nextLine();
			for (int i = 0; i < in.length(); i++) {
	             char c = in.charAt(i);
	             if (!(c>='0' && c<='9')) {
	            	 System.out.println("Please enter a number");
	            	 break;
	             }
	             if(i==in.length()-1) {
	            	 return Integer.parseInt(in);
	             }
			}
		} while (true);

	}
	
	public static double takeDouble(String x) {
		System.out.println(x);
		do {
			String in = scanner.nextLine();
			for (int i = 0; i < in.length(); i++) {
	             char c = in.charAt(i);
	             if (!((c>='0' && c<='9') || c=='.')) {
	            	 System.out.println("Please enter a number");
	            	 break;
	             }
	             if(i==in.length()-1) {
	            	 return  Double.parseDouble(in); 
	             }
			}
		} while (true);

	}
}