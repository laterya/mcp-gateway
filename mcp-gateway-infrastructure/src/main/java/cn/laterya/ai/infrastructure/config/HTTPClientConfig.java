package cn.laterya.ai.infrastructure.config;

import cn.laterya.ai.infrastructure.gateway.GenericHttpGateway;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * HTTP 客户端配置
 *
 * <p>初始化 OkHttpClient（连接池、超时）和 Retrofit2（通用 HTTP 网关）。
 */
@Configuration
public class HTTPClientConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public GenericHttpGateway genericHttpGateway(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://127.0.0.1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(GenericHttpGateway.class);
    }

}
