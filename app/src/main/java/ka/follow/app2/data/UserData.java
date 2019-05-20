package ka.follow.app2.data;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Stack;

import ka.follow.app2.App;

public class UserData {

    private static UserData userDataInstance;
    private InstagramUser self_user;
    private HashMap<Integer, Stack<Fragment>> mStacks;

    private UserData() {
        intiMainStack();
        self_user = new InstagramUser();
    }

    public static UserData getInstance() {
        if (userDataInstance == null) {
            userDataInstance = new UserData();
            userDataInstance.loadFromFile(App.context);
        }
        return userDataInstance;
    }

    public InstagramUser getSelf_user() {
        if (self_user == null)
            self_user = new InstagramUser();
        return self_user;
    }

    public void setSelf_user(InstagramUser self_user) {
        this.self_user = self_user;
    }

    private void intiMainStack() {
        if (mStacks != null)
            mStacks.clear();
        mStacks = new HashMap<>();
        mStacks.put(0, new Stack<Fragment>());
        mStacks.put(1, new Stack<Fragment>());
        mStacks.put(2, new Stack<Fragment>());
        mStacks.put(3, new Stack<Fragment>());
        mStacks.put(4, new Stack<Fragment>());
        mStacks.put(5, new Stack<Fragment>());
        mStacks.put(6, new Stack<Fragment>());
        mStacks.put(7, new Stack<Fragment>());
        mStacks.put(8, new Stack<Fragment>());
        mStacks.put(9, new Stack<Fragment>());
        mStacks.put(10, new Stack<Fragment>());
        mStacks.put(11, new Stack<Fragment>());
    }

    private void loadFromFile(Context context) {
        String FILE_NAME = "UserData";
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String json = preferences.getString("data", null);
        if (json != null) {
            GsonBuilder builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = builder.create();
            userDataInstance = gson.fromJson(json, UserData.class);
        }
    }
}