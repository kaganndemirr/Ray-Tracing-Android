package com.example.ray_tracing_triangle

import kotlin.math.abs

import android.graphics.Color


class Triangle: Shape{
    private var v0: Vertex
    private var v1: Vertex
    private var v2: Vertex

    constructor(v0: Vertex, v1:Vertex, v2:Vertex, shapeColor: Color){
        this.v0 = v0
        this.v1 = v1
        this.v2 = v2
        this.shapeColor = shapeColor
    }

    override fun intersect(ro: Vertex, rd: Vertex): Double{
        val normal: Vertex = (v1.subs(v0)).crossProduct(v2.subs(v0))
        val r: Vertex
        val s: Double
        val s1: Double
        val s2: Double
        val s3: Double

        val d: Double = -(normal.mul(v0))
        val t: Double = -(normal.mul(ro.add(d))) / (normal.mul(rd))

        if(t > 0){
            r = ro.add(rd.mul(t))

            s  = (v1.subs(v0)).crossProduct(v2.subs(v0)).length()
            s1 = (r.subs(v0)).crossProduct(v2.subs(v0)).length()
            s2 = (v1.subs(v0)).crossProduct(r.subs(v0)).length()
            s3 = (v1.subs(r)).crossProduct(v2.subs(r)).length()

            val difference: Double = abs(s - (s1 + s2 + s3))
            val epsilon = 0.005

            when(difference <= epsilon){
                true -> return t
                false -> return 0.0
            }
        }
        else
            return 0.0
    }
}
