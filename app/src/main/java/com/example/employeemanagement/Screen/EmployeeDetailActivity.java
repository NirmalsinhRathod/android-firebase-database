package com.example.employeemanagement.Screen;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.employeemanagement.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeDetailActivity extends AppCompatActivity {

    private TextView textName,textContact, textDesignation, textAbout;
    private TextView textEmail,textGender,textSalry,textAddress;
    private CircleImageView profilePic;
    private ImageView backImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private AppBarLayout mAppBarLayout;
    private NestedScrollView nestedScrollView;
    private LinearLayout linearLayout;
    private String EmployeeID;
    private String name, address, email, contact, gender, image, salary, designation, about;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_data);

        initSignleData();
        getEmployeeDetail();
        selectListeners();

    }
    private void getEmployeeDetail(){
        EmployeeID = getIntent().getStringExtra("EmployeeID");
        Log.e("ID",""+EmployeeID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Employee").child(EmployeeID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                email = dataSnapshot.child("email").getValue(String.class);
                contact = String.valueOf(dataSnapshot.child("contact").getValue(Long.class));
                designation = dataSnapshot.child("designation").getValue(String.class);
                address = dataSnapshot.child("address").getValue(String.class);
                gender = dataSnapshot.child("gender").getValue(String.class);
                salary = String.valueOf(dataSnapshot.child("salary").getValue(Float.class));
                about = dataSnapshot.child("about").getValue(String.class);
                image = dataSnapshot.child("image").getValue(String.class);

                textName.setText(name);
                textContact.setText(contact);
                textEmail.setText(email);
                textAddress.setText(address);
                textSalry.setText(salary);
                textGender.setText(gender);
                textDesignation.setText(designation);
                textAbout.setText(about);
                if(image!=null)
                {
                    Glide.with(EmployeeDetailActivity.this).load(image).into(profilePic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void initSignleData() {

        toolbar = findViewById(R.id.toolbarDetail);
        collapsingToolbarLayout = findViewById(R.id.collapseToolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        linearLayout = findViewById(R.id.linearlayoutDetail);
        nestedScrollView = findViewById(R.id.nestedView);

        textName = findViewById(R.id.detailName);
        textContact = findViewById(R.id.detailContact);
        textEmail = findViewById(R.id.detailEmail);
        textAddress = findViewById(R.id.detailAddress);
        textSalry = findViewById(R.id.detailSalary);
        textGender = findViewById(R.id.detailGender);
        profilePic = findViewById(R.id.detailImage);
        textDesignation = findViewById(R.id.detailDesignation);
        textAbout = findViewById(R.id.detailAbout);
        backImage = findViewById(R.id.imageBack);
    }

    private void selectListeners()
    {

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeDetailActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    profilePic.setVisibility(View.GONE);
                    collapsingToolbarLayout.setTitleEnabled(true);
                    collapsingToolbarLayout.setTitle(getString(R.string.textTitleEmployeeDetails));
                    linearLayout.setTop(60);
                }
                else if (isShow) {
                    isShow = false;
                    profilePic.setVisibility(View.VISIBLE);

                    collapsingToolbarLayout.setTitleEnabled(false);
                    linearLayout.setTop(160);
                }
            }
        });
    }
    public void onImageClick(View view)
    {
        Intent intent = new Intent(EmployeeDetailActivity.this, SingleImageActivity.class);
        intent.putExtra("image", image);
        startActivity(intent);
    }
}
