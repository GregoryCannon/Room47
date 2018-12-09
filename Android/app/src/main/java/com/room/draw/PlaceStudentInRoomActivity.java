package com.room.draw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import SSLPackage.Connection;
import SSLPackage.ServerPacket;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceStudentInRoomActivity extends AppCompatActivity {
    @BindView(R.id.student_username)
    EditText _studentUsername;
    @BindView(R.id.dorm_name)
    EditText _dormName;
    @BindView(R.id.room_number)
    EditText _roomNumber;
    @BindView(R.id.btn_place_student)
    Button _placeStudent;
    @BindView(R.id.dorm_name_remove)
    EditText _dormNameRemove;
    @BindView(R.id.room_number_remove)
    EditText _roomNumberRemove;
    @BindView(R.id.btn_remove_student)
    Button _removeStudent;
    private static String username;
    private static String dormName;
    private static String roomNumber;
    private static List<String> dorms;
    private ServerPacket response;
    private boolean placeStudent;
    private boolean removeStudent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_student_in_room);
        ButterKnife.bind(this);
        dorms = new ArrayList<>();
        dorms.add("Gibson");
        dorms.add("Harwood");
        dorms.add("Lyon");
        dorms.add("Mudd-Blaisdell");
        dorms.add("Oldenberg");
        dorms.add("Smiley");
        dorms.add("Wig");
        dorms.add("Clark I");
        dorms.add("Clark V");
        dorms.add("Lawry Court");
        dorms.add("Norton-Clark III");
        dorms.add("Dialynas");
        dorms.add("Sontag");
        dorms.add("Walker");
        _placeStudent.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                placeStudent = true;
                try {
                    placeStudent();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        _removeStudent.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                removeStudent = true;
                try {
                    removeStudent();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void placeStudent() throws ExecutionException, InterruptedException {
        if (!validatePlacement()) {
            onPlaceStudentFailure();
        }

        _placeStudent.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Placing...");
        progressDialog.show();

        new SslClientToServer().execute((Object) null).get();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if(response.message.equals(ServerPacket.PLACE_STUDENT_SUCCESSFUL)) {
                            onPlaceRemoveStudentSuccess();
                        }
                        else {
                            onPlaceStudentFailure();
                        }

                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void removeStudent() throws ExecutionException, InterruptedException {
        if (!validateRemoval()) {
            onRemoveStudentFailure();
        }

        _removeStudent.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Removing...");
        progressDialog.show();

        new SslClientToServer().execute((Object) null).get();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if(response.message.equals(ServerPacket.REMOVE_STUDENT_SUCCESSFUL)) {
                            onPlaceRemoveStudentSuccess();
                        }
                        else {
                            onRemoveStudentFailure();
                        }

                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onPlaceStudentFailure() {
        Toast.makeText(getBaseContext(), "Placing student failed", Toast.LENGTH_LONG).show();

        _placeStudent.setEnabled(true);
    }

    public void onPlaceRemoveStudentSuccess() {
        _placeStudent.setEnabled(true);
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    public void onRemoveStudentFailure() {
        Toast.makeText(getBaseContext(), "Removing student failed", Toast.LENGTH_LONG).show();

        _removeStudent.setEnabled(true);
    }

    public boolean validatePlacement() {
        boolean valid = true;
        username = _studentUsername.getText().toString();
        dormName = _dormName.getText().toString();
        roomNumber = _roomNumber.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            _studentUsername.setError("at least 3 characters");
            valid = false;
        } else {
            _studentUsername.setError(null);
        }

        if (!dorms.contains(dormName)) {
            _dormName.setError("Please enter a valid dorm name");
            valid = false;
        } else {
            _dormName.setError(null);
        }

        int roomNumberInt = Integer.parseInt(roomNumber);
        if (roomNumberInt < 0 || roomNumberInt > 10) {
            _roomNumber.setError("Please enter a valid room number");
            valid = false;
        }
        else {
            _roomNumber.setError(null);
        }

        return valid;
    }

    public boolean validateRemoval() {
        boolean valid = true;
        dormName = _dormNameRemove.getText().toString();
        roomNumber = _roomNumberRemove.getText().toString();

        if (!dorms.contains(dormName)) {
            _dormName.setError("Please enter a valid dorm name");
            valid = false;
        } else {
            _dormName.setError(null);
        }

        int roomNumberInt = Integer.parseInt(roomNumber);
        if (roomNumberInt < 0 || roomNumberInt > 10) {
            _roomNumber.setError("Please enter a valid room number");
            valid = false;
        }
        else {
            _roomNumber.setError(null);
        }
        return valid;
    }


    private class SslClientToServer extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (placeStudent) {
                try {
                    response = Connection.placeStudentInRoom(username, dormName, roomNumber);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                placeStudent = false;
            }

            if (removeStudent) {
                try {
                    response = Connection.removeStudentFromRoom(dormName, roomNumber);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                removeStudent = false;
            }
            return null;
        }
    }
}
