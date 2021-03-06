package me.danco.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrackAdapter extends ArrayAdapter<TrackParcelable> {
    private final Context context;
    private final int layoutResourceId;
    private ArrayList<TrackParcelable> tracks = null;

    public TrackAdapter(Context context, int layoutResourceId, ArrayList<TrackParcelable> tracks) {
        super(context, layoutResourceId, tracks);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.tracks = tracks;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(this.layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.albumThumbnail = (ImageView)row.findViewById(R.id.list_item_track_thumbnail);
            holder.albumTitle = (TextView)row.findViewById(R.id.list_item_track_album);
            holder.title = (TextView)row.findViewById(R.id.list_item_track_title);
            row.setTag(R.id.TrackAdapter_holder, holder);
        } else {
            holder = (ViewHolder)row.getTag(R.id.TrackAdapter_holder);
        }

        TrackParcelable track = tracks.get(position);
        row.setTag(R.id.TrackAdapter_data, track);

        holder.title.setText(track.title);
        holder.albumTitle.setText(track.albumTitle);

        if (track.albumThumbnail != null) {
            Picasso.with(this.context).load(track.albumThumbnail).into(holder.albumThumbnail);
        } else {
            holder.albumThumbnail.setImageResource(R.drawable.ic_broken_image_black_48dp);
        }

        return row;
    }

    private static class ViewHolder {
        public ImageView albumThumbnail;

        public TextView albumTitle;

        public TextView title;
    }
}
