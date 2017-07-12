package com.josephcatrambone.worfenstein

/**
 * Created by Jo on 2017-07-08.
 */
class Player {
	var currentSector: Int = 0

	var x: Float = 0f
	var y: Float = 0f
	var z: Float = 0f
	val location : Vec
		get() = Vec(x, y, z)

	var crouching = false
	val currentHeadHeight:Float
		get() = if(crouching) { headHeight+z } else { duckHeight+z }

	var speed: Float = 10f
	var rotateSpeed: Float = 50f

	var headHeight: Float = 1.5f
	var duckHeight: Float = 0.5f

	var rotation: Float = 0f
	val sightLine: Line
		get() = Line(location, location+(heading*sightLimit))
	val heading: Vec
		get() = Vec(Math.cos(Math.toRadians(this.rotation.toDouble())).toFloat(), Math.sin(Math.toRadians(this.rotation.toDouble())).toFloat())

	var fov: Float = 45f
	var sightLimit: Float = 100f
	var sightNearLimit: Float = 0.1f
}