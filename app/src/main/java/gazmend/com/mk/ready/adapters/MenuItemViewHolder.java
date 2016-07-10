package gazmend.com.mk.ready.adapters;

/**
 * Created by Gazmend on 1/13/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import gazmend.com.mk.ready.R;
import gazmend.com.mk.ready.models.MenuItem;

public class MenuItemViewHolder extends RecyclerView.ViewHolder {
    public EditText itemName;
    public EditText itemDescription;
    public EditText itemPrice;
    public ImageView deleteIcon;
    public ImageView editIcon;
    public ImageView itemImage;

    public MenuItemViewHolder(View itemView) {
        super(itemView);
        itemName = (EditText) itemView.findViewById(R.id.itemName);
        itemDescription = (EditText) itemView.findViewById(R.id.itemDesc);
        itemPrice = (EditText) itemView.findViewById(R.id.itemPrice);
        deleteIcon = (ImageView) itemView.findViewById(R.id.deleteicon);
        editIcon = (ImageView) itemView.findViewById(R.id.editicon);
        itemImage = (ImageView) itemView.findViewById(R.id.itemImageView);
    }

    public void bindToPost(MenuItem post, View.OnClickListener starClickListener) {
        itemName.setText(post.itemname);
        itemDescription.setText(post.description);
        itemPrice.setText(post.price);

        deleteIcon.setOnClickListener(starClickListener);
    }

}
