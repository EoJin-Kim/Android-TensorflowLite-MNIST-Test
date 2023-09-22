package com.ej.tensorflowlitetest.draw

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.divyanshu.draw.widget.DrawView
import com.ej.tensorflowlitetest.R
import com.ej.tensorflowlitetest.tflite.Classifier
import com.ej.tensorflowlitetest.tflite.ClassifierWithSupport
import java.io.IOException
import java.util.*

class DrawActivity : AppCompatActivity() {
//    lateinit var cls : Classifier
    lateinit var cls : ClassifierWithSupport
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)

        val drawView = findViewById<DrawView>(R.id.drawView)
        drawView.setStrokeWidth(100.0f)
        drawView.setBackgroundColor(Color.BLACK)
        drawView.setColor(Color.WHITE)

        val resultView = findViewById<TextView>(R.id.resultView)

        val classifyBtn = findViewById<Button>(R.id.classifyBtn)
        classifyBtn.setOnClickListener {
            val image = drawView.getBitmap()
            val res = cls.classify(image)
//            val outStr = String.format(
//                Locale.ENGLISH,
//                "%d, %.0f%%",
//                res.first,
//                res.second * 100.0f
//            )
//            resultView.text = outStr
        }

        val clearBtn = findViewById<Button>(R.id.clearBtn)

        clearBtn.setOnClickListener {
            drawView.clearCanvas()
        }

        cls  = ClassifierWithSupport(this)
        try {
            cls.init()
        } catch (e: IOException) {
            Log.d("DigitClassifier","failed to init Classifier", e)
        }
    }

    override fun onDestroy() {
        cls.finish()
        super.onDestroy()
    }
}