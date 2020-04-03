package com.example.employeemanagement.Screen;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.employeemanagement.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private EditText textEmail,textPassword;
    private Button signinEmailButton,googleLogin,facebookLogin;
    private String TAG="signIn";
    private AlertDialog alertDialog1;
    private int RC_SIGN_IN=101;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private SignInButton signInButton;
    private LoginButton loginButton;
    private AuthCredential credential;
    private String txtEmail,txtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @SuppressLint("WrongViewCast")
    public void init()
    {
        textEmail = findViewById(R.id.emailSignin);
        textPassword = findViewById(R.id.passwordSignin);
        signinEmailButton = findViewById(R.id.signinButton);

        mAuth = FirebaseAuth.getInstance();
        googleSignInOption();
        googleLogin = findViewById(R.id.google_login);
        facebookLogin = findViewById(R.id.facebook_login);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signinEmailButton.setOnClickListener(this);
        facebookLogin.setOnClickListener(this);
        googleLogin.setOnClickListener(this);
        loginButton = findViewById(R.id.facebook_login_button);
        FacebookCallback();
    }
    private void createUser()
    {
        Log.e("emaillo",""+txtEmail+txtPassword);
        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            EmailSignIn();

                        }
                        else {
                            showErrorMessage(getString(R.string.errorAuthentication));
                            Log.e("ErrorMessage",""+String.valueOf(R.string.errorAuthentication));
                        }
                    }
                });

        Log.e("User",""+mAuth.getCurrentUser());
    }

    private void FacebookCallback()
    {
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        mCallbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Log.e("Success", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                //Log.e("FacebookCancel", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                //Log.e("ErrorFacebook", "facebook:onError", error);
                // ...
            }
        });
    }

    private void googleSignInOption()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void signinEmail()
    {


        if(TextUtils.isEmpty(textEmail.getText().toString()) && TextUtils.isEmpty(textPassword.getText().toString()))
        {
            showErrorMessage(getString(R.string.errorEmptyData));
        }
        else
        {
            if(!Patterns.EMAIL_ADDRESS.matcher(textEmail.getText().toString().trim()).matches())
            {
                AlertMessage(getString(R.string.alertEmailTitle),String.valueOf(R.string.errorEmailValidate));
            }
            else if (textPassword.getText().toString().length()<5 && textPassword.getText().toString().length()>15)
            {

                showErrorMessage(getString(R.string.errorPasswordLength));
                Log.e("ErrorMSgs",""+R.string.errorPasswordLength);
            }
            else{
                //SignInAnonymously(email,password);
                txtEmail = textEmail.getText().toString();
                txtPassword = textPassword.getText().toString();
                EmailSignIn();
            }

        }
    }
    private void AlertMessage(String title, String message)
    {
        alertDialog1.setTitle(title);
        alertDialog1.setMessage(message);
        alertDialog1.setIcon(R.drawable.ic_warning_black_24dp);
        alertDialog1.show();
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
    private void EmailSignIn()
    {

        //credential = EmailAuthProvider.getCredential(email, password);
        mAuth.signInWithEmailAndPassword(txtEmail, txtPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            createUser();
                            /*Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Email Authentication Failed!", Snackbar.LENGTH_LONG);
                            View view = snackbar.getView();
                            view.setBackground(getResources().getDrawable(R.drawable.custom_button_drawable));
                            TextView tv = (TextView)view.findViewById(R.id.snackbar_text);
                            tv.setTextColor(getResources().getColor(R.color.colorBackground));
                            snackbar.show();*/
                        }

                        // ...
                    }
                });
        /*mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finishAffinity();
                    }
                    else
                    {

                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Email Authentication Failed!", Snackbar.LENGTH_LONG);
                        View view = snackbar.getView();
                        view.setBackground(getResources().getDrawable(R.drawable.custom_button_drawable));
                        TextView tv = (TextView)view.findViewById(R.id.snackbar_text);
                        tv.setTextColor(getResources().getColor(R.color.colorBackground));
                        snackbar.show();
                    }
                }
            });
            */

    }


    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

                // ...
            }
        }
    }

    /*@Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }*/
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Logged in with Google Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finishAffinity();

                        } else {
                            showErrorMessage(getString(R.string.errorGoogleSignIn));
                        }

                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {

        credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Logged in with Facebook Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finishAffinity();

                        } else {
                            showErrorMessage(getString(R.string.errorAuthentication));
                        }

                    }
                });
    }

    //Onclick
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login:
                signIn();
                break;
            case R.id.signinButton:
                signinEmail();
                break;
            case R.id.facebook_login:
                loginButton.performClick();
                break;
        }

    }
}
