package ka.follow.v4.parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ka.follow.v4.data.FriendStatus;

public class MultipleFriendStatusParser {

    public static HashMap<String, FriendStatus> parsFriendStatus(JSONObject response, HashMap<String, FriendStatus> users) throws JSONException {
        JSONObject friendship_statuses = response.getJSONObject("friendship_statuses");
        for (String id :
                users.keySet()) {
            JSONObject object = friendship_statuses.getJSONObject(id);
            FriendStatus status = new FriendStatus();
            status.setFollowing(object.getBoolean("following"));
            status.setOutgoing_request(object.getBoolean("outgoing_request"));
            status.setIncoming_request(object.getBoolean("incoming_request"));
            status.setIs_private(object.getBoolean("is_private"));
            users.put(id, status);
        }
        return users;
    }
}