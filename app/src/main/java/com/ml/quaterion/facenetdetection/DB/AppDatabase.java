package com.ml.quaterion.facenetdetection.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ml.quaterion.facenetdetection.DB.DAO.PersonDao;
import com.ml.quaterion.facenetdetection.DB.Model.Person;

@Database(entities = {Person.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PersonDao personDao();

}
