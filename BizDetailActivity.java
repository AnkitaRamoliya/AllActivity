package com.oozeetech.bizdesk.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.adapter.AddBizDetailItemAdapter;
import com.oozeetech.bizdesk.models.CommonResponse;
import com.oozeetech.bizdesk.models.mybiz.ConfirmMyBizRequest;
import com.oozeetech.bizdesk.models.mybiz.DeleteMyBizRequest;
import com.oozeetech.bizdesk.models.mybiz.GetBizDetailRequest;
import com.oozeetech.bizdesk.models.mybiz.GetBizDetailResponse;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.utils.Utils;
import com.oozeetech.bizdesk.widget.DButtonMaterial;
import com.oozeetech.bizdesk.widget.DTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BizDetailActivity extends BaseActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    double bizMasterId;
    @BindView(R.id.txtTitle)
    DTextView txtTitle;
    @BindView(R.id.imgDiamond)
    ImageView imgDiamond;
    @BindView(R.id.txtPolished)
    DTextView txtPolished;
    @BindView(R.id.txtPartyName)
    DTextView txtPartyName;
    @BindView(R.id.txtCustomerName)
    DTextView txtCustomerName;
    @BindView(R.id.txtSaleDate)
    DTextView txtSaleDate;
    @BindView(R.id.txtPaymentDate)
    DTextView txtPaymentDate;
    @BindView(R.id.txtDeliveryDate)
    DTextView txtDeliveryDate;
    @BindView(R.id.txtConfirmed)
    DTextView txtConfirmed;
    @BindView(R.id.txtTotalWeight)
    DTextView txtTotalWeight;
    @BindView(R.id.txtAvgPrice)
    DTextView txtAvgPrice;
    @BindView(R.id.txtTotalAmount)
    DTextView txtTotalAmount;
    @BindView(R.id.txtTotalLessAmt)
    DTextView txtTotalLessAmt;
    @BindView(R.id.txtBrokerageAmt)
    DTextView txtBrokerageAmt;
    @BindView(R.id.txtExchangeRate)
    DTextView txtExchangeRate;
    @BindView(R.id.TotalPayment)
    DTextView TotalPayment;
    @BindView(R.id.txtTotalBrokerage)
    DTextView txtTotalBrokerage;
    @BindView(R.id.txtReceivedPayment)
    DTextView txtReceivedPayment;
    @BindView(R.id.txtReceiveBrokerage)
    DTextView txtReceiveBrokerage;
    @BindView(R.id.txtOutstandingPayment)
    DTextView txtOutstandingPayment;
    @BindView(R.id.txtOutstandingBrokerage)
    DTextView txtOutstandingBrokerage;
    @BindView(R.id.txtEdit)
    DTextView txtEdit;
    @BindView(R.id.txtConfirm)
    DTextView txtConfirm;
    @BindView(R.id.txtDelete)
    DTextView txtDelete;
    @BindView(R.id.txtDueDays)
    DTextView txtDueDays;
    @BindView(R.id.lstNewItem)
    RecyclerView lstNewItem;
    int dd;
    AddBizDetailItemAdapter itemAdapter;
    String partyName = "", customerName = "", saleDate = "", paymentDate = "", deliveryDate = "",
            confirm = "", lessPer = "", brokeragePer = "", usdRate = "", currencyId = "";
    @BindView(R.id.svBizDetail)
    ScrollView svBizDetail;
    @BindView(R.id.nestedScroll)
    NestedScrollView nestedScroll;
    private String type;
    private AlertDialog dialog;
    private DButtonMaterial btnCancel;
    private DButtonMaterial btnDelete;
    private GetBizDetailResponse.Data d;
    private Callback<GetBizDetailResponse> getBizDetailResponseCallback = new Callback<GetBizDetailResponse>() {
        @Override
        public void onResponse(Call<GetBizDetailResponse> call, Response<GetBizDetailResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    svBizDetail.setVisibility(View.VISIBLE);
                    DecimalFormat df = new DecimalFormat("0.00");
                    d = response.body().getData();
                    bizMasterId = d.getBizMasterID();
                    partyName = d.getPartyName();
                    customerName = d.getCustomerName();
                    saleDate = d.getSaleDate();
                    paymentDate = d.getPaymentDate();
                    deliveryDate = d.getDeliveryDate();
                    usdRate = String.format("%s", d.getExchangeRate());
                    type = d.getBizType();
                    currencyId = d.getCurrencySymbol();
                    dd = d.getDueDay();
                    usdRate = String.format("%s", d.getExchangeRate());
                    lessPer = String.format("%s", d.getLessPer());
                    brokeragePer = String.format("%s", d.getBrokeragePer());
                    confirm = d.getConfirm();

                    txtPartyName.setText(partyName);
                    txtCustomerName.setText(customerName);
                    txtSaleDate.setText(saleDate);
                    txtPaymentDate.setText(paymentDate);
                    txtDeliveryDate.setText(deliveryDate.isEmpty() ? "-----" : deliveryDate);
                    imgDiamond.setColorFilter(ContextCompat.getColor(getActivity(), d.getBizType().equals("Rough") ? R.color.diamondOrange : R.color.diamondGreen));
                    txtPolished.setText(d.getBizType() + "\n" + df.format(d.getTotalCrt()) + " " + "Crt");
                    txtDueDays.setText("Due days: " + dd);
                    txtConfirmed.setText(confirm);
                    txtTotalWeight.setText(String.format("%s", df.format(d.getTotalCrt())));
                    txtExchangeRate.setText(df.format(d.getExchangeRate()) + " INR ");
                    txtAvgPrice.setText(String.format("%s", df.format(d.getAvgPricePerCrt())) + " " + d.getCurrencySymbol());
                    txtTotalAmount.setText(d.getTotalAmt() + " " + d.getCurrencySymbol());
                    txtTotalLessAmt.setText(d.getCurrencySymbol() + String.format("%s", df.format(d.getLessPerAmt())) + "(" + String.format("%s", df.format(d.getLessPer())) + "%)");
                    txtBrokerageAmt.setText(d.getCurrencySymbol() + d.getBrokerageAmt() + "(" + String.format("%s", df.format(d.getBrokeragePer())) + "%)");
                    txtTotalBrokerage.setText(d.getCurrencySymbol() + " " + d.getBrokerageAmt());
                    txtOutstandingBrokerage.setText(d.getCurrencySymbol() + " " + d.getBrokerageOutstanding());
                    txtReceiveBrokerage.setText(d.getCurrencySymbol() + " " + d.getBrokerageReceived());
                    txtOutstandingPayment.setText(d.getCurrencySymbol() + " " + d.getPaymentOutstanding());
                    txtReceivedPayment.setText(d.getCurrencySymbol() + " " + d.getPaymentReceived());
                    TotalPayment.setText(d.getCurrencySymbol() + " " + String.format("%s", df.format(d.getFinalAmt())));

                    itemAdapter.clear();
                    itemAdapter.addAll(d.getCBizDetail(), d.getBizType(), d.getCurrencySymbol());
                } else if (response.body().getReturnCode().equals("21"))
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());

            } else {
                log.LOGE("Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<GetBizDetailResponse> call, Throwable t) {

            log.LOGE("Failure Response");
            dismissProgress();
        }
    };
    private Callback<CommonResponse> deleteResponseCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
            dialog.dismiss();
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    showDialogDone("Biz Desk", "Record Deleted Successfully");
                    callGetMyBizAPI();
                    getActivity().finish();
                } else if (response.body().getReturnCode().equals("4")) {
                    showDialogDone("Biz Desk", "First DELETE Payment Receipt Entry..");
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
    private AlertDialog builder;
    private Callback<CommonResponse> confirmResponseCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    builder.dismiss();
                    callGetMyBizAPI();
                    getActivity().finish();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biz_detail_temp);
        ButterKnife.bind(this);

//        svBizDetail.setNestedScrollingEnabled(false);
        lstNewItem.setNestedScrollingEnabled(false);
        showToolBar(true, "Biz Detail");
        initList();
        callGetMyBizAPI();
    }

    private void initList() {
        itemAdapter = new AddBizDetailItemAdapter(getActivity());
        lstNewItem.setLayoutManager(new LinearLayoutManager(getActivity()));
        lstNewItem.setHasFixedSize(true);
        lstNewItem.setAdapter(itemAdapter);
    }

    private void callGetMyBizAPI() {
        if (!Utils.isInternetConnected(getActivity())) {
            showNoInternetDialog();
        } else {
            showProgress();
            svBizDetail.setVisibility(View.INVISIBLE);
            GetBizDetailRequest request = new GetBizDetailRequest();
            request.setAPIKey(Constants.API_KEY);
            request.setToken(Utils.getToken(getActivity()));
            request.setBizMasterID(getIntent().getStringExtra(Constants.ID));

            requestAPI.postGetBizDetailRequest(request).enqueue(getBizDetailResponseCallback);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Add your menu entries here
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        MenuItem share = menu.findItem(R.id.action_share);
        share.setVisible(true);
        share.setOnMenuItemClickListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        /*if (requestCode == UPDATE_BIZ_CODE) {
            d = (GetBizDetailResponse.Data) data.getSerializableExtra(Constants.UPDATE_BIZ);
            partyName = d.getPartyName();
            customerName = d.getCustomerName();
            saleDate = d.getSaleDate();
            paymentDate = d.getPaymentDate();
            deliveryDate = d.getDeliveryDate();
            lessPer = String.valueOf(d.getLessPer());
            brokeragePer = String.valueOf(d.getBrokeragePer());
            usdRate = String.valueOf(d.getExchangeRate());
        }*/
    }

    @OnClick({R.id.txtEdit, R.id.txtConfirm, R.id.txtDelete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txtEdit:
                Intent i = new Intent(getActivity(), AddNewBizActivity.class);
                i.putExtra(Constants.TYPE, "Update");
                i.putExtra(Constants.UPDATE_BIZ, d);
//                startActivityForResult(i, UPDATE_BIZ_CODE);
                startActivity(i);
                break;
            case R.id.txtConfirm:
                openConfirmDialog();
                break;
            case R.id.txtDelete:
                openDeleteDialog();
                break;
        }
    }

    private void openConfirmDialog() {
        builder = new AlertDialog.Builder(getActivity())
                .setTitle("Confirm Biz")
                .setMessage("Do you Want to Confirm Biz ..?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Utils.isInternetConnected(getActivity()))
                            showNoInternetDialog();
                        else {
                            showProgress();
                            ConfirmMyBizRequest request = new ConfirmMyBizRequest();
                            request.setAPIKey("123");
                            request.setToken(Utils.getToken(getActivity()));
                            request.setID(bizMasterId);
                            requestAPI.postConfirmMyBizRequest(request).enqueue(confirmResponseCallback);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        builder.dismiss();
                    }
                }).create();

        builder.show();

    }

    private void openDeleteDialog() {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_record, null);
        dialog = Utils.customDialog(getActivity(), v);
        txtTitle = v.findViewById(R.id.txtTitle);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnDelete = v.findViewById(R.id.btnDelete);

        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDelete:
                if (!Utils.isInternetConnected(getActivity()))
                    showNoInternetDialog();
                else {
                    showProgress();
                    DeleteMyBizRequest request = new DeleteMyBizRequest();
                    request.setAPIKey("123");
                    request.setToken(Utils.getToken(getActivity()));
                    request.setID(bizMasterId);
                    requestAPI.postDeleteMyBizRequest(request).enqueue(deleteResponseCallback);
                }
                break;
            case R.id.btnCancel:
                dialog.dismiss();
                break;
            case R.id.btnConfirm:
                if (!Utils.isInternetConnected(getActivity()))
                    showNoInternetDialog();
                else {
                    showProgress();
                    ConfirmMyBizRequest request = new ConfirmMyBizRequest();
                    request.setAPIKey("123");
                    request.setToken(Utils.getToken(getActivity()));
                    request.setID(bizMasterId);
                    requestAPI.postConfirmMyBizRequest(request).enqueue(confirmResponseCallback);
                }
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_share:
                print(svBizDetail);
        }
        return false;
    }

    private void print(View view) {
        ProgressDialog dialog = new ProgressDialog(BizDetailActivity.this);
        dialog.setMessage("Sharing...");
        dialog.show();
        Bitmap bitmap = null;
        if (view instanceof ScrollView) {
            bitmap = getBitmapFromView(view, ((ScrollView) view).getChildAt(0).getHeight(), ((ScrollView) view).getChildAt(0).getWidth());
        } else {
            bitmap = getBitmapFromView(view, view.getHeight(), view.getWidth());
        }

        File cache = getApplicationContext().getExternalCacheDir();
        File shareFile = new File(cache, "toShare.png");
        try {
            FileOutputStream out = new FileOutputStream(shareFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
        }
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + shareFile));
        try {
            startActivity(Intent.createChooser(share, "Share photo"));
            dialog.dismiss();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), (CharSequence) e, Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }
}