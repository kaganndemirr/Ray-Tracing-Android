package com.kaganndemirr.raytracing

import android.graphics.Color

abstract class V2Shape {
    lateinit var shapeColor: Color
    var ambient: Double = 0.0
    var dif: Double = 0.0
    var spec: Double = 0.0
    var refl: Double = 0.0

    abstract fun intersect(ro: Vertex, rd: Vertex): Double
    abstract fun normalAt(p: Vertex): Vertex
}