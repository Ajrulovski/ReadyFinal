package gazmend.com.mk.ready.adapters;

/**
 * Created by Gazmend on 1/13/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import gazmend.com.mk.ready.R;
import gazmend.com.mk.ready.models.ClientMenuItem;

public class ClientMenuItemViewHolder extends RecyclerView.ViewHolder {
    public EditText itemName;
    public EditText itemDescription;
    public EditText itemPrice;
    public View iView;

    public ClientMenuItemViewHolder(View itemView) {
        super(itemView);
        itemName = (EditText) itemView.findViewById(R.id.itemName);
        itemDescription = (EditText) itemView.findViewById(R.id.itemDesc);
        itemPrice = (EditText) itemView.findViewById(R.id.itemPrice);
        iView = itemView;
    }

    public void bindToPost(ClientMenuItem post, View.OnClickListener recItemClickListener) {
        itemName.setText(post.itemname);
        itemDescription.setText(post.description);
        itemPrice.setText(post.price);
        iView.setOnClickListener(recItemClickListener);
        itemName.setOnClickListener(recItemClickListener);
        itemPrice.setOnClickListener(recItemClickListener);
    }

}
