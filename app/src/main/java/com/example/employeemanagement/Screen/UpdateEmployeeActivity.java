package com.example.employeemanagement.Screen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.util.Log;
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
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.employeemanagement.Adapter.EmployeeAdapter;
import com.example.employeemanagement.Model.ModelEmployee;
import com.example.employeemanagement.R;
import com.google.android.gms.tasks.OnFailureListener;
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

public class UpdateEmployeeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener  {

    private String updatedId, updatedName, updatedAbout, updatedAddress, updatedEmail, updatedDesignation, updatedGender, updatedImageurl;
    private Long updatedContact;
    private String updatedSalary;
    private EditText editName,editAddress,editEmail,editContact,editSalary,editAbout;
    private RadioButton editMale,editFemale;
    private Spinner editDesignation;
    private CircleImageView editImage;
    private Toolbar toolbar;
    private ImageView backButton;
    private Uri imageUri;
    private String[] perm = {"Camera","Gallery","Cancel"};
    private AlertDialog.Builder builder;
    private final static int IMAGE_PICK_CODE = 1001;
    private final static int IMAGE_CAPTURE_CODE=0;
    private StorageReference storageReference;
    private Button updateButton;
    private ArrayAdapter<CharSequence> arrayAdapter;
    private String imageurl,name,email,designation,address,gender,about;
    private Long contact;
    private String salary;
    private RadioGroup radioGroup;
    private Dialog progressBar;
    private DatabaseReference databaseReference;
    String[] cameraPermission={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    String[] galleryPermission={Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_employee);

        updatedId=getIntent().getStringExtra("id");
        init();
        setData();
    }

    private void setData()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 name = dataSnapshot.child("name").getValue(String.class);
                 email = dataSnapshot.child("email").getValue(String.class);
                 contact = dataSnapshot.child("contact").getValue(Long.class);
                 designation = dataSnapshot.child("designation").getValue(String.class);
                 address = dataSnapshot.child("address").getValue(String.class);
                 gender = dataSnapshot.child("gender").getValue(String.class);
                 salary = dataSnapshot.child("salary").getValue(String.class);
                 about = dataSnapshot.child("about").getValue(String.class);
                 imageurl = dataSnapshot.child("image").getValue(String.class);

                editName.setText(name);
                editEmail.setText(email);
                editContact.setText(String.valueOf(contact));
                editDesignation.setSelection(arrayAdapter.getPosition(designation));
                editAddress.setText(address);
                updatedGender = gender;

                if(gender!=null)
                {
                    if(gender.equals("Female"))
                    {
                        radioGroup.check(R.id.updatedFemale);
                    }
                    else
                    {
                        radioGroup.check(R.id.updatedMale);
                    }
                }



                editSalary.setText(String.valueOf(salary));
                editAbout.setText(about);

                updatedImageurl = imageurl;
                Glide.with(getApplicationContext()).load(updatedImageurl).fitCenter().into(editImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateButton.setOnClickListener(this);
        editImage.setOnClickListener(this);
    }
    private void ShowProgressBar()
    {
        progressBar.show();
    }
    private void DismissProgressBar()
    {
        progressBar.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressBar.dismiss();
    }

    private void init()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference("Employee").child(updatedId);

        progressBar = new Dialog(UpdateEmployeeActivity.this);
        progressBar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressBar.setContentView(R.layout.layout_progressbar);

        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        editName = findViewById(R.id.updatedName);
        editEmail = findViewById(R.id.updatedEmail);
        editContact = findViewById(R.id.updatedContact);
        editDesignation= findViewById(R.id.updatedDesignation);
        editAddress = findViewById(R.id.updatedAddress);
        editSalary = findViewById(R.id.updatedSalary);
        editAbout = findViewById(R.id.updatedAbout);
        editName = findViewById(R.id.updatedName);
        editImage = findViewById(R.id.updatedImage);

        updateButton = findViewById(R.id.updateEmployee);

        toolbar = findViewById(R.id.toolbarInsert);
        backButton = findViewById(R.id.backButtonUpdate);
        backButton.setOnClickListener(UpdateEmployeeActivity.this);

        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.designationlist, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        editDesignation.setAdapter(arrayAdapter);
        editDesignation.setOnItemSelectedListener(UpdateEmployeeActivity.this);

        editImage.setImageResource(R.drawable.userplaceholder);

        builder = new AlertDialog.Builder(UpdateEmployeeActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Select Any..");


        radioGroup = findViewById(R.id.updatedRadiogroup);
        editFemale = findViewById(R.id.updatedFemale);
        editMale = findViewById(R.id.updatedMale);


    }

    private void validate()
    {
        if(!updatedImageurl.isEmpty()
                && !updatedName.isEmpty()
                && (!editMale.isSelected() || !editFemale.isSelected())
                && updatedContact != null
                && !updatedAddress.isEmpty()
                && updatedSalary != null
                && !updatedEmail.isEmpty()
                && !updatedAbout.isEmpty())
        {
            if(!Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString().trim()).matches())
            {
                showErrorMessage(getString(R.string.errorEmailValidate));
            }
            else if(updatedDesignation.equals(getString(R.string.textSelectSpinner)))
            {
                showErrorMessage(getString(R.string.errorDesignation));
            }
            else
            {
                updateEmployeeData(updatedId);
            }
        }
        else
        {
            showErrorMessage(getString(R.string.errorEmptyData));
        }
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
    private void getEmployeeData()
    {
        updatedName = editName.getText().toString();
        updatedEmail = editEmail.getText().toString();
        updatedContact = Long.parseLong(editContact.getText().toString());
        updatedAddress = editAddress.getText().toString();
        updatedSalary = editSalary.getText().toString();
        updatedAbout = editAbout.getText().toString();
    }
    private void updateEmployeeData(String id) {

        if(radioGroup.getCheckedRadioButtonId()!=-1)
        {
            RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
            updatedGender = radioButton.getText().toString();
        }



        if(imageurl.equals(updatedImageurl) && email.matches(updatedEmail) && name.matches(updatedName) && designation.equals(updatedDesignation) && gender.equals(updatedGender) && contact.equals(updatedContact) && address.equals(updatedAddress) && salary.equals(updatedSalary) && about.equals(updatedAbout))
        {
            showErrorMessage(getString(R.string.errorSameData));
        }
        else
            {
            if(!email.matches(updatedEmail))
            {
                String newEmailID=updatedEmail;
                DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("Employee");
                Query query = mReference.orderByChild("email").equalTo(newEmailID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()>0) {
                            showErrorMessage("Email is already Registered.");
                        }else{
                            doUpdate();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else{
                doUpdate();
            }
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
                        updatedImageurl = String.valueOf(uri);
                        deleteOldImage();
                        DismissProgressBar();
                    }
                });
            }
        });
    }

    private void doUpdate()
    {
        ModelEmployee updatedEmployeeModel = new ModelEmployee(updatedId, updatedImageurl, updatedName, updatedGender, updatedContact, updatedAddress, updatedSalary, updatedEmail, updatedDesignation, updatedAbout);
        databaseReference.setValue(updatedEmployeeModel);

        Toast.makeText(getApplicationContext(), "Employee Updated", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(UpdateEmployeeActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void deleteOldImage()
    {
        if(!imageurl.equals(getString(R.string.userPlaceholderPath)))
        {
            if(!imageurl.equals(updatedImageurl))
            {
                Uri image = Uri.parse("Image"+imageurl);
                Log.e("LAst", ""+image.getLastPathSegment());
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                StorageReference desertRef = storageRef.child(image.getLastPathSegment());
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showErrorMessage(getString(R.string.errorImageUpload));
                    }
                });
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.updateEmployee:
                getEmployeeData();
                validate();
                break;
            case R.id.backButtonUpdate:
                Intent intent = new Intent(UpdateEmployeeActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.updatedImage:
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
        ActivityCompat.requestPermissions(UpdateEmployeeActivity.this,galleryPermission,IMAGE_PICK_CODE);
    }

    private void requestCameraPermission()
    {

        ActivityCompat.requestPermissions(UpdateEmployeeActivity.this,cameraPermission,IMAGE_CAPTURE_CODE);
    }
    private boolean checkCameraPermission()
    {
        for(int i=0;i<cameraPermission.length;i++){
            int result= ContextCompat.checkSelfPermission(UpdateEmployeeActivity.this,cameraPermission[i]);
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
            int result= ContextCompat.checkSelfPermission(UpdateEmployeeActivity.this,galleryPermission[i]);
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
            Glide.with(UpdateEmployeeActivity.this).load(imageUri).into(editImage);
            CropImage.activity(imageUri).start(UpdateEmployeeActivity.this);

        }
        else if(resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE)
        {
            Bitmap myBitmap = (Bitmap) data.getExtras().get("data");
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), myBitmap, "Title", null);
            imageUri =  Uri.parse(path);
            editImage.setImageBitmap(myBitmap);
            CropImage.activity(imageUri).start(UpdateEmployeeActivity.this);


        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                editImage.setImageURI(imageUri);
                if(imageUri!=null)
                {
                    updatedImageurl= String.valueOf(imageUri);
                    imageUpload();

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updatedDesignation = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
