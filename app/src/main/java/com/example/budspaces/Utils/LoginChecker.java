package com.example.budspaces.Utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.budspaces.R;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class LoginChecker {
    public static void animationShakeError(Context context, View view) {
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        view.startAnimation(shake);
    }

    public static boolean validateEmail(Context context, TextInputLayout textInputLayout) {
        String input = textInputLayout.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            textInputLayout.setError(context.getText(R.string.field_cant_be_empty));
            animationShakeError(context, textInputLayout);
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            textInputLayout.setError(context.getText(R.string.invalid_email));
            animationShakeError(context, textInputLayout);
            return false;
        }

        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
        return true;
    }

    public static boolean validatePassword(Context context, TextInputLayout textInputLayout) {
        String input = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString().trim();

        if (input.isEmpty()) {
            textInputLayout.setError(context.getText(R.string.field_cant_be_empty));
            animationShakeError(context, textInputLayout);
            return false;
        }

        if (input.length() < 6) {
            textInputLayout.setError(context.getText(R.string.invalid_password));
            animationShakeError(context, textInputLayout);
            return false;
        }

        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
        return true;
    }

    public static boolean validatePasswordConfirmation(Context context, TextInputLayout textInputLayout, TextInputLayout passwordLayout) {
        if (validatePassword(context, textInputLayout)) {
            String input = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(passwordLayout.getEditText()).getText().toString().trim();

            if (!input.equals(password)) {
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError("password mismatch");
                animationShakeError(context, textInputLayout);
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean validateName(Context context, TextInputLayout textInputLayout) {
        String input = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString().trim();

        if (input.isEmpty()) {
            textInputLayout.setError(context.getText(R.string.field_cant_be_empty));
            animationShakeError(context, textInputLayout);
            return false;
        }

        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
        return true;
    }

    public static boolean validateCountry(Context context, TextInputLayout textInputLayout) {
        String input = textInputLayout.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            textInputLayout.setError(context.getText(R.string.field_cant_be_empty));
            animationShakeError(context, textInputLayout);
            return false;
        }

        if (!Arrays.asList(context.getResources().getStringArray(R.array.countries_array)).contains(input)) {
            textInputLayout.setError(context.getText(R.string.wrong_country));
            animationShakeError(context, textInputLayout);
            return false;
        }

        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
        return true;
    }

    public static boolean validateBirthdate(Context context, TextView textView, View view) {
        String input = textView.getText().toString().trim();

        if (input.isEmpty()) {
            textView.setError(context.getText(R.string.field_cant_be_empty));
            animationShakeError(context, view);
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(input);
            assert date != null;
            if (date.after(new Date())) {
                textView.setError(context.getText(R.string.field_cant_be_empty));
                animationShakeError(context, view);
                return false;
            }
        } catch (Exception e) {
            Log.e("birthdate parsing", e.getMessage()+"");
        }

        textView.setError(null);
//        textInputLayout.setErrorEnabled(false);
        return true;
    }

    public static boolean validateEventdate(Context context, TextView textView, View view) {
        String input = textView.getText().toString().trim();

        if (input.isEmpty()) {
            textView.setError(context.getText(R.string.incorrect_date));
            animationShakeError(context, view);
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(input);
            assert date != null;
            if (date.before(new Date())) {
                textView.setError(context.getText(R.string.incorrect_date));
                animationShakeError(context, view);
                return false;
            }
        } catch (Exception e) {
            Log.e("eventdate parsing", e.getMessage()+"");
        }

        textView.setError(null);
//        textInputLayout.setErrorEnabled(false);
        return true;
    }

    public static boolean isEmpty(TextInputLayout layout) {
        return Objects.requireNonNull(layout.getEditText()).getText().toString().isEmpty();
    }
}
