package com.example.fireauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView banner,registerUser,termsRegister;
    private Button goBack;
    private EditText editTextFullName,editTextPassword,editTextEmail,editTextAge;
    private ProgressBar progressBar;
    private ImageView registerWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //firebase auth
        mAuth = FirebaseAuth.getInstance();

        banner=(TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser=(Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        goBack=(Button) findViewById(R.id.backtoLogin);
        goBack.setOnClickListener(this);



        termsRegister=findViewById(R.id.terms_register);
        termsRegister.setOnClickListener(this);

        editTextFullName=(EditText) findViewById(R.id.fullName);
        editTextAge=(EditText) findViewById(R.id.age);
        editTextEmail=(EditText) findViewById(R.id.editTextEmailAddress);
        editTextPassword=(EditText) findViewById(R.id.registerPassword);

        progressBar=(ProgressBar) findViewById(R.id.registerProgressBar);
        registerWait=(ImageView) findViewById(R.id.register_wait);

        //FirebaseFirestore
         db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.banner:
                startActivity(new Intent(this,LoginUser.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.registerUser:
                registerUserOnClick();
                break;
            case R.id.backtoLogin:
                startActivity(new Intent(this,LoginUser.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.terms_register:
                showTerms();
                break;

        }
    }
// For Terms and conditions dialog
    private void showTerms() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(new Terms().getTerms());
        alertDialogBuilder.setPositiveButton("Accept",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(RegisterUser.this,"You \"Accepted\" terms and conditions",Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void registerUserOnClick(){
        String email=editTextEmail.getText().toString().trim();
        String fullName=editTextFullName.getText().toString().trim();
        String age=editTextAge.getText().toString().trim();
        String password=editTextPassword.getText().toString();
        String stocks="";

        if(fullName.isEmpty()){
            editTextFullName.setError("Full Name is required");
            editTextFullName.requestFocus();
            return;
        }
        if(age.isEmpty()){
            editTextAge.setError("Age is required");
            editTextAge.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        //To check if the email entered is correct
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }

        //for password check
        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        //for password length less than 6
        if(password.length()<=6){
        editTextPassword.setError("Password length must be greater than 6");
        editTextPassword.requestFocus();
        return;
        }


        progressBar.setVisibility(View.VISIBLE);
        registerWait.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user=new User(fullName, age, email,stocks);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "User registered", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        registerWait.setVisibility(View.GONE);
                                        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        db.collection(userId).document(editTextFullName.getText().toString().trim()).set(user);
                                        //redirect user to login user page.
                                        startActivity(new Intent(RegisterUser.this,LoginUser.class));

                                    }
                                    else{
                                        Toast.makeText(RegisterUser.this, "Failed to Register... Try Again", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        registerWait.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        //if not successful...
                        else{
                            Toast.makeText(RegisterUser.this, "Failed to Register... Try Again", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            registerWait.setVisibility(View.GONE);
                        }
                    }
                });

//        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
//        String userId=user.getUid();
//        Map<String,Object> map=new HashMap<>();
//        map.put("fullname",fullName);
//        map.put("stocks","");
//        db.collection(userId).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                Log.d("success","Document snapshot added");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("Failure","Document snapshot Error in adding");
//            }
//        });


    }



}