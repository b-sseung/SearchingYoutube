package org.techtown.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import org.techtown.search.main_adapter.TubeList;
import org.techtown.search.search_adapter.SearchAdapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected static String API_KEY = "AIzaSyBPJxqALGalE6FjiewC9jaL3otAuhYQ_sY";
    protected static String result;
    protected static SearchListResponse searchResponse;

    static LinearLayout searchLayout;

    protected static String searchtext;
    protected static String spinner_order_text;
    protected static String spinner_max_text;
    protected static String prevText;
    protected static String nextText;

    Toolbar toolbar;

    protected static EditText editText;
    protected static EditText editText_except;

    protected static DateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
    protected static Handler handler = new Handler();

    public static Context context;

    String[] spinner_item1;

    String[] spinner_item2;

    protected static Spinner spinner_order;
    protected static Spinner spinner_max;

    TextView todayText;
    TextView weekText;
    TextView monthText;
    TextView yearText;

    DatePickerDialog picker;
    protected static TextView textView_start;
    protected static TextView textView_end;
    protected static DateTime startText;
    protected static DateTime endText;
    static Date stringDate;
    static Date stringDate2;
    boolean dateValue;
    static DateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

    static Calendar calendar;

    protected static ViewPager pager;
    protected static Search_Fragment search_f;
    protected static Youtube_Fragment youtube_f;
    Search_List_Fragment search_list_f;
    static Button modifyButton;
    static int _id = 0;
    static FrameLayout blackLayout;

    protected static String textView_main;

    private InterstitialAd mInterstitialAd;
    int adNum = 0;

    private View 	decorView;
    private int	uiOption;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        //하단 홈버튼 표시할지 말지
//        decorView = getWindow().getDecorView();
//        uiOption = getWindow().getDecorView().getSystemUiVisibility();
//        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
//            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
////        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
////            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
//        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
//            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        context = getApplicationContext();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //테스트용
//        mInterstitialAd.setAdUnitId("ca-app-pub-8631957304793435/6481984075");

//        전면 광고 단위 아이디 ca-app-pub-8631957304793435/6481984075
//        테스트용 전면 광고 단위 아이디 ca-app-pub-3940256099942544/1033173712


        spinner_item1 = new String[]{getString(R.string.업데이트순), getString(R.string.정확도순), getString(R.string.평가순), getString(R.string.제목순),
                getString(R.string.조회수순)};
        spinner_item2 = new String[]{getString(R.string.개5), getString(R.string.개10), getString(R.string.개25), getString(R.string.개50)};

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.타이틀);
        setSupportActionBar(toolbar);

        search_list_f = new Search_List_Fragment();
        getSupportFragmentManager().beginTransaction().add(R.id.search_list_fragment, search_list_f).commit();

        searchLayout = findViewById(R.id.searchLayout);
        editText = findViewById(R.id.editText);
        editText_except = findViewById(R.id.editText_except);

        spinner_order = findViewById(R.id.spinner);
        ArrayAdapter<String> spinner_adapter_or = new ArrayAdapter<String>(this, R.layout.spinner_item, spinner_item1);
        spinner_order.setAdapter(spinner_adapter_or);

        spinner_max = findViewById(R.id.spinner2);
        ArrayAdapter<String> spinner_adapter_max = new ArrayAdapter<String>(this, R.layout.spinner_item, spinner_item2);
        spinner_max.setAdapter(spinner_adapter_max);

        pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        search_f = new Search_Fragment();
        youtube_f = new Youtube_Fragment();
        pagerAdapter.addItem(search_f);
        pagerAdapter.addItem(youtube_f);

        pager.setAdapter(pagerAdapter);

        calendar = Calendar.getInstance();
        picker = new DatePickerDialog(this, pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        picker.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        textView_start = findViewById(R.id.textView_start);
        textView_end = findViewById(R.id.textView_end);

        textView_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateValue = true;
                picker.show();

            }
        });
        textView_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateValue = false;
                picker.show();
            }
        });


        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                Intent intent = new Intent(getApplicationContext(), Dialog_Activity.class);
                startActivityForResult(intent, 101);
            }
        });

        findViewById(R.id.resetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_start.setText("");
                textView_end.setText("");
            }
        });

        blackLayout = findViewById(R.id.blackLayout);

        modifyButton = findViewById(R.id.modify_search);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blackLayout.setVisibility(View.INVISIBLE);
                search_list_f.modifyOneList(_id);
                _id = 0;
                v.setVisibility(View.INVISIBLE);
            }
        });


        spinner_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 :
                        spinner_order_text = "date";
                        break;
                    case 1 :
                        spinner_order_text = "relevance";
                        break;
                    case 2 :
                        spinner_order_text = "rating";
                        break;
                    case 3:
                        spinner_order_text = "title";
                        break;
                    case 4 :
                        spinner_order_text = "viewCount";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner_order_text = "date";
            }
        });

        spinner_max.setSelection(3);

        spinner_max.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 :
                        spinner_max_text = "5";
                        break;
                    case 1 :
                        spinner_max_text = "10";
                        break;
                    case 2 :
                        spinner_max_text = "25";
                        break;
                    case 3 :
                        spinner_max_text = "50";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner_max.setSelection(3);
                spinner_max_text = "50";
            }
        });

        todayText = findViewById(R.id.todayText);
        todayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dateChange(1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        weekText = findViewById(R.id.weekText);
        weekText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dateChange(2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        monthText = findViewById(R.id.monthText);
        monthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dateChange(3);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        yearText = findViewById(R.id.yearText);
        yearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dateChange(4);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        loadData();

    }

    protected void dateChange(int num) throws ParseException {
        Calendar todayDate = Calendar.getInstance();
        int year = todayDate.get(Calendar.YEAR);
        int month = todayDate.get(Calendar.MONTH) + 1;
        int day = todayDate.get(Calendar.DAY_OF_MONTH);

        String endT = year + "-" + month + "-" + (day + 1);
        textView_end.setText(year + "-" + month + "-" + day);
        endText = DateTime.parseRfc3339(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(
                transFormat.parse(endT)));

        switch (num){
            case 1:
                break;
            case 2:
                day = todayDate.get(Calendar.DAY_OF_MONTH) - 7;
                break;
            case 3:
                month = todayDate.get(Calendar.MONTH);
                break;
            case 4:
                year = todayDate.get(Calendar.YEAR) - 1;
                break;
        }
        String startT = year + "-" + month + "-" + day;
        textView_start.setText(startT);
        startText = DateTime.parseRfc3339(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(
                transFormat.parse(startT)));
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (dateValue){
                textView_start.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
            } else {
                textView_end.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth );
            }
        }
    };

    protected static class YoutubeAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
                final JsonFactory JSON_FACTORY = new JacksonFactory();

                YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {

                    }
                }).setApplicationName("SearchingYoutube").build();

                YouTube.Search.List search = youtube.search().list("snippet");

                search.setKey(API_KEY);


                search.setQ(searchtext);
                Log.d("1919", searchtext);
                search.setOrder(spinner_order_text); //date relevance
//                search.setChannelId("UCwW6D9G9hegNPKYrQ0zivvQ");
                search.setType("video");
                search.setMaxResults(Long.valueOf(spinner_max_text));

                if (search_f.prevClick){
                    search.setPageToken(prevText);
                    search_f.prevClick = false;
                }

                if (search_f.nextClick) {
                    search.setPageToken(nextText);
                    search_f.nextClick = false;
                }

                if (startText != null){
                    search.setPublishedAfter(startText);
                }
                if (endText != null){
                    search.setPublishedBefore(endText);
                }

                searchResponse = search.execute();

                if (searchResponse.getPrevPageToken() != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            search_f.prevButton.setVisibility(View.VISIBLE);
                            prevText = searchResponse.getPrevPageToken();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            search_f.prevButton.setVisibility(View.INVISIBLE);
                            prevText = "";
                        }
                    });
                }

                if (searchResponse.getNextPageToken() != null){
                    if (searchResponse.getPageInfo().getTotalResults() - 1 != Integer.valueOf(spinner_max_text)*(search_f.spinner_max_num)){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                search_f.nextButton.setVisibility(View.VISIBLE);
                                nextText = searchResponse.getNextPageToken();
                            }
                        });
                    } else {
                        Log.d("1919", "같음");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                search_f.nextButton.setVisibility(View.INVISIBLE);
                                nextText = "";
                            }
                        });
                    }

                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            search_f.nextButton.setVisibility(View.INVISIBLE);
                            nextText = "";
                        }
                    });
                }

                List<SearchResult> searchResultList = searchResponse.getItems();

                if (searchResultList != null) {
                    prettyPrint(searchResultList.iterator(), searchtext);
                }

            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
                System.err.println("There was a service error 2: " + e.getLocalizedMessage() + " , " + e.toString());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        public void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

            StringBuilder sb = new StringBuilder();

            while (iteratorSearchResults.hasNext()) {
                SearchResult video = iteratorSearchResults.next();
                ResourceId rId = video.getId();

                // Double checks the kind is video.
                if (rId.getKind().equals("youtube#video")) {
                    search_f.thumbnail = (Thumbnail) video.getSnippet().getThumbnails().get("default");
                    sb.append("제목 : " + video.getSnippet().getTitle() + " , 채널명 : " + video.getSnippet().getChannelTitle() +
                            " , 썸네일 주소 : " + search_f.thumbnail.getUrl() + " , 게시일 : " + video.getSnippet().getPublishedAt() +
                            " , url : " + video.getId().getVideoId());
                    sb.append("\n");
                    Log.d("1818", String.valueOf(sb));
                    search_f.adapter.addItem(new TubeList(video.getSnippet().getTitle(), video.getSnippet().getChannelTitle(), search_f.thumbnail.getUrl(),
                        dateFormat.format(video.getSnippet().getPublishedAt().getValue()), video.getId().getVideoId()));
                }
            }

            result = sb.toString();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    search_f.recycler.setAdapter(search_f.adapter);
                }
            });
        }
    }

    class PagerAdapter extends FragmentStatePagerAdapter{

        ArrayList<Fragment> items = new ArrayList<Fragment>();
        public PagerAdapter(FragmentManager fm){
            super(fm);
        }

        public void addItem(Fragment item){
            items.add(item);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId){
            case R.id.menu_search :
                if (searchLayout.getVisibility() == View.INVISIBLE){
                    searchLayout.setVisibility(View.VISIBLE);
                    search_list_f.loadNoteListData();

                    editText.setText("");
                    editText_except.setText("");
                    spinner_order.setSelection(0);
                    spinner_max.setSelection(3);
                    textView_start.setText("");
                    textView_end.setText("");

                } else {
                    searchLayout.setVisibility(View.INVISIBLE);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void reSearching(int id, String[] strings){
        blackLayout.setVisibility(View.VISIBLE);
        modifyButton.setVisibility(View.VISIBLE);
        _id = id;
        editText.setText(strings[0]);
        spinner_order_text = strings[1];
        spinner_max_text = strings[2];
        editText_except.setText(strings[3]);
        textView_start.setText(strings[4]);
        textView_end.setText(strings[5]);

        int order = 0;
        switch(strings[1]){
            case "date" :
                order = 0;
                break;
            case "relevance" :
                order = 1;
                break;
            case "rating" :
                order = 2;
                break;
            case "title" :
                order = 3;
                break;
            case "viewCount" :
                order = 4;
                break;
            }
        spinner_order.setSelection(order);

        int max = 0;
        switch (strings[2]){
            case "5" :
                max = 0;
                break;
            case "10" :
                max = 1;
                break;
            case "25" :
                max = 2;
                break;
            case "50" :
                max = 3;
                break;
        }
        spinner_max.setSelection(max);

    }

    public static void reSearching(String[] strings){
        editText.setText(strings[0]);
        spinner_order_text = strings[1];
        spinner_max_text = strings[2];
        editText_except.setText(strings[3]);
        textView_start.setText(strings[4]);
        textView_end.setText(strings[5]);

        int order = 0;
        switch(strings[1]){
            case "date" :
                order = 0;
                break;
            case "relevance" :
                order = 1;
                break;
            case "rating" :
                order = 2;
                break;
            case "title" :
                order = 3;
                break;
            case "viewCount" :
                order = 4;
                break;
        }
        spinner_order.setSelection(order);

        int max = 0;
        switch (strings[2]){
            case "5" :
                max = 0;
                break;
            case "10" :
                max = 1;
                break;
            case "25" :
                max = 2;
                break;
            case "50" :
                max = 3;
                break;
        }
        spinner_max.setSelection(max);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }

            if (resultCode == RESULT_OK){
                search_list_f.modifyOneList();
                textView_main = "기본";
            } else {
                textView_main = "";
            }

            searchLayout.setVisibility(View.INVISIBLE);
            search_f.spinner_max_num = 1;

            searchMethod();

            search_list_f.saveList();

            editText.setText("");
            editText_except.setText("");
            spinner_order.setSelection(0);
            spinner_max.setSelection(3);
            textView_start.setText("");
            textView_end.setText("");
        }
    }

    public void loadData(){

        String sql = "select _id, SEARCH_Q, SEARCH_ORDER, SEARCH_MAX, SEARCH_Q_MINUS, SEARCH_START, SEARCH_END, SEARCH_MAIN from "
                + ListDatabase.TABLE_LIST + " " + "where SEARCH_MAIN = " + "'기본'";

        int recordCount = -1;

        ListDatabase database = ListDatabase.getInstance(this);

        if (database != null){

            Cursor cursor = database.rawQuery(sql);
            recordCount = cursor.getCount();

            if (recordCount == 0){
                searchLayout.setVisibility(View.VISIBLE);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        search_list_f.loadNoteListData();
                    }
                });

            } else {
                for (int i = 0; i < recordCount; i++){
                    cursor.moveToNext();

                    int _id = cursor.getInt(0);
                    final String search_q = cursor.getString(1);
                    final String search_order1 = cursor.getString(2);
                    final String search_order2 = SearchAdapter.checkOrder(cursor.getString(2));
                    final String search_max = cursor.getString(3);
                    final String search_q_minus = cursor.getString(4);
                    final String search_start = cursor.getString(5);
                    final String search_end = cursor.getString(6);
                    String search_main = cursor.getString(7);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            search_list_f.title.setText(search_q);
                            search_list_f.keyword.setText(search_q_minus);
                            search_list_f.array.setText(search_order2);
                            search_list_f.num.setText(search_max);
                            search_list_f.date_start.setText(search_start);
                            search_list_f.date_end.setText(search_end);
                        }
                    });


                    reSearching(new String[]{search_q, search_order1, search_max, search_q_minus, search_start, search_end});

                    searchMethod();

                    Log.d("2020", "cursor : " + recordCount + _id + search_q + search_order1 + search_max + search_q_minus + search_start + search_end + search_main);

                }
            }

        }

    }

    protected static void setTextDate(){

        Date date = null;

        if (!textView_start.getText().toString().equals("")){
            try {
                stringDate = transFormat.parse(textView_start.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startText = DateTime.parseRfc3339(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(stringDate));
            Log.d("1919", String.valueOf(startText));
        } else {
            startText = null;
        }

        if (!textView_end.getText().toString().equals("")){
            try {
                stringDate2 = transFormat.parse(textView_end.getText().toString());
                date = new Date(stringDate2.getYear(), stringDate2.getMonth(), stringDate2.getDate() + 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            endText = DateTime.parseRfc3339(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date));
            Log.d("1919", String.valueOf(endText));
        } else {
            endText = null;
        }

    }

    static void searchMethod(){

        setTextDate();

        if (startText != null){
            if (endText == null){
                textView_end.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1)
                        + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            }
        }

        if (Search_Fragment.adapter != null){
            Search_Fragment.adapter.clear();
        }

        if (!editText_except.getText().toString().equals("")){
            String str = editText_except.getText().toString();
            str = str.replace(" ", "");
            str = str.replace(",", " -");
            searchtext = editText.getText().toString() + " -" + str;
        } else {
            searchtext = editText.getText().toString();
        }
        YoutubeAsyncTask youtubeAsyncTask = new YoutubeAsyncTask();
        youtubeAsyncTask.execute();
    }

    @Override
    public void onBackPressed() {

        if (searchLayout.getVisibility() == View.VISIBLE){
            searchLayout.setVisibility(View.INVISIBLE);
        } else if (searchLayout.getVisibility() == View.INVISIBLE){
            finish();
        }
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        // TODO Auto-generated method stub
//        // super.onWindowFocusChanged(hasFocus);
//
//        if( hasFocus ) {
//            decorView.setSystemUiVisibility( uiOption );
//        }
//    }

}