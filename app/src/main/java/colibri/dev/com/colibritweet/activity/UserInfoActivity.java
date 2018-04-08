package colibri.dev.com.colibritweet.activity;

import java.io.IOException;
import java.util.Collection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import colibri.dev.com.colibritweet.R;
import colibri.dev.com.colibritweet.adapter.TweetAdapter;
import colibri.dev.com.colibritweet.network.HttpClient;
import colibri.dev.com.colibritweet.pojo.Tweet;
import colibri.dev.com.colibritweet.pojo.User;

public class UserInfoActivity extends AppCompatActivity {

    public static final String USER_ID = "userId";

    private ImageView userImageView;
    private TextView nameTextView;
    private TextView nickTextView;
    private TextView descriptionTextView;
    private TextView locationTextView;
    private TextView followingCountTextView;
    private TextView followersCountTextView;
    private Toolbar toolbar;

    private RecyclerView tweetsRecyclerView;
    private TweetAdapter tweetAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private HttpClient httpClient;

    private int taskInProgressCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        final long userId = getIntent().getLongExtra(USER_ID, -1);

        userImageView = findViewById(R.id.user_image_view);
        nameTextView = findViewById(R.id.user_name_text_view);
        nickTextView = findViewById(R.id.user_nick_text_view);
        descriptionTextView = findViewById(R.id.user_description_text_view);
        locationTextView = findViewById(R.id.user_location_text_view);
        followingCountTextView = findViewById(R.id.following_count_text_view);
        followersCountTextView = findViewById(R.id.followers_count_text_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            tweetAdapter.clearItems();
            loadUserInfo(userId);
            loadTweets(userId);
        });

        initRecyclerView();

        httpClient = new HttpClient();
        loadUserInfo(userId);
        loadTweets(userId);
    }

    private void setRefreshLayoutVisible(boolean visible) {
        if(visible) {
            taskInProgressCount++;
            if(taskInProgressCount == 1) swipeRefreshLayout.setRefreshing(true);
        } else {
            taskInProgressCount--;
            if(taskInProgressCount == 0) swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, SearchUsersActivity.class);
            startActivity(intent);
        }
        return true;
    }


    private void loadTweets(long userId) {
        new TweetsAsyncTask().execute(userId);
    }

    @SuppressLint("StaticFieldLeak")
    private class TweetsAsyncTask extends AsyncTask<Long, Integer, Collection<Tweet>> {

        @Override
        protected void onPreExecute() {
            setRefreshLayoutVisible(true);
        }

        protected Collection<Tweet> doInBackground(Long... ids) {
            try {
                Long userId = ids[0];
                return httpClient.readTweets(userId);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Collection<Tweet> tweets) {
            setRefreshLayoutVisible(false);

            // успешный ответ
            if(tweets != null) {
                tweetAdapter.setItems(tweets);
            }
            // ошибка
            else {
                Toast.makeText(UserInfoActivity.this, R.string.loading_error_msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initRecyclerView() {
        tweetsRecyclerView = findViewById(R.id.tweets_recycler_view);
        ViewCompat.setNestedScrollingEnabled(tweetsRecyclerView, false);

        tweetsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tweetAdapter = new TweetAdapter();
        tweetsRecyclerView.setAdapter(tweetAdapter);
    }

    private void loadUserInfo(final long userId) {
        new UserInfoAsyncTask().execute(userId);
    }

    @SuppressLint("StaticFieldLeak")
    private class UserInfoAsyncTask extends AsyncTask<Long, Integer, User> {

        @Override
        protected void onPreExecute() {
            setRefreshLayoutVisible(true);
        }

        protected User doInBackground(Long... ids) {
            try {
                Long userId = ids[0];
                return httpClient.readUserInfo(userId);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(User user) {
            setRefreshLayoutVisible(false);

            // успешный ответ
            if(user != null) {
                displayUserInfo(user);
            }
            // ошибка
            else {
                Toast.makeText(UserInfoActivity.this, R.string.loading_error_msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayUserInfo(User user) {
        Picasso.with(this).load(user.getImageUrl()).into(userImageView);
        nameTextView.setText(user.getName());
        nickTextView.setText(user.getNick());
        descriptionTextView.setText(user.getDescription());
        locationTextView.setText(user.getLocation());

        String followingCount = String.valueOf(user.getFollowingCount());
        followingCountTextView.setText(followingCount);

        String followersCount = String.valueOf(user.getFollowersCount());
        followersCountTextView.setText(followersCount);

        getSupportActionBar().setTitle(user.getName());
    }
}