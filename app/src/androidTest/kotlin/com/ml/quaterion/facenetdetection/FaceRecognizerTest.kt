package com.ml.quaterion.facenetdetection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ml.quaterion.facenetdetection.model.FaceNetModel
import com.ml.quaterion.facenetdetection.model.Models.Companion.FACENET
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ObjectInputStream

@RunWith(AndroidJUnit4::class)
class FaceRecognizerTest {

    private lateinit var staticFrameAnalyser: StaticFrameAnalyser

    private fun loadSerializedImageData(context: Context): ArrayList<Pair<String, FloatArray>> {
        val imageData = context.assets.open("image_data")
        return try {
            val ois = ObjectInputStream(imageData)
            val data = ois.readObject()
            ois.close()
            data as ArrayList<Pair<String, FloatArray>>
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val modelInfo = FACENET
        val faceNetModel = FaceNetModel(context, modelInfo, useGpu = false, useXNNPack = false)
        staticFrameAnalyser = StaticFrameAnalyser(faceNetModel, loadSerializedImageData(context))
    }

    @Test
    fun testInferSinglePerson() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val imageData = context.assets.open("2.jpg")
        val bitmap = BitmapFactory.decodeStream(imageData)

        // Create a controlled input
        val inputBitmaps = arrayListOf<Bitmap>()
        inputBitmaps.add(bitmap)

        // Call the method you're testing
        val result = staticFrameAnalyser.inferSinglePerson(inputBitmaps)

        val person = result[0].first
        assertEquals("izik", person)
    }
}
