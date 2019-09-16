package com.example.ray_tracing_triangle

import android.graphics.Color

abstract class Shape{
    lateinit var shapeColor: Color

    abstract fun intersect(ro: Vertex, rd: Vertex): Double
}