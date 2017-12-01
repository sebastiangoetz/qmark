package org.tud.qmark.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.jboss.security.Base64Utils;

public class HashUtil
{
    public static PasswordHash generateHash(String password) throws NoSuchAlgorithmException,
            UnsupportedEncodingException
    {
        PasswordHash ph = new PasswordHash();
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] bSalt = new byte[8];
        random.nextBytes(bSalt);
        byte[] bDigest = getHash(1, password, bSalt);
        ph.hash = byteToBase64(bDigest);
        ph.salt = byteToBase64(bSalt);

        return ph;
    }

    public static boolean checkHash(String password, String salt, String oldHash) throws NoSuchAlgorithmException,
            IOException
    {
        boolean result = false;

        byte[] bSalt = base64ToByte(salt);
        
        byte[] bDigest = getHash(1, password, bSalt);
        String hash = byteToBase64(bDigest);

        if(hash.equals(oldHash))
            result = true;
        
        return result;
    }

    /**
     * From a base 64 representation, returns the corresponding byte[] 
     * @param data String The base64 representation
     * @return byte[]
     * @throws IOException
     */
    private static byte[] base64ToByte(String data) throws IOException
    {
        return Base64Utils.fromb64(data);
    }

    /**
     * From a byte[] returns a base 64 representation
     * @param data byte[]
     * @return String
     * @throws IOException
     */
    private static String byteToBase64(byte[] data)
    {
        return Base64Utils.tob64(data);
    }

    /**
     * @param iterationNb int The number of iterations of the algorithm
     * @param password String The password to encrypt
     * @param salt byte[] The salt
     * @return byte[] The digested password
     * @throws NoSuchAlgorithmException If the algorithm doesn't exist
     * @throws UnsupportedEncodingException 
     */
    public static byte[] getHash(int iterationNb, String password, byte[] salt)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        for(int i = 0; i < iterationNb; i++)
        {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
    }
}
