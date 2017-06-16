package edu.byui.maddldsdj;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInRegister extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "DALLAS LOG";

    private Button buttonSignin;
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_register);

        // get instance of Firebase Authorization
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user = new User();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(SignInRegister.class.getName(), "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(SignInRegister.class.getName(), "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // progress dialogue
        progressDialog = new ProgressDialog(this);
        // Buttons
        buttonSignin = (Button) findViewById(R.id.button_signin);
        buttonRegister = (Button) findViewById(R.id.button_register);
        // Views
        editTextEmail = (EditText) findViewById(R.id.text_email);
        editTextPassword = (EditText) findViewById(R.id.text_password);
        // assign onClickListener to buttons
        buttonSignin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            // stop further execution
            return;
        }
        if (TextUtils.isEmpty(password)) {
            // password is empty
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            // stop further execution
            return;
        }
        // email and password validations have passed
        // show progress of creating a new user
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        // create the user on firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(SignInRegister.class.getName(), "createUserWithEmail:success");
                            Toast.makeText(SignInRegister.this, "Registration Successful",
                                    Toast.LENGTH_SHORT).show();
                            /*FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);*/
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(SignInRegister.class.getName(), "createUserWithEmail:failure");
                            Toast.makeText(SignInRegister.this, "Registration failed...Please try again",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }

    private void signIn() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            // stop further execution
            return;
        }
        if (TextUtils.isEmpty(password)) {
            // password is empty
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            // stop further execution
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(SignInRegister.class.getName(), "signInWithEmail:onComplete:" + task.isSuccessful());
                        Toast.makeText(SignInRegister.this, "SignIn Success",
                                Toast.LENGTH_SHORT).show();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(SignInRegister.class.getName(), "signInWithEmail:failed", task.getException());
                            Toast.makeText(SignInRegister.this, "SignIn failed...Please try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (firebaseUser != null) {
                            // Name, email address, and profile photo Url
                            //String name = firebaseUser.getDisplayName();
                            user.setUserEmail(firebaseUser.getEmail());

                            // The user's ID, unique to the Firebase project. Do NOT use this value to
                            // authenticate with your backend server, if you have one. Use
                            // FirebaseUser.getToken() instead.
                            user.setUserID(firebaseUser.getUid());
                            Log.w(TAG, user.getUserEmail());
                            Log.w(TAG, user.getUserID());
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignin) {
            signIn();
        }
        if(view == buttonRegister ) {
            registerUser();
        }
    }
}