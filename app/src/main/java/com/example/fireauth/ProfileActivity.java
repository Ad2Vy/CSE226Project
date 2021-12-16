package com.example.fireauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity{

    URL ImageUrl = null;
    InputStream is = null;
    Bitmap bmImg = null;
    ImageView imageView = null;
    ProgressDialog p;
    String[] strings={"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRytSo0dwyQb0OQ-uuXZXQqmdtkjj2imQY9ruJ-EBvXPvsiut7f6zoVPDW9w8WP7oWJfLc&usqp=CAU","https://www.cbc-network.org/wp-content/uploads/2017/11/Amazon-icon.png","https://media.idownloadblog.com/wp-content/uploads/2018/07/Apple-logo-black-and-white.png","https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Google_%22G%22_Logo.svg/2048px-Google_%22G%22_Logo.svg.png","https://akm-img-a-in.tosshub.com/indiatoday/images/story/202106/Stars.jpg?4syKaMZXcTgsSMAzVB.sklrcrHxayQ3O&size=1200:675"};
    String[] string2={"TESLA (TSLA)","AMAZON (AMZN)","APPLE (AAPL)","GOOGLE (GOOGL)","A Star is Born"};
    String[] description={"Tesla, Inc. is an American electric vehicle and clean energy company based in Austin, Texas, United States. Tesla designs and manufactures electric cars, battery energy storage from home to grid-scale, solar panels and solar roof tiles, and related products and services. Tesla is one of the world's most valuable companies and remains the most valuable automaker in the world with a market cap of nearly $1 trillion.", "Amazon.com, Inc. is an American multinational technology company which focuses on e-commerce, cloud computing, digital streaming, and artificial intelligence. It is one of the Big Five companies in the U.S. information technology industry, along with Google, Apple, Meta, and Microsoft.  The company has been referred to as \"one of the most influential economic and cultural forces in the world\", as well as the world's most valuable brand.", "Apple Inc. is an American multinational technology company that specializes in consumer electronics, computer software and online services. Apple is the largest information technology company by revenue and, since January 2021, the world's most valuable company. Apple was founded in 1976 by Steve Jobs, Steve Wozniak and Ronald Wayne to develop and sell Wozniak's Apple I personal computer.", "Alphabet Inc. is an American multinational technology conglomerate holding company headquartered in Mountain View, California. It was created through a restructuring of Google on October 2, 2015, and became the parent company of Google and several former Google subsidiaries. Google was founded on September 4, 1998, by Larry Page and Sergey Brin while they were Ph.D. students at Stanford University in California.", "speculations of birth of first star right after BIG BANG"};
    String[] livedata={"https://finance.yahoo.com/quote/TSLA/",
            "https://finance.yahoo.com/quote/AMZN/",
            "https://finance.yahoo.com/quote/AAPL/",
            "https://finance.yahoo.com/quote/GOOG/"};
    String[] readmore={"https://en.wikipedia.org/wiki/Tesla,_Inc.",
            "https://en.wikipedia.org/wiki/Amazon_(company)",
            "https://en.wikipedia.org/wiki/Apple_Inc.",
            "https://en.wikipedia.org/wiki/Google"};




    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseFirestore db;

    private String userID;

    private TextView stocksName;

    private TextView logoutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        String text=intent.getStringExtra(LandingTest.EXTRA_STRING);


        stocksName=(TextView) findViewById(R.id.textView);

        //title and content
        TextView textView=findViewById(R.id.textView);
        TextView textView1=findViewById(R.id.textView2);
        TextView readMore=findViewById(R.id.readmore);


        //image
        imageView = findViewById(R.id.imageView);
        //button
        Button button=findViewById(R.id.button);

        textView.setVisibility(View.INVISIBLE);
        textView1.setVisibility(View.INVISIBLE);

        Random random=new Random();
        int i=0;
        switch(text){
            case "TSLA":
                i=0;
                break;
            case "AMZN":
                i=1;
                break;
            case "AAPL":
                i=2;
                break;
            case "GOOGL":
                i=3;
                break;
            default:
                i=0;
        }
        final int j = i;
        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse(readmore[j]); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri=Uri.parse(livedata[j]);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

        textView.setVisibility(View.VISIBLE);
        textView1.setVisibility(View.VISIBLE);
        textView.setText(string2[i]);
        textView1.setText(description[i]);

        AsyncTaskExample asyncTask = new AsyncTaskExample();
        asyncTask.execute(strings[i]);



//        user=FirebaseAuth.getInstance().getCurrentUser();
//        reference= FirebaseDatabase.getInstance().getReference("Users");
//        userID=user.getUid();

//        final TextView greetingTextView=(TextView) findViewById(R.id.greetings);
//        final TextView fullNameTextView=(TextView) findViewById(R.id.fullName);
//        final TextView emailTextView=(TextView) findViewById(R.id.emailAddress);
//        final TextView ageTextView= (TextView) findViewById(R.id.age_value);

//        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User userProfile=snapshot.getValue(User.class);
//                if(userProfile!=null){
//                    String fullName=userProfile.fullName;
//                    String email=userProfile.email;
//                    String age=userProfile.age;
//                    greetingTextView.setText("welcome, "+fullName+" !");
//                    fullNameTextView.setText(fullName);
//                    emailTextView.setText(email);
//                    ageTextView.setText(age);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ProfileActivity.this,"Something went wrong...!",Toast.LENGTH_LONG).show();
//            }
//        });




    }

    private class AsyncTaskExample extends AsyncTask<String, String, Bitmap> {

        int count;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Progress dialog
            p = new ProgressDialog(ProfileActivity.this);
            p.setMessage("Please wait...It is downloading");
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(imageView!=null){
                p.hide();
                imageView.setImageBitmap(bitmap);
            }
            else{
                p.show();
            }
        }
        //Progress Bar


        //Progress Dialog
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                ImageUrl = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) ImageUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                is= conn.getInputStream();
                BitmapFactory.Options options= new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bmImg= BitmapFactory.decodeStream(is , null, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;

        }




    }

}