package com.oozeetech.bizdesk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.fragment.drawer.filterfragment.CustomerFragment;
import com.oozeetech.bizdesk.fragment.drawer.filterfragment.DatePickerFragment;
import com.oozeetech.bizdesk.fragment.drawer.filterfragment.FilterFragment;
import com.oozeetech.bizdesk.fragment.drawer.filterfragment.PartyFragment;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.widget.DButtonMaterial;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FilterActivity extends BaseActivity {

    @BindView(R.id.container_body)
    FrameLayout containerBody;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    @BindView(R.id.btnFilter)
    DButtonMaterial btnFilter;
    @BindView(R.id.btnClear)
    DButtonMaterial btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        showToolBar(true, getString(R.string.nav_item_filter));
        ButterKnife.bind(this);
        callFragment();

    }

    private void callFragment() {

        Fragment fragment = new FilterFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment, getString(R.string.nav_item_filter));
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.btnFilter, R.id.btnClear})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnFilter:
               /* pref.putString(Constants.SELLER_FROM_DATE, DatePickerFragment.sellerFromDate);
                pref.putString(Constants.SELLER_TO_DATE, DatePickerFragment.sellerToDate);
                pref.putString(Constants.PAYMENT_FROM_DATE, DatePickerFragment.paymentFromDate);
                pref.putString(Constants.PAYMENT_TO_DATE, DatePickerFragment.paymentToDate);
                pref.putString(Constants.PARTY_IDS, PartyFragment.partyIds);
                pref.putString(Constants.CUSTOMER_IDS, CustomerFragment.customerIds);*/
                intent = new Intent();
                intent.putExtra(Constants.SELLER_FROM_DATE, DatePickerFragment.sellerFromDate);
                intent.putExtra(Constants.SELLER_TO_DATE, DatePickerFragment.sellerToDate);
                intent.putExtra(Constants.PAYMENT_FROM_DATE, DatePickerFragment.paymentFromDate);
                intent.putExtra(Constants.PAYMENT_TO_DATE, DatePickerFragment.paymentToDate);
                intent.putExtra(Constants.PARTY_IDS, PartyFragment.partyIds);
                intent.putExtra(Constants.CUSTOMER_IDS, CustomerFragment.customerIds);

                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
                break;
            case R.id.btnClear:
               /* pref.putString(Constants.SELLER_FROM_DATE, "");
                pref.putString(Constants.SELLER_TO_DATE, "");
                pref.putString(Constants.PAYMENT_FROM_DATE, "");
                pref.putString(Constants.PAYMENT_TO_DATE, "");
                pref.putString(Constants.PARTY_IDS, "");
                pref.putString(Constants.CUSTOMER_IDS, "");*/
                intent = new Intent();
                intent.putExtra(Constants.SELLER_FROM_DATE, "");
                intent.putExtra(Constants.SELLER_TO_DATE, "");
                intent.putExtra(Constants.PAYMENT_FROM_DATE, "");
                intent.putExtra(Constants.PAYMENT_TO_DATE, "");
                intent.putExtra(Constants.PARTY_IDS, "");
                intent.putExtra(Constants.CUSTOMER_IDS, "");

                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
                break;
        }

    }
}