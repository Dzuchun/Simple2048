package com.dzuchun.games.simple2048;

import java.awt.Point;

public class Testing {

	public static void main(String[] args) 
	{
		ResourceLoader.load();
		GameFrame a = new GameFrame(5);
		GraphicalPlate b = new GraphicalPlate(2);
		b.setPos(new Point(10, 10));
		a.addPlate(b);
		a.performAnimation(500);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		a.performAnimation(500);
	}

}
