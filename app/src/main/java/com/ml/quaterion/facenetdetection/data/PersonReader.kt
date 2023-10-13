package com.ml.quaterion.facenetdetection.data

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.ml.quaterion.facenetdetection.Logger
import kotlin.collections.Map
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

class PersonReader(private val path: String, private val baseImagesPath: String) {
    fun read(): Map<String, Person> {
        val data = mutableMapOf<String, Person>()
        try {
            val file = File(path)
            val rows: List<Map<String, String>> = csvReader().readAllWithHeader(file)

            for (row in rows) {
                val personId = row["id"]!!;
                val imageBaseDir = File(baseImagesPath);
                val pathToImg = File(imageBaseDir, "$personId.jpg")

                val person = Person(
                    personId,
                    row["name"]!!,
                    (row["age"]?:"-1").toInt(),
                    (row["gender"]?:"N/A"),
                    (row["status"]?:"N/A"),
                    (row["description"]?:"N/A"),
                    pathToImg.absolutePath
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