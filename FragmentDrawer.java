package com.oozeetech.bizdesk.ui.drawer;

/**
 * Created by Vishal Sojitra on 20/04/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.oozeetech.bizdesk.BaseFragment;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.adapter.NavigationDrawerAdapter;
import com.oozeetech.bizdesk.models.NavDrawerItem;
import com.oozeetech.bizdesk.ui.AddNewBizActivity;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.utils.Utils;
import com.oozeetech.bizdesk.widget.DButton;
import com.oozeetech.bizdesk.widget.DTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class FragmentDrawer extends BaseFragment implements View.OnClickListener {

    private static String[] titles = null;
    private static int[] res = {R.drawable.ic_home, R.drawable.ic_mybiz, R.drawable.ic_myprofile,
            R.drawable.ic_pricelist, R.drawable.ic_paymentoutstanding, R.drawable.ic_paymentreceiptreport,
            R.drawable.ic_myparty, R.drawable.ic_mystock, R.drawable.ic_setting};
    public ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;
    ArrayList<NavDrawerItem> drawerItems;
    NavigationDrawerAdapter adapter;
    @BindView(R.id.llProfile)
    LinearLayout llProfile;
    @BindView(R.id.drawerList)
    RecyclerView drawerList;
    Unbinder unbinder;
    @BindView(R.id.llNewBiz)
    LinearLayout llNewBiz;
    private View containerView;
    private FragmentDrawerListener drawerListener;
    private AlertDialog dialog;
    private DTextView txtTitle;
    private DTextView txtRough;
    private DTextView txtPolished;
    private DButton btnCancel;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setDrawerItem(intent.getIntExtra(getResources().getString(R.string.drawer_item_number), 0));
        }
    };

    public FragmentDrawer() {
    }

    public static ArrayList<NavDrawerItem> getData() {
        ArrayList<NavDrawerItem> data = new ArrayList<>();

        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.setRec(res[i]);
            data.add(navItem);
        }
        return data;
    }

    private void setDrawerItem(int position) {
        for (int i = 0; i < drawerItems.size(); i++) {
            drawerItems.get(i).setShowNotify(false);
        }
        drawerItems.get(position).setShowNotify(true);
        adapter.notifyDataSetChanged();
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // drawer labels
        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
//        IntentFilter filter = new IntentFilter(Constants.ACTION_UPDATE_PROFILE);
        getActivity().registerReceiver(receiver, new IntentFilter(getResources().getString(R.string.drawer_item_selected)));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setProfile();
        drawerItems = getData();
        drawerItems.get(0).setShowNotify(true);
        adapter = new NavigationDrawerAdapter(getActivity(), drawerItems);
        drawerList.setAdapter(adapter);
        drawerList.setLayoutManager(new LinearLayoutManager(getActivity()));
        drawerList.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), drawerList, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position, titles[position]);
                mDrawerLayout.closeDrawer(containerView);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

//    public void setProfile() {
//        txtUserName.setText(Utils.getLoginDetail(getActivity()).getData().getFirstName());
//        txtMobileNumber.setText(Utils.getLoginDetail(getActivity()).getData().getMobileNumber());
//    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_navigation_drawer;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
//        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
//                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
//                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.llNewBiz)
    public void onViewClicked() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        openDialog();

    }

    private void findViews(View v) {
        txtTitle = v.findViewById(R.id.txtTitle);
        txtRough = v.findViewById(R.id.txtRough);
        txtPolished = v.findViewById(R.id.txtPolished);
        btnCancel = v.findViewById(R.id.btnCancel);

        txtRough.setOnClickListener(this);
        txtPolished.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    private void openDialog() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_biz_type, null);
        dialog = Utils.customDialog(getActivity(), v);
        findViews(v);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.txtRough:
                intent = new Intent(getActivity(), AddNewBizActivity.class);
                intent.putExtra(Constants.TYPE, "Rough");
                startActivity(intent);
                dialog.dismiss();
                break;
            case R.id.txtPolished:
                intent = new Intent(getActivity(), AddNewBizActivity.class);
                intent.putExtra(Constants.TYPE, "Polish");
                startActivity(intent);
                dialog.dismiss();
                break;
            case R.id.btnCancel:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getActivity().unregisterReceiver(updateProfileReceiver);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position, String title);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
