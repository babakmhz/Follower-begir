package ka.follow.app2.parser;

import org.json.JSONException;
import org.json.JSONObject;

import ka.follow.app2.data.FriendStatus;

public class FriendStatusParser {

    public static FriendStatus parsFriendStatus(JSONObject response) throws JSONException {
        FriendStatus status = new FriendStatus();
        status.setIncoming_request(response.getBoolean("incoming_request"));
        status.setOutgoing_request(response.getBoolean("outgoing_request"));
        status.setFollowing(response.getBoolean("following"));
        status.setFollowed_by(response.getBoolean("followed_by"));
        status.setBlocking(response.getBoolean("blocking"));
        status.setIs_private(response.getBoolean("is_private"));
        return status;
    }
}