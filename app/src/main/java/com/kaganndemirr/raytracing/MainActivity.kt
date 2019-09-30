package com.kaganndemirr.raytracing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val welcomeText = findViewById<TextView>(R.id.welcome_text)

        val tButton = findViewById<Button>(R.id.t_button)
        val sButton = findViewById<Button>(R.id.sButton)
        val rTButton = findViewById<Button>(R.id.rTButton)


        welcomeText.text = "Listeden Birini Seçiniz."

        tButton.setOnClickListener {
            val intent = Intent(this, TriangleActivity::class.java)
            startActivity(intent)
        }

        sButton.setOnClickListener {
            val intent = Intent(this, SphereActivity::class.java)
            startActivity(intent)
        }

        rTButton.setOnClickListener {
            val intent = Intent(this, ReflectionTriangleActivity::class.java)
            startActivity(intent)
        }
    }
}
