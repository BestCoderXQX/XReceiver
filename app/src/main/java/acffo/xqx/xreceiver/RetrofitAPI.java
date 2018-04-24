package acffo.xqx.xreceiver;


import acffo.xqx.xreceiver.entity.ApiMyActionList;
import acffo.xqx.xreceiver.entity.ApiResult;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author xqx
 * @email djlxqx@163.com
 * blog:http://www.cnblogs.com/xqxacm/
 * createAt 2017/8/15
 * description:  网络请求  接口API
 */

public interface RetrofitAPI {

    /**
     *  获取动作集合
     * @param access_token
     * @return
     */
    @GET("patient/rehab_move/")
    Call<ApiMyActionList> getMyActionList(@Header("Authorization") String access_token);


    /**
     * 上传动作信息
     * @param access_token
     * @param type
     * @param josn
     * @return
     */
    @POST("patient/rehab_move_feedback/")
    Call<ApiResult> postMyActionList(@Header("Authorization") String access_token, @Header("Content-Type") String type, @Body RequestBody josn);

}
