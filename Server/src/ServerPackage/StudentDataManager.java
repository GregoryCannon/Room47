package ServerPackage;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Greg on 12/2/18.
 */
public class StudentDataManager {
    private HashMap<String, String> fullNamesById;
    private RedisDB redis;
    private EncryptionManager encryptionManager;

    private static final String TEST_ENC = "Server/TestStudentData-Enc.txt";
    private static final String VALID_ENC = "Server/ValidStudentData-Enc.txt";
    private static final String TEST = "Server/TestStudentData.txt";
    private static final String VALID = "Server/ValidStudentData.txt";

    public StudentDataManager(RedisDB parentRedis, EncryptionManager parentEncManager){
        fullNamesById = new HashMap<>();
        redis = parentRedis;
        encryptionManager = parentEncManager;

        // TODO: remove plaintext versions for final release
        // Encrypt the text files if necessary
        if (!new File(TEST_ENC).exists() || !new File(VALID_ENC).exists())
        try {
            encryptFile(TEST, TEST_ENC);
            encryptFile(VALID, VALID_ENC);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read student data
        try {
            readEncryptedStudentData(TEST_ENC);
            readEncryptedStudentData(VALID_ENC);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encryptFile(String filename, String outFilename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFilename));
        try {
            String line;

            while ((line = br.readLine()) != null) {
                String cipherLine = encryptionManager.AESEncrypt(line);
                bw.write(cipherLine);
                bw.newLine();
            }
        } finally {
            br.close();
            bw.close();
        }
    }

    public void readEncryptedStudentData(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        try {
            String line;

            while ((line = br.readLine()) != null) {
                String plaintextLine = encryptionManager.AESDecrypt(line);
                addStudentToMap(plaintextLine);
            }
        } finally {
            br.close();
        }
    }

    public void readStudentData(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        try {
            StringBuilder sb = new StringBuilder();
            String line = "";

            while ((line = br.readLine()) != null) {
                addStudentToMap(line);
            }
        } finally {
            br.close();
        }
    }

    public boolean isValidStudentId(String studentId){
        return fullNamesById.containsKey(studentId);
    }

    public String getStudentFullName(String studentId){
        if (fullNamesById.containsKey(studentId)) {
            return fullNamesById.get(studentId);
        } else {
            return null;
        }
    }

    private boolean addStudentToMap(String rawLine){

        String[] chunks = rawLine.split("@",3);

        if (chunks.length != 3) return false;

        String studentId = chunks[1];
        String fullName = chunks[0];
        String accessLevel = chunks[2];

        fullNamesById.put(studentId, fullName);
        if(accessLevel.equals("A")) {
            redis.addAdmin(fullName);
        }

        return true;
    }
}