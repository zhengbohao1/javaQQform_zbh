package domain;

import java.io.Serializable;

public class User implements Serializable {
    private String userId;  // 用户账号
    private String password;  // 密码
    private String registMessageType;  // 设置是否请求注册

    private String state = null;    // 4.0 设置发送状态

    public User() { }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", registMessageType='" + registMessageType + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    public User(String userId, String password) {
        super();
        this.userId = userId;
        this.password = password;
    }


    /* get set */
    public String getRegistMessageType() {
        return registMessageType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setRegistMessageType(String registMessageType) {
        this.registMessageType = registMessageType;
    }

    public String getPassword() {
        return password;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPassward(String password) {
        this.password = password;
    }
}
