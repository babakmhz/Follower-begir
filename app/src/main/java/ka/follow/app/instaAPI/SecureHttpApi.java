package ka.follow.app.instaAPI;

import org.json.JSONObject;

public class SecureHttpApi {

    public interface ResponseHandler {
        void OnSuccess(JSONObject response);

        void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse);
    }
}