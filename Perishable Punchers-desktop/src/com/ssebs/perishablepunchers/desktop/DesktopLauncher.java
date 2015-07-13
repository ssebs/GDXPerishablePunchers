package com.ssebs.perishablepunchers.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ssebs.perishablepunchers.PPMain;

public class DesktopLauncher
{
	public static void main(String[] arg)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Perishable Punchers";
		config.width = 1280;
		config.height = 720;
		config.addIcon("data/logo32.png", Files.FileType.Internal);
		config.addIcon("data/Logo16.png", FileType.Internal);
		//config.fullscreen = true;
		new LwjglApplication(new PPMain(), config);
	}
}
