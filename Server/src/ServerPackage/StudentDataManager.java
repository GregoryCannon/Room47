package ServerPackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Greg on 12/2/18.
 */
public class StudentDataManager {
    private HashMap<String, String> fullNamesById;
    private HashMap<String, String> emailById;
    private RedisDB redis;

    public StudentDataManager(RedisDB parentRedis){
        fullNamesById = new HashMap<>();
        emailById = new HashMap<>();
        redis = parentRedis;

        // Read student data
        try {
            readStudentData("Server/TestStudentData.txt");
            readStudentData("Server/ValidStudentData.txt");

        } catch (IOException e) {
            e.printStackTrace();
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

    public boolean isValidEmail(String email){
        return emailById.containsValue(email);
    }

    public String getStudentFullName(String studentId){
        if (fullNamesById.containsKey(studentId)) {
            return fullNamesById.get(studentId);
        } else {
            return null;
        }
    }

    private boolean addStudentToMap(String rawLine){

        String[] chunks = rawLine.split("/",4);

        if (chunks.length != 4) return false;

        String studentId = chunks[1];
        String fullName = chunks[0];
        String accessLevel = chunks[2];
        String email = chunks[3];

        fullNamesById.put(studentId, fullName);
        emailById.put(studentId, email);
        if(accessLevel.equals("A")) {
            redis.addAdmin(fullName);
        }

        return true;
    }
}