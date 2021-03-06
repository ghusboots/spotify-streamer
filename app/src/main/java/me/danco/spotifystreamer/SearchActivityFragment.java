package me.danco.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {
    private ArtistAdapter artistAdapter;
    private ArrayList<ArtistParcelable> artistList;

    public SearchActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("artists", this.artistList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(this.getActivity(), this.getActivity().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }

        if (savedInstanceState == null || !savedInstanceState.containsKey("artists")) {
            this.artistList = new ArrayList<>();
        } else {
            this.artistList = savedInstanceState.getParcelableArrayList("artists");
        }

        artistAdapter = new ArtistAdapter(getActivity(), R.layout.list_item_artist, this.artistList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ListView artistListView = (ListView)rootView.findViewById(R.id.list_artists);
        artistListView.setAdapter(artistAdapter);

        final EditText searchBox = (EditText) rootView.findViewById(R.id.edit_search);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event.getAction() == KeyEvent.KEYCODE_ENTER)) {
                SpotifyApi api = new SpotifyApi(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
                SpotifyService spotify = api.getService();

                spotify.searchArtists(v.getText().toString(), new Callback<ArtistsPager>() {
                    @Override
                    public void success(ArtistsPager artistsPager, Response response) {
                        artistList.clear();

                        if (artistsPager.artists.total > 0) {
                            for (Artist a : artistsPager.artists.items) {
                                artistList.add(new ArtistParcelable(a.id, a.name, a.images.size() > 0 ? a.images.get(0).url : null));
                            }
                        } else {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.search_no_results), Toast.LENGTH_LONG).show();
                        }

                        artistAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e("Artist search failure", retrofitError.toString());
                        Toast.makeText(getActivity(), getActivity().getString(R.string.search_no_connection), Toast.LENGTH_LONG).show();
                    }


                });

                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

                return true;
            }

            return false;
            }
        });

        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent topTrackIntent = new Intent(getActivity(), TopTracksActivity.class);
                ArtistParcelable artist = (ArtistParcelable) view.getTag(R.id.ArtistAdapter_data);
                topTrackIntent.putExtra("artistId", artist.id);
                topTrackIntent.putExtra("artistName", artist.name);
                startActivity(topTrackIntent);
            }
        });

        return rootView;
    }
}