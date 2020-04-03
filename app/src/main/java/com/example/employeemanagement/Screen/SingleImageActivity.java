package com.example.employeemanagement.Screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.employeemanagement.R;
import com.github.chrisbanes.photoview.PhotoView;

public class SingleImageActivity extends AppCompatActivity {

    private String image;
    private ImageView imageClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);

        imageClose = findViewById(R.id.closeImage);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleImageActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        image = getIntent().getStringExtra("image");
        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        Glide.with(this).load(image).override(400, 400).into(photoView);
    }
}
