package com.room.draw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        ButterKnife.bind(this);

        _resetPassword.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
