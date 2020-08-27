package com.example.budspaces.ViewModels.Auth;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Enums.ProviderType;
import com.example.budspaces.Models.FacebookProfile;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.AuthRequests;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupViewModel extends ViewModel {
    private MutableLiveData<Boolean> isRegistered = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<FacebookProfile> fbProfile = new MutableLiveData<>();
    private MutableLiveData<ProviderType> providerSignUp = new MutableLiveData<>();
    private boolean verified;

    private GoogleSignInAccount account = null;
    private CallbackManager mCallbackManager;
    private AccessToken accessToken = null;
    private final String TAG_FB = "facebook";

    public SignupViewModel() {
        isRegistered.setValue(false);
        fbProfile.setValue(null);
        verified = false;
        mCallbackManager = CallbackManager.Factory.create();
    }

    public CallbackManager getCallbackManager() { return mCallbackManager; }
    public LiveData<Boolean> getIsRegistered() {
        return isRegistered;
    }
    public LiveData<FacebookProfile> getFbProfile() { return fbProfile; }
    public LiveData<ProviderType> getProvider() {
        return providerSignUp;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public boolean isVerified() { return verified; }

    public void setAccount(GoogleSignInAccount account) {
        this.account = account;
    }

    public void register(String name, String birthdate, String mail, String country, String password) {
        String queryBody = AuthRequests.getRegisterRequest(name, birthdate, mail, country, password).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance().getService()
        .registerUser(body)
        .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
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
                    login(mail, password);
                }
            }
            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    private void login(String mail, String password) {
        String queryBody = AuthRequests.getLoginRequest(mail, password).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance().getService()
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
            Cache.getInstance().saveSession("auth-token", val);
            String id = response.headers().get("id");
            Cache.getInstance().saveSession("userId", id);
            isRegistered.postValue(true);

            if (type.equals(ProviderType.mail)) {
                verified = Boolean.parseBoolean(response.headers().get("verified"));
            } else {
                verified = true;
            }

            Cache.getInstance().saveSession("provider", type.name());
        }
    }

    public void registerWithGoogle(String name, String birthdate, String country) {
        if (account == null) {
            errorMessage.postValue("Google account is null");
            return;
        }

        String queryBody = AuthRequests.getRegisterRequest(name, birthdate, country).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance()
        .getService()
        .registerWithGoogle(account.getIdToken(), body)
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

    public void registerWithFacebook() {
        LoginManager.getInstance().registerCallback(mCallbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    accessToken = loginResult.getAccessToken();

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            (object, response) -> {
                                // Application code
                                Gson gson = new Gson();
                                FacebookProfile profile = gson.fromJson(String.valueOf(response.getJSONObject()), FacebookProfile.class);
                                fbProfile.postValue(profile);
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,gender,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    // App code
                    Log.d(TAG_FB, "Cancel");
                    errorMessage.postValue("Cancel");
                    isRegistered.postValue(false);
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
                    isRegistered.postValue(false);
                }
            });
    }

    public void handleFbUser(String name, String birthdate, String country) {
        if (accessToken == null) {
            errorMessage.postValue("Facebook Access Token is null");
            return;
        }

        String queryBody = AuthRequests.getRegisterRequest(name, birthdate, country).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance()
        .getService()
        .registerWithFacebook(accessToken.getToken(), body)
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
}
