package com.example.budspaces.Views.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.budspaces.Enums.ProviderType;
import com.example.budspaces.Listeners.OnDateSetListener;
import com.example.budspaces.Models.FacebookProfile;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.R;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.Auth.SignupViewModel;
import com.example.budspaces.Views.Fragments.Settings.DatePickerFragment;
import com.facebook.login.LoginManager;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.budspaces.Kotlin.ViewExtensionKt.makeStatusBarWhite;
import static com.example.budspaces.Utils.LoginChecker.validateBirthdate;
import static com.example.budspaces.Utils.LoginChecker.validateCountry;
import static com.example.budspaces.Utils.LoginChecker.validateEmail;
import static com.example.budspaces.Utils.LoginChecker.validateName;
import static com.example.budspaces.Utils.LoginChecker.validatePassword;
import static com.example.budspaces.Utils.LoginChecker.validatePasswordConfirmation;

public class SignUpActivity extends AppCompatActivity implements TextWatcher {

    @BindView(R.id.autoComplete)
    AutoCompleteTextView autoComplete;
    @BindView(R.id.birthdate)
    TextView birthdate;
    @BindView(R.id.name)
    TextInputLayout name;
    @BindView(R.id.mail_layout)
    TextInputLayout mailLayout;
    @BindView(R.id.country_layout)
    TextInputLayout countryLayout;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.confirm_pass_layout)
    TextInputLayout confirmPassLayout;
    @BindView(R.id.birthday_layout)
    ConstraintLayout birthdayLayout;
    @BindView(R.id.facebook)
    ImageView facebook;
    @BindView(R.id.google)
    ImageView google;

    private SignupViewModel signupViewModel;
    private Disposable internetDisposable;
    private boolean isConnected = true;

    private final int RC_SIGN_IN = 42;
    private GoogleSignInClient mGoogleSignInClient;
    private ProviderType currentProvider = ProviderType.mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        makeStatusBarWhite(this);
        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, Utils.getGso());

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.budspaces", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String provider = getIntent().getStringExtra("provider");
        if (provider != null) {
            if (provider.equals("facebook"))
                facebook.callOnClick();
            else if (provider.equals("google"))
                google.callOnClick();
        }
    }

    private void countryPicker() {
        String[] items = getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        autoComplete.setAdapter(adapter);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(new OnDateSetListener() {
            @Override
            public void setDate(Date date) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                birthdate.setText(simpleDateFormat.format(date));
            }

            @Override
            public void setTime(String time) { }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
        birthdate.setError(null);
    }

    @OnClick({R.id.facebook, R.id.google, R.id.inscription, R.id.signin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.facebook:
                if (!isConnected) {
                    Toast.makeText(this, getText(R.string.internet_required), Toast.LENGTH_LONG).show();
                    return;
                }

                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday", "user_gender"));
                signupViewModel.registerWithFacebook();
                break;
            case R.id.google:
                if (!isConnected) {
                    Toast.makeText(this, getText(R.string.internet_required), Toast.LENGTH_LONG).show();
                    return;
                }

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.inscription:
                if (!isConnected) {
                    Toast.makeText(this, getText(R.string.internet_required), Toast.LENGTH_LONG).show();
                    return;
                }

                name.setErrorEnabled(true);
                countryLayout.setErrorEnabled(true);
                switch (currentProvider) {
                    case mail:
                        signupWithMail();
                        break;
                    case google:
                        if (validateName(this, name) && validateBirthdate(this, birthdate, birthdayLayout)
                                && validateCountry(this, countryLayout)) {
                            signupViewModel.registerWithGoogle(
                                    Objects.requireNonNull(name.getEditText()).getText().toString(),
                                    birthdate.getText().toString(),
                                    Objects.requireNonNull(countryLayout.getEditText()).getText().toString());
                        }
                        break;
                    case facebook:
                        if (validateName(this, name) && validateBirthdate(this, birthdate, birthdayLayout)
                                && validateCountry(this, countryLayout)) {
                            signupViewModel.handleFbUser(
                                    Objects.requireNonNull(name.getEditText()).getText().toString(),
                                    birthdate.getText().toString(),
                                    Objects.requireNonNull(countryLayout.getEditText()).getText().toString());
                        }
                        break;
                }
                break;
            case R.id.signin:
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                break;
        }
    }

    private void signupWithMail() {
        mailLayout.setErrorEnabled(true);
        passwordLayout.setErrorEnabled(true);
        confirmPassLayout.setErrorEnabled(true);
        if (validateName(this, name) && validateBirthdate(this, birthdate, birthdayLayout)
                && validateEmail(this, mailLayout) && validateCountry(this, countryLayout)
                && validatePassword(this, passwordLayout) && validatePasswordConfirmation(this, confirmPassLayout, passwordLayout)) {
            signupViewModel.register(Objects.requireNonNull(name.getEditText()).getText().toString(),
                    birthdate.getText().toString(), Objects.requireNonNull(mailLayout.getEditText()).getText().toString(),
                    Objects.requireNonNull(countryLayout.getEditText()).getText().toString(),
                    Objects.requireNonNull(passwordLayout.getEditText()).getText().toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (Objects.requireNonNull(name.getEditText()).getText().hashCode() == s.hashCode()) {
            name.setErrorEnabled(false);
        } else if (Objects.requireNonNull(mailLayout.getEditText()).getText().hashCode() == s.hashCode()) {
            mailLayout.setErrorEnabled(false);
        } else if (Objects.requireNonNull(countryLayout.getEditText()).getText().hashCode() == s.hashCode()) {
            countryLayout.setErrorEnabled(false);
        } else if (Objects.requireNonNull(passwordLayout.getEditText()).getText().hashCode() == s.hashCode()) {
            passwordLayout.setErrorEnabled(false);
        } else if (Objects.requireNonNull(confirmPassLayout.getEditText()).getText().hashCode() == s.hashCode()) {
            confirmPassLayout.setErrorEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connected -> {
                    AppRetrofit.setIsConnected(connected);
                    this.isConnected = connected;
                });

        Objects.requireNonNull(name.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(countryLayout.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(mailLayout.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(passwordLayout.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(confirmPassLayout.getEditText()).addTextChangedListener(this);

        countryPicker();

        signupViewModel.getIsRegistered().observe(this, isRegistered -> {
            if (isRegistered) {
                Intent intent = new Intent(SignUpActivity.this, CategoryChooserActivity.class);
                intent.putExtra("verified", signupViewModel.isVerified());
                startActivity(intent);
            }
        });

        signupViewModel.getErrorMessage().observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            mGoogleSignInClient.signOut();
        });

        signupViewModel.getFbProfile().observe(this, facebookProfile -> {
            if (facebookProfile != null) {
                updateUI(facebookProfile);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.safelyDispose(internetDisposable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signupViewModel.getCallbackManager().onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            assert account != null;
            updateUI(account);
//            signupViewModel.loginWithGoogle(account);
        } catch (ApiException e) {
            Log.w("Google Signin", "handleSignInResult:error", e);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        mailLayout.setVisibility(View.GONE);
        passwordLayout.setVisibility(View.GONE);
        confirmPassLayout.setVisibility(View.GONE);

        Objects.requireNonNull(name.getEditText()).setText(account.getDisplayName());
        currentProvider = ProviderType.google;
        signupViewModel.setAccount(account);
    }

    private void updateUI(FacebookProfile profile) {
        mailLayout.setVisibility(View.GONE);
        passwordLayout.setVisibility(View.GONE);
        confirmPassLayout.setVisibility(View.GONE);

        Objects.requireNonNull(name.getEditText()).setText(profile.getName());
        birthdate.setText(profile.getBirthdate());

        currentProvider = ProviderType.facebook;
    }
}