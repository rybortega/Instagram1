package com.codepath.teleroid.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject{

    //TODO: Test getters to see accesibility to Parse Like Table.

    //Keys for the names of each of the attributes of the Parse entity.
    public static final String KEY_TARGET = "target";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_TIME = "createdAt";

    public Like(){
        //empty constructor needed by Parceler & Parce
    }

    public ParseUser getAuthor(){
        return getParseUser(KEY_AUTHOR);
    }

    public Post getTarget(){
        return (Post) get(KEY_TARGET);
    }

    public void setAuthor(ParseUser author){
        put(KEY_AUTHOR, author);
    }

    public void setTarget(Post post){
        put(KEY_TARGET, post);
    }
}
