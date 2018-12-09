package com.room.draw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusActivity extends AppCompatActivity {

    @BindView(R.id.user_name)
    TextView _userName;
    @BindView(R.id.dorm_name)
    TextView _dormName;
    @BindView(R.id.dorm_room_number)
    TextView _dormRoomNumber;
    @BindView(R.id.reg_number)
    TextView _regNumber;
    @BindView(R.id.reg_time)
    TextView _regTime;
    @BindView(R.id.student_id)
    TextView _studentId;

    public static String [] userInfo=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        ButterKnife.bind(this);
        if (!userInfo[0].equals("-1")) {
            _userName.setText(userInfo[0]);
        }
        else {
            _userName.setText("No username");
        }
        if (!userInfo[1].equals("-1")) {
            _dormName.setText("Dorm: " + userInfo[1]);
        }
        else {
            _dormName.setText("No dorm selected");
        }
        if (!userInfo[2].equals("-1")) {
            _dormRoomNumber.setText("Dorm Number: " + userInfo[2]);
        }
        else {
            _dormRoomNumber.setText("No dorm room number selected yet");
        }
        if (!userInfo[3].equals("-1")) {
            _regNumber.setText("Registration Number: " + userInfo[3]);
        }
        else {
            _regNumber.setText("Don't have a registration number");
        }
        if (!userInfo[4].equals("-1")) {
            _regTime.setText("Registration Time: " + DashboardActivity.setDate(Long.parseLong(userInfo[4])));
        }
        else {
            _regTime.setText("Don't have a registration time");
        }
        if (!userInfo[5].equals("-1")) {
            _studentId.setText("Student ID: " + userInfo[5]);
        }
        else {
            _studentId.setText("Don't have a student ID");
        }
    }

    public static void setUserData(String [] data) {
    userInfo = data;
    }

    public static String[] getUserData() {
        return userInfo;
    }
}
