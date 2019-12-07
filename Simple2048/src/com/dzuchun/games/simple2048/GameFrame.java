package com.dzuchun.games.simple2048;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GameFrame extends JFrame 
{
	private static int PLATE_SIZE = 40;
	
	private Canvas canvas;
	private int size;
	private JLayeredPane mainPanel;
	private Vector<GraphicalPlate> plates;
	public GameFrame (int size)
	{
		this.size = size;
		this.plates = new Vector<GraphicalPlate>(0);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		class GameCanvas extends Canvas
		{
			private static final long serialVersionUID = 1L;
			
			public GameCanvas()
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
		this.canvas = new GameCanvas();
		this.mainPanel.setLayer(this.canvas, 1);
		this.mainPanel.add(this.canvas);
		this.setSize(300, 300);
		this.canvas.repaint();
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
		this.mainPanel.repaint();
		this.canvas.repaint();
	}
	public static int getPlateSize() 
	{
		return PLATE_SIZE;
	}
	public static void setPlateSize(int pLATE_SIZE) 
	{
		PLATE_SIZE = pLATE_SIZE;
	}
}
class GraphicalPlate extends JPanel
{
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
	
	@Override
	public void paint (Graphics g)
	{
		super.paint(g);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		//TODO define
	}
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
}