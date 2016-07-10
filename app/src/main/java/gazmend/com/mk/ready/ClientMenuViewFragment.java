package gazmend.com.mk.ready;

/**
 * Created by Gazmend on 6/26/2016.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
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

import gazmend.com.mk.ready.adapters.ClientMenuItemViewHolder;
import gazmend.com.mk.ready.models.BusinessUser;
import gazmend.com.mk.ready.models.ClientMenuItem;

public class ClientMenuViewFragment extends Fragment {
    private View mRootView;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<ClientMenuItem, ClientMenuItemViewHolder> mAdapter;
    public ArrayList<JSONObject> metadata = new ArrayList<JSONObject>();
    public JSONObject orderObject;
    public JSONObject orderFront;

    public static ClientMenuViewFragment newInstance() {
        return new ClientMenuViewFragment();
    }

    public ClientMenuViewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.content_menu_client, container, false);

        String extras = getActivity().getIntent().getExtras().getString("CLIENT_KEY");
        final BusinessUser uobj= getActivity().getIntent().getParcelableExtra("BUSINESS_OBJ");

        //first fill the general business info
        if (uobj != null) {
            TextView businessTitle = (TextView) mRootView.findViewById(R.id.article_title);
            businessTitle.setText(uobj.getFullname());

            TextView address = (TextView) mRootView.findViewById(R.id.article_byline);
            address.setText(uobj.getAddress());

            ImageView header = (ImageView) mRootView.findViewById(R.id.photo);
            Picasso.with(header.getContext()).load(uobj.getImagepath())
                    .placeholder(R.drawable.placeholder) //
                    .error(R.drawable.picasso_error)
                    .into(header);
        }

        // empty the previous order data
        SharedPreferences prefs = mRootView.getContext().getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("order", "");
        editor.commit();

        // now initialise the order JSONObject
        orderObject = new JSONObject();

        // then fill the recycler view for the menu items
        String loggedUsername = extras;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRootView.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        fillAdapter(loggedUsername);

        FloatingActionButton fab = (FloatingActionButton) mRootView.findViewById(R.id.share_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startActivity = new Intent(mRootView.getContext(), CheckoutActivity.class);
                startActivity.putExtra("BUSINESS_OBJ", uobj);
                startActivity(startActivity);
            }
        });

        return mRootView;
    }

    public void fillAdapter(final String userId){
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = mDatabase.child("menuItems").child(userId);
        mAdapter = new FirebaseRecyclerAdapter<ClientMenuItem, ClientMenuItemViewHolder>(ClientMenuItem.class, R.layout.item_client_menu,
                ClientMenuItemViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(ClientMenuItemViewHolder viewHolder, ClientMenuItem model, int position) {
                final DatabaseReference postRef = getRef(position);
                final String itemname = model.getItemname();
                final String itemprice = model.getPrice();
                // Set click listener for the whole post view
                final String postKey = postRef.getKey();

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        //mDatabase.child("menuItems").child(userId).child(postRef.getKey()).removeValue();
                        //Toast.makeText(mRootView.getContext(), postKey, Toast.LENGTH_LONG).show();
                        show(mRootView, postKey, itemname, itemprice);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);

    }

    public void show(View v, final String key, final String itemname, final String itemprice)
    {

        final Dialog d = new Dialog(v.getContext());
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.number_picker);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(100);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);

        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = mRootView.getContext().getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
                String currentOrder = prefs.getString("order",null);
                String currentOrderJsonString = prefs.getString("jsonordervalues",null);
                SharedPreferences.Editor editor = prefs.edit();

                // add the new item to the orderObject
                try {
                    if (currentOrderJsonString == null) {
                        orderObject = new JSONObject();
                    }
                    else {
                        orderObject = new JSONObject(currentOrderJsonString);
                    }
                    orderObject.put(key, itemname+";"+itemprice+";"+String.valueOf(np.getValue()));
                    editor.putString("jsonordervalues", orderObject.toString());
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                currentOrder = jsonToHumanReadable(orderObject);
                d.dismiss();

                Snackbar.make(mRootView, currentOrder, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        d.show();
    }

    public String jsonToHumanReadable(JSONObject jo)
    {
        Iterator<String> iter = jo.keys();
        String output = "";
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                Object value = jo.get(key);
                String parts[] = value.toString().split(";");
                output = output + parts[2] + " x " + parts[0] + ", ";
            } catch (JSONException e) {
                // Something went wrong!
                Log.v("READY_ERROR_JSON_PARSE", e.getMessage());
            }
        }

        output = output.substring(0,output.length()-2);
        return output;
    }
}
