package gazmend.com.mk.ready.adapters;

/**
 * Created by Gazmend on 1/13/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import gazmend.com.mk.ready.R;
import gazmend.com.mk.ready.models.OrderObject;
import gazmend.com.mk.ready.models.OrderObjectBusiness;

public class BusinessOrderViewHolder extends RecyclerView.ViewHolder {
    public EditText clientname;
    public EditText ordercontent;
    public EditText ordertime;
    public EditText ordercomment;
    public ImageView nextButton;

    public BusinessOrderViewHolder(View itemView) {
        super(itemView);
        clientname = (EditText) itemView.findViewById(R.id.orderClient);
        ordercontent = (EditText) itemView.findViewById(R.id.orderContent);
        ordertime = (EditText) itemView.findViewById(R.id.ordertime);
        ordercomment = (EditText) itemView.findViewById(R.id.orderComment);
        nextButton = (ImageView) itemView.findViewById(R.id.nextButton);
        //cv = (CardView) itemView.findViewById(R.id.categories_card);
    }

    public void bindToPost(DatabaseReference mDatabase, OrderObjectBusiness post, View.OnClickListener starClickListener) {
        clientname.setText(post.getClientkey());
        //ordercontent.setText(post.getLastchanged());
        ordertime.setText(post.getOrderdate());
        nextButton.setOnClickListener(starClickListener);
        //cv.setOnClickListener(cardClickListener);

        //get the orderjson
        String clientorderkey = post.getClientkey();

        mDatabase.child("clientorders").child(clientorderkey).child(post.getOrderkey()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value

                        OrderObject order = dataSnapshot.getValue(OrderObject.class);
                        if (order == null) {
                            Log.e("NO_ORDERS_FOUND", "NO_ORDERS_FOUND");
                            //Toast.makeText(, "No user found.", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            JSONObject jo = null;
                            try {
                                jo = new JSONObject(order.getOrderjson());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String ordertext = jsonToHumanReadable(jo);
                            ordercontent.setText(ordertext);
                            ordercomment.setText(order.getAdditionalinfo());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("CANCELED_ORDER", "getOrder:onCancelled", databaseError.toException());
                    }
                });
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
                String amountpart = parts[2].replace("\"","").replace("{","").replace("}","");
                output = output + amountpart + " x " + parts[0] + ", ";
            } catch (JSONException e) {
                // Something went wrong!
                Log.v("READY_ERROR_JSON_PARSE", e.getMessage());
            }
        }

        output = output.substring(0,output.length()-2);
        return output;
    }

}
