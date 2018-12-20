package com.room.draw;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import SSLPackage.Connection;
import SSLPackage.ServerPacket;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordActivity extends AppCompatActivity {
    @BindView(R.id.reset_password_username)
    TextView _enterUsername;
    @BindView(R.id.username)
    EditText _username;
    @BindView(R.id.btn_reset_password)
    Button _resetPassword;
    ServerPacket response;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);

        _resetPassword.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                try {
                    new SslClientToServer().execute((Object) null).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                String message = response.message;
                if(message.equals(ServerPacket.REQUEST_TEMP_PASSWORD_SUCCESSFUL)) {
                    Intent intent = new Intent(getApplicationContext(), NewPasswordActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private class SslClientToServer extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String user = _username.getText().toString();
                response = Connection.requestTempPassword(_username.getText().toString(), getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
