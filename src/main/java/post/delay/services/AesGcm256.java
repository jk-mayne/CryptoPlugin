package post.delay.services;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Base64;

import org.apache.http.protocol.HTTP;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
/* loaded from: classes.dex */
public class AesGcm256 {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static int NonceBitSize = 128;
    public static int MacBitSize = 128;
    public static int KeyBitSize = 256;

    public AesGcm256() {
    }

    public static byte[] NewKey() {
        byte[] bArr = new byte[KeyBitSize / 8];
        SECURE_RANDOM.nextBytes(bArr);
        return bArr;
    }

    public static byte[] NewIv() {
        byte[] bArr = new byte[NonceBitSize / 8];
        SECURE_RANDOM.nextBytes(bArr);
        return bArr;
    }

    public static byte[] HexToByte(String str) {
        int length = str.length();
        byte[] bArr = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    public static String toHex(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            sb.append(Integer.toString(b, 16));
        }
        return sb.toString();
    }

    public static String encrypt(String str, byte[] bArr, byte[] bArr2) {
        try {
            byte[] bytes = str.getBytes(HTTP.UTF_8);
            GCMBlockCipher gCMBlockCipher = new GCMBlockCipher(new AESFastEngine());
            gCMBlockCipher.init(true, new AEADParameters(new KeyParameter(bArr), MacBitSize, bArr2, null));
            byte[] bArr3 = new byte[gCMBlockCipher.getOutputSize(bytes.length)];
            gCMBlockCipher.doFinal(bArr3, gCMBlockCipher.processBytes(bytes, 0, bytes.length, bArr3, 0));
            return Base64.getEncoder().encodeToString(bArr3);
        } catch (UnsupportedEncodingException | IllegalArgumentException | IllegalStateException | DataLengthException | InvalidCipherTextException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    public static String decrypt(String str, byte[] bArr, byte[] bArr2) {
        try {
            byte[] decode = Base64.getDecoder().decode(str);
            GCMBlockCipher gCMBlockCipher = new GCMBlockCipher(new AESFastEngine());
            gCMBlockCipher.init(false, new AEADParameters(new KeyParameter(bArr), MacBitSize, bArr2, null));
            byte[] bArr3 = new byte[gCMBlockCipher.getOutputSize(decode.length)];
            gCMBlockCipher.doFinal(bArr3, gCMBlockCipher.processBytes(decode, 0, decode.length, bArr3, 0));
            return new String(bArr3, Charset.forName(HTTP.UTF_8));
        } catch (IllegalArgumentException | IllegalStateException | DataLengthException | InvalidCipherTextException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }
}
