package com.dzuchun.math;

public class LinePart 
{
	private Value begin;
	private Value end;
	public LinePart (Value begin, Value end)
	{
		this.begin = begin;
		this.end = end;
	}
	
	public boolean belongs (Value v)
	{
		return((v.greaterThan(this.begin))&&(this.end.greaterThan(v)));
	}
	public boolean intersects (LinePart part)
	{
		return((this.belongs(part.begin))||(this.belongs(part.end)));
	}
}
