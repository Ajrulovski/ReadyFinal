package gazmend.com.mk.ready;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gazmend.com.mk.ready.adapters.CheckoutViewAdapter;
import gazmend.com.mk.ready.models.BusinessUser;
import gazmend.com.mk.ready.models.CheckoutObject;
import gazmend.com.mk.ready.models.OrderObject;
import gazmend.com.mk.ready.models.OrderObjectBusiness;
import gazmend.com.mk.ready.rest.ApiService;
import gazmend.com.mk.ready.rest.ServiceClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<CheckoutObject> mDataList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    public JSONObject orderObj;
    BusinessUser uobj;
    String businessKey;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean googleServiceAvailable =  isGooglePlayServicesAvailable(this);
        if (!googleServiceAvailable)
        {
            Toast.makeText(CheckoutActivity.this,
                    "No google services available, notifications may not work properly",
                    Toast.LENGTH_LONG).show();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String extras = this.getIntent().getExtras().getString("BUSINESS_KEY",null);
        if (extras!= null){
            businessKey = extras;
        }
        else {
            uobj = this.getIntent().getParcelableExtra("BUSINESS_OBJ");
            businessKey = uobj.getEmail().replace("@","_").replace(".","_");
        }
        // read the order data
        final SharedPreferences prefs = getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
        final String currentOrder = prefs.getString("jsonordervalues",null);
        //final String userId = mEmail.replace("@","_").replace(".","_");
        final String currentMail = prefs.getString("email",null).replace("@","_").replace(".","_");
        try {
            orderObj = new JSONObject(currentOrder);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // init the total value
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("total_order", "0");
        editor.commit();
        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CheckoutViewAdapter(orderObj,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        /////
        String currentTotal = prefs.getString("total_order",null);
        EditText totalPrice = (EditText) findViewById(R.id.itemTotalPrice);
        totalPrice.setText(currentTotal);

        final String firebasetoken = prefs.getString("firebasetoken",null);

        final EditText addInfo = (EditText) findViewById(R.id.itemAdditionalInfo);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.checkoutFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                String finalOrder = prefs.getString("jsonordervalues",null);
                registerNewOrder(currentMail,finalOrder,businessKey,addInfo.getText().toString(),firebasetoken);
            }
        });
    }

    private void registerNewOrder(final String email, final String orderjson, String businessKey, String additionalInfo, String firebasetoken) {
        // Create new order and save it in the client orders list
        String key = mDatabase.child("clientorders").child(email).push().getKey();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        String date = df.format(new Date());

        OrderObject post = new OrderObject(email, key, orderjson, date, businessKey, "0", additionalInfo);
        Map<String, Object> orderValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/clientorders/" + email+ "/" + key, orderValues);

        mDatabase.updateChildren(childUpdates);

        // save to local storage
        saveToLocalStorage(orderjson,date,businessKey);

        // Create new order and save it in the business orders list
        String bkey = mDatabase.child("businessorders").child(businessKey).push().getKey();

        OrderObjectBusiness bpost = new OrderObjectBusiness(email,key,"0",date,"",date, firebasetoken);
        Log.v("FOUND_TOKEN", firebasetoken);
        Map<String, Object> orderBusinessValues = bpost.toMap();

        Map<String, Object> bChildUpdates = new HashMap<>();
        bChildUpdates.put("/businessorders/" + businessKey+ "/" + bkey, orderBusinessValues);

        mDatabase.updateChildren(bChildUpdates);

        sendNotificationToBusiness(businessKey);
        Intent startActivity = new Intent(CheckoutActivity.this, ClientHomeActivity.class);
        startActivity(startActivity);
    }

    public void sendNotificationToBusiness(String businessKey){
        final String[] btoken = new String[1];
        // first get the token for the business
        // so first check if there is such auser in the db
        mDatabase.child("businessUsers").child(businessKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value

                        BusinessUser bu = dataSnapshot.getValue(BusinessUser.class);
                        if (bu == null) {
                            Log.v("NO_ORDERS_FOUND", "NO_ORDERS_FOUND");
                            //Toast.makeText(, "No user found.", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            String t = bu.getFirebasetoken();
                            Log.v("TOKEN_FOUND", t);
                            // here notify the business

                            ApiService service = ServiceClient.getInstance().getClient(getApplicationContext(), ApiService.class);

                            Log.v("FINAL_TOKEN",t);
                            service.notify(t,"3",new Callback<Response>() {
                                @Override
                                public void success(Response cb, Response response) {

                                    //Try to get response body
                                    BufferedReader reader = null;
                                    StringBuilder sb = new StringBuilder();
                                    try {
                                        reader = new BufferedReader(new InputStreamReader(cb.getBody().in()));
                                        String line;

                                        try {
                                            while ((line = reader.readLine()) != null) {
                                                sb.append(line);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                    String result = sb.toString();
                                    Log.v("RESULT", result);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.v("LOGIN_RESULT_ERROR", error.getMessage());
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("CANCELED_ORDER", "getOrder:onCancelled", databaseError.toException());
                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean googleServiceAvailable =  isGooglePlayServicesAvailable(this);
        if (!googleServiceAvailable)
        {
            Toast.makeText(CheckoutActivity.this,
                    "No google services available, notifications may not work properly",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void saveToLocalStorage(String ordertext, String orderdate, String businessKey)
    {
        // Add a new order record
        ContentValues values = new ContentValues();

        values.put(OrdersProvider.ORDERNAME,ordertext);
        values.put(OrdersProvider.ORDERDATE, orderdate);
        values.put(OrdersProvider.BUSNAME, businessKey);

        Uri uri = getContentResolver().insert(OrdersProvider.CONTENT_URI, values);

        Intent intent = new Intent(this, ReadyWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ReadyWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
}
