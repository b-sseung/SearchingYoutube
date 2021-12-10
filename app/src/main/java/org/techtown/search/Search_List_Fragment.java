package org.techtown.search;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.search.search_adapter.OnSearchClickListener;
import org.techtown.search.search_adapter.SearchAdapter;
import org.techtown.search.search_adapter.SearchList;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class Search_List_Fragment extends Fragment {

    RecyclerView dbcycler;
    SearchAdapter adapter;
    ItemTouchHelper helper;

    LinearLayout layout;
    FrameLayout allButtons;
    LinearLayout buttonsLayout;
    Button mainButton;
    Button subButton;
    Button selectButton;

    static TextView title;
    static TextView keyword;
    static TextView array;
    static TextView num;
    static TextView date_start;
    static TextView date_end;

    String s_q;
    String s_order;
    String s_num;
    String s_keyword;
    String s_start;
    String s_end;

    boolean changeColor = false;
    ArrayList<Integer> changeId = new ArrayList<Integer>();
    ArrayList<SearchAdapter.ViewHolder> holders = new ArrayList<SearchAdapter.ViewHolder>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.search_list_fragment, container, false);

        dbcycler = rootView.findViewById(R.id.search_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        dbcycler.setLayoutManager(layoutManager);
        adapter = new SearchAdapter();
        dbcycler.setAdapter(adapter);

        helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        helper.attachToRecyclerView(dbcycler);

        title = rootView.findViewById(R.id.titleText);
        keyword = rootView.findViewById(R.id.keywordText);
        array = rootView.findViewById(R.id.arrayText);
        num = rootView.findViewById(R.id.numText);
        date_start = rootView.findViewById(R.id.startText);
        date_end = rootView.findViewById(R.id.endText);

        adapter.setOnItemClickListener(new OnSearchClickListener() {
            @Override
            public void onItemClick(SearchAdapter.ViewHolder holder, View view, int position) {
                Log.d("2222", "클릭함 444");
                int a = 0;

                if (changeColor){
                    for (int i = 0; i < changeId.size(); i++){
                        if (adapter.getItem(position).getId() == changeId.get(i)){
                            changeId.remove(i);

                            a = 1;
                        }
                    }

                    if(a == 0){
                        holder.changeColor();
                        holders.add(holder);
                        changeId.add(adapter.getItem(position).getId());
                    } else if (a == 1){
                        holder.returnColor();
                    }

                    Log.d("2222", String.valueOf(changeId.size()));

                    Log.d("2222", String.valueOf(changeId));

                } else {
                    int id = adapter.getItem(position).getId();
                    Intent intent = new Intent(getContext(), Dialog_Activity.class);
                    intent.putExtra("1818", id);
                    Log.d("2828", "id : " + id);
                    startActivityForResult(intent, 102);
                }
            }
        });

        Button AllDelete = rootView.findViewById(R.id.allDeleteButton);
        AllDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllList();
            }
        });

        buttonsLayout = rootView.findViewById(R.id.buttons);

        selectButton = rootView.findViewById(R.id.selectButton);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor = true;
                buttonsLayout.setVisibility(View.VISIBLE);
                v.setVisibility(View.INVISIBLE);

            }
        });

        Button modifyButton = rootView.findViewById(R.id.modifyButton);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeId.size() == 1) {
                    SearchList item = null;
                    for (int i = 0; i < adapter.getItemCount(); i++){
                        if (adapter.getItem(i).getId() == changeId.get(0)){
                            item = adapter.getItem(i);
                        }
                    }

                    holders.get(0).returnColor();

                    int id = item.getId();
                    String[] searchText = new String[]{item.getTitle(), item.getArray(), item.getNum(), item.getKeyword(), item.getDate_start(), item.getDate_end()};

                    MainActivity.reSearching(id, searchText);

                    changeId.clear();
                    holders.clear();

                    changeColor = false;
                    v.setVisibility(View.VISIBLE);
                    buttonsLayout.setVisibility(View.INVISIBLE);
                    selectButton.setVisibility(View.VISIBLE);
                } else if (changeId.size() == 0){
                    Toast.makeText(getContext(), "한 가지 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if (changeId.size() > 1){
                    v.setVisibility(View.INVISIBLE);
                }

            }
        });

        Button cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor = false;
                buttonsLayout.setVisibility(View.INVISIBLE);
                selectButton.setVisibility(View.VISIBLE);
            }
        });


        Button deleteButton = rootView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < changeId.size(); i++){

                    deleteOneList(changeId.get(i));
                    holders.get(i).returnColor();
                }
                changeId.clear();
                holders.clear();

                changeColor = false;
                buttonsLayout.setVisibility(View.INVISIBLE);
                selectButton.setVisibility(View.VISIBLE);

            }
        });

        mainButton = rootView.findViewById(R.id.mainButton);
        subButton = rootView.findViewById(R.id.subButton);
        layout = rootView.findViewById(R.id.layout);
        allButtons = rootView.findViewById(R.id.allButtons);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.VISIBLE);
                dbcycler.setVisibility(View.INVISIBLE);
                allButtons.setVisibility(View.INVISIBLE);
                v.setVisibility(View.INVISIBLE);
                subButton.setVisibility(View.VISIBLE);
            }
        });
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.INVISIBLE);
                dbcycler.setVisibility(View.VISIBLE);
                allButtons.setVisibility(View.VISIBLE);
                mainButton.setVisibility(View.VISIBLE);
                v.setVisibility(View.INVISIBLE);
            }
        });


        return rootView;
    }

    protected void setUpRecyclerView(){
        dbcycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                helper.onDraw(c,parent, state);
            }
        });
    }


    public int loadNoteListData(){

        println("loadSearchListData called.");
        String sql = "select _id, SEARCH_Q, SEARCH_ORDER, SEARCH_MAX, SEARCH_Q_MINUS, SEARCH_START, SEARCH_END, SEARCH_MAIN from "
                + ListDatabase.TABLE_LIST + " order by _id desc";
        int recordCount = -1;
        ListDatabase database = ListDatabase.getInstance(getContext());
        if (database != null){
            Cursor outCursor = database.rawQuery(sql);

            recordCount = outCursor.getCount();
            println("record count : " + recordCount + "\n");

            ArrayList<SearchList> items = new ArrayList<SearchList>();

            for (int i = 0; i < recordCount; i++){
                outCursor.moveToNext();
                println("record count : " + recordCount + "\n");

                int _id = outCursor.getInt(0);
                String search_q = outCursor.getString(1);
                String search_order = outCursor.getString(2);
                String search_max = outCursor.getString(3);
                String search_q_minus = outCursor.getString(4);
                String search_start = outCursor.getString(5);
                String search_end = outCursor.getString(6);
                String search_main = outCursor.getString(7);
                items.add(new SearchList(_id, search_q, search_q_minus, search_order, search_max, search_start, search_end, search_main));
            }

            outCursor.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();

        }
        return recordCount;
    }

    protected void saveList(){

        String sql = "insert into " + ListDatabase.TABLE_LIST + "(SEARCH_Q, SEARCH_ORDER, SEARCH_MAX, SEARCH_Q_MINUS, SEARCH_START, SEARCH_END, SEARCH_MAIN) values("
                + "'" + MainActivity.editText.getText().toString() + "', " + "'" + MainActivity.spinner_order_text + "', " + "'" + MainActivity.spinner_max_text + "', "
                + "'" + MainActivity.editText_except.getText().toString() + "', " + "'" + MainActivity.textView_start.getText().toString() + "', "
                + "'" + MainActivity.textView_end.getText().toString() + "', " + "'" + MainActivity.textView_main + "')";

        println("sql : " + sql);
        ListDatabase database = ListDatabase.getInstance(getContext());
        database.execSQL(sql);

        if (MainActivity.textView_main == "기본"){
            title.setText(MainActivity.editText.getText().toString());
            keyword.setText(MainActivity.editText_except.getText().toString());
            array.setText(SearchAdapter.checkOrder(MainActivity.spinner_order_text));
            num.setText(MainActivity.spinner_max_text);
            date_start.setText(MainActivity.textView_start.getText().toString());
            date_end.setText(MainActivity.textView_end.getText().toString());
        }
    }

    protected void modifyOneList(int id){
        String sql = "update " + ListDatabase.TABLE_LIST + " set " + " SEARCH_Q = '" + MainActivity.editText.getText().toString() + "'" +
                " ,SEARCH_ORDER = '" + MainActivity.spinner_order_text + "'" + " ,SEARCH_MAX = '" + MainActivity.spinner_max_text + "'"
                + " ,SEARCH_Q_MINUS = '" + MainActivity.editText_except.getText().toString() + "'" + " ,SEARCH_START = '"
                + MainActivity.textView_start.getText().toString() + "'" + " ,SEARCH_END = '" + MainActivity.textView_end.getText().toString() + "'"
                + " where " + " _id = " + id;

        ListDatabase database = ListDatabase.getInstance(getContext());
        database.execSQL(sql);

        loadNoteListData();
    }

    protected void modifyOneList(){
        String sql = "update " + ListDatabase.TABLE_LIST + " set " +  " SEARCH_MAIN = '" + "'" + " where " + " SEARCH_MAIN = " + "'기본'";

        ListDatabase database = ListDatabase.getInstance(getContext());
        database.execSQL(sql);
    }

    protected void deleteAllList(){
        println("deleteList called");


        String sql = "delete from " + ListDatabase.TABLE_LIST;

        ListDatabase database = ListDatabase.getInstance(getContext());
        database.execSQL(sql);

        title.setText("");
        keyword.setText("");
        array.setText("");
        num.setText("");
        date_start.setText("");
        date_end.setText("");

        ArrayList<SearchList> items = new ArrayList<SearchList>();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    protected void deleteOneList(int id){

        String sql = "delete from " + ListDatabase.TABLE_LIST + " where " + " _id = " + id;

        ListDatabase database = ListDatabase.getInstance(getContext());
        database.execSQL(sql);

        loadNoteListData();
    }

    protected void println(String msg){
        Log.d("2020", msg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102){
            if (data != null){
                int id = data.getIntExtra("1818", 0);

                for (int i = 0; i < adapter.getItemCount(); i++){
                    if (adapter.getItem(i).getId() == id){
                        s_q = adapter.getItem(i).getTitle();
                        s_order = adapter.getItem(i).getArray();
                        s_num = adapter.getItem(i).getNum();
                        s_keyword = adapter.getItem(i).getKeyword();
                        s_start = adapter.getItem(i).getDate_start();
                        s_end = adapter.getItem(i).getDate_end();
                    }
                }

                if (resultCode == RESULT_OK){
                    modifyOneList();

                    String sql = "update " + ListDatabase.TABLE_LIST + " set " + " SEARCH_Q = '" + s_q + "'" +
                            " ,SEARCH_ORDER = '" + s_order + "'" + " ,SEARCH_MAX = '" + s_num + "'"
                            + " ,SEARCH_Q_MINUS = '" + s_keyword + "'" + " ,SEARCH_START = '"
                            + s_start + "'" + " ,SEARCH_END = '" + s_end + "'"
                            + " ,SEARCH_MAIN = '" + "기본" + "'"
                            + " where " + " _id = " + id;

                    ListDatabase database = ListDatabase.getInstance(getContext());
                    database.execSQL(sql);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            title.setText(s_q);
                            keyword.setText(s_keyword);
                            array.setText(SearchAdapter.checkOrder(s_order));
                            num.setText(s_num);
                            date_start.setText(s_start);
                            date_end.setText(s_end);
                        }
                    });

                } else {

                }


                MainActivity.reSearching(new String[]{s_q, s_order, s_num, s_keyword, s_start, s_end});

                MainActivity.searchMethod();


                loadNoteListData();

                MainActivity.searchLayout.setVisibility(View.INVISIBLE);
            }


        }
    }
}
