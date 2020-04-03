package com.example.employeemanagement.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.employeemanagement.Model.ModelEmployee;
import com.example.employeemanagement.R;
import com.example.employeemanagement.Screen.HomeActivity;
import com.example.employeemanagement.Screen.EmployeeDetailActivity;
import com.example.employeemanagement.Screen.SingleImageActivity;
import com.example.employeemanagement.Screen.UpdateEmployeeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.Customview> {
    @NonNull
    private Context context;
    private ArrayList<ModelEmployee> arrayList;
    public static Boolean[] arrayId;
    private String url;
    public static ArrayList<ModelEmployee> templist;
    private Boolean[] checked;
    private ArrayList<String> idArray;



    public EmployeeAdapter(Context context, ArrayList<ModelEmployee> arrayList, Boolean[] checked)
    {
        this.context = context;
        this.arrayList=arrayList;
        this.checked=checked;
        arrayId = new Boolean[arrayList.size()];
        templist=new ArrayList<ModelEmployee>();
        idArray=new ArrayList<String>();


    }



    @Override
    public Customview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_items_employee, parent, false);
        return new Customview(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final Customview holder, final int position) {
        final ModelEmployee modelEmployee = arrayList.get(position);
        url = modelEmployee.getImage();
        Glide.with(context).load(url).into(holder.circleImageView);

        Log.e("NEWPOSition",""+checked[position]);

        if(HomeActivity.selected)
        {
            holder.checkItem.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.checkItem.setVisibility(View.GONE);
        }

        if (checked[position])
        {
            holder.checkItem.setChecked(true);
            idArray.add(arrayList.get(holder.getAdapterPosition()).getEmployeeId());
            templist.add(arrayList.get(holder.getAdapterPosition()));

            Log.e("IDS", ""+idArray);
        }
        else {
            holder.checkItem.setChecked(false);
            idArray.remove(arrayList.get(holder.getAdapterPosition()).getEmployeeId());
            Log.e("IDS", ""+idArray);
        }


        holder.checkItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if (idArray.contains(arrayList.get(holder.getAdapterPosition()).getEmployeeId())) {
                        idArray.remove(arrayList.get(holder.getAdapterPosition()).getEmployeeId());
                        templist.remove(arrayList.get(holder.getAdapterPosition()));

                    } else {
                        idArray.add(arrayList.get(holder.getAdapterPosition()).getEmployeeId());
                        templist.add(arrayList.get(holder.getAdapterPosition()));
                    }
                    checked[position]=true;
                    arrayId[position]=checked[position];
                }
                else
                {
                    idArray.remove(arrayList.get(holder.getAdapterPosition()).getEmployeeId());
                    checked[holder.getAdapterPosition()]=false;
                    for (int i = 0; i < arrayId.length; i++) {
                        arrayId[i]=checked[i];
                        Log.e("Tempo",""+arrayId[i]+checked[i]);
                    }
                    Log.e("PassedArray", ""+arrayId[holder.getAdapterPosition()]);
                    HomeActivity.selectAll.setChecked(false);
                }
                Log.e("IDS::INside", ""+idArray);

            }
        });

        HomeActivity.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                Uri image = Uri.parse(arrayList.get(holder.getAdapterPosition()).getImage());
                Log.e("LastSegment", ""+image.getLastPathSegment());
                StorageReference desertRef = storageRef.child(image.getLastPathSegment());
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("Success","Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.e("Failed","Failed");
                    }
                });
                int i=0;

                while(i<idArray.size())
                {

                    Log.e("Inside", ""+idArray.get(i));
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Employee").child(idArray.get(i));
                    ref.removeValue();
                    idArray.remove(i);
                }
                arrayList.removeAll(templist);
                HomeActivity.selectAll.setVisibility(View.GONE);
                HomeActivity.delete.setVisibility(View.GONE);
                HomeActivity.selected=false;
                notifyDataSetChanged();
                Log.e("Removed", ""+idArray.size()+" "+idArray);
            }
        });



        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Id", "");
                Intent intent = new Intent(context, UpdateEmployeeActivity.class);
                intent.putExtra("id",arrayList.get(position).getEmployeeId());
                context.startActivity(intent);
            }
        });
        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String imageUrl = arrayList.get(holder.getAdapterPosition()).getImage();
                Intent intent = new Intent(context, SingleImageActivity.class);
                intent.putExtra("image", imageUrl);
                context.startActivity(intent);
            }
        });

        holder.textName.setText(modelEmployee.getName());
        holder.textName.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String EmployeeID = arrayList.get(holder.getAdapterPosition()).getEmployeeId();
                Intent intent = new Intent(context, EmployeeDetailActivity.class);
                intent.putExtra("EmployeeID", EmployeeID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }


    public class Customview extends RecyclerView.ViewHolder{
        TextView textName;
        CircleImageView circleImageView;
        ImageView editButton;
        CheckBox checkItem;


        public Customview(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            circleImageView = itemView.findViewById(R.id.profileImage);
            editButton = itemView.findViewById(R.id.editButton);
            checkItem = itemView.findViewById(R.id.checkItem);


        }


    }


}
