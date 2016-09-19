package com.example.jeonghun.heathcare.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JeongHun on 16. 5. 24..
 */
public class Dao {

    private static SQLiteDatabase database;

    private static Dao ourInstance;

    public static Dao getInstance(Context context) {
        ourInstance = new Dao(context);
        return  ourInstance;
    }

    private Dao(Context context) {
        database = context.openOrCreateDatabase("Body.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    }

    public static void insertData(JSONObject object){
        try {
            String sql = "DROP TABLE IF EXISTS Body";

            database.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS Body(ID integer primary key autoincrement,"
                    + "                                   Name text not null,"
                    + "                                   Age integer not null,"
                    + "                                   Height real not null,"
                    + "                                   Weight real not null,"
                    + "                                   BMI real not null);";
            database.execSQL(sql);

            sql = "INSERT INTO Body(Name, Age, Height, Weight, BMI)"
                    + " VALUES('"
                    + object.getString("name") + "', "
                    + object.getInt("age") + ", "
                    + object.getDouble("height") + ", "
                    + object.getDouble("weight") + ", "
                    + object.getDouble("bmi") + ");";

            database.execSQL(sql);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static JSONObject getData(){
        JSONObject object = new JSONObject();
        String sql = "SELECT * FROM Body;";
        try {
            Cursor cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            object.put("name", cursor.getString(1));
            object.put("age", cursor.getInt(2));
            object.put("height", cursor.getDouble(3));
            object.put("weight", cursor.getDouble(4));
            object.put("bmi", cursor.getDouble(5));
        } catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

}
