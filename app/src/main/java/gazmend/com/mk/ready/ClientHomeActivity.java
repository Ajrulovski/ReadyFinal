package gazmend.com.mk.ready;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Visibility;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import gazmend.com.mk.ready.adapters.HomeRecyclerViewAdapter;
import gazmend.com.mk.ready.models.Homeobject;


public class ClientHomeActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Intent startActivity;

    String[] homeMenuItems;// = new String[] { "routes", "messages", "settings" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupWindowAnimations();

        ImageButton logoutImage = (ImageButton) findViewById(R.id.logout);
        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", "");
                editor.putString("email", "");
                editor.commit();

                Intent startActivity = new Intent(ClientHomeActivity.this, LoginActivity.class);
                startActivity(startActivity);
            }
        });

        ImageButton orderHistoryImage = (ImageButton) findViewById(R.id.orderhistory);
        orderHistoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startActivity = new Intent(ClientHomeActivity.this, ClientOrdersHistoryActivity.class);
                startActivity(startActivity);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        homeMenuItems = new String[] { "food", "services", "shopping" };


        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HomeRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);


        ((HomeRecyclerViewAdapter) mAdapter).setOnItemClickListener(new HomeRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                switch (position) {
                    case 0:
                        startActivity = new Intent(ClientHomeActivity.this, BusinessListActivity.class);
                        startActivity.putExtra("BUSINESS_TYPE", "Food and drinks");
                        startActivity(startActivity);
                        break;
                    case 1:
                        startActivity = new Intent(ClientHomeActivity.this, BusinessListActivity.class);
                        startActivity.putExtra("BUSINESS_TYPE", "Services");
                        startActivity(startActivity);
                        break;
                    case 2:
                        startActivity = new Intent(ClientHomeActivity.this, BusinessListActivity.class);
                        startActivity.putExtra("BUSINESS_TYPE", "Shopping");
                        startActivity(startActivity);
                        break;
                    default:
                        throw new RuntimeException("Unknown button ID");
                }
            }
        });
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Fade fade = new Fade();
        fade.setDuration(300);

            getWindow().setEnterTransition(fade);


        Slide slide = new Slide();
        slide.setDuration(300);
        getWindow().setEnterTransition(slide);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Homeobject> getDataSet() {
        ArrayList results = new ArrayList<Homeobject>();
        for (int index = 0; index < 3; index++) {
            Homeobject obj = new Homeobject(this,homeMenuItems[index]);
            //Log.i("FOUND",homeMenuItems[index]);
            obj.setimageResId(homeMenuItems[index]);
            results.add(index, obj);
        }
        return results;
    }
}
