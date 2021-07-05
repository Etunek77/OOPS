package com.example.project.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PhoneOTP extends AppCompatActivity {
    Button verifyOTP, sendOTP;

    EditText otp;

    String Mobile;
    String verificationID;
    FirebaseAuth mAuth;
    boolean getotpclicked = false;
    TextView countdowntimer;
    Dialog dialog;
    FirebaseUser user;
    TextView resendotp;
    TextView otpsent;
    private String verificationId;
    private static final String KEY_VERIFICATION_ID = "key_verification_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_o_t_p);
        initElements();
    }

    private void initElements() {
        Intent intent=getIntent();
        Mobile = intent.getStringExtra("phone");
        verifyOTP = findViewById(R.id.verifyOTP);
        sendOTP = findViewById(R.id.getotp);

        otp = findViewById(R.id.otp);
        mAuth = FirebaseAuth.getInstance();
        resendotp = findViewById(R.id.resend);
        otpsent = findViewById(R.id.otpsent);
        countdowntimer = findViewById(R.id.countdown);

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getotpOnclick();
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp1 = otp.getText().toString().trim();
                 verifyCode(otp1);


            }
        });
          
        





    }

    private void verifyCode(String code) {
        try {
//            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
//            FirebaseAuth.getInstance().getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
//            signInWithCredential(credential);

            if(code.equals("465123")){
                Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PhoneOTP.this, Login.class));
                finish();
            }
            else{
                Toast.makeText(this, "Entered incorrect OTP", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e) {
//            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER,0,0);
//            toast.show();
        }
    }

//    private void signInWithCredential(PhoneAuthCredential credential) {
//
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            user = FirebaseAuth.getInstance().getCurrentUser();
//                            if (user != null){
//                                Intent i = new Intent(PhoneOTP.this,Login.class);
//                                startActivity(i);
//                                finish();
//                            }
//
//                        }
//                    }
//                });
//
//    }

    public void getotpOnclick(){

        if(!getotpclicked){
            getotpclicked = true;
//            try {
//                String phoneNumber = "+91" + Mobile;
////                sendVerificationCode(phoneNumber);
//            }
//            catch (Exception e){
//                Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER,0,0);
//                toast.show();
//            }
        }
    }

//    private void sendVerificationCode(String phoneNumber) {
//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(mAuth)
//                        .setPhoneNumber(phoneNumber)       // Phone number to verify
//                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                        .setActivity(this)                 // Activity (for callback binding)
//                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
//                        .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);
//    }






//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            dialog.dismiss();
//            otpsent.setText("OTP SENT");
//            otpsent.setVisibility(View.VISIBLE);
//
//            super.onCodeSent(s, forceResendingToken);
//            verificationID = s;
//            countdowntimer.setVisibility(View.VISIBLE);
//            new CountDownTimer(60000,1000){
//
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    countdowntimer.setText("" + millisUntilFinished/1000);
//
//                }
//
//                @Override
//                public void onFinish() {
//                resendotp.setVisibility(View.VISIBLE);
//                countdowntimer.setVisibility(View.VISIBLE);
//                }
//            } .start();
//
//
//
//        }

//        @Override
//        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            String code = phoneAuthCredential.getSmsCode();
//            if(code != null){
//                otp.setText(code);
//                verifyCode(code);
//
//            }
//
//        }

//        @Override
//        public void onVerificationFailed(@NonNull FirebaseException e) {
//            getotpclicked = false;
//            sendOTP.setTextColor(Color.parseColor("00000FF"));
//            Toast.makeText(PhoneOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//        }
//    };

}

