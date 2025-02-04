package com.dzuchun.games.simple2048;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;

class GraphicalPlate
{
	private static Image ImageForWorth (int worth)
	{
		switch(worth)
		{
		case 2:
			return(ResourceLoader.plateWorth2Icon);
		case 4:
			return(ResourceLoader.plateWorth4Icon);
		case 8:
			return(ResourceLoader.plateWorth8Icon);
		case 16:
			return(ResourceLoader.plateWorth16Icon);
		case 32:
			return(ResourceLoader.plateWorth32Icon);
		case 64:
			return(ResourceLoader.plateWorth64Icon);
		case 128:
			return(ResourceLoader.plateWorth128Icon);
		case 256:
			return(ResourceLoader.plateWorth256Icon);
		case 512:
			return(ResourceLoader.plateWorth512Icon);
		case 1024:
			return(ResourceLoader.plateWorth1024Icon);
		case 2048:
			return(ResourceLoader.plateWorth2048Icon);
		default:
			return(null);
		}
	}
	private static Integer getNextWorth (int worth)
	{
		switch(worth)
		{
		case 2:
			return 4;
		case 4:
			return 8;
		case 8:
			return 16;
		case 16:
			return 32;
		case 32:
			return 64;
		case 64:
			return 128;
		case 128:
			return 256;
		case 256:
			return 512;
		case 512:
			return 1024;
		case 1024:
			return 2048;
		default:
			return null;
		}
	}
	
	private int worth;
	private Point pos;
	private Image image;
	
	public GraphicalPlate (int worth)
	{
		this.worth = worth;
		this.updateIcon();
	}
	public void setPos (Point newPos)
	{
		this.pos = newPos;
		this.isValid = false;
	}
	public Point getPos()
	{
		return (this.pos);
	}
	public void draw (Graphics g)
	{
		g.drawImage(this.image, this.pos.x+1, this.pos.y+1, GameFrame.getPlateSize()-1, GameFrame.getPlateSize()-1, new ImageObserver()
		{	
			@Override
			public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) 
			{
				// TODO Lol, what should be here?
				return false;
			}
		});
	}
	private boolean isValid;
	public void revalidate()
	{
		if ((this.pos.x%GameFrame.getPlateSize() != 0)||(this.pos.y%GameFrame.getPlateSize() != 0))
		{
			this.isValid = false;
		}
		if (!this.isValid)
		{
			this.validate();
			this.isValid = true;
		}
		//System.out.println("Revalidated plate at pos " + this.pos);
	}
	private void validate()
	{
		//System.out.println("Validating plate at coords: " + this.pos.toString());
		//System.out.print("Changing plate pos from " + this.pos.toString() + " to ");
		int plateSize = GameFrame.getPlateSize();
		int shaftle;
		Point newPos = this.getPos();
		if ((this.pos.x%plateSize) != 0)
		{
			shaftle = this.pos.x%plateSize;
			newPos.x = this.pos.x - shaftle;
			if (shaftle > plateSize/2)
			{ 
				newPos.x += plateSize; 
				//System.out.print("a bit more, due to shaflex = " + shaftle);
			}
		}
		if ((this.pos.y%plateSize) != 0)
		{
			shaftle = this.pos.y%plateSize;
			newPos.y = this.pos.y - shaftle;
			if (shaftle > plateSize/2)
			{ 
				newPos.y += plateSize; 
				//System.out.print("a bit more, due to shafley = " + shaftle);
			}
		}
		this.setPos(newPos);
		//System.out.println(this.pos.toString());
	}
	public Boolean hasAnimation()
	{
		@SuppressWarnings("rawtypes")
		Class[] a = {GraphicalPlate.class};
		try 
		{
			boolean b = AbstractAnimation.hasAs(PlateAnimation.class.getMethod("isFor", a), this);
			return b;
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	public void addAnimation (Point newPos) throws IntersectsAnimationException
	{
		System.out.println("Adding animation for plate " + this.toString() + " from " + this.pos.toString() + " to " + newPos.toString());
		if (!((this.pos.x == newPos.x)||(this.pos.y == newPos.y)))
		{
			System.out.println("WARNING! INCORRECT ANIMATION");	
		}
		new PlateAnimation(GameFrame.getAnimationTimeMillis(), this, newPos);
	}
	public int getWorth()
	{
		return(this.worth);
	}
	private boolean updateScheduled;
	private GraphicalPlate infusor;
	public void addUpdate(GraphicalPlate infusor)
	{
		//System.out.println("Infusing " + infusor.toString() + " to " + this.toString() + " hascode - " + this.hashCode());
		this.worth = getNextWorth(this.worth);
		this.updateScheduled = true;
		this.infusor = infusor;
	}
	private void updateIcon ()
	{
		this.image = ImageForWorth(this.worth);
	}
	public void worthUpdate(GameFrame gameFrame)
	{
		//System.out.println("Worthupdating " + this.toString());
		this.updateIcon();
		this.updateScheduled = false;
		gameFrame.removePlate(this.infusor);
		this.infusor = null;
	}
	public boolean isUpdateScheduled()
	{
		return(this.updateScheduled);
	}
}
class WrongPositionException extends Exception
{
	private static final long serialVersionUID = 1L;
}