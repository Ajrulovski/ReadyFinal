package gazmend.com.mk.ready.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gazmend on 6/6/2016.
 */

public class OrderObjectBusiness {
    public String clientkey;
    public String orderkey;
    public String status;
    public String orderdate;
    public String businesscoment;
    public String lastchanged;
    public String clienttoken;

    public OrderObjectBusiness() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public OrderObjectBusiness(String clientkey, String orderkey, String status, String orderdate, String businesscoment, String lastchanged, String clienttoken) {
        this.clientkey = clientkey;
        this.orderkey = orderkey;
        this.status = status;
        this.orderdate = orderdate;
        this.businesscoment = businesscoment;
        this.lastchanged = lastchanged;
        this.clienttoken = clienttoken;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("clientkey", this.clientkey);
        result.put("orderkey", this.orderkey);
        result.put("status", this.status);
        result.put("orderdate", this.orderdate);
        result.put("businesscoment", this.businesscoment);
        result.put("lastchanged", this.lastchanged);
        result.put("clienttoken", this.clienttoken);
        return result;
    }

    public String getClienttoken() {
        return clienttoken;
    }

    public void setClienttoken(String clienttoken) {
        this.clienttoken = clienttoken;
    }

    public String getLastchanged() {
        return lastchanged;
    }

    public void setLastchanged(String lastchanged) {
        this.lastchanged = lastchanged;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public String getBusinesscoment() {
        return businesscoment;
    }

    public void setBusinesscoment(String businesscoment) {
        this.businesscoment = businesscoment;
    }

    public String getClientkey() {
        return clientkey;
    }

    public void setClientkey(String clientkey) {
        this.clientkey = clientkey;
    }

    public String getOrderkey() {
        return orderkey;
    }

    public void setOrderkey(String orderkey) {
        this.orderkey = orderkey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
