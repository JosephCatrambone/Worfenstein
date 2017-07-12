package com.josephcatrambone.worfenstein.screens

/**
 * Created by Jo on 2017-07-08.
 */

abstract class Screen {
	abstract fun create()
	abstract fun wake() // Called after the screen is put on top of the stack.
	abstract fun dispose()
	abstract fun update()
	abstract fun render()
}