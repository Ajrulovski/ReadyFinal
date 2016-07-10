package gazmend.com.mk.ready;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Visibility;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import gazmend.com.mk.ready.adapters.BusinessItemViewHolder;
import gazmend.com.mk.ready.models.BusinessUser;


public class BusinessListActivity extends ActionBarActivity {
    private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Intent startActivity;

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<gazmend.com.mk.ready.models.BusinessUser, BusinessItemViewHolder> mAdapter;

    String[] homeMenuItems;// = new String[] { "routes", "messages", "settings" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_list);
        setupWindowAnimations();

        // remove any items from previous orders
        SharedPreferences prefs = getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("jsonordervalues", null);
        editor.commit();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // get the extras
        Bundle extras = getIntent().getExtras();
        String extrasValue="";
        if (extras != null) {
            extrasValue = extras.getString("BUSINESS_TYPE");
            //The key argument here must match that used in the other activity
        }
        //if (savedInstanceState == null) {
            fillAdapter(extrasValue);
        //}
        //else {
        //    //mAdapter
        //}
    }

    public void onSaveInstanceState(Bundle outState) {
        //outState.putParcelable("mItems", Parcels.wrap(mMyAdapter.getItems()));
        //outState.putStringArrayList(SAVED_ADAPTER_KEYS, mMyAdapter.getKeys());


//        ArrayList<BusinessUser> mItems = new ArrayList<BusinessUser>();
//        ArrayList<String> mIds = new ArrayList<String>();
//        int itemCount = mAdapter.getItemCount();
//        //mAdapter.
//        for(int i=0; i<itemCount; i++){
//            mItems.add(mAdapter.getItem(i));
//            mIds.add(String.valueOf(mAdapter.getItemId(i)));
//        }
//        outState.putParcelableArrayList("mItems", mItems);
//        outState.putParcelableArrayList("mIds", mIds);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    private void setupWindowAnimations() {
        Visibility enterTransition = buildEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(enterTransition);
        }
    }

    private Visibility buildEnterTransition() {
        Fade enterTransition = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enterTransition = new Fade();
            enterTransition.setDuration(1000);
            // This view will not be affected by enter transition animation
            //enterTransition.excludeTarget(R.id.square_red, true);
        }
        return enterTransition;
    }

    private Visibility buildReturnTransition() {
        Visibility enterTransition = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enterTransition = new Slide();
            enterTransition.setDuration(1000);
        }
        return enterTransition;
    }

    public void fillAdapter(String type){
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = mDatabase.child("businessUsers").orderByChild("type").equalTo(type);

        mAdapter = new FirebaseRecyclerAdapter<gazmend.com.mk.ready.models.BusinessUser, BusinessItemViewHolder>(gazmend.com.mk.ready.models.BusinessUser.class, R.layout.home_business_list_row,
                BusinessItemViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(BusinessItemViewHolder viewHolder, final BusinessUser model, int position) {
                final DatabaseReference postRef = getRef(position);
                Log.v("FILL_ADAPTER","FIRED");
                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View heartView) {

                    }
                },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View cardView) {
                                //Log.v("FOUND_KEY",postRef.getKey());
                                // HERE PUT FAV LOGIC
                                Intent startActivity = new Intent(BusinessListActivity.this, MenuClientActivity.class);
                                startActivity.putExtra("CLIENT_KEY", postKey);
                                startActivity.putExtra("BUSINESS_OBJ", model);
                                startActivity(startActivity);
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View starView) {
                                // custom dialog
                                final Dialog dialog = new Dialog(BusinessListActivity.this);
                                dialog.setContentView(R.layout.rating_dialog_layout);
                                dialog.setTitle(R.string.rate_client);

                                Button dialogButton = (Button) dialog.findViewById(R.id.btnSubmit);
                                // if button is clicked, close the custom dialog
                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        RatingBar rb = (RatingBar) dialog.findViewById(R.id.ratingBar);
                                        //Toast.makeText(root.getContext(), String.valueOf(rb.getRating()), Toast.LENGTH_LONG).show();

                                        // insert into firebase
                                        String ratingkey = mDatabase.child("ratings").child(postKey).push().getKey();

                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                                        String date = df.format(new Date());

                                        HashMap<String, Object> ratinghash = new HashMap<>();
                                        ratinghash.put("rating", String.valueOf(rb.getRating()));
                                        ratinghash.put("daterated", date);

                                        Map<String, Object> bChildUpdates = new HashMap<>();
                                        bChildUpdates.put("/ratings/" + postKey + "/" + ratingkey, ratinghash);

                                        mDatabase.updateChildren(bChildUpdates);

                                        // now that you rated the business, update the businesse's rating in firebase
                                        // first calculate the new average

                                        //Query postsQuery = mDatabase.child("businessorders").child(postKey);
                                        // Get a reference to our posts

                                        // Attach a listener to read the data at our posts reference
                                        mDatabase.child("ratings").child(postKey).addValueEventListener(new ValueEventListener() {

                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                //Post post = dataSnapshot.getValue(Post.class);
                                                //System.out.println(post);
                                                //String fullrating = "";
                                                Double fullrating = 0.0;
                                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                        String[] ratingparts = child.getValue().toString().split(",");
                                                        fullrating += Double.valueOf(ratingparts[1].split("=")[1].toString().replace("}",""));
                                                }
                                                long timesRated = dataSnapshot.getChildrenCount();
                                                Double finalrating = fullrating/timesRated;
                                                String finalratingstr = String.format("%.2f", finalrating);
                                                // now finally update the rating in business user model in Firebase
                                                model.setRating(finalratingstr);
                                                Map<String, Object> businessUserValues = model.toMap();
                                                Map<String, Object> childUpdates = new HashMap<>();
                                                childUpdates.put("/businessUsers/" + postKey, businessUserValues);
                                                mDatabase.updateChildren(childUpdates);
                                            }

                                            public void onCancelled(DatabaseError databaseError) {
                                                System.out.println("The read failed: " + databaseError.getCode());
                                            }
                                        });


                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();

                            }
                        }
                );
            }

        };
        Log.v("MADAPTER",mAdapter.toString());
        mRecyclerView.setAdapter(mAdapter);

    }
}
