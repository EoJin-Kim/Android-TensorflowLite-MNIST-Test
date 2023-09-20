package com.ej.tensorflowlitetest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ej.tensorflowlitetest.draw.DrawActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawBtn: Button = findViewById(R.id.drawBtn)
        drawBtn.setOnClickListener { view ->
            val i = Intent(this, DrawActivity::class.java)
            startActivity(i)
        }
    }
}