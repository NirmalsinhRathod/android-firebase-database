package com.example.employeemanagement.Screen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.employeemanagement.Model.ModelEmployee;
import com.example.employeemanagement.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import de.hdodenhof.circleimageview.CircleImageView;

public class InsertEmployeeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private EditText employeeName, employeeContact, employeeAddress, employeeSalary, employeeEmail, employeeAbout;
    private Button submitButton;
    private ModelEmployee modelEmployee;
    private RadioGroup radioGroup;
    private RadioButton male,female;
    private String gender;
    private ImageView backButton;
    private Toolbar toolbar;
    private String ProfileImageUri;
    private Uri imageUri;
    private String[] perm = {"Camera","Gallery","Cancel"};
    private AlertDialog.Builder builder;
    private final static int IMAGE_PICK_CODE = 1001;
    private final static int IMAGE_CAPTURE_CODE=0;
    private CircleImageView circleImageView;
    private StorageReference storageReference;
    private Spinner employeeDesignationSpinner;
    private String employeeDesignation;
    private Dialog progressBar;
    private DatabaseReference myRef;
    String[] cameraPermission={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    String[] galleryPermission={Manifest.permission.READ_EXTERNAL_STORAGE};

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_employee);

        init();

        submitButton.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
    }


    private void ShowProgressBar()
    {
        progressBar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressBar.setContentView(R.layout.layout_progressbar);
        progressBar.show();
    }
    private void DismissProgressBar()
    {
        progressBar.dismiss();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init()
    {
        myRef = FirebaseDatabase.getInstance().getReference().child("Employee");

        progressBar = new Dialog(InsertEmployeeActivity.this);

        toolbar = findViewById(R.id.toolbarInsert);
        backButton = findViewById(R.id.backButtonInsert);
        backButton.setOnClickListener(this);

        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        circleImageView = findViewById(R.id.circleProfileImage);
        employeeName = findViewById(R.id.editName);
        employeeContact = findViewById(R.id.editContact);
        employeeAddress = findViewById(R.id.editAddress);
        employeeSalary = findViewById(R.id.editSalary);
        employeeEmail = findViewById(R.id.editEmail);
        employeeDesignationSpinner = findViewById(R.id.edit_desingation);
        employeeAbout = findViewById(R.id.editAbout);
        submitButton = findViewById(R.id.submitEmployee);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.designationlist, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        employeeDesignationSpinner.setAdapter(arrayAdapter);
        employeeDesignationSpinner.setOnItemSelectedListener(InsertEmployeeActivity.this);

        circleImageView.setImageResource(R.drawable.userplaceholder);

        builder = new AlertDialog.Builder(InsertEmployeeActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Select Any..");


        radioGroup = findViewById(R.id.radioSex);
        male = findViewById(R.id.radioMale);
        female = findViewById(R.id.radioFemale);
        if(male.isChecked())
        {
            gender = male.getText().toString();
        }
        else
        {
            gender = female.getText().toString();
        }


    }

    private void imageUpload()
    {


        ShowProgressBar();

        final StorageReference image = storageReference.child("Image"+imageUri.getLastPathSegment());
        image.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ProfileImageUri = String.valueOf(uri);
                        storeData(ProfileImageUri);
                    }
                });
            }
        });
    }
    private void storeData(String url)
    {
        String name = employeeName.getText().toString();
        Long contact = Long.parseLong(employeeContact.getText().toString());
        String address = employeeAddress.getText().toString();
        String salary = employeeSalary.getText().toString();
        String email = employeeEmail.getText().toString();
        String desingation = employeeDesignation;
        String about = employeeAbout.getText().toString();


        String id = myRef.push().getKey();
        modelEmployee = new ModelEmployee(id,url,name,gender,contact,address,salary,email,desingation,about);
        myRef.child(id).setValue(modelEmployee);
        DismissProgressBar();

        Toast.makeText(InsertEmployeeActivity.this, "Data uploaded Successfully :)", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(InsertEmployeeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showErrorMessage(String message)
    {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackground(getResources().getDrawable(R.drawable.custom_button_drawable));
        TextView tv = (TextView)view.findViewById(R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.colorBackground));
        snackbar.show();
    }

    private void validate()
    {
        if(!TextUtils.isEmpty(employeeName.getText().toString())
                && (!male.isSelected() || !female.isSelected())
                && !TextUtils.isEmpty(employeeContact.getText().toString())
                && !TextUtils.isEmpty(employeeAddress.getText().toString())
                && !TextUtils.isEmpty(employeeSalary.getText().toString())
                && !TextUtils.isEmpty(employeeEmail.getText().toString())
                && !TextUtils.isEmpty(employeeAbout.getText().toString()))
        {
            if(!Patterns.EMAIL_ADDRESS.matcher(employeeEmail.getText().toString().trim()).matches())
            {
                showErrorMessage(getString(R.string.errorEmailValidate));
            }
            else if(employeeDesignation.equals(getString(R.string.textSelectSpinner)))
            {
                showErrorMessage(getString(R.string.errorDesignation));
            }
            else{
                Query query = myRef.orderByChild("email").equalTo(employeeEmail.getText().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()>0) {
                            showErrorMessage("Email is already Registered.");

                        }else{
                            if(imageUri == null)
                            {
                                imageUri= Uri.parse("https://ethioexplorer.com/wp-content/uploads/2019/07/Author__Placeholder.png");
                                storeData(String.valueOf(imageUri));
                            }
                            else
                            {
                                imageUpload();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
        else
        {
            showErrorMessage(getString(R.string.errorEmptyData));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submitEmployee:
                validate();
                break;
            case R.id.backButtonInsert:
                Intent intent = new Intent(InsertEmployeeActivity.this, HomeActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.circleProfileImage:
                builder.setItems(perm, new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        if (perm[i].equals("Camera")) {

                            if(checkCameraPermission())
                            {
                                openCamera();
                            }
                            else
                            {
                                requestCameraPermission();
                            }

                        } else if (perm[i].equals("Gallery")) {
                            if(checkGalleryPermission())
                            {
                                openGallery();
                            }
                            else
                            {
                                requestGalleryPermission();
                            }


                        } else if (perm[i].equals("Cancel")) {
                            dialogInterface.dismiss();
                        }

                    }
                });
                AlertDialog alert11 = builder.create();
                alert11.show();
                break;
        }
    }

    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(InsertEmployeeActivity.this,galleryPermission,IMAGE_PICK_CODE);
    }
    private void requestCameraPermission()
    {

        ActivityCompat.requestPermissions(InsertEmployeeActivity.this,cameraPermission,IMAGE_CAPTURE_CODE);
    }
    private boolean checkCameraPermission()
    {
        for(int i=0;i<cameraPermission.length;i++){
            int result= ContextCompat.checkSelfPermission(InsertEmployeeActivity.this,cameraPermission[i]);
            if(result== PackageManager.PERMISSION_GRANTED){
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    private boolean checkGalleryPermission() {
        for(int i=0;i<galleryPermission.length;i++){
            int result= ContextCompat.checkSelfPermission(InsertEmployeeActivity.this,galleryPermission[i]);
            if(result== PackageManager.PERMISSION_GRANTED){
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case IMAGE_CAPTURE_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    requestCameraPermission();
                }
                break;
            case IMAGE_PICK_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openGallery();
                }
                else{
                    requestGalleryPermission();
                }
                break;

        }

    }
    private void openCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAPTURE_CODE);
    }

    private void openGallery()
    {
        Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(in, IMAGE_PICK_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE)
            {

                 imageUri = data.getData();
                 CropImage.activity(imageUri).start(InsertEmployeeActivity.this);
                 Glide.with(InsertEmployeeActivity.this).load(imageUri).into(circleImageView);

            }
            else if(resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE)
            {
                //CropImage.activity(imageUri).start(InsertEmployeeActivity.this);
                Bitmap myBitmap = (Bitmap) data.getExtras().get("data");
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), myBitmap, "Title", null);
                imageUri =  Uri.parse(path);
                CropImage.activity(imageUri).start(InsertEmployeeActivity.this);
                circleImageView.setImageBitmap(myBitmap);


            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                circleImageView.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        employeeDesignation = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}