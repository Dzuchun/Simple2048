package com.dzuchun.games.simple2048;

import java.awt.Point;

public class Testing {

	public static void main(String[] args) 
	{
		GameFrame a = new GameFrame(5);
		GraphicalPlate b = new GraphicalPlate(2);
		b.setPos(new Point(10, 10));
		a.addPlate(2, new Point(10, 10));
		b.scheduleMove(new Point(50, 50));
		a.performAnimation(1000);
		b.setPos(new Point(50, 50));
	}

}
