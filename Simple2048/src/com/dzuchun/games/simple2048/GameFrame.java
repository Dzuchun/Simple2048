package com.dzuchun.games.simple2048;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
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
	private static int PLATE_SIZE = 40;
	private static int ANIMATION_TIME_MILLIS = 1000;
	private static int FRAMES_PER_SECOND = 60;
	
	JPanel canvas;
	private int size;
	JLayeredPane mainPanel;
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
		class GamePanel extends JPanel
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
			}
		}
		this.mainPanel = new JLayeredPane();
		this.mainPanel.setLayout(null);
		this.add(this.mainPanel);
		this.canvas = new GamePanel();
		this.mainPanel.add(this.canvas);
		this.mainPanel.setLayer(this.canvas, 1);
		this.setSize(300, 300);
		this.setVisible(true);
	}
	public void addPlate(int worth, Point pos)
	{
		//TODO check if plate exists
		GraphicalPlate plate = new GraphicalPlate(worth);
		plate.setPos(pos);
		this.plates.add(plate);
		this.mainPanel.add(plate);
		this.mainPanel.setLayer(plate, 2);
		this.repaint();
	}
	public void addPlate(GraphicalPlate plate)
	{
		//TODO check if plate exists
		this.plates.add(plate);
		this.mainPanel.add(plate);
		this.mainPanel.setLayer(plate, 2);
		this.repaint();
	}
	public void performAnimation (int delay)
	{
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
	public static int getFRAMES_PER_SECOND() {
		return FRAMES_PER_SECOND;
	}
	public static void setFRAMES_PER_SECOND(int fRAMES_PER_SECOND) {
		FRAMES_PER_SECOND = fRAMES_PER_SECOND;
	}
	public static int getANIMATION_TIME_MILLIS() {
		return ANIMATION_TIME_MILLIS;
	}
	public static void setANIMATION_TIME_MILLIS(int aNIMATION_TIME_MILLIS) {
		ANIMATION_TIME_MILLIS = aNIMATION_TIME_MILLIS;
	}
	public void paint(Graphics g)
	{
		super.paint(g);
		this.canvas.repaint();
		this.mainPanel.repaint();
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
			if (currentTime >= millisEnd)
			{
				cancel();
				return;
			}
			double part = ((double)currentTime - (double)millisStart)/((double)millisEnd - (double)millisStart);
			for(GraphicalPlate plate : scheduledPlates)
			{
				plate.moveToSheduled(part);
			}
			frame.canvas.repaint();
			frame.mainPanel.repaint();
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
	}
	@Override
	public void scheduleAtFixedRate(TimerTask task, long delay, long period)
	{
		frame.animationDrawn = true;
		super.scheduleAtFixedRate(task, delay, period);
	}
	@Override
	public void cancel()
	{
		frame.animationDrawn = false;
		super.cancel();
	}
}
class GraphicalPlate extends JPanel
{
	private static final long serialVersionUID = 1L;
	private static Color colorForWorth (int worth)
	{
		switch(worth)
		{
		case 2:
			return(Color.YELLOW);
		default:
			return(null);
		}
	}
	private static Vector<GraphicalPlate> scheduledPlates = new Vector<GraphicalPlate>(0);
	public static Vector<GraphicalPlate> getScheduledPlates() 
	{
		return scheduledPlates;
	}
	
	private int worth;
	private Point pos;
	private JLabel label;
	public GraphicalPlate (int worth)
	{
		this.setSize(GameFrame.getPlateSize(), GameFrame.getPlateSize());
		this.worth = worth;
		this.label = new JLabel(new Integer(worth).toString());
		this.label.setAlignmentX(CENTER_ALIGNMENT);
		this.setBackground(colorForWorth(this.worth));
		this.label.setBackground(colorForWorth(this.worth));
		this.add(this.label);
	}
	public void setPos (Point newPos)
	{
		this.pos = new Point(newPos);
		this.setBounds(this.pos.x, this.pos.y, GameFrame.getPlateSize(), GameFrame.getPlateSize());
	}
	public Point getPos()
	{
		return (this.pos);
	}
	private Point beginPos;
	private Point scheduledPos;
	public void scheduleMove (Point newPos)
	{
		this.scheduledPos = newPos;
		this.beginPos = this.pos;
		scheduledPlates.add(this);
	}
	public void moveToSheduled (double part)
	{
		if (part > 1.0)
		{
			part = 1.0;
		}
		if (part< 0.0)
		{
			return;
		}
		this.setPos(new Point((int)(part*(this.scheduledPos.x - this.beginPos.x) + this.beginPos.x), (int)(part*(this.scheduledPos.y - this.beginPos.y) + this.beginPos.y)));
		System.out.println("changed pos to " + this.pos.toString());
	}
	
	@Override
	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		this.setBounds(this.pos.x, this.pos.y, GameFrame.getPlateSize(), GameFrame.getPlateSize());
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		//TODO define
	}
}