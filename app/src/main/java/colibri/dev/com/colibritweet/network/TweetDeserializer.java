package colibri.dev.com.colibritweet.network;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import colibri.dev.com.colibritweet.pojo.Tweet;

public class TweetDeserializer implements JsonDeserializer<Tweet> {
    private static final Gson GSON = new Gson();

    @Override
    public Tweet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject tweetJson = json.getAsJsonObject();
        Tweet tweet = GSON.fromJson(tweetJson, Tweet.class);

        String imageUrl = getTweetImageUrl(tweetJson);
        tweet.setImageUrl(imageUrl);

        return tweet;
    }

    private String getTweetImageUrl(JsonObject tweetJson) {
        JsonObject entities = tweetJson.get("entities").getAsJsonObject();
        JsonArray mediaArray = entities.has("media") ? entities.get("media").getAsJsonArray() : null;
        JsonObject firstMedia = mediaArray != null ? mediaArray.get(0).getAsJsonObject() : null;
        return firstMedia != null ?  firstMedia.get("media_url").getAsString() : null;
    }
}
