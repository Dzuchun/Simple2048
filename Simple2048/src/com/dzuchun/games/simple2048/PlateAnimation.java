package com.dzuchun.games.simple2048;

import java.awt.Graphics;
import java.awt.Point;

public class PlateAnimation extends AbstractAnimation 
{
	private Point beginPos, endPos;
	private GraphicalPlate plate;
	public PlateAnimation (long duration, GraphicalPlate plate, Point endPos) throws IntersectsAnimationException
	{
		super(duration);
		this.beginPos = plate.getPos();
		this.endPos = endPos;
		this.plate = plate;
		this.duration = duration;
	}
	public void drawAnimation (Graphics g, long time)
	{
		this.plate.setPos(this.getPos(this.getPart()));
		this.plate.draw(g);
	}
	private double getPart()
	{
		if (this.partCompleted<0.5)
		{
			return(2*this.partCompleted*this.partCompleted);
		}
		else
		{
			return(1-2*(1-this.partCompleted)*(1-this.partCompleted));
		}
	}
	private Point getPos(double part)
	{
		return new Point(this.beginPos.x + (int)(part * (this.endPos.x - this.beginPos.x)), this.beginPos.y + (int)(part * (this.endPos.y - this.beginPos.y)));
	}
	protected boolean interrupts (AbstractAnimation animation)
	{
		if (this.getClass().equals(animation.getClass()))
		{
			PlateAnimation ani = (PlateAnimation)animation;
			if ((ani.plate.equals(this.plate)))
			{
				return true;
			}
		}
		return false;
	}
}
