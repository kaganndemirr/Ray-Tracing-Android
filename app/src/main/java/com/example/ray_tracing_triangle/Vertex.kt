package com.example.ray_tracing_triangle

import kotlin.math.sqrt

class Vertex(x: Double, y: Double, z: Double){
    private var x: Double = x
    private var y: Double = y
    private var z: Double = z

    fun normalize(): Vertex{
        val length: Double = sqrt(x * x + y * y + z * z)
        x /= length
        y /= length
        z /= length
        return this
    }

    fun length(): Double{
        return sqrt(x * x + y * y + z * z)
    }

    fun crossProduct(v: Vertex): Vertex{
        return Vertex(y * v.z - z * v.y,z * v.x - x * v.z,x * v.y - y * v.x)
    }

    fun add(v: Vertex): Vertex{
        return Vertex(x + v.x, y + v.y, z + v.z)
    }

    fun add(d: Double): Vertex{
        return Vertex(x + d, y + d, z + d)
    }

    fun subs(v: Vertex): Vertex{
        return Vertex(x - v.x, y - v.y, z - v.z)
    }

    fun mul(v: Vertex): Double{
        return x * v.x +y * v.y + z * v.z
    }

    fun mul(d: Double): Vertex{
        return Vertex(x * d, y * d, z * d)
    }

    fun div(d: Double): Vertex{
        return Vertex(x / d, y / d, z / d)
    }
}