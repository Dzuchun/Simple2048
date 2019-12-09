package com.dzuchun.math;

public class Value 
{
	private double valueD;
	private int valueI;
	private long valueL;
	public Value (int value)
	{
		this.valueI = value;
		this.valueD = (double)value;
		this.valueL = (long)value;
	}
	public Value (long value)
	{
		this.valueL = value;
		this.valueD = (double)value;
		this.valueI = (int)value;
	}
	
	public boolean greaterThan(Value v)
	{
		return (v.valueI < this.valueI);
	}
	
	public int getIntValue ()
	{
		return (this.valueI);
	}
}
