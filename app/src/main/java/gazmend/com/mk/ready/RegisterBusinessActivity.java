package gazmend.com.mk.ready;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gazmend.com.mk.ready.models.BusinessTypes;
import gazmend.com.mk.ready.models.BusinessUser;

import static gazmend.com.mk.ready.R.id.phone;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterBusinessActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private DatePickerDialog datePickerDialog;
    private DatabaseReference mDatabase;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mFullnameView;
    private EditText mPhoneView;
    private Spinner mTypeView;
    private EditText mAddress;
    private EditText mLocation;

    private View mProgressView;
    private View mLoginFormView;
    private SimpleDateFormat dateFormatter;
    private static final String TAG = "FIREBASE";

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap foundBitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_business);

        mEmailView = (EditText) findViewById(R.id.email);
        mAddress = (EditText) findViewById(R.id.address);
        mFullnameView = (EditText) findViewById(R.id.fullname);

        mPhoneView = (EditText) findViewById(phone);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLocation = (EditText) findViewById(R.id.location);

        mTypeView = (Spinner) findViewById(R.id.typespinner);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://ready-c384e.appspot.com");

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(storageRef, foundBitmap);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Spinner element
        //Spinner spinner = (Spinner) findViewById(R.id.typespinner);

        // Spinner click listener
        mTypeView.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Food and drinks");
        categories.add("Services");
        categories.add("Shopping");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mTypeView.setAdapter(dataAdapter);

        // handle location
        TextView tv=(TextView)findViewById(R.id.location);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("business_location", Context.MODE_PRIVATE);
        String loc = prefs.getString("last_loc", null);
        if (loc != null){
            tv.setText(loc);
        }


        Button mChooseLoc = (Button) findViewById(R.id.addloc);
        mChooseLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startActivity = new Intent(RegisterBusinessActivity.this, ChooseLocation.class);
                startActivity(startActivity);
            }
        });


        Button mChooseImage = (Button) findViewById(R.id.addimage);
        mChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent();
                    // Show only images, no videos or anything else
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    // Always show the chooser (if there are multiple options available)
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        ////getBusinessTypes();
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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(StorageReference storage, Bitmap bitmap) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String fullname = mFullnameView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String address = mAddress.getText().toString();
        String type = mTypeView.getSelectedItem().toString();
        String location = mLocation.getText().toString();

        boolean cancel = false;
        View focusView = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, fullname, phone, type, address, location, storage, data);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void registerNewBusiness(final String email, final String pass, final String fullname, final String birthday, final String phone, final String type, final String address, final String location, final String usertype, StorageReference storageref, byte[] data) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        //String key = mDatabase.child("posts").push().getKey();
        final String key = email.replace("@","_").replace(".","_");

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

                Log.i("FIREBASE",key);
                BusinessUser post = new BusinessUser(email, pass, fullname, birthday, phone, type, address, location, usertype, "0.0",finalurl,"");
                Map<String, Object> businessUserValues = post.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/businessUsers/" + key, businessUserValues);
                //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

                mDatabase.updateChildren(childUpdates);
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void getBusinessTypes() {
        final String businessTypeNodeName = "business_types";
        mDatabase.child(businessTypeNodeName).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Log.e(TAG, "ENTERED");
                        BusinessTypes types = dataSnapshot.getValue(BusinessTypes.class);

                        // [START_EXCLUDE]
                        if (types == null) {
                            // User is null, error out
                            Log.e(TAG, "Types not found");
                            Toast.makeText(RegisterBusinessActivity.this,
                                    "Error: Types not found.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // Write new post
                            Toast.makeText(RegisterBusinessActivity.this,
                                    "TYPES FOUND.",
                                    Toast.LENGTH_LONG).show();

                            String inputJSONString = types.toString(); // Your string JSON here
                            JSONObject jObject = null;
                            try {
                                jObject = new JSONObject(inputJSONString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Iterator<String> keys = jObject.keys();

                            String full = "";
                            while( keys.hasNext() ) {
                                String key = keys.next();
                                try {
                                    full = full + jObject.getString(key) +" ";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            Toast.makeText(RegisterBusinessActivity.this,
                                    full,
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getTypes:onCancelled", databaseError.toException());
                    }
                });
        // [END single_value_read]
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mFullname;
        private final String mPhone;
        private final String mType;
        private final String mAddress;
        private final String mLocation;
        private final StorageReference mStorageRef;
        private final byte[] mData;

        UserLoginTask(String email, String password, String fullname, String phone, String type, String address, String location, StorageReference storageref, byte[] data) {
            mEmail = email;
            mPassword = password;
            mFullname = fullname;
            mPhone = phone;
            mType = type;
            mAddress = address;
            mLocation = location;
            mStorageRef = storageref;
            mData = data;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // Simulate network access.
            //Thread.sleep(2000);
            registerNewBusiness(mEmail,mPassword,mFullname,"",mPhone,mType,mAddress,mLocation,"2",mStorageRef,mData);

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent startActivity = new Intent(RegisterBusinessActivity.this, LoginActivity.class);
                startActivity(startActivity);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

