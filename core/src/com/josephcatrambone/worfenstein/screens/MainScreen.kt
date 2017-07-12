package com.josephcatrambone.worfenstein.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.josephcatrambone.worfenstein.*

/**
 * Created by Jo on 2017-07-08.
 */
class MainScreen : Screen() {
	var isInitialized = false
	val shapeRenderer = ShapeRenderer()
	var player = Player()
	lateinit var level:Level

	override fun create() {
		player.x = 1f
		player.y = 1f
		level = Level(Gdx.app.files.internal("test.map"))
		isInitialized = true
	}

	override fun wake() { // Called when we're put on the stack.
		if(!this.isInitialized) {
			create()
			this.isInitialized = true
		}
	}

	override fun dispose() {

	}

	override fun update() {
		val timeDelta = Gdx.graphics.deltaTime

		// DEBUG INPUT HANDLING
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			val deltaVec = player.heading * player.speed * timeDelta
			player.x += deltaVec.x
			player.y += deltaVec.y
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			val deltaVec = player.heading * player.speed * timeDelta
			player.x -= deltaVec.x
			player.y -= deltaVec.y
		}

		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			player.rotation += player.rotateSpeed*timeDelta
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			player.rotation -= player.rotateSpeed*timeDelta
		}
		// END OF DEBUG INPUT HANDLING
	}

	override fun render() {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

		shapeRenderer.setAutoShapeType(true)
		shapeRenderer.begin()
		// Render the map
		level.draw(shapeRenderer, player)
		shapeRenderer.end()
	}

}