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

final class ArtistAdapter extends ArrayAdapter<ArtistParcelable> {
    private final Context context;
    private final int layoutResourceId;
    private ArrayList<ArtistParcelable> artists = null;

    public ArtistAdapter(Context context, @SuppressWarnings("SameParameterValue") int layoutResourceId, ArrayList<ArtistParcelable> artists) {
        super(context, layoutResourceId, artists);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.artists = artists;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(this.layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView)row.findViewById(R.id.list_item_artist_name);
            holder.thumbnail = (ImageView)row.findViewById(R.id.list_item_artist_thumbnail);
            row.setTag(R.id.ArtistAdapter_holder, holder);
        } else {
            holder = (ViewHolder)row.getTag(R.id.ArtistAdapter_holder);
        }

        ArtistParcelable artist = artists.get(position);
        row.setTag(R.id.ArtistAdapter_data, artist);

        holder.name.setText(artist.name);
        if (artist.thumbnail != null) {
            Picasso.with(this.context).load(artist.thumbnail).into(holder.thumbnail);
        } else {
            holder.thumbnail.setImageResource(R.drawable.ic_broken_image_black_48dp);
        }

        return row;
    }

    private static class ViewHolder {
        public ImageView thumbnail;

        public TextView name;
    }
}
