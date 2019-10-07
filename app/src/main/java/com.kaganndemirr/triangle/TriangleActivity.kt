package com.kaganndemirr.triangle

import android.app.Activity
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.widget.Button
import android.widget.ImageView
import androidx.core.graphics.toColor

import com.kaganndemirr.raytracing.R


class TriangleActivity : Activity() {

    private fun traceRay(ro: Vertex, rd: Vertex, shapes: ArrayList<Shape>): Color {
        val intersection = Intersection()
        val intersections: ArrayList<Intersection> = ArrayList()

        for(i in 0 until 3)
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
        setContentView(R.layout.activity_triangle)

        val renderTriangleButton: Button = findViewById(R.id.triangleRenderButton)
        val triangleImageView: ImageView = findViewById(R.id.triangleImageView)

        renderTriangleButton.setOnClickListener {
            val surface: Bitmap = Bitmap.createBitmap(800, 450, Bitmap.Config.ARGB_8888)
            triangleImageView.setImageBitmap(surface)


            val t1 = Triangle(
                Vertex(0.0, 30.0, 40.0),
                Vertex(40.0, -30.0, 120.0),
                Vertex(-40.0, -30.0, 120.0),
                Color.BLUE.toColor()
            )
            val t2 = Triangle(
                Vertex(-50.0, 30.0, 124.0),
                Vertex(50.0, 30.0, 124.0),
                Vertex(0.0, -30.0, 44.0),
                Color.RED.toColor()
            )
            val t3 = Triangle(
                Vertex(-30.0, 0.0, 37.0),
                Vertex(30.0, 40.0, 117.0),
                Vertex(30.0, -40.0, 117.0),
                Color.GREEN.toColor()
            )

            val shapes = arrayListOf<Shape>(t1, t2, t3)

            val camera = Vertex(0.0, 0.0, 0.0)

            for (y in 0 until 450) {
                for (x in 0 until 800) {
                    val pixel = Vertex(16 * x / 799.0 - 8, 4.5 - y * 9 / 449.0, 10.0)
                    val rd = (pixel - camera).normalize()
                    val c = traceRay(camera, rd, shapes)
                    surface.setPixel(x, y, c.toArgb())
                }
            }

        }
    }
}
