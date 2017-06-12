package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.ddutra9.popularmoviesapp.model.Movie;

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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<Movie> adapter;

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

        List<Movie> movieList = new ArrayList<Movie>();

        adapter = new MovieAdapter(getActivity(), movieList);

        GridView gridMovies = (GridView)view.findViewById(R.id.grid_view_movies);
        gridMovies.setAdapter(adapter);

        gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Toast.makeText(getContext(), adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void updateMovies(){
        if(isOnline()){
            new MoviesTask(getContext()).execute(new String[]{});
        } else {
            Toast.makeText(getActivity(), "Favor verificar sua coneccao com a internet!", Toast.LENGTH_LONG);
        }
    }


    private class MoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String  THE_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
        private final String TAG = MoviesTask.class.getSimpleName();

        final Context context;

        public MoviesTask(Context context){
            this.context = context;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

//            if (params.length == 0) {
//                return null;
//            }


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonString = null;

            try {
                final String QUERY_PARAM = "popular";
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

        private Movie[] getMoviesFromJson(String moviesJsonString) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String TITLE = "title";
            final String OVERVIEW = "overview";
            final String VOTE = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String IMAGE = "poster_path";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

            Movie[] movies = new Movie[moviesArray.length()];
            Movie movie = null;
            for(int i = 0; i < moviesArray.length(); i++) {

                JSONObject movieJson = moviesArray.getJSONObject(i);

                Log.d(TAG, "moviesJson: " + movieJson.toString());

                movie = new Movie();

                movie.setTitle(movieJson.getString(TITLE));
                movie.setOverview(movieJson.getString(OVERVIEW));
                movie.setVoteAverage(movieJson.getDouble(VOTE));
                movie.setReleaseDate(parseDateFromAPI(movieJson.getString(RELEASE_DATE)));
                movie.setPosterPath(movieJson.getString(IMAGE));

                movies[i] = movie;
            }

            return movies;
        }
        
        private Date parseDateFromAPI(String dtStr){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date dt = null;
            try {
                dt = sdf.parse(dtStr);
            } catch (ParseException e) {
                Log.e(TAG, "erro ao fazer parse data", e);
            }

            return dt;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if(movies != null){
                adapter.clear();
                adapter.addAll(Arrays.asList(movies));
            }
        }
    }
}
