package com.example.psswd.Client.generator;

import java.util.Random;

/**
 * Class for password generator
 */
public class PasswordGenerator {

    /**
     * Generates random string for password
     * @param hasCapitals true if password should contain capital letters
     * @param hasNumbers true if password should contain numbers
     * @param hasSymbols true if password should contain special symbols (#$&@!+=?)
     * @param numOfChars number of characters in output password
     * @return random password
     */
    public static String generatePassword(boolean hasCapitals, boolean hasNumbers, boolean hasSymbols, double numOfChars) {
        // character pools for password generator
        String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String specialCharacters = "#$&@!+=?";

        // final pool to be used in generator
        StringBuilder characterPool = new StringBuilder(lowercaseLetters);
        if (hasCapitals) {
            characterPool.append(uppercaseLetters);
        }
        if (hasNumbers) {
            characterPool.append(digits);
        }
        if (hasSymbols) {
            characterPool.append(specialCharacters);
        }

        // checks if pool is not empty
        if (characterPool.isEmpty()) {
            throw new IllegalStateException("Character pool is empty. Check the input parameters.");
        }

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < numOfChars; i++) {
            int randomIndex = random.nextInt(characterPool.length());
            password.append(characterPool.charAt(randomIndex));
        }

        return password.toString();
    }
}
