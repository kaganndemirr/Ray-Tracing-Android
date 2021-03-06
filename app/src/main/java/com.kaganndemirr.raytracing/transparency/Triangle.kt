package com.kaganndemirr.raytracing.transparency

import android.graphics.Color

import kotlin.math.abs

class Triangle: Shape
{
    private val v0: Vertex
    private val v1: Vertex
    private val v2: Vertex

    constructor(v0: Vertex, v1: Vertex, v2: Vertex, ambient: Double, diffuse: Double,
                specular: Double, reflection: Double, shapeColor: Color){
        this.v0 = v0
        this.v1 = v1
        this.v2 = v2
        this.ambient = ambient
        this.diffuse = diffuse
        this.specular = specular
        this.reflection = reflection
        this.shapeColor = shapeColor
    }

    override fun intersect (ro: Vertex, rd: Vertex): Double
    {
        val normal: Vertex = (v1 - v0).crossProduct(v2 - v0)
        val r: Vertex
        val s: Double
        val s1: Double
        val s2: Double
        val s3: Double

        val d: Double = -(normal * v0)
        val t: Double = -(normal * ro + d) / (normal * rd)

        if (t > 0)
        {
            r = ro + rd * t

            s = (v1 - v0).crossProduct(v2 - v0).length()
            s1 = (r - v0).crossProduct(v2 - v0).length()
            s2 = (v1 - v0).crossProduct(r - v0).length()
            s3 = (v1 - r).crossProduct(v2 - r).length()

            val difference: Double = abs(s - (s1 + s2 + s3))
            val epsilon = 0.005

            return if (difference <= epsilon) {
                t
            } else
                0.0
        }

        else
            return 0.0
    }

    override fun normalAt(v: Vertex): Vertex{
        return (v1 - v0).crossProduct(v2 - v0)
    }
}