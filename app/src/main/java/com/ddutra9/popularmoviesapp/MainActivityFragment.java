package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.ddutra9.popularmoviesapp.model.ParcelableMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static String TAG = MainActivityFragment.class.getSimpleName();

    private ArrayAdapter<ParcelableMovie> adapter;
    private ArrayList<ParcelableMovie> movieList;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movieList = new ArrayList<ParcelableMovie>();
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
        }

        adapter = new MovieAdapter(getActivity(), movieList);

        GridView gridMovies = (GridView)view.findViewById(R.id.grid_view_movies);
        gridMovies.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void updateMovies(){
        Log.d(TAG, "isOnline: " + isOnline());
        if(isOnline()){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String order = prefs.getString(getString(R.string.pref_order_key),
                    getString(R.string.pref_order_popular));

            new MoviesTask(getContext()).execute(new String[]{order});
        } else {
            Toast.makeText(getActivity(), R.string.no_internet_connected, Toast.LENGTH_LONG).show();
        }
    }


    private class MoviesTask extends AsyncTask<String, Void, ParcelableMovie[]> {

        private final String THE_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
        private final String TAG = MoviesTask.class.getSimpleName();

        final Context context;

        public MoviesTask(Context context) {
            this.context = context;
        }

        @Override
        protected ParcelableMovie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonString = null;

            try {
                final String QUERY_PARAM = params[0];
                final String API_KEY = "api_key";
                final String LANGUAGE = "language";

                Uri builtUri = Uri.parse(THE_MOVIE_URL).buildUpon()
                        .appendPath(QUERY_PARAM)
                        .appendQueryParameter(API_KEY, context.getString(R.string.API_MOVIE_KEY))
                        .appendQueryParameter(LANGUAGE, "pt-BR")
                        .build();

                Log.d(TAG, "url: " + builtUri.toString());
                URL url = new URL(builtUri.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                moviesJsonString = buffer.toString();
                Log.d(TAG, moviesJsonString);

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Erro ao fechar stream", e);
                    }
                }
            }

            try {
                return getMoviesFromJson(moviesJsonString);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        private ParcelableMovie[] getMoviesFromJson(String moviesJsonString) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String TITLE = "title";
            final String OVERVIEW = "overview";
            final String VOTE = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String IMAGE = "poster_path";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

            ParcelableMovie[] movies = new ParcelableMovie[moviesArray.length()];
            ParcelableMovie movie = null;
            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject movieJson = moviesArray.getJSONObject(i);

                Log.d(TAG, "moviesJson: " + movieJson.toString());

                movie = new ParcelableMovie();

                movie.setTitle(movieJson.getString(TITLE));
                movie.setOverview(movieJson.getString(OVERVIEW));
                movie.setVoteAverage(movieJson.getDouble(VOTE));
                movie.setReleaseDate(parseDateFromAPI(movieJson.getString(RELEASE_DATE)));
                movie.setPosterPath(movieJson.getString(IMAGE));

                movies[i] = movie;
            }

            return movies;
        }

        private Long parseDateFromAPI(String dtStr) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date dt = sdf.parse(dtStr);
                return dt.getTime();
            } catch (ParseException e) {
                Log.e(TAG, "erro ao fazer parse data", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ParcelableMovie[] movies) {
            if (movies != null) {
                adapter.clear();
                adapter.addAll(Arrays.asList(movies));
            }
        }
    }
}
