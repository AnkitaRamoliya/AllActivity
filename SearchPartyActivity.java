package com.oozeetech.bizdesk.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.adapter.GetPartyAdapter;
import com.oozeetech.bizdesk.listener.PartyListener;
import com.oozeetech.bizdesk.listener.RecyclerItemClickListener;
import com.oozeetech.bizdesk.models.CommonResponse;
import com.oozeetech.bizdesk.models.party.AddNewPartyRequest;
import com.oozeetech.bizdesk.models.party.GetPartyRequest;
import com.oozeetech.bizdesk.models.party.GetPartyResponse;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.utils.Utils;
import com.oozeetech.bizdesk.widget.DButtonMaterial;
import com.oozeetech.bizdesk.widget.DEditText;
import com.oozeetech.bizdesk.widget.DTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.oozeetech.bizdesk.BaseFragment.CALL_PHONE_CODE;
import static com.oozeetech.bizdesk.fragment.drawer.MyBizFragment.FILTER_CODE;
import static com.oozeetech.bizdesk.utils.MarshMallowPermission.READ_CONTACTS_CODE;

public class SearchPartyActivity extends BaseActivity implements View.OnClickListener {

    final int RQS_PICK_CONTACT = 1;
    @BindView(R.id.fabAddParty)
    FloatingActionButton fabAddParty;
    int editPosition = -1, callPosition = -1;
    AlertDialog dialog;
    GetPartyAdapter adapter;
    @BindView(R.id.rclViewGetParty)
    RecyclerView rvGetParty;
    String type, partyId;
    @BindView(R.id.llNoRecordFound)
    LinearLayout llNoRecordFound;
    private DTextView txtTitle;
    private DEditText edPartyName;
    private DEditText edCompanyName;
    private DEditText edCustomerNo1;
    private DEditText edCustomerNo2;
    private ImageView imgContactBook;
    private DButtonMaterial btnCancel;
    private DButtonMaterial btnAdd;
    PartyListener partyListener = new PartyListener() {
        @Override
        public void onPartyEditClickListener(int position) {
            editPosition = position;
            openDialog();
            txtTitle.setText("Edit Party");
            btnAdd.setText("Update");
            edPartyName.setText(adapter.get(position).getName());
            edCompanyName.setText(adapter.get(position).getCompanyName());
            edCustomerNo1.setText(adapter.get(position).getContact1());
            edCustomerNo2.setText(adapter.get(position).getContact2());
        }

        @Override
        public void onCallClickListener(int position) {
            if (!marshMallowPermission.checkPermissionForCallPhone()) {
                marshMallowPermission.requestPermissionForCallPhone();
            } else {
                callOnNumber(adapter.get(position).getContact1());
                log.LOGE("ActionCall");
            }
        }
    };
    private Callback<GetPartyResponse> getPartyResponseCallback = new Callback<GetPartyResponse>() {
        @Override
        public void onResponse(Call<GetPartyResponse> call, Response<GetPartyResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    rvGetParty.setVisibility(View.VISIBLE);
                    llNoRecordFound.setVisibility(View.GONE);
                    adapter.clear();
                    adapter.addAll(response.body().getData());
                } else if (response.body().getReturnCode().equals("21"))
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
                else {
                    rvGetParty.setVisibility(View.GONE);
                    llNoRecordFound.setVisibility(View.VISIBLE);
                }
            } else {
                log.LOGE("Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<GetPartyResponse> call, Throwable t) {
            t.printStackTrace();
            log.LOGE("Failure Response");
            dismissProgress();
        }
    };
    private Callback<CommonResponse> commonResponseCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    rvGetParty.setVisibility(View.VISIBLE);
                    llNoRecordFound.setVisibility(View.GONE);
                    callGetPartyAPI();
                } else if (response.body().getReturnCode().equals("21"))
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
                else {
                    rvGetParty.setVisibility(View.GONE);
                    llNoRecordFound.setVisibility(View.VISIBLE);
                }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_party);
        ButterKnife.bind(this);
        handleIntent();

        initRecyclerView();
        callGetPartyAPI();
    }

    private void handleIntent() {
        if (getIntent().hasExtra(Constants.TYPE))
            type = getIntent().getStringExtra(Constants.TYPE);
        if (type.equals("Party"))
            showToolBar(true, "Search Party");
        else if (type.equals("Customer"))
            showToolBar(true, "Search Customer");
        else showToolBar(true, "Search Party");
    }

    private void callGetPartyAPI() {
        showProgress();
        GetPartyRequest request = new GetPartyRequest();
        request.setAPIKey(Constants.API_KEY);
        request.setToken(Utils.getToken(getActivity()));
        request.setPageIndex(-1);
        request.setSearchString(" ");

        requestAPI.postGetPartyRequest(request).enqueue(getPartyResponseCallback);
        log.LOGE("GetPartyRequest API Call ..");
    }

    private void initRecyclerView() {
        adapter = new GetPartyAdapter(getActivity(), partyListener);
        rvGetParty.setLayoutManager(new LinearLayoutManager(this));
        rvGetParty.setHasFixedSize(true);
        rvGetParty.setAdapter(adapter);

        rvGetParty.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Intent intent = new Intent();
                        intent.putExtra(Constants.GET_PARTY, adapter.get(position));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                })
        );
    }

    @OnClick(R.id.fabAddParty)
    public void onViewClicked() {
        // custom dialog
        openDialog();
    }

    private void openDialog() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_new_party, null);
        dialog = Utils.customDialog(getActivity(), v);
        findViews(v);
        dialog.show();
    }

    private void findViews(View v) {
        txtTitle = v.findViewById(R.id.txtTitle);
        edPartyName = v.findViewById(R.id.edPartyName);
        edCompanyName = v.findViewById(R.id.edCompanyName);
        edCustomerNo1 = v.findViewById(R.id.edCustomerNo1);
        edCustomerNo2 = v.findViewById(R.id.edCustomerNo2);
        imgContactBook = v.findViewById(R.id.imgContactBook);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnAdd = v.findViewById(R.id.btnAdd);

        btnCancel.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        imgContactBook.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            // Handle clicks for btnCancel
            dialog.dismiss();
        } else if (v == btnAdd) {
            // Handle clicks for btnAdd
            String partyName = edPartyName.getString();
            String companyName = edCompanyName.getString();
            String customerNo1 = edCustomerNo1.getString();
            String customerNo2 = edCustomerNo2.getString();

            if (partyName.isEmpty())
                showToast("Please Enter Name", true);
            else if (companyName.isEmpty())
                showToast("Please Enter Company Name", true);
            else if (customerNo1.isEmpty())
                showToast("Enter Your Contact Number", true);
            else if (!Utils.isInternetConnected(getActivity()))
                showNoInternetDialog();
            else {
                dialog.dismiss();
                showProgress();
                AddNewPartyRequest request = new AddNewPartyRequest();
                request.setAPIKey(Constants.API_KEY);
                request.setToken(Utils.getToken(getActivity()));
                request.setName(partyName);
                request.setCompanyName(companyName);
                request.setContact1(customerNo1);
                request.setContact2(customerNo2);

                requestAPI.postAddNewPartyRequest(request).enqueue(commonResponseCallback);
                log.LOGE("API Call ..");
            }
        } else if (v == imgContactBook) {
            if (!marshMallowPermission.checkPermissionForReadContacts()) {
                marshMallowPermission.requestPermissionForReadContacts();
            } else {
                openContactBook();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_CONTACTS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openContactBook();
                }
                break;
            case CALL_PHONE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callOnNumber(adapter.get(callPosition).getContact1());
                }
                break;
        }
    }

    private void openContactBook() {
        final Uri uriContact = ContactsContract.Contacts.CONTENT_URI;
        Intent intentPickContact = new Intent(Intent.ACTION_PICK, uriContact);
        startActivityForResult(intentPickContact, RQS_PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQS_PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();

                Cursor cursor = getActivity().getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                String contact1 = "", contact2 = "";
                while (phones.moveToNext()) {
                    String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (contact1.isEmpty())
                        contact1 = number;
                    else if (contact2.isEmpty())
                        contact2 = number;
                }
                phones.close();

                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (editPosition != -1) {
                    edPartyName.setText(name.isEmpty() ? adapter.get(editPosition).getName() : name);
                    edCustomerNo1.setText(contact1.isEmpty() ? adapter.get(editPosition).getContact1() : contact1);
                    edCustomerNo2.setText(contact2.isEmpty() ? adapter.get(editPosition).getContact2() : contact2);
                } else {
                    edPartyName.setText(name);
                    edCustomerNo1.setText(contact1);
                    edCustomerNo2.setText(contact2);
                }
//                GetPartyResponse.Data d = (GetPartyResponse.Data) data.getSerializableExtra(Constants.GET_PARTY);
//                partyId = d.getID();

            } else if (resultCode == FILTER_CODE) {
                type = data.getStringExtra(Constants.TYPE);
                partyId = data.getStringExtra(Constants.PARTY_IDS);
            }
        }
    }
}