package com.bol.app;

import com.application.BolApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingsActivity extends Activity {
	
	Switch soundSwitch,LockSwitch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		soundSwitch = (Switch)findViewById(R.id.soundlock);
		LockSwitch = (Switch)findViewById(R.id.lock);
		
		if(BolApp.sp.getBoolean("sound_locked", false))
			soundSwitch.setChecked(true);
		else
			soundSwitch.setChecked(false);
		

		if(BolApp.sp.getBoolean("lock_locked", false))
			LockSwitch.setChecked(true);
		else
			LockSwitch.setChecked(false);
		
		
		soundSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				BolApp.edit.putBoolean("sound_locked", isChecked).apply();
				setResult(3);
				
		
			}
		});
		
		
		LockSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				BolApp.edit.putBoolean("lock_locked", isChecked).apply();
				setResult(3);
			}
		});
		
		
		
	}

}
