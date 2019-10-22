package com.kaganndemirr.raytracing.reflection

import android.graphics.Color

import kotlin.math.sqrt

class Sphere: Shape{
    private val center: Vertex
    private val radius: Int

    constructor(center: Vertex, radius: Int, ambient: Double, diffuse: Double, specular: Double, reflection: Double,  shapeColor: Color){
        this.center = center
        this.radius = radius
        this.ambient = ambient
        this.diffuse = diffuse
        this.specular = specular
        this.reflection = reflection
        this.shapeColor = shapeColor
    }

    override fun intersect(ro: Vertex, rd: Vertex): Double {
        val l = center - ro
        val s = l * rd
        val l2 = l * l
        val r2 = radius * radius

        if (s < 0 && l2 > r2)
            return 0.0

        val s2 = s * s
        val m2 = l2 - s2

        if (m2 > r2)
            return 0.0

        val q = sqrt(r2 - m2)

        return if (l2 > r2)
            s - q
        else
            return s + q
    }

    override fun normalAt(v: Vertex): Vertex {
        return (v - center) / radius
    }
}