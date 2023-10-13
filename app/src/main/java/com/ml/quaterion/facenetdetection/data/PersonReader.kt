package com.ml.quaterion.facenetdetection.data

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.ml.quaterion.facenetdetection.Logger
import kotlin.collections.Map
import java.io.File
import java.io.FileNotFoundException
class PersonReader(private val path: String, private val baseImagesPath: String) {
    fun read(): Map<String, Person> {
        val data = mutableMapOf<String, Person>()
        try {
            Logger.Companion.log("WOHOO1");
            val file = File(path)
            Logger.Companion.log("WOHOO2");

            val rows: List<Map<String, String>> = csvReader().readAllWithHeader(file)
            Logger.Companion.log(rows.count().toString());

            for (row in rows) {
                Logger.Companion.log(row.toString());
                val person = Person(
                    row["id"]!!,
                    row["name"]!!,
                    row["age"]!!.toInt(),
                    row["gender"]!!,
                    row["status"]!!,
                    row["description"]!!,
                    ArrayList<String>()
                );
                data[person.id] = person;

            }
            return data;
        } catch (e: Exception) {
            Logger.Companion.log(e.toString()
            )
            return data;
        }
    }
}