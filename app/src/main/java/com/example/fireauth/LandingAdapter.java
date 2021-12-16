package com.example.fireauth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LandingAdapter extends RecyclerView.Adapter<LandingAdapter.MyViewHolder> {
    Context ct;
    ArrayList<String> al;
    public static final String EXTRA_STRING="com.example.application.fireauth.EXTRA_TEXT";

    public LandingAdapter(Context context, ArrayList<String> al) {
        this.ct=context;
        this.al=al;
    }

    @NonNull
    @Override
    public LandingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(ct);

        View v1 = li.inflate(R.layout.layout_recyclerview, parent, false);

        return new MyViewHolder(v1);
    }

    @Override
    public void onBindViewHolder(@NonNull LandingAdapter.MyViewHolder holder, int position) {

        String stock=al.get(position);
        holder.stockName.setText(stock);
        float num= (float) (2000 - 50 + (Math.random() * (50 + 50)));

        switch(stock){
            case "TSLA": num= (float) 975.99;break;
            case "AMZN": num=(float)3466.30;break;
            case "AAPL": num=(float)179.33;break;
            case "GOOGL": num=(float)2947.37;break;
            default: num= (float) (2000 - 50 + (Math.random() * (50 + 50)));
        }
        holder.textViewPrice.setText(Float.toString(num));
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView stockName,textViewPrice;
        ImageView ivRemove;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            stockName=itemView.findViewById(R.id.tv_stock);
            textViewPrice=itemView.findViewById(R.id.tv_price);
            ivRemove=itemView.findViewById(R.id.ivRemove);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll);

            ivRemove.setVisibility(View.INVISIBLE);

            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    delete(getAdapterPosition());
//                    if(view.getVisibility()==View.VISIBLE){
//                        ivRemove.setVisibility(View.INVISIBLE);
//                    }
//                    ivRemove.setVisibility(View.VISIBLE);
//                    ivRemove.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            delete(getAdapterPosition());
//                            ivRemove.setVisibility(View.INVISIBLE);
//                        }
//                    });
                    return true;
                }
            });

            //onclick for new page
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSecondPage();
                }
            });

        }

        public void openSecondPage() {
            Intent intent=new Intent(ct,ProfileActivity.class);
            intent.putExtra(EXTRA_STRING,stockName.getText().toString());
            ct.startActivity(intent);
        }

        public void delete(int position){
            AlertDialog.Builder builder= new AlertDialog.Builder(ct);
            builder.setMessage("Are you sure to delete?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    al.remove(position);
                    notifyDataSetChanged();
                    dialogInterface.cancel();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    notifyDataSetChanged();
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();


//            Toast.makeText(ct,"deleting  "+al.get(position),Toast.LENGTH_SHORT).show();
//            al.remove(position);
//            notifyDataSetChanged();

        }
    }
}
