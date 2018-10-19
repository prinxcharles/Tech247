package com.example.android.tech247;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * An {@link TechNewAdapter} knows how to create a list item layout for each news
 * in the data source (a list of {@link TechNewAdapter} objects).
 *
 * These list item layouts will be provided to an adapter view like listView
 * to be displayed to the user
 */
public class TechNewAdapter extends ArrayAdapter {

    /**
     * The texts that precedes the author's name
     */
    private static final String AUTHOR_PRECEDENT = "By ";
    // Texts that is used to separate the date from time
    private static final String DATE_SEPARATOR = "T";
    // Texts that is used to isolate the time
    private static final String TIME_SEPARATOR = "Z";

    /**
     * Constructs a new TechNewsAdapter
     *
     * @param context of the app
     * @param techNews is the list of technews which is the data source of the adapter
     */
    public TechNewAdapter( Context context, List<TechNews> techNews) {
        super(context,0 ,techNews);
    }

    /**
     * Returns a list item view that displays information tech news
     *
     * @param position of the item in the list
     * @param convertView view to be recycled
     * @param parent viewGroup
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View  listView = convertView;
        // Check if an existing view is being reused, otherwise inflate the view
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.tech_list_item, parent, false);
        }

        // Get the data item for this position
        TechNews currentTechNews = (TechNews) getItem(position);
        // Lookup view for data population
        TextView headlineView = listView.findViewById(R.id.headline);
        TextView sectionView = listView.findViewById(R.id.section);
        TextView authorView = listView.findViewById(R.id.author);
        TextView dateView = listView.findViewById(R.id.date);
        TextView timeView = listView.findViewById(R.id.time);
        ImageView imageView = listView.findViewById(R.id.image_resource);

        // Populate the data into the template view using the data object
       String headline = currentTechNews.getmHeadline();
        // Set the headline to the headlineView
        headlineView.setText(headline);

        // Populate the data into the template view using the data object
        String section = currentTechNews.getmSection();
        // Set the section name to the sectionView
        sectionView.setText(section);

        String author = currentTechNews.getmAuthor();
        // Set the author name to the author View
        // could be "anonymous" OR "By Author's Name"
        if (author.equals("anonymous")){
            authorView.setText(author);
        } else {
            author = AUTHOR_PRECEDENT + author;
            authorView.setText(author);
        }


        // Populate the data into the template view using the data object
        String originalPublishedDate = currentTechNews.getmPublishedDate();
        // The original date string (i.e. "2018-10-17T05:27Z") contains
        // a date (2018-10-17) and a time (05:27)
        // then store the date and time separately from the originalPublishedDtae in 2 Strings,
        // so they can be displayed in 2 TextViews.
        String primaryDate;
        String originalTime;
        String primaryTime;
        // Split the string into different parts (as an array of Strings)
        // based on the "T" text. We expect an array of 2 Strings, where
        // the first String will be "2018-10-17" and the second String will be "05:27Z".
        String[] dateParts = originalPublishedDate.split(DATE_SEPARATOR);
        // date should take the first part of the split
        primaryDate = dateParts[0];
        // time concatenated with Z should take the second part of the split
        originalTime = dateParts[1];
        // Split the time string into different parts (as an array of Strings)
        // based on the "Z" text. We expect an array of 2 Strings, where
        // the first String will be "05:27" and the second String will be "".
        String[] timeParts = originalTime.split(TIME_SEPARATOR);
        primaryTime = timeParts[0];

        // Set the isolated date to the dateView
        dateView.setText(primaryDate);

        // Set the isolated to the timeView
        timeView.setText(primaryTime);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String showImage = sharedPrefs.getString(
                getContext().getString(R.string.settings_show_thumbnail_key),
                getContext().getString(R.string.settings_show_thumbnail_default));

        if (showImage.equals(getContext().getString(R.string.settings_show_thumbnail_false_value))) {
            // Hide image because the user does not want images
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            // Get an image from the thumbnail
            Glide.with(getContext())
                    .load(currentTechNews.getImageResource())
                    // placeholder image till the image loads
                    .placeholder(R.drawable.ic_loading_image)
                    // image to be displayed if image load was not successful
                    .error(R.drawable.image_not_found)
                    .into(imageView);
        }
        // Return the completed view to render on screen
        return listView;

    }

}
