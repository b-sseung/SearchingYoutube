package org.techtown.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.api.services.youtube.model.Thumbnail;

import org.techtown.search.main_adapter.ListAdapter;
import org.techtown.search.main_adapter.OnListClickListener;
import org.techtown.search.main_adapter.TubeList;

public class Search_Fragment extends Fragment {

    static Button prevButton;
    static boolean prevClick = false;
    static Button nextButton;
    static boolean nextClick = false;

    static Thumbnail thumbnail;
    static ListAdapter adapter;
    static RecyclerView recycler;
    static int spinner_max_num;

    private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.search_fragment, container, false);


        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        recycler = rootView.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        adapter = new ListAdapter();
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnListClickListener() {
            @Override
            public void onItemClick(ListAdapter.ViewHolder holder, View view, int position) {
                TubeList item = adapter.getItem(position);
                String url = "https://www.youtube.com/watch?v=" + item.getUrl();

                Youtube_Fragment.webView.loadUrl(url);

                MainActivity.pager.setCurrentItem(1);

            }
        });

        prevButton = rootView.findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevClick = true;
                spinner_max_num--;
                adapter.clear();
                MainActivity.YoutubeAsyncTask youtubeAsyncTask = new MainActivity.YoutubeAsyncTask();
                youtubeAsyncTask.execute();
            }
        });

        nextButton = rootView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextClick = true;
                spinner_max_num++;
                adapter.clear();
                MainActivity.YoutubeAsyncTask youtubeAsyncTask = new MainActivity.YoutubeAsyncTask();
                youtubeAsyncTask.execute();
            }
        });

        return rootView;
    }
}
