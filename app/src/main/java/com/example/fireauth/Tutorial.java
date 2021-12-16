package com.example.fireauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class Tutorial extends Activity {


    Button close;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        getActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        close=findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Tutorial.this,LandingTest.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });
    }
}
