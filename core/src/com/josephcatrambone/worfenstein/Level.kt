package com.josephcatrambone.worfenstein

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.collision.Ray as GDXRay
import com.badlogic.gdx.math.MathUtils.*

/**
 * Created by Jo on 2017-07-08.
 */
data class Sector(var walls: Array<Int>, var floorHeight:Float, var ceilingHeight:Float, var floorMaterial:Int, var ceilingMaterial:Int)

data class Wall(var start:Vec, var nextSector: Int, var materialIndex:Int)

class Level {
	val materials: Array<Texture>
	val walls: Array<Wall>
	val sectors: Array<Sector>

	constructor(fileHandle: FileHandle) {
		val mats = mutableListOf<Texture>()
		val walls = mutableListOf<Wall>()
		val sectors = mutableListOf<Sector>()

		// Parse the map file.
		// Map format:
		// m id string // Defines a material (in the assets) with number 'n'.
		// w id x1 y1 x2 y2 -1 m // Define a wall
		// s id numwalls wid ... floorheight ceilheight floormat ceilmat
		// TODO: We should probably pre-define the number of materials and walls and such so we don't have to worry about using mutable lists or going out of sync.
		val fin = fileHandle.file()
		val lines = fin.readLines()

		// Read through the lines to load the file.
		lines.forEach({line ->
			val tokens = line.split(' ')
			when(tokens[0]) {
				"#" -> {} // Comment
				"m" -> {
					val id = tokens[1].toInt()
					val materialName = tokens[2]
					MainGame.assets.load(materialName, Texture::class.java)
					MainGame.assets.finishLoading() // Lazy.  We can do better.
					assert(mats.size == id)
					mats.add(MainGame.assets.get(materialName))
				}
				"w" -> {
					val id = tokens[1].toInt()
					val x = tokens[2].toFloat()
					val y = tokens[3].toFloat()
					val nextSector = tokens[4].toInt()
					val materialIndex = tokens[5].toInt()
					val newWall = Wall(Vec(x, y), nextSector, materialIndex)
					assert(walls.size == id)
					walls.add(newWall)
				}
				"s" -> {
					val id = tokens[1].toInt()
					val numWalls = tokens[2].toInt()
					val sectorWalls = Array<Int>(numWalls, { i -> tokens[i+3].toInt() })
					val floorHeight = tokens[3+numWalls].toFloat()
					val ceilHeight = tokens[4+numWalls].toFloat()
					val floorMat = tokens[5+numWalls].toInt()
					val ceilMat = tokens[6+numWalls].toInt()
					assert(sectors.size == id)
					sectors.add(Sector(sectorWalls, floorHeight, ceilHeight, floorMat, ceilMat))
				}
				else -> {
					// What the fuck is this shit?
				}
			}
		})

		materials = mats.toTypedArray()
		this.walls = walls.toTypedArray()
		this.sectors = sectors.toTypedArray()

		// TODO: Do we need to release the file handle?
	}

	fun draw(shapeRenderer: ShapeRenderer, player: Player, sectorIndex:Int = player.currentSector, columnSweep:IntRange=(0 until Gdx.graphics.width), rowSweep:IntRange = (0 until Gdx.graphics.height), depthLimit:Int = 10) {
		// Draw the map as the player sees it in 3D.
		shapeRenderer.color = Color.WHITE
		columnSweep.forEach({ column ->
			// Where is this column with respect to the player's FOV.
			// Interpolate from zero to FOV.  column/width * FOV = degree with respect to player.
			val columnToFOVAngleInPlayerSpace = (-(column-(Gdx.graphics.width/2)).toFloat()/Gdx.graphics.width.toFloat())*player.fov
			val worldViewAngle = (player.rotation + columnToFOVAngleInPlayerSpace)*degreesToRadians
			//val playerFace: GDXRay = GDXRay(Vec(player.x, player.y).toGDXVector3(), Vector3(cos(worldViewAngle), sin(worldViewAngle), 0f))
			// Now our rays from the player originate at the player position and go outward WRT that.
			var nearestWallIndex = -1
			var nearestWallDistance = Float.MAX_VALUE
			val sectorWalls = this.sectors[sectorIndex].walls
			val playerSightLine = Line(
				player.location,
				player.location + Vec(cos(worldViewAngle)*player.sightLimit, sin(worldViewAngle)*player.sightLimit)
			)
			for(indexIndex in 0 until sectorWalls.size) {
				val wallStartIndex = sectorWalls[indexIndex]
				val wallEndIndex = sectorWalls[(indexIndex+1)%sectorWalls.size]
				// Look up the wall positions in the map list.
				val wallStart = this.walls[wallStartIndex]
				val wallEnd = this.walls[wallEndIndex]
				val line = Line(wallStart.start, wallEnd.start)
				// The walls array in sectors is an array of indices!
				val hit = playerSightLine.segmentIntersection2D(line)
				if(hit != null) {
					// Hit this wall.  Yay.  How far is it?
					val delta = player.location - hit
					val distanceSquared = delta.dot(delta)
					if(distanceSquared < nearestWallDistance) {
						nearestWallIndex = wallStartIndex
						nearestWallDistance = distanceSquared
					}
				}
			}
			// Now render this wall as appropriate, recursing if needed.
			if(nearestWallIndex != -1 && (nearestWallDistance > player.sightNearLimit || walls[nearestWallIndex].nextSector != -1)) {
				// Draw the floor and the ceiling of this segment.
				// Conditions: if the other sector is higher than this one, we have to draw the wall material in that range.
				// If the other sector's ceiling is lower than this, we need to draw the wall material there, too.
				when(nearestWallIndex) {
					0 -> shapeRenderer.color = Color.BLUE
					1 -> shapeRenderer.color = Color.RED
					2 -> shapeRenderer.color = Color.GREEN
					3 -> shapeRenderer.color = Color.YELLOW
					else -> shapeRenderer.color = Color.WHITE
				}
				// Draw a vertical line from the floor to the ceiling.
				// TODO: Copy a texture sample.
				nearestWallDistance = Math.sqrt(nearestWallDistance.toDouble()).toFloat()
				val drawScalar = (rowSweep.endInclusive-rowSweep.start)*player.sightNearLimit
				shapeRenderer.line(
					column.toFloat(),
					drawScalar*((this.sectors[sectorIndex].ceilingHeight-player.currentHeadHeight)/nearestWallDistance),
					column.toFloat(),
					drawScalar*((this.sectors[sectorIndex].floorHeight-player.currentHeadHeight)/nearestWallDistance)
				)

				if(walls[nearestWallIndex].nextSector != -1) {
					// Recurse and draw the stuff through the portal.
				}
			}
		})
	}
}