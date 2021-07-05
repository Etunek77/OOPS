package com.example.project.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private TextView txtEmail;
    private TextView txtUsername;
    private TextView txtProfession;
    private TextView txtPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        setTitle("Profile");

        txtEmail = findViewById(R.id.email);
        txtUsername = findViewById(R.id.username);
        txtPhone = findViewById(R.id.phone);
        txtProfession = findViewById(R.id.profession);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(firebaseUser.getUid());


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass user = snapshot.getValue(UserHelperClass.class);

                txtEmail.setText(user.getEmail());
                txtUsername.setText(user.getUsername());
                txtPhone.setText(user.getPhone());
                txtProfession.setText(user.getProfession());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}