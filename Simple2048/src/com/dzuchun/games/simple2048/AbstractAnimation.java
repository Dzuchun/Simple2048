package com.dzuchun.games.simple2048;

import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
				if (!animation.initialized)
				{
					//System.out.println("Initing animation " + animation.toString() + " at time " + time);
					animation.initialize(time);
				}
				animation.draw(g, time);
			}
		}
	}
	public static boolean isAllComplete()
	{
		synchronized (animations)
		{
			return (animations.isEmpty());
		}
	} 
	protected long duration;
	protected AbstractAnimation (long duration) throws IntersectsAnimationException
	{
		AbstractAnimation animation;
		synchronized(animations)
		{
			for (int i=0; i<animations.size(); i++)
			{
				animation = animations.get(i);
				if (this.interrupts(animation))
				{
					throw (new IntersectsAnimationException());
				}
			}
			this.duration = duration;
		}
		synchronized (animations)
		{
			animations.add(this);
		}
	}
	protected boolean initialized;
	protected long beginTime;
	protected long endTime;
	protected void initialize(long time)
	{
		this.initialized = true;
		this.beginTime = time;
		this.endTime = this.beginTime + this.duration;
	}
	protected void complete(Graphics g)
	{
		synchronized (animations)
		{
			animations.remove(this);
		}
		this.drawAnimation(g, this.endTime);
	}
	protected double partCompleted;
	public final void draw (Graphics g, long time)
	{
		this.partCompleted = ((double)time - (double)this.beginTime) / ((double)this.endTime - (double)this.beginTime);
		//System.out.println("Animation " + this.toString() + " completed " + this.partCompleted);
		//System.out.println("begin - " + this.beginTime + " current - " + time + " end - " + this.endTime);
		if (time >= this.endTime)
		{
			this.complete(g);
		}
		else
		{
			this.drawAnimation(g, time);
		}
	}
	protected abstract void drawAnimation (Graphics g, long time);
	
	protected abstract boolean interrupts (AbstractAnimation animation);
	
	public static Boolean hasAs (Method method, Object o)
	{
		synchronized(animations)
		{
			AbstractAnimation animation;
			try
			{
				for (int i=0; i<animations.size(); i++)
				{
					animation = animations.get(i);
					if((Boolean)method.invoke(animation, o))
					{
						return true;
					}
				}
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e)
			{
				e.printStackTrace();
				return null;
			}
			return false;
		}
	}
}
class IntersectsAnimationException extends Exception
{
	private static final long serialVersionUID = 1L;
}