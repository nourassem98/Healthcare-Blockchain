package Security;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

@SuppressWarnings("serial")
public class RSA implements Serializable{
	private BigInteger P;
	private BigInteger Q;
	private BigInteger N;
	private BigInteger PHI;
	private BigInteger e;
	private BigInteger d;
	private int maxLength = 1024;
	private Random R;

	public RSA() {
		R = new Random();
		P = BigInteger.probablePrime(maxLength, R);
		Q = BigInteger.probablePrime(maxLength, R);
		N = P.multiply(Q);
		PHI = P.subtract(BigInteger.ONE).multiply(Q.subtract(BigInteger.ONE));
		e = BigInteger.probablePrime(maxLength / 2, R);
		while (PHI.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(PHI) < 0) {
			e.add(BigInteger.ONE);
		}
		d = e.modInverse(PHI);
	}

	public RSA(BigInteger e, BigInteger d, BigInteger N) {
		this.e = e;
		this.d = d;
		this.N = N;
	}

	// Encrypting the message
	public byte[] encryptMessage(String message1) {
		byte[] message = message1.getBytes();
		return (new BigInteger(message)).modPow(e, N).toByteArray();
	}

	// Decrypting the message
	public String decryptMessage(byte[] message) {
		return new String((new BigInteger(message)).modPow(d, N).toByteArray());
	}
}