package com.dzuchun.games.simple2048;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.prefs.Preferences;

public class GameBrain 
{
	public static final int version = 0;
	public static final String stringVersion = "0.1-alpha";
	private static final String preferenciesName = "preferencies.pref"; 
	
	private static Preferencies preferencies;
	private static GameFrame gameFrame;
	public static void main(String[] args) 
	{
		try 
		{
			preferencies = loadPreferencies();
		}
		catch (ClassNotFoundException | IOException e) 
		{
			e.printStackTrace();
		}
		gameFrame = new GameFrame(preferencies.getGridSize());
	}
	private static Preferencies loadPreferencies() throws ClassNotFoundException, FileNotFoundException, IOException
	{
		FileInputStream a = new FileInputStream((new File(preferenciesName)));
		ObjectInputStream b = new ObjectInputStream(a);
		//@SuppressWarnings("unchecked")
		//Preferencies res = new Preferencies((LinkedHashMap<Integer, Object>)b.readObject());
		b.readObject();
		a.close();
		b.close();
		return(null);
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
}
class Preferencies implements Serializable
{
	private static final int VERSION = 0;
	private static final int STRING_VERSION = 1; 
	private static final int GRID_SIZE = 2;
	
	private LinkedHashMap<Integer, Object> data;
	public Preferencies(int version, String stringVersion)
	{
		this.data.put(VERSION, version);
		this.data.put(STRING_VERSION, stringVersion);
		this.data.put(GRID_SIZE, 5); //TODO default value
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
