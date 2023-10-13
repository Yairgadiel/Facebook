package com.ml.quaterion.facenetdetection.DB.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ml.quaterion.facenetdetection.DB.Model.Person;

import java.util.List;

@Dao
public interface PersonDao {
    @Query("SELECT * FROM person")
    List<Person> getAll();

    @Insert
    void insertAll(Person... users);

    @Delete
    void delete(Person user);
}
