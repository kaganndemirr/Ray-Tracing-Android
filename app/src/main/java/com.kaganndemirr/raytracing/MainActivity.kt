package com.kaganndemirr.raytracing

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.TextView

import com.kaganndemirr.sphere.SphereActivity
import com.kaganndemirr.triangle.TriangleActivity
import com.kaganndemirr.reflection.ReflectionActivity
import com.kaganndemirr.shadow.ShadowActivity
import com.kaganndemirr.transparency.TransparencyActivity


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val welcomeText = findViewById<TextView>(R.id.welcomeText)

        val triangleButton = findViewById<Button>(R.id.triangleButton)
        val sphereButton = findViewById<Button>(R.id.sphereButton)
        val reflectionButton = findViewById<Button>(R.id.reflectionButton)
        val transparencyButton = findViewById<Button>(R.id.transparencyButton)
        val shadowButton = findViewById<Button>(R.id.shadowButton)


        welcomeText.text = "Listeden Birini Seçiniz!"

        triangleButton.setOnClickListener {
            val intent = Intent(this, TriangleActivity::class.java)
            startActivity(intent)
        }

        sphereButton.setOnClickListener {
            val intent = Intent(this, SphereActivity::class.java)
            startActivity(intent)
        }

        reflectionButton.setOnClickListener {
            val intent = Intent(this, ReflectionActivity::class.java)
            startActivity(intent)
        }

        transparencyButton.setOnClickListener {
            val intent = Intent(this, TransparencyActivity::class.java)
            startActivity(intent)
        }

        shadowButton.setOnClickListener {
            val intent = Intent(this, ShadowActivity::class.java)
            startActivity(intent)
        }
    }
}
