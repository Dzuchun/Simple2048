package com.dzuchun.games.simple2048;

import java.awt.Point;

public class Testing {

	public static void main(String[] args) 
	{
		ResourceLoader.load();
		GameFrame a = new GameFrame(5);
		GraphicalPlate b = new GraphicalPlate(2);
		try
		{
			while (true)
			{
					b.setPos(GameFrame.getPointForPos(0, 0));
					Thread.sleep(GameFrame.getAnimationTimeMillis());
					a.addPlate(b);
					Thread.sleep(GameFrame.getAnimationTimeMillis());
					b.addAnimation(GameFrame.getPointForPos(1, 0));
					GameFrame.drawAnimations();
					Thread.sleep(GameFrame.getAnimationTimeMillis());
					b.addAnimation(GameFrame.getPointForPos(1, 1));
					a.removePlate(b);
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


