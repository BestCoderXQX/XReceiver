package acffo.xqx.xreceiver;

/**
 * @author xqx
 * @email djlxqx@163.com
 * blog:http://www.cnblogs.com/xqxacm/
 * createAt $date$
 * description:
 */
public class ConnectState {
    // 连接状态
    private int CONNECTING = 1;  // 连接中
    private int CONNECTED = 2;   // 已连接
    private int NORMAL = 3;       // 连接异常
    private int state;           // 连接状态
    private String mac ; // 设备

    public ConnectState(String mac, int state) {
        this.mac = mac;
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
