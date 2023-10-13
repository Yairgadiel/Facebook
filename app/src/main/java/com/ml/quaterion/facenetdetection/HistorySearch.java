package com.ml.quaterion.facenetdetection;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity
public class HistorySearch {

    // region Members

    @PrimaryKey
    private int _id;

    private String _personId;

    private Timestamp _timeSearched;

    // endregion

    // region Properties

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public String getPersonId() {
        return _personId;
    }

    public void setPersonId(String personId) {
        _personId = personId;
    }

    public Timestamp getTimeSearched() {
        return _timeSearched;
    }

    public void setTimeSearched(Timestamp timeSearched) {
        _timeSearched = timeSearched;
    }

    // endregion

}
