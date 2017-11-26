package com.oozeetech.bizdesk.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.daimajia.swipe.util.Attributes;
import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.adapter.AddItemAdapter;
import com.oozeetech.bizdesk.adapter.CustomSpinnerAdapter;
import com.oozeetech.bizdesk.adapter.GetPartyAdapter;
import com.oozeetech.bizdesk.listener.ItemListener;
import com.oozeetech.bizdesk.models.CommonResponse;
import com.oozeetech.bizdesk.models.mybiz.AddNewBizRequest;
import com.oozeetech.bizdesk.models.mybiz.GetBizDetailResponse;
import com.oozeetech.bizdesk.models.party.AddNewPartyRequest;
import com.oozeetech.bizdesk.models.party.GetPartyResponse;
import com.oozeetech.bizdesk.utils.Constants;
import com.oozeetech.bizdesk.utils.DateUtils;
import com.oozeetech.bizdesk.utils.Utils;
import com.oozeetech.bizdesk.widget.DButton;
import com.oozeetech.bizdesk.widget.DButtonMaterial;
import com.oozeetech.bizdesk.widget.DCheckBox;
import com.oozeetech.bizdesk.widget.DEditText;
import com.oozeetech.bizdesk.widget.DTextView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.oozeetech.bizdesk.R.id.btnDelete;

@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
public class AddNewBizActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final int SELECT_PARTY_CODE = 101;
    public static final int SELECT_CUSTOMER_CODE = 102;
    final int RQS_PICK_CONTACT = 1;
    public double pltotalAmt = 0, plshipCharge = 0, plweight = 0, plrate = 0, plshipPer = 0;
    public double rghtotalAmt = 0, rghshipCharge = 0, rghCut = 0, rghweight = 0, rghrate = 0, rghshipPer = 0,
            rghFinaRate = 0, rghPremium = 0;
    List<AddNewBizRequest.CBizDetail> bizDetailList = new ArrayList<>();
    GetPartyAdapter adapter;
    AddItemAdapter itemAdapter;
    int editPosition = -1;
    @BindView(R.id.txtPartyName)
    DTextView txtPartyName;
    @BindView(R.id.imgAddParty)
    ImageView imgAddParty;
    @BindView(R.id.txtCustomer)
    DTextView txtCustomer;
    @BindView(R.id.imgAddCustomer)
    ImageView imgAddCustomer;
    @BindView(R.id.txtSaleDate)
    DTextView txtSaleDate;
    @BindView(R.id.edDueDays)
    DEditText edDueDays;
    @BindView(R.id.txtPaymentDate)
    DTextView txtPaymentDate;
    @BindView(R.id.spCurrency)
    Spinner spCurrency;
    @BindView(R.id.txtDeliveryDate)
    DTextView txtDeliveryDate;
    @BindView(R.id.btnAddItem)
    DButton btnAddItem;
    @BindView(R.id.lstNewItem)
    RecyclerView lstNewItem;
    @BindView(R.id.txtTotalWeight)
    DTextView txtTotalWeight;
    @BindView(R.id.txtAvgPrice)
    DTextView txtAvgPrice;
    @BindView(R.id.txtTotalAmount)
    DTextView txtTotalAmount;
    @BindView(R.id.edLessPer)
    DEditText edLessPer;
    @BindView(R.id.txtTotalLessAmt)
    DTextView txtTotalLessAmt;
    @BindView(R.id.edBrokeragePer)
    DEditText edBrokeragePer;
    @BindView(R.id.txtBrokerageAmt)
    DTextView txtBrokerageAmt;
    @BindView(R.id.edUsdRate)
    DEditText edUsdRate;
    @BindView(R.id.cbBrokerage)
    DCheckBox cbBrokerage;
    @BindView(R.id.btnAddBiz)
    DButton btnAddBiz;
    int dueDay = 0;
    DecimalFormat format = new DecimalFormat("00.00");
    Calendar now = Calendar.getInstance();
    DatePickerDialog dpd = DatePickerDialog.newInstance(
            this,
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
    );
    String type;
    int bizTypeId;
    String partyName = "", customerName = "";
    double partyId = 0, customerId = 0;
    double totalAmount = 0, totalWeight = 0, avgPrice = 0;
    @BindView(R.id.llTotal)
    LinearLayout llTotal;
    private AlertDialog dialog;
    private DTextView txtTitle;
    private DButtonMaterial btnCancel;
    private DButtonMaterial btnAdd;
    private DTextView txtplTitle;
    private DEditText edplItemName;
    private DEditText edplWeight;
    private DEditText edplRate;
    private DTextView txtplTotalAmt;
    private DCheckBox cbplIsShipping;
    private CardView plcardShipPer;
    private DEditText edplShippingPer;
    private LinearLayout llplShippingAmount;
    private DTextView txtplTotalShipAmt;
    private DButtonMaterial btnplCancel;
    private DButtonMaterial btnplAdd;
    private DEditText edPartyName;
    private DEditText edCompanyName;
    private DEditText edCustomerNo1;
    private DEditText edCustomerNo2;
    private ImageView imgContactBook;
    private DTextView txtrghTitle;
    private DEditText edrghItemName;
    private DEditText edrghWeight;
    private DEditText edrghCut;
    private DEditText edrghRate;
    private DEditText edrghPremium;
    private DTextView txtrghFinalRate;
    private DTextView txtrghTotalAmt;
    private DCheckBox cbrghIsShipping;
    private CardView cardrghShipPer;
    private DEditText edrghShippingPer;
    private LinearLayout llrghShippingAmount;
    private DTextView txtrghTotalShipAmt;
    private DButtonMaterial btnrghCancel;
    private DButtonMaterial btnrghAdd;
    private int itemPosition;
    private DButtonMaterial btnADCancel;
    private DButtonMaterial delete;
    ItemListener itemListener = new ItemListener() {
        @Override
        public void onEditClickListener(int position) {
            itemPosition = position;
            AddNewBizRequest.CBizDetail detail = bizDetailList.get(position);
            if (type.equals("Rough")) {
                openDialogRough();
                txtrghTitle.setText("Update Item");
                btnrghAdd.setText("Update");
                edrghItemName.setText(detail.getItemName());
                edrghWeight.setText(String.valueOf(detail.getCrt()));
                edrghCut.setText(detail.getCut());
                edrghRate.setText(String.valueOf(detail.getPricePerCrt()));
                edrghPremium.setText(String.valueOf(detail.getPremiumPer()));
                cbrghIsShipping.setChecked(detail.isIsShipping1());
                llrghShippingAmount.setVisibility(detail.isIsShipping1() ? View.VISIBLE : View.GONE);
                cardrghShipPer.setVisibility(detail.isIsShipping1() ? View.VISIBLE : View.GONE);
                edrghShippingPer.setText(String.valueOf(detail.isIsShipping1() ? detail.getShipPer() : ""));

                rghCut = edrghCut.getDouble();
                rghweight = edrghWeight.getDouble();
                rghrate = edrghRate.getDouble();
                rghPremium = edrghPremium.getDouble();

                rghFinaRate = rghrate + ((rghrate / 100) * rghPremium);
                rghtotalAmt = (rghrate * rghweight * rghCut) + (((rghrate * rghweight * rghCut) / 100) * rghPremium);
                txtrghTotalAmt.setText(String.format("%s", format.format(rghtotalAmt)));
                txtrghFinalRate.setText(String.format("%s", format.format(rghFinaRate)));
                if (detail.isIsShipping1()) {
                    rghshipPer = edrghShippingPer.getDouble();
                    rghshipCharge = ((rghtotalAmt / 100) * rghshipPer) + rghtotalAmt;
                    if (spCurrency.getSelectedItemPosition() + 1 == 1)
                        txtrghTotalShipAmt.setText(String.format("%s", format.format(rghshipCharge)) + " ₹ ");
                    else
                        txtrghTotalShipAmt.setText(String.format("%s", format.format(rghshipCharge)) + " $ ");
                }
                actionListenersRough();
            } else {
                openDialogPolished();
                txtplTitle.setText("Update Item");
                edplItemName.setText(detail.getItemName());
                edplWeight.setText(String.valueOf(detail.getCrt()));
                edplRate.setText(String.valueOf(detail.getPricePerCrt()));
                cbplIsShipping.setChecked(detail.isIsShipping1());
                llplShippingAmount.setVisibility(detail.isIsShipping1() ? View.VISIBLE : View.GONE);
                plcardShipPer.setVisibility(detail.isIsShipping1() ? View.VISIBLE : View.GONE);
                edplShippingPer.setText(String.valueOf(detail.isIsShipping1() ? detail.getShipPer() : ""));

                plweight = edplWeight.getDouble();
                plrate = edplRate.getDouble();
                pltotalAmt = plweight * plrate;
                txtplTotalAmt.setText(String.format("%s", format.format(pltotalAmt)));
                if (detail.isIsShipping1()) {
                    plshipPer = edplShippingPer.getDouble();
                    plshipCharge = ((pltotalAmt / 100) * plshipPer) + pltotalAmt;
                    if (spCurrency.getSelectedItemPosition() + 1 == 1)
                        txtplTotalShipAmt.setText(String.format("%s", format.format(plshipCharge)) + " ₹ ");
                    else
                        txtplTotalShipAmt.setText(String.format("%s", format.format(plshipCharge)) + " $ ");
                }
                btnplAdd.setText("Update");
                actionListenersPolish();
            }
        }

        @Override
        public void onDeleteClickListener(final int position) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_record, null);
            dialog = Utils.customDialog(getActivity(), v);
            txtTitle = v.findViewById(R.id.txtTitle);
            btnADCancel = v.findViewById(R.id.btnCancel);
            delete = v.findViewById(btnDelete);
            dialog.show();
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemAdapter.remove(position);
                    itemAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            btnADCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
    };
    private double lessAmt, brokAmt;
    private GetBizDetailResponse.Data d;
    private GetBizDetailResponse.Data.CBizDetail biz;
    private Callback<CommonResponse> commonResponseCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
            dismissProgress();
            if (response.isSuccessful()) {
                if (response.body().getReturnCode().equals("1")) {
                    if (type.equals("Update"))
                        showAlertDialog("Record Updated Successfully");
                    else
                        showAlertDialog("Record Inserted Successfully");

                } else if (response.body().getReturnCode().equals("21"))
                    showDialog("", response.body().getReturnMsg(), response.body().getReturnCode());
                else if (response.body().getReturnCode().equals("2")) {
                    showAlertDialog(response.body().getReturnMsg());
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

    private void showAlertDialog(String msg) {
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle("Biz Desk")
                .setMessage(msg)
                .setPositiveButton(R.string.hint_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).create();
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_biz);
        ButterKnife.bind(this);
        initNewItemList();
        handleIntent();

        Utils.setDrawableTint(txtPartyName, getResources().getDrawable(R.drawable.ic_search_), getResources().getColor(R.color.colorTextBlack));
        Utils.setDrawableTint(txtCustomer, getResources().getDrawable(R.drawable.ic_search_), getResources().getColor(R.color.colorTextBlack));
        Utils.setDrawableTint(txtSaleDate, getResources().getDrawable(R.drawable.ic_date_), getResources().getColor(R.color.colorTextBlack));
        Utils.setDrawableTint(txtPaymentDate, getResources().getDrawable(R.drawable.ic_date_), getResources().getColor(R.color.colorTextBlack));
        Utils.setDrawableTint(txtDeliveryDate, getResources().getDrawable(R.drawable.ic_date_), getResources().getColor(R.color.colorTextBlack));

        spCurrency.setOnItemSelectedListener(this);
        CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(getActivity(), getActivity().getResources().getStringArray(R.array.currency_type));
        spCurrency.setAdapter(customAdapter);
        setPercent();
    }

    private void setPercent() {
        edLessPer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                double temp = edLessPer.getDouble();

                lessAmt = totalAmount - ((totalAmount / 100) * temp);
                brokAmt = lessAmt / 100 * temp;
                txtTotalLessAmt.setText(String.format("%s", format.format(lessAmt)));
                txtBrokerageAmt.setText(String.format("%s", format.format(brokAmt)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edBrokeragePer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                double temp = edBrokeragePer.getDouble();

                brokAmt = lessAmt / 100 * temp;
                txtBrokerageAmt.setText(String.format("%s", format.format(brokAmt)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initNewItemList() {
        itemAdapter = new AddItemAdapter(getActivity(), itemListener);
        lstNewItem.setLayoutManager(new LinearLayoutManager(getActivity()));
        lstNewItem.setHasFixedSize(true);

        lstNewItem.setItemAnimator(new FadeInAnimator());
        itemAdapter.setMode(Attributes.Mode.Single);
        lstNewItem.setAdapter(itemAdapter);
    }

    private void handleIntent() {
        if (getIntent().hasExtra(Constants.TYPE)) {
            type = getIntent().getStringExtra(Constants.TYPE);
            if (type.equals("Rough")) {
                showToolBar(true, "Add new Rough biz");
                edBrokeragePer.setText(pref.getString(Constants.ROUGH_BROKERAGE));
            } else if (type.equals("Polish")) {
                showToolBar(true, "Add new Polish biz");
                edBrokeragePer.setText(pref.getString(Constants.POLISH_BROKERAGE));
            } else if (type.equals("Update")) {
                d = (GetBizDetailResponse.Data) getIntent().getSerializableExtra(Constants.UPDATE_BIZ);
                setBizDetail(d);
            }
        }
        edUsdRate.setText(pref.getString(Constants.EXCHANGE_RATE));
    }

    private void setBizDetail(GetBizDetailResponse.Data d) {
        showToolBar(true, "Update " + d.getBizType() + " Biz");
        btnAddBiz.setText("Update Biz");

        partyId = d.getPartyID();
        customerId = d.getCustomerID();
        partyName = d.getPartyName();
        customerName = d.getCustomerName();

        txtPartyName.setText(d.getPartyName());
        txtCustomer.setText(d.getCustomerName());
        txtSaleDate.setText(DateUtils.convertDateStringToString(d.getSaleDate(), DateUtils.AppCurrentDateFormat, DateUtils.AppDateFormat));
        txtPaymentDate.setText(DateUtils.convertDateStringToString(d.getPaymentDate(), DateUtils.AppCurrentDateFormat, DateUtils.AppDateFormat));
        txtDeliveryDate.setText(DateUtils.convertDateStringToString(d.getDeliveryDate(), DateUtils.AppCurrentDateFormat, DateUtils.AppDateFormat));
        edDueDays.setText(String.valueOf(d.getDueDay()));
        edLessPer.setText(String.valueOf(d.getLessPer()));
        edBrokeragePer.setText(String.valueOf(d.getBrokeragePer()));
        edUsdRate.setText(String.valueOf(d.getExchangeRate()));
        txtTotalAmount.setText(d.getTotalAmt());
        txtTotalWeight.setText(String.valueOf(d.getTotalCrt()));
        txtAvgPrice.setText(String.valueOf(d.getAvgPricePerCrt()));
        txtTotalLessAmt.setText(String.valueOf(format.format(d.getLessPerAmt())));
        double brok = Double.parseDouble(d.getBrokerageAmt());
        txtBrokerageAmt.setText(String.valueOf(format.format(brok)));

        for (int i = 0; i < d.getCBizDetail().size(); i++) {
            String symbol = ((spCurrency.getSelectedItemPosition() + 1) == 1) ? " ₹ " : " $ ";

            AddNewBizRequest.CBizDetail bizDetail = new AddNewBizRequest.CBizDetail();

            bizDetail.setType(d.getBizType());
            if (d.getBizType().equals("Rough")) {
                biz = d.getCBizDetail().get(i);
                bizDetail.setItemName(biz.getItemName());
                bizDetail.setCrt(biz.getCrt());
                bizDetail.setCut(biz.getCut());
                bizDetail.setPricePerCrt(Double.parseDouble(biz.getPricePerCrt()));
                bizDetail.setPremiumPer(biz.getPremiumPer());
                bizDetail.setIsShipping1(biz.isIsShipping());
                bizDetail.setShipPer(biz.isIsShipping() ? biz.getShipPer() : 0.0);
                itemAdapter.add(bizDetail, symbol);

                llTotal.setVisibility(View.VISIBLE);
                bizDetailList.add(bizDetail);
            } else {
                biz = d.getCBizDetail().get(i);
                bizDetail.setItemName(biz.getItemName());
                bizDetail.setCrt(biz.getCrt());
                bizDetail.setPricePerCrt(Double.parseDouble(biz.getPricePerCrt()));
                bizDetail.setIsShipping1(biz.isIsShipping());
                bizDetail.setShipPer(biz.isIsShipping() ? biz.getShipPer() : 0.0);
                itemAdapter.add(bizDetail, symbol);

                llTotal.setVisibility(View.VISIBLE);
                bizDetailList.add(bizDetail);
            }
        }
    }

    @OnClick({R.id.txtPartyName, R.id.imgAddParty, R.id.txtCustomer, R.id.imgAddCustomer, R.id.txtSaleDate, R.id.txtPaymentDate, R.id.txtDeliveryDate, R.id.btnAddItem, R.id.btnAddBiz})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.txtPartyName:
                intent = new Intent(getActivity(), SearchPartyActivity.class);
                intent.putExtra(Constants.TYPE, "Party");
                startActivityForResult(intent, SELECT_PARTY_CODE);
                break;
            case R.id.imgAddParty:
                openAddPartyDialog();
                txtTitle.setText("New Party");
                edPartyName.setHint("Party Name");
                break;
            case R.id.txtCustomer:
                intent = new Intent(getActivity(), SearchPartyActivity.class);
                intent.putExtra(Constants.TYPE, "Customer");
                startActivityForResult(intent, SELECT_CUSTOMER_CODE);
                break;
            case R.id.imgAddCustomer:
                openAddPartyDialog();
                txtTitle.setText("New Customer");
                edPartyName.setHint("Customer Name");
                break;
            case R.id.txtSaleDate:
                String saleDate = txtSaleDate.getText().toString();
                if (!saleDate.isEmpty()) {
                    String[] splitSaleDate = saleDate.split("/");
                    int saleDate1 = Integer.parseInt(splitSaleDate[0]);
                    int saleMonth = Integer.parseInt(splitSaleDate[1]);
                    int saleYear = Integer.parseInt(splitSaleDate[2]);
                    dpd = DatePickerDialog.newInstance(this, saleYear, saleMonth - 1, saleDate1);
                } else
                    dpd = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                if (!dpd.isAdded())
                    dpd.show(getFragmentManager(), "SaleDateDialog");
                break;
            case R.id.txtPaymentDate:
                if (txtSaleDate.getText().toString().isEmpty()) {
                    showDialogDone("Date", "Please Select Sale date First");
                } else {
                    String paymentDate = txtPaymentDate.getText().toString();
                    if (!paymentDate.isEmpty()) {
                        String[] splitPaymentDate = paymentDate.split("/");
                        int paymentDate1 = Integer.parseInt(splitPaymentDate[0]);
                        int paymentMonth = Integer.parseInt(splitPaymentDate[1]);
                        int paymentYear = Integer.parseInt(splitPaymentDate[2]);
                        dpd = DatePickerDialog.newInstance(this, paymentYear, paymentMonth - 1, paymentDate1);
                    } else
                        dpd = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    if (!dpd.isAdded())
                        dpd.show(getFragmentManager(), "PaymentDateDialog");
                }
                break;
            case R.id.txtDeliveryDate:
                String deliveryDate = txtDeliveryDate.getText().toString();
                if (!deliveryDate.isEmpty()) {
                    String[] splitDeliveryDate = deliveryDate.split("/");
                    int deliveryDate1 = Integer.parseInt(splitDeliveryDate[0]);
                    int deliveryMonth = Integer.parseInt(splitDeliveryDate[1]);
                    int deliveryYear = Integer.parseInt(splitDeliveryDate[2]);
                    dpd = DatePickerDialog.newInstance(this, deliveryYear, deliveryMonth - 1, deliveryDate1);
                } else
                    dpd = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                if (!dpd.isAdded())
                    dpd.show(getFragmentManager(), "DeliveryDateDialog");
                break;
            case R.id.btnAddItem:
                if (type.equals("Rough")) {
                    bizTypeId = 2;
                } else if (type.equals("Polish")) {
                    bizTypeId = 1;
                } else if (type.equals("Update")) {
                    bizTypeId = d.getBizTypeID();
                }
                if (bizTypeId == 1) {
                    openDialogPolished();
                    txtplTitle.setText("Add New Item");
                    btnplAdd.setText("Add");
                    actionListenersPolish();
                } else {
                    openDialogRough();
                    txtrghTitle.setText("Add New Item");
                    btnrghAdd.setText("Add");
                    actionListenersRough();
                }
                break;
            case R.id.btnAddBiz:
                if (type.equals("Update")) {
                    bizTypeId = d.getBizTypeID();
                }
                callAddNewBizAPI();
                break;
        }
    }

    private void actionListenersRough() {

        edrghCut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                rghCut = edrghCut.getDouble();
                rghweight = edrghWeight.getDouble();
                rghrate = edrghRate.getDouble();
                rghPremium = edrghPremium.getDouble();

                rghFinaRate = rghrate + ((rghrate / 100) * rghPremium);
//                rghtotalAmt = (rghrate * (rghweight * rghCut)) + (((rghrate * rghweight) / 100) * rghPremium);
                rghtotalAmt = (rghrate * rghweight * rghCut) + (((rghrate * rghweight * rghCut) / 100) * rghPremium);

                txtrghTotalAmt.setText(String.format("%s", format.format(rghtotalAmt)));
                txtrghFinalRate.setText(String.format("%s", format.format(rghFinaRate)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edrghWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                rghCut = edrghCut.getDouble();
                rghweight = edrghWeight.getDouble();
                rghrate = edrghRate.getDouble();
                rghPremium = edrghPremium.getDouble();

                rghFinaRate = rghrate + ((rghrate / 100) * rghPremium);
                //                rghtotalAmt = (rghrate * rghweight) + (((rghrate * rghweight) / 100) * rghPremium);
                rghtotalAmt = (rghrate * rghweight * rghCut) + (((rghrate * rghweight * rghCut) / 100) * rghPremium);

                txtrghTotalAmt.setText(String.format("%s", format.format(rghtotalAmt)));
                txtrghFinalRate.setText(String.format("%s", format.format(rghFinaRate)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edrghRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                rghCut = edrghCut.getDouble();
                rghweight = edrghWeight.getDouble();
                rghrate = edrghRate.getDouble();
                rghPremium = edrghPremium.getDouble();

                rghFinaRate = rghrate + ((rghrate / 100) * rghPremium);
                //                rghtotalAmt = (rghrate * rghweight) + (((rghrate * rghweight) / 100) * rghPremium);
//                rghtotalAmt = (rghrate * (rghweight * rghCut)) + (((rghrate * rghweight) / 100) * rghPremium);
                rghtotalAmt = (rghrate * rghweight * rghCut) + (((rghrate * rghweight * rghCut) / 100) * rghPremium);

                txtrghTotalAmt.setText(String.format("%s", format.format(rghtotalAmt)));
                txtrghFinalRate.setText(String.format("%s", format.format(rghFinaRate)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edrghPremium.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                rghCut = edrghCut.getDouble();
                rghweight = edrghWeight.getDouble();
                rghrate = edrghRate.getDouble();
                rghPremium = edrghPremium.getDouble();

                rghFinaRate = rghrate + ((rghrate / 100) * rghPremium);
//                rghtotalAmt = (rghrate * rghweight) + (((rghrate * rghweight) / 100) * rghPremium);
//                rghtotalAmt = (rghrate * (rghweight * rghCut)) + (((rghrate * rghweight) / 100) * rghPremium);
                rghtotalAmt = (rghrate * rghweight * rghCut) + (((rghrate * rghweight * rghCut) / 100) * rghPremium);

                txtrghTotalAmt.setText(String.format("%s", format.format(rghtotalAmt)));
                txtrghFinalRate.setText(String.format("%s", format.format(rghFinaRate)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        cbrghIsShipping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cbrghIsShipping.isChecked()) {
                    cardrghShipPer.setVisibility(View.VISIBLE);
                    llrghShippingAmount.setVisibility(View.VISIBLE);
                    edrghShippingPer.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            rghshipPer = edrghShippingPer.getDouble();
                            rghshipCharge = ((rghtotalAmt / 100) * rghshipPer) + rghtotalAmt;
                            if (spCurrency.getSelectedItemPosition() + 1 == 1) {
                                txtrghTotalShipAmt.setText(String.format("%s", format.format(rghshipCharge)) + " ₹ ");
                            } else {
                                txtrghTotalShipAmt.setText(String.format("%s", format.format(rghshipCharge)) + " $ ");
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                        }
                    });
                } else {
                    cardrghShipPer.setVisibility(View.GONE);
                    llrghShippingAmount.setVisibility(View.GONE);
                }
            }
        });
    }

    private void actionListenersPolish() {

        edplRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                plweight = edplWeight.getDouble();
                plrate = edplRate.getDouble();
                pltotalAmt = plweight * plrate;
                if (edplRate.equals(""))
                    txtplTotalAmt.setText(String.format("%s", format.format(plweight)));
                else
                    txtplTotalAmt.setText(String.format("%s", format.format(pltotalAmt)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        edplWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                plweight = edplWeight.getDouble();
                plrate = edplRate.getDouble();
                pltotalAmt = plweight * plrate;
                if (edplWeight.equals(""))
                    txtplTotalAmt.setText(String.format("%s", format.format(plrate)));
                else
                    txtplTotalAmt.setText(String.format("%s", format.format(pltotalAmt)));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        cbplIsShipping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cbplIsShipping.isChecked()) {
                    plcardShipPer.setVisibility(View.VISIBLE);
                    llplShippingAmount.setVisibility(View.VISIBLE);
                    edplShippingPer.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            plshipPer = edplShippingPer.getDouble();
                            plshipCharge = ((pltotalAmt / 100) * plshipPer) + pltotalAmt;
                            if (spCurrency.getSelectedItemPosition() + 1 == 1) {
                                txtplTotalShipAmt.setText(String.format("%s", format.format(plshipCharge)) + " ₹ ");
                            } else {
                                txtplTotalShipAmt.setText(String.format("%s", format.format(plshipCharge)) + " $ ");

                            }

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                        }
                    });
                } else {
                    plcardShipPer.setVisibility(View.GONE);
                    llplShippingAmount.setVisibility(View.GONE);
                }
            }
        });
        //adapter.addAll();
    }

    private void setFinalValue() {
        llTotal.setVisibility(View.VISIBLE);
        if (type.equals("Update")) {
            if (d.getBizType().equals("Rough")) {
                totalAmount = totalAmount + (cbrghIsShipping.isChecked() ? rghshipCharge : rghtotalAmt);
                totalWeight = rghweight + totalWeight;
                avgPrice = totalAmount / totalWeight;
            } else {
                totalAmount = totalAmount + (cbplIsShipping.isChecked() ? plshipCharge : pltotalAmt);
                totalWeight = plweight + totalWeight;
                avgPrice = totalAmount / totalWeight;
            }
        } else {
            if (type.equals("Rough")) {
                totalAmount = totalAmount + (cbrghIsShipping.isChecked() ? rghshipCharge : rghtotalAmt);
                totalWeight = rghweight + totalWeight;
                avgPrice = totalAmount / totalWeight;
            } else {
                totalAmount = totalAmount + (cbplIsShipping.isChecked() ? plshipCharge : pltotalAmt);
                totalWeight = plweight + totalWeight;
                avgPrice = totalAmount / totalWeight;
            }
        }
        brokAmt = (totalAmount / 100) * edBrokeragePer.getDouble();
        txtTotalAmount.setText(String.format("%s", format.format(totalAmount)));
        txtTotalWeight.setText(String.format("%s", format.format(totalWeight)));
        txtAvgPrice.setText(String.format("%s", format.format(avgPrice)));
        txtTotalLessAmt.setText(String.format("%s", format.format(totalAmount)));
        txtBrokerageAmt.setText(String.format("%s", format.format(brokAmt)));
    }

    private void openDialogPolished() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_new_polish_item, null);
        dialog = Utils.customDialog(getActivity(), v);
        findViewsPolish(v);
        if (spCurrency.getSelectedItemPosition() + 1 == 1) {
            edplRate.setHint("Rate ₹/Crt");
            txtplTotalShipAmt.setText("00.00 ₹");
        } else {
            edplRate.setHint("Rate $/Crt");
            txtplTotalShipAmt.setText("00.00 $");
        }
        dialog.show();
    }

    private void findViewsPolish(View v) {
        txtplTitle = v.findViewById(R.id.txtplTitle);
        edplItemName = v.findViewById(R.id.edplItemName);
        edplWeight = v.findViewById(R.id.edplWeight);
        edplRate = v.findViewById(R.id.edplRate);
        txtplTotalAmt = v.findViewById(R.id.txtplTotalAmt);
        cbplIsShipping = v.findViewById(R.id.cbplIsShipping);
        plcardShipPer = v.findViewById(R.id.plcardShipPer);
        edplShippingPer = v.findViewById(R.id.edplShippingPer);
        llplShippingAmount = v.findViewById(R.id.llplShippingAmount);
        txtplTotalShipAmt = v.findViewById(R.id.txtplTotalShipAmt);
        btnplCancel = v.findViewById(R.id.btnplCancel);
        btnplAdd = v.findViewById(R.id.btnplAdd);

        btnplAdd.setOnClickListener(this);
        btnplCancel.setOnClickListener(this);
    }

    private void openDialogRough() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_new_rough_item, null);
        dialog = Utils.customDialog(getActivity(), v);
        findViewsRough(v);
        if (spCurrency.getSelectedItemPosition() + 1 == 1) {
            edrghRate.setHint("Rate ₹/Crt");
            txtrghTotalShipAmt.setText("00.00 ₹");
        } else {
            edrghRate.setHint("Rate $/Crt");
            txtrghTotalShipAmt.setText("00.00 $");
        }
        dialog.show();
    }

    private void findViewsRough(View v) {
        txtrghTitle = v.findViewById(R.id.txtrghTitle);
        edrghItemName = v.findViewById(R.id.edrghItemName);
        edrghWeight = v.findViewById(R.id.edrghWeight);
        edrghCut = v.findViewById(R.id.edrghCut);
        edrghRate = v.findViewById(R.id.edrghRate);
        edrghPremium = v.findViewById(R.id.edrghPremium);
        txtrghFinalRate = v.findViewById(R.id.txtrghFinalRate);
        txtrghTotalAmt = v.findViewById(R.id.txtrghTotalAmt);
        cbrghIsShipping = v.findViewById(R.id.cbrghIsShipping);
        cardrghShipPer = v.findViewById(R.id.cardrghShipPer);
        edrghShippingPer = v.findViewById(R.id.edrghShippingPer);
        llrghShippingAmount = v.findViewById(R.id.llrghShippingAmount);
        txtrghTotalShipAmt = v.findViewById(R.id.txtrghTotalShipAmt);
        btnrghCancel = v.findViewById(R.id.btnrghCancel);
        btnrghAdd = v.findViewById(R.id.btnrghAdd);

        btnrghAdd.setOnClickListener(this);
        btnrghCancel.setOnClickListener(this);
    }

    private void callAddNewBizAPI() {
        String saleDate = txtSaleDate.getText().toString().trim();
        String paymentDate = txtPaymentDate.getText().toString().trim();
        String deliveryDate = txtDeliveryDate.getText().toString().trim();

        if (partyName.isEmpty())
            showToast("Please Select Party Name", true);
        else if (customerName.isEmpty())
            showToast("Please Select Customer Name", true);
        else if (saleDate.isEmpty())
            showToast("Please Select Sale Date", true);
        else if (paymentDate.isEmpty())
            showToast("Please Select Party Name", true);
//        else if (deliveryDate.isEmpty())
//            showToast("Please Select Delivery Date", true);
        else if (bizDetailList.size() == 0)
            showToast("Please Enter At Least one Item", true);
        else if (!Utils.isInternetConnected(getActivity()))
            showNoInternetDialog();
        else {
            showProgress();
            AddNewBizRequest request = new AddNewBizRequest();
            request.setAPIKey(Constants.API_KEY);
            request.setToken(Utils.getToken(getActivity()));
            request.setBizMasterID(type.equals("Update") ? d.getBizMasterID() : 0);
            request.setBizTypeID(bizTypeId);
            request.setPartyID(partyId);
            request.setCustomerID(customerId);
            request.setSaleDate(saleDate);
            request.setDueDay(edDueDays.getInt());
            request.setPaymentDate(paymentDate);
            request.setCurrencyID(spCurrency.getSelectedItemPosition() + 1);
            request.setDeliveryDate(deliveryDate);
            request.setLessPer(edLessPer.getDouble());
            request.setBrokeragePer(edBrokeragePer.getDouble());
            request.setIsCustomerPay(cbBrokerage.isChecked() ? 1 : 0);
            request.setCBizDetailForIOS("");
            request.setCBizDetail(bizDetailList);

            requestAPI.postAddNewBizRequest(request).enqueue(commonResponseCallback);
        }
    }

    @OnTextChanged(value = R.id.edDueDays, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void afterDueDayInput(Editable editable) {
        if (type.equals("Update")) {
            d.setDueDay(edDueDays.getInt());
            dueDay = d.getDueDay();
//            setPaymentDate();
        } else {
            dueDay = edDueDays.getInt();
//            setPaymentDate();
        }
        setPaymentDate();
    }

    private void setPaymentDate() {
        if (dueDay > 0 && !txtSaleDate.getText().toString().trim().isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            if (type.equals("Update")) {
                calendar.setTime(DateUtils.convertStringToDate(txtSaleDate.getText().toString().trim(), DateUtils.AppDateFormat));
//                calendar.setTime(DateUtils.convertDateStringToDate(d.getPaymentDate(), DateUtils.AppCurrentDateFormat, DateUtils.AppDateFormat));
                calendar.add(Calendar.DAY_OF_MONTH, dueDay);
                txtPaymentDate.setText(DateUtils.convertDateToString(calendar.getTime(), DateUtils.AppDateFormat));
            } else {
                calendar.setTime(DateUtils.convertStringToDate(txtSaleDate.getText().toString().trim(), DateUtils.AppDateFormat));
                calendar.add(Calendar.DAY_OF_MONTH, dueDay);
                txtPaymentDate.setText(DateUtils.convertDateToString(calendar.getTime(), DateUtils.AppDateFormat));
            }
        } else txtPaymentDate.setText("");
    }

    private void openAddPartyDialog() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_new_party, null);
        dialog = Utils.customDialog(getActivity(), v);
        addPartyFindViews(v);
        dialog.show();

    }

    private void addPartyFindViews(View v) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == SELECT_PARTY_CODE) {
            GetPartyResponse.Data d = (GetPartyResponse.Data) data.getSerializableExtra(Constants.GET_PARTY);
            partyId = Long.parseLong(d.getID());
            partyName = d.getName();
            if (customerName.isEmpty())
                txtCustomer.setHint("Customer");
        } else if (requestCode == SELECT_CUSTOMER_CODE) {
            GetPartyResponse.Data d = (GetPartyResponse.Data) data.getSerializableExtra(Constants.GET_PARTY);
            customerId = Long.parseLong(d.getID());
            customerName = d.getName();
            if (partyName.isEmpty())
                txtPartyName.setHint("Party");
        } else if (requestCode == RQS_PICK_CONTACT) {
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
        }
        setData();
    }

    private void setData() {
        txtPartyName.setText(partyName);
        txtCustomer.setText(customerName);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (view.getTag().equals("SaleDateDialog")) {
            txtSaleDate.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
            setPaymentDate();
        } else if (view.getTag().equals("PaymentDateDialog")) {
            txtPaymentDate.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
            if (!txtSaleDate.getText().toString().trim().isEmpty()) {
                long diff = DateUtils.converDatetomillis(txtPaymentDate.getText().toString().trim(), DateUtils.AppDateFormat) - DateUtils.converDatetomillis(txtSaleDate.getText().toString().trim(), DateUtils.AppDateFormat);
                System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                dueDay = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                edDueDays.setText(String.format("%d", dueDay));
            }
        } else if (view.getTag().equals("DeliveryDateDialog")) {
            txtDeliveryDate.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                callAddParty();
                break;
            case R.id.btnCancel:
                dialog.dismiss();
                break;
            case R.id.imgContactBook:
                if (!marshMallowPermission.checkPermissionForReadContacts()) {
                    marshMallowPermission.requestPermissionForReadContacts();
                } else {
                    openContactBook();
                }
            case R.id.btnrghAdd:
                if (btnrghAdd.getText().equals("Update"))
                    updateRoughItem();
                else
                    addNewRoughItem();
                break;
            case R.id.btnrghCancel:
                dialog.dismiss();
                break;
            case R.id.btnplAdd:
                if (btnplAdd.getText().equals("Update"))
                    updatePolishItem();
                else
                    addNewPolishItem();
                break;
            case R.id.btnplCancel:
                dialog.dismiss();
                break;
        }
    }

    private void updatePolishItem() {
        if (edplItemName.getText().toString().trim().equals(""))
            showToast("Please Enter Name", true);
        else if (edplWeight.getText().toString().trim().equals(""))
            showToast("Please Enter Weight", true);
        else if (edplRate.getText().toString().trim().equals(""))
            showToast("Please Enter Rate", true);
        else if (cbplIsShipping.isChecked() && edplShippingPer.getString().equals(""))
            showToast("Please Enter Shipping charge", true);
        else {
            dialog.dismiss();
            setFinalValue();
            AddNewBizRequest.CBizDetail bizDetail = new AddNewBizRequest.CBizDetail();

            bizDetail.setType(type);
            bizDetail.setItemName(edplItemName.getString());
            bizDetail.setCrt(edplWeight.getDouble());
            bizDetail.setPricePerCrt(edplRate.getDouble());
            bizDetail.setShipPer(cbplIsShipping.isChecked() ? edplShippingPer.getDouble() : 0.0);
            bizDetail.setIsShipping1(cbplIsShipping.isChecked());

            itemAdapter.remove(itemPosition);
            itemAdapter.notifyDataSetChanged();
//            bizDetailList.add(itemPosition, bizDetail);
            itemAdapter.add(itemPosition, bizDetail);
            itemAdapter.notifyDataSetChanged();
        }
    }

    private void updateRoughItem() {
        if (edrghItemName.getText().toString().trim().equals(""))
            showToast("Please Enter Name", true);
        else if (edrghWeight.getText().toString().trim().equals(""))
            showToast("Please Enter Weight", true);
        else if (edrghRate.getText().toString().trim().equals(""))
            showToast("Please Enter Rate", true);
        else if (edrghCut.getText().toString().trim().equals(""))
            showToast("Please Enter Cut", true);
        else if (edrghPremium.getText().toString().trim().equals(""))
            showToast("Please Enter Premium", true);
        else if (cbrghIsShipping.isChecked() && edrghShippingPer.getString().equals(""))
            showToast("Please Enter Shipping charge", true);
        else {
            dialog.dismiss();
            setFinalValue();
            AddNewBizRequest.CBizDetail bizDetail = new AddNewBizRequest.CBizDetail();

            bizDetail.setType(type);
            bizDetail.setItemName(edrghItemName.getString());
            bizDetail.setCrt(edrghWeight.getDouble());
            bizDetail.setCut(edrghCut.getString());
            bizDetail.setPricePerCrt(edrghRate.getDouble());
            bizDetail.setPremiumPer(edrghPremium.getDouble());
            bizDetail.setIsShipping1(cbrghIsShipping.isChecked());
            bizDetail.setShipPer(cbrghIsShipping.isChecked() ? edrghShippingPer.getDouble() : 0.0);

//            bizDetailList.remove(itemPosition);
//            itemAdapter.remove(itemPosition);
//            itemAdapter.notifyDataSetChanged();
//
//            bizDetailList.add(itemPosition, bizDetail);
//            itemAdapter.add(itemPosition, bizDetail);
//            itemAdapter.notifyDataSetChanged();

            itemAdapter.remove(itemPosition);
            itemAdapter.notifyDataSetChanged();
//            bizDetailList.add(itemPosition, bizDetail);
            itemAdapter.add(itemPosition, bizDetail);
            itemAdapter.notifyDataSetChanged();
        }
    }

    private void addNewPolishItem() {
        //This is for Polished Item
        if (edplItemName.getText().toString().trim().equals(""))
            showToast("Please Enter Name", true);
        else if (edplWeight.getText().toString().trim().equals(""))
            showToast("Please Enter Weight", true);
        else if (edplRate.getText().toString().trim().equals(""))
            showToast("Please Enter Rate", true);
        else if (cbplIsShipping.isChecked() && edplShippingPer.getString().equals(""))
            showToast("Please Enter Shipping charge", true);
        else {
            dialog.dismiss();
            setFinalValue();
            AddNewBizRequest.CBizDetail bizDetail = new AddNewBizRequest.CBizDetail();
            String symbol = ((spCurrency.getSelectedItemPosition() + 1) == 1) ? " ₹ " : " $ ";

            bizDetail.setType(type);
            bizDetail.setItemName(edplItemName.getString());
            bizDetail.setCrt(edplWeight.getDouble());
            bizDetail.setPricePerCrt(edplRate.getDouble());
            bizDetail.setShipPer(cbplIsShipping.isChecked() ? edplShippingPer.getDouble() : 0.0);
            bizDetail.setIsShipping1(cbplIsShipping.isChecked());
            itemAdapter.add(bizDetail, symbol);

            llTotal.setVisibility(View.VISIBLE);
            bizDetailList.add(bizDetail);
//            list.add(edplShippingPer.getDouble());
        }
    }

    private void addNewRoughItem() {
        //This is for rough

        if (edrghItemName.getText().toString().trim().equals(""))
            showToast("Please Enter Name", true);
        else if (edrghWeight.getText().toString().trim().equals(""))
            showToast("Please Enter Weight", true);
        else if (edrghRate.getText().toString().trim().equals(""))
            showToast("Please Enter Rate", true);
        else if (edrghCut.getText().toString().trim().equals(""))
            showToast("Please Enter Cut", true);
        else if (edrghPremium.getText().toString().trim().equals(""))
            showToast("Please Enter Premium", true);
        else if (cbrghIsShipping.isChecked() && edrghShippingPer.getString().equals(""))
            showToast("Please Enter Shipping charge", true);
        else {
            dialog.dismiss();
            setFinalValue();
            AddNewBizRequest.CBizDetail bizDetail = new AddNewBizRequest.CBizDetail();
            String symbol = ((spCurrency.getSelectedItemPosition() + 1) == 1) ? " ₹ " : " $ ";

            bizDetail.setType(type);
            bizDetail.setItemName(edrghItemName.getString());
            bizDetail.setCrt(edrghWeight.getDouble());
            bizDetail.setCut(edrghCut.getString());
            bizDetail.setPricePerCrt(edrghRate.getDouble());
            bizDetail.setPremiumPer(edrghPremium.getDouble());
            bizDetail.setIsShipping1(cbrghIsShipping.isChecked());
            bizDetail.setShipPer(cbrghIsShipping.isChecked() ? edrghShippingPer.getDouble() : 0.0);
            itemAdapter.add(bizDetail, symbol);
            bizDetailList.add(bizDetail);
        }
    }

    private void callAddParty() {
        String partyName = edPartyName.getString();
        String companyName = edCompanyName.getString();
        String customerNo1 = edCustomerNo1.getString();
        String customerNo2 = edCustomerNo2.getString();

        if (partyName.isEmpty())
            showToast("Please Enter Name", true);
        else if (companyName.isEmpty())
            showToast("Please Enter Company Name", true);
        else if (customerNo1.isEmpty() || customerNo1.length() != 10)
            showToast("Enter Your Contact Number", true);
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
        }
    }

    private void openContactBook() {
        final Uri uriContact = ContactsContract.Contacts.CONTENT_URI;
        Intent intentPickContact = new Intent(Intent.ACTION_PICK, uriContact);
        startActivityForResult(intentPickContact, RQS_PICK_CONTACT);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

}