package com.yq.symmetric;

/**
 * Created by EricYang on 2021/3/13.
 */

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


class ThreeDESECBUtil {
    private Cipher cipher;

    public ThreeDESECBUtil() throws Exception {
        // Create the cipher
        cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
    }
    /*
     * 加密后密文不一定能按照string进行显示，一把会进行base64，然后保存到文件或者数据， 使用时
     */
    public byte[] doEncryption(String plaintext, String threeDESKeyBase64String) throws Exception {
        // Initialize the cipher for encryption
        final SecretKey key = new SecretKeySpec(Base64.decodeBase64(threeDESKeyBase64String), "DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        //sensitive information
        byte[] text = plaintext.getBytes();

        // Encrypt the text
        byte[] textEncrypted = cipher.doFinal(text);

        return(textEncrypted);
    }

    public String doDecryption(byte[] s, String threeDESKeyBase64String) throws Exception {
        // Initialize the same cipher for decryption
        final SecretKey key = new SecretKeySpec(Base64.decodeBase64(threeDESKeyBase64String), "DESede");
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Decrypt the text
        byte[] textDecrypted = cipher.doFinal(s);

        return(new String(textDecrypted));
    }
}

