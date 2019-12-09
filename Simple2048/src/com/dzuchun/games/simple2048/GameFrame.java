package com.dzuchun.games.simple2048;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GameFrame extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private static int PLATE_SIZE = 40;
	private static int ANIMATION_TIME_MILLIS = 1000;
	private static int FRAMES_PER_SECOND = 60;
	
	JLayeredPane canvas;
	private int size;
	JPanel mainPanel;
	private Vector<GraphicalPlate> plates;
	private AnimationThread animationThread;
	boolean animationDrawn;
	public GameFrame (int size)
	{
		this.size = size;
		this.plates = new Vector<GraphicalPlate>(0);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		
		this.animationThread = new AnimationThread(this);
		this.animationDrawn = false;
		class GamePanel extends JLayeredPane
		{
			private static final long serialVersionUID = 1L;
			
			public GamePanel()
			{
				super();
				this.setSize((getPlateSize()+1)*size + 1, (getPlateSize()+1)*size + 1); //TODO append
			}
			
			@Override
			public void paint(Graphics g)
			{
				super.paint(g);
				g.setColor(Color.GRAY);
				for (int i=0; i<=size; i++)
				{
					g.drawLine(0, (getPlateSize()+1)*i, (int)this.getSize().getWidth(), (getPlateSize()+1)*i); //TODO append
					g.drawLine((getPlateSize()+1)*i, 0, (getPlateSize()+1)*i, (int)this.getSize().getHeight()); //TODO append
				}
				for (GraphicalPlate plate : plates)
				{
					plate.repaint();
				}
				Toolkit.getDefaultToolkit().sync();
			}
		}
		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(null);
		this.add(this.mainPanel);
		this.canvas = new GamePanel();
		this.mainPanel.add(this.canvas);
		this.setSize(300, 300);
		this.setVisible(true);
	}
	public void addPlate(int worth, Point pos)
	{
		//TODO check if plate exists
		GraphicalPlate plate = new GraphicalPlate(worth);
		plate.setPos(pos);
		this.plates.add(plate);
		this.canvas.add(plate);
		this.canvas.setLayer(plate, 2);
		this.repaint();
	}
	public void addPlate(GraphicalPlate plate)
	{
		//TODO check if plate exists
		this.plates.add(plate);
		this.canvas.add(plate);
		this.canvas.setLayer(plate, 2);
		this.repaint();
	}
	public void performAnimation (int delay)
	{
		if (animationDrawn)
		{
			return;
		}
		this.animationThread.performAnimation(delay, GraphicalPlate.getScheduledPlates());
	}
	public static int getPlateSize() 
	{
		return PLATE_SIZE;
	}
	public static void setPlateSize(int pLATE_SIZE) 
	{
		PLATE_SIZE = pLATE_SIZE;
	}
	public static int getFRAMES_PER_SECOND() 
	{
		return FRAMES_PER_SECOND;
	}
	public static void setFRAMES_PER_SECOND(int fRAMES_PER_SECOND) 
	{
		FRAMES_PER_SECOND = fRAMES_PER_SECOND;
	}
	public static int getANIMATION_TIME_MILLIS() {
		return ANIMATION_TIME_MILLIS;
	}
	public static void setANIMATION_TIME_MILLIS(int aNIMATION_TIME_MILLIS) 
	{
		ANIMATION_TIME_MILLIS = aNIMATION_TIME_MILLIS;
	}
	public void paint(Graphics g)
	{
		//TODO DEFINE
	}
}
class AnimationThread extends Timer
{
	class DrawFrame extends TimerTask
	{
		@Override
		public void run() 
		{
			long currentTime = System.currentTimeMillis();
			//TODO DEFINE
		}
	}
	private GameFrame frame;
	public AnimationThread(GameFrame frame)
	{
		super(true);
		this.frame = frame;
	}
	private long millisStart;
	private long millisEnd;
	private long frameLength;
	private Vector<GraphicalPlate> scheduledPlates;
	@SuppressWarnings("unchecked")
	public void performAnimation(int delay, Vector<GraphicalPlate> scheduledPlates)
	{
		this.scheduledPlates = (Vector<GraphicalPlate>)scheduledPlates.clone();
		this.millisStart = System.currentTimeMillis() + delay;
		this.millisEnd = this.millisStart + GameFrame.getANIMATION_TIME_MILLIS() + delay;
		this.frameLength = 1000/GameFrame.getFRAMES_PER_SECOND();
		this.scheduleAtFixedRate(new DrawFrame(), delay, this.frameLength);
		frame.animationDrawn = true;
	}
}
