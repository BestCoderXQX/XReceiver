package acffo.xqx.xreceiver.entity;

/**
 * Created by Administrator on 2017/8/28 0028.
 */

public class ApiResult {

    /**
     * code : 200
     * data : {}
     * msg : ok
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
    }
}
