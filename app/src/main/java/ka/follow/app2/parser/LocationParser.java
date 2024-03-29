package ka.follow.app2.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ka.follow.app2.data.InstagramLocation;

public class LocationParser {

    public ArrayList<InstagramLocation> parsLocation(JSONObject response) {
        ArrayList<InstagramLocation> locations = new ArrayList<>();
        try {
            JSONArray locJsonArray = response.getJSONArray("items");
            for (int i = 0; i < locJsonArray.length(); i++) {
                JSONObject itemObject = locJsonArray.getJSONObject(i);
                JSONObject dataObject = itemObject.getJSONObject("location");
                InstagramLocation location = new InstagramLocation();
                location.setId(dataObject.getString("pk"));
                location.setName(dataObject.getString("name"));
                location.setLat(dataObject.getDouble("lat"));
                location.setLng(dataObject.getDouble("lng"));
                locations.add(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locations;
    }
}