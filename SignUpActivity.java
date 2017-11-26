package com.oozeetech.bizdesk.ui;

import android.content.Intent;
import android.os.Bundle;

import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.models.login.LoginRegisterResponse;
import com.oozeetech.bizdesk.models.register.RegisterRequest;
import com.oozeetech.bizdesk.ui.drawer.DrawerActivity;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.utils.Utils;
import com.oozeetech.bizdesk.widget.DButton;
import com.oozeetech.bizdesk.widget.DEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends BaseActivity {

    @BindView(R.id.edYourName)
    DEditText edYourName;
    @BindView(R.id.edEmailId)
    DEditText edEmailId;
    @BindView(R.id.edMobileNumber)
    DEditText edMobileNumber;
    @BindView(R.id.edPassword)
    DEditText edPassword;
    @BindView(R.id.edConfirmPassword)
    DEditText edConfirmPassword;
    @BindView(R.id.btnSignUp)
    DButton btnSignUp;
    private Callback<LoginRegisterResponse> registerResponseCallback = new Callback<LoginRegisterResponse>() {
        @Override
        public void onResponse(Call<LoginRegisterResponse> call, Response<LoginRegisterResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {

                    pref.putString(Constants.LOGIN_REGISTER_RESPONSE, gsonUtils.toJson(response.body()));
                    pref.putBoolean(Constants.IS_LOGIN, true);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnSignUp)
    public void onViewClicked() {

        String yourName = edYourName.getString();
        String emailId = edEmailId.getString();
        String mobileNumber = edMobileNumber.getString();
        String password = edPassword.getString();
        String conPassword = edConfirmPassword.getString();

        if (yourName.isEmpty())
            showToast("Please enter your name", true);
        else if (!Utils.isValidEmail(emailId))
            showToast("Please Enter Valid Email Id", true);
        else if (mobileNumber.isEmpty() || mobileNumber.length() != 10)
            showToast("Please Enter Mobile Number", true);
        else if (password.isEmpty())
            showToast("Enter Password", true);
        else if (!conPassword.equals(password))
            showToast("Password Does not Match", true);
        else if (!Utils.isInternetConnected(getActivity())) {
            showNoInternetDialog();
        } else {
            showProgress();
            RegisterRequest request = new RegisterRequest();
            request.setAPIKey(Constants.API_KEY);
            request.setFirstName(yourName);
            request.setEmailID(emailId);
            request.setMobileNumber(mobileNumber);
            request.setPassword(password);
            request.setDeviceTypeID(1);
            request.setDeviceID(pref.getString(Constants.FCM_KEY));
            requestAPI.postRegisterRequest(request).enqueue(registerResponseCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
