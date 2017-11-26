package com.oozeetech.bizdesk.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.adapter.GetPriceListDetailAdapter;
import com.oozeetech.bizdesk.models.pricelist.GetPriceMasterDetailRequest;
import com.oozeetech.bizdesk.models.pricelist.GetPriceMasterDetailResponse;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.utils.DateUtils;
import com.oozeetech.bizdesk.utils.Utils;
import com.oozeetech.bizdesk.widget.DTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceDetailActivity extends BaseActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    GetPriceListDetailAdapter adapter;
    Calendar aCalendar = Calendar.getInstance();
    @BindView(R.id.imgPrevMonth)
    ImageView imgPrevMonth;
    @BindView(R.id.txtCurrentMonth)
    DTextView txtCurrentMonth;
    @BindView(R.id.imgNextMonth)
    ImageView imgNextMonth;
    @BindView(R.id.txtType)
    DTextView txtType;
    @BindView(R.id.txtCrt)
    DTextView txtCrt;
    @BindView(R.id.txt$Crt)
    DTextView txt$Crt;
    @BindView(R.id.rclPriceListDetail)
    RecyclerView rclPriceListDetail;
    @BindView(R.id.nestedScroll)
    NestedScrollView scrollView;
    @BindView(R.id.llNoRecordFound)
    LinearLayout llNoRecordFound;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    private Callback<GetPriceMasterDetailResponse> getPriceMasterDetailResponseCallback = new Callback<GetPriceMasterDetailResponse>() {
        @Override
        public void onResponse(Call<GetPriceMasterDetailResponse> call, Response<GetPriceMasterDetailResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    scrollView.setVisibility(View.VISIBLE);
                    llNoRecordFound.setVisibility(View.GONE);
                    llTitle.setVisibility(View.VISIBLE);
                    initRecyclerView();
                    adapter.addAll(response.body().getData());
                } else if (response.body().getReturnCode().equals("21"))
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
                else {
                    try {
                        adapter.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    scrollView.setVisibility(View.GONE);
                    llNoRecordFound.setVisibility(View.VISIBLE);
                    llTitle.setVisibility(View.GONE);
                }
            } else {
                log.LOGE("Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<GetPriceMasterDetailResponse> call, Throwable t) {
            t.printStackTrace();
            log.LOGE("Failure Response");
            dismissProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list_detail);
        ButterKnife.bind(this);

//        scrollView.setNestedScrollingEnabled(false);
        rclPriceListDetail.setNestedScrollingEnabled(false);
        showToolBar(true, getIntent().getStringExtra("title"));
        setDate();

        callGetPriceMasterDetailAPI();
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


    private String setDate() {
//        SimpleDateFormat currentDate = new SimpleDateFormat("MMM yyyy");

        String thisDate = DateUtils.convertDateToString(aCalendar.getTime(), "MMM yyyy");
//        String thisDate = currentDate.format(aCalendar.getTime());
        txtCurrentMonth.setText(thisDate);
        return thisDate;
    }

    private void initRecyclerView() {
        adapter = new GetPriceListDetailAdapter(getActivity());
        rclPriceListDetail.setLayoutManager(new LinearLayoutManager(getActivity()));
        rclPriceListDetail.setHasFixedSize(true);
        rclPriceListDetail.setAdapter(adapter);
    }

    private void callGetPriceMasterDetailAPI() {
        if (!Utils.isInternetConnected(getActivity()))
            showNoInternetDialog();
        else {
            showProgress();
            GetPriceMasterDetailRequest request = new GetPriceMasterDetailRequest();
            request.setAPIKey(Constants.API_KEY);
            request.setToken(Utils.getToken(getActivity()));
            request.setPriceMasterID(getIntent().getLongExtra("id", 0));
            request.setMonth((aCalendar.get(Calendar.MONTH) + 1));
            request.setYear(aCalendar.get(Calendar.YEAR));

            requestAPI.postGetPriceMasterDetailRequest(request).enqueue(getPriceMasterDetailResponseCallback);
        }
    }

    @OnClick({R.id.imgPrevMonth, R.id.imgNextMonth})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgPrevMonth:
                getPreviousMonth();
                callGetPriceMasterDetailAPI();
                break;
            case R.id.imgNextMonth:
                getNextMonth();
                callGetPriceMasterDetailAPI();
                break;
        }
    }

    private void print(View view) {
        ProgressDialog dialog = new ProgressDialog(PriceDetailActivity.this);
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
//        try {
//            File defaultFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name) + "/ScreenShot");
//            if (!defaultFile.exists())
//                defaultFile.mkdirs();
//
//            String filename = System.currentTimeMillis() + ".jpg";
//            File file = new File(defaultFile, filename);
//            if (file.exists()) {
//                file.delete();
//                file = new File(defaultFile, filename);
//            }
//
//            FileOutputStream output = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
//            output.flush();
//            output.close();
//
//            dialog.dismiss();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            dialog.dismiss();
//        }
    }

    //create bitmap from the ScrollView
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

    private void getNextMonth() {
        aCalendar.set(Calendar.MONTH, aCalendar.get(Calendar.MONTH) + 1);
        setDate();
    }

    private void getPreviousMonth() {
        aCalendar.set(Calendar.MONTH, aCalendar.get(Calendar.MONTH) - 1);
        setDate();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_share:
                if (adapter.getItemCount() == 0)
                    Toast.makeText(getApplicationContext(), "There is no Data to share SnapShot..!", Toast.LENGTH_SHORT).show();
                else
                    print(rclPriceListDetail);
        }
        return false;
    }
}