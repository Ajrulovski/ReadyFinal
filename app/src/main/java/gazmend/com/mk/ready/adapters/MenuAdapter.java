package gazmend.com.mk.ready.adapters;

/**
 * Created by Gazmend on 1/13/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import gazmend.com.mk.ready.R;
import gazmend.com.mk.ready.models.MenuItem;


public class MenuAdapter extends RecyclerView.Adapter<MenuItemViewHolder> implements View.OnClickListener{

    private List<MenuItem> mFeedList;
    private Context cntx;
    private ViewGroup mParent;
    MenuItem timeLineModel;
    ImageView deleteIcon,editIcon;

    public MenuAdapter(List<MenuItem> feedList) {
        mFeedList = feedList;
    }

    @Override
    public MenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = View.inflate(parent.getContext(), R.layout.item_menu, null);

        mParent = parent;
        return new MenuItemViewHolder(view);
    }

//    @Override
//    public int getItemViewType(int position) {
//        // Just as an example, return 0 or 2 depending on position
//        // Note that unlike in ListView adapters, types don't have to be contiguous
//        timeLineModel = mFeedList.get(position);
//        int retvalue=0;
//        // check for type of style
//        String itemname = timeLineModel.getItemname();
//        String itemdesc = timeLineModel.getDescription();
//        String itemprice = timeLineModel.getPrice();
//
//        return retvalue;
//    }

    @Override
    public void onBindViewHolder(MenuItemViewHolder holder, int position) {
        MenuItem timeLineModel = mFeedList.get(position);

        String itemname = timeLineModel.getItemname();
        String itemdesc = timeLineModel.getDescription();
        String itemprice = timeLineModel.getPrice();

        holder.itemName.setText(itemname);
        holder.itemDescription.setText(itemdesc);
        holder.itemPrice.setText(itemprice);

        deleteIcon = (ImageView) holder.deleteIcon;
        editIcon = (ImageView) holder.editIcon;

        cntx = editIcon.getContext();

        deleteIcon.setOnClickListener(this);
        editIcon.setOnClickListener(this);
    }




    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == deleteIcon.getId()) {
            Toast.makeText(cntx, "DeleteIcon Clicked!", Toast.LENGTH_LONG).show();
            //mFeedList.get(8).setVisible(false);
            //mFeedList.remove(8);
            //notifyItemRemoved(8);
            //notifyDataSetChanged();
        }
        if (v.getId() == editIcon.getId()) {
            Toast.makeText(cntx, "EditIcon Clicked!", Toast.LENGTH_LONG).show();
            //mFeedList.get(8).setVisible(false);
            //mFeedList.remove(8);
            //notifyItemRemoved(8);
            //notifyDataSetChanged();
        }
    }
}