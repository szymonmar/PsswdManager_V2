package com.example.psswd.Server.crypto;

import com.example.psswd.CommPsswd;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;

/**
 * Represents crypto controller used to encrypt passwords stored in the database
 */
public class CryptoController {

    /**
     * Name of the database (username)
     */
    private String databaseName;

    /**
     * Salt - random data added to password before hashing
     */
    private byte[] salt = generateRandom(20);

    /**
     * Secret key for encryption algorithm
     */
    private static SecretKey key;

    /**
     * Encryption algorithm
     */
    private static final String algorithm = "AES/CBC/PKCS5Padding";

    /**
     * Crypto Controller instance
     */
    private static CryptoController cryptoControllerInstance;

    private CryptoController() {}

    /**
     * Creates Crypto Controller instance if does not exist
     * and returns it
     * @return Crypto Controller instance
     */
    public static CryptoController getInstance(){
        if (cryptoControllerInstance == null) {
            cryptoControllerInstance = new CryptoController();
        }
        return cryptoControllerInstance;
    }

    /**
     * Deletes crypto controller instance
     */
    public static void deleteInstance() {
        cryptoControllerInstance = null;
    }

    /**
     * Sets database name (username) to the crypto controller
     * @param databaseName database name to the crypto controller
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Sets salt to the crypto controller
     * @param salt salt
     */
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    /**
     * @return salt
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Generates new private key from the passsword
     * @param password password for the generator
     * @throws NoSuchAlgorithmException if algorithm provided is invalid
     * @throws InvalidKeySpecException if key specification is invalid
     */
    public void initializeKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        key = new SecretKeySpec(secretKeyFactory.generateSecret(spec)
                .getEncoded(), "AES");
    }

    /**
     * Checks if challenge matches the database (used for checking the password to user account)
     * @param challenge byte array to be decrypted and compared to database name
     * @return true if decrypted challenge equals database name, false if not
     */
    public boolean verify(byte[] challenge) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return databaseName.equals(decrypt(challenge));
    }

    /**
     * Encrypts a password
     * @param input password to be encrypted
     * @return array of bytes
     */
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

    /**
     * Decrypts a password
     * @param ciphertext array of bytes to be decrypted
     * @return decrypted password
     */
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

    /**
     * Generates an array of random bytes
     * @param len array length
     * @return array of random bytes
     */
    private static byte[] generateRandom(int len) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[len];
        random.nextBytes(bytes);
        return bytes;
    }

    /**
     * Decrypts a serializable list of passwords
     * @param passwords ArrayList of CommPsswd objects to decrypt
     * @return ArrayList of decrypted CommPsswds
     */
    public static ArrayList<CommPsswd> decryptPasswordsArray(ArrayList<CommPsswd> passwords) {
        for (CommPsswd password : passwords) {
            try {
                CryptoController cryptoController = CryptoController.getInstance();
                String passwordPlaintext = cryptoController.decrypt(password.getEncryptedPassword());
                password.setPassword(passwordPlaintext);
            } catch (Exception exception) {
                System.out.println("Failed to decrypt passwords.");
            }
        }
        return passwords;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
