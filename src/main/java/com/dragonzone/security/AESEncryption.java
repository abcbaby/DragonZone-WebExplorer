package com.dragonzone.security;

import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * http://www.digizol.org/2009/10/java-encrypt-decrypt-jce-salt.html
 * Modified from original author.
 * Using Advanced Encryption Standard (AES) Algorithm.
 * Key should be stored here & salt should come from user, like using username.
 */
public final class AESEncryption {

    private static final String KEY_VALUE = "RFI-Forum-Key123"; // needs to be 16 bytes long for 128-bit encryption.
    private static final String ENCODING_SCHEME = "UTF-8";
    private static final String ALGORITHM = "AES";
    private static final int ITERATIONS = 2; 
    
    private AESEncryption() {
    }
    
    public static String encrypt(String value, String salt) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);  
        c.init(Cipher.ENCRYPT_MODE, key);
  
        String eValue = value;
        for (int i = 0; i < ITERATIONS; i++) {
            String valueToEnc = salt + eValue;
            byte[] encValue = c.doFinal(valueToEnc.getBytes(ENCODING_SCHEME));
            eValue = Base64.encodeBase64String(encValue);
        }
        return eValue;
    }

    public static String decrypt(String value, String salt) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
  
        String dValue = null;
        String valueToDecrypt = value;
        for (int i = 0; i < ITERATIONS; i++) {
            byte[] decordedValue = Base64.decodeBase64(valueToDecrypt);
            byte[] decValue = c.doFinal(decordedValue);
            dValue = new String(decValue, ENCODING_SCHEME).substring(salt.length());
            valueToDecrypt = dValue;
        }
        return dValue;
    }

    private static Key generateKey() throws Exception {
        return new SecretKeySpec(getKeyValue(), ALGORITHM);
    }

    private static byte[] getKeyValue() throws Exception {
        return KEY_VALUE.getBytes(ENCODING_SCHEME);
    }

}