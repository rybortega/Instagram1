package com.codepath.teleroid.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post") //Matches the name of the Parse entity
public class Post extends ParseObject {

    //Keys for the names of each of the attributes of the Parse entity.
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public void setImage(ParseFile file){
        put(KEY_IMAGE, file);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }


}