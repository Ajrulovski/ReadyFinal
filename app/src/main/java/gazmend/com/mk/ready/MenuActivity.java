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

import java.util.ArrayList;
import java.util.List;

import gazmend.com.mk.ready.adapters.MenuAdapter;
import gazmend.com.mk.ready.adapters.MenuItemViewHolder;
import gazmend.com.mk.ready.models.MenuItem;

public class MenuActivity extends AppCompatActivity {
    private List<MenuItem> mDataList = new ArrayList<>();
    private MenuAdapter mMenuAdapter;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<MenuItem, MenuItemViewHolder> mAdapter;
    private String loggedUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the logged in user from the shared prefs
        SharedPreferences prefs = getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
        loggedUsername = prefs.getString("username",null);
        if (loggedUsername == null)
        {
            // no user found in shared prefs, reirect to login
            Intent startActivity = new Intent(MenuActivity.this, LoginActivity.class);
            startActivity(startActivity);
        }
        else {

            mDatabase = FirebaseDatabase.getInstance().getReference();

            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(linearLayoutManager);

            fillAdapter(loggedUsername);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    //        .setAction("Action", null).show();

                    Intent startActivity = new Intent(MenuActivity.this, AddNewMenuItemActivity.class);
                    startActivity(startActivity);
                }
            });
        }
    }

    public void fillAdapter(final String userId){
//        MenuItem menuItemModel = new MenuItem();
//        menuItemModel.setItemname("COFFEE");
//        menuItemModel.setDescription("Some awesome description");
//        menuItemModel.setPrice("5$");
//        mDataList.add(menuItemModel);
//
//        MenuItem menuItemModel2 = new MenuItem();
//        menuItemModel2.setItemname("Cheesecake");
//        menuItemModel2.setDescription("I love it like a LOT!");
//        menuItemModel2.setPrice("4$");
//        mDataList.add(menuItemModel2);
//
//        mDataList.add(menuItemModel);
//        mDataList.add(menuItemModel2);
//
//        mMenuAdapter = new MenuAdapter(mDataList);
//        mRecyclerView.setAdapter(mMenuAdapter);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = mDatabase.child("menuItems").child(userId);
        mAdapter = new FirebaseRecyclerAdapter<MenuItem, MenuItemViewHolder>(MenuItem.class, R.layout.item_menu,
                MenuItemViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(MenuItemViewHolder viewHolder, MenuItem model, int position) {
                final DatabaseReference postRef = getRef(position);

                ImageView posterView = viewHolder.itemImage;

                //new ImageLoaderAsyncTask(posterView).execute("http://image.tmdb.org/t/p/w185" + row.getString("poster"));
                Picasso.with(getApplicationContext()).load(model.getImagepath())
                        .placeholder(R.drawable.placeholder) //
                        .error(R.drawable.picasso_error)
                        .into(posterView);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "tapped!!", Toast.LENGTH_LONG).show();
                    }
                });

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        Log.v("FOUND_KEY",postRef.getKey());
                        // Need to write to both places the post is stored
                        //DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        //DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());
                        mDatabase.child("menuItems").child(userId).child(postRef.getKey()).removeValue();
                        Toast.makeText(getApplicationContext(), "Item removed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);

    }

    public void fetchMenuData(){

    }
}
