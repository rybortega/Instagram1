package com.codepath.teleroid.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("Comment")
@Parcel(analyze={Comment.class})
public class Comment extends ParseObject {

    //Keys for the names of each of the attributes of the Parse entity.
    public static final String KEY_TARGET = "target";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_BODY = "body";
    public static final String KEY_TIME = "createdAt";

    public Comment(){
        //empty constructor needed by Parce
    }

    public ParseUser getAuthor(){
        return getParseUser(KEY_AUTHOR);
    }

    public Post getTarget(){
        return (Post) get(KEY_TARGET);
    }

    public String getBody(){
        return getString(KEY_BODY);
    }

    public void setAuthor(ParseUser author){
        put(KEY_AUTHOR, author);
    }

    public void setTarget(Post post){
        put(KEY_TARGET, post);
    }

    public void setBody(String body){
        put(KEY_BODY, body);
    }
}
