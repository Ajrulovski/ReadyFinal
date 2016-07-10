package gazmend.com.mk.ready.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import gazmend.com.mk.ready.CheckoutActivity;
import gazmend.com.mk.ready.R;
import gazmend.com.mk.ready.models.CheckoutObject;


/**
 * Created by Gazmend on 11/4/2015.
 */
public class CheckoutViewAdapter extends RecyclerView
        .Adapter<CheckoutViewAdapter
        .homeobjectHolder> {
    private static String LOG_TAG = "CheckoutViewAdapter";
    private ArrayList<CheckoutObject> mDataset;
    private static MyClickListener myClickListener;
    public Context tContext;
    public JSONObject recievedOrder;

    public static class homeobjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        EditText itemName;
        EditText itemQuantity;
        EditText itemFullPrice;
        ImageView removeButton;

        public homeobjectHolder(View itemView) {
            super(itemView);
            itemName = (EditText) itemView.findViewById(R.id.itemName);
            itemQuantity = (EditText) itemView.findViewById(R.id.itemQuantity);
            itemFullPrice = (EditText) itemView.findViewById(R.id.itemFullPrice);
            removeButton = (ImageView) itemView.findViewById(R.id.removeImage);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // this is invoked when the remove button is pressed
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public CheckoutViewAdapter(JSONObject orderjson, Context cntx) {
        mDataset = jsonToObjectArray(orderjson,cntx);
        tContext = cntx;
        recievedOrder = orderjson;
    }

    public ArrayList<CheckoutObject> jsonToObjectArray(JSONObject jo,Context cntx)
    {
        ArrayList results = new ArrayList<CheckoutObject>();
        int index = 0;
        Iterator<String> iter = jo.keys();
        String output = "";
        int totalPrice = 0;

        while (iter.hasNext()) {
            String key = iter.next();
            try {
                Object value = jo.get(key);
                // string cleanup and split
                String parts[] = value.toString().replace("\"","").replace("{","").replace("}","").split(";");
                CheckoutObject obj = new CheckoutObject(parts[0],parts[2],parts[1],key);
                results.add(index, obj);
                index +=1;
                totalPrice += Integer.valueOf(parts[2])*Integer.valueOf(parts[1]);
            } catch (JSONException e) {
                // Something went wrong!
                Log.v("READY_ERROR_JSON_PARSE", e.getMessage());
            }
        }

        // record the current totalPrice
        SharedPreferences prefs = cntx.getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("total_order", String.valueOf(totalPrice));
        editor.commit();
        //CheckoutObject objTotal = new CheckoutObject("TOTAL","0",String.valueOf(totalPrice));
        //results.add(index, objTotal);
        return results;
    }

    @Override
    public homeobjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkout_row, parent, false);

        homeobjectHolder homeobjectHolder = new homeobjectHolder(view);
        return homeobjectHolder;
    }

    @Override
    public void onBindViewHolder(final homeobjectHolder holder, final int position) {
        // HERE PARSE DATA TO DETECT IMAGES
        //holder.homemenuitem.setImageResource(R.drawable.nearby);
        //holder.homemenuitem.setImageResource(mDataset.get(position).getimageResId());
        //holder.item
        holder.itemName.setText(mDataset.get(position).getItemTitle());
        holder.itemQuantity.setText("Quantity: "+mDataset.get(position).getItemQuantity());
        int fprice = Integer.valueOf(mDataset.get(position).getItemQuantity())*Integer.valueOf(mDataset.get(position).getItemPrice());
        holder.itemFullPrice.setText(String.valueOf(fprice));

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View starView) {
                recievedOrder.remove(mDataset.get(position).getItemKey());
                deleteItem(position);

                //after succesfull removal edit the sharedprefs of the order as well
                SharedPreferences prefs = holder.itemView.getContext().getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("jsonordervalues", recievedOrder.toString());
                editor.commit();

                // update the total price
                String newTotal = getTotal(mDataset);
                EditText totals = (EditText) ((CheckoutActivity)tContext).findViewById(R.id.itemTotalPrice);
                totals.setText(newTotal);
            }
        });
    }

    public String getTotal(ArrayList<CheckoutObject> mDataset){
        int tot=0;
        for(int i=0;i<mDataset.size();i++)
        {
            tot += Integer.valueOf(mDataset.get(i).getItemPrice())*Integer.valueOf(mDataset.get(i).getItemQuantity());
        }
        return String.valueOf(tot);
    }

    public void addItem(CheckoutObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
