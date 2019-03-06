package instahelper.ghonchegi.myfollower.Manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import instahelper.ghonchegi.myfollower.Models.User;
import instahelper.ghonchegi.myfollower.data.InstagramUser;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sqlite.db";
    public static String DATABASE_PATH = "/data/data/com.unfollowyab.gargij.sibroid/data/";
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DATABASE_PATH = myContext.getDatabasePath(DATABASE_NAME).toString();
    }

    public void createDatabase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        }
        boolean dbExist1 = checkDataBase();
        if (!dbExist1) {
            this.getReadableDatabase();
            try {
                this.close();
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            String myPath = DATABASE_PATH;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        } catch (SQLiteException ignored) {
        }
        return checkDB;
    }

    private void copyDataBase() throws IOException {
        String outFileName = DATABASE_PATH;
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    public void db_delete() {
        File file = new File(DATABASE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    public void openDatabase() throws SQLException {
        String myPath = DATABASE_PATH;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    public synchronized void closeDataBase() throws SQLException {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db_delete();
        }
    }


    public boolean checkkUser(InstagramUser user) {
        boolean isExist = false;
        openDatabase();
        Cursor cursor = myDataBase.rawQuery("SELECT * FROM userInfo WHERE userId = ?", new String[]{user.getUserId()});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            isExist = true;
            cursor.moveToNext();

        }
        cursor.close();
        myDataBase.close();
        return isExist;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        openDatabase();
        Cursor cursor = myDataBase.rawQuery("SELECT * FROM userInfo", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = new User();
            user.setUserId(String.valueOf(cursor.getInt(5)));
            user.setProfilePicture(cursor.getString(1));
            user.setUserName(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            if (cursor.getInt(4)==1) {
                user.setIsActive(1);
            } else user.setIsActive(0);
            users.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        myDataBase.close();
        return users;
    }

    public long addUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("profilePic", user.getProfilePicture());
        contentValues.put("userName", user.getUserName());
        contentValues.put("password", user.getPassword());
        contentValues.put("isActive", 1);
        contentValues.put("userId",( user.getUserId()));
        openDatabase();
        long returnValue = myDataBase.insert("userInfo", null, contentValues);
        myDataBase.close();
        return returnValue;
    }

    public long addUser(InstagramUser user) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("profilePic", user.getProfilePicture());
            contentValues.put("userName", user.getUserName());
            contentValues.put("password", user.getPassword());
            contentValues.put("isActive", true);
            contentValues.put("userId", user.getUserId());
            openDatabase();
            long returnValue = myDataBase.insert("userInfo", null, contentValues);
            myDataBase.close();
            return returnValue;
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;

        }

    }

    public boolean removeAllData() {
        try {
            openDatabase();
            int result= myDataBase.delete("userInfo", null,null);
            myDataBase.close();

            return true;
        } catch (Exception e) {
            return false;
        }


    }

    public boolean deleteUserById(String userId) {
        openDatabase();
        int result = myDataBase.delete("userInfo", "userId =?", new String[]{userId});
        myDataBase.close();
        return result != 0;
    }

    public Long setAllValueNotActive()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("isActive", 0);
        openDatabase();
        openDatabase();
        long returnValue = myDataBase.update("userInfo", contentValues,null, null);
        myDataBase.close();
        return returnValue;
    }
}