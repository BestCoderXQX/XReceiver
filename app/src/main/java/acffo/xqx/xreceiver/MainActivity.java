package acffo.xqx.xreceiver;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import acffo.xqx.xlogrecordlib.XLogRecordHelper;
import acffo.xqx.xreceiver.entity.ApiMyActionList;
import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author xqx
 * @email djlxqx@163.com
 * blog:http://www.cnblogs.com/xqxacm/
 * createAt 2018/4/20
 * description: 蓝牙接收器 核心代码
 */

public class MainActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "blexqx";  // Log日志打印
    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";   // usb 权限

    private String token;                           //token
    private String url;                             //url

    // usb 默认参数设置
    public int baudRate;
    public byte stopBit;
    public byte dataBit;
    public byte parity;
    public byte flowControl;


    public byte[] writeBuffer;
    public byte[] readBuffer;
    private boolean isOpen;

    private int retval;

    private Handler handler;

    ArrayList<String> needConnectedDevice;                 //需要连接的设备mac集合
    ArrayList<Integer> needConnectedState ;                // 需要连接的设备的状态
    ArrayList<String> hasConnectedDeviceList ;     // 已经连接上的设备mac集合

    // 测试控件
    private Button btnConnect ;  // 开始连接按钮
    private Button btnReConnect ;  // 重新连接未连接成功的
    private TextView txtContent ;   // 返回数据内容
    private TextView txtContent2 ;   // 返回数据内容
    private TextView txtContent3 ;   // 返回数据内容
    private TextView txtConnectState ;   // 连接状态
    private TextView txtConnectedDevice ;   // 已经连接上的传感器
    private Button btnNeedDevice; // 获取当前需要连接的传感器
    private Button btnConnectedDevice ; // 获取当前已经连接的传感器
    private Button btnOpenDevice ; // 打开设别
    private Button btnConfigDevice ; // 配置设备

    float[][] floats = new float[7][30];   // 存放传感器数据

    XLogRecordHelper xLogRecordHelper;



    // 连接状态
    private final int CONNECTING = 1;  // 连接中
    private final int CONNECTED = 2;   // 已连接
    private final int NORMAL = 3;       // 连接异常

    boolean sensor_ready = false ;                  //传感器是否都连接成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = this.getIntent();
        token = intent.getStringExtra("token");  //token
        url = intent.getStringExtra("url");  //url
        NetConfig.url = url;
//        if (token==null || url == null){
//             不是正常的进入
//            UnityPlayer.UnitySendMessage("GetMessage", "AbnormalEntry","quit");
//            return;
//        }
        initPermission(Permission.STORAGE);
        initVariables();
        initHandler();
        initReceiver();
//        getActionListData();


        xLogRecordHelper = XLogRecordHelper.getInstance(MainActivity.this, "zzzz", "blejsq.txt");
        xLogRecordHelper.setFilterStr(TAG);
        xLogRecordHelper.start();
        Log.i(TAG , "第一条测试");

        EventBus.getDefault().register(this);

    }

    private void initPermission(String s[]) {
        AndPermission.with(this)
                .requestCode(100)
                .permission(s)
                .callback(listener)
                .start();
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。

            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if (requestCode == 200) {
                // TODO ...
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == 200) {
                // TODO ...
                Toast.makeText(MainActivity.this,"未获取权限，无法正常使用",Toast.LENGTH_SHORT).show();
                // 关闭
            }
        }
    };
    private void initVariables() {

        needConnectedDevice = new ArrayList<>();
        hasConnectedDeviceList = new ArrayList<>();
        needConnectedState = new ArrayList<>();
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnNeedDevice = (Button) findViewById(R.id.btnNeedDevice);
        btnConnectedDevice = (Button) findViewById(R.id.btnConnectedDevice);
        btnOpenDevice = (Button) findViewById(R.id.btnOpenDevice);
        btnConfigDevice = (Button) findViewById(R.id.btnConfigDevice);
        btnReConnect = (Button) findViewById(R.id.btnReConnect);
        txtConnectedDevice = (TextView) findViewById(R.id.txtConnectedDevice);
        txtContent = (TextView) findViewById(R.id.txtContent);
        txtContent2 = (TextView) findViewById(R.id.txtContent2);
        txtConnectState = (TextView) findViewById(R.id.txtConnectState);
        txtContent3 = (TextView) findViewById(R.id.txtContent3);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connentBluetooth("C1:7B:CF:54:8E:85-DA:DC:26:25:E7:0C");
//                connentBluetooth("C1:7B:CF:54:8E:85");
            }
        });
        btnNeedDevice.setOnClickListener(this);
        btnConnectedDevice.setOnClickListener(this);
        btnOpenDevice.setOnClickListener(this);
        btnConfigDevice.setOnClickListener(this);
        btnReConnect.setOnClickListener(this);

    }



    /**
     * 网络请求获取数据Json
     */
    private void getActionListData() {
        //网络请求获取数据结果
        NetConfig.Retrofit().getMyActionList(token).enqueue(new Callback<ApiMyActionList>() {
            @Override
            public void onResponse(Call<ApiMyActionList> call, Response<ApiMyActionList> response) {
                if (response.code()==200){
                    ApiMyActionList entity = response.body();
                    if (entity!=null){
                        // 有数据
                        if (entity.getCode()==200){
                            //正常数据
                            String json = new Gson().toJson(entity);
                            Log.d("magikarejson数据1", json);
                            //传unity结果
//                            UnityPlayer.UnitySendMessage("GetMessage", "GetMesJson", json);
                        }else {
                            Toast.makeText(MainActivity.this,"异常错误，错误码："+entity.getCode(),Toast.LENGTH_SHORT).show();
                            // 退出
//                            UnityPlayer.UnitySendMessage("GetMessage", "AbnormalEntry","quit");
                        }
                    }else{
                        Toast.makeText(MainActivity.this,"无返回体",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    // 系统吗不为200
                    Toast.makeText(MainActivity.this,"系统码："+response.code(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiMyActionList> call, Throwable t) {
                Log.i("xqxinfo","错误原因:"+t.getMessage());
                Toast.makeText(MainActivity.this,"连接错误",Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void initReceiver() {
        XApplication.driver = new CH34xUARTDriver(
                (UsbManager) getSystemService(Context.USB_SERVICE), this,
                ACTION_USB_PERMISSION);

        // 设置usb的默认参数
        baudRate = 115200;
        stopBit = 1;
        dataBit = 8;
        parity = 0;
        flowControl = 0;

        // 保持常亮的屏幕的状态
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        writeBuffer = new byte[512];
        readBuffer = new byte[512];
        isOpen = false;
        if (!XApplication.driver.UsbFeatureSupported())// 判断系统是否支持USB HOST
        {
            Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("提示")
                    .setMessage("您的手机不支持USB HOST，请更换其他手机再试！")
                    .setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    System.exit(0);
                                }
                            }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return; // 自己加
        }
//        openDevice();
    }

    /**
     * 配置设备参数
     */
    private void configDevice() {
        if (XApplication.driver.SetConfig(baudRate, dataBit, stopBit, parity,//配置串口波特率，函数说明可参照编程手册
                flowControl)) {
            Toast.makeText(MainActivity.this, "串口设置成功!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "串口设置失败!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开设备
     */
    private void openDevice() {
        retval = XApplication.driver.ResumeUsbList();
        if (retval == -1)// ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
        {
            Toast.makeText(MainActivity.this, "打开设备失败!",
                    Toast.LENGTH_SHORT).show();
            XApplication.driver.CloseDevice();
        } else if (retval == 0){
            if (!XApplication.driver.UartInit()) {//对串口设备进行初始化操作
                Toast.makeText(MainActivity.this, "设备初始化失败!",
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "打开" +
                                "设备失败!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(MainActivity.this, "打开设备成功!",
                    Toast.LENGTH_SHORT).show();
            isOpen = true;
            new readThread().start();//开启读线程读取串口接收的数据
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("未授权限");
            builder.setMessage("确认退出吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
//								MainFragmentActivity.this.finish();
                    System.exit(0);
                }
            });
            builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            });
            builder.show();

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnNeedDevice:
                // 获取需要连接的传感器
                Toast.makeText(this,"需要连接的传感器:"+ needConnectedDevice.toString(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnConnectedDevice:
                // 获取已经连接的传感器
                getHasConnectedDevice();
                break;
            case R.id.btnConfigDevice:
                configDevice();
                break;
            case R.id.btnOpenDevice:
                openDevice();
                break;
            case R.id.btnReConnect:
                restartUnConnectedDevice();
                break;
        }
    }

    /**
     * 获取已经连接上的设备
     */
    public void getHasConnectedDevice(){
        byte[] to_send2 = toByteArray2("AT+DECONNECT");
        int retval = XApplication.driver.WriteData(to_send2, to_send2.length);//写数据，第一个参数为需要发送的字节数组，第二个参数为需要发送的字节长度，返回实际发送的字节长度
        if (retval < 0)
            Toast.makeText(MainActivity.this, "写失败!",
                    Toast.LENGTH_SHORT).show();
    }

    /**
     * 读取数据 子线程
     */
    private class readThread extends Thread {

        public void run() {

            byte[] buffer = new byte[4096];

            while (true) {

                Message msg = Message.obtain();
                if (!isOpen) {
                    break;
                }

                int length = XApplication.driver.ReadData(buffer, buffer.length);
                if (length > 0) {
//					String recv = toHexString(buffer, length);
//					String recv = new String(buffer, 0, length);
//                    totalrecv += length;
//                    String content = String.valueOf(totalrecv);
//                    String content = new String(buffer);
//                    String content = hexStringToString(toHexString(buffer,length));
                    String content = toHexString(buffer,length);
                    msg.obj = buffer;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    private void initHandler() {
        handler = new Handler() {

            public void handleMessage(Message msg) {
                byte[] result = (byte[]) msg.obj;
                String content = toHexString(result,32);
                if (result[0]!=(byte)0xAA){
                    return;
                }
                switch (result[1]){
                    case (byte) 0xA1:
                        // 数据

                        txtContent.setText("数据:"+content);
                        if (result[10]!=(byte)0x55){
                            return;
                        }
                        break;
                    case (byte) 0xA2:
                        String ss = "";
                        ArrayList<String> hasConnectedDeviceList = new ArrayList<>();
                        // 已连接上的
                        switch (result[2]){
                            // 连接上的数量
                            case 0x00:
                                // 没有连接设备
                                txtConnectedDevice.setText("已经连接上的设备:无");
                                break;
                            case 0x01:
                                ss = getMacUpper(toHexString(subBytes(result,3,6),6));
                                hasConnectedDeviceList.add(ss);
                                txtConnectedDevice.setText("aa已经连接上的设备:"+hasConnectedDeviceList.toString());
                                break;
                            case 0x02:
                                hasConnectedDeviceList.add(getMacUpper(toHexString(subBytes(result,3,6),6)));
                                hasConnectedDeviceList.add(getMacUpper(toHexString(subBytes(result,9,6),6)));
                                txtConnectedDevice.setText("bb已经连接上的设备:"+hasConnectedDeviceList.toString());
                                break;
                            case 0x03:
                                hasConnectedDeviceList.add(getMacUpper(toHexString(subBytes(result,3,6),6)));
                                hasConnectedDeviceList.add(getMacUpper(toHexString(subBytes(result,9,6),6)));
                                hasConnectedDeviceList.add(getMacUpper(toHexString(subBytes(result,15,6),6)));
                                txtConnectedDevice.setText("cc已经连接上的设备:"+hasConnectedDeviceList.toString());
                                break;
                            default:
                                txtConnectedDevice.setText("已经连接上的设备:"+toHexString(result,32));
                                break;
                        }
                        EventBus.getDefault().post(new ConnectedDevice(hasConnectedDeviceList));
                        break;
                    case (byte) 0xA0:
                        // 状态

                        if (toHexString(subBytes(result,2,3),3).equals("43 45 44")){
                            // CED 说明连接
//                            EventBus.getDefault().post(new ConnectState(getMacUpper(toHexString(subBytes(result,6,6),6)),CONNECTED));
                            Toast.makeText(MainActivity.this,"成功连接设备:"+getMacUpper(toHexString(subBytes(result,6,6),6)),Toast.LENGTH_SHORT).show();
                            Log.i(TAG , "成功连接设备:"+getMacUpper(toHexString(subBytes(result,6,6),6)));
                            txtContent3.setText("11已经连接上的设备:"+getMacUpper(toHexString(subBytes(result,6,6),6)));
                            needConnectedState.set(needConnectedDevice.indexOf(getMacUpper(toHexString(subBytes(result,6,6),6))),CONNECTED);
                            if (needConnectedState.contains(NORMAL) || needConnectedState.contains(CONNECTING)){
                                sensor_ready = false;
                            }else{
                                sensor_ready = true;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                        for (int i = 0; i < needConnectedState.size(); i++) {
                                            if (needConnectedState.get(i) != CONNECTED){
                                                Toast.makeText(MainActivity.this,"开始连接设备:"+needConnectedDevice.get(i),Toast.LENGTH_SHORT).show();

                                                connectSingleDevice(needConnectedDevice.get(i));
                                                break;
                                            }
                                        }

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();

                                    }
                                }
                            }).start();

                        }else if (toHexString(subBytes(result,2,3),3).equals("44 49 43")){
                            // DIC 说明断开
                            Log.i(TAG , "断开连接设备:"+getMacUpper(toHexString(subBytes(result,6,6),6)));
                            Toast.makeText(MainActivity.this,"断开连接设备:"+getMacUpper(toHexString(subBytes(result,6,6),6)),Toast.LENGTH_SHORT).show();
//                            EventBus.getDefault().post(new ConnectState(getMacUpper(toHexString(subBytes(result,6,6),6)),NORMAL));
                            needConnectedState.set(needConnectedDevice.indexOf(getMacUpper(toHexString(subBytes(result,6,6),6))),NORMAL);
                            restartUnConnectedDevice();
//                            if (needConnectedDevice.contains(getMacUpper(toHexString(subBytes(result,6,6),6)))){
//                                // 需要连接的设备集合中包含该连接失败的设备 , 则重新连接
//                                sensor_ready = false ;
//                                if (!needConnectedState.contains(CONNECTING)){
//                                    // 当前没有正在连接中的
//                                    connectSingleDevice(getMacUpper(toHexString(subBytes(result,6,6),6)));
//                                }
//                            }
                        }else if (toHexString(subBytes(result,2,3),3).equals("43 49 4E")){
                            Toast.makeText(MainActivity.this,"正在连接设备:"+getMacUpper(toHexString(subBytes(result,6,6),6)),Toast.LENGTH_SHORT).show();
//                            EventBus.getDefault().post(new ConnectState(getMacUpper(toHexString(subBytes(result,6,6),6)),CONNECTING));
                            txtContent3.setText("A0:"+toHexString(subBytes(result,2,3),3)+"----"+result[2]+result[3]+result[4]);
                            needConnectedState.set(needConnectedDevice.indexOf(getMacUpper(toHexString(subBytes(result,6,6),6))),CONNECTING);
                            Log.i(TAG , "正在连接设备:"+getMacUpper(toHexString(subBytes(result,6,6),6)));
                        }else {
                            txtContent2.setText("0xA0状态:" + toHexString(result, 32));
                            Log.i(TAG , "其他状态:"+toHexString(result, 32)+"，设备:"+getMacUpper(toHexString(subBytes(result,6,6),6)));
                        }
                        txtConnectState.setText("设备连接状态:"+needConnectedState.toString());
//                        restartUnConnectedDevice();
                        break;

                }
//                switch (result[0]){
//                    case 0xaa:
//                        String content = toHexString(result,32);
//                        txtContent.setText(content);
//                        break;
//                }

//                readText.setText((String) msg.obj);
//				readText.append((String) msg.obj);
            }
        };
    }

    /**
     * 最新已经连接的设备
     * @param event
     */
    public void onEventMainThread(ConnectedDevice event){
        hasConnectedDeviceList = event.getMacs();
    }


    /**
     * 自定义连接状态接口回调
     * @param event
     */
    public void onEventMainThread(ConnectState event) {
        Calendar c = Calendar.getInstance();
        String address = event.getMac();
        int state = event.getState();
        final StringBuilder str = new StringBuilder();

        if (needConnectedDevice.indexOf(address) == -1){
            // 该设备不在需要连接的设备组中 则 过滤
            Toast.makeText(MainActivity.this,"xxxxxx:"+address,Toast.LENGTH_SHORT).show();

            return ;
        }
        switch (state){
            case CONNECTING:
                str.append("数据:"+c.get(Calendar.HOUR)+"时"+c.get(Calendar.MINUTE)+"分"+c.get(Calendar.SECOND)+"秒"+c.get(Calendar.MILLISECOND)+"毫秒 设备:"+address+"状态:正在连接"+"\n");
                Log.i("ConnectState","设备:"+address+"连接状态:"+"正在连接"+ needConnectedDevice.indexOf(address));
//                UnityPlayer.UnitySendMessage("ConnectSceneManager", "GetSenSorState", needConnectedDevice.indexOf(address) +"-1");
//                UnityPlayer.UnitySendMessage("GetMessage","MainSensorLose",address+"-1");
                needConnectedState.set(needConnectedDevice.indexOf(address),CONNECTING);
                break;
            case CONNECTED:
                Toast.makeText(MainActivity.this,"已经连接上设备:"+address,Toast.LENGTH_SHORT).show();

                Log.i("ConnectState","设备:"+address+"连接状态:"+"连接成功"+ needConnectedDevice.indexOf(address));
                str.append("数据:"+c.get(Calendar.HOUR)+"时"+c.get(Calendar.MINUTE)+"分"+c.get(Calendar.SECOND)+"秒"+c.get(Calendar.MILLISECOND)+"毫秒 设备:"+address+"状态:连接成功"+"\n");
                Log.i("xqxtag","11macArrayList:"+ needConnectedDevice +"，连接成功的mac:"+address);
//                UnityPlayer.UnitySendMessage("ConnectSceneManager", "GetSenSorState", needConnectedDevice.indexOf(address) +"-3");
//                UnityPlayer.UnitySendMessage("GetMessage","MainSensorLose",address+"-3");
                needConnectedState.set(needConnectedDevice.indexOf(address),CONNECTED);
                if (needConnectedState.contains(NORMAL) || needConnectedState.contains(CONNECTING)){
                    sensor_ready = false;
                }else{
                    sensor_ready = true;
                }
                for (int i = 0; i < needConnectedState.size(); i++) {
                    if (needConnectedState.get(i) != CONNECTED){
                        Toast.makeText(MainActivity.this,"开始连接设备:"+needConnectedDevice.get(i),Toast.LENGTH_SHORT).show();

                        connectSingleDevice(needConnectedDevice.get(i));
                        break;
                    }
                }

                break;
            case NORMAL:
//                Log.i(Tag , "设备:"+needConnectedDevice.indexOf(address)+"断开连接");
                str.append("数据:"+c.get(Calendar.HOUR)+"时"+c.get(Calendar.MINUTE)+"分"+c.get(Calendar.SECOND)+"秒"+c.get(Calendar.MILLISECOND)+"毫秒 设备:"+address+"状态:连接丢失"+"\n");
                Log.i("ConnectState","设备:"+address+"连接状态:"+"连接丢失"+ needConnectedDevice.indexOf(address));
                needConnectedState.set(needConnectedDevice.indexOf(address),NORMAL);
                if (needConnectedDevice.contains(address)){
                    // 需要连接的设备集合中包含该连接失败的设备 , 则重新连接
                    sensor_ready = false ;
                }
//                UnityPlayer.UnitySendMessage("ConnectSceneManager", "GetSenSorState", needConnectedDevice.indexOf(address) +"-2");
//                UnityPlayer.UnitySendMessage("GetMessage","MainSensorLose",address+"-2");
                break;
        }
        txtConnectState.setText("设备连接状态:"+needConnectedState.toString());
    }

    /**
     * 重新连接 需要的但是没有连接上的设备
     */
    public void restartUnConnectedDevice(){

        for (int i = 0; i < needConnectedState.size(); i++) {
            if (needConnectedState.get(i) != CONNECTED){
                Toast.makeText(MainActivity.this,"重新开始连接设备:"+needConnectedDevice.get(i),Toast.LENGTH_SHORT).show();
                connectSingleDevice(needConnectedDevice.get(i));
                break;
            }
        }
    }

    /**
     * 将小写Mac地址转换大写 并添加冒号
     * @param macLower
     * @return
     */
    public String getMacUpper(String macLower){
        String s = macLower.toUpperCase();
        s = s.replace(" ",":");
        return s;
    }
    public byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    /**
     * 将byte[]数组转化为String类型
     * @param arg
     *            需要转换的byte[]数组
     * @param length
     *            需要转换的数组长度
     * @return 转换后的String队形
     */
    private String toHexString(byte[] arg, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                if (i==length-1){
                    result = result
                            + (Integer.toHexString(
                            arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                            + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                            : arg[i])
                            : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                            : arg[i])) + "";
                }else {
                    result = result
                            + (Integer.toHexString(
                            arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                            + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                            : arg[i])
                            : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                            : arg[i])) + " ";
                }
            }
            return result;
        }
        return "";
    }

    /**
     * 连接单个设备
     * @param address
     */
    private void connectSingleDevice(String address){
        Toast.makeText(this,"连接设别:"+address,Toast.LENGTH_SHORT).show();
        byte[] to_send2 = toByteArray2("AT+CONNECT"+ address);
        int retval = XApplication.driver.WriteData(to_send2, to_send2.length);//写数据，第一个参数为需要发送的字节数组，第二个参数为需要发送的字节长度，返回实际发送的字节长度
        if (retval < 0)
            Toast.makeText(MainActivity.this, "连接异常:address",
                    Toast.LENGTH_SHORT).show();
    }

    /**
     * 连接需要连接的传感器  unity 调用
     * @param macArrayString
     */
    private void connentBluetooth(final String macArrayString) {

        if (needConnectedState.contains(NORMAL) || needConnectedState.contains(CONNECTING)){
            sensor_ready = false;
        }else{
            sensor_ready = true;
        }

        Log.i("magikareconnentBlue", "需要连接的传感器" + macArrayString);
        String macs[] = macArrayString.split("-");
        needConnectedDevice.clear();
        needConnectedState.clear();
        //添加需要连接的设备mac地址
        for (int i = 0; i < macs.length; i++) {
            needConnectedDevice.add(macs[i]);
            needConnectedState.add(NORMAL);
        }
        // 获取当前已经连接的mac设备\

        byte[] to_send2 = toByteArray2("AT+CONNECT"+ needConnectedDevice.get(0));
        Toast.makeText(this,"AT+CONNECT"+ needConnectedDevice.get(0),Toast.LENGTH_SHORT).show();
        int retval = XApplication.driver.WriteData(to_send2, to_send2.length);//写数据，第一个参数为需要发送的字节数组，第二个参数为需要发送的字节长度，返回实际发送的字节长度
        if (retval < 0)
            Toast.makeText(MainActivity.this, "连接异常:address",
                    Toast.LENGTH_SHORT).show();

    }

    /**
     * 将String转化为byte[]数组
     * @param arg
     *            需要转换的String对象
     * @return 转换后的byte[]数组
     */
    private byte[] toByteArray2(String arg) {
        if (arg != null) {
			/* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
            NewArray[length] = 0x0D;
            NewArray[length + 1] = 0x0A;
            length += 2;

            byte[] byteArray = new byte[length];
            for (int i = 0; i < length; i++) {
                byteArray[i] = (byte)NewArray[i];
            }
            return byteArray;

        }
        return new byte[] {};
    }

    public void onResume() {
        super.onResume();
        if(!XApplication.driver.isConnected()) {
            int retval = XApplication.driver.ResumeUsbPermission();
            if (retval == 0) {

            } else if (retval == -2) {
                Toast.makeText(MainActivity.this, "获取权限失败!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        isOpen = false;
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        XApplication.driver.CloseDevice();
        XApplication.driver.CloseDevice();
        xLogRecordHelper.stop();
        EventBus.getDefault().unregister(this);
    }

    //检测传感器状态
    public boolean SensorIsReady()
    {
        return sensor_ready;
    }

    /**
     * 关闭所有的需要的但没有连接上的设备
     */
    public void closeSingleDevice(){
        // 已经连接上的设备
        ArrayList<String> connectedDevice = hasConnectedDeviceList;
        // 当前需要连接的设备

        for (int i = 0; i < needConnectedDevice.size(); i++) {
            if (!connectedDevice.contains(needConnectedDevice.get(i))){
                // 已经连接上的设备中 不包含需要的这个设备
                // 停止连接该设备
                byte[] to_send2 = toByteArray2("AT+DISCONNECT"+ needConnectedDevice.get(i));
                int retval = XApplication.driver.WriteData(to_send2, to_send2.length);//写数据，第一个参数为需要发送的字节数组，第二个参数为需要发送的字节长度，返回实际发送的字节长度
                if (retval < 0)
                    Toast.makeText(MainActivity.this, "写失败!",
                            Toast.LENGTH_SHORT).show();
            }
        }
        sensor_ready = false ;
    }


}
