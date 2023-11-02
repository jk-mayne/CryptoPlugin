package com.jkmayne.showmeburp;

import post.delay.services.CryptLib;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

//package post.delay.services;


public class showmecrypto {
    public static void main(String[] args) throws InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        //System.out.println("Test");
        //AesGcm256 x = new AesGcm256();
        CryptLib x = new CryptLib();
        try {
            //System.out.println(x.decrypt("4e70ZD20TesUyh11Scb+F9dycjtZzfuG6g=="));
            System.out.println("Plaintext: " + x.decrypt(args[0]));
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
