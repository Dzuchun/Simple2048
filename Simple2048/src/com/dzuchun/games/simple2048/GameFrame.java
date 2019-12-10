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
	private static int PLATE_SIZE = 40;
	private static long ANIMATION_TIME_MILLIS = 700;
	private static int FRAMES_PER_SECOND = 60;
	
	JLayeredPane canvas;
	private int size;
	JPanel mainPanel;
	private Vector<GraphicalPlate> plates;
	private Thread animationThread;
	static final Object animationLock = new Object();
	public GameFrame (int size)
	{
		realoadResources();
		this.setTitle("Simple 2048");
		this.setIconImage(ResourceLoader.plateWorth2);
		this.size = size;
		this.plates = new Vector<GraphicalPlate>(0);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
				this.setSize((PLATE_SIZE+1)*size + 1, (PLATE_SIZE+1)*size + 1); //TODO append
			}
			
			@Override
			public void paint(Graphics g)
			{
				super.paint(g);
				//System.out.println("Painting canvas");
				g.setColor(Color.GRAY);
				for (int i=0; i<=size; i++)
				{
					g.drawLine(0, (PLATE_SIZE+1)*i, (int)this.getSize().getWidth(), (PLATE_SIZE+1)*i); //TODO append
					g.drawLine((PLATE_SIZE+1)*i, 0, (PLATE_SIZE+1)*i, (int)this.getSize().getHeight()); //TODO append
				}
				AbstractAnimation.drawAll(g, System.currentTimeMillis());
				for (GraphicalPlate plate : plates)
				{
					//System.out.println("Drawing plate " + plate.toString());
					plate.draw(g);
				}
				Toolkit.getDefaultToolkit().sync();
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
					//System.out.println("Invoking repaint fo canvas");
					canvas.repaint(frameLength);
				}
			}
		}
		this.animationThread = new AnimationThread();
		this.animationThread.start();
		this.mainPanel.add(this.canvas);
		this.setSize(300, 300);
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
		//TODO check if plate exists
		this.plates.add(plate);
		this.canvas.add(plate);
		this.canvas.setLayer(plate, 2);
		this.repaint();
		this.canvas.repaint();
		plate.repaint(0);
	}
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
	public static void realoadResources()
	{
		ResourceLoader.load();
	}
}