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

/**
 * Klasa obsługująca szyfrowanie bazy danych z hasłami
 */
public class CryptoController {

    /**
     * Nazwa bazy danych (nazwa użytkownika)
     */
    private String databaseName;

    /**
     * Sól - losowe dane dodawane do hasła przed hashowaniem
     */
    private byte[] salt = generateRandom(20);

    /**
     * Klucz prywatny do algorytmu szyfrowania
     */
    private static SecretKey key;

    /**
     * Algorytm, którym baza danych będzie szyfrowana
     */
    private static final String algorithm = "AES/CBC/PKCS5Padding";

    /**
     * Instancja klasy CryptoController
     */
    private static CryptoController cryptoControllerInstance;

    /**
     * Konstruktor obiektu typu CryptoController (pusty)
     */
    private CryptoController() {
    }

    /**
     * Tworzy instancję klasy CryptoController
     * @return Instancja klasy [CryptoController]
     */
    public static CryptoController getInstance(){
        if (cryptoControllerInstance == null) {
            cryptoControllerInstance = new CryptoController();
        }
        return cryptoControllerInstance;
    }

    /**
     * Służy do ustawienia nazwy bazy danych (nazwy użytkownika)
     * @param databaseName Nowa nazwa [String]
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Służy do ustawienia sól
     * @param salt sól [ byte[] ]
     */
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    /**
     * Zwraca sól
     * @return salt [ byte[] ]
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Generuje nowy klucz prywatny do algorytmu szyfrowania dla danego hasła
     * @param password hasło do bazy danych
     * @throws NoSuchAlgorithmException jeżeli podany algorytm nie jest wspierany przez SecretKeyFactory
     * @throws InvalidKeySpecException jeżeli specyfikacja klucza podana do secretKeyFactory.generateSecret nie jest wspierana przez tę metodę
     */
    public void initializeKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        key = new SecretKeySpec(secretKeyFactory.generateSecret(spec)
                .getEncoded(), "AES");
    }

    /**
     * Sprawdza, czy hasło do bazy danych jest poprawne odkodowując challenge bazy danych używając klucza z hasła
     * @param challenge wartość do odkodowania, która zostanie porównana z nazwą bazy danych [ byte[] ]
     * @return true jeśli ciągi znaków są jednakowe (jeśli hasło poprawne), false jeśli niepoprawne
     * @throws NoSuchPaddingException jeśli transformacja zawiera nieobsługiwany schemat dopełniania bloków
     * @throws NoSuchAlgorithmException jeśli transformacja jest równa null, pusta, w nieprawidłowym formacie lub jeśli nie ma implementacji CipherSpi dla określonego algorytmu.
     * @throws InvalidKeyException jeśli klucz nie jest odpowiedni do uruchomienia tego Ciphera, lub długość klucza jest większa niż maksymalna
     * @throws InvalidAlgorithmParameterException jeśli podane parametry algorytmu są nieodpowiednie dla tego szyfru, albo ten szyfr wymaga parametrów algorytmu, które zostały podane jako null, albo podane parametry algorytmu sugerują siłę kryptograficzną, która przekracza dopuszczone limity
     * @throws IllegalBlockSizeException jeśli rozmiar bloku jest nieprawidłowy
     * @throws BadPaddingException jeśli odszyfrowane dane nie są ograniczone odpowiednimi bajtami dopełniającymi
     */
    public boolean verify(byte[] challenge) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return databaseName.equals(decrypt(challenge));
    }

    /**
     * Szyfruje hasło
     * @param input hasło do zaszyfrowania [String]
     * @return tablica znaków z zaszyfrowanym hasłem [ byte[] ]
     * @throws NoSuchPaddingException jeśli transformacja zawiera nieobsługiwany schemat dopełniania bloków
     * @throws NoSuchAlgorithmException jeśli transformacja jest równa null, pusta, w nieprawidłowym formacie lub jeśli nie ma implementacji CipherSpi dla określonego algorytmu.
     * @throws InvalidKeyException jeśli klucz nie jest odpowiedni do uruchomienia tego Ciphera, lub długość klucza jest większa niż maksymalna
     * @throws InvalidAlgorithmParameterException jeśli podane parametry algorytmu są nieodpowiednie dla tego szyfru, albo ten szyfr wymaga parametrów algorytmu, które zostały podane jako null, albo podane parametry algorytmu sugerują siłę kryptograficzną, która przekracza dopuszczone limity
     * @throws IllegalBlockSizeException jeśli rozmiar bloku jest nieprawidłowy
     * @throws BadPaddingException jeśli odszyfrowane dane nie są ograniczone odpowiednimi bajtami dopełniającymi
     * @throws IOException jeśli wystąpi błąd strumienia wejścia / wyjścia
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
     * Służy do odszyfrowania
     * @param ciphertext hasło do odszyfrowania
     * @return odszyfrowane hasło [String]
     * @throws NoSuchPaddingException jeśli transformacja zawiera nieobsługiwany schemat dopełniania bloków
     * @throws NoSuchAlgorithmException jeśli transformacja jest równa null, pusta, w nieprawidłowym formacie lub jeśli nie ma implementacji CipherSpi dla określonego algorytmu.
     * @throws InvalidKeyException jeśli klucz nie jest odpowiedni do uruchomienia tego Ciphera, lub długość klucza jest większa niż maksymalna
     * @throws InvalidAlgorithmParameterException jeśli podane parametry algorytmu są nieodpowiednie dla tego szyfru, albo ten szyfr wymaga parametrów algorytmu, które zostały podane jako null, albo podane parametry algorytmu sugerują siłę kryptograficzną, która przekracza dopuszczone limity
     * @throws IllegalBlockSizeException jeśli rozmiar bloku jest nieprawidłowy
     * @throws BadPaddingException jeśli odszyfrowane dane nie są ograniczone odpowiednimi bajtami dopełniającymi
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
     * Generuje listę losowych znaków (używany do generowania soli)
     * @param len Długość listy (ilość losowych znaków) [int]
     * @return Lista losowych znaków [ byte[] ]
     */
    private static byte[] generateRandom(int len) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[len];
        random.nextBytes(bytes);
        return bytes;
    }
}
