package Main;


import java.io.Serializable;

import Security.RSA;

@SuppressWarnings("serial")
public class HealthcareProfessional implements Serializable{
	/**
	 * 
	 */

	private String username;
	private String password;
	private String name;
	private int age;
	private String SSN;
	private String hospitalName;
	private RSA rsa;

	public HealthcareProfessional(String username, String password, String name, int age, String sSN, String hospitalName) {
		this.username=username;
		this.password=password;
		this.name = name;
		this.age = age;
		this.SSN = sSN;
		this.hospitalName = hospitalName;
		rsa = new RSA();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public String getSSN() {
		return SSN;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public RSA getRsa() {
		return rsa;
	}

}
