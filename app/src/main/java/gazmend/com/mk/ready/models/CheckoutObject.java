package gazmend.com.mk.ready.models;

/**
 * Created by Gazmend on 11/4/2015.
 */
public class CheckoutObject {
    private String itemTitle;
    private String itemQuantity;
    private String itemPrice;
    private String itemKey;

    public CheckoutObject(String itemTitle,String itemQuantity,String itemPrice, String itemKey){
        this.itemTitle = itemTitle;
        this.itemQuantity = itemQuantity;
        this.itemPrice = itemPrice;
        this.itemKey = itemKey;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }
}
