package com.example.android.jimmynews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by utilizator12 on 01/09/2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        ViewHolder holder;
        News currentNews = getItem(position);

        //TODO: remove the debugging logs


        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder)listItemView.getTag();
        }

        holder.title.setText(currentNews.getTitle());
        holder.author.setText(currentNews.getAuthor());
        holder.category.setText(currentNews.getCategory());
        String inputDate = currentNews.getPublishDate();
        try{
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(inputDate);
            String outputDate = new SimpleDateFormat("MMM dd, yyyy").format(date);
            holder.publishDate.setText(outputDate);
        } catch (ParseException e){
            Log.e(LOG_TAG, "Encountered a problem while parsing the date");
        }
        holder.image.setImageBitmap(currentNews.getImage());

        /**
         * Open the story in the user's browser when an article is clicked on
         */
        final String urlString = currentNews.getUrl();
        listItemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                getContext().startActivity(intent);
            }
        });

        return listItemView;
    }

    static class ViewHolder {
        @BindView(R.id.article_image_view)
        ImageView image;
        @BindView(R.id.article_title_text_view)
        TextView title;
        @BindView(R.id.author_name_text_view)
        TextView author;
        @BindView(R.id.category_text_view)
        TextView category;
        @BindView(R.id.publish_date_text_view)
        TextView publishDate;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
