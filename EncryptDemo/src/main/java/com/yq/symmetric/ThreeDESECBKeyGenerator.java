package com.yq.symmetric;

/**
 * Created by EricYang on 2021/3/13.
 */

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class ThreeDESECBKeyGenerator {
    private KeyGenerator keyGenerator;
    private SecretKey desKey;
    private Cipher cipher;

    public ThreeDESECBKeyGenerator() throws Exception {
        // Generate the Key
        keyGenerator = KeyGenerator.getInstance("DESede");
        desKey = keyGenerator.generateKey();
    }

    public String get3DESKey() {
        return Base64.encodeBase64String(desKey.getEncoded());
    }
}

