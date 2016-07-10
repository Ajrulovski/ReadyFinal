package gazmend.com.mk.ready.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import gazmend.com.mk.ready.R;
import gazmend.com.mk.ready.models.Homeobject;


/**
 * Created by Gazmend on 11/4/2015.
 */
public class HomeRecyclerViewAdapter extends RecyclerView
        .Adapter<HomeRecyclerViewAdapter
        .homeobjectHolder> {
    private static String LOG_TAG = "HomeRecyclerViewAdapter";
    private ArrayList<Homeobject> mDataset;
    private static MyClickListener myClickListener;

    public static class homeobjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView homemenuitem;

        public homeobjectHolder(View itemView) {
            super(itemView);
            homemenuitem = (ImageView) itemView.findViewById(R.id.imageView);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public HomeRecyclerViewAdapter(ArrayList<Homeobject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public homeobjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_cards_row, parent, false);

        homeobjectHolder homeobjectHolder = new homeobjectHolder(view);
        return homeobjectHolder;
    }

    @Override
    public void onBindViewHolder(homeobjectHolder holder, int position) {
        // HERE PARSE DATA TO DETECT IMAGES
        //holder.homemenuitem.setImageResource(R.drawable.nearby);
        holder.homemenuitem.setImageResource(mDataset.get(position).getimageResId());
    }

    public void addItem(Homeobject dataObj, int index) {
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
