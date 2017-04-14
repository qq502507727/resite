package com.gizwits.opensource.appkit.ControlModule;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.opensource.appkit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

@SuppressLint("HandlerLeak")
public class GosDeviceControlActivity extends GosControlModuleBaseActivity implements OnClickListener {

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
	/** 环境温度. */
	private static final String KEY_TEMPLATE = "Temperature";

	/** 环境湿度. */
	private static final String KEY_HUMIDITY = "Humidity";

	/** 环境 光照. */
	private static final String KEY_GZ = "lux";
	/** 环境含氧量. */
	private static final String KEY_O2 = "O2";
	/** 环境pm2.5. */
	private static final String KEY_PM = "PM";
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


	/** The tv template. */
	private TextView tvTemplate;

	/** The tv humidity. */
	private TextView tvHumidity;

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
	private TextView tv_gz;
	private TextView tv_o2;
	private TextView tv_pm;
	private TextView tv_she_bei;

	private LinearLayout lay_gz;
	private LinearLayout lay_o2;

	private LineChartView chart;
	private ArrayList<PointValue> values;
	private LineChartData data;
	private ArrayList<Line> lines;
	private Axis axisX;
	private Axis axisY;
	private Timer timer;
	private int position = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gos_device_control);
		initDevice();
		// 设置ActionBar
		setActionBar(true, true, title);
		initData();
		initViews();
//		initEvents();
		device.setListener(gizWifiDeviceListener);
		device.getDeviceStatus();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!isUpDateUi) {
//			try {
//				sendJson(KEY_LIGHT_COLOR, spf.getInt("COLOR", 0));
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}


	}
	private void setLineChart(int y){
		//实时添加新的点
		PointValue value1 = new PointValue(position * 5,y);
		value1.setLabel("00:00");
		values.add(value1);
		float x = value1.getX();
		//根据新的点的集合画出新的线
		Line line = new Line(values);
		line.setColor(Color.RED);
		line.setShape(ValueShape.CIRCLE);
		line.setCubic(true);//曲线是否平滑，即是曲线还是折线

		lines.clear();
		lines.add(line);
		data = initDates(lines);
		chart.setLineChartData(data);
		//根据点的横坐实时变幻坐标的视图范围
		Viewport port;
		if (x > 50) {
			port = initViewPort(x - 50, x);
		} else {
			port = initViewPort(0, 50);
		}
		chart.setCurrentViewport(port);//当前窗口

		Viewport maPort = initMaxViewPort(x);
		chart.setMaximumViewport(maPort);//最大窗口
		position++;
	}
	private LineChartData initDates(ArrayList<Line> lines) {
		LineChartData data = new LineChartData(this.lines);
		data.setAxisYLeft(axisY);
		data.setAxisXBottom(axisX);
		return data;
	}
	private Viewport initViewPort(float left, float right) {
		Viewport port = new Viewport();
		port.top = 50;
		port.bottom =-10;
		port.left = left;
		port.right = right;
		return port;
	}
	private Viewport initMaxViewPort(float right) {
		Viewport port = new Viewport();
		port.top = 50;
		port.bottom = -10;
		port.left = 0;
		port.right = right + 50;
		return port;
	}
	/**
	 * 初始化控件.
	 */
	private void initViews() {
		/**
		 * 换界面第一步
		 */
		lay_gz = (LinearLayout) findViewById(R.id.lay_gz);
		lay_o2 = (LinearLayout) findViewById(R.id.lay_o2);
		tvTemplate = (TextView) findViewById(R.id.tv_template);
		tvHumidity = (TextView) findViewById(R.id.tv_humidity);
		tv_gz = (TextView) findViewById(R.id.tv_gz);
		tv_o2 = (TextView) findViewById(R.id.tv_o2);
		tv_pm = (TextView) findViewById(R.id.tv_pm);
		tv_she_bei = (TextView) findViewById(R.id.tv_she_bei);

		chart = ((LineChartView) findViewById(R.id.chart));
		lines=new ArrayList<>();
		data= new LineChartData();
		axisX = new Axis();//x轴
		axisY = new Axis();//y轴
		axisX.setLineColor(Color.BLACK);

		data=initDates(lines);
		chart.setLineChartData(data);

		Viewport port = initViewPort(0, 50);

		chart.setCurrentViewportWithAnimation(port);
		chart.setInteractive(false);
		chart.setScrollEnabled(true);
		chart.setValueTouchEnabled(true);
		chart.setFocusableInTouchMode(true);
		chart.setViewportCalculationEnabled(false);
		chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
		chart.startDataAnimation();
		values= new ArrayList<>();




		String waitingText = (String) getText(R.string.waiting_device_ready);
		setProgressDialog(waitingText, true, false);
		progressDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (progressDialog.isShowing()) {
						GosDeviceControlActivity.this.finish();
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
	private  int countTem;
	/** The handler. */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {

			case UPDATE_UI:
				isUpDateUi = true;
				if (deviceStatu.get(KEY_TEMPLATE)!=null){
					int tem= (int) deviceStatu.get(KEY_TEMPLATE);
					tvTemplate.setText(tem+" ℃");
					setLineChart(tem);
				}else {
					tvTemplate.setVisibility(View.GONE);
				}
				if (deviceStatu.get(KEY_HUMIDITY)!=null){
					tvHumidity.setText(deviceStatu.get(KEY_HUMIDITY).toString()+" %");
				}else {
					tvHumidity.setVisibility(View.GONE);
				}
				if (deviceStatu.get(KEY_GZ)!=null){
					tv_gz.setText(deviceStatu.get(KEY_GZ).toString()+" lux");
					tv_she_bei.setText("智能大棚");
				}else {
					lay_gz.setVisibility(View.GONE);
				}
				if (deviceStatu.get(KEY_O2)!=null){
					tv_o2.setText(deviceStatu.get(KEY_O2).toString()+" %");
					tv_she_bei.setText("智能教室");
				}else {
					lay_o2.setVisibility(View.GONE);
				}
				if (deviceStatu.get(KEY_PM)!=null){
					tv_pm.setText(deviceStatu.get(KEY_PM).toString()+"  ");
				}else {
					tv_pm.setVisibility(View.GONE);
				}
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
					Toast.makeText(GosDeviceControlActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
				}
				break;
			case TOAST:
				String info = msg.obj + "";
				Toast.makeText(GosDeviceControlActivity.this, info, Toast.LENGTH_SHORT).show();
				break;
			case HARDWARE:
				showHardwareInfo((String) msg.obj);
				break;

			case DISCONNECT:
				String disconnectText = (String) getText(R.string.disconnect);
				Toast.makeText(GosDeviceControlActivity.this, disconnectText, Toast.LENGTH_SHORT).show();
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

	/**
	 * 换界面第二步
	 * @param v
     */
	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (device.isLAN()) {
			getMenuInflater().inflate(R.menu.devicecontrol_lan, menu);
		} else {
			getMenuInflater().inflate(R.menu.devicecontrol, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem menu) {
		super.onOptionsItemSelected(menu);
		switch (menu.getItemId()) {
		case android.R.id.home:
			device.setSubscribe(false);
			device.setListener(null);
			finish();
			break;
		// // 取消
		// case R.id.action_Cancle:

		// break;
		// 设置设备信息
		case R.id.action_setDeviceInfo:
			setDeviceInfo();
			break;

		// 获取设备硬件信息
		case R.id.action_getHardwareInfo:
			device.getHardwareInfo();
			break;

		// 获取设备状态
		case R.id.action_getStatu:
			String getingStatuText = (String) getText(R.string.getStatu);
			progressDialog.setMessage(getingStatuText);
			progressDialog.show();
			device.getDeviceStatus();
			break;

		default:
			break;
		}

		return true;
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

	private void setDeviceInfo() {

		final Dialog dialog = new AlertDialog.Builder(this).setView(new EditText(this)).create();
		dialog.show();

		Window window = dialog.getWindow();
		window.setContentView(R.layout.alert_gos_set_device_info);

		final EditText etAlias;
		final EditText etRemark;
		etAlias = (EditText) window.findViewById(R.id.etAlias);
		etRemark = (EditText) window.findViewById(R.id.etRemark);

		LinearLayout llNo, llSure;
		llNo = (LinearLayout) window.findViewById(R.id.llNo);
		llSure = (LinearLayout) window.findViewById(R.id.llSure);

		if (!TextUtils.isEmpty(device.getAlias())) {
			etAlias.setText(device.getAlias());
		}
		if (!TextUtils.isEmpty(device.getRemark())) {
			etRemark.setText(device.getRemark());
		}

		llNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		llSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				device.setCustomInfo(etRemark.getText().toString(), etAlias.getText().toString());
				dialog.cancel();
				String loadingText = (String) getText(R.string.loadingtext);
				progressDialog.setMessage(loadingText);
				progressDialog.show();
			}
		});
	}

	private void initData() {
		String[] colors = getResources().getStringArray(R.array.color);
		colorsList = new ArrayList<String>();
		for (String str : colors) {
			colorsList.add(str);
		}
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

}
