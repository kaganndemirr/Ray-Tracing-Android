package com.kaganndemirr.raytracing.transparency

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColor

import com.kaganndemirr.raytracing.R

import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


class TransparencyActivity: Activity(){

    private fun shadeDiffuse(s: Shape, iPoint: Vertex): Color {
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

        r = if (r > 255.0)
            255.0
        else
            r
        r = if (r < 0.0)
            0.0
        else
            r

        g = if (g > 255.0)
            255.0
        else
            g
        g = if (g < 0.0)
            0.0
        else
            g

        b = if (b > 255.0)
            255.0
        else
            b
        b = if (b < 0.0)
            0.0
        else
            b

        return Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat())
    }

    private fun shadeSpecular(s: Shape, iPoint: Vertex, camera: Vertex): Color {
        val light = Vertex(0.0, 30.0, 60.0)
        val fromLight = (iPoint - light).normalize()
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

        r = if (r > 255.0)
            255.0
        else
            r
        r = if (r < 0.0)
            0.0
        else
            r

        g = if (g > 255.0)
            255.0
        else
            g
        g = if (g < 0.0)
            0.0
        else
            g

        b = if (b > 255.0)
            255.0
        else
            b
        b = if (b < 0.0)
            0.0
        else
            b

        return Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat())
    }

    private fun shadingModel(s: Shape, diffuseColor: Color, specularColor: Color, reflectedColor: Color,
                             transmittedColor: Color, ambient: Double, diffuse: Double, specular: Double,
                             reflection: Double, transmitted: Double): Color
    {
        val ambientColor = s.shapeColor

        val r = min(255.0, ambient * ambientColor.red() + diffuse * diffuseColor.red() + specular * specularColor.red() + reflection * reflectedColor.red() + transmitted * transmittedColor.red())
        val g = min(255.0, ambient * ambientColor.green() + diffuse * diffuseColor.green() + specular * specularColor.green() + reflection * reflectedColor.green() + transmitted * transmittedColor.green())
        val b = min(255.0, ambient * ambientColor.blue() + diffuse * diffuseColor.blue() + specular * specularColor.blue() + reflection * reflectedColor.blue() + transmitted * transmittedColor.blue())

        return Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat())
    }

    private fun calculateReflection(s: Shape, iPoint: Vertex, rd: Vertex): Vertex{
        val normal = s.normalAt(iPoint).normalize()

        return (rd - normal * (normal * rd) * 2.0).normalize()
    }

    private fun calculateTransmission(rd: Vertex): Vertex = rd

    private fun traceRay(ro: Vertex, rd: Vertex, shapes: ArrayList<Shape>, camera: Vertex,
                         depth: Int, prevShape: Shape?): Color {
        if (depth > 4)
            if (prevShape != null) {
                return prevShape.shapeColor
            }

        val intersection = Intersection()
        val intersections: ArrayList<Intersection> = ArrayList()

        for (i in 0 until shapes.size) {
            val t = shapes[i].intersect(ro, rd)

            if (t > 0.1) {
                intersection.distance = t
                intersection.indices = i

                intersections.add(intersection)
            }
        }

        if (intersections.size > 0) {
            var minDistance = Double.MAX_VALUE
            var minIndices = -1

            for (i in 0 until intersections.size) {
                if (intersections[i].distance < minDistance) {
                    minIndices = intersections[i].indices
                    minDistance = intersections[i].distance
                }
            }

            val iPoint = ro + rd * minDistance
            val s = shapes[minIndices]

            var reflectedColor = Color.BLACK.toColor()
            if (s.reflection != 0.0) {
                val reflectedDirection = calculateReflection(s, iPoint, rd)
                reflectedColor = traceRay(iPoint, reflectedDirection, shapes, camera, depth + 1, s)
            }

            var transmittedColor = Color.BLACK.toColor()
            if (s.transmitted != 0.0) {
                val transmittedDirection = calculateTransmission(rd)
                transmittedColor =
                    traceRay(iPoint, transmittedDirection, shapes, camera, depth + 1, s)
            }

            val diffuseColor = shadeDiffuse(s, iPoint)
            val specularColor = shadeSpecular(s, iPoint, camera)

            return shadingModel(
                s,
                diffuseColor,
                specularColor,
                reflectedColor,
                transmittedColor,
                s.ambient,
                s.diffuse,
                s.specular,
                s.reflection,
                s.transmitted
            )
        }
        return Color.BLACK.toColor()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_transparency)

        val transparencyImageView = findViewById<ImageView>(R.id.transparencyImageView)
        val elapsedTimeTextView = findViewById<TextView>(R.id.elapsedTimeTextView)

        transparencyImageView.setOnClickListener {
            val startTime = System.currentTimeMillis()

            val surface = Bitmap.createBitmap(1600, 900, Bitmap.Config.ARGB_8888)
            transparencyImageView.setImageBitmap(surface)

            val t1 = Triangle(
                Vertex(60.0, -40.0, 120.0),
                Vertex(-60.0, -40.0, 120.0),
                Vertex(-60.0, 40.0, 120.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t2 = Triangle(
                Vertex(60.0, -40.0, 120.0),
                Vertex(-60.0, 40.0, 120.0),
                Vertex(60.0, 40.0, 120.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t3 = Triangle(
                Vertex(60.0, -40.0, 0.0),
                Vertex(-60.0, -40.0, 0.0),
                Vertex(60.0, -40.0, 120.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t4 = Triangle(
                Vertex(-60.0, -40.0, 120.0),
                Vertex(60.0, -40.0, 120.0),
                Vertex(-60.0, -40.0, 0.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t5 = Triangle(
                Vertex(60.0, 40.0, 120.0),
                Vertex(-60.0, 40.0, 0.0),
                Vertex(60.0, 40.0, 0.0),
                0.7,
                0.5,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t6 = Triangle(
                Vertex(60.0, 40.0, 120.0),
                Vertex(-60.0, 40.0, 120.0),
                Vertex(-60.0, 40.0, 0.0),
                0.7,
                0.5,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t7 = Triangle(
                Vertex(60.0, 40.0, 120.0),
                Vertex(60.0, 40.0, 0.0),
                Vertex(60.0, -40.0, 0.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t8 = Triangle(
                Vertex(60.0, 40.0, 120.0),
                Vertex(60.0, -40.0, 0.0),
                Vertex(60.0, -40.0, 120.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t9 = Triangle(
                Vertex(-60.0, 40.0, 120.0),
                Vertex(-60.0, -40.0, 0.0),
                Vertex(-60.0, 40.0, 0.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t10 = Triangle(
                Vertex(-60.0, 40.0, 120.0),
                Vertex(-60.0, -40.0, 120.0),
                Vertex(-60.0, -40.0, 0.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t11 = Triangle(
                Vertex(60.0, -40.0, 0.0),
                Vertex(-60.0, 40.0, 0.0),
                Vertex(-60.0, -40.0, 0.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val t12 = Triangle(
                Vertex(60.0, -40.0, 0.0),
                Vertex(60.0, 40.0, 0.0),
                Vertex(-60.0, 40.0, 0.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.WHITE.toColor()
            )

            val s1 = Sphere(
                Vertex(20.0, 10.0, 95.0),
                20,
                0.2,
                0.3,
                0.5,
                0.0,
                0.0,
                Color.BLUE.toColor()
            )

            val s2 = Sphere(
                Vertex(-10.0, -5.0, 40.0),
                10,
                0.2,
                0.3,
                0.5,
                0.0,
                0.0,
                Color.RED.toColor()
            )

            val s3 = Sphere(
                Vertex(5.0, -25.0, 80.0),
                15,
                0.2,
                0.3,
                0.5,
                0.0,
                0.0,
                Color.YELLOW.toColor()
            )

            val s4 = Sphere(
                Vertex(10.0, 0.0, 30.0),
                10,
                0.1,
                0.1,
                0.0,
                0.0,
                0.8,
                Color.WHITE.toColor()
            )

            val shapes = arrayListOf(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12,
                s1, s2, s3, s4)

            val camera = Vertex(0.0, 0.0, 0.0)

            for (y in 0 until 900) {
                for (x in 0 until 1600) {
                    val pixel = Vertex(16.0 * x / 1599.0 - 8.0, 4.5 - y * 9.0 / 899.0, 10.0)
                    val rd = (pixel - camera).normalize()
                    val c = traceRay(camera, rd, shapes, camera, 0, null)
                    surface.setPixel(x, y, c.toArgb())
                }
            }

            val endTime = System.currentTimeMillis()

            elapsedTimeTextView.text = (endTime - startTime).toString() + " milisaniye."


            // Type
            elapsedTimeTextView.setTypeface(null, Typeface.BOLD)

            // Color
            elapsedTimeTextView.setTextColor(Color.BLACK)

            // Text Size
            elapsedTimeTextView.textSize = 24f
        }
    }
}