package com.example.subletpark;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ViewHolder> {

    ArrayList<Parking_Class>datalist;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context=getApplicationContext();


    public ParkingAdapter(ArrayList<Parking_Class> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.address.setText(datalist.get(position).getAddress());
        holder.desc.setText(datalist.get(position).getDesc());
        holder.daily_price.setText(datalist.get(position).getPrice());
        holder.start_date.setText(datalist.get(position).getStart_date());
        holder.end_date.setText(datalist.get(position).getEnd_date());
        String uri=datalist.get(position).getUri();
        Uri uri1=Uri.parse(uri);
        Glide.with(holder.parking_image.getContext()).load(uri1).dontAnimate().into(holder.parking_image);
        //Picasso.with(holder.parking_image.getContext()).load(uri1).into(holder.parking_image);
        holder.parking_image.setImageURI(uri1);
        holder.parking_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent (Intent.ACTION_VIEW,uri1);
                holder.parking_image.getContext().startActivity(I);

            }
        });

        holder.whatsappicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid=datalist.get(position).getId();
                String msg="היי ראיתי את החניה שפרסמת בסאבלט פארק, האם עדיין רלוונטי? תודה!";



              db.collection("User").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                  @Override
                  public void onSuccess(DocumentSnapshot documentSnapshot) {
                      String phone= documentSnapshot.getData().get("phone").toString();
                      String phone1="+972"+phone;

                      boolean isInstalled=whatsappAvailable("com.whatsapp");
                      if (isInstalled){
                          Intent whatsapp= new Intent(Intent.ACTION_VIEW,Uri.parse("https://api.whatsapp.com/send?phone="+phone1+"&text="+msg));
                          holder.whatsappicon.getContext().startActivity(whatsapp);

                      }else {
                          Toast.makeText(holder.whatsappicon.getContext(),"whatsapp is not installed in your device",Toast.LENGTH_LONG).show();

                      }


                  }
              });

            }
        });

        holder.phoneicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uid=datalist.get(position).getId();

                db.collection("User").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String phone= documentSnapshot.getData().get("phone").toString();

                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                        callIntent.setData(Uri.parse("tel:"+phone));
                        holder.phoneicon.getContext().startActivity(callIntent);

                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView address;
        TextView desc;
        TextView daily_price;
        TextView start_date;
        TextView end_date;
        ImageView parking_image;
        ImageView phoneicon;
        ImageView whatsappicon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            address=itemView.findViewById(R.id.address);
            desc=itemView.findViewById(R.id.desc);
            daily_price=itemView.findViewById(R.id.daily_price);
            start_date=itemView.findViewById(R.id.start_date);
            end_date=itemView.findViewById(R.id.end_date);
            parking_image=itemView.findViewById(R.id.parking_image);
            phoneicon=itemView.findViewById(R.id.phoneicon);
            whatsappicon=itemView.findViewById(R.id.whatsappicon);


        }



    }

    private boolean whatsappAvailable(String uri){
        PackageManager packageManager = context.getPackageManager();
        boolean isInstalled;

            try {
                packageManager.getPackageInfo(uri,packageManager.GET_ACTIVITIES);
                isInstalled=true;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                isInstalled=false;
            }

        return isInstalled;

    }



}
