package com.kaganndemirr.raytracing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import java.sql.Ref
import kotlin.math.min
import kotlin.math.pow

class ReflectionActivity : AppCompatActivity(){

    private fun shadeDiffuse(s: ReflectionShape, iPoint: Vertex): Color {
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

    private fun shadeSpecular(s: ReflectionShape, iPoint: Vertex, camera: Vertex): Color{
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

    private fun shadingModel(s: ReflectionShape, diffuseColor: Color,  specularColor: Color, reflectedColor: Color,
                             ambient: Double, diffuse: Double, specular: Double, reflection: Double): Color
    {
        val ambientColor = s.shapeColor

        val r = min(255.0, ambient * ambientColor.red() + diffuse * diffuseColor.red() + specular * specularColor.red() + reflection * reflectedColor.red())
        val g = min(255.0, ambient * ambientColor.green() + diffuse * diffuseColor.green() + specular * specularColor.green() + reflection * reflectedColor.green())
        val b = min(255.0, ambient * ambientColor.blue() + diffuse * diffuseColor.blue() + specular * specularColor.blue() + reflection * reflectedColor.blue())

        return Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat())
    }

    private fun calculateReflection(s: ReflectionShape, iPoint: Vertex, rd: Vertex): Vertex{
        val normal = s.normalAt(iPoint).normalize()

        return (rd - normal * (normal * rd) * 2.0).normalize()
    }

    private fun traceRay(ro: Vertex, rd: Vertex, shapes: ArrayList<ReflectionShape>, camera: Vertex,
                         depth: Int, prevShape: ReflectionShape?): Color
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

        val button = findViewById<Button>(R.id.rRenderButton)
        val imageView = findViewById<ImageView>(R.id.rImageView)

        button.setOnClickListener {
            val surface = Bitmap.createBitmap(800, 450, Bitmap.Config.ARGB_8888)
            imageView.setImageBitmap(surface)

            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(-60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(-60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 0.0),Vertex(-60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(-60.0, -40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, 40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, 40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, 40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, 40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(-60.0, 40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(-60.0, 40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 0.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 0.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())

            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())
            val t1 = ReflectionTriangle(Vertex(60.0, -40.0, 120.0),Vertex(60.0, -40.0, 120.0), Vertex(-60.0, 40.0, 120.0),  0.3, 0.7, 0.0, 0.0, Color.MAGENTA.toColor())

        }
    }
}