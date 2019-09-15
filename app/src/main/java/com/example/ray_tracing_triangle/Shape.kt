package com.example.ray_tracing_triangle

import android.graphics.Color

abstract class Shape{
    lateinit var shapeColor: Color

    abstract fun intersect(rO: Vertex, rd: Vertex): Double
}