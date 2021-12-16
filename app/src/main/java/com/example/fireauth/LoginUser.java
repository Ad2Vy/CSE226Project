package com.example.fireauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginUser extends AppCompatActivity implements View.OnClickListener {


    private TextView register;
    private TextView forgotPassword;

    private EditText editTextEmail, editTextPassword;
    private Button login;

    private ProgressBar progressBar;
    private ImageView loginWait;

    private TextView terms,accepted;

    //icons
    private ImageView registerImage;
    private ImageView forgotPasswordImage;

    //Firebase auth
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        register=findViewById(R.id.register);
        register.setOnClickListener(this);

        forgotPassword=findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        terms=findViewById(R.id.terms_login);
        terms.setOnClickListener(this);

        accepted=findViewById(R.id.accept_terms);

        login=(Button)findViewById(R.id.login);
        login.setOnClickListener(this);

        editTextEmail=(EditText) findViewById(R.id.email);
        editTextPassword = (EditText)findViewById(R.id.password);

        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        loginWait=(ImageView) findViewById(R.id.login_wait);

        registerImage=(ImageView) findViewById(R.id.image_register);
        registerImage.setOnClickListener(this);
        forgotPasswordImage=(ImageView) findViewById(R.id.forget_password_image);
        forgotPasswordImage.setOnClickListener(this);

        mAuth=FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.register:
                startActivity(new Intent(this,RegisterUser.class));
                register.setTextColor(Color.BLACK);
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this,ForgotPassword.class));
                forgotPassword.setTextColor(Color.BLACK);
                break;
            case R.id.forget_password_image:
                startActivity(new Intent(this,ForgotPassword.class));
                break;
            case R.id.image_register:
                register.setTextColor(Color.BLACK);
                startActivity(new Intent(this,RegisterUser.class));
                break;
            case R.id.login:
                userLogin();
                break;
            case R.id.terms_login:
                showTerms();
                break;
        }
    }

    //Showing the terms and condition's dialog
    private void showTerms() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(new Terms().getTerms());
        alertDialogBuilder.setPositiveButton("Accept",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(LoginUser.this,"You \"Accepted\" terms and conditions",Toast.LENGTH_LONG).show();
                        accepted.setText("accepted");
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //For test purpose only..
//        startActivity(new Intent(LoginUser.this,LandingTest.class));

        //validations
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextPassword.requestFocus();
            return;
        }
        //check for valid email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Invalid Email");
            editTextEmail.requestFocus();
            return;
        }
        //checking for password if empty
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("must be greater than 6");
            editTextPassword.requestFocus();
            return;
        }
        //checking if terms accepted or not..
        if(accepted.getText().toString()!="accepted") {
            showTerms();
        }
        if (accepted.getText().toString()=="accepted") {
                progressBar.setVisibility(View.VISIBLE);
                loginWait.setVisibility(View.VISIBLE);
                //password provided and now to check if it matches.
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified()) {
                                //redirect to user profile

                                //change it accordingly.
                                startActivity(new Intent(LoginUser.this, LandingTest.class));
                            } else {
                                user.sendEmailVerification();
                                progressBar.setVisibility(View.GONE);
                                loginWait.setVisibility(View.GONE);

                                //firebasefirestore
//                                String userId=user.getUid();
//                                Map<String,Object> map=new HashMap<>();
//                                map.put("email",editTextEmail.getText().toString().trim());
//                                map.put("stocks","");
//                                db.collection(userId).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                    @Override
//                                    public void onSuccess(DocumentReference documentReference) {
//                                        Log.d("success","Document snapshot added");
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.d("Failure","Document snapshot Error in adding");
//                                    }
//                                });

                                Toast.makeText(LoginUser.this, "Verification send on your email, please verify and then login again", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            loginWait.setVisibility(View.GONE);
                            Toast.makeText(LoginUser.this, "Wrong Credentials, Failed to Login", Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                        loginWait.setVisibility(View.GONE);
                    }
                });

            }
        }


    //State management on rotation..
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("email",editTextEmail.getText().toString());
        outState.putString("pass",editTextPassword.getText().toString());
        outState.putString("accept",accepted.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        editTextEmail.setText(savedInstanceState.getString("email"));
        editTextPassword.setText(savedInstanceState.getString("pass"));
        accepted.setText(savedInstanceState.getString("accept"));
    }
}