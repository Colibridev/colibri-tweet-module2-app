package colibri.dev.com.colibritweet.network;

import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import colibri.dev.com.colibritweet.pojo.Tweet;
import colibri.dev.com.colibritweet.pojo.User;

public class JsonParser {

    private final Gson gson;

    public JsonParser() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Tweet.class, new TweetDeserializer())
                .create();
    }

    public Collection<User> getUsers(String response) {
        return gson.fromJson(response, new TypeToken<Collection<User>>(){}.getType());
    }

    public User getUser(String response) {
        return gson.fromJson(response, User.class);
    }

    public Collection<Tweet> getTweets(String response) {
        return gson.fromJson(response, new TypeToken<Collection<Tweet>>(){}.getType());
    }
}