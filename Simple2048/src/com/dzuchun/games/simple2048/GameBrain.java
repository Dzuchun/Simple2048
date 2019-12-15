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
import org.jnativehook.keyboard.NativeKeyAdapter;
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
	private static boolean gameLost;
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
				if (gameLost)
				{
					return;
				}
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
						System.out.println("Detected left arrow press");
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
					performPlateUpdates();
					gameFrame.revalidateAll();
					if ((moveHappened)&&isGameLost())
					{
						performGameLost();
						return;
					}
					if (moveHappened)
					{
						spawnPlate(spawnAmount);
					}
					System.out.println("STEP DIVIDER");
				}
			}

			@Override
			public void nativeKeyPressed(NativeKeyEvent arg0)	{}
			
		});
        /*for(Thread thread : Thread.getAllStackTraces().keySet())
        {
        	if (thread.getName().equals("JNativeHook Hook Thread"))
        	{
        		thread.setDaemon(true);
        	}
        }*///TODO make dispatcher daemon!!
	}
	private static void performGameLost() 
	{
		gameLost = true;
		spawnPlate(getFreePlace().size());
		System.out.println("Game lost");
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
		readPreferencies();
		gameFrame = new GameFrame(size, true);
		spawnPlate(spawnAmount);
		platesToUpdate = new Vector<GraphicalPlate>(0);
	}
	private static int size;
	private static int spawnAmount;
	private static void readPreferencies()
	{
		size = preferencies.getGridSize();
		spawnAmount = preferencies.getSpawnAmount();
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
		for (int i=0; i<size; i++)
		{
			for (int j=0; j<size; j++)
			{
				if (!gameFrame.hasPlateForPos(new Point(i, j)))
				{
					res.add(new Point(i, j));
					//System.out.println("Adding free pos: " + new Point(i, j));
				}
			}
		}
		//System.out.println("Free space left - " + res.size() + ":" + res.toString());
		return(res);
	}
	private static Random plateSpawnRandom;
	private static Vector<Point> freePlace;
	private static void spawnPlate(int howMuch)
	{
		System.out.println("Spawning plates");
		for (int i=0; i<howMuch; i++)
		{
			freePlace = getFreePlace();
			Point place = freePlace.get((int)(plateSpawnRandom.nextDouble()*freePlace.size()));
			gameFrame.addPlate(2, GameFrame.getPointForPos(place)); //TODO define default begin worth
			System.out.println("Placing plate on " + place.toString());
		}
		gameFrame.canvas.repaint();
	}
	private static boolean isBusy;
	private static final Object busyMonitor = new Object();
	private static boolean moveHappened; 
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
		moveHappened = false;
		int used;
		GraphicalPlate plate;
		GraphicalPlate prev = null;
		switch (move)
		{
		case MOVE_RIGHT:
			for (int i=0; i<size; i++)
			{
				used = size-1;
				prev = null;
				for (int j=size-1; j>=0; j--)
				{
					try 
					{
						if (gameFrame.hasPlateForPos(new Point(j, i)))
						{
							plate = gameFrame.getPlateForPos(new Point(j, i));
							if (prev != null)
							{
								if ((prev.getWorth() == plate.getWorth())&&(!prev.isUpdateScheduled()))
								{
									used++;
									if (used != j)
									{
										plate.addAnimation(GameFrame.getPointForPos(new Point(used, i)));
									}
									prev.addUpdate(plate);
									platesToUpdate.add(prev);
									prev = null;
								}
								else
								{
									//System.out.println(plate);
									if (used != j)
									{
										plate.addAnimation(GameFrame.getPointForPos(new Point(used, i)));
									}
									prev = plate;
								}
							}
							else
							{
								//System.out.println(plate);
								if (used != j)
								{
									plate.addAnimation(GameFrame.getPointForPos(new Point(used, i)));
								}
								prev = plate;
							}
							if (used != j)
							{
								moveHappened = true;
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
		case MOVE_LEFT:
			for (int i=0; i<size; i++)
			{
				used = 0;
				prev = null;
				for (int j=0; j<size; j++)
				{
					try 
					{
						if (gameFrame.hasPlateForPos(new Point(j, i)))
						{
							plate = gameFrame.getPlateForPos(new Point(j, i));
							if (prev != null)
							{
								if ((prev.getWorth() == plate.getWorth())&&(!prev.isUpdateScheduled()))
								{
									used--;
									if (used != j)
									{
										plate.addAnimation(GameFrame.getPointForPos(new Point(used, i)));
									}
									prev.addUpdate(plate);
									platesToUpdate.add(prev);
									prev = null;
								}
								else
								{
									//System.out.println(plate);
									if (used != j)
									{
										plate.addAnimation(GameFrame.getPointForPos(new Point(used, i)));
									}
									prev = plate;
								}
							}
							else
							{
								//System.out.println(plate);
								if (used != j)
								{
									plate.addAnimation(GameFrame.getPointForPos(new Point(used, i)));
								}
								prev = plate;
							}
							if (used != j)
							{
								moveHappened = true;
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
									if (used != j)
									{
										plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
									}
									prev.addUpdate(plate);
									platesToUpdate.add(prev);
									prev = null;
								}
								else
								{
									//System.out.println(plate);
									if (used != j)
									{
										plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
									}
									prev = plate;
								}
							}
							else
							{
								//System.out.println(plate);
								if (used != j)
								{
									plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
								}
								prev = plate;
							}
							if (used != j)
							{
								moveHappened = true;
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
									if (used != j)
									{
										plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
									}
									prev.addUpdate(plate);
									platesToUpdate.add(prev);
									prev = null;
								}
								else
								{
									//System.out.println(plate);
									if (used != j)
									{
										plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
									}
									prev = plate;
								}
							}
							else
							{
								//System.out.println(plate);
								if (used != j)
								{
									plate.addAnimation(GameFrame.getPointForPos(new Point(i, used)));
								}
								prev = plate;
							}
							if (used != j)
							{
								moveHappened = true;
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
		if (moveHappened)
		{
			try
			{
				Thread.sleep(GameFrame.getAnimationTimeMillis());
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		synchronized(busyMonitor)
		{
			isBusy = false;
		}
	}
	public static boolean isGameLost()
	{
		if (getFreePlace().size() < spawnAmount)
		{
			return true;
		}
		GraphicalPlate a;
		for (int i=0; i < size; i++)
		{
			for (int j = 0; j<size; j++)
			{
				if (gameFrame.hasPlateForPos(new Point(i, j)))
				{
					a = gameFrame.getPlateForPos(new Point(i, j));
					if (gameFrame.hasPlateForPos(new Point(i, j-1)))
					{
						if(gameFrame.getPlateForPos(new Point(i, j-1)).getWorth() == a.getWorth())
						{
							return false;
						}
					}
					if (gameFrame.hasPlateForPos(new Point(i, j+1)))
					{
						if(gameFrame.getPlateForPos(new Point(i, j+1)).getWorth() == a.getWorth())
						{
							return false;
						}
					}
				}
				else
				{
					return false;
				}
			}
		}
		for (int i=0; i < size; i++)
		{
			for (int j = 0; j<size; j++)
			{
				if (gameFrame.hasPlateForPos(new Point(j, i)))
				{
					a = gameFrame.getPlateForPos(new Point(j, i));
					if (gameFrame.hasPlateForPos(new Point(j-1, i)))
					{
						if(gameFrame.getPlateForPos(new Point(j-1, i)).getWorth() == a.getWorth())
						{
							return false;
						}
					}
					if (gameFrame.hasPlateForPos(new Point(j+1, i)))
					{
						if(gameFrame.getPlateForPos(new Point(j+1, i)).getWorth() == a.getWorth())
						{
							return false;
						}
					}
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}
}
class Preferencies implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final int VERSION = 0;
	private static final int STRING_VERSION = 1; 
	private static final int GRID_SIZE = 2; 
	private static final int SPAWN_AMOUNT = 3;
	
	private LinkedHashMap<Integer, Object> data;
	public Preferencies(int version, String stringVersion)
	{
		this.data = new LinkedHashMap<Integer, Object>(0);
		this.data.put(VERSION, version);
		this.data.put(STRING_VERSION, stringVersion);
		this.data.put(GRID_SIZE, 10); //TODO default value
		this.data.put(SPAWN_AMOUNT, 2); //TODO default value
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
	public int getSpawnAmount()
	{
		return (int)this.data.get(SPAWN_AMOUNT);
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
