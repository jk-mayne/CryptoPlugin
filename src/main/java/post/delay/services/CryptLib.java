package post.delay.services;

//import android.content.Context;
//import android.content.SharedPreferences;

import post.delay.services.AesGcm256;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
/* loaded from: classes.dex */
public class CryptLib {
    static String strVector = "";
    static String HexMyKey = "SOMEKEYINHEX"; //replace me!!!
    static String HexIv = "MyIVInHex";
    //static String data = "4e70ZD20TesUyh11Scb+F9dycjtZzfuG6g==";

    String IMEINO;
    Cipher _cx;
    byte[] _iv;
    byte[] _key;
    //SharedPreferences sharedPreferences;

    /* loaded from: classes.dex */
    private enum EncryptMode {
        ENCRYPT,
        DECRYPT
    }

    public String encrypt(String str) throws InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException {
        return AesGcm256.encrypt(str, AesGcm256.HexToByte(HexMyKey), AesGcm256.HexToByte(HexIv));
    }

    public String decrypt(String str) throws InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException {
        return AesGcm256.decrypt(str, AesGcm256.HexToByte(HexMyKey), AesGcm256.HexToByte(HexIv));
    }
}
