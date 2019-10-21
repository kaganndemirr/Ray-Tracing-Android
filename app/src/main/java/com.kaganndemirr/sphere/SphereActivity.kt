package com.kaganndemirr.sphere

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.graphics.Color
import android.graphics.Point
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
        setContentView(R.layout.activity_sphere)

        val sphereImageView: ImageView = findViewById(R.id.sphereImageView)
        val elapsedTimeTextView = findViewById<TextView>(R.id.elapsedTimeTextView)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        sphereImageView.setOnClickListener {
            val startTime = System.currentTimeMillis()

            val surface: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            sphereImageView.setImageBitmap(surface)

            val s = Sphere(Vertex(0.0, 0.0, 200.0), 75.0, Color.BLUE.toColor())
            val shapes = arrayListOf<Shape>(s)
            val camera = Vertex(0.0, 0.0, 0.0)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = Vertex(9 * y / (height - 1).toDouble() - 4.5, 9.5 - x * 19 / (width - 1).toDouble(), 10.0)
                    val rd = (pixel - camera).normalize()
                    val c = traceRay(camera, rd, shapes)
                    surface.setPixel(x, y, c.toArgb())
                }
            }

            val endTime = System.currentTimeMillis()

            elapsedTimeTextView.text = (endTime - startTime).toString() + " milisaniye."
        }
    }
}