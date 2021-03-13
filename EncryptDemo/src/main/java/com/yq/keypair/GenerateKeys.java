package com.yq.keypair;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;


public class GenerateKeys {
	private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;

	public GenerateKeys(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
		this.keyGen = KeyPairGenerator.getInstance("RSA");
		this.keyGen.initialize(keylength);
	}

	public void createKeys() {
		this.pair = this.keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

    public String getPrivateKeyString() {
        return Base64.encodeBase64String(this.privateKey.getEncoded());
    }

    public String getPublicKeyString() {
        return Base64.encodeBase64String(this.publicKey.getEncoded());
    }

    public void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();
		System.out.println("keyFilePath:" + f.getAbsolutePath());

		FileOutputStream fos = new FileOutputStream(f);
		fos.write(key);
		fos.flush();
		fos.close();
	}

	public static void main(String[] args) {
		GenerateKeys gk;
		try {
			gk = new GenerateKeys(1024);
            gk.createKeys();
            gk.writeToFile("KeyPair/publicKey", gk.getPublicKey().getEncoded());
			gk.writeToFile("KeyPair/privateKey", gk.getPrivateKey().getEncoded());

			System.out.println("privateKey:" + gk.getPrivateKeyString());
            System.out.println("publicKey:" + gk.getPublicKeyString());
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}
}
