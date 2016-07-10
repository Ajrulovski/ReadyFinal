package gazmend.com.mk.ready.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gazmend on 6/6/2016.
 */

public class OrderObject {
    public String email;
    public String key;
    public String orderjson;
    public String orderdate;
    public String businesskey;
    public String orderstatus;
    public String additionalinfo;

    public OrderObject() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public OrderObject(String email, String key, String orderjson, String orderdate, String businesskey, String orderstatus, String additionalinfo) {
        this.email = email;
        this.key = key;
        this.orderjson = orderjson;
        this.orderdate = orderdate;
        this.businesskey = businesskey;
        this.orderstatus = orderstatus;
        this.additionalinfo = additionalinfo;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", this.email);
        result.put("key", this.key);
        result.put("orderjson", this.orderjson);
        result.put("orderdate", this.orderdate);
        result.put("businesskey", this.businesskey);
        result.put("orderstatus", this.orderstatus);
        result.put("additionalinfo", this.additionalinfo);
        return result;
    }

    public String getAdditionalinfo() {
        return additionalinfo;
    }

    public void setAdditionalinfo(String additionalinfo) {
        this.additionalinfo = additionalinfo;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public String getBusinesskey() {
        return businesskey;
    }

    public void setBusinesskey(String businesskey) {
        this.businesskey = businesskey;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOrderjson() {
        return orderjson;
    }

    public void setOrderjson(String orderjson) {
        this.orderjson = orderjson;
    }
}
