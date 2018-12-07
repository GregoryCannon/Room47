package com.room.draw;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import SSLPackage.Connection;
import SSLPackage.ServerPacket;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity {
    @BindView(R.id.select_dorm)
    Button _selectDorm;
    @BindView(R.id.regTime)
    TextView _regTime;
    @BindView(R.id.room_status)
    Button _roomStatus;
    @BindView(R.id.logout) Button _logout;
    private static String username;
    private static String password;
    private static boolean logoutButtonClicked;
    private static boolean roomStatusButtonClicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        _selectDorm.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DormSelectionActivity.class);
                startActivity(intent);
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

    public static void setPassword(String password2) {
        password = password2;
    }

    @Override
    public void onBackPressed() {

    }

    private class SslClientToServer extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (roomStatusButtonClicked) {
                ServerPacket response = null;
                try {
                    response = Connection.getInfo(username, password, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String message = response.message;
                String[] userInfo = new String[7];
                StringTokenizer str = new StringTokenizer(message);
                for (int i = 0; i < userInfo.length; i++) {
                    userInfo[i] = str.nextToken("|");
                }
                StatusActivity.setUserData(userInfo);
                Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                startActivity(intent);
                roomStatusButtonClicked = false;
            }

            if (logoutButtonClicked) {
                ServerPacket response = null;
                try {
                    response = Connection.logout(username, password, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
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
            return null;
        }
    }
}
