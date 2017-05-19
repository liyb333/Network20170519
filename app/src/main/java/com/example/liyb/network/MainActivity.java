package com.example.liyb.network;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final int NP_CELL_INFO_UPDATE = 1001;

    private PhoneInfoThread phoneInfoThread;
    private  int msgcount;
    public Handler mMainHandler;
    // for current
    private List<CellGeneralInfo> CellInfoList;
    private CellnfoRecycleViewAdapter myRecycleViewAdapter;
    private RecyclerView recyclerView;
    //for history
    private List<CellGeneralInfo> HistoryServerCellList;
    private CellnfoRecycleViewAdapter historyRecycleViewAdapter;
    private RecyclerView historyrecyclerView;

    void InitProcessThread()
    {
        mMainHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == NP_CELL_INFO_UPDATE)
                {
                    msgcount++;
                    Bundle bundle = msg.getData();
                    myRecycleViewAdapter.notifyDataSetChanged();
                    historyRecycleViewAdapter.notifyDataSetChanged();
                    TextView tvTime = (TextView)findViewById(R.id.tvTimeleaps);
                    tvTime.setText("Time:" + msgcount);
                    TextView tvAllCellInfo = (TextView)findViewById(R.id.tvCellCount);
                    tvAllCellInfo.setText("("+HistoryServerCellList.size()+")");

                    TextView tvDeviceId = (TextView)findViewById(R.id.tvDeviceId);
                    tvDeviceId.setText("DeviceId:" + phoneInfoThread.deviceId);

                    TextView tvRatType = (TextView)findViewById(R.id.tvRatType);
                    tvRatType.setText("RatType:"+phoneInfoThread.ratType);

                    TextView tvMnc = (TextView)findViewById(R.id.tMnc);
                    tvMnc.setText("Mnc:"+phoneInfoThread.mnc);

                    TextView tvMcc = (TextView)findViewById(R.id.tvMcc);
                    tvMcc.setText("Mcc:"+phoneInfoThread.mcc);

                    TextView tvOperatorName = (TextView)findViewById(R.id.tvOperaterName);
                    tvOperatorName.setText("Operator:"+phoneInfoThread.operaterName);

                    TextView tvImsi = (TextView)findViewById(R.id.tvImsi);
                    tvImsi.setText("Imsi:"+phoneInfoThread.Imsi);

                    TextView tvLine1Number = (TextView)findViewById(R.id.tvLine1Number);
                    tvLine1Number.setText("LN:"+phoneInfoThread.line1Number);

                    TextView tvSerialNum = (TextView)findViewById(R.id.tvSerialNum);
                    tvSerialNum.setText("SN:"+phoneInfoThread.serialNumber);

                    TextView tvModel = (TextView)findViewById(R.id.tvModel);
                    tvModel.setText("Model:" + phoneInfoThread.phoneModel);

                    TextView tvSoftwareVersion = (TextView)findViewById(R.id.tvSoftware);
                    tvSoftwareVersion.setText("Version:" + phoneInfoThread.deviceSoftwareVersion);

                }
                super.handleMessage(msg);
            }
        };

        phoneInfoThread = new PhoneInfoThread(MainActivity.this);
        phoneInfoThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CellInfoList = new ArrayList<CellGeneralInfo>();
        recyclerView = (RecyclerView)findViewById(R.id.myrcv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        myRecycleViewAdapter  = new CellnfoRecycleViewAdapter(MainActivity.this,CellInfoList);
        recyclerView.setAdapter(myRecycleViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //
        HistoryServerCellList = new ArrayList<CellGeneralInfo>();
        historyrecyclerView = (RecyclerView)findViewById(R.id.historyrcv);
        LinearLayoutManager historylayoutManager = new LinearLayoutManager(this);
        historylayoutManager.setOrientation(OrientationHelper.VERTICAL);
        historyrecyclerView.setLayoutManager(historylayoutManager);
        historyRecycleViewAdapter  = new CellnfoRecycleViewAdapter(MainActivity.this,HistoryServerCellList);
        historyrecyclerView.setAdapter(historyRecycleViewAdapter);
        historyrecyclerView.setItemAnimator(new DefaultItemAnimator());

        msgcount = 0;
        InitProcessThread();

    }

    public void onClick(View view)
    {
        startActivity(new Intent("com.example.liyb.network.MapActivity"));
    }

    class CellGeneralInfo
    {
        public int type;
        public int CId;
        public int lac;
        public int tac;
        public int psc;
        public int pci;
        public int RatType= TelephonyManager.NETWORK_TYPE_UNKNOWN;
        public int signalStrength;
        public int asulevel;
    }

    class PhoneInfoThread extends  Thread
    {
        private Context context;
        public String deviceId;
        public String deviceSoftwareVersion;
        public String Imsi;
        public String Imei;
        public String line1Number;
        public String serialNumber;
        public String operaterName;
        public String operaterId;
        public int mnc;
        public int mcc;
        public int datastate;
        public int ratType= TelephonyManager.NETWORK_TYPE_UNKNOWN;
        public int cellcount;
        public int phoneDatastate;
        public String phoneModel;
        public int timecount;
        public PhoneInfoThread(Context context)
        {
            this.context = context;
            timecount = 0;
        }

        public void run()
        {
            while (true) {
                try {
                    timecount++;
                    Message message = new Message();
                    message.what = NP_CELL_INFO_UPDATE;
                    getCellInfo();
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceId", deviceId);
                    message.setData(bundle);
                    mMainHandler.sendMessage(message);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void getCellInfo()
        {
            TelephonyManager phoneManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
            operaterName = phoneManager.getNetworkOperatorName();
            operaterId = phoneManager.getNetworkOperator();
            mnc = Integer.parseInt(operaterId.substring(0, 3));
            mcc = Integer.parseInt(operaterId.substring(3));
            phoneDatastate = phoneManager.getDataState();
            deviceId = phoneManager.getDeviceId();
            Imei = phoneManager.getSimSerialNumber();
            Imsi = phoneManager.getSubscriberId();
            line1Number = phoneManager.getLine1Number();
            serialNumber = phoneManager.getSimSerialNumber();
            deviceSoftwareVersion = android.os.Build.VERSION.RELEASE;
            phoneModel = android.os.Build.MODEL;
            ratType = phoneManager.getNetworkType();
            //for lte getCellLocation can not be used.

            CellInfoList.clear();
            try
            {
                List<CellInfo> allCellinfo;
                allCellinfo = phoneManager.getAllCellInfo();
                if (allCellinfo != null)
                {
                    cellcount = allCellinfo.size();
                    for(CellInfo cellInfo:allCellinfo)
                    {
                        CellGeneralInfo newCellInfo = new CellGeneralInfo();
                        newCellInfo.type = 0;
                        if (cellInfo instanceof CellInfoGsm) {
                            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                            newCellInfo.CId = cellInfoGsm.getCellIdentity().getCid();
                            newCellInfo.signalStrength = cellInfoGsm.getCellSignalStrength().getDbm();
                            newCellInfo.asulevel = cellInfoGsm.getCellSignalStrength().getAsuLevel();
                            newCellInfo.lac = cellInfoGsm.getCellIdentity().getLac();
                            newCellInfo.RatType = TelephonyManager.NETWORK_TYPE_GSM;
                            if (cellInfoGsm.isRegistered()) {
                                newCellInfo.type = 1;
                            }
                        } else if (cellInfo instanceof CellInfoWcdma) {
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                            newCellInfo.CId = cellInfoWcdma.getCellIdentity().getCid();
                            newCellInfo.psc = cellInfoWcdma.getCellIdentity().getPsc();
                            newCellInfo.lac = cellInfoWcdma.getCellIdentity().getLac();
                            newCellInfo.signalStrength = cellInfoWcdma.getCellSignalStrength().getDbm();
                            newCellInfo.asulevel = cellInfoWcdma.getCellSignalStrength().getAsuLevel();
                            newCellInfo.RatType = TelephonyManager.NETWORK_TYPE_UMTS;
                            if (cellInfoWcdma.isRegistered()) {
                                newCellInfo.type = 1;
                            }
                        } else if (cellInfo instanceof CellInfoLte) {
                            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                            newCellInfo.CId = cellInfoLte.getCellIdentity().getCi();
                            newCellInfo.pci = cellInfoLte.getCellIdentity().getPci();
                            newCellInfo.tac = cellInfoLte.getCellIdentity().getTac();
                            newCellInfo.signalStrength = cellInfoLte.getCellSignalStrength().getDbm();
                            newCellInfo.asulevel = cellInfoLte.getCellSignalStrength().getAsuLevel();
                            newCellInfo.RatType = TelephonyManager.NETWORK_TYPE_LTE;
                            if (cellInfoLte.isRegistered()) {
                                newCellInfo.type = 1;
                            }
                        }
                        CellInfoList.add(newCellInfo);
                        if(newCellInfo.type == 1)
                        {
                            int flag = 0;
                            for (CellGeneralInfo serverCellInfo:HistoryServerCellList)
                            {
                                if ((newCellInfo.CId == serverCellInfo.CId) && (newCellInfo.RatType == serverCellInfo.RatType))
                                {
                                    flag = 1;
                                    break;
                                }
                            }
                            if(flag == 0)
                            {
                                HistoryServerCellList.add(newCellInfo);
                            }
                            //delete first one if more than 5
                            if(HistoryServerCellList.size() > 5) {
                                HistoryServerCellList.remove(0);
                            }
                        }
                    }
                }
            }
            catch(Exception e) {
                //for older devices
                GsmCellLocation location = (GsmCellLocation) phoneManager.getCellLocation();
                CellGeneralInfo newCellInfo = new CellGeneralInfo();
                newCellInfo.type = 1;
                newCellInfo.CId = location.getCid();
                newCellInfo.tac = location.getLac();
                newCellInfo.psc = location.getPsc();
            }
        }

    }
}

