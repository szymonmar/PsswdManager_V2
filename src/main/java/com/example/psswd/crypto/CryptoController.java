package com.example.psswd.crypto;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class CryptoController {
    private String databaseName;
    private byte[] salt = generateRandom(20);
    private static SecretKey key;

    private static final String algorithm = "AES/CBC/PKCS5Padding";


    private static CryptoController cryptoControllerInstance;

    private CryptoController() {
    }

    public static CryptoController getInstance(){
        if (cryptoControllerInstance == null) {
            cryptoControllerInstance = new CryptoController();
        }
        return cryptoControllerInstance;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void initializeKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        key = new SecretKeySpec(secretKeyFactory.generateSecret(spec)
                .getEncoded(), "AES");
    }

    public boolean verify(byte[] challenge) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return databaseName.equals(decrypt(challenge));
    }

    public byte[] encrypt(String input) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance(algorithm);
        byte[] iv = generateRandom(16);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(cipherText);
        outputStream.write(iv);
        return outputStream.toByteArray();
    }

    public String decrypt(byte[] ciphertext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(algorithm);
        ByteBuffer byteBuffer = ByteBuffer.wrap(ciphertext);

        byte[] c = new byte[ciphertext.length - 16];
        byte[] iv = new byte[16];
        byteBuffer.get(c, 0, ciphertext.length - 16);
        byteBuffer.get(iv);

        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return new String(cipher.doFinal(c));
    }

    private static byte[] generateRandom(int len) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[len];
        random.nextBytes(bytes);
        return bytes;
    }
}
