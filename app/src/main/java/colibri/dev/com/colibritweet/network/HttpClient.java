package colibri.dev.com.colibritweet.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.oauth.OAuth1aHeaders;

public class HttpClient {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String GET = "GET";

    public String readUserInfo(long userId) throws IOException {
        String requestUrl = "https://api.twitter.com/1.1/users/show.json?user_id=" + userId;

        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // add auth header to request
        String authHeader = getAuthHeader(requestUrl);
        connection.setRequestProperty(HEADER_AUTHORIZATION, authHeader);

        connection.connect();

        InputStream in;
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            in = connection.getErrorStream();
        } else {
            in = connection.getInputStream();
        }

        String response = convertStreamToString(in);

        return response;
    }

    private String convertStreamToString(InputStream stream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        stream.close();

        return sb.toString();
    }

    private String getAuthHeader(String url) {
        TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        return new OAuth1aHeaders().getAuthorizationHeader(authConfig,
                session.getAuthToken(), null, GET, url, null);
    }
}