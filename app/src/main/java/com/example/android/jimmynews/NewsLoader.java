package com.example.android.jimmynews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by utilizator12 on 01/09/2017.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private String mUrl;
    public NewsLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    public void onStartLoading(){
        forceLoad();
    }

    @Override
    public List<News> loadInBackground(){
        if(mUrl == null){
            return null;
        }

        return QueryUtils.extractNews(mUrl);
    }

}
