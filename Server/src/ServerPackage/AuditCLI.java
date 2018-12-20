package ServerPackage;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static ServerPackage.ServerTestAccountData.*;

/**
 * Created by Greg on 12/20/18.
 */
public class AuditCLI {
    static AuditLogDB auditLogDB;

    private static void makeDemoList() throws IOException {
        auditLogDB.registerLog(GREG_ID, GREG_USERNAME, "", "", "");
        auditLogDB.loginLog(GREG_ID, GREG_USERNAME, "", "", "");
        auditLogDB.selectRoomLog(GREG_ID, GREG_USERNAME, "", "Clark I", "117");

        auditLogDB.registerLog(SAM_ID, SAM_USERNAME, "", "", "");
        auditLogDB.loginLog(SAM_ID, SAM_USERNAME, "", "", "");
        auditLogDB.selectRoomLog(SAM_ID, SAM_USERNAME, "", "Clark I", "104");

        auditLogDB.displaceStudentLog(SAM_ID, SAM_USERNAME, ADMIN_USERNAME, "Clark I", "104");
        auditLogDB.placeStudentLog(JOSH_ID, JOSH_USERNAME, ADMIN_USERNAME, "Clark I", "104");
    }

    public static void main(String args[]) throws IOException {
        auditLogDB = new AuditLogDB("localhost", 6379);
        //makeDemoList();

        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("\n\nEnter the type of query you want to perform (or 'quit'):" +
                    "\nStudent" +
                    "\nAdmin" +
                    "\nRoom\n");
            String inputLine = scanner.nextLine();
            switch (inputLine.toLowerCase()) {
                case "student":
                    System.out.println("Enter the username of the student:");
                    String name = scanner.nextLine();
                    studentQuery(name);
                    break;
                case "admin":
                    System.out.println("Enter the username of the admin:");
                    String adminUsername = scanner.nextLine();
                    adminQuery(adminUsername);
                    break;
                case "room":
                    System.out.println("Enter the name of the dorm (e.g. 'Clark V'):");
                    String dormName = scanner.nextLine();
                    System.out.println("Enter the room number (e.g. 205):");
                    String roomNumber = scanner.nextLine();
                    roomQuery(dormName, roomNumber);
                    break;
                case "quit":
                    return;
                default:
                    System.out.println("Unknown query type. Please enter one of the listed options.");

            }
        }
    }

    private static void studentQuery(String username){
        try {
            List<AuditLogEntry> entries = auditLogDB.getLogsForStudent(username);
            for (AuditLogEntry entry : entries){
                System.out.println(entry);
            }
            if (entries.isEmpty()){
                System.out.println("\nNo entries found!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void adminQuery(String adminUsername){
        try {
            List<AuditLogEntry> entries = auditLogDB.getLogsForAdmin(adminUsername);
            for (AuditLogEntry entry : entries){
                System.out.println(entry);
            }
            if (entries.isEmpty()){
                System.out.println("\nNo entries found!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void roomQuery(String dormName, String roomNumber){
        try {
            List<AuditLogEntry> entries = auditLogDB.getLogsForRoom(dormName, roomNumber);
            for (AuditLogEntry entry : entries){
                System.out.println(entry);
            }
            if (entries.isEmpty()){
                System.out.println("\nNo entries found!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
