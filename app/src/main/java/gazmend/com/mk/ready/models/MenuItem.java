package gazmend.com.mk.ready.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gazmend on 6/6/2016.
 */

public class MenuItem {
    public String username;
    public String itemname;
    public String description;
    public String price;
    public String imagepath;

    public MenuItem() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public MenuItem(String username, String itemname, String description, String price, String imagepath) {
        this.username = username;
        this.itemname = itemname;
        this.description = description;
        this.imagepath = imagepath;
        this.price = price;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", this.username);
        result.put("itemname", this.itemname);
        result.put("description", this.description);
        result.put("price", this.price);
        result.put("imagepath",this.imagepath);
        return result;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
