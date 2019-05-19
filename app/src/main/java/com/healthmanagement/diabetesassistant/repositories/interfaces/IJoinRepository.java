package com.healthmanagement.diabetesassistant.repositories.interfaces;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public interface IJoinRepository<T>
{
    void create( T item );

    T read( String id );

    ArrayList<T> readAll();

    ArrayList<T> readAll( String fistId, String secondId );

    T readFromCursor( Cursor cursor );

    ContentValues putContentValues(T item );

    void update( String fistId, String secondId, T item );

    void delete( T item );

    void delete( String firstId, String secondId );

    void delete( String patientId );

    void setAllSynced();

} // interface
