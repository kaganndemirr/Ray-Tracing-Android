package com.kaganndemirr.raytracing

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor

import kotlin.math.pow
import kotlin.math.min

class ReflectionTriangleActivity: AppCompatActivity() {

    private fun shadeDiffuse(s: V2Shape, iPoint: Vertex): Color {
        val light = Vertex(0.0, 30.0, 60.0)
        val toLight = (light - iPoint).normalize()
        val normal = s.normalAt(iPoint).normalize()
        val c = s.shapeColor

        val diffuseCoefficient = normal * toLight
        when {
            diffuseCoefficient < 0.0 -> Color.BLACK.toColor()
        }

        var r = 0.0
        var g = 0.0
        var b = 0.0

        r += diffuseCoefficient * c.red()
        g += diffuseCoefficient * c.green()
        b += diffuseCoefficient * c.blue()

        r = if (r > 255)
            255.0
        else
            r
        r = if (r < 0)
            0.0
        else
            r

        g = if (g > 255)
            255.0
        else
            g
        g = if (g < 0)
            0.0
        else
            g

        b = if (b > 255)
            255.0
        else
            b
        b = if (b < 0)
            0.0
        else
            b

        return Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat())
    }

    private fun shadeSpecular(s: V2Shape, iPoint: Vertex, camera: Vertex): Color{
        val light = Vertex(0.0, 30.0, 60.0)
        val fromLight = (light - iPoint).normalize()
        val normal = s.normalAt(iPoint).normalize()
        val toCamera = (camera - iPoint).normalize()

        val lamp = Color.WHITE.toColor()
        val reflected = (fromLight -  normal * (normal * fromLight) * 2.0).normalize()
        val dotProduct = reflected * toCamera

        if(dotProduct < 0.0)
            return Color.BLACK.toColor()

        val specularCoefficient = dotProduct.pow(8.0)
        var r = 0.0
        var g = 0.0
        var b = 0.0

        r += specularCoefficient * lamp.red()
        g += specularCoefficient * lamp.green()
        b += specularCoefficient * lamp.blue()

        r = if (r > 255)
            255.0
        else
            r
        r = if (r < 0)
            0.0
        else
            r

        g = if (g > 255)
            255.0
        else
            g
        g = if (g < 0)
            0.0
        else
            g

        b = if (b > 255)
            255.0
        else
            b
        b = if (b < 0)
            0.0
        else
            b

        return Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat())
    }

    private fun shadingModel(s: V2Shape, diffuseColor: Color,  specularColor: Color, reflectedColor: Color,
                     amb: Double, dif: Double, spec: Double, refl: Double): Color
    {
        val ambientColor = s.shapeColor

        val r = min(255.0, amb * ambientColor.red() + dif * diffuseColor.red() + spec * specularColor.red() + refl * reflectedColor.red())
        val g = min(255.0, amb * ambientColor.green() + dif * diffuseColor.green() + spec * specularColor.green() + refl * reflectedColor.green())
        val b = min(255.0, amb * ambientColor.blue() + dif * diffuseColor.blue() + spec * specularColor.blue() + refl * reflectedColor.blue())

        return Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat())
    }

    private fun calculateReflection(s: V2Shape, iPoint: Vertex, rd: Vertex): Vertex{
        val normal = s.normalAt(iPoint).normalize()

        return (rd - normal * (normal * rd) * 2.0).normalize()
    }

    private fun traceRay(ro: Vertex, rd: Vertex, shapes: ArrayList<V2Shape>, camera: Vertex,
                         depth: Int, prevShape: V2Shape?): Color
    {
        if(depth > 4)
            if (prevShape != null) {
                return prevShape.shapeColor
            }

        val intersection = Intersection()
        val intersections: ArrayList<Intersection> = ArrayList()

        for(i in 0 until 3)
        {
            val t = shapes[i].intersect(ro, rd)

            if (t > 0.1)
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

            val iPoint = ro + rd * minDistance
            val s = shapes[minIndices]

            var reflectedColor = Color.BLACK.toColor()
            if(s.refl != 0.0){
                val reflectedDirection = calculateReflection(s, iPoint, rd)
                reflectedColor = traceRay(iPoint, reflectedDirection, shapes, camera, depth + 1, s)
            }

            val diffuseColor = shadeDiffuse(s, iPoint)
            val specularColor = shadeSpecular(s, iPoint, camera)

            return shadingModel(s, diffuseColor, specularColor, reflectedColor, s.ambient, s.dif, s.spec, s.refl)
        }

        return Color.BLACK.toColor()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reflection_triangle)

        val renderRTButton = findViewById<Button>(R.id.rTButton)
        val rtRTImageView = findViewById<ImageView>(R.id.rtRTImageView)

        renderRTButton.setOnClickListener {
            val surface = Bitmap.createBitmap(800, 450, Bitmap.Config.ARGB_8888)
            rtRTImageView.setImageBitmap(surface)

            val t1 = V2Triangle(
                Vertex(-60.0, 30.0, 120.0),
                Vertex(40.0, -30.0, 60.0),
                Vertex(-40.0, -30.0, 120.0),
                0.2, 0.2, 0.0, 0.6, Color.RED.toColor()
            )

            val t2 = V2Triangle(
                Vertex(40.0, 30.0, 40.0),
                Vertex(40.0, -30.0, 40.0),
                Vertex(-40.0, -30.0, 100.0),
                0.2, 0.2, 0.0, 0.6, Color.GREEN.toColor()
            )

            val t3 = V2Triangle(
                Vertex(-24.0, 20.0, 70.0),
                Vertex(-24.0, -20.0, 85.0),
                Vertex(-24.0, -20.0, 55.0),
                0.2, 0.8, 0.0, 0.0, Color.BLUE.toColor()
            )

            val shapes = arrayListOf<V2Shape>(t1, t2, t3)

            val camera = Vertex(0.0,0.0,0.0)
            // var rd = Vertex(0.0,0.0,1.0)

            // var c = traceRay(camera, rd, shapes, camera, 0, null)

            for (y in 0 until 450) {
                for (x in 0 until 800) {
                    val pixel = Vertex(16 * x / 799.0 - 8, 4.5 - y * 9 / 449.0, 10.0)
                    val rd = (pixel - camera).normalize()
                    val c = traceRay(camera, rd, shapes, camera, 0, null)
                    surface.setPixel(x, y, c.toArgb())
                }
                rtRTImageView.invalidate()
            }
        }
    }
}


