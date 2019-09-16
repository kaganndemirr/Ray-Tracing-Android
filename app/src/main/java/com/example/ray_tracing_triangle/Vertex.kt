package com.example.ray_tracing_triangle

import kotlin.math.sqrt

class Vertex(x: Double, y: Double, z: Double){
    private var x = x
    private var y = y
    private var z = z

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

    operator fun plus(v: Vertex): Vertex{
        return Vertex(x + v.x, y + v.y, z + v.z)
    }

    operator fun plus(d: Double): Vertex{
        return Vertex(x + d, y + d, z + d)
    }

    operator fun minus(v: Vertex): Vertex{
        return Vertex(x - v.x, y - v.y, z - v.z)
    }

    operator fun times(v: Vertex): Double{
        return x * v.x +y * v.y + z * v.z
    }

    operator fun times(d: Double): Vertex{
        return Vertex(x * d, y * d, z * d)
    }

    operator fun div(d: Double): Vertex{
        return Vertex(x / d, y / d, z / d)
    }
}