package com.dzuchun.games.simple2048;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Vector;

public class GameBrain 
{
	public static final int version = 0;
	public static final String stringVersion = "0.1-alpha";
	private static final String preferenciesName = "preferencies.pref"; 
	private static final int MOVE_RIGHT = 1; 
	private static final int MOVE_LEFT = 2; 
	private static final int MOVE_UP = 3; 
	private static final int MOVE_DOWN = 4;
	
	private static Preferencies preferencies;
	private static GameFrame gameFrame;
	private static int[][] worthes;
	public static void main(String[] args) 
	{
		//TODO DELETE these lines!!
		try 
		{
			savePreferencies(new Preferencies(version, stringVersion));
		}
		catch (IOException e) 		
		{
			e.printStackTrace();
		}
		//TODO end.
		try 
		{
			preInitGame();
		}
		catch (ClassNotFoundException | IOException e) 
		{
			e.printStackTrace();
		}
		initGame();
		gameFrame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e)  //TODO FIX FOR LINUX!!!
			{
				switch (e.getKeyCode())
				{
				case KeyEvent.VK_A:
					System.out.println("Detected left press");
					moveAll(MOVE_LEFT);
					break;
				case KeyEvent.VK_D:
					System.out.println("Detected right press");
					moveAll(MOVE_RIGHT);
					break;
				case KeyEvent.VK_W:
					System.out.println("Detected up press");
					moveAll(MOVE_UP);
					break;
				case KeyEvent.VK_S:
					System.out.println("Detected down press");
					moveAll(MOVE_DOWN);
					break;
					default:
						System.out.println("Idk this key:" + e.getKeyCode());
						//System.out.println("Please use " + KeyEvent.VK_A + " " + KeyEvent.VK_S + " " + KeyEvent.VK_W + " " + KeyEvent.VK_D + " ");
						return;
				}
				gameFrame.performUpdates();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
	}
	private static void preInitGame () throws ClassNotFoundException, FileNotFoundException, IOException
	{
		preferencies = loadPreferencies();
		plateSpawnRandom = new Random();
	}
	private static void initGame()
	{
		gameFrame = new GameFrame(preferencies.getGridSize());
		worthes = new int[preferencies.getGridSize()][preferencies.getGridSize()];
		for (int[] b : worthes) 
		{
			for (int j = 0; j < b.length; j++) 
			{
				@SuppressWarnings("unused")
				int i = b[j];
				i=0;
			}
		}
	}
	private static Preferencies loadPreferencies() throws ClassNotFoundException, FileNotFoundException, IOException
	{
		FileInputStream a = new FileInputStream((new File(preferenciesName)));
		ObjectInputStream b = new ObjectInputStream(a);
		@SuppressWarnings("unchecked")
		Preferencies res = new Preferencies((LinkedHashMap<Integer, Object>)b.readObject());
		a.close();
		b.close();
		return(res);
	}
	private static void savePreferencies(Preferencies pref) throws IOException
	{
		File f = new File(preferenciesName);
		if (!f.exists())
		{
			f.createNewFile();
		}
		pref.save(f);
	}
	private static Vector<Point> getFreePlace()
	{
		Vector<Point> res = new Vector<Point>(0);
		int[] a;
		for (int i=0; i<worthes.length; i++)
		{
			a = worthes[i];
			for (int j=0; j<a.length; j++)
			{
				if (worthes[i][j] == 0)
				{
					res.add(new Point(i, j));
				}
			}
		}
		return(res);
	}
	private static Random plateSpawnRandom;
	private static Vector<Point> freePlace;
	private static void spawnPlate(int howMuch)
	{
		for (int i=0; i<howMuch; i++)
		{
			freePlace = getFreePlace();
			Point place = freePlace.get((int)(plateSpawnRandom.nextDouble()*freePlace.size()));
			gameFrame.addPlate(2, GameFrame.getPointForPos(place)); //TODO define default begin worth
			System.out.println("Placing plate on " + place.toString());
			worthes[place.x][place.y] = 2;
		}
	}
	public static void moveAll(int move)
	{
		//TODO dodefine!
		int used;
		int[] array;
		int current;
		@SuppressWarnings("unused")
		int [][] worthes1 = transponate(worthes);
		GraphicalPlate plate;
		GraphicalPlate prev = null;
		switch (move)
		{
		case MOVE_LEFT:
			break;
		case MOVE_RIGHT:
			break;
		case MOVE_UP:
			for (int i=0; i<worthes.length; i++)
			{
				array = worthes[i];
				used = 0;
				prev = null;
				for (int j=0; j<array.length; j++)
				{
					current = array[j];
					try 
					{
						if (current != 0)
						{
							plate = gameFrame.getPlateForPos(new Point(i, j));
							if (prev != null)
							{
								if ((prev.getWorth() == plate.getWorth())&&(!prev.isUpdateScheduled()))
								{
									used--;
									plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
									prev.addUpdate(plate);
									prev = null;
								}
								else
								{
									//System.out.println(plate);
									plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
									prev = plate;
								}
							}
							else
							{
								//System.out.println(plate);
								plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
								prev = plate;
							}
							used++;
						}
					}
					catch (IntersectsAnimationException e) 
					{
						e.printStackTrace();
					}
					catch (java.lang.NullPointerException e)
					{
						e.printStackTrace();
					}
				}
			}
			break;
		case MOVE_DOWN:
			for (int i=0; i<worthes.length; i++)
			{
				array = worthes[i];
				used = array.length-1;
				prev = null;
				for (int j=array.length-1; j>=0; j--)
				{
					current = array[j];
					try 
					{
						if (gameFrame.hasPlateForPos(new Point(i, j)))
						{
							plate = gameFrame.getPlateForPos(new Point(i, j));
							if (prev != null)
							{
								if ((prev.getWorth() == plate.getWorth())&&(!prev.isUpdateScheduled()))
								{
									used++;
									plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
									prev.addUpdate(plate);
									prev = null;
								}
								else
								{
									//System.out.println(plate);
									plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
									prev = plate;
								}
							}
							else
							{
								//System.out.println(plate);
								plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
								prev = plate;
							}
							used--;
						}
						else
						{
							//System.out.println("Got error on " + i + " " + j);
						}
					}
					catch (IntersectsAnimationException e) 
					{
						e.printStackTrace();
					}
					catch (java.lang.NullPointerException e)
					{
						e.printStackTrace();
					}
				}
			}
			break;
			default:
				throw(new IllegalArgumentException());
		}
		GameFrame.drawAnimations();
		try
		{
			Thread.sleep(GameFrame.getAnimationTimeMillis());
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		gameFrame.performUpdates();
		updateWorthes();
	}
	public static boolean isGameLost()
	{
		return(getFreePlace().isEmpty());
	}
	private static void updateWorthes()
	{
		for (int i=0; i<worthes.length; i++)
		{
			for (int j=0; j<worthes[i].length; j++)
			{
				if (gameFrame.hasPlateForPos(new Point(i, j)))
				{
					worthes[i][j] = gameFrame.getPlateForPos(new Point(i, j)).getWorth();
				}
				else
				{
					worthes[i][j] = 0;
				}
			}
		}
	}
	private static int[][] transponate (int[][] arg) //TODO doformate
	{
		int l = arg.length;
		int[][] res = new int[l][l];
		for (int i=0; i<l; i++)
		{
			for (int j=0; j<l; j++)
			{
				res[i][j] = arg[j][i];
			}
		}
		return res;
	}
}
class Preferencies implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final int VERSION = 0;
	private static final int STRING_VERSION = 1; 
	private static final int GRID_SIZE = 2;
	
	private LinkedHashMap<Integer, Object> data;
	public Preferencies(int version, String stringVersion)
	{
		this.data = new LinkedHashMap<Integer, Object>(0);
		this.data.put(VERSION, version);
		this.data.put(STRING_VERSION, stringVersion);
		this.data.put(GRID_SIZE, 12); //TODO default value
	}
	public Preferencies (LinkedHashMap<Integer, Object> in)
	{
		this.data = in;
	}
	public int getGridSize() 
	{
		return (int)this.data.get(GRID_SIZE);
	}
	public void setGridSize(int gridSize) 
	{
		this.data.replace(GRID_SIZE, gridSize);
	}
	public void save (File f) throws IOException
	{
		FileOutputStream a = new FileOutputStream(f);
		ObjectOutputStream b = new ObjectOutputStream(a);
		b.writeObject(this.data);
		a.close();
		b.close();
	}
}
