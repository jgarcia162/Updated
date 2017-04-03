package com.example.jose.updated.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jose.updated.R;
import com.example.jose.updated.model.UpdatedConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by Joe on 4/2/17.
 */

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SharedPreferences preferences;
    private Button googleLoginButton;
    private Button emailLoginButton;
    private Button loginButton;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextView skipLoginTextView;
    private TextView backTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is logged in

                } else {
                    //user not logged in
                }
            }
        };

        findViews();
        setClickListeners();
        preferences = getSharedPreferences(UpdatedConstants.PREFS_NAME, 0);
        //TODO uncomment this after testing, this skips login activity
//        if (preferences.getBoolean(UpdatedConstants.FIRST_TIME_PREF_TAG, true)) {
//            //Login
//        } else {
//            Intent intent = new Intent(getBaseContext(), MainActivity.class);
//            startActivity(intent);
//        }
    }

    private void findViews() {
        googleLoginButton = (Button) findViewById(R.id.google_login_button);
        emailLoginButton = (Button) findViewById(R.id.email_login_button);
        loginButton = (Button) findViewById(R.id.login_button);
        emailEditText = (TextInputEditText) findViewById(R.id.enter_email_edittext);
        passwordEditText = (TextInputEditText) findViewById(R.id.enter_password_edittext);
        skipLoginTextView = (TextView) findViewById(R.id.skip_login_text_view);
        backTextView = (TextView) findViewById(R.id.back_text_view);
    }

    private void setClickListeners() {
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithGoogle();
            }
        });

        emailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextAndHideButtons();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(emailEditText.getText());
                String password = String.valueOf(passwordEditText.getText());
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password) || !checkValidPassword(password)) {
                    showInvalidPasswordDialog();
                } else {
                    loginWithEmail(email, password);
                }
            }
        });

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEditTextAndShowButtons();
            }
        });
    }

    private void hideEditTextAndShowButtons() {
        Animation slideOutLeft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out_left);
        slideOutLeft.setDuration(500);
        Animation slideInRight = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in_right);
        slideInRight.setDuration(500);

        emailEditText.setVisibility(GONE);
        passwordEditText.setVisibility(GONE);
        loginButton.setVisibility(GONE);
        backTextView.setVisibility(GONE);
        backTextView.setClickable(false);

        googleLoginButton.startAnimation(slideInRight);
        emailLoginButton.startAnimation(slideInRight);

        googleLoginButton.setVisibility(VISIBLE);
        emailLoginButton.setVisibility(VISIBLE);
        googleLoginButton.setClickable(true);
        emailLoginButton.setClickable(true);

        emailEditText.startAnimation(slideOutLeft);
        passwordEditText.startAnimation(slideOutLeft);
        loginButton.startAnimation(slideOutLeft);
    }

    private void showEditTextAndHideButtons() {
        Animation slideOutRight = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out_right);
        slideOutRight.setDuration(500);
        Animation slideInLeft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in_left);
        slideInLeft.setDuration(500);

        emailEditText.setVisibility(VISIBLE);
        passwordEditText.setVisibility(VISIBLE);
        loginButton.setVisibility(VISIBLE);
        backTextView.setVisibility(VISIBLE);

        googleLoginButton.startAnimation(slideOutRight);
        emailLoginButton.startAnimation(slideOutRight);

        googleLoginButton.setVisibility(INVISIBLE);
        emailLoginButton.setVisibility(INVISIBLE);
        googleLoginButton.setClickable(false);
        emailLoginButton.setClickable(false);

        emailEditText.startAnimation(slideInLeft);
        passwordEditText.startAnimation(slideInLeft);
        loginButton.startAnimation(slideInLeft);
    }

    private void showInvalidPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        builder.setMessage(R.string.invalid_password_dialog_message);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private boolean checkValidPassword(String password) {
        return password.length() >= 6;
    }

    private void loginWithEmail(String email, String password) {

    }

    private void loginWithGoogle() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public void showSkipLoginDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        builder.setPositiveButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setTitle(R.string.reset_default_alert_title);
        builder.setMessage(R.string.skip_login_dialog_message);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
