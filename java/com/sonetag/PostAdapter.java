package com.sonetag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class is the post adapter. It allows to build a post to display it
 * @version 1
 * @author Benjamin BOURG
 */
public class PostAdapter extends BaseAdapter {

    /**
     * The app context
     */
    private final Context mContext;
    private final LayoutInflater mInflater;
    /**
     * The list of posts
     */
    private final ArrayList<JSONObject> listAccounts;

    /**
     * Constructor
     * @param context the page context
     * @param list the array of posts
     */
    public PostAdapter(Context context, ArrayList<JSONObject> list) {
        this.mContext = context;
        this.listAccounts = list;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return listAccounts.size();
    }

    @Override
    public Object getItem(int position) {
        return listAccounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Build the post
     * @param position post position in the list
     * @param convertView
     * @param parent
     * @return the build post
     */
    /**
     * This function is still under development to be optimized
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConstraintLayout layoutItem = null;

        try {
            String source = (String) listAccounts.get(position).get("source");
            JSONObject user = (JSONObject)listAccounts.get(position).get("user");

            if(source.equals("twitter")){
                if(listAccounts.get(position).has("media")){
                    if (convertView == null) {
                        layoutItem = (ConstraintLayout) mInflater.inflate(R.layout.post_layout_w_img_twitter, parent, false);
                    } else {
                        layoutItem = (ConstraintLayout) convertView;
                    }
                    ImageView img = layoutItem.findViewById(R.id.post_image);
                    if(listAccounts.get(position).get("media").getClass().toString().contains("String")){
                        new DownLoadImageTask(img).execute((String) listAccounts.get(position).get("media"));
                    }else if(listAccounts.get(position).get("media").getClass().toString().contains("JSON")){

                    }
                }else {
                    if (convertView == null) {
                        layoutItem = (ConstraintLayout) mInflater.inflate(R.layout.post_layout_no_img_twitter, parent, false);
                    } else {
                        layoutItem = (ConstraintLayout) convertView;
                    }
                }

                ImageView icon = layoutItem.findViewById(R.id.post_networkAvatar);
                icon.setImageResource(R.drawable.ic_twitter_icon);

                ImageView react = layoutItem.findViewById(R.id.post_reaction);
                if((boolean)listAccounts.get(position).get("favorited")){
                    react.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary));
                }else {
                    react.setColorFilter(ContextCompat.getColor(mContext, R.color.grey));
                }
                react.setTag(""+position);
                react.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        try {
                            reactionsTwitterDialog(v.getContext(), (boolean)listAccounts.get(position).get("favorited"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }else if(source.equals("reddit")){
                if (convertView == null) {
                    layoutItem = (ConstraintLayout) mInflater.inflate(R.layout.post_layout_no_img_reddit, parent, false);
                } else {
                    layoutItem = (ConstraintLayout) convertView;
                }

                ImageView icon = layoutItem.findViewById(R.id.post_networkAvatar);
                icon.setImageResource(R.drawable.ic_reddit_icon);
            }else if(source.equals("tumblr")){
                if(listAccounts.get(position).has("media")){
                    if (convertView == null) {
                        layoutItem = (ConstraintLayout) mInflater.inflate(R.layout.post_layout_w_img_twitter, parent, false);
                    } else {
                        layoutItem = (ConstraintLayout) convertView;
                    }
                    ImageView img = layoutItem.findViewById(R.id.post_image);
                    if(listAccounts.get(position).get("media").getClass().toString().contains("String")){
                        new DownLoadImageTask(img).execute((String) listAccounts.get(position).get("media"));
                    }else if(listAccounts.get(position).get("media").getClass().toString().contains("JSON")){

                    }
                }else {
                    if (convertView == null) {
                        layoutItem = (ConstraintLayout) mInflater.inflate(R.layout.post_layout_no_img_twitter, parent, false);
                    } else {
                        layoutItem = (ConstraintLayout) convertView;
                    }
                }

                ImageView icon = layoutItem.findViewById(R.id.post_networkAvatar);
                icon.setImageResource(R.drawable.ic_tumblr);


                ImageView react = layoutItem.findViewById(R.id.post_reaction);
                react.setTag(""+position);
                if((boolean)listAccounts.get(position).get("favorited")){
                    react.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary));
                }else {
                    react.setColorFilter(ContextCompat.getColor(mContext, R.color.grey));
                }
                react.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        try {
                            reactionsTwitterDialog(v.getContext(), (boolean)listAccounts.get(position).get("favorited"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            ImageView iconProfile = layoutItem.findViewById(R.id.post_accountAvatar);
            if(!user.get("profile_image_url_https").equals("")) {
                new DownLoadImageTask(iconProfile).execute((String) user.get("profile_image_url_https"));
            }else {
                iconProfile.setImageResource(R.drawable.ic_avatar);
            }

            TextView text = layoutItem.findViewWithTag("post_text");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                text.setText(Html.fromHtml((String) listAccounts.get(position).get("text"), Html.FROM_HTML_MODE_LEGACY));
            } else {
                text.setText(Html.fromHtml((String) listAccounts.get(position).get("text")));
            }
            text.setId(position);
            text.setMaxLines(3);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView)v.findViewById(v.getId());
                    if(tv.getMaxLines() == 3) {
                        tv.setMaxLines(500);
                    }else {
                        tv.setMaxLines(3);
                    }
                }
            });

            TextView accountName = layoutItem.findViewById(R.id.post_accountName);
            accountName.setText((String)user.get("name"));

            TextView date = layoutItem.findViewById(R.id.post_date);
            date.setText(getDate((int) listAccounts.get(position).get("created_at")));

            ImageView com = layoutItem.findViewById(R.id.post_comment);
            com.setTag(""+position);
            com.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentsDialog(v.getContext(), v.getTag().toString());
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return layoutItem;
    }

    /**
     * Load the images
     */
    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap bmp = null;
            try{
                URL url = new URL(urlOfImage);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            }catch(Exception e){
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result){
            if(imageView != null && result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }


    /**
     * Get post published date from timestamp
     * @param t timestamp
     * @return the published date
     */
    private String getDate(int t){
        long tmp = (new Long(t));
        long time = tmp * 1000;

        Date d = new Date();
        long difference = d.getTime() - time;
        long s = TimeUnit.MILLISECONDS.toSeconds(difference);//difference * 1000;
        long m = TimeUnit.MILLISECONDS.toMinutes(difference); //difference / 60;
        long h = TimeUnit.MILLISECONDS.toHours(difference); //difference / (24*60*60);

        String r = "";
        if(h == 0){
            if(m == 0){
                r = s + " seconds ago";
            }else {
                r = m + " minutes ago";
            }
        }else if(h <= 24){
            r = h + " hours ago";
        }else {
            SimpleDateFormat format = new SimpleDateFormat("d MMMM YYYY");
            r = format.format(new Date(time));
        }
        return r;
    }


    /**
     * Manage the post comments
     * @param context app context
     * @param tag
     */
    public void commentsDialog(Context context, String tag){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set layout
        final View customLayout = LayoutInflater.from(context).inflate( R.layout.comments_display_layout,null);
        builder.setView(customLayout);

        // add buttons
        builder.setNegativeButton(context.getResources().getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Create and show
        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }

    /**
     * Set upt twitter post reactions
     * @param context app context
     * @param like is the user liked the post
     */
    public void reactionsTwitterDialog(Context context, boolean like){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set layout
        final View customLayout = LayoutInflater.from(context).inflate( R.layout.reactions_post_twitter_layout,null);
        if(like){
            ((ImageView)customLayout.findViewById(R.id.reaction_like)).setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
        builder.setView(customLayout);

        // Create and show
        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }
}
