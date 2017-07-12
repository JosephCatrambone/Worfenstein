package com.josephcatrambone.worfenstein;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.josephcatrambone.worfenstein.screens.MainScreen;
import com.josephcatrambone.worfenstein.screens.Screen;

import java.util.Stack;

public class MainGame extends ApplicationAdapter {
	public static Stack<Screen> screenStack = new Stack<Screen>();
	public static AssetManager assets = new AssetManager();
	
	@Override
	public void create () {
		Screen newScreen = new MainScreen();
		newScreen.create();
		screenStack.push(newScreen);
	}

	@Override
	public void render () {
		screenStack.peek().render();
		screenStack.peek().update();
	}
	
	@Override
	public void dispose () {
	}
}
