package gazmend.com.mk.ready.adapters;

/**
 * Created by Gazmend on 1/13/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import gazmend.com.mk.ready.R;
import gazmend.com.mk.ready.models.CheckoutObject;
import gazmend.com.mk.ready.models.MenuItem;
import gazmend.com.mk.ready.models.OrderObject;

public class ClientOrderHistoryViewHolder extends RecyclerView.ViewHolder {
    public EditText businessName;
    public EditText itemOrderText;
    public EditText itemPrice;
    public ImageView editIcon;

    public ClientOrderHistoryViewHolder(View itemView) {
        super(itemView);
        businessName = (EditText) itemView.findViewById(R.id.businessName);
        itemOrderText = (EditText) itemView.findViewById(R.id.itemOrderText);
        itemPrice = (EditText) itemView.findViewById(R.id.itemPrice);
        editIcon = (ImageView) itemView.findViewById(R.id.editicon);
    }

    public void bindToPost(OrderObject post, View.OnClickListener starClickListener) {
        businessName.setText(post.getBusinesskey());

        JSONObject orderObj = null;
        try {
            orderObj = new JSONObject(post.getOrderjson());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String humanReadableOrder = jsonToHumanReadable(orderObj);
        itemOrderText.setText(humanReadableOrder);

        String totalString = calculateTotal(orderObj);
        itemPrice.setText(totalString);
        editIcon.setOnClickListener(starClickListener);
    }

    public String calculateTotal(JSONObject jo)
    {
            Iterator<String> iter = jo.keys();
            String output = "";
            int totalPrice = 0;

            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    Object value = jo.get(key);
                    String parts[] = value.toString().split(";");
                    String amountpart = parts[2].replace("\"","").replace("{","").replace("}","");
                    String pricepart = parts[1].replace("\"","").replace("{","").replace("}","");
                    totalPrice += Integer.valueOf(amountpart)*Integer.valueOf(pricepart);
                } catch (JSONException e) {
                    // Something went wrong!
                    Log.v("READY_ERROR_JSON_PARSE", e.getMessage());
                }
            }
            return String.valueOf(totalPrice);
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
