package gazmend.com.mk.ready;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gazmend.com.mk.ready.adapters.ClientOrderHistoryViewHolder;
import gazmend.com.mk.ready.adapters.MenuAdapter;
import gazmend.com.mk.ready.adapters.MenuItemViewHolder;
import gazmend.com.mk.ready.models.MenuItem;
import gazmend.com.mk.ready.models.OrderObject;

public class ClientOrdersHistoryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<OrderObject, ClientOrderHistoryViewHolder> mAdapter;
    private String loggedUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_orders_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the logged in user from the shared prefs
        SharedPreferences prefs = getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
        loggedUsername = prefs.getString("username",null);
        if (loggedUsername == null)
        {
            // no user found in shared prefs, redirect to login
            Intent startActivity = new Intent(ClientOrdersHistoryActivity.this, LoginActivity.class);
            startActivity(startActivity);
        }
        else {

            mDatabase = FirebaseDatabase.getInstance().getReference();

            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerOrderHistoryView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(linearLayoutManager);

            fillAdapter(loggedUsername);
        }
    }

    public void fillAdapter(final String userId){
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = mDatabase.child("clientorders").child(userId);
        mAdapter = new FirebaseRecyclerAdapter<OrderObject, ClientOrderHistoryViewHolder>(OrderObject.class, R.layout.item_order_history,
                ClientOrderHistoryViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(ClientOrderHistoryViewHolder viewHolder, final OrderObject model, int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                final String orderjson = model.getOrderjson();

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // here transfer the tapped order into shared prefs and invoke the checkout activity
                        SharedPreferences prefs = getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        // iterate through the orderjson and build the extras for the CheckoutActivity
                        String currentOrderJsonString = prefs.getString("jsonordervalues",null);
                        JSONObject orderObject = new JSONObject();
                        String[] orderitems = orderjson.split(",");
                        for(int i=0;i<orderitems.length;i++)
                        {
                            String[] orderparts = orderitems[i].split("\\:");
                            try {
                                orderObject.put(orderparts[0].replace("\"",""), orderparts[1]);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //orderObject.put(key, itemname+";"+itemprice+";"+String.valueOf(np.getValue()));

                        editor.putString("jsonordervalues", orderObject.toString());
                        editor.commit();

                        Intent startActivity = new Intent(ClientOrdersHistoryActivity.this, CheckoutActivity.class);
                        startActivity.putExtra("BUSINESS_KEY",model.getBusinesskey());
                        startActivity(startActivity);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);

    }
}
