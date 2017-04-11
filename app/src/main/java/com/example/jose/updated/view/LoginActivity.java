package com.example.jose.updated.view;

import android.content.Context;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
    private Button newUserLoginButton;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextView skipLoginTextView;
    private TextView backTextView;
    private boolean newUserClicked = false;
    private boolean loginSkipped;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ghms.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                    //user is logged in
                    loginSkipped = false;
                    openMainActivity();
                }
            }
        };

        findViews();
        setClickListeners();
    }

    private void openMainActivity() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("loginSkipped", loginSkipped);
        startActivity(intent);
    }

    private void findViews() {
        googleLoginButton = (Button) findViewById(R.id.google_login_button);
        emailLoginButton = (Button) findViewById(R.id.email_login_button);
        loginButton = (Button) findViewById(R.id.login_button);
        newUserLoginButton = (Button) findViewById(R.id.new_user_button);
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
                newUserClicked = false;
                showEditTextAndHideButtons();
            }
        });

        newUserLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUserClicked = true;
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
                    if (newUserClicked) {
                        createNewUser(email, password);
                    } else {
                        loginWithEmail(email, password);
                    }
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
        newUserLoginButton.startAnimation(slideInRight);

        googleLoginButton.setVisibility(VISIBLE);
        emailLoginButton.setVisibility(VISIBLE);
        newUserLoginButton.setVisibility(VISIBLE);

        googleLoginButton.setClickable(true);
        emailLoginButton.setClickable(true);
        newUserLoginButton.setClickable(true);

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
        backTextView.setClickable(true);

        googleLoginButton.startAnimation(slideOutRight);
        emailLoginButton.startAnimation(slideOutRight);
        newUserLoginButton.startAnimation(slideOutRight);

        googleLoginButton.setVisibility(INVISIBLE);
        emailLoginButton.setVisibility(INVISIBLE);
        newUserLoginButton.setVisibility(INVISIBLE);
        googleLoginButton.setClickable(false);
        newUserLoginButton.setClickable(false);
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

    private void showLoginFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        builder.setMessage(R.string.login_failed_message);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void loginWithEmail(final String email, String password) {
        if (firebaseAuth.getCurrentUser() != null) {
            showUserAlreadyLoggedInDialog();
        }else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                showLoginFailedDialog();
                            }else{
                                loginSkipped = false;
                                openMainActivity();
                            }
                        }
                    });
        }
    }

    private void showUserAlreadyLoggedInDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        builder.setMessage(firebaseAuth.getCurrentUser().getEmail().concat(" "+getResources().getString((R.string.user_already_logged_in))));
        builder.setNegativeButton(R.string.skip_log_in, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                loginSkipped = true;
                openMainActivity();
            }
        });
        builder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseAuth.signOut();
                Toast.makeText(getApplicationContext(), R.string.logged_out, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void createNewUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            showLoginFailedDialog();
                        }else{
                            loginSkipped = false;
                            openMainActivity();
                        }
                    }


                });
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
        builder.setPositiveButton(R.string.skip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loginSkipped = true;
                openMainActivity();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
