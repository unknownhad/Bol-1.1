package com.bol.database;

import java.io.IOException;
import java.util.ArrayList;

import com.application.BolApp;
import com.model.classes.DataObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataHelper {

	private static final String PATH = "img_path";
	private static final String IMAGE_NAME = "img_name";
	private static final String CATEGORYID = "category_id";
	private static final String CATNAME = "cat_name";
	private static final String PARENTID = "parent_id";
	private static final String IS_FAV = "is_fav";

	
	private static final String LEVEL = "level";
	public static final int CLOSE = -10000;
	
	private Context context;
	static private SQLiteDatabase database;
	private OpenHelperClass dbHelper;

	public DataHelper(Context context)

	{
		this.context = context;

	}

	public void close() {

		dbHelper.close();
	}

	public Cursor Customquery(String query) {
		System.out.println("query is "+query);
		Cursor c = null;
		if(database!=null)
		{
		 c = database.rawQuery(query, null);
		}
		else
		{
		
			
		}
		return c;
	}

	public DataHelper open() throws SQLException, IOException {

		dbHelper = new OpenHelperClass(context);
		dbHelper.createDataBase();
		database = dbHelper.getWritableDatabase();
		return this;
	}

	
	
	
	public  ArrayList<DataObject> GetCategories(int parentId)
	{
		
		Cursor c = BolApp.dataHelper.Customquery("select * from category_master where parent_id="+parentId);
		
		ArrayList<DataObject> datalist = null;
		if(c.getCount()>0)
		{
			datalist = new ArrayList<DataObject>();
			c.moveToFirst();
			do
			{
				DataObject dataobject = new DataObject();
				dataobject.setCategoryId(""+c.getInt(c.getColumnIndex(CATEGORYID)));
				dataobject.setCategoryName(c.getString(c.getColumnIndex(CATNAME)));
				dataobject.setImg_name(c.getString(c.getColumnIndex(IMAGE_NAME)));
				dataobject.setImg_path(c.getString(c.getColumnIndex(PATH)));
				dataobject.setParentId(""+c.getInt(c.getColumnIndex(PARENTID)));
				dataobject.setLevel(c.getInt(c.getColumnIndex(LEVEL)));
				dataobject.setAudioPath(c.getString(c.getColumnIndex("audio_path")));
				datalist.add(dataobject);
				System.out.println("dataobject get image path "+dataobject.getImg_path());
				
			}while(c.moveToNext());
		}
		
		return datalist;
		
	}
	
	
	
	public  ArrayList<DataObject> GetParentCategories(int categoryId)
	{
		
		Cursor c = BolApp.dataHelper.Customquery("select * from category_master where category_id="+categoryId);
		
		ArrayList<DataObject> datalist = null;
		if(c.getCount()>0)
		{
			datalist = new ArrayList<DataObject>();
			c.moveToFirst();
			do
			{
				DataObject dataobject = new DataObject();
				dataobject.setCategoryId(""+c.getInt(c.getColumnIndex(CATEGORYID)));
				dataobject.setCategoryName(c.getString(c.getColumnIndex(CATNAME)));
				dataobject.setImg_name(c.getString(c.getColumnIndex(IMAGE_NAME)));
				dataobject.setImg_path(c.getString(c.getColumnIndex(PATH)));
				dataobject.setParentId(""+c.getInt(c.getColumnIndex(PARENTID)));
				dataobject.setLevel(c.getInt(c.getColumnIndex(LEVEL)));
				dataobject.setAudioPath(c.getString(c.getColumnIndex("audio_path")));
				datalist.add(dataobject);
				System.out.println("dataobject get audio path2 "+dataobject.getAudioPath());
				
			}while(c.moveToNext());
		}
		
		return datalist;
		
	}
	
	
	
	public long AddCategory(DataObject object)
	{
		ContentValues values = new ContentValues();
		
		values.put(CATNAME, object.getCategoryName());
		values.put(PARENTID, object.getParentId());
		values.put(IMAGE_NAME, object.getImg_name());
		values.put(PATH, object.getImg_path());
		values.put(LEVEL, object.getLevel());
		values.put("audio_path", object.getAudioPath());
		
		return database.insert("category_master", null, values);
	}
	
	
	public boolean isAlreadyAdded(String parentId,String catName)
	{
		String query = "select cat_name from category_master where UPPER(parent_id) = UPPER('"+parentId+"') AND UPPER(cat_name) = UPPER('"+catName+"')";
		Cursor c = Customquery(query);
		if(c!=null && c.getCount()>0)
		{
			return true;
		}
		return false;
	}
	
	public long DeleteCategory(int id)
	{
		
		return database.delete("category_master", "category_id="+id, null);
		
	}
	
}
