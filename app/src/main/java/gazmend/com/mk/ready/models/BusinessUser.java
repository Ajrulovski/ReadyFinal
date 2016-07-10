package gazmend.com.mk.ready.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gazmend on 6/10/2016.
 */

public class BusinessUser implements Parcelable {
    public String email;
    public String pass;
    public String fullname;
    public String birthday;
    public String phone;
    public String type;
    public String address;
    public String location;
    public String usertype;
    public String rating;
    public String imagepath;
    public String firebasetoken;

    public BusinessUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public BusinessUser(String email, String pass, String fullname, String birthday, String phone, String type, String address, String location, String usertype, String rating, String imagepath, String firebasetoken) {
        this.email = email;
        this.pass = pass;
        this.fullname = fullname;
        this.birthday = birthday;
        this.phone = phone;
        this.type = type;
        this.address = address;
        this.location = location;
        this.usertype = usertype;
        this.rating = rating;
        this.imagepath = imagepath;
        this.firebasetoken = firebasetoken;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", this.email);
        result.put("pass", this.pass);
        result.put("fullname", this.fullname);
        result.put("birthday", this.birthday);
        result.put("phone", this.phone);
        result.put("type", this.type);
        result.put("address", this.address);
        result.put("location", this.location);
        result.put("usertype", this.usertype);
        result.put("rating", this.rating);
        result.put("imagepath", this.imagepath);
        result.put("firebasetoken", this.firebasetoken);
        return result;
    }

    public String getFirebasetoken() {
        return firebasetoken;
    }

    public void setFirebasetoken(String firebasetoken) {
        this.firebasetoken = firebasetoken;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    //parcel part
    public BusinessUser(Parcel in){
        String[] data= new String[11];

        in.readStringArray(data);

        this.email = data[0];
        this.pass = data[1];
        this.fullname = data[2];
        this.birthday = data[3];
        this.phone = data[4];
        this.type = data[5];
        this.address = data[6];
        this.location = data[7];
        this.usertype = data[8];
        this.rating = data[9];
        this.imagepath = data[10];
    }
    @Override
    public int describeContents() {
// TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeStringArray(new String[]{this.email,this.pass,this.fullname,this.birthday,this.phone,this.type,this.address,this.location,this.usertype,this.rating,this.imagepath});
    }

    public static final Parcelable.Creator<BusinessUser> CREATOR= new Parcelable.Creator<BusinessUser>() {

        @Override
        public BusinessUser createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new BusinessUser(source);  //using parcelable constructor
        }

        @Override
        public BusinessUser[] newArray(int size) {
            // TODO Auto-generated method stub
            return new BusinessUser[size];
        }
    };
}
