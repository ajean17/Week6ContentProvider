package com.example.alvin.w5_d3_ex01;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Alvin on 8/17/2017.
 */

public class ProviderTest extends AndroidTestCase {

    private static final String TEST_GENRE_NAME = "Family";
    private static final String TEST_UPDATE_GENRE_NAME = "SciFi";
    private static final String TEST_MOVIE_NAME = "Return of the Jedi";
    private static final String TEST_UPDATE_MOVIE_NAME = "Raiders of the Lost Ark";
    private static final String TEST_MOVIE_RELEASE_DATE = "1985-09-15";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testDeleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testDeleteAllRecords();
    }

    public void testDeleteAllRecords() {
        mContext.getContentResolver().delete(
            FeedReaderContract.MovieEntry.CONTENT_URI,
            null,
            null
            );
        mContext.getContentResolver().delete(
                FeedReaderContract.GenreEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                FeedReaderContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());

        cursor = mContext.getContentResolver().query(
                FeedReaderContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());

        cursor.close();
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(FeedReaderContract.GenreEntry.CONTENT_URI);
        assertEquals(FeedReaderContract.GenreEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(FeedReaderContract.GenreEntry.buildGenreUri(0));
        assertEquals(FeedReaderContract.GenreEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(FeedReaderContract.MovieEntry.CONTENT_URI);
        assertEquals(FeedReaderContract.MovieEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(FeedReaderContract.MovieEntry.buildMovieUri(0));
        assertEquals(FeedReaderContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testInsertandReadGenre() {
        ContentValues values = getGenreContentValues();
        Uri genreInsertUri = mContext.getContentResolver().insert(
                FeedReaderContract.GenreEntry.CONTENT_URI,
                values
        );
        long genreId = ContentUris.parseId(genreInsertUri);

        assertTrue(genreId > 0);

        Cursor cursor = mContext.getContentResolver().query(
                FeedReaderContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        validateCursor(cursor, values);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                FeedReaderContract.GenreEntry.buildGenreUri(genreId),
                null,
                null,
                null,
                null
        );

        validateCursor(cursor, values);
        cursor.close();


    }

    public void testUpdateGenre() {
        ContentValues values = getGenreContentValues();
        Uri genreUri = mContext.getContentResolver().insert(FeedReaderContract.GenreEntry.CONTENT_URI, values);
        long genreId = ContentUris.parseId(genreUri);

        ContentValues updateValues = new ContentValues();
        updateValues.put(FeedReaderContract.GenreEntry._ID, genreId);
        updateValues.put(FeedReaderContract.GenreEntry.COLUMN_NAME, TEST_UPDATE_GENRE_NAME);
    }

    public void testInsertReadMovie() {
        ContentValues values = getGenreContentValues();
        Uri genreInsertUri = mContext.getContentResolver().insert(FeedReaderContract.GenreEntry.CONTENT_URI, values);
        long genreId = ContentUris.parseId(genreInsertUri);

        ContentValues movieValues = getMovieContentValues(genreId);
        Uri movieInsertUri = mContext.getContentResolver().insert(FeedReaderContract.MovieEntry.CONTENT_URI, movieValues);
        long movieId = ContentUris.parseId(movieInsertUri);

        assertTrue(movieId > 0);

        Cursor movieCursor = mContext.getContentResolver().query(
                FeedReaderContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, movieValues);
        movieCursor.close();

        movieCursor = mContext.getContentResolver().query(
                FeedReaderContract.MovieEntry.buildMovieUri(movieId),
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, movieValues);
        movieCursor.close();
    }

    public void testUpdateMovie() {
        ContentValues genreValues = getGenreContentValues();
        Uri genreInsertUri = mContext.getContentResolver().insert(FeedReaderContract.GenreEntry.CONTENT_URI, genreValues);
        long genreId = ContentUris.parseId(genreInsertUri);

        ContentValues movieValues = getMovieContentValues(genreId);
        Uri movieInsertUri = mContext.getContentResolver().insert(FeedReaderContract.MovieEntry.CONTENT_URI, movieValues);
        long movieId = ContentUris.parseId(movieInsertUri);

        ContentValues updateMovie = new ContentValues(movieValues);
        updateMovie.put(FeedReaderContract.MovieEntry._ID, movieId);
        updateMovie.put(FeedReaderContract.MovieEntry.COLUMN_NAME, TEST_UPDATE_MOVIE_NAME);

        long updateId = mContext.getContentResolver().update(
                FeedReaderContract.MovieEntry.CONTENT_URI,
                updateMovie,
                FeedReaderContract.MovieEntry._ID + " =?",
                new String[] {String.valueOf(movieId)}
        );

        assertTrue(updateId > 0);

        Cursor movieCursor = mContext.getContentResolver().query(
                FeedReaderContract.MovieEntry.buildMovieUri(movieId),
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, updateMovie);
        movieCursor.close();
    }

    private ContentValues getGenreContentValues() {
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.GenreEntry.COLUMN_NAME, TEST_GENRE_NAME);
        return values;
    }

    private ContentValues getMovieContentValues (long genreId) {
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.MovieEntry.COLUMN_NAME, TEST_MOVIE_NAME);
        values.put(FeedReaderContract.MovieEntry.COLUMN_DATE, TEST_MOVIE_RELEASE_DATE);
        values.put(FeedReaderContract.MovieEntry.COLUMN_GENRE, genreId);
        return values;
    }

    private void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());//If it has a row, it will return true

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for(Map.Entry<String,Object> entry: valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);

            assertFalse(idx == -1);

            switch (valueCursor.getType(idx)) {
                case Cursor.FIELD_TYPE_FLOAT:
                    assertEquals(entry.getValue(), valueCursor.getDouble(idx));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    assertEquals(Integer.parseInt(entry.getValue().toString()), valueCursor.getInt(idx));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    assertEquals(entry.getValue(), valueCursor.getString(idx));
                    break;
                default:
                    assertEquals(entry.getValue(), valueCursor.getString(idx));
            }
        }
        valueCursor.close();
    }
}
