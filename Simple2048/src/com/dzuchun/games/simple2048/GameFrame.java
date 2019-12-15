package com.dzuchun.games.simple2048;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GameFrame extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private static int PLATE_SIZE = 100;
	private static long ANIMATION_TIME_MILLIS = 400;
	private static int FRAMES_PER_SECOND = 60;
	public static Point getPointForPos (int xCoordinate, int yCoordinate)
	{
		return(new Point(xCoordinate*PLATE_SIZE, yCoordinate*PLATE_SIZE));
	}
	public static Point getPointForPos (Point pos)
	{
		return(getPointForPos(pos.x, pos.y));
	}
	public static Point getPosForPoint (Point p)
	{
		//System.out.println("Returning " + new Point(p.x/(PLATE_SIZE-1), p.y/(PLATE_SIZE-1))); //TODO KOSTIL!!!
		return(new Point(p.x/PLATE_SIZE, p.y /PLATE_SIZE));
	}
	JLayeredPane canvas;
	private int size;
	JPanel mainPanel;
	private Vector<GraphicalPlate> plates;
	private Thread animationThread;
	static final Object animationLock = new Object();
	static final Object canvasPaintLock = new Object();
	public GameFrame (int size, boolean elder)
	{
		realoadResources();
		this.setTitle("Simple 2048");
		this.setIconImage(ResourceLoader.plateWorth2Icon);
		this.size = size;
		this.plates = new Vector<GraphicalPlate>(0);
		if (elder)
		{
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		else
		{
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}
		this.setLayout(new BorderLayout());
		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(null);
		this.add(this.mainPanel);
		class GamePanel extends JLayeredPane
		{
			private static final long serialVersionUID = 1L;
			
			public GamePanel()
			{
				super();
				this.setSize(PLATE_SIZE*size+1, PLATE_SIZE*size + 1);
			}
			
			@Override
			public void paint(Graphics g)
			{
				super.paint(g);
				if (AbstractAnimation.isAllComplete())
				{
					System.out.println("Painting canvas - " + plates.size() + " plates");
				}
				g.setColor(Color.GRAY);
				for (int i=0; i<=size; i++)
				{
					g.drawLine(0, PLATE_SIZE*i, (int)this.getSize().getWidth(), PLATE_SIZE*i); //TODO append
					g.drawLine(PLATE_SIZE*i, 0, PLATE_SIZE*i, (int)this.getSize().getHeight()); //TODO append
				}
				//TODO draw plate background instead
				AbstractAnimation.drawAll(g, System.currentTimeMillis());
				synchronized (plates)
				{
					for (GraphicalPlate plate : plates)
					{
						plate.draw(g);
					}
				}
				Toolkit.getDefaultToolkit().sync();
				synchronized(canvasPaintLock)
				{
					canvasPaintLock.notifyAll();
				}
			}
		}
		this.canvas = new GamePanel();
		class AnimationThread extends Thread
		{
			public AnimationThread()
			{
				this.setDaemon(true);
				this.setName("Animations-Thread");
			}
			@Override
			public void run()
			{
				System.out.println("Started " + this.getName());
				long currentTime;
				long sleep;
				long frameLength = 1000/FRAMES_PER_SECOND;
				while(true)
				{
					currentTime = System.currentTimeMillis();
					sleep = frameLength - (System.currentTimeMillis() - currentTime);
					if (AbstractAnimation.isAllComplete())
					{
						try 
						{
							synchronized (animationLock)
							{
								//System.out.println("Paused animation thread");
								animationLock.wait();
								//System.out.println("Resumed animation thread");
							}
						}
						catch (InterruptedException e) 
						{
							System.out.println("Unexpected exception:");
							e.printStackTrace();
						}
					}
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
					//System.out.println("Invoking repaint for canvas");
					canvas.repaint(frameLength);
				}
			}
		}
		this.animationThread = new AnimationThread();
		this.animationThread.start();
		this.mainPanel.add(this.canvas);
		this.setSize(this.size * PLATE_SIZE + 10, this.size * PLATE_SIZE + 40);
		this.setVisible(true);
	}
	public void addPlate(int worth, Point pos)
	{
		GraphicalPlate plate = new GraphicalPlate(worth);
		plate.setPos(pos);
		this.addPlate(plate);
	}
	public void addPlate(GraphicalPlate plate)
	{
		synchronized(plates)
		{
			this.plates.add(plate);
		}
	}
	public void removePlate(GraphicalPlate plate)
	{
		synchronized(plates)
		{
			System.out.println("Removing plate with hashcode " + plate.hashCode());
			this.plates.remove(plate);
		}
	}
	public boolean hasPlateForPos (Point pos)
	{
		for (GraphicalPlate plate : plates)
		{
			plate.revalidate();
			if (getPosForPoint(plate.getPos()).equals(pos))
			{
				//System.out.println("For pos " + pos + " found plate at coords " + plate.getPos());
				return true;
			}
		}
		//System.out.println("No plate for pos - " + pos.toString());
		return false;
	}
	public GraphicalPlate getPlateForPos (Point pos)
	{
		for (GraphicalPlate plate : plates)
		{
			if (GameFrame.getPosForPoint(plate.getPos()).equals(pos))
			{
				return(plate);
			}
		}
		return null;
	}
	/*public boolean platePresentAt(Point pos)
	{
		for (GraphicalPlate plate : this.plates)
		{
			if (pos.equals(getPosForPoint(plate.getPos())))
			{
				return (true);
			}
		}
		return(false);
	}*/
	public static int getPlateSize() 
	{
		return PLATE_SIZE;
	}
	public static void drawAnimations()
	{
		//System.out.println("Drawing animations!");
		synchronized (animationLock) 
		{
			animationLock.notifyAll();
		}
	}
	public static long getAnimationTimeMillis() 
	{
		return ANIMATION_TIME_MILLIS;
	}
	public static void setAnimationTimeMillis(long aNIMATION_TIME_MILLIS) 
	{
		ANIMATION_TIME_MILLIS = aNIMATION_TIME_MILLIS;
	}
	public static int getFrameLength()
	{
		return(1000/FRAMES_PER_SECOND);
	}
	public static void realoadResources()
	{
		ResourceLoader.load();
	}
	public boolean platePresent(GraphicalPlate plate)
	{
		for (GraphicalPlate g : plates)
		{
			if (g.equals(plate))
			{
				return true;
			}
		}
		return false;
	}
	public void revalidateAll()
	{
		synchronized(plates)
		{
			for (GraphicalPlate plate : plates)
			{
				plate.revalidate();
			}
		}
			
	}
}