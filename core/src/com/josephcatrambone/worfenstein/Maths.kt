package com.josephcatrambone.worfenstein

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * Created by Jo on 2017-07-09.
 */
class Vec(var x:Float=0f, var y:Float=0f, var z:Float=0f, var w:Float=0f) {

	// libGDX Interop section.
	fun constructor(v2: Vector2) {
		this.x = v2.x
		this.y = v2.y
		z = 0f
		w = 0f
	}
	fun toGDXVector2(): Vector2 = Vector2(this.x, this.y)
	fun toGDXVector3(): Vector3 = Vector3(this.x, this.y, this.z)
	// End libGDX interop section

	val isZero:Boolean
		get():Boolean = x==0f && y==0f && z==0f && w==0f

	val magnitude:Float
		get():Float = this.dot(this)

	var data:FloatArray
		get() = floatArrayOf(x, y, z, w)
		set(value:FloatArray) {
			this.x = value.getOrElse(0, {_ -> 0f})
			this.y = value.getOrElse(1, {_ -> 0f})
			this.z = value.getOrElse(2, {_ -> 0f})
			this.w = value.getOrElse(3, {_ -> 0f})
		}

	operator fun plus(value:Float):Vec = Vec(this.x+value, this.y+value, this.z+value, this.w+value)
	operator fun minus(value:Float):Vec = Vec(this.x-value, this.y-value, this.z-value, this.w-value)
	operator fun times(value:Float):Vec = Vec(this.x*value, this.y*value, this.z*value, this.w*value)
	operator fun div(value:Float):Vec = Vec(this.x/value, this.y/value, this.z/value, this.w/value)

	operator fun plus(other:Vec):Vec = Vec(this.x+other.x, this.y+other.y, this.z+other.z, this.w+other.w)
	operator fun minus(other:Vec):Vec = Vec(this.x-other.x, this.y-other.y, this.z-other.z, this.w-other.w)
	operator fun times(other:Vec):Vec = Vec(this.x*other.x, this.y*other.y, this.z*other.z, this.w*other.w)
	operator fun div(other:Vec):Vec = Vec(this.x/other.x, this.y/other.y, this.z/other.z, this.w/other.w) // TODO: This probably shouldn't exist because of the default zeros.

	fun sum():Float {
		return x+y+z+w
	}

	fun dot(other:Vec):Float {
		return (this * other).sum()
	}

	// Perform the cross product with this as a vector2
	fun cross2(other:Vec):Float = x*other.y - y*other.x

	fun cross3(other:Vec):Vec {
		TODO()
	}

	fun normalized():Vec {
		// elements / sqrt(sum(elem ^2))
		val mag = this.magnitude
		// If we have no magnitude, just return a zero vec.
		if(mag == 0f) {
			TODO("Unhandled case: Normalizing zero-length vector")
		}

		return Vec(x/mag, y/mag, z/mag, w/mag)
	}

	fun normalize() {
		val mag = this.magnitude
		if(mag == 0f) { TODO() }
		x /= mag
		y /= mag
		z /= mag
		w /= mag
	}
}

class Line(var start:Vec, var end:Vec) {
	/***
	 *
	 */
	fun intersection2D(other:Line, epsilon:Float = 1e-8f): Vec? {
		// Begin with line-line intersection.
		val determinant = ((start.x-end.x)*(other.start.y-other.end.y))-((start.y-end.y)*(other.start.x-other.end.x))
		if(Math.abs(determinant.toDouble()).toFloat() < epsilon) {
			return null;
		}

		val candidatePoint = Vec(
			((start.x*end.y - start.y*end.x)*(other.start.x-other.end.x))-((start.x-end.x)*(other.start.x*other.end.y - other.start.y*other.end.x)),
			((start.x*end.y - start.y*end.x)*(other.start.y-other.end.y))-((start.y-end.y)*(other.start.x*other.end.y - other.start.y*other.end.x))
		)/determinant

		// If the lines are infinite, we're done.  No more work.
		return candidatePoint
	}

	fun segmentIntersection2D(other:Line): Vec? {
		val a = this.start
		val b = this.end
		val c = other.start
		val d = other.end

		val r = b-a
		val s = d-c

		val rxs = r.cross2(s)
		val t:Float = (c-a).cross2(s)/rxs
		val u:Float = (c-a).cross2(r)/rxs

		if(t < 0 || t > 1 || u < 0 || u > 1) {
			return null;
		}
		return a + r*t
	}

	fun pointOnLine(pt:Vec, epsilon:Float = 1e-6f):Boolean {
		// Is this point a solution to this line?
		if(epsilon == 0f) {
			// THIS IS A BAD IDEA!  NUMERICAL PRECISION IS A FACTOR!
			// p1 + t*(p2-p1) = pt?
			// Just solve for t, and if the value is between 0 and 1, it's on the line.
			// t*(p2-p1) = pt - p1
			// t = (pt - p1)/(p2-p1)
			// Unfortunately, we've gotta' do this component-wise.
			val tX = (pt.x - start.x) / (end.x - start.x)
			if (tX < 0 || tX > 1) {
				return false
			}
			val tY = (pt.y - start.y) / (end.y - start.y)
			if (tY < 0 || tY > 1) {
				return false
			}
			return true
		} else {
			TODO("Bugfix")
			return ((Math.abs(((end.y-start.y)*pt.x - (end.x-start.x)*pt.y + end.x*start.y - end.y*start.x).toDouble()))/Math.sqrt(((end.x-start.x)*(end.x-start.x) + (end.y-start.y)*(end.y-start.y)).toDouble()).toFloat()) < epsilon
		}
	}
}

class Poligon(val points:MutableList<Vec>) {

}