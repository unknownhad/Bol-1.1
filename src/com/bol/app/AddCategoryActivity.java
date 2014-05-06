package com.bol.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.BolApp;
import com.model.classes.DataObject;

public class AddCategoryActivity extends Activity implements OnClickListener {

	private static int BROWSE_PICTURE = 1;
	private static int CAPTURE_PICTURE = 2;
	public  String picturePath;
	private  Uri capturedImageUri;
	ImageView profileImage;
	private Bitmap bitmap;
	Button createBtn, cancelBtn;
	int parentId;
	EditText Name;
	String dirPath;
	private File f;
	static Uri selectedImage;
	boolean isCatAlreadyAdded;
	boolean isRecording;
	
	
	private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private Button mRecordButton = null;
    private MediaRecorder mRecorder = null;

    TextView RecordingLabel;
    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_page);
		
		

		Name = (EditText) findViewById(R.id.name_field);
		profileImage = (ImageView) findViewById(R.id.profile_img);
		createBtn = (Button) findViewById(R.id.create);
		RecordingLabel = (TextView)findViewById(R.id.recording_label);
		cancelBtn = (Button) findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(this);
		createBtn.setOnClickListener(this);
		dirPath = Environment.getExternalStorageDirectory() + "";
		
		mRecordButton = (Button)findViewById(R.id.recording_btn);
		mRecordButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(isRecording)
				{
					stopRecording();
					
				}
				else
				{
					startRecording();
				}
			}
		});
		

		parentId = Integer.parseInt(getIntent().getExtras().getString(
				"parent_id"));
		
		MainActivity.parentId = parentId;
		if(parentId == 0)
			parentId = 1;
		
		AudioRecordTest();
		
		
Name.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				
				if(!hasFocus && Name.getText().toString().length()>0)
				{
					isCatAlreadyAdded = BolApp.dataHelper.isAlreadyAdded(""+parentId, Name.getText().toString());
				
				if(isCatAlreadyAdded)
					Name.setError("This category is already added");
				}
				
			}
		});
		System.out.println("parent id is "+parentId);
	
		if (savedInstanceState != null) {
			
			try {
				capturedImageUri = Uri.parse(savedInstanceState.getString("uri"));
				SetProfileImage(getPicturePath(capturedImageUri));
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}

		profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ShowDialog();

			}
		});

	}

	/**
	 * Browse for a picture.
	 */
	private void browsePicture() {
		/*
		 * Intent browsePhotoIntent = new Intent(Intent.ACTION_PICK,
		 * android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		 */

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, BROWSE_PICTURE);
		// startActivityForResult(browsePhotoIntent, BROWSE_PICTURE);
	}

	/**
	 * Starts the camera to capture a photo
	 */
	private void capturePicture() {
		String fileName = "Orizon_Image_Capture.jpg";
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.Images.Media.TITLE, fileName);
		contentValues.put(MediaStore.Images.Media.DESCRIPTION,
				"Image capture by camera");

		capturedImageUri = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

		Intent capturePictureIntent = new Intent(
				"android.media.action.IMAGE_CAPTURE");
		capturePictureIntent
				.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);

		startActivityForResult(capturePictureIntent, CAPTURE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == BROWSE_PICTURE && resultCode == RESULT_OK
				&& data != null) {
			selectedImage = data.getData();
			System.out.println(data.getDataString() + " selectedimage "
					+ selectedImage);

			picturePath = getPicturePath(selectedImage);

			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {

					SetProfileImage(picturePath);

				}
			});
			t.start();

		} else if (requestCode == CAPTURE_PICTURE
				&& resultCode == Activity.RESULT_OK) {

			picturePath = getPicturePath(capturedImageUri);
			System.out.println("Picture pathhh is "+picturePath);

			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {

					System.out.println("picture path is s " + picturePath);
					SetProfileImage(picturePath);

				}
			});

			t.start();
		}

	}

	/**
	 * Extracts the image path from the given uri.
	 * 
	 * @param selectedImage
	 * @return
	 */
	public String getPicturePath(Uri selectedImage) {

		String[] filePathColumn = { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media._ID, MediaStore.Images.Thumbnails._ID,
				MediaStore.Images.ImageColumns.ORIENTATION };

		System.out.println("filepathcolumn " + filePathColumn);

		System.out.println("selectedImage " + selectedImage);

		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String path = cursor.getString(columnIndex);
		cursor.close();
		return path;

	}

	/**
	 * Decodes a URI into a Bitmap object.
	 * 
	 * @param c
	 * @param uri
	 * @param requiredSize
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
			throws FileNotFoundException {
		BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
		bitmapFactoryOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri),
				null, bitmapFactoryOptions);

		int width_tmp = bitmapFactoryOptions.outWidth, height_tmp = bitmapFactoryOptions.outHeight;
		int scale = 1;

		while (true) {
			if (width_tmp <= requiredSize && height_tmp <= requiredSize)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		System.out.println("in decode " + uri);
		bitmapFactoryOptions = new BitmapFactory.Options();
		bitmapFactoryOptions.inSampleSize = scale;
		return BitmapFactory.decodeStream(c.getContentResolver()
				.openInputStream(uri), null, bitmapFactoryOptions);
	}

	private void SetProfileImage(String picturePath) {
		if (picturePath != null) {
			File imageFile = new File(picturePath);
			Uri filePath = Uri.fromFile(imageFile);

			try {

				System.out.println("uri is " + filePath);
				bitmap = decodeUri(AddCategoryActivity.this, filePath, 300);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						profileImage.setImageBitmap(bitmap);
					}
				});

			} catch (Exception e) {

				e.printStackTrace();
				return;
			}

		}
	}

	@Override
	public void onClick(View v) {

		if (v == cancelBtn) {
			setResult(-2);
			finish();
		} else {
			setResult(3);

			if (Name.getText().toString().length() > 0)
			// create new category
			{
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {

						isCatAlreadyAdded = BolApp.dataHelper.isAlreadyAdded(""+parentId, Name.getText().toString());

						if(!isCatAlreadyAdded)
						{
						CreateNewCategory();
						 finish();
						}
						else
						{
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									Toast.makeText(AddCategoryActivity.this, "Please rename the category to a different name.", Toast.LENGTH_LONG).show();

								}
							});
						}

					}
				});
				t.start();
			} else {
				Toast.makeText(AddCategoryActivity.this,
						"Please enter the name of image.", Toast.LENGTH_LONG)
						.show();
			}
		}

	}

	public void CreateNewCategory() {

		String path = SaveBitmap(bitmap, Name.getText().toString());
		System.out.println("path is " + path);
		DataObject object = new DataObject();
		object.setCategoryName(Name.getText().toString());
		if(parentId == 0)
			parentId = 1;
		object.setParentId("" + parentId);
		object.setAudioPath(RenameFile("audio.3gp", Name.getText().toString().trim()+".3gp"));
		object.setImg_name(Name.getText().toString());
		object.setImg_path(path);
		
		System.out.println("Result iss "+BolApp.dataHelper.AddCategory(object));
	}

	private String SaveBitmap(Bitmap bitmap, String filename) {

		File mFolder = new File(dirPath + "/Bol");

		if (!mFolder.exists()) {
			mFolder.mkdir();
		}

		f = new File(mFolder.getAbsolutePath(), filename + ".png");

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			if (bitmap != null)
				bitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
			System.out.println("bitmap " + bitmap);

			fos.flush();
			fos.close();
			// MediaStore.Images.Media.insertImage(getContentResolver(), b,
			// "Screen", "screen");
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

		if (f != null)
			return f.toString();
		else
			return null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		try {
			outState.putString("picpath", picturePath);
			outState.putString("uri", capturedImageUri.toString());
		} catch (Exception e) {
			
			e.printStackTrace();
		}

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	public void ShowDialog() {
		AlertDialog.Builder alert = new Builder(AddCategoryActivity.this);
		alert.setMessage("What do you want to do ?");
		alert.setPositiveButton("Capture",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						capturePicture();
					}
				});

		alert.setNegativeButton("Cancel", null);
		alert.setNeutralButton("Gallery",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						browsePicture();
					}
				});
		
		alert.show();
	}
	
	
	@Override
	protected void onPause() {
		
		super.onPause();
		
		   if (mRecorder != null) {
	            mRecorder.release();
	            mRecorder = null;
	        }

	        if (mPlayer != null) {
	            mPlayer.release();
	            mPlayer = null;
	        }

	}
	
	
	 private void onRecord(boolean start) {
	        if (start) {
	            startRecording();
	        } else {
	            stopRecording();
	        }
	    }

	    private void onPlay(boolean start) {
	        if (start) {
	            startPlaying();
	        } else {
	            stopPlaying();
	        }
	    }

	    private void startPlaying() {
	        mPlayer = new MediaPlayer();
	        try {
	            mPlayer.setDataSource(mFileName);
	            mPlayer.prepare();
	            mPlayer.start();
	        } catch (IOException e) {
	            Log.e(LOG_TAG, "prepare() failed");
	        }
	    }

	    private void stopPlaying() {
	        mPlayer.release();
	        mPlayer = null;
	    }

	    private void startRecording() {
	    	
	    	mRecordButton.setText("Stop");
	    	RecordingLabel.setText("Recording...");
	        mRecorder = new MediaRecorder();
	        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	        mRecorder.setOutputFile(mFileName);
	        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	        try {
	            mRecorder.prepare();
	        } catch (IOException e) {
	            Log.e(LOG_TAG, "prepare() failed");
	        }

	        mRecorder.start();
	        
	        isRecording = true;
	    }

	    private void stopRecording() {
	    	mRecordButton.setText("Record");
	    	RecordingLabel.setText("");
	    	isRecording = false;
	        mRecorder.stop();
	        mRecorder.release();
	        mRecorder = null;
	    }

	    public class RecordButton extends Button {
	        boolean mStartRecording = true;

	        OnClickListener clicker = new OnClickListener() {
	            public void onClick(View v) {
	                onRecord(mStartRecording);
	                if (mStartRecording) {
	                    setText("Stop recording");
	                } else {
	                    setText("Start recording");
	                }
	                mStartRecording = !mStartRecording;
	            }
	        };

	        public RecordButton(Context ctx) {
	            super(ctx);
	            setText("Start recording");
	            setOnClickListener(clicker);
	        }
	    }

	    class PlayButton extends Button {
	        boolean mStartPlaying = true;

	        OnClickListener clicker = new OnClickListener() {
	            public void onClick(View v) {
	                onPlay(mStartPlaying);
	                if (mStartPlaying) {
	                    setText("Stop playing");
	                } else {
	                    setText("Start playing");
	                }
	                mStartPlaying = !mStartPlaying;
	            }
	        };

	        public PlayButton(Context ctx) {
	            super(ctx);
	            setText("Start playing");
	            setOnClickListener(clicker);
	        }
	    }

	    public void AudioRecordTest() {
	        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
	        mFileName += "/audio.3gp";
	    }
	    
	    
	    private String RenameFile(String fromFile, String toFile)
	    {
	    	File from = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),fromFile);
	    	File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),toFile);
	    	from.renameTo(to);
	    	from.delete();
	    	
	    	return to.toString();
	    }


}
