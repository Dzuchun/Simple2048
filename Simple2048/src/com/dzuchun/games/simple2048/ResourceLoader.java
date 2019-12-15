package com.dzuchun.games.simple2048;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ResourceLoader 
{
	public static Image plateWorth2Icon;
	public static Image plateWorth4Icon;
	public static Image plateWorth8Icon;
	public static Image plateWorth16Icon;
	public static Image plateWorth32Icon;
	public static Image plateWorth64Icon;
	public static Image plateWorth128Icon;
	public static Image plateWorth256Icon;
	public static Image plateWorth512Icon;
	public static Image plateWorth1024Icon;
	public static Image plateWorth2048Icon;
	public static void load()
	{
		plateWorth2Icon = (new ImageIcon("src/resources/Plate_Worth_2.jpg")).getImage();
		plateWorth4Icon = (new ImageIcon("src/resources/Plate_Worth_4.png")).getImage();
		plateWorth8Icon = (new ImageIcon("src/resources/Plate_Worth_8.png")).getImage();
		plateWorth16Icon = (new ImageIcon("src/resources/Plate_Worth_16.png")).getImage();
		plateWorth32Icon = (new ImageIcon("src/resources/Plate_Worth_32.png")).getImage();
		plateWorth64Icon = (new ImageIcon("src/resources/Plate_Worth_64.png")).getImage();
		plateWorth128Icon = (new ImageIcon("src/resources/Plate_Worth_128.png")).getImage();
		plateWorth256Icon = (new ImageIcon("src/resources/Plate_Worth_256.png")).getImage();
		plateWorth512Icon = (new ImageIcon("src/resources/Plate_Worth_512.png")).getImage();
		plateWorth1024Icon = (new ImageIcon("src/resources/Plate_Worth_1024.png")).getImage();
		plateWorth2048Icon = (new ImageIcon("src/resources/Plate_Worth_2048.png")).getImage();
	}
}
