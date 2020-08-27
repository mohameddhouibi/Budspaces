package com.example.budspaces.Views.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.budspaces.Enums.ProviderType;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.R;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.Auth.SigninViewModel;
import com.example.budspaces.ViewModels.Auth.SignupViewModel;
import com.facebook.login.LoginManager;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.budspaces.Kotlin.ViewExtensionKt.makeStatusBarWhite;
import static com.example.budspaces.Utils.LoginChecker.validateEmail;
import static com.example.budspaces.Utils.LoginChecker.validatePassword;

public class SignInActivity extends AppCompatActivity implements TextWatcher {

    @BindView(R.id.mail)
    TextInputLayout mail;
    @BindView(R.id.password)
    TextInputLayout password;

    private SigninViewModel signinViewModel;
    private Disposable internetDisposable;
    private boolean isConnected = false;

    private final int RC_SIGN_IN = 42;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        makeStatusBarWhite(this);

        signinViewModel = new ViewModelProvider(this).get(SigninViewModel.class);
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, Utils.getGso());

        signinViewModel.getProvider().observe(this, providerType -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);

            switch (providerType) {
                case mail:
                    return;
                case facebook:
                    intent.putExtra("provider", "facebook");
                    break;
                case google:
                    intent.putExtra("provider", "google");
                    break;
            }

            startActivity(intent);
        });
    }

    @OnClick({R.id.facebook, R.id.google, R.id.recover, R.id.signin, R.id.signup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.facebook:
                if (!isConnected) {
                    Toast.makeText(this, getText(R.string.internet_required), Toast.LENGTH_LONG).show();
                    return;
                }

                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                signinViewModel.loginWithFacebook();
                break;
            case R.id.google:
                if (!isConnected) {
                    Toast.makeText(this, getText(R.string.internet_required), Toast.LENGTH_LONG).show();
                    return;
                }

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.recover:
                startActivity(new Intent(SignInActivity.this, RecoverActivity.class));
                break;
            case R.id.signin:
                mail.setErrorEnabled(true);
                password.setErrorEnabled(true);
                if (validateEmail(this, mail) && validatePassword(this, password)) {
                    if (!isConnected) {
                        Toast.makeText(this, getText(R.string.internet_required), Toast.LENGTH_LONG).show();
                        return;
                    }
                    signinViewModel.login(Objects.requireNonNull(mail.getEditText()).getText().toString(),
                            Objects.requireNonNull(password.getEditText()).getText().toString());
                }
                break;
            case R.id.signup:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        if (Objects.requireNonNull(mail.getEditText()).hashCode() == s.hashCode()) {
            mail.setErrorEnabled(false);
        } else if (Objects.requireNonNull(password.getEditText()).hashCode() == s.hashCode()) {
            password.setErrorEnabled(false);
        }
    }

    @Override protected void onResume() {
        super.onResume();

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(connected -> {
                AppRetrofit.setIsConnected(connected);
                this.isConnected = connected;
            });

        signinViewModel.getIsLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.putExtra("verified", signinViewModel.isVerified());
                startActivity(intent);
            }
        });

        signinViewModel.getErrorMessage().observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            mGoogleSignInClient.signOut();
        });

        Objects.requireNonNull(mail.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(password.getEditText()).addTextChangedListener(this);
    }

    @Override protected void onPause() {
        super.onPause();
        Utils.safelyDispose(internetDisposable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signinViewModel.getCallbackManager().onActivityResult(requestCode, resultCode, data);

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
            signinViewModel.loginWithGoogle(account);
        } catch (ApiException e) {
            Log.w("Google Signin", "handleSignInResult:error", e);
        }
    }
}