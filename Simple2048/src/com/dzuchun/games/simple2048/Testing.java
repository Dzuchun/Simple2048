package com.dzuchun.games.simple2048;

import java.awt.Point;

public class Testing {

	public static void main(String[] args) 
	{
		ResourceLoader.load();
		GameFrame a = new GameFrame(5);
		GraphicalPlate b = new GraphicalPlate(2);
		a.addPlate(b);
		b.setPos(new Point(0, 42));
		try
		{
			while (true)
			{
					b.performAnimation(new Point(125, 42));
					Thread.sleep(GameFrame.getAnimationTimeMillis());
					b.performAnimation(new Point(0, 42));
					Thread.sleep(GameFrame.getAnimationTimeMillis());
			}
		}
		catch (IntersectsAnimationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


