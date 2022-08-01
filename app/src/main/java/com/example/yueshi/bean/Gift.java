package com.example.yueshi.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class Gift extends BmobObject {

    private String GiftName;//礼品名
    private Integer SpendPoint;//花费积分
    private BmobRelation GiftUser;//已兑换的用户（关联到用户）
    private BmobFile GiftPicture;//礼品图片

    public String getGiftName() {
        return GiftName;
    }

    public void setGiftName(String giftName) {
        GiftName = giftName;
    }

    public Integer getSpendPoint() {
        return SpendPoint;
    }

    public void setSpendPoint(Integer spendPoint) {
        SpendPoint = spendPoint;
    }

    public BmobRelation getGiftUser() {
        return GiftUser;
    }

    public void setGiftUser(BmobRelation user) {
        this.GiftUser = user;
    }

    public BmobFile getGiftPicture() {
        return GiftPicture;
    }

    public void setGiftPicture(BmobFile giftPicture) {
        GiftPicture = giftPicture;
    }

}
