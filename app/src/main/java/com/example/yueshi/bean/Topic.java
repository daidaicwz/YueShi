package com.example.yueshi.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class Topic extends BmobObject {
    private String content;//发贴文字内容
    private String title;//标题
    private MyUser author;//发帖人
    private String type;//发布类型
    private BmobFile picture;//图片
    private BmobRelation view;//浏览量(关联到用户)一对多关系
    private BmobRelation likes;//点赞
    private BmobRelation collection;//收藏

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public String gettype()
    {
        return type;
    }

    public void settype(String type)
    {
        this.type =type;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    public BmobRelation getView() {
        return view;
    }

    public void setView(BmobRelation view) {
        this.view = view;
    }

    public BmobRelation getCollection()
    {
        return collection;
    }

    public void setCollection(BmobRelation collection)
    {
        this.collection=collection;
    }

    public BmobRelation getLikes()
    {
        return likes;
    }

    public void setLikes(BmobRelation likes)
    {
        this.likes=likes;
    }

}
