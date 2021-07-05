package com.example.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.project.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends Activity {
    public static final String TAG = "TAG";
    EditText mUsername,mEmail,mPassword,mPhone,mProfession;
    TextView mLoginhere;
    FirebaseAuth firebaseAuth;
    Button mNext;
    ProgressBar progressBar;
    FirebaseDatabase rootnode;
    DatabaseReference reference;
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(getApplicationContext(),CategoryActivity.class);
            startActivity(intent);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);

            mUsername = findViewById(R.id.username);
            mEmail = findViewById(R.id.email);
            mPassword = findViewById(R.id.password);
            mPhone = findViewById(R.id.phone);
            mProfession = findViewById(R.id.profession);
            mLoginhere = findViewById(R.id.login_here);
            mNext = findViewById(R.id.Next_button);
            progressBar = findViewById(R.id.progressBar);


            firebaseAuth = FirebaseAuth.getInstance();


            if(firebaseAuth.getCurrentUser() != null){
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                finish();

            }

            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   rootnode = FirebaseDatabase.getInstance();
                   reference = rootnode.getReference("Users");
                    String email  = mEmail.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    String profession = mProfession.getText().toString().trim();
                    String username = mUsername.getText().toString();
                    String phone = mPhone.getText().toString().trim();


                    if(TextUtils.isEmpty(email)){
                        mEmail.setError("Email is required");
                        return;
                    }
                    if(TextUtils.isEmpty(profession)){
                        mProfession.setError("Profession is required");
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

                    Pattern pattern1 = Pattern.compile("^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}$");
                    Matcher matcher1 = pattern1.matcher(phone);

                    if(!matcher1.matches()){
                        mPhone.setError("Invalid Phone Number");
                        return;
                    }
                    if(phone.length() < 10){
                        mPhone.setError("Invalid Phone Number");
                        return;
                    }

                    Pattern pattern = Pattern.compile("^[a-zA-Z0-9](_(?!(\\.|_))|\\.(?!(_|\\.))|[a-zA-Z0-9]){6,18}[a-zA-Z0-9]$");//^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$
                    Matcher matcher = pattern.matcher(username);

                    if(!matcher.matches()){
                        mUsername.setError("Invalid Username");
                        return;
                    }


                    progressBar.setVisibility(View.VISIBLE);

                    /*Intent intent = new Intent(getApplicationContext(),PhoneOTP.class);
                    intent.putExtra("phone",phone);
                    startActivity(intent);
                    */

                    Item dummyItem = new Item("Dummy Item", "Dummy Path", 0);
                    Map<String, Item> items = new HashMap<>();
                    items.put(dummyItem.getName(), dummyItem);
                    Category dummyCategory = new Category("Dummy Category", items);
                    Map<String, Category> categories = new HashMap<>();
                    categories.put(dummyCategory.getName(), dummyCategory);

                    UserHelperClass helperclass = new UserHelperClass(username,email,phone,password,profession, categories);
//                    reference.child(phone).setValue(helperclass);
                    







                   firebaseAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                                reference.child(mUser.getUid()).setValue(helperclass);
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){



                                                Toast.makeText(Register.this, "User created successfully. Please verify your email.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), VerifyEmail.class));
                                            finish();
                                            /*if(firebaseAuth.getCurrentUser().isEmailVerified()) {


                                                Toast.makeText(Register.this, "Email is verified.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), PhoneOTP.class));
                                                finish();

                                            }else {
                                                Toast.makeText(Register.this, "Please verify email ID", Toast.LENGTH_SHORT).show();
                                            } */




                                        }
                                        else {
                                            Toast.makeText(Register.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });



                            }
                            else{
                                Toast.makeText(Register.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });


                }
            });

            mLoginhere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), Login.class));

                }
            });

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            SignInButton signInButton = findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_STANDARD);


            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });


        }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


    /*private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Toast.makeText(this, "User Email :" +personEmail, Toast.LENGTH_SHORT).show();
            }

            startActivity(new Intent(Register.this, CategoryActivity.class));

            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

            Log.d("Message",e.toString());

        }
    } */

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Register.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(),CategoryActivity.class);
                            startActivity(intent);




                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "signInWithCredential:failure", Toast.LENGTH_SHORT).show();
                            

                        }

                        // ...
                    }
                });
    }


}





