package gazmend.com.mk.ready;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gazmend.com.mk.ready.models.MenuItem;

public class AddNewMenuItemActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    AddMenuItemTask addItemTask;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap foundBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_menu_item);

        SharedPreferences prefs = getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
        final String uname = prefs.getString("username", null);

        final EditText etItemname = (EditText) findViewById(R.id.itemname);
        final EditText etDescription = (EditText) findViewById(R.id.description);
        final EditText etPrice = (EditText) findViewById(R.id.price);
        final ImageView iView = (ImageView) findViewById(R.id.itemImageView);

        // create reference towards the database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://ready-c384e.appspot.com");

        // Create a reference to "mountains.jpg"
        //StorageReference mountainsRef = storageRef.child("mountains.jpg");

        Button mAddNewItemButton = (Button) findViewById(R.id.add_new_item);
        mAddNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uname != null)
                {
                    //iView.setDrawingCacheEnabled(true);
                    //iView.buildDrawingCache();
                    Bitmap bitmap = foundBitmap;

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    addItemTask = new AddMenuItemTask(uname, etItemname.getText().toString(), etDescription.getText().toString(), etPrice.getText().toString(),storageRef,data);
                    addItemTask.execute((Void) null);
                }
                else
                {
                    //getFragmentIds(docId);
                }

            }
        });

        Button mChooseImage = (Button) findViewById(R.id.addimage);
        mChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uname != null)
                {
                    Intent intent = new Intent();
                    // Show only images, no videos or anything else
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    // Always show the chooser (if there are multiple options available)
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
                else
                {
                    //getFragmentIds(docId);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                foundBitmap = bitmap;
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.itemImageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerNewMenuItem(final String username, final String itemname, final String description, final String price, StorageReference storageref, byte[] data) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        // get the push key here
        final String key = mDatabase.child(username).push().getKey();

        // Get the data from an ImageView as bytes
        StorageReference mountainsRef = storageref.child(key);

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //taskSnapshot.getMetadata(); //contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String finalurl = downloadUrl.toString();
                Log.v("FOUND_URI",finalurl);

                MenuItem post = new MenuItem(key, itemname, description, price, finalurl);
                Map<String, Object> menuItemValues = post.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/menuItems/" + username + "/" + key, menuItemValues);
                //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

                mDatabase.updateChildren(childUpdates);
            }
        });
        //String key = username;
        //Log.i("FIREBASE",key);

    }

    public class AddMenuItemTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUname;
        private final String mItemName;
        private final String mDescription;
        private final String mPrice;
        private final StorageReference mStorageRef;
        private final byte[] mData;

        AddMenuItemTask(String uname, String itemName, String description, String price, StorageReference storageref, byte[] data) {
            mItemName = itemName;
            mDescription = description;
            mPrice = price;
            mUname = uname;
            mStorageRef = storageref;
            mData = data;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            registerNewMenuItem(mUname,mItemName,mDescription,mPrice,mStorageRef,mData);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                //Intent startActivity = new Intent(RegisterBusinessActivity.this, OrdersActivity.class);
                //startActivity(startActivity);
                Toast.makeText(AddNewMenuItemActivity.this, "ADDED MENU ITEM.", Toast.LENGTH_LONG).show();
                finish();
            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
