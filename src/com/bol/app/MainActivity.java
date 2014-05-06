package com.bol.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.adapters.GridViewAdapter;
import com.application.BolApp;
import com.bol.database.DataHelper;
import com.model.classes.DataObject;

public class MainActivity extends Activity implements OnItemClickListener,
		OnInitListener {

	GridView gridView, topGridView;
	MediaPlayer mp;
	private static final int REQUESTCODE = 2;
	public static int parentId = 1;
	ArrayList<DataObject> list;
	ArrayList<DataObject> topList = new ArrayList<DataObject>();
	Handler handler;
	private TextToSpeech tts;
	ImageView GoImage;

	DataObject dataobject, AddCategoryObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AddCategoryObject = new DataObject();
		AddCategoryObject = new DataObject();
		AddCategoryObject.setCategoryId(0 + "");
		AddCategoryObject.setCategoryName("");
		AddCategoryObject.setParentId("add" + "");
		AddCategoryObject.setLevel(-1);
		AddCategoryObject.setImg_name("add_category_img");

		gridView = (GridView) findViewById(R.id.grid_view);
		topGridView = (GridView) findViewById(R.id.top_grid_view);

		dataobject = new DataObject();
		dataobject.setCategoryId(0 + "");
		dataobject.setCategoryName("");
		dataobject.setParentId(0 + "");
		dataobject.setLevel(0);
		dataobject.setImg_name("go_image");
		try {
			BolApp.dataHelper = new DataHelper(this);
			BolApp.dataHelper.open();
		} catch (SQLException e1) {

			e1.printStackTrace();
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		topList.add(dataobject);

		GoImage = (ImageView) findViewById(R.id.go_image);

		gridView.setOnItemClickListener(this);
		topGridView.setOnItemClickListener(this);

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				
				super.handleMessage(msg);

				if (msg.what == 1) {
					/*
					 * if (!BolApp.sp.getBoolean("lock_locked", false))
					 * topList.add(AddCategoryObject);
					 */
					GridViewAdapter topadapter = new GridViewAdapter(
							MainActivity.this, topList,true);
					topGridView.setAdapter(topadapter);

					if (list != null) {

						GridViewAdapter adapter = new GridViewAdapter(
								MainActivity.this, list,false);
						gridView.setAdapter(adapter);
					}
					/*
					 * else { if (!BolApp.sp.getBoolean("lock_locked", false)) {
					 * list = new ArrayList<DataObject>();
					 * list.add(AddCategoryObject); GridViewAdapter adapter =
					 * new GridViewAdapter(MainActivity.this, list);
					 * gridView.setAdapter(adapter); } }
					 */
				}
			}
		};

		LoadCategories(1);

		tts = new TextToSpeech(this, this);

		/*
		 * ImageView iv = new ImageView(this);
		 * 
		 * int drawableID = getResources().getIdentifier("drawableName",
		 * "drawable", getPackageName()); iv.setImageResource(drawableID);
		 */

	}

	@Override
	public void onItemClick(final AdapterView<?> view, final View view2,
			final int position, long arg3) {

		// ShowDialog();

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {

					if (view == topGridView) {
						System.out.println("inside topgrid "
								+ view.getClass().getSimpleName());
						ArrayList<DataObject> templist = BolApp.dataHelper
								.GetParentCategories(Integer.parseInt(topList
										.get(position).getParentId()));

						topList.clear();
						topList.add(templist.get(position));

						list = BolApp.dataHelper.GetCategories(Integer
								.parseInt(templist.get(position)
										.getCategoryId()));
						if (!BolApp.sp.getBoolean("lock_locked", false)) {
							
							list.add(AddCategoryObject);
						}

						handler.sendEmptyMessage(1);
					} else {

						if (list != null
								&& list.get(position).getParentId()
										.equalsIgnoreCase("add")) {
							
							
							
							startActivityForResult(new Intent(
									MainActivity.this,
									AddCategoryActivity.class).putExtra(
									"parent_id", topList.get(0).getCategoryId()),
									REQUESTCODE);

						} else {

							DataObject tempobject = null;
							System.out.println("inside " + topList);
							if (topList.size() > 0)
								tempobject = topList.get(0);
							topList.clear();
							if (list != null && position < list.size()) {
								if (BolApp.sp.getBoolean("sound_locked", false))
								{
									if(list.get(position).getAudioPath()!=null )
									{
									System.out.println("audio path "+list.get(position).getAudioPath());
										 mp =MediaPlayer.create(MainActivity.this, Uri.parse(list.get(position).getAudioPath()));
										mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
										
										mp.start();
										
									}
									else
									{
										tts.speak(list.get(position)
												.getCategoryName(),
												TextToSpeech.QUEUE_FLUSH, null);
									}
									
								}
								topList.add(list.get(position));

								ArrayList<DataObject> newlist = BolApp.dataHelper.GetCategories(Integer
										.parseInt(list.get(position)
												.getCategoryId()));
								if (!BolApp.sp.getBoolean("lock_locked", false)) {
									if (newlist == null) {
										newlist = new ArrayList<DataObject>();
										newlist.add(AddCategoryObject);
									} else {
										newlist.add(AddCategoryObject);
									}
								}

								if (newlist != null && newlist.size() > 0) {
									list = newlist;
								} else {
									topList.clear();
									topList.add(tempobject);
								}

								// list =
								// BolApp.dataHelper.GetCategories(Integer.parseInt(list.get(position).getCategoryId()));

							}

							try {

								System.out.println("LIST SIZE IS "
										+ list.size());

							} catch (Exception e) {

								e.printStackTrace();
							}

							handler.sendEmptyMessage(1);

						}

					}

				} catch (SQLException e) {

					e.printStackTrace();
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});
		t.start();

	}

	@Override
	public void onInit(int status) {

		tts.setLanguage(Locale.getDefault());

	}

	/*
	 * private void ShowDialog() { final Dialog d = new
	 * Dialog(MainActivity.this,
	 * android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
	 * 
	 * ImageView img = new ImageView(MainActivity.this);
	 * 
	 * img.setImageDrawable(getResources().getDrawable(R.drawable.almonds));
	 * 
	 * d.setContentView(img); d.show();
	 * 
	 * Handler h = new Handler(); h.postDelayed(new Runnable() {
	 * 
	 * @Override public void run() { d.dismiss(); } }, 1000); }
	 */

	@Override
	protected void onResume() {

		super.onResume();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class),REQUESTCODE);
		try {
			parentId = Integer.parseInt(topList.get(0).getCategoryId());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		System.out.println("result code " + resultCode);

		if (resultCode == 3) {
			
			if(parentId == 0)
				parentId = 1;
			LoadCategories(parentId);
			
		}

	}

	public void onDelete(View v) {

		final DataObject object = (DataObject) v.getTag();

		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setMessage("Are you sure you want to delete this category ?");
		dialog.setPositiveButton("Yes",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("object " + object);
						BolApp.dataHelper.DeleteCategory(Integer
								.parseInt(object.getCategoryId()));

						LoadCategories(Integer.parseInt(object.getParentId()));
					}
				});

		dialog.setNegativeButton("No", null);
		dialog.show();

	}

	public void LoadCategories(final int parentId) {
		
		System.out.println("parent id is "+parentId);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					BolApp.dataHelper.open();
					list = BolApp.dataHelper.GetCategories(parentId);
					if(list!=null)
					System.out.println("list.size "+list.size());
					boolean isLocked = BolApp.sp.getBoolean("lock_locked", false);
					System.out.println("Is locked "+isLocked);
					if (!isLocked) {
						
						if(list!=null)
						list.add(AddCategoryObject);
						else
						{
							list = new ArrayList<DataObject>();
							list.add(AddCategoryObject);
						}
					}
					else
					{
						if(list == null)
							list = new ArrayList<DataObject>();
					}
					handler.sendEmptyMessage(1);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		t.start();
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		AlertDialog .Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setMessage("Are you sure you want to exit ?");
		alert.setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		alert.setNegativeButton("No", null);
		alert.show();
	}
	
	
	@Override
	protected void onStop() {
		
		super.onStop();
		
		if(mp!=null)
		{
			mp.stop();
			mp.reset();
			mp.release();
		}
	}
}
