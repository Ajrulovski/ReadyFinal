package gazmend.com.mk.ready.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gazmend on 6/6/2016.
 */

public class ClientUser {
    public String email;
    public String pass;
    public String fullname;
    public String birthday;

    public ClientUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ClientUser(String email, String pass, String fullname, String birthday) {
        this.email = email;
        this.pass = pass;
        this.fullname = fullname;
        this.birthday = birthday;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", this.email);
        result.put("pass", this.pass);
        result.put("fullname", this.fullname);
        result.put("birthday", this.birthday);

        return result;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return pass;
    }

    public void setPassword(String password) {
        this.pass = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
