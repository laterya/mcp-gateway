package cn.laterya.ai.infrastructure.gateway;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * 通用 HTTP 网关接口（Retrofit2）
 *
 * <p>面向接口的 HTTP 调用封装，支持 POST/GET。
 * 通过 @Url 动态传入请求地址，不绑定固定 baseUrl。
 */
public interface GenericHttpGateway {

    @POST
    Call<ResponseBody> post(
            @Url String url,
            @HeaderMap Map<String, Object> headers,
            @Body RequestBody body
    );

    @GET
    Call<ResponseBody> get(
            @Url String url,
            @HeaderMap Map<String, Object> headers,
            @QueryMap Map<String, Object> queryParams
    );

}
