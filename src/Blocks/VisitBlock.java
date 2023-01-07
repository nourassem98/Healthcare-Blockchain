package Blocks;
import Enums.Reason;
import Security.RSA;

@SuppressWarnings("serial")
public class VisitBlock extends Block {
	private byte[] reason;
	private byte[] diagnosis;
	private byte[] prescription;
	private RSA rsa;

	public VisitBlock(int id, String previousHash, int patientID, Reason reason, String diagnosis, String prescription, byte[] signature, RSA rsa) {
		super(id,patientID, previousHash,signature);
		this.reason =  rsa.encryptMessage(reason.toString());
		this.diagnosis =  rsa.encryptMessage(diagnosis);
		this.prescription =  rsa.encryptMessage(prescription);
		this.rsa= rsa;
	}


	public Reason getReason() {
		String reasonstring = rsa.decryptMessage(reason);
		if(reasonstring.equals("CASE_MANAGEMMENT"))
			return Reason.CASE_MANAGEMMENT;
		else if(reasonstring.equals("CHECKUP"))
			return Reason.CHECKUP;
		else 
			return Reason.COMPLAINT;
	}

	public String getDiagnosis() {
		return rsa.decryptMessage(diagnosis);
	}

	public String getPrescription() {
		return rsa.decryptMessage(prescription);
	}
	

}
