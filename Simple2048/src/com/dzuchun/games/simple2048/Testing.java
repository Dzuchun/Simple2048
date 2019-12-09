package com.dzuchun.games.simple2048;

import java.awt.Point;

public class Testing {

	public static void main(String[] args) 
	{
		ResourceLoader.load();
		GameFrame a = new GameFrame(5);
		GraphicalPlate b = new GraphicalPlate(2);
		b.setPos(new Point(0, 1));
		a.addPlate(b);
		try {
			b.addAnimation(1000, new Point(42, 1));
		} catch (IntersectsAnimationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("1");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("2");
		try {
			b.addAnimation(1000, new Point(42, 124));
		} catch (IntersectsAnimationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
