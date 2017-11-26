package com.oozeetech.bizdesk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.models.CommonResponse;
import com.oozeetech.bizdesk.models.resetpassword.ResetPasswordRequest;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.utils.Utils;
import com.oozeetech.bizdesk.widget.DButton;
import com.oozeetech.bizdesk.widget.DEditText;
import com.oozeetech.bizdesk.widget.DTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends BaseActivity {

    @BindView(R.id.edNewPassword)
    DEditText edNewPassword;
    @BindView(R.id.edConfirmPassword)
    DEditText edConfirmPassword;
    @BindView(R.id.btnResetNow)
    DButton btnResetNow;
    @BindView(R.id.txtGoBack)
    DTextView txtGoBack;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    private String userId;
    private Callback<CommonResponse> resetPasswordResponseCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                showToast("Password Successfully Changed ..", true);
                Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(i);
                finishAffinity();
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
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);

        userId = getIntent().getStringExtra(Constants.USER_ID);
    }

    @OnClick({R.id.btnResetNow, R.id.txtGoBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnResetNow:
                callResetPasswordApi();
                break;
            case R.id.txtGoBack:
                Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(i);
                break;
        }
    }

    private void callResetPasswordApi() {

        if (!edNewPassword.getString().equals(edConfirmPassword.getString()))
            showToast(" Both Password does not match", true);
        else if (!Utils.isInternetConnected(getActivity()))
            showNoInternetDialog();
        else if (edNewPassword.getString().equals(edConfirmPassword.getString())) {
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setAPIKey(Constants.API_KEY);
            request.setPassword(edNewPassword.getString());
            request.setUserID(userId);

            requestAPI.postResetPasswordRequest(request).enqueue(resetPasswordResponseCallback);
        }
    }
}
