package com.example.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;

public class VerifyEmail extends Activity {

    TextView Verifyemail,Verifytext;
    Button Verifynext;
    FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        Verifyemail = findViewById(R.id.verifyemail);
        Verifytext = findViewById(R.id.verifytext);
        Verifynext = findViewById(R.id.verifynext);
        firebaseAuth = FirebaseAuth.getInstance();

        Verifynext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    firebaseAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), PhoneOTP.class));
                    finish();



            }
        });


    }
}