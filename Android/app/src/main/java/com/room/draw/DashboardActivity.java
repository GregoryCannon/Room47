package com.room.draw;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import SSLPackage.Connection;
import SSLPackage.ServerPacket;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity {
    @BindView(R.id.select_dorm)
    Button _selectDorm;
    @BindView(R.id.room_status)
    Button _roomStatus;
    @BindView(R.id.logout)
    Button _logout;
    @BindView(R.id.pick_room_for_student)
    Button _selectRoom;
    private static String username;
    private static String password;
    private static boolean logoutButtonClicked;
    private static boolean roomStatusButtonClicked;
    private static boolean selectRoomButtonClicked;
    private static ServerPacket response;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        try {
            new SslClientToServer().execute((Object) null).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        checkAdminStatus();
        _selectRoom.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                _selectDorm.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), PlaceStudentInRoomActivity.class);
                startActivity(intent);
                finish();

            }
        });

        _selectDorm.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                selectRoomButtonClicked = true;
                try {
                    new SslClientToServer().execute((Object) null).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                checkRoomStatus(getApplicationContext());
            }
        });
        _logout.setOnClickListener(new Button.OnClickListener() {


            @Override
            public void onClick(View view) {
                logoutButtonClicked = true;
                try {
                    new SslClientToServer().execute((Object) null).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        _roomStatus.setOnClickListener(new Button.OnClickListener() {


            @Override
            public void onClick(View view) {
                roomStatusButtonClicked = true;
                try {
                    new SslClientToServer().execute((Object) null).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static void setUsername(String username2) {
        username = username2;
    }
    public static String getUsername() {
        return username;
    }

    public static void setPassword(String password2) {
        password = password2;
    }
    public static String getPassword() {
        return password;
    }

    @Override
    public void onBackPressed() {

    }

    public static String setDate(long milliDate) {
        Date date = new Date(milliDate);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    public static void checkRoomStatus(Context context) {
        String message = response.message;
        String[] userInfo = new String[3];
        StringTokenizer str = new StringTokenizer(message);
        for (int i = 0; i < userInfo.length; i++) {
            userInfo[i] = str.nextToken("|");
        }
        if (!userInfo[1].equals("-1") && !userInfo[2].equals("-1")) {
            Toast.makeText(context, "Already in a room", Toast.LENGTH_LONG).show();
        }
        else {
            context.startActivity(new Intent(context, DormSelectionActivity.class));

        }
        roomStatusButtonClicked = false;

    }

    public void checkAdminStatus() {
        String message = response.message;
        String [] info = new String[7];
        StringTokenizer st = new StringTokenizer(message);
        for (int i=0; i<info.length; i++) {
            info[i] = st.nextToken("|");
        }
        if (info[6].equals("true")) {
            _selectRoom.setVisibility(View.VISIBLE);
        }
    }

    private class SslClientToServer extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                response = Connection.getInfo(username, password, getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (roomStatusButtonClicked) {
                try {
                    response = Connection.getInfo(username, password, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                String message = response.message;
                String[] userInfo = new String[7];
                StringTokenizer str = new StringTokenizer(message);
                for (int i = 0; i < userInfo.length; i++) {
                    userInfo[i] = str.nextToken("|");
                }
                String admin = userInfo[6];
                StatusActivity.setUserData(userInfo);
                Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                startActivity(intent);
                roomStatusButtonClicked = false;
            }

            if (logoutButtonClicked) {
                try {
                    response = Connection.logout(username, password, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                String message = response.message;
                if (message.equals("Logout successful")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                logoutButtonClicked = false;
            }

            if (selectRoomButtonClicked) {
                try {
                    response = Connection.getInfo(username, password, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
