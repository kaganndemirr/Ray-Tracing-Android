package com.kaganndemirr.reflection

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import com.kaganndemirr.raytracing.R
import kotlin.math.min
import kotlin.math.pow

class ReflectionActivity : Activity(){

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

    private fun shadeSpecular(s: Shape, iPoint: Vertex, camera: Vertex): Color{
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

    private fun shadingModel(s: Shape, diffuseColor: Color,  specularColor: Color, reflectedColor: Color,
                             ambient: Double, diffuse: Double, specular: Double, reflection: Double): Color
    {
        val ambientColor = s.shapeColor

        val r = min(255.0, ambient * ambientColor.red() + diffuse * diffuseColor.red() + specular * specularColor.red() + reflection * reflectedColor.red())
        val g = min(255.0, ambient * ambientColor.green() + diffuse * diffuseColor.green() + specular * specularColor.green() + reflection * reflectedColor.green())
        val b = min(255.0, ambient * ambientColor.blue() + diffuse * diffuseColor.blue() + specular * specularColor.blue() + reflection * reflectedColor.blue())

        return Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat())
    }

    private fun calculateReflection(s: Shape, iPoint: Vertex, rd: Vertex): Vertex{
        val normal = s.normalAt(iPoint).normalize()

        return (rd - normal * (normal * rd) * 2.0).normalize()
    }

    private fun traceRay(ro: Vertex, rd: Vertex, shapes: ArrayList<Shape>, camera: Vertex,
                         depth: Int, prevShape: Shape?): Color
    {
        if(depth > 4)
            if (prevShape != null) {
                return prevShape.shapeColor
            }

        val intersection = Intersection()
        val intersections: ArrayList<Intersection> = ArrayList()

        for(i in 0 until shapes.size)
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
            if(s.reflection != 0.0){
                val reflectedDirection = calculateReflection(s, iPoint, rd)
                reflectedColor = traceRay(iPoint, reflectedDirection, shapes, camera, depth + 1, s)
            }

            val diffuseColor = shadeDiffuse(s, iPoint)
            val specularColor = shadeSpecular(s, iPoint, camera)

            return shadingModel(s, diffuseColor, specularColor, reflectedColor, s.ambient, s.diffuse, s.specular, s.reflection)
        }

        return Color.BLACK.toColor()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reflection)

        val reflectionRenderButton = findViewById<Button>(R.id.reflectionRenderButton)
        val reflectionImageView = findViewById<ImageView>(R.id.reflectionImageView)
        val elapsedTimeTextView = findViewById<TextView>(R.id.elapsedTimeTextView)

        reflectionRenderButton.setOnClickListener {
            val startTime = System.currentTimeMillis()

            val surface = Bitmap.createBitmap(800, 450, Bitmap.Config.ARGB_8888)
            reflectionImageView.setImageBitmap(surface)

            val t1 = Triangle(
                Vertex(60.0, -40.0, 120.0),
                Vertex(-60.0, -40.0, 120.0),
                Vertex(-60.0, 40.0, 120.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.MAGENTA.toColor()
            )

            val t2 = Triangle(
                Vertex(60.0, -40.0, 120.0),
                Vertex(-60.0, 40.0, 120.0),
                Vertex(60.0, 40.0, 120.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.MAGENTA.toColor()
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
                Color.CYAN.toColor()
            )

            val t10 = Triangle(
                Vertex(-60.0, 40.0, 120.0),
                Vertex(-60.0, -40.0, 120.0),
                Vertex(-60.0, -40.0, 0.0),
                0.3,
                0.7,
                0.0,
                0.0,
                Color.CYAN.toColor()
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

            // Mirror and Frame
            val t13 = Triangle(
                Vertex(50.0, 30.0, 70.0),
                Vertex(50.0, -30.0, 70.0),
                Vertex(0.0, 30.0, 118.0),
                0.2,
                0.0,
                0.0,
                0.8,
                Color.WHITE.toColor()
            )

            val t14 = Triangle(
                Vertex(0.0, 30.0, 118.0),
                Vertex(50.0, -30.0, 70.0),
                Vertex(0.0, -30.0, 118.0),
                0.2,
                0.0,
                0.0,
                0.8,
                Color.WHITE.toColor()
            )

            val t15 = Triangle(
                Vertex(50.0, 32.0, 70.0),
                Vertex(50.0, 30.0, 70.0),
                Vertex(0.0, 32.0, 120.0),
                0.2,
                0.8,
                0.0,
                0.0,
                Color.BLACK.toColor()
            )

            val t16 = Triangle(
                Vertex(0.0, 32.0, 120.0),
                Vertex(50.0, 30.0, 70.0),
                Vertex(0.0, 30.0, 120.0),
                0.2,
                0.8,
                0.0,
                0.0,
                Color.BLACK.toColor()
            )

            val t17 = Triangle(
                Vertex(50.0, -30.0, 70.0),
                Vertex(50.0, -32.0, 70.0),
                Vertex(0.0, -30.0, 120.0),
                0.2,
                0.8,
                0.0,
                0.0,
                Color.BLACK.toColor()
            )

            val t18 = Triangle(
                Vertex(0.0, -30.0, 120.0),
                Vertex(50.0, -32.0, 70.0),
                Vertex(0.0, -32.0, 120.0),
                0.2,
                0.8,
                0.0,
                0.0,
                Color.BLACK.toColor()
            )

            val t19 = Triangle(
                Vertex(52.0, 32.0, 68.0),
                Vertex(52.0, -32.0, 68.0),
                Vertex(50.0, 32.0, 70.0),
                0.2,
                0.8,
                0.0,
                0.0,
                Color.BLACK.toColor()
            )

            val t20 = Triangle(
                Vertex(50.0, 32.0, 70.0),
                Vertex(52.0, -32.0, 68.0),
                Vertex(50.0, -32.0, 70.0),
                0.2,
                0.8,
                0.0,
                0.0,
                Color.BLACK.toColor()
            )

            val t21 = Triangle(
                Vertex(-2.0, 31.5, 120.0),
                Vertex(0.0, 31.5, 118.0),
                Vertex(-2.0, -31.5, 120.0),
                0.2,
                0.8,
                0.0,
                0.0,
                Color.BLACK.toColor()
            )

            val t22 = Triangle(
                Vertex(-2.0, -31.5, 120.0),
                Vertex(0.0, 31.5, 118.0),
                Vertex(0.0, -31.5, 118.0),
                0.2,
                0.8,
                0.0,
                0.0,
                Color.BLACK.toColor()
            )

            val s1 = Sphere(
                Vertex(-30.0, -15.0, 90.0),
                25,
                0.2,
                0.2,
                0.0,
                0.6,
                Color.WHITE.toColor()
            )

            val s2 = Sphere(
                Vertex(-5.0, -25.0, 60.0),
                7,
                0.2,
                0.4,
                0.4,
                0.0,
                Color.DKGRAY.toColor()
            )

            val s3 = Sphere(
                Vertex(0.0, 10.0, 70.0),
                7,
                0.2,
                0.4,
                0.4,
                0.0,
                Color.RED.toColor()
            )

            val s4 = Sphere(
                Vertex(5.0, -25.0, 50.0),
                7,
                0.2,
                0.4,
                0.4,
                0.0,
                Color.YELLOW.toColor()
            )

            val shapes = arrayListOf(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13,
                t14, t15, t16, t17, t18, t19, t20, t21, t22, s1, s2, s3, s4)

            val camera = Vertex(0.0, 0.0, 0.0)

            for (y in 0..449) {
                for (x in 0..799) {
                    val pixel = Vertex(16 * x / 799.0 - 8, 4.5 - y * 9 / 449.0, 10.0)
                    val rd = pixel.minus(camera).normalize()
                    val c = traceRay(camera, rd, shapes, camera, 0, null)
                    surface.setPixel(x, y, c.toArgb())
                }
            }

            val endTime = System.currentTimeMillis()

            elapsedTimeTextView.text = (endTime - startTime).toString() + " milisaniye."

        }
    }
}