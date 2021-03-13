package com.yq.symmetric;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by EricYang on 2021/3/13.
 */

public class D3ESDemo {
    public static void main(String[] argv) throws Exception {
        ThreeDESECBKeyGenerator keyGenerator = new ThreeDESECBKeyGenerator();
        String newKey = keyGenerator.get3DESKey();
        System.out.println("newKey:" + newKey);
        ThreeDESECBUtil threeDES = new ThreeDESECBUtil();
        //String key = "Abc123%^&";
        //3des秘钥有长度要求的	168，112或56 位(对应密钥选项 1, 2, 3)
        String key = "wc3l+2LxAiYLqAtAx0klehDqKbVwduky";

        String plaintext = "a1b2c3&*()123456";
        byte[] encryptedBytes = threeDES.doEncryption(plaintext, key);
        System.out.println("plaintext String:" + plaintext);
        System.out.println("Encrypted bytes:" + encryptedBytes);
        System.out.println("Encrypted String:" + Base64.encodeBase64String(encryptedBytes));
        System.out.println("Decrypted String:" + threeDES.doDecryption(encryptedBytes, key));

    }
}
