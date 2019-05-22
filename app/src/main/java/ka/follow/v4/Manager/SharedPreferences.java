package ka.follow.v4.Manager;

import android.annotation.SuppressLint;
import android.content.Context;

public class SharedPreferences {

    //region Keys
    private final String _user = "userKey";
    private final String _sendMobileTime = "sendMobileTimeKey";
    private final String _specialWheel = "specialWheelKey";
    //endregion
    //region Variables
    private android.content.SharedPreferences pr;
    private android.content.SharedPreferences.Editor editor;
    private Context context;
    //endregion

    @SuppressLint("CommitPrefEdits")
    public SharedPreferences(Context context) {
        this.context = context;
        pr = this.context.getSharedPreferences("Tourism", Context.MODE_PRIVATE);
        editor = pr.edit();
    }

    public void setAppVariables(String uuid, String apiToken) {

        editor.putString("_UUID", uuid);
        editor.putString("_Token", apiToken);
        editor.apply();
        editor.commit();
    }

    public String getUUID() {
        return pr.getString("_UUID", "null");

    }


    public String getApiToken() {
        return pr.getString("_Token", "null");

    }

    public void setSpeccialWhhel(boolean state) {
        editor.putBoolean(_specialWheel, state);
        editor.apply();
        editor.commit();
    }

    public boolean getSpecialWheel() {
        return pr.getBoolean(_specialWheel, false);
    }

}

