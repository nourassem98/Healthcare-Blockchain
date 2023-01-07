package Blocks;

import Enums.Gender;
import Security.RSA;

@SuppressWarnings("serial")
public class PatientBlock extends Block {
	private byte[] name;
	private byte[] age;
	private byte[] weight;
	private byte[] height;
	private byte[] sex;
	private byte[] pulse;
	private byte[] oxygenLevel;
	private byte[] glucoseLevel;
	private RSA rsa;

	public PatientBlock(int id, String previousHash, int patientID, String name, int age, double weight, double height,
			Gender sex, int pulse, int oxygenLevel, int glucoseLevel, byte[] signature, RSA rsa) {
		super(id, patientID, previousHash, signature);
		this.name = rsa.encryptMessage(name);
		this.age = rsa.encryptMessage(age + "");
		this.weight = rsa.encryptMessage(weight + "");
		this.height = rsa.encryptMessage(height + "");
		this.sex = rsa.encryptMessage(sex.toString());
		this.pulse = rsa.encryptMessage(pulse + "");
		this.oxygenLevel = rsa.encryptMessage(oxygenLevel + "");
		this.glucoseLevel = rsa.encryptMessage(glucoseLevel + "");
		this.rsa = rsa;
	}

	public String getName() {
		return rsa.decryptMessage(name);
	}

	public int getAge() {
		return Integer.parseInt(rsa.decryptMessage(age));
	}

	public double getWeight() {
		return Double.parseDouble(rsa.decryptMessage(weight));
	}

	public double getHeight() {
		return Double.parseDouble(rsa.decryptMessage(height));
	}

	public Gender getSex() {
		String sexstring = rsa.decryptMessage(sex);
		if(sexstring.equals("MALE"))
			return Gender.MALE;
		return Gender.FEMALE;
	}

	public int getPulse() {
		return Integer.parseInt(rsa.decryptMessage(pulse));
	}

	public int getOxygenLevel() {
		return Integer.parseInt(rsa.decryptMessage(oxygenLevel));
	}

	public int getGlucoseLevel() {
		return Integer.parseInt(rsa.decryptMessage(glucoseLevel));
	}

}
