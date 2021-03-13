package com.yq.asymmetric;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSADemo {
	private Cipher cipher;

	public RSADemo() throws NoSuchAlgorithmException, NoSuchPaddingException{
		this.cipher = Cipher.getInstance("RSA");
	}

	//https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
	/*
	 publicKey privateKey 都经过base64Encode存储的，需要先进行decode
	 */
	public PrivateKey getPrivateFromDB(String privateStr) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateStr);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}
	//https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
	public PublicKey getPublicFromDB(String publicStr) throws Exception {
		byte[] keyBytes =Base64.decodeBase64(publicStr);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	public String encryptText(String msg, PrivateKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException{
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.encodeBase64String(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
	}
	
	public String decryptText(String msg, PublicKey key) throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException{
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64.decodeBase64(msg)), StandardCharsets.UTF_8);
	}

	public static void main(String[] args) throws Exception {
		RSADemo demo = new RSADemo();
		String privateKeyStr = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ/6TlH1y+ex1rwE+TP7YgguL0aakJvVzmYzoEh7wFn6rUT9/S+ENY70biEHSAiJZlfus739AO6Jg6r5ZugI7Gty+a7I+IBV549D6dlH9B1LR0I2qVy86AZikF/+ZYD1VpncPpll89qKSwoEWRQTRyhJabAhw84AExm0ezxGJ2GZAgMBAAECgYAThVqbO7AG9LdsN/sksa8TqncQWXn92ggWhoQvdBJTqHgOLCQB8VMLv53Un+vVRtbbMgBy1XWQpe0QJuC8vaIDhQDyH5Y6pEw9PEIrvsPEx/OgyVkIGxXsO8DdUkSgo+fz+IyOTPSs+0j280gG65epxahd6MxGkjuny7T7tjNZlQJBANj4BU0vRc3SvittBowCRItcEYLPnwBOx2zqzWOTwB5FGXb97+iK9HLsRc2YiyNvgh7B8l6CiH0ZKulePDCKtVsCQQC8wbKvminmhUIEoIxn5ZGQXidbFksZFJP2OEgDNk5Q60h8ilvHxj9wJCUbOPUmTuHTpGTT/cdFolCke1u6mZMbAkB0EUIsxzQtZiarbniJH5Fxh8AqXU0uyfXvMzDKqzikOMzllfToTqV0cSbqVEdinn1aXOB+ZJAFdBg7nVjPkvRvAkEAk+nFuE26X+Y04aNd41zGQLdHZ92EY9b4S423AK1hmY+GcfGKEnaL57irSzXLBYXy+QJt8KGBslOLlhkEvKjz2QJBALmXXtXB7v5urVu88nO1Tv1vi2LoXHOOYhOuANJkjP0/g0LwGvW3okNy/roaJMtUdYNKtapj1obDwcw5OWrNsU8=";
		String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCf+k5R9cvnsda8BPkz+2IILi9GmpCb1c5mM6BIe8BZ+q1E/f0vhDWO9G4hB0gIiWZX7rO9/QDuiYOq+WboCOxrcvmuyPiAVeePQ+nZR/QdS0dCNqlcvOgGYpBf/mWA9VaZ3D6ZZfPaiksKBFkUE0coSWmwIcPOABMZtHs8RidhmQIDAQAB";
		String plaintext = "abcd1234!@#$";
		PrivateKey privateKey = demo.getPrivateFromDB(privateKeyStr);
		PublicKey publicKey = demo.getPublicFromDB(publicKeyStr);
		

		String encrypted_msg = demo.encryptText(plaintext, privateKey);
		String decrypted_msg = demo.decryptText(encrypted_msg, publicKey);
		System.out.println("Original Message: " + plaintext + "\nEncrypted Message: " + encrypted_msg + "\nDecrypted Message: " + decrypted_msg);

	}
}
