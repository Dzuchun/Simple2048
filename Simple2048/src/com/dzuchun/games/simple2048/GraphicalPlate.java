package com.dzuchun.games.simple2048;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;

class GraphicalPlate extends JPanel
{
	private static final long serialVersionUID = 1L;
	private static Image ImageForWorth (int worth)
	{
		switch(worth)
		{
		case 2:
			return(ResourceLoader.plateWorth2);
		default:
			return(null);
		}
	}
	
	private int worth;
	private Point pos;
	private Image image;
	
	public GraphicalPlate (int worth)
	{
		this.worth = worth;
		this.image = ImageForWorth(this.worth);
	}
	public void setPos (Point newPos)
	{
		this.pos = newPos;
	}
	public Point getPos()
	{
		return (this.pos);
	}
	
	@Override
	public void paintComponent (Graphics g)
	{
		this.draw(g);
	}
	
	@Override
	public void paint (Graphics g)
	{
		this.draw(g);
	}
	public void draw (Graphics g)
	{
		g.drawImage(this.image, this.pos.x, this.pos.y, GameFrame.getPlateSize(), GameFrame.getPlateSize(), new ImageObserver() 
		{	
			@Override
			public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) 
			{
				// TODO Lol, what should be here?
				return false;
			}
		});
	}
	public void addAnimation (long delay, Point newPos) throws IntersectsAnimationException
	{
		long currentTime = System.currentTimeMillis();
		new PlateAnimation(currentTime + delay, currentTime + delay + GameFrame.getAnimationTimeMillis(), this, this.pos, newPos);
		class DelayedNotificationThread extends Thread
		{
			private long time;
			public DelayedNotificationThread (long time)
			{
				this.time = time;
				this.setDaemon(true);
				this.setName("Delayed-Notification-Thread-" + this.getId());
			}
			public void run()
			{
				long sleep = time - System.currentTimeMillis();
				if (sleep > 0)
				{
					try 
					{
						Thread.sleep(sleep);
					} 
					catch (InterruptedException e) 
					{
						System.out.println("Unexpected exception:");
						e.printStackTrace();
					}
				}
				GameFrame.drawAnimations();
			}
		}
		(new DelayedNotificationThread(currentTime + delay)).start();
	}
}