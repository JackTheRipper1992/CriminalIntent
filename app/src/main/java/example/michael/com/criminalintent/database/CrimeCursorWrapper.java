package example.michael.com.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import example.michael.com.criminalintent.database.CrimeDbSchema.CrimeTable;
import example.michael.com.criminalintent.Crime;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = this.getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = this.getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = this.getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = this.getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = this.getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        return crime;
    }
}
