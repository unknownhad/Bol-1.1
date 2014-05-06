package com.bol.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.R;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OpenHelperClass extends SQLiteOpenHelper 
{
	
	public static final String DB_NAME="bol.sqlite";
	public static final String DB_PATH="/data/data/com.bol.app/databases/";
	
	
	private final Context myContext; 
	private SQLiteDatabase myDataBase;

	
	public OpenHelperClass(Context context) 
	{	 
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}	

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase()
	{
		SQLiteDatabase checkDB = null;
		try
		{
			String myPath = DB_PATH + DB_NAME;
			File file = new File(myPath);
			if(file.exists())
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		}
		catch(SQLiteException e)
		{
			//database doesn't exist yet.
			e.printStackTrace();
		}

		if(checkDB != null)
		{
			checkDB.close();
		}
		
		return checkDB != null ? true : false;
	}

	@Override
	public synchronized void close() 
	{
		if(myDataBase != null)
			myDataBase.close();

		super.close();

	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException
	{
		
		//Open your local db as the input stream
		InputStream is;
		//Open the empty db as the output stream
		OutputStream myOutput;
		try {
			AssetManager am = myContext.getAssets();
			is = am.open(DB_NAME);
			// Path to the just created empty db
			String outFileName = DB_PATH + DB_NAME;

			myOutput = new FileOutputStream(outFileName);
		

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer))>0)
		{
			//sytem.println("inside While");
			myOutput.write(buffer, 0, length);
		}
		//Close the streams
		myOutput.flush();
		myOutput.close();
		is.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	/*private void copyDataBase() throws IOException 
	{
		
		
        AssetManager am = myContext.getAssets();
        OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] b = new byte[1024];
        
        int r;
      
            InputStream is = am.open("demo.sqlite");
            try 
            {
				while ((r = is.read(b)) != -1) 
				{					
				    os.write(b, 0, r);
				}
			} 
            catch (Exception e) 
			{			
				e.printStackTrace();
			}
            try 
            {
				is.close();
			} 
            catch (Exception e) 
			{
				e.printStackTrace();
			}
        
        try 
        {
			os.close();
		} 
        catch (Exception e) 
		{		
			e.printStackTrace();
		}
    }*/
	

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException
	{
		boolean dbExist = checkDataBase();

		if(dbExist)
		{
			
			//do nothing - database already exist
		}
		else
		{
			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();
			this.close();
			try 
			{
				copyDataBase();
			} 
			catch (IOException e) 
			{
				throw new Error("Error copying database");
			}
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{

		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{

	}

	public void openDataBase() throws SQLException
	{
		//Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		
	}
}

