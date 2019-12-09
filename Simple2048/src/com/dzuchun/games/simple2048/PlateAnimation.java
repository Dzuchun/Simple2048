package com.dzuchun.games.simple2048;

import java.awt.Graphics;
import java.awt.Point;

import com.dzuchun.math.LinePart;
import com.dzuchun.math.Value;

public class PlateAnimation extends AbstractAnimation {

	private Point beginPos, endPos;
	private GraphicalPlate plate;
	public PlateAnimation (long beginTime, long endTime, GraphicalPlate plate, Point beginPos, Point endPos) throws IntersectsAnimationException
	{
		super(beginTime, endTime);
		this.beginPos = beginPos;
		this.endPos = endPos;
		this.plate = plate;
	}
	@Override
	public void draw (Graphics g, long time)
	{
		super.draw(g, time);
		this.plate.setPos(this.getPos(this.getPart()));
		this.plate.draw(g);
	}
	private double getPart()
	{
		return(this.partCompleted);
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
			if ((ani.plate.equals(this.plate)) && ((new LinePart(new Value(this.beginTime), new Value(this.endTime))).intersects(new LinePart(new Value(ani.beginTime), new Value(ani.endTime)))))
			{
				return true;
			}
		}
		return false;
	}
}
