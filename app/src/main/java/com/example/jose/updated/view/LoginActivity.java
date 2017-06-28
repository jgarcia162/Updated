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

import com.example.jose.updated.BuildConfig;
import com.example.jose.updated.R;
import com.example.jose.updated.controller.DatabaseHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.jose.updated.model.UpdatedConstants.GOOGLE_SIGN_IN_REQUEST_CODE;

/**
 * Created by Joe on 4/2/17.
 */

public class LoginActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    private SharedPreferences preferences;
    private Button googleLoginButton;
    private Button emailLoginButton;
    private Button loginButton;
    private Button newUserLoginButton;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextView skipLoginTextView;
    private boolean newUserClicked = false;
    private boolean loginSkipped;
    private boolean loginFieldsHidden = true;
    private Animation slideOutLeft;
    private Animation slideInRight;
    private Animation slideOutRight;
    private Animation slideInLeft;
    private AlertDialog loggingInDialog;

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

        googleSignInOptions = getGoogleSignInOptions();
        googleApiClient = getGoogleApiClient(googleSignInOptions);

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
        createAnimations();
        loggingInDialog = createLoggingInDialog();
    }

    private void openMainActivity() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("loginSkipped", loginSkipped);
        if (loggingInDialog.isShowing()) {
            loggingInDialog.dismiss();
        }
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
                    Toast.makeText(LoginActivity.this, R.string.enter_valid_email, Toast.LENGTH_SHORT).show();
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
    }

    private void hideEditTextAndShowButtons() {
        loginFieldsHidden = true;

        emailEditText.setVisibility(GONE);
        passwordEditText.setVisibility(GONE);
        loginButton.setVisibility(GONE);

        googleLoginButton.startAnimation(slideInLeft);
        emailLoginButton.startAnimation(slideInLeft);
        newUserLoginButton.startAnimation(slideInLeft);

        googleLoginButton.setVisibility(VISIBLE);
        emailLoginButton.setVisibility(VISIBLE);
        newUserLoginButton.setVisibility(VISIBLE);

        googleLoginButton.setClickable(true);
        emailLoginButton.setClickable(true);
        newUserLoginButton.setClickable(true);

        emailEditText.startAnimation(slideOutRight);
        passwordEditText.startAnimation(slideOutRight);
        loginButton.startAnimation(slideOutRight);
    }

    private void createAnimations() {
        slideOutLeft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out_left);
        slideOutLeft.setDuration(500);
        slideInRight = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in_right);
        slideInRight.setDuration(500);

        slideOutRight = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out_right);
        slideOutRight.setDuration(500);
        slideInLeft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in_left);
        slideInLeft.setDuration(500);
    }

    @Override
    public void onBackPressed() {
        if (loginFieldsHidden) {
            super.onBackPressed();
        } else {
            hideEditTextAndShowButtons();
            loginFieldsHidden = true;
        }
    }

    private void showEditTextAndHideButtons() {
        loginFieldsHidden = false;

        emailEditText.setVisibility(VISIBLE);
        passwordEditText.setVisibility(VISIBLE);
        loginButton.setVisibility(VISIBLE);

        googleLoginButton.startAnimation(slideOutLeft);
        emailLoginButton.startAnimation(slideOutLeft);
        newUserLoginButton.startAnimation(slideOutLeft);

        googleLoginButton.setVisibility(INVISIBLE);
        emailLoginButton.setVisibility(INVISIBLE);
        newUserLoginButton.setVisibility(INVISIBLE);
        googleLoginButton.setClickable(false);
        newUserLoginButton.setClickable(false);
        emailLoginButton.setClickable(false);

        emailEditText.startAnimation(slideInRight);
        passwordEditText.startAnimation(slideInRight);
        loginButton.startAnimation(slideInRight);
    }

    private void showInvalidPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        builder.setMessage(R.string.invalid_password_dialog_message);
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    private boolean checkValidPassword(String password) {
        return password.length() >= 6;
    }

    private void showLoginFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        builder.setMessage(R.string.login_failed_message);
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    private void showNewUserFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        builder.setTitle(R.string.new_user_failed_title);
        builder.setMessage(R.string.new_user_failed_message);
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    private void loginWithEmail(final String email, String password) {
        loggingInDialog.show();
        if (firebaseAuth.getCurrentUser() != null) {
            showUserAlreadyLoggedInDialog();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                showLoginFailedDialog();
                            } else {
                                loginSkipped = false;
                                openMainActivity();
                            }
                        }
                    });
        }
    }

    private AlertDialog createLoggingInDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setView(dialog.getLayoutInflater().inflate(R.layout.logging_in_dialog, null));
        dialog.setOnDismissListener(this);
        return dialog;
    }

    private void showUserAlreadyLoggedInDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        builder.setMessage(firebaseAuth.getCurrentUser().getEmail().concat(" " + getResources().getString((R.string.user_already_logged_in))));
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
                new DatabaseHelper().emptyDatabase();
                Toast.makeText(getApplicationContext(), R.string.logged_out, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(this);
        dialog.show();
        if (loggingInDialog.isShowing()) {
            loggingInDialog.dismiss();
        }
    }


    private void createNewUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            showNewUserFailedDialog();
                        } else {
                            loginSkipped = false;
                            openMainActivity();
                        }
                    }


                });
    }

    private void loginWithGoogle() {
        loggingInDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent,GOOGLE_SIGN_IN_REQUEST_CODE);

    }

    @NonNull
    private GoogleApiClient getGoogleApiClient(GoogleSignInOptions googleSignInOptions) {
        return new GoogleApiClient.Builder(getBaseContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();
    }

    @NonNull
    private GoogleSignInOptions getGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(BuildConfig.defaultWebClientId)
                    .requestEmail()
                    .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                showLoginFailedDialog();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        //TODO create new user with google if user doesn't exist
                        loginSkipped = false;
                        openMainActivity();
                        if (!task.isSuccessful()) {
                            showLoginFailedDialog();
                        }
                    }
                });
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (loggingInDialog.isShowing()) {
            loggingInDialog.dismiss();
        }
    }
}
