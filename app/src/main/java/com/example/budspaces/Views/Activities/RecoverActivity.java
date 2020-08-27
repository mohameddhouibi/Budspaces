package com.example.budspaces.Views.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.budspaces.R;
import com.example.budspaces.ViewModels.Auth.RecoverViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.budspaces.Kotlin.ViewExtensionKt.makeStatusBarWhite;
import static com.example.budspaces.Utils.LoginChecker.validateEmail;

public class RecoverActivity extends AppCompatActivity implements TextWatcher {
    @BindView(R.id.mail)
    TextInputLayout mail;

    private RecoverViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);
        ButterKnife.bind(this);
        makeStatusBarWhite(this);
        mViewModel = new ViewModelProvider(this).get(RecoverViewModel.class);

        Objects.requireNonNull(mail.getEditText()).addTextChangedListener(this);

        mViewModel.getErrorMessage().observe(this, msgErr -> {
            Toast.makeText(this, msgErr, Toast.LENGTH_SHORT).show();
        });

        mViewModel.getIsMailSent().observe(this, isMailSent -> {
            if (isMailSent) {
                Snackbar.make(findViewById(R.id.root), R.string.recover_mail_sent, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.recover, R.id.signup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recover:
                mail.setErrorEnabled(true);
                if (validateEmail(this, mail)) {
                    mViewModel.recoverPassword(Objects.requireNonNull(mail.getEditText()).getText().toString());
                }
                break;
            case R.id.signup:
                startActivity(new Intent(RecoverActivity.this, SignUpActivity.class));
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        mail.setErrorEnabled(false);
    }
}