package gazmend.com.mk.ready.adapters;

/**
 * Created by Gazmend on 1/13/2016.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import gazmend.com.mk.ready.R;
import gazmend.com.mk.ready.models.BusinessUser;

public class BusinessItemViewHolder extends RecyclerView.ViewHolder {
    public EditText rating;
    public EditText businesstitle;
    public ImageView businessImageView;
    public ImageView favImageView;
    public ImageView rateImageView;
    public CardView cv;

    public BusinessItemViewHolder(View itemView) {
        super(itemView);
        rating = (EditText) itemView.findViewById(R.id.rating);
        businesstitle = (EditText) itemView.findViewById(R.id.businesstitle);
        businessImageView = (ImageView) itemView.findViewById(R.id.businessImageView);
        favImageView = (ImageView) itemView.findViewById(R.id.favicon);
        rateImageView = (ImageView) itemView.findViewById(R.id.rateicon);
        cv = (CardView) itemView.findViewById(R.id.categories_card);
    }

    public void bindToPost(BusinessUser post, View.OnClickListener heartClickListener, View.OnClickListener cardClickListener, View.OnClickListener starClickListener) {

        rating.setText(post.rating);
        businesstitle.setText(post.fullname);
        Picasso.with(businessImageView.getContext()).load(post.getImagepath())
                .placeholder(R.drawable.placeholder) //
                .error(R.drawable.picasso_error)
                .into(businessImageView);
        favImageView.setOnClickListener(heartClickListener);
        cv.setOnClickListener(cardClickListener);
        rateImageView.setOnClickListener(starClickListener);
    }

}
