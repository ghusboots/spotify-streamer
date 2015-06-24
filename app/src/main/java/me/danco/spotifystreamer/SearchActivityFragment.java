package me.danco.spotifystreamer;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {
    ArrayAdapter<String> artistAdapter;

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        artistAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_artist, R.id.list_item_artist_textview, new ArrayList<String>());
        artistAdapter.add("blah 1");
        artistAdapter.add("Blah 2");

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ListView artistList = (ListView)rootView.findViewById(R.id.list_artists);
        artistList.setAdapter(artistAdapter);

        final EditText searchBox = (EditText) rootView.findViewById(R.id.edit_search);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    new ArtistSearchTask().execute(v.getText().toString());
                    //getActivity();
                    Log.v(getClass().getName(), "Enter pressed");
                } else {
                    Log.v(getClass().getName(), "nope");
                }

                return true;
            }
        });

        return rootView;
    }

    private class ArtistSearchTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArrayList<String> artistNames = new ArrayList<>();

            ArtistsPager artists = spotify.searchArtists(params[0]);
            for (Artist a : artists.artists.items) {
                artistNames.add(a.name);
            }

            return artistNames;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);

            artistAdapter.clear();
            artistAdapter.addAll(strings);
        }
    }


}
