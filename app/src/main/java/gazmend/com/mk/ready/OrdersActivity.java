package gazmend.com.mk.ready;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Rating;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import gazmend.com.mk.ready.adapters.BusinessOrderViewHolder;
import gazmend.com.mk.ready.models.OrderObjectBusiness;
import gazmend.com.mk.ready.rest.ApiService;
import gazmend.com.mk.ready.rest.ServiceClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrdersActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    private static DatabaseReference mDatabase;
    private static FirebaseRecyclerAdapter<OrderObjectBusiness, BusinessOrderViewHolder> mAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    public ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        boolean googleServiceAvailable =  isGooglePlayServicesAvailable(this);
        if (!googleServiceAvailable)
        {
            Toast.makeText(OrdersActivity.this,
                    "No google services available, notifications may not work properly",
                    Toast.LENGTH_LONG).show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // handle logout
        ImageButton logoutImage = (ImageButton) findViewById(R.id.logout);
        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", "");
                editor.putString("email", "");
                editor.commit();

                Intent startActivity = new Intent(OrdersActivity.this, LoginActivity.class);
                startActivity(startActivity);
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SharedPreferences prefs = getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
        final String currentMail = prefs.getString("email", null).replace("@","_").replace(".","_");

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                // Check if this is the page you want.

                Log.v("FOUND_VALUE",String.valueOf(position));
                //fillAdapter(currentMail,String.valueOf(position));
                //mAdapter.notifyDataSetChanged();
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_menu) {
            Intent startActivity = new Intent(OrdersActivity.this, MenuActivity.class);
            startActivity(startActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_orders, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            // handle the database initiation
            SharedPreferences prefs = rootView.getContext().getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
            final String currentMail = prefs.getString("email", null).replace("@","_").replace(".","_");

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

            //mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(rootView.getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            ApiService service = ServiceClient.getInstance().getClient(rootView.getContext(), ApiService.class);
            fillAdapter(currentMail,String.valueOf(getArguments().getInt(ARG_SECTION_NUMBER)-1),rootView,service);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.v("FOUND_VALUE_2",String.valueOf(position));
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "New";
                case 1:
                    return "Accepted";
                case 2:
                    return "Ready!";
            }
            return null;
        }
    }

    public static void fillAdapter(final String businesskey, final String status, final View root, final ApiService service){
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = mDatabase.child("businessorders").child(businesskey).orderByChild("status").equalTo(status);
        mAdapter = new FirebaseRecyclerAdapter<gazmend.com.mk.ready.models.OrderObjectBusiness, BusinessOrderViewHolder>(gazmend.com.mk.ready.models.OrderObjectBusiness.class, R.layout.order_business_list_row,
                BusinessOrderViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final BusinessOrderViewHolder viewHolder, final OrderObjectBusiness model, int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(mDatabase, model, new View.OnClickListener() {
                            @Override
                            public void onClick(View starView) {

                                int status = Integer.valueOf(model.getStatus());
                                //if (status == 0 ){status = 1;}
                                //else {status = 2;}
                                if (status == 2 ) {
                                    ImageView confirm =  (ImageView) root.findViewById(R.id.nextButton);
                                    confirm.setImageResource(R.mipmap.ic_close_black_24dp);
                                    // remove record from list
                                    mDatabase.child("businessorders").child(businesskey).child(postKey).removeValue();

                                    // custom dialog
                                    final Dialog dialog = new Dialog(root.getContext());
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
                                            String ratingkey = mDatabase.child("ratings").child(model.clientkey).push().getKey();

                                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                                            String date = df.format(new Date());

                                            HashMap<String, Object> ratinghash = new HashMap<>();
                                            ratinghash.put("rating", String.valueOf(rb.getRating()));
                                            ratinghash.put("daterated", date);

                                            Map<String, Object> bChildUpdates = new HashMap<>();
                                            bChildUpdates.put("/ratings/" + model.clientkey+ "/" + ratingkey, ratinghash);

                                            mDatabase.updateChildren(bChildUpdates);
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.show();
                                }
                                else
                                {
                                    Log.v("ORDER_CONFIRMED", postRef.getKey());
                                    // HERE PUT FAV LOGIC
                                    //Toast.makeText(OrdersActivity.this, "CLIENT USER FOUND.", Toast.LENGTH_LONG).show();
                                    if (status == 0 ){status = 1;}
                                    else {status = 2;}

                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                                    String date = df.format(new Date());


                                    OrderObjectBusiness bpost = new OrderObjectBusiness(model.getClientkey(), model.getOrderkey(), String.valueOf(status), model.getOrderdate(), "", date, model.getClienttoken());
                                    Map<String, Object> orderBusinessValues = bpost.toMap();

                                    Map<String, Object> bChildUpdates = new HashMap<>();
                                    bChildUpdates.put("/businessorders/" + businesskey + "/" + postKey, orderBusinessValues);

                                    mDatabase.updateChildren(bChildUpdates);

                                    service.notify(model.getClienttoken(),String.valueOf(status),new Callback<Response>() {
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
                                        Toast.makeText(root.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                    //MyFirebaseMessagingService fcm = new MyFirebaseMessagingService();
                                    //fcm.sendNotification("TEST NOTIFICATION");
                                }
                            }
                        }
                );
            }

        };
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean googleServiceAvailable =  isGooglePlayServicesAvailable(this);
        if (!googleServiceAvailable)
        {
            Toast.makeText(OrdersActivity.this,
                    "No google services available, notifications may not work properly",
                    Toast.LENGTH_LONG).show();
        }
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
