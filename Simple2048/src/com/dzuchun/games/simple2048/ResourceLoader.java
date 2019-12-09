package com.dzuchun.games.simple2048;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ResourceLoader 
{
	public static Image plateWorth2;
	public static void load()
	{
		plateWorth2 = (new ImageIcon("src/resources/Plate_Worth_2.png")).getImage();
	}
}
