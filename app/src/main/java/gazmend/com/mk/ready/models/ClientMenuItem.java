package gazmend.com.mk.ready.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gazmend on 6/6/2016.
 */

public class ClientMenuItem {
    public String itemname;
    public String description;
    public String price;

    public ClientMenuItem() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ClientMenuItem(String itemname, String description, String price) {
        this.itemname = itemname;
        this.description = description;
        this.price = price;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemname", this.itemname);
        result.put("description", this.description);
        result.put("price", this.price);
        return result;
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
