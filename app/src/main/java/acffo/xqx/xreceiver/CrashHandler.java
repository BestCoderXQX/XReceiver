package acffo.xqx.xreceiver;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by 徐启鑫 on 2016/12/8.
 * 未捕获异常的处理类
 * http://www.cnblogs.com/xqxacm/p/6143836.html
 *
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler{

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    Context context;
    Object2FileUtils object2FileUtils;

    //CrashHandler实例
    private static CrashHandler instance;

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }

        return instance;
    }
    /**
     * 初始化
     */
    public void init(Context context) {
        this.context = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        object2FileUtils = new Object2FileUtils(context,"");
    }
    //实现uncaughException方法
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!solveException(ex) && mDefaultHandler != null){
            //如果用户没有处理则让系统默认的异常处理器处理
            mDefaultHandler.uncaughtException(thread, ex);
        }else{
            // 等待2秒钟后关闭程序
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /*
    *  错误处理
    * */
    private boolean solveException(final Throwable e){
        if (e == null){
            return false;
        }
        object2FileUtils.writeObjectToFile(e);

        new Thread() {
            @Override
            public void run() {
                object2FileUtils.writeObjectToFile(e);
                Looper.prepare();
                Toast.makeText(context,"程序出现异常，2秒后退出",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        };
        return true;
    }

}