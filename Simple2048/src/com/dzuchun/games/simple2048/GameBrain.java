package com.dzuchun.games.simple2048;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

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
	private static Vector<GraphicalPlate> platesToUpdate;
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
		try 
        {
                GlobalScreen.registerNativeHook();
                Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.WARNING);
        }
        catch (NativeHookException ex) 
        {
                System.err.println("There was a problem registering the native hook.");
                System.err.println(ex.getMessage());
        }
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() 
        {	
			@Override
			public void nativeKeyTyped(NativeKeyEvent arg0)	{}
			
			@Override
			public void nativeKeyReleased(NativeKeyEvent arg0) 
			{
				if (gameFrame.isFocused())
				{
					switch (arg0.getKeyCode())
					{
					case NativeKeyEvent.VC_UP:
						System.out.println("Detected up arrow press");
						moveAll(MOVE_UP);
						break;
					case NativeKeyEvent.VC_DOWN:
						System.out.println("Detected down arrow press");
						moveAll(MOVE_DOWN);
						break;
					case NativeKeyEvent.VC_LEFT:
						System.out.println("Detected LEFT arrow press");
						moveAll(MOVE_LEFT);
						break;
					case NativeKeyEvent.VC_RIGHT:
						System.out.println("Detected right arrow press");
						moveAll(MOVE_RIGHT);
						break;
					default:
						System.out.println("Idk a \"" + arg0.getKeyCode() + "\" key, please use " + NativeKeyEvent.VC_LEFT + " " + NativeKeyEvent.VC_UP + " " + NativeKeyEvent.VC_RIGHT + " " + NativeKeyEvent.VC_DOWN + " ");
						return;
					}
				}
				performPlateUpdates();
				spawnPlate(10);
				System.out.println("STEP DIVIDER");
			}
			
			@Override
			public void nativeKeyPressed(NativeKeyEvent arg0) {}
		});
	}
	private static void performPlateUpdates()
	{
		for (GraphicalPlate plate : platesToUpdate)
		{
			plate.worthUpdate(gameFrame);
		}
		platesToUpdate.clear();
	}
	private static void preInitGame () throws ClassNotFoundException, FileNotFoundException, IOException
	{
		preferencies = loadPreferencies();
		plateSpawnRandom = new Random();
	}
	private static void initGame()
	{
		size = preferencies.getGridSize();
		gameFrame = new GameFrame(size);
		spawnPlate(2);
		platesToUpdate = new Vector<GraphicalPlate>(0);
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
	private static int size;
	private static Vector<Point> getFreePlace()
	{
		Vector<Point> res = new Vector<Point>(0);
		for (int i=0; i<size; i++)
		{
			for (int j=0; j<size; j++)
			{
				if (!gameFrame.hasPlateForPos(new Point(i, j)))
				{
					res.add(new Point(i, j));
				}
			}
		}
		System.out.println("Free space left - " + res.size());
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
		}
	}
	private static boolean isBusy;
	private static final Object busyMonitor = new Object();
	public static void moveAll(int move)
	{
		if (isBusy)
		{
			System.out.println("Moving plates, i'm busy!");
			return;
		}
		synchronized(busyMonitor)
		{
			isBusy = true;
		}
		//TODO dodefine!
		int used;
		GraphicalPlate plate;
		GraphicalPlate prev = null;
		switch (move)
		{
		case MOVE_LEFT:
			break;
		case MOVE_RIGHT:
			break;
		case MOVE_UP:
			for (int i=0; i<size; i++)
			{
				used = 0;
				prev = null;
				for (int j=0; j<size; j++)
				{
					try 
					{
						if (gameFrame.hasPlateForPos(new Point(i, j)))
						{
							plate = gameFrame.getPlateForPos(new Point(i, j));
							if (prev != null)
							{
								if ((prev.getWorth() == plate.getWorth())&&(!prev.isUpdateScheduled()))
								{
									used--;
									plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
									prev.addUpdate(plate);
									platesToUpdate.add(prev);
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
			for (int i=0; i<size; i++)
			{
				used = size-1;
				prev = null;
				for (int j=size-1; j>=0; j--)
				{
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
									platesToUpdate.add(prev);
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
		synchronized(busyMonitor)
		{
			isBusy = false;
		}
	}
	public static boolean isGameLost()
	{
		return(getFreePlace().isEmpty());
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
