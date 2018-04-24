package acffo.xqx.xreceiver;

import java.util.ArrayList;

/**
 * @author xqx
 * @email djlxqx@163.com
 * blog:http://www.cnblogs.com/xqxacm/
 * createAt $date$
 * description: 已经连接上的设备
 */
public class ConnectedDevice {
    private ArrayList<String> macs;

    public ConnectedDevice(ArrayList<String> macs) {
        this.macs = macs;
    }

    public ArrayList<String> getMacs() {
        return macs;
    }

    public void setMacs(ArrayList<String> macs) {
        this.macs = macs;
    }
}
