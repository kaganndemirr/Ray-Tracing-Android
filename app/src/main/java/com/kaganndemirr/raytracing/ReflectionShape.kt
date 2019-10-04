package com.kaganndemirr.raytracing

import android.graphics.Color

abstract class ReflectionShape{
    var ambient = 0.0
    var diffuse = 0.0
    var specular = 0.0
    var reflection = 0.0
    lateinit var shapeColor: Color

    abstract fun intersect(ro: Vertex, rd: Vertex): Double
    abstract fun normalAt(v: Vertex): Vertex
}