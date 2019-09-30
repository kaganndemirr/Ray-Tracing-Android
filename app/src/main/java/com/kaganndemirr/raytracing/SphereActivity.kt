package com.kaganndemirr.raytracing

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.widget.Button
import android.widget.ImageView
import androidx.core.graphics.toColor

class SphereActivity: AppCompatActivity(){

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

        val renderSButton: Button = findViewById(R.id.renderSButton)
        val rtSImageView: ImageView = findViewById(R.id.rtSImageView)

        renderSButton.setOnClickListener {
            val surface: Bitmap = Bitmap.createBitmap(800, 450, Bitmap.Config.ARGB_8888)
            rtSImageView.setImageBitmap(surface)

            val s = Sphere(Vertex(0.0, 0.0, 200.0), 75.0, Color.BLUE.toColor())
            val shapes = arrayListOf<Shape>(s)
            val camera = Vertex(0.0, 0.0, 0.0)

            for (y in 0 until 450) {
                for (x in 0 until 800) {
                    val pixel = Vertex(16 * x / 799.0 - 8, 4.5 - y * 9 / 449.0, 10.0)
                    val rd = (pixel - camera).normalize()
                    val c = traceRay(camera, rd, shapes)
                    surface.setPixel(x, y, c.toArgb())
                }
                rtSImageView.invalidate()
            }
        }
    }
}