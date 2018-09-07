package com.example.sivaprasad.trackme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;


public class SignupActivity extends AppCompatActivity {

    DatabaseHelper mydb;
    EditText email,password;
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        email = (EditText)findViewById(R.id.input_email);
        password = (EditText)findViewById(R.id.input_password);
        mydb = new DatabaseHelper(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;

        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        mydb.clear();

        Boolean isInserted = mydb.insertData(email.getText().toString(),password.getText().toString());
        // TODO: Implement your own signup logic here.
        Cursor res = mydb.getAllData();
        StringBuffer buff = new StringBuffer();

        while (res.moveToNext()){

            buff.append(res.getString(0)+" "+res.getString(1)+" ");
        }


        if (isInserted==true){
            Toast.makeText(SignupActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(SignupActivity.this,"Data Not Inserted",Toast.LENGTH_LONG).show();
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success


                        progressDialog.dismiss();
                        Intent move = new Intent(getApplicationContext(),Main2Activity.class);
                        startActivity(move);
                        finish();
                    }
                }, 3000);


    }




    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }


        if (password.isEmpty() || password.length() < 6 || password.length() > 6) {
            _passwordText.setError("Enter 6 digit number");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}