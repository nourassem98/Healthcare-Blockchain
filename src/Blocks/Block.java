package Blocks;
// Java implementation for creating
// a block in a Blockchain

import java.io.Serializable;
import java.util.Date;

import Security.crypt;

public abstract class Block implements Serializable{

	// Every block contains
	// a hash, previous hash and
	// data of the transaction made
	
	private static final long serialVersionUID = 6529685098267757692L;
    public int uid;

	private int patientID;
	public String hash;
	public String previousHash;
	
	private long timeStamp;
	private byte[] signature;

	// Constructor for the block
	public Block(int id,int patientID, String previousHash, byte[] signature) {
		this.patientID=patientID;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
		this.signature = signature;
        uid=id;
	}

	
	public String calculateHash() {
		String calculatedhash = crypt.sha256(previousHash + Long.toString(timeStamp));
		return calculatedhash;
	}


	public int getPatientID() {
		return patientID;
	}


	public byte[] getSignature() {
		return signature;
	}


	public long getTimeStamp() {
		return timeStamp;
	}
}