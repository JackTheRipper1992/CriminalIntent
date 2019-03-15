package example.michael.com.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import example.michael.com.criminalintent.database.CrimeBaseHelper;
import example.michael.com.criminalintent.database.CrimeCursorWrapper;
import example.michael.com.criminalintent.database.CrimeDbSchema.CrimeTable;

public class CrimeLab {

    private static volatile CrimeLab       sCrimeLab;
    private                 Context        mContext;
    private                 SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public void addCrime(Crime crime) {
        ContentValues values = buildContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public List<Crime> getCrimes() {
        List<Crime> crimeList = new ArrayList<>();

        try (CrimeCursorWrapper ccw = queryCrimes(null, null)) {
            ccw.moveToFirst();
            while (!ccw.isAfterLast()) {
                crimeList.add(ccw.getCrime());
                ccw.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return crimeList;
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = buildContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + "= ?", new String[]{uuidString});
    }

    public File getPhotoFile(Crime crime){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,crime.getPhotoFilename());
    }
    public Crime getCrime(UUID id) {
        try (CrimeCursorWrapper ccw = queryCrimes(CrimeTable.Cols.UUID + "= ?", new String[]{id.toString()})) {
            if (ccw.getCount() == 0) {
                return null;
            } else {
                ccw.moveToFirst();
                return ccw.getCrime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ContentValues buildContentValues(Crime crime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID, crime.getId().toString());
        contentValues.put(CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        contentValues.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        return contentValues;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }

}
