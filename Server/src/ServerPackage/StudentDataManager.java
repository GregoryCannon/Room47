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

    public StudentDataManager(){
        fullNamesById = new HashMap<>();

        // Read student data
        try {
            readStudentData("Server/TestStudentData.txt");
            readStudentData("Server/ValidStudentData.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readStudentData(String filename) throws IOException {
        System.out.println(filename);
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
        String isAdmin = chunks[2];

        fullNamesById.put(studentId, fullName);
        if(isAdmin.equals("A")) {
            ServerActor.getRedisInstance().addAdmin(fullName);
        }

        return true;
    }
}