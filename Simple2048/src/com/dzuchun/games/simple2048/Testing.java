package com.dzuchun.games.simple2048;

import java.awt.Point;

public class Testing {

	public static void main(String[] args) 
	{
		GameFrame a = new GameFrame(5);
		GraphicalPlate b = new GraphicalPlate(2);
		b.setPos(new Point(10, 10));
		a.addPlate(b);
		b.scheduleMove(new Point(120, 80));
		a.performAnimation(500);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		b.scheduleMove(new Point(0, 160));
		a.performAnimation(500);
	}

}
