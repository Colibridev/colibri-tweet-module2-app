package colibri.dev.com.colibritweet.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

import colibri.dev.com.colibritweet.pojo.Tweet;
import colibri.dev.com.colibritweet.pojo.User;

public class JsonParser {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Tweet.class, new TweetDeserializer())
            .create();

    public Collection<User> getUsers(String response) {
        Type usersType = new TypeToken<Collection<User>>(){}.getType();
        return GSON.fromJson(response, usersType);
    }

    public User getUser(String response) {
        return GSON.fromJson(response, User.class);
    }

    public Collection<Tweet> getTweets(String response) {
        Type tweetsType = new TypeToken<Collection<Tweet>>(){}.getType();
        return GSON.fromJson(response, tweetsType);
    }

}