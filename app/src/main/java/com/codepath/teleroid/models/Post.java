package com.codepath.teleroid.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Post") //Matches the name of the Parse entity
@Parcel(analyze={Post.class})
public class Post extends ParseObject {

    //Keys for the names of each of the attributes of the Parse entity.
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_TIME = "createdAt";

    private List<Like> likes = new ArrayList<>();

    public  Post(){
        //empty constructor needed by Parceler & Parce
    }

    public String getCaption(){
        return getString(KEY_CAPTION);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setCaption(String caption){
        put(KEY_CAPTION, caption);
    }

    public void setImage(ParseFile file){
        put(KEY_IMAGE, file);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public void setLikes(List<Like> likes) {
        this.likes.addAll(likes);
    }
}