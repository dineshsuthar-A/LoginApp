package com.example.googlelogin;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Userdata implements Parcelable {
    String name=new String();
    String email= new String();
    String id= new String();
    Uri personPhoto;
    String loggedby = new String();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(id);
        dest.writeString(loggedby);
        if(personPhoto!=null)
        dest.writeString(personPhoto.toString());

    }

    public static final Parcelable.Creator<Userdata> CREATOR
            = new Parcelable.Creator<Userdata>() {
        public Userdata createFromParcel(Parcel in) {
            return new Userdata(in);
        }

        public Userdata[] newArray(int size) {
            return new Userdata[size];
        }
    };

     Userdata(Parcel in) {
        name = in.readString();
        email = in.readString();
        id= in.readString();
        loggedby  = in.readString();
        String a = in.readString();
        if(a != null) {
            personPhoto = Uri.parse(a);
        }

    }

    Userdata(){}

}
