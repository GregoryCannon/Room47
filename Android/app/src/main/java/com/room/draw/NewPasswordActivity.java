package com.room.draw;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import SSLPackage.Connection;
import SSLPackage.ServerPacket;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewPasswordActivity extends AppCompatActivity {

    @BindView(R.id.temporary_password_text)
    TextView _temporaryPasswordText;
    @BindView(R.id.temporary_password)
    EditText _temporaryPassword;
    @BindView(R.id.new_password)
    EditText _newPassword;
    @BindView(R.id.new_password_check)
    EditText _newPasswordCheck;
    @BindView(R.id.btn_reset_new_password)
    Button _resetPassword;
    ServerPacket response;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        ButterKnife.bind(this);

        _resetPassword.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(NewPasswordActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Resetting Password...");
                progressDialog.show();
                try {
                    new SslClientToServer().execute((Object) null).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                String message = response.message;
                if (message.equals(ServerPacket.RESET_PASSWORD_SUCCESSFUL)) {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
                else {
                    Toast.makeText(getApplicationContext(), ServerPacket.RESET_PASSWORD_FAILED, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class SslClientToServer extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String password = _newPassword.getText().toString();
                String tempPassword = _temporaryPassword.getText().toString();
                response = Connection.resetPassword(ResetPasswordActivity.getUsername(),password,tempPassword);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
