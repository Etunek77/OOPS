package com.example.project.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends Activity {
    EditText mEmail,mPassword;
    Button mlogin;
    TextView mRegisterhere,mReset;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = (findViewById(R.id.email));
        mPassword = findViewById(R.id.password);
        mlogin = findViewById(R.id.login_button);
        mRegisterhere = findViewById(R.id.register_here);
        mReset = findViewById(R.id.reset_password);

        firebaseAuth = FirebaseAuth.getInstance();


        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email  = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    mPassword.setError("Password must be greater than 6 characters");
                    return;
                }

                //authenticate user
                firebaseAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            if(firebaseAuth.getCurrentUser().isEmailVerified()) {


                                Toast.makeText(Login.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                                finish();
                            }else {
                                Toast.makeText(Login.this, "Please verify email ID", Toast.LENGTH_SHORT).show();
                            }


                        }else{
                            Toast.makeText(Login.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        mRegisterhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));

            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetemail = new EditText(v.getContext());
                AlertDialog.Builder passwordresetdialog = new AlertDialog.Builder(v.getContext());
                passwordresetdialog.setTitle("Reset Password");
                passwordresetdialog.setMessage("Enter your Email to receive reset link.");
                passwordresetdialog.setView(resetemail);
                passwordresetdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetemail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Reset link sent to your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Failed to send reset link"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordresetdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog
                    }
                });

                passwordresetdialog.create().show();
            }
        });


    }
}