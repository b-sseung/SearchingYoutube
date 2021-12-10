package org.techtown.search.main_adapter;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.ResourceId;

public class TubeList {

    String title;
    String channal;
    String resId;
    String date;
    String url;

    public TubeList(String title, String channal, String resId, String date, String url){
        this.title = title;
        this.channal = channal;
        this.resId = resId;
        this.date = date;
        this.url = url;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getChannal(){
        return this.channal;
    }

    public void setChannale(String channal){
        this.channal = channal;
    }

    public String getResId(){
        return this.resId;
    }

    public void setImage(String res){
        this.resId = res;
    }

    public String getDate(){
        return this.date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url = url;
    }

}
