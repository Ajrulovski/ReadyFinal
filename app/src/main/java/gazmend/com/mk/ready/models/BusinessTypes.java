package gazmend.com.mk.ready.models;

/**
 * Created by Gazmend on 6/8/2016.
 */

public class BusinessTypes {
    public String title;
    public String desc;

    public BusinessTypes() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public BusinessTypes(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
