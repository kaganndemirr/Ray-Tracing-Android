package com.kaganndemirr.raytracing.sphere

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColor

import com.kaganndemirr.raytracing.R

class SphereActivity: Activity(){

    private fun traceRay(ro: Vertex, rd: Vertex, shapes: ArrayList<Shape>): Color {
        val intersection = Intersection()
        val intersections: ArrayList<Intersection> = ArrayList()

        for(i in 0 until 1)
        {
            val t = shapes[i].intersect(ro, rd)

            if (t > 0.0)
            {
                intersection.distance = t
                intersection.indices = i

                intersections.add(intersection)
            }
        }

        if (intersections.size > 0)
        {
            var minDistance = Double.MAX_VALUE
            var minIndices = -1

            for (i in 0 until intersections.size)
            {
                if (intersections[i].distance < minDistance)
                {
                    minIndices = intersections[i].indices
                    minDistance = intersections[i].distance
                }
            }

            return shapes[minIndices].shapeColor
        }

        return Color.BLACK.toColor()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_sphere)

        val sphereImageView: ImageView = findViewById(R.id.sphereImageView)
        val elapsedTimeTextView = findViewById<TextView>(R.id.elapsedTimeTextView)

        sphereImageView.setOnClickListener {
            val startTime = System.currentTimeMillis()

            val surface: Bitmap = Bitmap.createBitmap(1600, 900, Bitmap.Config.ARGB_8888)
            sphereImageView.setImageBitmap(surface)

            val s = Sphere(Vertex(0.0, 0.0, 200.0), 75.0, Color.BLUE.toColor())
            val shapes = arrayListOf<Shape>(s)
            val camera = Vertex(0.0, 0.0, 0.0)

            for (y in 0 until 900) {
                for (x in 0 until 1600) {
                    val pixel = Vertex(16.0 * x / 1599.0 - 8.0, 4.5 - y * 9.0 / 899.0, 10.0)
                    val rd = (pixel - camera).normalize()
                    val c = traceRay(camera, rd, shapes)
                    surface.setPixel(x, y, c.toArgb())
                }
            }

            val endTime = System.currentTimeMillis()

            elapsedTimeTextView.text = (endTime - startTime).toString() + " milisaniye."

            // Type
            elapsedTimeTextView.setTypeface(null, Typeface.BOLD)

            // Color
            elapsedTimeTextView.setTextColor(Color.WHITE)

            // Text Size
            elapsedTimeTextView.textSize = 24f
        }
    }
}