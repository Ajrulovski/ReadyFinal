package gazmend.com.mk.ready.rest;

import android.content.Context;


import com.jakewharton.retrofit.Ok3Client;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;


/**
 * Created by INSDare on 7/10/2014.
 */
public class ServiceClient {
    private static ServiceClient instance;
    //public static final String BASE_URL = "https://demo.tokyofarmer.com";
    //public static final String BASE_URL = "http://intelligent.noip.me:86/api";
    public static final String BASE_URL = "http://intelligent.noip.me";
    private RestAdapter mRestAdapter;
    private Map<String, Object> mClients = new HashMap<String, Object>();

    private String mBaseUrl = BASE_URL;

    private ServiceClient() {
    }

    public static ServiceClient getInstance() {
        if (null == instance) {
            instance = new ServiceClient();
        }
        return instance;
    }

    public <T> T getClient(Context context, Class<T> clazz) {
        if (mRestAdapter == null) {

/*            //Only for testing purposes for https
            DefaultHttpClient myHttpsClient;
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactory sf = new CustomSSLSocketFactory(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                // Setting up parameters
                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, "utf-8");
                params.setBooleanParameter("http.protocol.expect-continue", false);
                // Setting timeout
                HttpConnectionParams.setConnectionTimeout(params,
                        10000);
                HttpConnectionParams.setSoTimeout(params,
                        10000);
                // Registering schemes for both HTTP and HTTPS
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory
                        .getSocketFactory(), 80));
                registry.register(new Scheme("https", sf, 443));
                // Creating thread safe client connection manager
                ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                        params, registry);
                // Creating HTTP client
                myHttpsClient = new DefaultHttpClient(ccm, params);
            } catch (Exception e) {
                myHttpsClient = new DefaultHttpClient();
            }*/

            //mRestAdapter = new RestAdapter.Builder().
            //        setEndpoint(getBaseUrl(context)).
            //        //setClient(new ApacheClient(myHttpsClient)).
            //        build();

            OkHttpClient okHttpClient = new OkHttpClient();

            //okHttpClient.interceptors().add(new AddCookiesInterceptor(context,"tf.uk.com.tokyofarmer"));
            //okHttpClient.interceptors().add(new ReceivedCookiesInterceptor(context,"tf.uk.com.tokyofarmer"));

            //OkHttpClient okClient = new OkHttpClient.Builder()
            //        .addInterceptor(new AddCookiesInterceptor(context,"tf.uk.com.tokyofarmer"))
            //        .addInterceptor(new ReceivedCookiesInterceptor(context,"tf.uk.com.tokyofarmer"))
            //        .build();

            //OkHttpClient.Builder okClient = new OkHttpClient.Builder()
            //        .addInterceptor(new ReceivedCookiesInterceptor(context,"tf.uk.com.tokyofarmer"));

            mRestAdapter = new RestAdapter.Builder().
                    setEndpoint(getBaseUrl(context)).
                    setClient(new Ok3Client(okHttpClient)).
                            build();
        }
        T client = null;
        if ((client = (T) mClients.get(clazz.getCanonicalName())) != null) {
            return client;
        }
        client = mRestAdapter.create(clazz);
        mClients.put(clazz.getCanonicalName(), client);
        return client;
    }

    public void setRestAdapter(RestAdapter restAdapter) {
        mRestAdapter = restAdapter;
    }

    public String getBaseUrl(Context context) {
        // TODO: switch base url by some sort of settings logic
        return mBaseUrl;
    }
}
