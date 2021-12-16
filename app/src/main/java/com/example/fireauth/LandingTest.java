package com.example.fireauth;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandingTest extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    RecyclerView rv;
    ArrayList<String> al,temp;
    LandingAdapter md;
    private TextView logoutText;
    TextView stockName;
    private FloatingActionButton addButton;
    ImageView img;
    TextView textView;


    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    User data=new User();

    public static final String EXTRA_STRING="com.example.application.fireauth.EXTRA_TEXT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_test);
        db=FirebaseFirestore.getInstance();

        stockName = findViewById(R.id.tv_stock);
        addButton = findViewById(R.id.add_button);
        logoutText = (TextView) findViewById(R.id.logout_text);


        //temp arraylist
        temp=new ArrayList<String>();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String userId=user.getUid();
        db.collection(userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Log.d(TAG,"SUCCESS But empty");
                    return;
                }
                else{
                    List<User> value= queryDocumentSnapshots.toObjects(User.class);
                    data=value.get(0);
                    String[]  arr= value.get(0).stocks.split(",");
                    for(int i=0;i<arr.length;i++){
                        if(arr[i]!=""){
                            temp.add(arr[i]);
                        }
                    }
                    Log.d(TAG, "onSuccess: " + value.get(0).stocks);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
            }
        });

        rv = (RecyclerView) findViewById(R.id.rv);
        al = new ArrayList<String>(temp);




        md = new LandingAdapter(this, al);


        img = findViewById(R.id.imageView4);
        textView = findViewById(R.id.text_add);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);

        rv.setAdapter(md);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);


        setAddStocks(al);


        logoutText.setText("Refresh");
        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al.addAll(temp);
                md.notifyDataSetChanged();
                setAddStocks(al);
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(LandingTest.this,LoginUser.class));
            }
        });
    }


    //for swipe to delete

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        //On swip delete with alert dialog
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LandingTest.this);
            builder.setMessage("Are you sure to delete?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    // Your action
                    al.remove(viewHolder.getAdapterPosition());
                    md.notifyDataSetChanged();

                    setAddStocks(al);
                    dialog.cancel();
                }

                ;
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    md.notifyItemChanged(viewHolder.getAdapterPosition());
                    dialog.cancel();
                }

                ;
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }

    };

    public void doThis(View view) {
//        String p="TSLA";
//        al.add(p);
//        md.notifyDataSetChanged();
        final String[] fonts = {
                "TSLA", "AMZN", "AAPL", "GOOGL", "TSLA", "AMZN", "AAPL", "GOOGL",
                "TSLA", "AMZN", "AAPL", "GOOGL", "TSLA", "AMZN", "AAPL", "GOOGL",
                "TSLA", "AMZN", "AAPL", "GOOGL", "TSLA", "AMZN", "AAPL", "GOOGL",

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(LandingTest.this);
        builder.setTitle("Select a Stock");
        builder.setItems(fonts, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(al.contains(fonts[which])){
                    Toast.makeText(LandingTest.this,"Already Added..",Toast.LENGTH_LONG).show();
                }
                else if ("TSLA".equals(fonts[which])) {
                    al.add("TSLA");

                    md.notifyDataSetChanged();
                    Toast.makeText(LandingTest.this, "TSLA Selected", Toast.LENGTH_SHORT).show();
                } else if ("AMZN".equals(fonts[which])) {
                    al.add("AMZN");

                    md.notifyDataSetChanged();
//                Toast.makeText(LandingTest.this, "you cracked it", Toast.LENGTH_SHORT).show();
                } else if ("AAPL".equals(fonts[which])) {
                    al.add("AAPL");

                    md.notifyDataSetChanged();
//                Toast.makeText(LandingTest.this, "you hacked it", Toast.LENGTH_SHORT).show();
                } else if ("GOOGL".equals(fonts[which])) {
                    al.add("GOOGL");

                    md.notifyDataSetChanged();
//                Toast.makeText(LandingTest.this, "you digged it", Toast.LENGTH_SHORT).show();
                }
                saveIntoFirestore(al);
                setAddStocks(al);
            }
        });
        builder.show();

    }

    //saving into firebaseFirestore
    public void saveIntoFirestore(ArrayList<String> al) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        // Read from the database
        ValueEventListener changeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User value = dataSnapshot.getValue(User.class);
                String newStr = new String(updateStocks(al));
                Log.d(TAG, "String Value: " + newStr);
                Log.d(TAG, "Value is: " + value.fullName);
                Log.d(TAG, "Value is: " + value.stocks);
                value.stocks = newStr;
                data.stocks=newStr;
                myRef.setValue(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };
        ref.addValueEventListener(changeListener);

        //FireBase Firestore database
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String userId=user.getUid();
//        Map<String,Object> map=new HashMap<String,Object>();
//        map.put("stocks",updateStocks(al));
        data.stocks=updateStocks(al);

        db.collection(userId).document(data.fullName).set(data);

//        db.collection(userId).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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



//        myRef.removeEventListener(changeListener);

//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                User value = dataSnapshot.getValue(User.class);
//                Log.d(TAG, "Value is: " + value.fullName);
//                Log.d(TAG, "Value is: " + value.stocks);
//                value.stocks = newStr;
//                myRef.setValue(value);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });


//        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
//        String userId=user.getUid();
//        Map<String,Object> map=new HashMap<>();
//        map.put("Stocks",str);
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

    String updateStocks(ArrayList<String> arr) {
        String str = "";
        int i = 0;
        for (i = 0; i < al.size(); i++) {
            str += al.get(i) + ",";
        }
        return str;
    }

    void getStocks() {



//        // Read from the database
//        ValueEventListener changeListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                User value = dataSnapshot.getValue(User.class);
////                Log.d(TAG, "Value is: " + value.fullName);
////                Log.d(TAG, "Value is: " + value.stocks);
//                String str =value.stocks;
//                data=value;
//                String[] stocksArray= str.split(",");
//                for(int i=0;i<stocksArray.length;i++){
//                    if(stocksArray[i] != ""){
//                        temp.add(stocksArray[i]);
//                        Log.d(TAG,temp.get(i)+", ");
//                    }
//                }
//
//                Log.d(TAG,"String Value: "+str);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        };
//        myRef.addValueEventListener(changeListener);
//
//        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
//        String userId=user.getUid();
//        db.collection(userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                if(queryDocumentSnapshots.isEmpty()){
//                    Log.d(TAG,"SUCCESS But empty");
//                    return;
//                }
//                else{
//                    List<User> value= queryDocumentSnapshots.toObjects(User.class);
//                    String[]  arr= value.get(0).stocks.split(",");
//                    for(int i=0;i<arr.length;i++){
//                        if(arr[i]!=""){
//                            temp.add(arr[i]);
//                        }
//                    }
//                    Log.d(TAG, "onSuccess: " + value.get(0).stocks);
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
//            }
//        });




//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                User value = dataSnapshot.getValue(User.class);
//                Log.d(TAG, "Value is: " + value.fullName);
//                Log.d(TAG, "Value is: " + value.stocks);
//                String str =value.stocks;
//                String[] stocksArray= str.split(",");
//                for(int i=0;i<stocksArray.length;i++){
//                    if(stocksArray[i] != ""){
//                     al.add(stocksArray[i]);
//                    }
//                }
//
//                Log.d(TAG,"String Value: "+str);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });



//        String stcks= str[0];
//        String[] stocksArray= stcks.split(",");
//        for(int i=0;i<stocksArray.length;i++){
//            if(stocksArray[i] != ""){
//                arr.add(stocksArray[i]);
//            }
//        }

     }


    //Saving Instance
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putStringArrayList("recycler",al);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        al=new ArrayList<>(savedInstanceState.getStringArrayList("recycler"));
        md=new LandingAdapter(this,al);
        rv.setAdapter(md);
    }
//    boolean deleteDialogResult=false;
//    public boolean showDeleteDialog(){
//
//        AlertDialog.Builder  alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage("Are you sure you want to delete the item?");
//        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(LandingTest.this, "Item deleted", Toast.LENGTH_SHORT).show();
////                rv.setAdapter(md);
//                md.notifyDataSetChanged();
//
//                deleteDialogResult=true;
//            }
//        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(LandingTest.this, "Item restored", Toast.LENGTH_SHORT).show();
//                deleteDialogResult=false;
//                rv.setAdapter(md);
//            }
//        });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//        return deleteDialogResult;
//    }

    void setAddStocks(ArrayList<String> al){

        if(al.isEmpty()){
            img.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }
        else{
            img.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
    }


    //Options Menu
    // two methods need to be overridden ( inflate the menu)

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch(id){
            case R.id.item1:
                startActivity(new Intent(LandingTest.this,UserProfile.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                Toast.makeText(getApplicationContext(),"PROFILE Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.item2:
                startActivity(new Intent(LandingTest.this,Tutorial.class));
                Toast.makeText(getApplicationContext(),"TUTORIAL Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.item3:
                startActivity(new Intent(LandingTest.this,AboutUs.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                Toast.makeText(getApplicationContext(),"ABOUT US Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.item4:
                startActivity(new Intent(LandingTest.this,ContactUs.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                Toast.makeText(getApplicationContext(),"CONTCT US Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.item5:
                //Logout user
                //saving into firebase
                saveIntoFirestore(al);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(LandingTest.this, LoginUser.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                Toast.makeText(this, "logging out", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}