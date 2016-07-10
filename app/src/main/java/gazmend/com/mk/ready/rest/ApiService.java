package gazmend.com.mk.ready.rest;


import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedInput;

/**
 * Created by Gazmend on 6/3/2016.
 */
public interface ApiService {
    //@POST("/mobileapi")
    //void getDocument(@Header("Authorization") String authorization, @Body TypedInput body, Callback<Response> cb);

    @GET("/notify.php")
    void notify(@Query("to_token") String token, @Query("not_type") String type, Callback<Response> cb);

    //@POST("/api/rpc")
    //void getAllDocIds(@Body TypedInput body, Callback<Response> cb);

}
