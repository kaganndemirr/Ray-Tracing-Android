package com.kaganndemirr.raytracing.triangle

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColor

import com.kaganndemirr.raytracing.R


class TriangleActivity : Activity() {

    private fun traceRay(ro: Vertex, rd: Vertex, shapes: ArrayList<Shape>): Color {
        val intersection = Intersection()
        val intersections: ArrayList<Intersection> = ArrayList()

        for(i in 0 until shapes.size)
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

        // No Status Bar, No Action Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_triangle)

        val triangleImageView: ImageView = findViewById(R.id.triangleImageView)
        val elapsedTimeTextView = findViewById<TextView>(R.id.elapsedTimeTextView)

        // Get Device Screen Size in Pixels
        val size = Point()
        val display = windowManager.defaultDisplay
        display.getSize(size)
        val width = size.x
        val height = size.y

        triangleImageView.setOnClickListener {
            val startTime = System.currentTimeMillis()

            val surface: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            triangleImageView.setImageBitmap(surface)

            val t = Triangle(
                Vertex(-30.0, 0.0, 37.0),
                Vertex(30.0, 40.0, 117.0),
                Vertex(30.0, -40.0, 117.0),
                Color.GREEN.toColor()
            )

            val shapes = arrayListOf<Shape>(t)

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


            // Type
            elapsedTimeTextView.setTypeface(null, Typeface.BOLD)

            // Color
            elapsedTimeTextView.setTextColor(Color.WHITE)

            // Text Size
            elapsedTimeTextView.textSize = 24f

        }
    }
}

