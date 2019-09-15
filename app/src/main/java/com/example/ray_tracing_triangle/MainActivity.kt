package com.example.ray_tracing_triangle

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.widget.Button
import android.widget.ImageView
import androidx.core.graphics.toColor


class MainActivity : AppCompatActivity() {

    private val intersection = Intersection()

    private fun traceRay(ro: Vertex, rd: Vertex, shapes: MutableList<Shape>): Color{

        val intersections = mutableListOf<Intersection>()

        for(i in 0..2){
            val t: Double = shapes[i].intersect(ro, rd)

            if (t > 0.0){
                intersection.distance = t
                intersection.indices = i

                intersections.add(intersection)
            }
        }

        if (intersections.size > 0){
            var minDistance: Double = Double.MAX_VALUE
            var minIndices: Int = -1

            for (i in 0..intersections.size){
                if (intersections[i].distance < minDistance){
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
        setContentView(R.layout.activity_main)

        val renderButton: Button = findViewById(R.id.renderButton)
        val rttImageView: ImageView = findViewById(R.id.rttImageView)

        renderButton.setOnClickListener {
            val surface: Bitmap = Bitmap.createBitmap(800, 450, Bitmap.Config.RGB_565)
            rttImageView.setImageBitmap(surface)

            val t1 = Triangle(Vertex(0.0, 30.0, 40.0), Vertex(40.0, -30.0, 120.0), Vertex(-40.0, -30.0, 120.0), Color.BLUE.toColor())
            val t2 = Triangle(Vertex(-50.0, 30.0, 124.0), Vertex(30.0, 50.0, 124.0), Vertex(0.0, -30.0, 44.0), Color.RED.toColor())
            val t3 = Triangle(Vertex(0.0, 30.0, 40.0), Vertex(40.0, -30.0, 120.0), Vertex(-40.0, -30.0, 120.0), Color.GREEN.toColor())

            val shapes = mutableListOf<Shape>(t1, t2, t3)

            val camera = Vertex(0.0, 0.0, 0.0)

            for (x in 0..799){
                for (y in 0..449){
                    val pixel = Vertex(16 * x / 799.0 - 8, 4.5 - y * 9 / 449.0, 10.0)
                    val rd = (pixel.subs(camera)).normalize()
                    val c: Color = traceRay(camera, rd, shapes)
                    surface.setPixel(x, y, c.toArgb())
                }
                rttImageView.invalidate()
            }
        }
    }
}
