package com.oozeetech.bizdesk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.FlowParameters;
import com.firebase.ui.auth.ui.phone.PhoneVerificationActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.models.CommonResponse;
import com.oozeetech.bizdesk.models.login.LoginRegisterResponse;
import com.oozeetech.bizdesk.models.login.NormalLoginRequest;
import com.oozeetech.bizdesk.models.login.SocialLoginRequest;
import com.oozeetech.bizdesk.models.updatemobile.CheckMobileNumberRequest;
import com.oozeetech.bizdesk.ui.drawer.DrawerActivity;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.utils.Utils;
import com.oozeetech.bizdesk.widget.DButton;
import com.oozeetech.bizdesk.widget.DEditText;
import com.oozeetech.bizdesk.widget.DTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int RC_SIGN_IN = 101;
    private static final int RC_MOBILE_SIGN_IN = 121;
    @BindView(R.id.edMobileNumber)
    DEditText edMobileNumber;
    @BindView(R.id.edPassword)
    DEditText edPassword;
    @BindView(R.id.imgViewPassword)
    ImageView imgViewPassword;
    @BindView(R.id.txtForgotPassword)
    DTextView txtForgotPassword;
    @BindView(R.id.btnLoginNow)
    DButton btnLoginNow;
    @BindView(R.id.llLoginWithFacebook)
    LinearLayout llLoginWithFacebook;
    @BindView(R.id.llLoginWithGoogle)
    LinearLayout llLoginWithGoogle;
    @BindView(R.id.llCreateAccount)
    LinearLayout llCreateAccount;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    int passwordNotVisible = 1;
    private GoogleApiClient mGoogleApiClient;
    private String number;
    private boolean isSocialLogin = false;
    private CallbackManager callbackManager;

    private Callback<LoginRegisterResponse> loginResponseCallback = new Callback<LoginRegisterResponse>() {
        @Override
        public void onResponse(Call<LoginRegisterResponse> call, Response<LoginRegisterResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    LoginRegisterResponse.Data data = response.body().getData();
                    pref.putString(Constants.LOGIN_REGISTER_RESPONSE, gsonUtils.toJson(response.body()));
                    pref.putBoolean(Constants.IS_LOGIN, true);

                    pref.putBoolean(Constants.IS_SOCIAL_LOGIN, isSocialLogin);
                    pref.putString(Constants.ROUGH_BROKERAGE, data.getRoughBrokerage());
                    pref.putString(Constants.POLISH_BROKERAGE, data.getPolishBrokerage());
                    pref.putString(Constants.EXCHANGE_RATE, data.getExchangeRate());
                    pref.putBoolean(Constants.NOTIFY_PAYMENT_DUE, data.isNotifyPaymentDue());
                    pref.putBoolean(Constants.NOTIFY_BIZ_CONFIRM, data.isNotifyBizConfirm());
                    pref.putString(Constants.NOTIFY_BIZ_CONFIRM_DAY, String.valueOf(data.getNotifyBizConfirmDay()));
                    pref.putBoolean(Constants.NOTIFY_UPDATES, data.isNotifyUpdates());

                    Intent intent = new Intent(getActivity(), DrawerActivity.class);
                    startActivity(intent);
                    finishAffinity();

                } else if (response.body().getReturnCode().equals("21"))
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
                else
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
            } else {
                log.LOGE("Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<LoginRegisterResponse> call, Throwable t) {
            t.printStackTrace();
            dismissProgress();
        }
    };

    private Callback<CommonResponse> checkNumberResponseCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    Intent i = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                    i.putExtra(Constants.USER_ID, response.body().getReturnValue());
                    startActivity(i);
                    finishAffinity();
                } else if (response.body().getReturnCode().equals("4")) {
                    showDialogDone("Biz Desk", " No data Found");
                } else if (response.body().getReturnCode().equals("21"))
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
                else
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
            } else {
                log.LOGE("Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<CommonResponse> call, Throwable t) {
            t.printStackTrace();
            log.LOGE("Failure Response");
            dismissProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        ButterKnife.bind(this);
        initGoogleSignIn();
        callbackManager = CallbackManager.Factory.create();

    }

    private void initGoogleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId().requestProfile().requestEmail()
//                .requestIdToken()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @OnClick({R.id.imgViewPassword, R.id.btnLoginNow, R.id.llLoginWithFacebook, R.id.llLoginWithGoogle, R.id.llCreateAccount, R.id.txtForgotPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgViewPassword:
                if (passwordNotVisible == 1) {
                    edPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordNotVisible = 0;
                } else {
                    edPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordNotVisible = 1;
                }
                break;
            case R.id.btnLoginNow:
                String userName = edMobileNumber.getString();
                String password = edPassword.getString();

                if (userName.isEmpty())
                    showToast("Enter Your Mobile Number as Username", true);
                else if (password.isEmpty())
                    showToast("Enter Password", true);
                else if (!Utils.isInternetConnected(getActivity())) {
                    showNoInternetDialog();
                } else {
                    showProgress();
                    NormalLoginRequest request = new NormalLoginRequest();
                    request.setAPIKey(Constants.API_KEY);
                    request.setUserName(userName);
                    request.setPassword(password);
                    request.setDeviceTypeID(1);
                    request.setDeviceID(pref.getString(Constants.FCM_KEY));

                    requestAPI.postNormalLoginRequest(request).enqueue(loginResponseCallback);
                }
                break;
            case R.id.llLoginWithFacebook:
                facebookSignIn();
                break;
            case R.id.llLoginWithGoogle:
                if (mGoogleApiClient.isConnected())
                    googleSignIn();
                break;
            case R.id.llCreateAccount:
                Intent i = new Intent(getActivity(), SignUpActivity.class);
                startActivity(i);
//                finishAffinity();
                break;
            case R.id.txtForgotPassword:
                FlowParameters flowParameters = new FlowParameters(FirebaseApp.getInstance().getName(),
                        getSelectedProviders(), 0, 0, "", "", false, false, false);
                startActivityForResult(
                        PhoneVerificationActivity.createIntent(LoginActivity.this, flowParameters, null),
                        RC_MOBILE_SIGN_IN);
                break;

        }
    }

    private void googleSignIn() {

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(AuthenticatedActivity.this.getResources().getString(R.string.server_client_id))
//                .requestEmail().build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void facebookSignIn() {
        //Authentication Pending

        LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                System.out.println("onSuccess");
                String accessToken = loginResult.getAccessToken().getToken();
//                log.LOGE("accessToken  " + accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Get facebook data from login
//                        log.LOGE(object.toString());
                        socialLogin(object.optString("name"), object.optString("id"), object.optString("email"), 1);
                        LoginManager.getInstance().logOut();


                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,name,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("FBcANCEL", "");
            }

            @Override
            public void onError(FacebookException error) {

                Log.e("FBERROR", error.toString());
            }
        });
    }


    private void handleSignInResult(GoogleSignInResult result) {
        log.LOGE("handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            log.LOGE("getDisplayName:" + acct.getDisplayName());
            socialLogin(acct.getDisplayName(), acct.getId(), acct.getEmail(), 2);
        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }

    private void socialLogin(String displayName, String id, String email, int type) {

        if (!Utils.isInternetConnected(getActivity())) {
            showNoInternetDialog();
        } else {
            showProgress();
            SocialLoginRequest request = new SocialLoginRequest();
            showProgress();
            request.setAPIKey(Constants.API_KEY);
            request.setFirstName(displayName);
            request.setEmailID(email);
            request.setDeviceTypeID(1);
            request.setDeviceID(pref.getString(Constants.FCM_KEY));
            request.setSocialType(type);
            request.setSocialID(id);

            isSocialLogin = true;
            requestAPI.postSocialLoginRequest(request).enqueue(loginResponseCallback);
        }
    }

    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
        selectedProviders.add(
                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build());

        return selectedProviders;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_MOBILE_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (response != null) {
                number = response.getPhoneNumber();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                callCheckNumberApi();
            }
            return;
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }

    private void callCheckNumberApi() {
        if (!Utils.isInternetConnected(getActivity()))
            showNoInternetDialog();
        else {
            CheckMobileNumberRequest request = new CheckMobileNumberRequest();
            request.setAPIKey(Constants.API_KEY);
            request.setMobileNumber(number);

            requestAPI.postCheckMobileNumberRequest(request).enqueue(checkNumberResponseCallback);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        googleSignIn();
        log.LOGE("onConnected ");
//        initGoogleSignIn();
    }

    @Override
    public void onConnectionSuspended(int i) {
        log.LOGE("onConnectionSuspended " + String.valueOf(i));
        if (!mGoogleApiClient.isConnected())
            initGoogleSignIn();
    }
}
