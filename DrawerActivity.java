package com.oozeetech.bizdesk.ui.drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.fragment.drawer.HomeFragment;
import com.oozeetech.bizdesk.fragment.drawer.MyBizFragment;
import com.oozeetech.bizdesk.fragment.drawer.MyProfileFragment;
import com.oozeetech.bizdesk.fragment.drawer.MyStockFragment;
import com.oozeetech.bizdesk.fragment.drawer.PriceListFragment;
import com.oozeetech.bizdesk.fragment.drawer.SettingFragment;
import com.oozeetech.bizdesk.fragment.drawer.filterfragment.FilterFragment;
import com.oozeetech.bizdesk.fragment.drawer.myparty.MyPartyFragment;
import com.oozeetech.bizdesk.fragment.drawer.paymentoutstanding.PaymentOutstandingFragment;
import com.oozeetech.bizdesk.fragment.drawer.paymentreport.PaymentFragment;
import com.oozeetech.bizdesk.utils.FontUtils;
import com.oozeetech.bizdesk.utils.Utils;

import butterknife.ButterKnife;


public class DrawerActivity extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    public static MaterialSearchView searchView;
    public static DrawerLayout drawer;
    private FragmentDrawer drawerFragment;
    private BroadcastReceiver brodCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayView(intent.getStringExtra("Fragment"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ButterKnife.bind(this);

        showToolBar(false, getString(R.string.nav_item_home));
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, drawer, toolbar);
        drawerFragment.setDrawerListener(this);
        displayView(getString(R.string.nav_item_home));

        registerReceiver(brodCast, new IntentFilter("ChangeFragment"));
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            super.onBackPressed();
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position, String title) {
        if (Utils.isInternetConnected(this)) {
            displayView(title);
        } else
            showNoInternetDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setMenuItem(searchItem);
        FontUtils.setFont(searchView, FontUtils.fontName(DrawerActivity.this, 1));

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(brodCast);
    }

    public void displayView(String title) {

        Fragment fragment = null;
        if (title.equals(getString(R.string.nav_item_home))) {
            fragment = new HomeFragment();
            selectedFragment(0);
        } else if (title.equals(getString(R.string.nav_item_my_biz))) {
            fragment = new MyBizFragment();
            selectedFragment(1);
        } else if (title.equals(getString(R.string.nav_item_my_profile))) {
            fragment = new MyProfileFragment();
            selectedFragment(2);
        } else if (title.equals(getString(R.string.nav_item_price_list))) {
            fragment = new PriceListFragment();
            selectedFragment(3);
        } else if (title.equals(getString(R.string.nav_item_payment_outstanding))) {
            fragment = new PaymentOutstandingFragment();
            selectedFragment(4);
        } else if (title.equals(getString(R.string.nav_item_payment_receipt_report))) {
            fragment = new PaymentFragment();
            selectedFragment(5);
        } else if (title.equals(getString(R.string.nav_item_my_party))) {
            fragment = new MyPartyFragment();
            selectedFragment(6);
        } else if (title.equals(getString(R.string.nav_item_my_stock))) {
            fragment = new MyStockFragment();
            selectedFragment(7);
        } else if (title.equals(getString(R.string.nav_item_settings))) {
            fragment = new SettingFragment();
            selectedFragment(8);
        } else if (title.equals(getString(R.string.nav_item_filter))) {
            fragment = new FilterFragment();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment, title);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

            // set the toolbar title
            txtTitle.setText(title);
        }
    }

    private void selectedFragment(int i) {
        Intent intent = new Intent(getResources().getString(R.string.drawer_item_selected));
        intent.putExtra(getResources().getString(R.string.drawer_item_number), i);
        getActivity().sendBroadcast(intent);
    }

}