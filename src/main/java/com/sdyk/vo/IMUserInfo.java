package com.sdyk.vo;

/**
 * Created by taylor on 2018/4/19.
 */
public class IMUserInfo {
    private String user;
    private String nickName;
    private String headPic;
    private String mobileTypeNo;
    //1:启用；-1：关闭
    private int onOff;

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getOnOff() {
        return onOff;
    }

    public void setOnOff(int onOff) {
        this.onOff = onOff;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMobileTypeNo() {
        return mobileTypeNo;
    }

    public void setMobileTypeNo(String mobileTypeNo) {
        this.mobileTypeNo = mobileTypeNo;
    }
}
