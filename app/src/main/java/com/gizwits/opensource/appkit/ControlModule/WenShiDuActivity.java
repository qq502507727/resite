package com.gizwits.opensource.appkit.ControlModule;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;
import com.gizwits.opensource.appkit.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
@SuppressLint("HandlerLeak")
public class WenShiDuActivity extends GosBaseActivity implements View.OnClickListener {
/**
 * 1.实例化数据 打标签
 */

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

        /** The Disconnect */
        protected static final int DISCONNECT = 6;

	/*
	 * ===========================================================
	 * 以下key值对应http://site.gizwits.com/v2/datapoint?product_key={productKey}
	 * 中显示的数据点名称，sdk通过该名称作为json的key值来收发指令，demo中使用的key都是对应机智云实验室的微信宠物屋项目所用数据点
	 * ===========================================================
	 */
//        /** led红灯开关 0=关 1=开. */
//        private static final String KEY_RED_SWITCH = "LED_OnOff";
//
//        /** 指定led颜色值 0=自定义 1=黄色 2=紫色 3=粉色. */
//        private static final String KEY_LIGHT_COLOR = "LED_Color";
//
//        /** led灯红色值 0-254. */
//        private static final String KEY_LIGHT_RED = "LED_R";
//
//        /** led灯绿色值 0-254. */
//        private static final String KEY_LIGHT_GREEN = "LED_G";
//
//        /** led灯蓝色值 0-254. */
//        private static final String KEY_LIGHT_BLUE = "LED_B";
//
//        /** 电机转速 －5～－1 电机负转 0 停止 1～5 电机正转. */
//        private static final String KEY_SPEED = "Motor_Speed";
//
//        /** 红外探测 0无障碍 1有障碍. */
//        private static final String KEY_INFRARED = "Infrared";

        /** 环境温度. */
        private static final String KEY_TEMPLATE = "Temperature";

        /** 环境湿度. */
        private static final String KEY_HUMIDITY = "Humidity";
	/*
	 * ===========================================================
	 * 以下key值对应设备硬件信息各明细的名称，用与回调中提取硬件信息字段。
	 * ===========================================================
	 */

        /** The wifiHardVerKey */
        private static final String wifiHardVerKey = "wifiHardVersion";

        /** The wifiSoftVerKey */
        private static final String wifiSoftVerKey = "wifiSoftVersion";

        /** The mcuHardVerKey */
        private static final String mcuHardVerKey = "mcuHardVersion";

        /** The mcuSoftVerKey */
        private static final String mcuSoftVerKey = "mcuSoftVersion";

        /** The wifiFirmwareIdKey */
        private static final String FirmwareIdKey = "wifiFirmwareId";

        /** The wifiFirmwareVerKey */
        private static final String FirmwareVerKey = "wifiFirmwareVer";

        /** The productKey */
        private static final String productKey = "productKey";

        /** The sw red. */
        private Switch swRed;

        /** The sw infrared. */
        private Switch swInfrared;

        /** The sp color. */
        // private Spinner spColor;

        /** The ll color */
        private LinearLayout llColor;

        /** The tv red. */
        private TextView tvRed;

        /** The tv green. */
        private TextView tvGreen;

        /** The tv blue. */
        private TextView tvBlue;

        /** The tv speed. */
        private TextView tvSpeed;

        /** The tv template. */
        private TextView tvTemplate;

        /** The tv humidity. */
        private TextView tvHumidity;

        /** The tv ColorText */
        private TextView tvColorText;

        /** The sb red. */
        private SeekBar sbRed;

        /** The sb green. */
        private SeekBar sbGreen;

        /** The sb blue. */
        private SeekBar sbBlue;

        /** The sb speed. */
        private SeekBar sbSpeed;

        ImageView redsub, redadd, greensub, greenadd, bluesub, blueadd, speedsub, speedadd;

        /** The GizWifiDevice device */
        private GizWifiDevice device;

        /** The device statu. */
        private HashMap<String, Object> deviceStatu;

        /** The First */
        // private boolean isFirst = true;

        /** The isUpDateUi */
        protected static boolean isUpDateUi = true;

        /** The Title */
        private String title;

        /** The colors list */
        ArrayList<String> colorsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wen_shi_du);
        initDevice();
        // 设置ActionBar
        setActionBar(true, true, title);
        initData();
        initViews();
        initEvents();
        /**
         * 重要点  ----------------------------------------------------------
         */

//        device.setListener(gizWifiDeviceListener);
        device.getDeviceStatus();
    }


//

    @Override
        protected void onResume() {
            super.onResume();

//            if (!isUpDateUi) {
//                try {
//                    sendJson(KEY_LIGHT_COLOR, spf.getInt("COLOR", 0));
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }

        }

        /**
         * 初始化控件.
         */
        private void initViews() {

            // spColor = (Spinner) findViewById(R.id.sp_color);
//            swRed = (Switch) findViewById(R.id.sw_red);
//
//            tvRed = (TextView) findViewById(R.id.tv_red);
//            tvGreen = (TextView) findViewById(R.id.tv_green);
//            tvBlue = (TextView) findViewById(R.id.tv_blue);
//
//            tvTemplate = (TextView) findViewById(R.id.tv_template);
//            tvHumidity = (TextView) findViewById(R.id.tv_humidity);
//            sbRed = (SeekBar) findViewById(R.id.sb_red);
//
//            sbSpeed = (SeekBar) findViewById(R.id.sb_speed);
//
//            //
//            redadd = (ImageView) findViewById(R.id.redadd);
//            redsub = (ImageView) findViewById(R.id.redsub);
//            greenadd = (ImageView) findViewById(R.id.greenadd);
//            greensub = (ImageView) findViewById(R.id.greensub);
//
//            speedadd = (ImageView) findViewById(R.id.speedadd);
//            speedsub = (ImageView) findViewById(R.id.speedsub);

            String waitingText = (String) getText(R.string.waiting_device_ready);
            setProgressDialog(waitingText, true, false);
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (progressDialog.isShowing()) {
                            WenShiDuActivity.this.finish();
                            device.setSubscribe(false);
                            device.setListener(null);
                            return true;
                        }
                    }

                    return false;
                }
            });
            progressDialog.show();
        }

        /**
         * 初始化监听器.
         */
        private void initEvents() {
//            redadd.setOnClickListener(this);
//            redsub.setOnClickListener(this);
//            greenadd.setOnClickListener(this);
//            greensub.setOnClickListener(this);
//            blueadd.setOnClickListener(this);
//            bluesub.setOnClickListener(this);
//            speedadd.setOnClickListener(this);
//            speedsub.setOnClickListener(this);
            //
//            swRed.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    try {
//                        sendJson(KEY_RED_SWITCH, swRed.isChecked());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

            // spColor.setOnItemSelectedListener(new OnItemSelectedListener() {
            //
            // @Override
            // public void onItemSelected(AdapterView<?> parent, View view, int
            // position, long id) {
            //
            // Log.i("Apptest", isUpDateUi + "1");
            //
            // if (isFirst) {
            // isFirst = false;
            // return;
            // }
            //
            // isUpDateUi = false;
            //
            // if (isUpDateUi) {
            // return;
            // }
            //
            // try {
            // Log.i("Apptest", isUpDateUi + "2");
            // sendJson(KEY_LIGHT_COLOR, position);
            // } catch (JSONException e) {
            // e.printStackTrace();
            // }
            // }
            //
            // @Override
            // public void onNothingSelected(AdapterView<?> parent) {
            //
            // }
            // });

//            sbRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    try {
//                        sendJson(KEY_LIGHT_RED, seekBar.getProgress());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    tvRed.setText(progress + "");
//
//                }
//            });
//            sbBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    try {
//                        sendJson(KEY_LIGHT_BLUE, seekBar.getProgress());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    tvBlue.setText(progress + "");
//
//                }
//            });
//            sbGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    try {
//                        sendJson(KEY_LIGHT_GREEN, seekBar.getProgress());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    tvGreen.setText(progress + "");
//
//                }
//            });
//            sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    try {
//                        sendJson(KEY_SPEED, seekBar.getProgress() - 5);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                }
//
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    tvSpeed.setText((progress - 5) + "");
//                }
//            });

        }

        private void initDevice() {
            Intent intent = getIntent();
            device = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
            deviceStatu = new HashMap<String, Object>();

            if (TextUtils.isEmpty(device.getAlias())) {
                title = device.getProductName();
            } else {
                title = device.getAlias();
            }
        }

        /**
         * 发送指令。格式为json。
         * <p>
         * 例如 {"entity0":{"attr2":74},"cmd":1}
         * 其中entity0为gokit所代表的实体key，attr2代表led灯红色值，cmd为1时代表写入
         * 。以上命令代表改变gokit的led灯红色值为74.
         *
         * @param key
         *            数据点对应的的json的key
         * @param value
         *            需要改变的值
         * @throws JSONException
         *             the JSON exception
         */
        private void sendJson(String key, Object value) throws JSONException {
            ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
            hashMap.put(key, value);
            device.write(hashMap, 0);
            Log.i("Apptest", hashMap.toString());
        }

        /** The handler. */
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {

                    case UPDATE_UI:
                        isUpDateUi = true;
//                        tvColorText.setText(colorsList.get(Integer.parseInt(deviceStatu.get(KEY_LIGHT_COLOR).toString())));
//                        spf.edit().putInt("COLOR", Integer.parseInt(deviceStatu.get(KEY_LIGHT_COLOR).toString())).commit();
                        // spColor.setSelection(Integer.parseInt(deviceStatu.get(KEY_LIGHT_COLOR).toString()));
                        //
//                        swRed.setChecked((Boolean) deviceStatu.get(KEY_RED_SWITCH));
//                        swInfrared.setChecked((Boolean) deviceStatu.get(KEY_INFRARED));
//                        tvBlue.setText(deviceStatu.get(KEY_LIGHT_BLUE).toString());
//                        tvGreen.setText(deviceStatu.get(KEY_LIGHT_GREEN).toString());
//                        tvRed.setText(deviceStatu.get(KEY_LIGHT_RED).toString());
//                        tvSpeed.setText(deviceStatu.get(KEY_SPEED).toString());
                        tvTemplate.setText(deviceStatu.get(KEY_TEMPLATE).toString());
                        tvHumidity.setText(deviceStatu.get(KEY_HUMIDITY).toString());
//                        if (deviceStatu.get(KEY_LIGHT_BLUE).toString() != null) {
//                            sbBlue.setProgress(Integer.parseInt(deviceStatu.get(KEY_LIGHT_BLUE).toString()));
//                        } else {
//                            sbBlue.setProgress(0);
//                        }
//
//                        if (deviceStatu.get(KEY_LIGHT_GREEN).toString() != null) {
//                            sbGreen.setProgress(Integer.parseInt(deviceStatu.get(KEY_LIGHT_GREEN).toString()));
//                        } else {
//                            sbBlue.setProgress(0);
//                        }
//
//                        if (deviceStatu.get(KEY_LIGHT_RED).toString() != null) {
//                            sbRed.setProgress(Integer.parseInt(deviceStatu.get(KEY_LIGHT_RED).toString()));
//                        } else {
//                            sbBlue.setProgress(0);
//                        }
//
//                        if (deviceStatu.get(KEY_SPEED).toString() != null) {
//                            sbSpeed.setProgress(5 + Integer.parseInt(deviceStatu.get(KEY_SPEED).toString()));
//                        } else {
//                            sbSpeed.setProgress(5);
//                        }
//                        break;
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
                            Toast.makeText(WenShiDuActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case TOAST:
                        String info = msg.obj + "";
                        Toast.makeText(WenShiDuActivity.this, info, Toast.LENGTH_SHORT).show();
                        break;
                    case HARDWARE:
                        showHardwareInfo((String) msg.obj);
                        break;

                    case DISCONNECT:
                        String disconnectText = (String) getText(R.string.disconnect);
                        Toast.makeText(WenShiDuActivity.this, disconnectText, Toast.LENGTH_SHORT).show();
                        device.setSubscribe(false);
                        device.setListener(null);
                        finish();
                        break;
                }

            }
        };

        /**
         * Show data in ui.
         *
         * @param data
         *            the data
         * @throws JSONException
         *             the JSON exception
         */
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

        /**
         * 展示设备硬件信息
         *
         * @param hardwareInfo
         */
        private void showHardwareInfo(String hardwareInfo) {
            String hardwareInfoTitle = (String) getText(R.string.hardwareInfo);
            new AlertDialog.Builder(this).setTitle(hardwareInfoTitle).setMessage(hardwareInfo)
                    .setPositiveButton(R.string.besure, null).show();
        }

        protected void didReceiveData(GizWifiErrorCode result, GizWifiDevice arg1,
                                      ConcurrentHashMap<String, Object> dataMap, int arg3) {
            if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS || dataMap.isEmpty()) {
                return;
            }
            progressDialog.cancel();

            if (dataMap.get("data") != null) {
                Log.i("Apptest", dataMap.get("data").toString());
                Message msg = new Message();
                msg.obj = dataMap.get("data");
                msg.what = RESP;
                handler.sendMessage(msg);
            }

            if (dataMap.get("alerts") != null) {
                Message msg = new Message();
                msg.obj = dataMap.get("alerts");
                msg.what = LOG;
                handler.sendMessage(msg);
            }

            if (dataMap.get("faults") != null) {
                Message msg = new Message();
                msg.obj = dataMap.get("faults");
                msg.what = LOG;
                handler.sendMessage(msg);
            }

            if (dataMap.get("binary") != null) {
                Log.i("info", "Binary data:" + bytesToHex((byte[]) dataMap.get("binary")));
            }
        }

        protected void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice arg1,
                                          ConcurrentHashMap<String, String> hardwareInfo) {
            Log.i("Apptest", hardwareInfo.toString());
            StringBuffer sb = new StringBuffer();
            if (GizWifiErrorCode.GIZ_SDK_SUCCESS == result) {
                sb.append("Wifi Hardware Version:" + hardwareInfo.get(wifiHardVerKey) + "\r\n");
                sb.append("Wifi Software Version:" + hardwareInfo.get(wifiSoftVerKey) + "\r\n");
                sb.append("MCU Hardware Version:" + hardwareInfo.get(mcuHardVerKey) + "\r\n");
                sb.append("MCU Software Version:" + hardwareInfo.get(mcuSoftVerKey) + "\r\n");
                sb.append("Wifi Firmware Id:" + hardwareInfo.get(FirmwareIdKey) + "\r\n");
                sb.append("Wifi Firmware Version:" + hardwareInfo.get(FirmwareVerKey) + "\r\n");
                sb.append("Product Key:" + "\r\n" + hardwareInfo.get(productKey) + "\r\n");

                // 设备属性
                sb.append("Device ID:" + "\r\n" + device.getDid() + "\r\n");
                sb.append("Device IP:" + device.getIPAddress() + "\r\n");
                sb.append("Device MAC:" + device.getMacAddress() + "\r\n");

            }
            Message msg = new Message();
            msg.what = HARDWARE;
            msg.obj = sb.toString();
            handler.sendMessage(msg);
        }

        protected void didSetCustomInfo(GizWifiErrorCode arg0, GizWifiDevice arg1) {
            progressDialog.cancel();
            Message msg = new Message();
            msg.what = TOAST;
            String toastText;
            if (GizWifiErrorCode.GIZ_SDK_SUCCESS == arg0) {
                toastText = (String) getText(R.string.set_info_successful);
            } else {
                toastText = (String) getText(R.string.set_info_failed) + "\n" + arg0;
            }
            msg.obj = toastText;
            handler.sendMessage(msg);
        }

        protected void didUpdateNetStatus(GizWifiDevice arg0, GizWifiDeviceNetStatus arg1) {
            if (device == arg0) {
                if (GizWifiDeviceNetStatus.GizDeviceUnavailable == arg1
                        || GizWifiDeviceNetStatus.GizDeviceOffline == arg1) {
                    handler.sendEmptyMessage(DISCONNECT);
                }
            }
        }

//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.redadd:
//                    int redNum1 = sbRed.getProgress();
//                    if (redNum1 < 254) {
//                        redNum1++;
//                        try {
//                            sendJson(KEY_LIGHT_RED, redNum1);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//                case R.id.redsub:
//                    int redNum2 = sbRed.getProgress();
//                    if (redNum2 > 0) {
//                        redNum2--;
//                        try {
//                            sendJson(KEY_LIGHT_RED, redNum2);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//                case R.id.greenadd:
//                    int greenNum1 = sbGreen.getProgress();
//                    if (greenNum1 < 254) {
//                        greenNum1++;
//                        try {
//                            sendJson(KEY_LIGHT_GREEN, greenNum1);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//                case R.id.greensub:
//                    int greenNum2 = sbGreen.getProgress();
//                    if (greenNum2 > 0) {
//                        greenNum2--;
//                        try {
//                            sendJson(KEY_LIGHT_GREEN, greenNum2);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//
//                case R.id.speedadd:
//                    int speedNum1 = sbSpeed.getProgress();
//                    if (speedNum1 < 10) {
//                        speedNum1++;
//                        try {
//                            sendJson(KEY_SPEED, speedNum1 - 5);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//                case R.id.speedsub:
//                    int speedNum2 = sbSpeed.getProgress();
//                    if (speedNum2 > 0) {
//                        speedNum2--;
//                        try {
//                            sendJson(KEY_SPEED, speedNum2 - 5);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }

//        @Override
//        public boolean onCreateOptionsMenu(Menu menu) {
//            if (device.isLAN()) {
//                getMenuInflater().inflate(R.menu.devicecontrol_lan, menu);
//            } else {
//                getMenuInflater().inflate(R.menu.devicecontrol, menu);
//            }
//            return super.onCreateOptionsMenu(menu);
//        }

//        public boolean onOptionsItemSelected(MenuItem menu) {
//            super.onOptionsItemSelected(menu);
//            switch (menu.getItemId()) {
//                case android.R.id.home:
//                    device.setSubscribe(false);
//                    device.setListener(null);
//                    finish();
//                    break;
//                // // 取消
//                // case R.id.action_Cancle:
//
//                // break;
//                // 设置设备信息
//                case R.id.action_setDeviceInfo:
//                    setDeviceInfo();
//                    break;
//
//                // 获取设备硬件信息
//                case R.id.action_getHardwareInfo:
//                    device.getHardwareInfo();
//                    break;
//
//                // 获取设备状态
//                case R.id.action_getStatu:
//                    String getingStatuText = (String) getText(R.string.getStatu);
//                    progressDialog.setMessage(getingStatuText);
//                    progressDialog.show();
//                    device.getDeviceStatus();
//                    break;
//
//                default:
//                    break;
//            }
//
//            return true;
//        }

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

//        private void setDeviceInfo() {
//
//            final Dialog dialog = new AlertDialog.Builder(this).setView(new EditText(this)).create();
//            dialog.show();
//
//            Window window = dialog.getWindow();
//            window.setContentView(R.layout.alert_gos_set_device_info);

//            final EditText etAlias;
//            final EditText etRemark;
//            etAlias = (EditText) window.findViewById(R.id.etAlias);
//            etRemark = (EditText) window.findViewById(R.id.etRemark);

//            LinearLayout llNo, llSure;
//            llNo = (LinearLayout) window.findViewById(R.id.llNo);
//            llSure = (LinearLayout) window.findViewById(R.id.llSure);

//            if (!TextUtils.isEmpty(device.getAlias())) {
//                etAlias.setText(device.getAlias());
//            }
//            if (!TextUtils.isEmpty(device.getRemark())) {
//                etRemark.setText(device.getRemark());
//            }
//
//            llNo.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    dialog.cancel();
//                }
//            });
//
//            llSure.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    device.setCustomInfo(etRemark.getText().toString(), etAlias.getText().toString());
//                    dialog.cancel();
//                    String loadingText = (String) getText(R.string.loadingtext);
//                    progressDialog.setMessage(loadingText);
//                    progressDialog.show();
//                }
//            });
//        }

        private void initData() {
//            String[] colors = getResources().getStringArray(R.array.color);
//            colorsList = new ArrayList<String>();
//            for (String str : colors) {
//                colorsList.add(str);
//            }
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                device.setSubscribe(false);
                device.setListener(null);
                finish();
            }
            return false;
        }

    @Override
    public void onClick(View v) {

    }
}


