package com.gizwits.opensource.appkit.ControlModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.opensource.appkit.DeviceModule.GosDeviceModuleBaseActivity;
import com.gizwits.opensource.appkit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WenDuActivity extends GosDeviceModuleBaseActivity {

    private static final String TEM = "Temperature";
    private static final String HUM = "Humidity";
    private TextView tv_hd;
    private TextView tv_sd;
    private GizWifiDevice device;
    /** The device statu. */
    private HashMap<String, Object> deviceStatu;
    /** The Constant TOAST. */
    protected static final int TOAST = 0;

    /** The Constant SETNULL. */
    protected static final int SETNULL = 1;

    /** The Constant UPDATE_UI. */
    protected static final int UPDATE_UI = 2;

    /** The Constant LOG. */
    protected static final int LOG = 3;

    /** The Constant RESP. */
    protected static final int RESP = 4;

    /** The Constant HARDWARE. */
    protected static final int HARDWARE = 5;
    /**
     * 判断是否更新 配合更新界面时使用
     */
    protected static boolean isUpDateUi = true;
    /** The Disconnect */
    protected static final int DISCONNECT = 6;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wen_du);
        initDevice();
        initView();
        initData();



    }

    private void initData() {
        List<String> attrs=new ArrayList<>();
        device.getDeviceStatus(attrs);
        System.out.println(attrs.toArray().toString());
    }

    private void initView() {

        tv_hd = ((TextView) findViewById(R.id.tv_hd));
        tv_sd = ((TextView) findViewById(R.id.tv_sd));

    }
    private void initDevice() {
        Intent intent = getIntent();
        device = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
        Log.i("Apptest", device.getDid());
    }

    /**
     * 发送指令 json 调用write方法吧json发送给设备 设备接受到以后再吧设备状态发送给SDK
     * SDK吧收到的信息 再次封装为JSON回调给使用者
     * @param key
     * @param value
     * @throws JSONException
     */
    private void sendJson(String key, Object value) throws JSONException {
        ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
        hashMap.put(key, value);
        device.write(hashMap, 0);
        Log.i("Apptest", hashMap.toString());
    }
    protected void didReceiveData(GizWifiErrorCode result, GizWifiDevice arg1,
                                  ConcurrentHashMap<String, Object> dataMap, int arg3) {
        System.out.println(dataMap.toString());

        if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS || dataMap.isEmpty()) {
            return;
        }
        progressDialog.cancel();
        //设备信息
        if (dataMap.get("data") != null) {
            Log.i("Apptest", dataMap.get("data").toString());
            Message msg = new Message();
            msg.obj = dataMap.get("data");
            msg.what = RESP;
            handler.sendMessage(msg);
        }
        //警告 高温等等
        if (dataMap.get("alerts") != null) {
            Message msg = new Message();
            msg.obj = dataMap.get("alerts");
            msg.what = LOG;
            handler.sendMessage(msg);
        }
        //故障信息
        if (dataMap.get("faults") != null) {
            Message msg = new Message();
            msg.obj = dataMap.get("faults");
            msg.what = LOG;
            handler.sendMessage(msg);
        }
        //二进制数据方式
        if (dataMap.get("binary") != null) {
            Log.i("info", "Binary data:" + bytesToHex((byte[]) dataMap.get("binary")));
        }
    }

    /** The handler. */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                //更新UI界面
                case UPDATE_UI:
                    tv_hd.setText(deviceStatu.get(TEM).toString());
                    tv_sd.setText(deviceStatu.get(HUM).toString());
                    break;
                case RESP:
                    String data = msg.obj.toString();

                    try {
                        showDataInUI(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case LOG:
                    StringBuilder sb = new StringBuilder();
                    JSONObject jsonObject;
                    int logText = 1;
                    try {
                        jsonObject = new JSONObject(msg.obj.toString());
                        for (int i = 0; i < jsonObject.length(); i++) {
                            if (jsonObject.getBoolean(jsonObject.names().getString(i)) != false) {
                                sb.append(jsonObject.names().getString(i) + " " + logText + "\r\n");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (sb.length() != 0) {
                        Toast.makeText(WenDuActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case TOAST:
                    String info = msg.obj + "";
                    Toast.makeText(WenDuActivity.this, info, Toast.LENGTH_SHORT).show();
                    break;
                case HARDWARE:
//                    showHardwareInfo((String) msg.obj);
                    break;

                case DISCONNECT:
                    String disconnectText = (String) getText(R.string.disconnect);
                    Toast.makeText(WenDuActivity.this, disconnectText, Toast.LENGTH_SHORT).show();
                    device.setSubscribe(false);
                    device.setListener(null);
                    finish();
                    break;
            }

        }
    };
    @SuppressWarnings("rawtypes")
    private void showDataInUI(String data) throws JSONException {
        Log.i("revjson", data);
        JSONObject receive = new JSONObject(data);
        Iterator actions = receive.keys();
        while (actions.hasNext()) {
            String param = actions.next().toString();
            Object value = receive.get(param);
            deviceStatu.put(param, value);
        }
        Message msg = new Message();
        msg.obj = data;
        msg.what = UPDATE_UI;
        handler.sendMessage(msg);
    }
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    public void btn(View view) {
        Object value= 1;
    }


}
