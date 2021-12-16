package com.example.fireauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText resetEmail;

    private Button sendReset;
    private Button resetTologin;
    private ProgressBar progressBar;

    FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetEmail=(EditText) findViewById(R.id.reset_email);

        sendReset=(Button) findViewById(R.id.send_reset);
        resetTologin=(Button) findViewById(R.id.resetToLogin);

        progressBar=(ProgressBar) findViewById(R.id.resetProgressBar);

        Auth=FirebaseAuth.getInstance();

        sendReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        resetTologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPassword.this,LoginUser.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });


    }

    private void resetPassword() {
        String email=resetEmail.getText().toString().trim();


        if(email.isEmpty()){
            resetEmail.setError("Email is required");
            resetEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            resetEmail.setError("Provide valid email");
            resetEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(ForgotPassword.this,LoginUser.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    Toast.makeText(ForgotPassword.this,"Check your email to reset password!",Toast.LENGTH_LONG).show();
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgotPassword.this, "Something went wrong... Try again!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}