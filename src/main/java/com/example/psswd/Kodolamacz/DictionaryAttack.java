package com.example.psswd.Kodolamacz;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

    public class DictionaryAttack {
        private static final String DICTIONARY_FILE = "D://projekt java/Kodolamacz/Kodolamacz/beznazwy-kopia.txt";
        private static final int THREAD_COUNT = 5;

        public DictionaryAttack() {
        }

        public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
            DatabaseHandler db = new DatabaseHandler();
            db.initialize();
            long numOfLines = db.getNumOfLines();
            String targetPassword = db.getWord((long)(Math.random() * (double)numOfLines));
            byte[] hashedPassword = hashPassword(targetPassword);
            System.out.println("Hasło do złamania: " + targetPassword);
            System.out.println("Zahashowane hasło: " + Arrays.toString(hashedPassword));
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            long startTime = System.currentTimeMillis();
            long chunkSize = numOfLines / THREAD_COUNT;

            for(int i = 0; i < THREAD_COUNT; ++i) {
                long start = (long)i * chunkSize;
                long end = i == THREAD_COUNT - 1 ? numOfLines : (long)(i + 1) * chunkSize;
                PasswordCracker pc = new PasswordCracker(start, end, hashedPassword, startTime, executor);
                executor.execute(pc);
            }

            executor.shutdown();
            db.dbShutdown();
            executor.awaitTermination(3L, TimeUnit.SECONDS);
        }

        private static byte[] hashPassword(String password) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            return hashedPassword;
        }

        private static class PasswordCracker implements Runnable {
            private long start;
            private long end;
            private final byte[] hashedPassword;
            private final long startTime;
            private final ExecutorService executor;
            private DatabaseHandler dbh = new DatabaseHandler();

            PasswordCracker(long start, long end, byte[] hashedPassword, long startTime, ExecutorService executor) {
                this.start = start;
                this.end = end;
                this.hashedPassword = hashedPassword;
                this.startTime = startTime;
                this.executor = executor;
                this.dbh.initialize();
            }

            public void run() {
                try {
                    for(long i = this.start; i <= this.end; ++i) {
                        String password = this.dbh.getWord(i);
                        byte[] temp = this.hashPassword(password);
                        if (Arrays.equals(temp, this.hashedPassword)) {
                            long endTime = System.currentTimeMillis();
                            System.out.println("Hasło znalezione: " + password);
                            long var10001 = endTime - this.startTime;
                            System.out.println("Czas złamania hasła: " + var10001 + " ms");
                            this.executor.shutdownNow();
                            this.dbh.dbShutdown();
                            return;
                        }
                    }
                } catch (NoSuchAlgorithmException var7) {
                    NoSuchAlgorithmException e = var7;
                    e.printStackTrace();
                }

            }

            private byte[] hashPassword(String password) throws NoSuchAlgorithmException {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashedPassword = md.digest(password.getBytes());
                return hashedPassword;
            }
        }
    }
