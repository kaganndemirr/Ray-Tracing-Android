package com.kaganndemirr.raytracing.triangle

import android.graphics.Color

abstract class Shape {
    lateinit var shapeColor: Color

    abstract fun intersect(ro: Vertex, rd: Vertex): Double
}