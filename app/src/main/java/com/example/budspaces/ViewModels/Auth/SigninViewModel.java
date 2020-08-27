package com.example.budspaces.ViewModels.Auth;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Enums.ProviderType;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.AuthRequests;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<ProviderType> providerSignUp = new MutableLiveData<>();

    private boolean verified;
    private CallbackManager mCallbackManager;
    private final String TAG_FB = "facebook";

    public SigninViewModel() {
        isLoggedIn.setValue(false);
        verified = false;
        providerSignUp.setValue(ProviderType.mail);
        mCallbackManager = CallbackManager.Factory.create();
    }

    public CallbackManager getCallbackManager() { return mCallbackManager; }
    public LiveData<Boolean> getIsLoggedIn() { return isLoggedIn; }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<ProviderType> getProvider() {
        return providerSignUp;
    }
    public boolean isVerified() { return verified; }

    public void login(String mail, String password) {
        String queryBody = AuthRequests.getLoginRequest(mail, password).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance()
        .getService()
        .login(body)
        .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
                handleLogin(response, ProviderType.mail);
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void loginWithGoogle(GoogleSignInAccount account) {
        AppRetrofit.getInstance()
        .getService()
        .loginWithGoogle(account.getIdToken())
        .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
                handleLogin(response, ProviderType.google);
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void loginWithFacebook() {
        LoginManager.getInstance().registerCallback(mCallbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Log.d(TAG_FB, "Success");
                    handleFbToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    // App code
                    Log.d(TAG_FB, "Cancel");
                    errorMessage.postValue("Cancel");
                    isLoggedIn.postValue(false);
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    if (exception instanceof FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                    }
                    Log.d(TAG_FB, "Error: " + exception.toString());
                    errorMessage.postValue(exception.toString());
                    isLoggedIn.postValue(false);
                }
            });
    }

    private void handleFbToken(AccessToken token) {
        AppRetrofit.getInstance()
        .getService()
        .loginWithFacebook(token.getToken())
        .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
                handleLogin(response, ProviderType.facebook);
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void handleLogin(@NotNull Response<ServerResponse> response, ProviderType type) {
        if (!response.isSuccessful()) {
            try {
                assert response.errorBody() != null;
                if (response.code() == 400 && type != ProviderType.mail)
                    providerSignUp.postValue(type);
                else {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    errorMessage.postValue(jObjError.getJSONObject("error").getString("message"));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            assert response.body() != null;
            String val = response.headers().get("auth-token");
            String id = response.headers().get("id");

            Cache.getInstance().saveSession("auth-token", val);
            Cache.getInstance().saveSession("userId", id);

            if (type.equals(ProviderType.mail)) {
                verified = Boolean.parseBoolean(response.headers().get("verified"));
            } else {
                verified = true;
            }

            Cache.getInstance().saveSession("provider", type.name());
            subscribeToTopics(val);
        }
    }

    private void subscribeToTopics(String token) {
        AppRetrofit.getInstance()
        .getService()
        .getTopics(token)
        .enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NotNull Call<List<String>> call, @NotNull Response<List<String>> response) {
                if (!response.isSuccessful()) {
                    try {
                        assert response.errorBody() != null;
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        errorMessage.postValue(jObjError.getJSONObject("error").getString("message"));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    assert response.body() != null;
                    for (String topic : response.body()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(topic);
                    }

                    isLoggedIn.postValue(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<String>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
