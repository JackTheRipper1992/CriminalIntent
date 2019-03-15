package example.michael.com.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import example.michael.com.criminalintent.database.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final String DATEBASE_NAME = "crimes.db";
    private static final int    DB_VERSION    = 1;

    public CrimeBaseHelper(Context context) {
        super(context, DATEBASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeTable.NAME + " ( _id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID + ", "
                + CrimeTable.Cols.TITLE + ","
                + CrimeTable.Cols.DATE + ", "
                + CrimeTable.Cols.SOLVED + ","
                + CrimeTable.Cols.SUSPECT + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(getClass().toString(), "onUpgrade method invoked...");
    }
}
