package gazmend.com.mk.ready.models;

import android.content.Context;

/**
 * Created by Gazmend on 11/4/2015.
 */
public class Homeobject {
    private String imageResName;
    private int imageResId;
    private Context cntx;

    public Homeobject(Context context, String imageResName){
        imageResName = imageResName;
        cntx = context;
    }

    public String getimageResName() {
        return imageResName;
    }

    public void setimageResName(String imageResName) {
        this.imageResName = imageResName;
    }

    public int getimageResId() {
        return imageResId;
    }

    public void setimageResId(String imageResName) {
        int resID = cntx.getResources().getIdentifier(imageResName, "drawable", cntx.getPackageName());
        this.imageResId = resID;
    }
}
