package com.kaganndemirr.raytracing

import android.graphics.Color

import kotlin.math.sqrt

class Sphere: Shape{
    private val center: Vertex
    private val radius: Double

    constructor(center: Vertex, radius: Double, shapeColor: Color){
        this.center = center
        this.radius = radius
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
}