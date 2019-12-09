package com.dzuchun.games.simple2048;

import java.awt.Graphics;
import java.util.Vector;

public abstract class AbstractAnimation 
{
	private static Vector<AbstractAnimation> animations = new Vector<AbstractAnimation>(0);
	public static void drawAll(Graphics g, long time)
	{
		synchronized (animations)
		{
			AbstractAnimation animation;
			for (int i=0; i<animations.size(); i++)
			{
				animation = animations.get(i);
				if (animation.endTime <= time)
				{
					System.out.println("Completing " + animation.toString());
					animation.complete(g);
				}
				else
				{
					animation.draw(g, time);
				}
			}
		}
	}
	public static boolean allComplete()
	{
		synchronized (animations)
		{
			return (animations.isEmpty());
		}
	}
	
	protected AbstractAnimation (long beginTime, long endTime) throws IntersectsAnimationException
	{
		this.beginTime = beginTime;
		this.endTime = endTime;
		for (AbstractAnimation animation : animations)
		{
			if (this.interrupts(animation))
			{
				throw (new IntersectsAnimationException());
			}
		}
		synchronized (animations)
		{
			animations.add(this);
		}
	}
	public void complete(Graphics g)
	{
		this.draw(g, this.endTime);
		synchronized (animations)
		{
			animations.remove(this);
		}
	}
	protected long beginTime;
	protected long endTime;
	protected double partCompleted;
	/**
	 * Must be overriden, and invoked in overriden method.
	 * @param g
	 * @param time
	 */
	public void draw (Graphics g, long time)
	{
		this.partCompleted = ((double)time - (double)this.beginTime) / ((double)this.endTime - (double)this.beginTime);
	}
	
	protected abstract boolean interrupts (AbstractAnimation animation);
}
class IntersectsAnimationException extends Exception
{
	private static final long serialVersionUID = 1L;
}