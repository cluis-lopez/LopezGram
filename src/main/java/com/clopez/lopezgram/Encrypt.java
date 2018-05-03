package com.clopez.lopezgram;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

public class Encrypt {

	public static String[] HashPasswd (String plainPasswd) throws NoSuchAlgorithmException {
		String[] ret = new String[2];
		byte[] salt = getSalt();
        ret[0] = get_SHA_256_SecurePassword(plainPasswd, salt);
        // Create a "printable" salt
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< salt.length ;i++)
        {
            sb.append(Integer.toString((salt[i] & 0xff) + 0x100, 16).substring(1));
        }
        ret[1] = sb.toString();
        return ret;
	}
	
	public static boolean CheckPasswd(String givenPassword, String storedPassword, String salt) {
		System.out.println("Password introducida: "+givenPassword);
		System.out.println("Password almacenada: "+ storedPassword);
		System.out.println("Sal :" + salt);
		byte [] bytes = new byte[salt.length()/2];
		for (int i = 0; i < salt.length(); i += 2) {
	        bytes[i / 2] = (byte) ((Character.digit(salt.charAt(i), 16) << 4)
	                             + Character.digit(salt.charAt(i+1), 16));
	    }
		String encryptPass = get_SHA_256_SecurePassword(givenPassword, bytes);
		System.out.println("Nueva password encriptada :"+ encryptPass);
		return (encryptPass.equals(storedPassword));
	}
	
	private static String get_SHA_256_SecurePassword(String passwordToHash, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
	
    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}
