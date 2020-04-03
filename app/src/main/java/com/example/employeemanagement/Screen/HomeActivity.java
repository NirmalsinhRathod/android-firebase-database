
package com.example.employeemanagement.Screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.employeemanagement.Adapter.EmployeeAdapter;
import com.example.employeemanagement.Model.ModelEmployee;
import com.example.employeemanagement.R;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    ArrayList<ModelEmployee> data;
    private ImageView logout;
    private FloatingActionButton floatingActionButton;
    private DatabaseReference databaseReference;
    EmployeeAdapter employeeAdapter;
    private Toolbar toolbarDashboard;
    public static boolean selected;
    public static CheckBox selectAll;
    public static ImageView delete;
    private TextView textEmpty;
    private Dialog progressBar;
    private ArrayList<String> EmployeeId;
    String[] permissions_all={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    private final static int request_code=1001;
    Boolean[] checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            init();
            ShowProgressBar();
            checkPermission();
            requestPermission();


        } else {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

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


    private void getEmployeeData()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Employee");
        databaseReference.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    String id = dataSnapshot1.child("id").getValue(String.class);
                    ModelEmployee employee = dataSnapshot1.getValue(ModelEmployee.class);
                    data.add(employee);

                    DismissProgressBar();
                    Log.e("Data RecylerSize",""+data.size());
                }
                checked=new Boolean[data.size()];
                Arrays.fill(checked, false);
                employeeAdapter = new EmployeeAdapter(HomeActivity.this, data,checked);
                recyclerView.setAdapter(employeeAdapter);

                if(data.size()==0)
                {
                    textEmpty.setVisibility(View.VISIBLE);
                    textEmpty.setText("There is No Data Available!");
                    DismissProgressBar();
                }
                else
                {
                    textEmpty.setVisibility(View.GONE);
                }
                employeeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Something is wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void init()
    {
        EmployeeId = new ArrayList<String>();

        progressBar = new Dialog(HomeActivity.this);
        selected=false;
        selectAll = findViewById(R.id.selectAll);
        delete = findViewById(R.id.deleteData);
        delete.setOnClickListener(this);
        textEmpty = findViewById(R.id.textNodata);

        toolbarDashboard = findViewById(R.id.toolbarEmployee);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);

        setSupportActionBar(toolbarDashboard);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        data=new ArrayList<ModelEmployee>();

        floatingActionButton  =findViewById(R.id.addEmployee);
        floatingActionButton.setOnClickListener(this);
        recyclerView = findViewById(R.id.employeeRecycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Arrays.fill(checked, true);
                    employeeAdapter = new EmployeeAdapter(HomeActivity.this, data,checked);
                    recyclerView.setAdapter(employeeAdapter);

                    Log.e("SelectedAll", ""+selectAll.isChecked());
                }
                else
                {
                    for (int i = 0; i < checked.length; i++) {
                        if(EmployeeAdapter.arrayId[i]!=null)
                        {
                            checked[i]=EmployeeAdapter.arrayId[i];
                        }
                        else
                        {
                            checked[i]=false;
                        }
                        Log.e("EmploID:",""+EmployeeAdapter.arrayId.length+EmployeeAdapter.arrayId[i]);
                        Log.e("EmploID:",""+EmployeeAdapter.arrayId.length+checked[i]);
                    }

                    employeeAdapter = new EmployeeAdapter(HomeActivity.this, data,checked);
                    recyclerView.setAdapter(employeeAdapter);
                }
                employeeAdapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                if(selected)
                {
                    delete.setVisibility(View.GONE);
                    selectAll.setVisibility(View.GONE);
                    employeeAdapter.notifyDataSetChanged();
                    selected=false;
                }
                else
                {
                    selected=true;
                    delete.setVisibility(View.VISIBLE);
                    selectAll.setVisibility(View.VISIBLE);
                }
                employeeAdapter = new EmployeeAdapter(HomeActivity.this, data,checked);
                recyclerView.setAdapter(employeeAdapter);
                employeeAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.addEmployee:
                Intent intent = new Intent(HomeActivity.this, InsertEmployeeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.logout:
                Toast.makeText(getApplicationContext(), "Logged out Successfully :)", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                if(LoginManager.getInstance()!=null)
                {
                    LoginManager.getInstance().logOut();
                }

                Intent intentLogout = new Intent(HomeActivity.this, MainActivity.class);
                intentLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentLogout);
                finishAffinity();
                break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(HomeActivity.this,permissions_all,request_code);
    }

    private boolean checkPermission() {
        for(int i=0;i<permissions_all.length;i++){
            int result= ContextCompat.checkSelfPermission(HomeActivity.this,permissions_all[i]);
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
            case request_code:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    getEmployeeData();
                }
                else{
                    getEmployeeData();
                }
        }

    }
}
