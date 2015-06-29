package me.danco.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        artistAdapter = new ArtistAdapter(getActivity(), R.layout.list_item_artist, new ArrayList<Artist>());

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

        return rootView;
    }

    private class ArtistSearchTask extends AsyncTask<String, Void, ArrayList<Artist>> {
        @Override
        protected ArrayList<Artist> doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArrayList<Artist> artistNames = new ArrayList<>();

            ArtistsPager artists = spotify.searchArtists(params[0]);
            for (Artist a : artists.artists.items) {
                artistNames.add(a);
            }

            return artistNames;
        }

        @Override
        protected void onPostExecute(ArrayList<Artist> artists) {
            super.onPostExecute(artists);

            artistAdapter.clear();
            artistAdapter.addAll(artists);
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

            ((TextView)row.findViewById(R.id.list_item_artist_textview)).setText(artist.name);

            return row;
        }
    }
}
