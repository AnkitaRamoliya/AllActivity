package com.oozeetech.bizdesk.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.adapter.AddOutstandingReceiptAdapter;
import com.oozeetech.bizdesk.adapter.CustomSpinnerAdapter;
import com.oozeetech.bizdesk.listener.DeletePaymentListener;
import com.oozeetech.bizdesk.models.CommonResponse;
import com.oozeetech.bizdesk.models.payment.AddPaymentReceiptRequest;
import com.oozeetech.bizdesk.models.payment.DeletePaymentRequest;
import com.oozeetech.bizdesk.models.payment.GetPaymentOutstandingDetailRequest;
import com.oozeetech.bizdesk.models.payment.GetPaymentOutstandingDetailResponse;
import com.oozeetech.bizdesk.models.payment.SettlePaymentRequest;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.utils.Utils;
import com.oozeetech.bizdesk.widget.DButton;
import com.oozeetech.bizdesk.widget.DButtonMaterial;
import com.oozeetech.bizdesk.widget.DEditText;
import com.oozeetech.bizdesk.widget.DTextView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.oozeetech.bizdesk.BaseFragment.CALL_PHONE_CODE;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PaymentOutstandingDetailActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, View.OnClickListener {

    AddOutstandingReceiptAdapter adapter;
    String title;
    String bizMasterId;
    AlertDialog dialog;
    AlertDialog callNowDialog;
    Calendar now = Calendar.getInstance();
    DatePickerDialog dpd = DatePickerDialog.newInstance(
            this,
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
    );
    @BindView(R.id.paymentReceipt)
    DTextView paymentReceipt;
    @BindView(R.id.txtTitle)
    DTextView txtTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtCustomerName)
    DTextView txtCustomerName;
    @BindView(R.id.txtSaleDate)
    DTextView txtSaleDate;
    @BindView(R.id.txtDueDays)
    DTextView txtDueDays;
    @BindView(R.id.txtPaymentDate)
    DTextView txtPaymentDate;
    @BindView(R.id.txtBrokerageAmt)
    DTextView txtBrokerageAmt;
    @BindView(R.id.txtBrokerageReceipt)
    DTextView txtBrokerageReceipt;
    @BindView(R.id.txtBrokerageOutstanding)
    DTextView txtBrokerageOutstanding;
    @BindView(R.id.btnAddReceipt)
    LinearLayout btnAddReceipt;
    @BindView(R.id.txtTotalAmount)
    DTextView txtTotalAmount;
    @BindView(R.id.lstOutstandingReceipt)
    RecyclerView lstOutstandingReceipt;
    @BindView(R.id.txtOutstandingPayment)
    DTextView txtOutstandingPayment;
    @BindView(R.id.btnSettledBrokerage)
    DButton btnSettledBrokerage;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    @BindView(R.id.btnCall)
    ImageView btnCall;
    GetPaymentOutstandingDetailResponse.Data data;
    private Callback<GetPaymentOutstandingDetailResponse> getPriceMasterDetailResponseCallback = new Callback<GetPaymentOutstandingDetailResponse>() {
        @Override
        public void onResponse(Call<GetPaymentOutstandingDetailResponse> call, Response<GetPaymentOutstandingDetailResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    data = response.body().getData().get(0);

                    txtCustomerName.setText(data.getCustomerName());
                    txtBrokerageAmt.setText(data.getAmount());
                    txtBrokerageReceipt.setText(data.getReceiptAmount());
                    txtBrokerageOutstanding.setText(data.getOutstandingAmount());
                    txtSaleDate.setText(data.getSaleDate());
                    txtDueDays.setText(data.getDueDay());
                    txtPaymentDate.setText(data.getPaymentDate());
                    txtTotalAmount.setText(data.getAmount());
                    txtOutstandingPayment.setText(data.getOutstandingAmount());

                    adapter.clear();
                    adapter.addAll(data.getReceivePayment());
//                    adapter.notifyDataSetChanged();

                } else if (response.body().getReturnCode().equals("21"))
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
                else {
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
                }
            } else {
                log.LOGE("Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<GetPaymentOutstandingDetailResponse> call, Throwable t) {
            t.printStackTrace();
            log.LOGE("Failure Response");
            dismissProgress();
        }
    };
    private DTextView txtDate;
    private Spinner spCurrency;
    private DEditText edExchangeRate;
    private DEditText edReceiptAmount;
    private DButtonMaterial btnCancel;
    private DButtonMaterial btnAdd;
    private Callback<CommonResponse> commonResponseCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
            dialog.dismiss();
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    callGetPriceMasterDetailAPI();
                } else if (response.body().getReturnCode().equals("21"))
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
    private Callback<CommonResponse> deletePaymentResponseCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    callGetPriceMasterDetailAPI();
                } else if (response.body().getReturnCode().equals("21"))
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
    DeletePaymentListener deletePaymentListener = new DeletePaymentListener() {
        @Override
        public void onDeleteTapListener(long paymentId) {
            DeletePaymentRequest request = new DeletePaymentRequest();
            request.setAPIKey("123");
            request.setToken(Utils.getToken(getActivity()));
            request.setPaymentID(paymentId);

            requestAPI.postDeletePaymentRequest(request).enqueue(deletePaymentResponseCallback);
        }
    };
    private Callback<CommonResponse> settlePaymentResponseCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    //Do Action here for Settle Payment
                } else if (response.body().getReturnCode().equals("21"))
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
    private DTextView ADtxtPartyName;
    private LinearLayout llPartyContact1;
    private DTextView txtpartyContact1;
    private ImageView btnPartyContact1;
    private LinearLayout llPartyContact2;
    private DTextView txtpartyContact2;
    private ImageView btnPartyContact2;
    private DTextView ADtxtCustomerName;
    private LinearLayout llCustomerContact1;
    private DTextView txtCustomerContact1;
    private ImageView btnCustomerContact1;
    private LinearLayout llCustomerContact2;
    private DTextView txtCustomerContact2;
    private ImageView btnCustomerContact2;
    private DButton btncNCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brokerages_outstanding_receipt_temp);
        ButterKnife.bind(this);
        handleIntent();
//        Utils.setDrawableTint(paymentReceipt, getResources().getDrawable(R.drawable.ic_plus), getResources().getColor(R.color.colorWhite));
        Drawable drawable = DrawableCompat.wrap(getResources().getDrawable(R.drawable.ic_plus_));
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorWhite));
        paymentReceipt.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        initRecyclerView();
        callGetPriceMasterDetailAPI();
    }

    private void initRecyclerView() {
        adapter = new AddOutstandingReceiptAdapter(getActivity(), deletePaymentListener);
        lstOutstandingReceipt.setLayoutManager(new LinearLayoutManager(getActivity()));
        lstOutstandingReceipt.setHasFixedSize(true);
        lstOutstandingReceipt.setAdapter(adapter);
    }

    private void handleIntent() {
        title = getIntent().getStringExtra(Constants.TYPE);
        bizMasterId = getIntent().getStringExtra(Constants.ID);
        showToolBar(true, title + " Outstanding Receipt");
    }

    private void callGetPriceMasterDetailAPI() {
        showProgress();
        GetPaymentOutstandingDetailRequest request = new GetPaymentOutstandingDetailRequest();
        if (!Utils.isInternetConnected(getActivity())) {
            showNoInternetDialog();
        } else {
            request.setAPIKey(Constants.API_KEY);
            request.setToken(Utils.getToken(getActivity()));
            request.setPaymentType(title.equals("Brokerage") ? 2 : 1);
            request.setBizMasterID(bizMasterId);

            requestAPI.postGetPaymentOutstandingDetailRequest(request).enqueue(getPriceMasterDetailResponseCallback);
        }
    }

    private void openDialog() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_brok_receipt, null);
        dialog = Utils.customDialog(getActivity(), v);
        findViews(v);
        Utils.setDrawableTint(txtDate, getResources().getDrawable(R.drawable.ic_date_), getResources().getColor(R.color.colorTextBlack));
        setSpinner();
        setDatePickerDialog();
        dialog.show();
    }

    private void setDatePickerDialog() {
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dpd.isAdded())
                    dpd.show(getFragmentManager(), "SetDate");
            }
        });
    }

    private void setSpinner() {
        spCurrency.setOnItemSelectedListener(this);
        CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(getActivity(), getActivity().getResources().getStringArray(R.array.currency_type));
        spCurrency.setAdapter(customAdapter);
    }

    private void findViews(View v) {
        txtDate = v.findViewById(R.id.txtDate);
        spCurrency = v.findViewById(R.id.spCurrency);
        edExchangeRate = v.findViewById(R.id.edExchangeRate);
        edReceiptAmount = v.findViewById(R.id.edReceiptAmount);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnAdd = v.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAddPaymentReceiptAPI();
//                callGetPriceMasterDetailAPI();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void callAddPaymentReceiptAPI() {
        if (!Utils.isInternetConnected(getActivity())) {
            showNoInternetDialog();
        } else if (txtDate.getText().toString().trim().equals("Date")) {
            showToast("Please set Date", true);
        } else {
            showProgress();
            AddPaymentReceiptRequest request = new AddPaymentReceiptRequest();
            request.setAPIKey(Constants.API_KEY);
            request.setToken(Utils.getToken(getActivity()));
            request.setBizMasterID(bizMasterId);
            request.setPaymentType(title.equals("Brokerage") ? 2 : 1);
            request.setPaymentDate(txtDate.getText().toString().trim());
            request.setCurrencyID(spCurrency.getSelectedItemPosition() + 1);
            request.setExchangeRate(edExchangeRate.getDouble());
            request.setAmount(edReceiptAmount.getDouble());

            requestAPI.postAddPaymentReceiptRequest(request).enqueue(commonResponseCallback);
        }
    }

    @OnClick({R.id.btnAddReceipt, R.id.btnSettledBrokerage, R.id.btnCall})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAddReceipt:
                openDialog();
                break;
            case R.id.btnSettledBrokerage:
                callSettlePaymentAPI();
                break;
            case R.id.btnCall:
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_call_now, null);
                callNowDialog = Utils.customDialog(getActivity(), v);
                findViewsOfCallNow(v);
                callNowDialog.show();
                setData();
                break;
        }

    }

    private void setData() {
        if (data.getPartyContact2().equals(""))
            llPartyContact2.setVisibility(View.GONE);
        if (data.getCustomerContact2().equals(""))
            llCustomerContact2.setVisibility(View.GONE);
        ADtxtPartyName.setText(data.getPartyName());
        ADtxtCustomerName.setText(data.getCustomerName());
        txtpartyContact1.setText(data.getPartyContact1());
        txtpartyContact2.setText(data.getPartyContact2());
        txtCustomerContact1.setText(data.getCustomerContact1());
        txtCustomerContact2.setText(data.getCustomerContact2());
    }

    private void findViewsOfCallNow(View v) {
        txtTitle = v.findViewById(R.id.txtTitle);
        ADtxtPartyName = v.findViewById(R.id.ADtxtPartyName);
        llPartyContact1 = v.findViewById(R.id.llPartyContact1);
        txtpartyContact1 = v.findViewById(R.id.txtpartyContact1);
        btnPartyContact1 = v.findViewById(R.id.btnPartyContact1);
        llPartyContact2 = v.findViewById(R.id.llPartyContact2);
        txtpartyContact2 = v.findViewById(R.id.txtpartyContact2);
        btnPartyContact2 = v.findViewById(R.id.btnPartyContact2);
        ADtxtCustomerName = v.findViewById(R.id.ADtxtCustomerName);
        llCustomerContact1 = v.findViewById(R.id.llCustomerContact1);
        txtCustomerContact1 = v.findViewById(R.id.txtCustomerContact1);
        btnCustomerContact1 = v.findViewById(R.id.btnCustomerContact1);
        llCustomerContact2 = v.findViewById(R.id.llCustomerContact2);
        txtCustomerContact2 = v.findViewById(R.id.txtCustomerContact2);
        btnCustomerContact2 = v.findViewById(R.id.btnCustomerContact2);
        btncNCancel = v.findViewById(R.id.btnCancel);

        btncNCancel.setOnClickListener(this);
        btnPartyContact1.setOnClickListener(this);
        btnPartyContact2.setOnClickListener(this);
        btnCustomerContact1.setOnClickListener(this);
        btnCustomerContact2.setOnClickListener(this);
    }

    private void callSettlePaymentAPI() {
        if (!Utils.isInternetConnected(getActivity())) {
            showNoInternetDialog();
        } else {
            showProgress();
            SettlePaymentRequest request = new SettlePaymentRequest();
            request.setAPIKey(Constants.API_KEY);
            request.setToken(Utils.getToken(getActivity()));
            request.setBizMasterID(bizMasterId);
            request.setPaymentType(getIntent().getLongExtra(Constants.TYPE, 1));

            requestAPI.postSettlePaymentRequest(request).enqueue(settlePaymentResponseCallback);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (view.getTag().equals("SetDate")) {
            txtDate.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btncNCancel) {
            // Handle clicks for btnCancel
            callNowDialog.dismiss();
        } else if (v == btnPartyContact1) {
            // Handle clicks for btnPartyContact1
            if (!checkPermissionForCallPhone()) {
                requestPermissionForCallPhone();
            } else {
                String partyContact1 = txtpartyContact1.getText().toString().trim();
                callOnNumber(partyContact1);
            }
        } else if (v == btnPartyContact2) {
            if (!checkPermissionForCallPhone()) {
                requestPermissionForCallPhone();
            } else {
                String partyContact2 = txtpartyContact2.getText().toString().trim();
                callOnNumber(partyContact2);
            }
        } else if (v == btnCustomerContact1) {
            if (!checkPermissionForCallPhone()) {
                requestPermissionForCallPhone();
            } else {
                String customerContact1 = txtCustomerContact1.getText().toString().trim();
                callOnNumber(customerContact1);
            }
        } else if (v == btnCustomerContact2) {
            if (!checkPermissionForCallPhone()) {
                requestPermissionForCallPhone();
            } else {
                String customerContact2 = txtpartyContact2.getText().toString().trim();
                callOnNumber(customerContact2);
            }
        }

    }

    public boolean checkPermissionForCallPhone() {
        int result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionForCallPhone() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CODE);
        }
    }

    private void callOnNumber(String number) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

}