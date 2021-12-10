package org.techtown.search.search_adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.search.ItemTouchHelperListener;
import org.techtown.search.ListDatabase;
import org.techtown.search.MainActivity;
import org.techtown.search.R;
import org.techtown.search.Search_List_Fragment;
import org.techtown.search.search_adapter.OnSearchClickListener;

import java.text.DateFormat;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements OnSearchClickListener, ItemTouchHelperListener {

    ArrayList<SearchList> items = new ArrayList<SearchList>();
    OnSearchClickListener listener;

    static ViewGroup parent;

    int id;
    String[] searchText;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        this.parent = parent;
        View itemView = inflater.inflate(R.layout.activity_search_list, parent, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchList item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(SearchList item){
        items.add(item);
    }

    public void setItems(ArrayList<SearchList> items){
        this.items = items;
    }

    public SearchList getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, SearchList item){
        items.set(position, item);
    }

    public void clear() {
        setItems(new ArrayList<SearchList>());
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {

        Log.d("2222", "onItemMove 호출");
        SearchList item = items.get(from_position);

        //이동할 객체 삭제
        items.remove(from_position);

        //이동하고 싶은 position에 추가
        items.add(to_position, item);

        //Adapter에 데이터 이동알림
        notifyItemMoved(from_position,to_position);

        return true;

    }

    @Override
    public void onItemSwipe(int position) {

        Log.d("2222", "onItemSwipe 호출");

        items.remove(position);
        notifyItemRemoved(position);
    }

    //수정할때
    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {
        SearchList item = items.get(position);
        id = item.getId();
        searchText = new String[]{item.getTitle(), item.getArray(), item.getNum(), item.getKeyword(), item.getDate_start(), item.getDate_end()};
        MainActivity.reSearching(id, searchText);
    }

    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {
        deleteOneList(items.get(position).getId());
        items.remove(position);
        notifyItemRemoved(position);
    }

    protected void deleteOneList(int id){

        String sql = "delete from " + ListDatabase.TABLE_LIST + " where " + " _id = " + id;

        ListDatabase database = ListDatabase.getInstance(parent.getContext());
        database.execSQL(sql);
    }

    public void setOnItemClickListener(OnSearchClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null){
            listener.onItemClick(holder, view, position);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout layout;
        TextView title;
        TextView keyword;
        TextView array;
        TextView num;
        TextView date_start;
        TextView date_end;

        public ViewHolder(@NonNull View itemView, final OnSearchClickListener listener) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            title = itemView.findViewById(R.id.titleText);
            keyword = itemView.findViewById(R.id.keywordText);
            array = itemView.findViewById(R.id.arrayText);
            num = itemView.findViewById(R.id.numText);
            date_start = itemView.findViewById(R.id.startText);
            date_end = itemView.findViewById(R.id.endText);

            returnColor();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (listener != null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setItem(SearchList item){

            checkText(item.getTitle(), title);
            checkText(item.getKeyword(), keyword);
            array.setText(checkOrder(item.getArray()));
            checkText(item.getNum(), num);
            checkText(item.getDate_start(), date_start);
            checkText(item.getDate_end(), date_end);

        }

        public void checkText(String data, TextView view){
            Log.d("2121", data + view);
            if (data.equals(null)){
                view.setText("");
            } else {
                view.setText(data);
            }
        }

        public void changeColor(){
            layout.setBackgroundResource(R.drawable.stroke2);
        }

        public void returnColor(){
            layout.setBackgroundResource(R.drawable.stroke);
        }

    }

    public static String checkOrder(String data){
        String order = "";

        switch(data){
            case "date" :
                order = MainActivity.context.getString(R.string.업데이트순);
                break;
            case "relevance" :
                order = MainActivity.context.getString(R.string.정확도순);
                break;
            case "rating" :
                order = MainActivity.context.getString(R.string.평가순);
                break;
            case "title" :
                order = MainActivity.context.getString(R.string.제목순);
                break;
            case "viewCount" :
                order = MainActivity.context.getString(R.string.조회수순);
                break;
        }
        return order;

    }


}
