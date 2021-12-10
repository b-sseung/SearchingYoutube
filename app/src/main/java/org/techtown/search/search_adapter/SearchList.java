package org.techtown.search.search_adapter;

public class SearchList {

    int id;
    String title;
    String keyword;
    String array;
    String num;
    String date_start;
    String date_end;
    String main;

    public SearchList(int _id, String title, String keyword, String array, String num, String date_start, String date_end, String main){
        this.id = _id;
        this.title = title;
        this.keyword = keyword;
        this.array = array;
        this.num = num;
        this.date_start = date_start;
        this.date_end = date_end;
        this.main = main;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getKeyword(){
        return this.keyword;
    }

    public void setKeyworde(String keyword){
        this.keyword = keyword;
    }

    public String getArray(){
        return this.array;
    }

    public void setArray(String array){
        this.array = array;
    }

    public String getNum(){
        return this.num;
    }

    public void setNum(String num){
        this.num = num;
    }

    public String getDate_start(){
        return this.date_start;
    }

    public void setDate_start(String date){
        this.date_start = date;
    }

    public String getDate_end(){
        return this.date_end;
    }

    public void setDate_end(String date){
        this.date_end = date;
    }

    public String getMain(){
        return this.main;
    }

    public void setMain(String date){
        this.main = date;
    }

}
