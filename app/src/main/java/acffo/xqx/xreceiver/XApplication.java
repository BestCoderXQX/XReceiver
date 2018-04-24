package acffo.xqx.xreceiver;

import android.app.Application;

import com.umeng.commonsdk.UMConfigure;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

/**
 * @author xqx
 * @email djlxqx@163.com
 * blog:http://www.cnblogs.com/xqxacm/
 * createAt $date$
 * description:
 */
public class XApplication extends Application{
    public static CH34xUARTDriver driver;         //需要将CH34x的驱动类写在APP类下面，使得帮助类的生命周期与整个应用程序的生命周期是相同的

    // 未捕获异常的处理

    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.init(this, "5add36708f4a9d6012000084", "null", UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(true);
//        CrashHandler.getInstance().init(getApplicationContext());
    }

}
