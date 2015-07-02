package me.danco.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {
    private ArtistAdapter artistAdapter;

    public SearchActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
        artistAdapter = new ArtistAdapter(getActivity(), R.layout.list_item_artist, new ArrayList<Artist>());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ListView artistList = (ListView)rootView.findViewById(R.id.list_artists);
        artistList.setAdapter(artistAdapter);

        final EditText searchBox = (EditText) rootView.findViewById(R.id.edit_search);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event.getAction() == KeyEvent.KEYCODE_ENTER)) {
                new ArtistSearchTask().execute(v.getText().toString());

                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

                return true;
            }

            return false;
            }
        });

        artistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent topTrackIntent = new Intent(getActivity(), TopTracksActivity.class);
                Artist artist = (Artist)view.getTag();
                topTrackIntent.putExtra("artistId", artist.id);
                topTrackIntent.putExtra("artistName", artist.name);
                startActivity(topTrackIntent);
            }
        });

        return rootView;
    }

    private class ArtistSearchTask extends AsyncTask<String, Void, ArrayList<Artist>> {
        @Override
        protected ArrayList<Artist> doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArrayList<Artist> artistList = new ArrayList<>();

            try {
                ArtistsPager artists = spotify.searchArtists(params[0]);

                for (Artist a : artists.artists.items) {
                    artistList.add(a);
                }
            } catch (Exception ex) {
                Log.d(ArtistSearchTask.class.getName(), ex.toString());
                return null;
            }

            return artistList;
        }

        @Override
        protected void onPostExecute(ArrayList<Artist> artists) {
            super.onPostExecute(artists);

            artistAdapter.clear();
            if (artists == null) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.search_no_connection), Toast.LENGTH_LONG).show();
            } else if (artists.size() == 0) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.search_no_results), Toast.LENGTH_LONG).show();
            } else {
                artistAdapter.addAll(artists);
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    public final class ArtistAdapter extends ArrayAdapter<Artist> {
        private final Context context;
        private final int layoutResourceId;
        private ArrayList<Artist> artists = null;

        public ArtistAdapter(Context context, int layoutResourceId, ArrayList<Artist> artists) {
            super(context, layoutResourceId, artists);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.artists = artists;
        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            if (row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(this.layoutResourceId, parent, false);
            }

            Artist artist = artists.get(position);
            row.setTag(artist);

            ((TextView)row.findViewById(R.id.list_item_artist_name)).setText(artist.name);
            if (artist.images.size() > 0) {
                Picasso.with(this.context).load(artist.images.get(0).url).into((ImageView)row.findViewById(R.id.list_item_artist_thumbnail));
            } else {
                ((ImageView)row.findViewById(R.id.list_item_artist_thumbnail)).setImageResource(R.drawable.ic_broken_image_black_48dp);
            }

            return row;
        }
    }
}
