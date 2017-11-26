package com.oozeetech.bizdesk.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.adapter.NotificationAdapter;
import com.oozeetech.bizdesk.models.notification.GetNotificationRequest;
import com.oozeetech.bizdesk.models.notification.GetNotificationResponse;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends BaseActivity {

    NotificationAdapter adapter;
    Callback<GetNotificationResponse> getNotificationResponseCallback = new Callback<GetNotificationResponse>() {
        @Override
        public void onResponse(Call<GetNotificationResponse> call, Response<GetNotificationResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    adapter.clear();
                    adapter.addAll(response.body().getData());
                } else if (response.body().getReturnCode().equals("21"))
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
                else
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
            } else {
                log.LOGE("Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<GetNotificationResponse> call, Throwable t) {
            t.printStackTrace();
            dismissProgress();
        }
    };
    @BindView(R.id.rclvNotification)
    RecyclerView rclvNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        showToolBar(true, "Notification");
        initRecylerView();
        callNotificationAPI();

    }

    private void initRecylerView() {
        adapter = new NotificationAdapter(getActivity());
        rclvNotification.setLayoutManager(new LinearLayoutManager(getActivity()));
        rclvNotification.setHasFixedSize(true);
        rclvNotification.setAdapter(adapter);
    }

    private void callNotificationAPI() {
        GetNotificationRequest request = new GetNotificationRequest();

        request.setAPIKey(Constants.API_KEY);
        request.setToken(Utils.getToken(getActivity()));
        request.setPageIndex(-1);
        request.setSearchString("");

        requestAPI.postGetNotificationRequest(request).enqueue(getNotificationResponseCallback);
    }

}