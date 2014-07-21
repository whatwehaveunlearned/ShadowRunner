package com.lava.shadowrunner;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MenuActivity extends Activity implements OnInitListener {
	private static final String TAG = "MenuActivity";
	private TextToSpeech tts;
	private boolean mAttachedToWindow;
	private boolean mTTSSelected;
	FileInputStream fis;
	final StringBuffer storedString = new StringBuffer();
	float latit_float;
	float long_float;
	
	//To test Path class
	private Path path = new Path();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTTSSelected = false;
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mAttachedToWindow = true;
		openOptionsMenu();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mAttachedToWindow = false;
	}	

	@Override
	public void openOptionsMenu() {
		if (mAttachedToWindow) {
			super.openOptionsMenu();
		}
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String file = "test.txt";
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		Intent intent = new Intent(this, LocationActivity.class);
		Bundle b;
		switch (item.getItemId()) {
		case R.id.stop:
			stopService(new Intent(this, AppService.class));
			return true;

		case R.id.tts:
			mTTSSelected = true;
			tts = new TextToSpeech(this, this);  
			tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
				@Override
				public void onDone(String utteranceId) {
					Log.w(TAG, "onDone");
					if (tts != null) {
						tts.stop();
						tts.shutdown();
					}                  
					finish();
				}

				@Override
				public void onError(String utteranceId) {
					Log.w(TAG, "onError");
				}

				@Override
				public void onStart(String utteranceId) {
					Log.w(TAG, "onStart");
				}
			});
			return true;

		case R.id.asr:
			//startActivityForResult(i, 0);  
			return true;
		
		case R.id.location:
			startActivity(intent);
			return true;
			
		case R.id.load:
			StringBuffer storage = load(file);
			System.out.println(storage);
			Toast.makeText(this,"distance: " + storage, Toast.LENGTH_LONG)
	        .show();
			return true;
			
		case R.id.calculate:
			b = intent.getExtras();
			System.out.println(b);
			System.out.println(intent.getExtras());
			System.out.println(intent.getDoubleExtra("distance", 5.0));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void onOptionsMenuClosed(Menu menu) {
		if (!mTTSSelected) 
			finish();
	}


	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"helloID");				
				tts.speak("Hello Glass!", TextToSpeech.QUEUE_FLUSH, map);
			}
		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}
	
	//Method used to read the values from file
	public StringBuffer load(String file){
		FileInputStream fis;
		final StringBuffer storedString = new StringBuffer();

		try {
		    fis = openFileInput(file);
		    DataInputStream dataIO = new DataInputStream(fis);
		    String strLine = null;

		    if ((strLine = dataIO.readLine()) != null) {
		        storedString.append(strLine);
		    }

		    dataIO.close();
		    fis.close();
		    Toast.makeText(this, storedString, Toast.LENGTH_LONG)
	        .show();
		    return storedString;
		}
		catch  (Exception e) {  
			return null;
		}
		}
	
	//Method to convert values
	public float convert (StringBuffer stringbuffer){
		float f;
		float [] latitudes;
		float longitudes [];
		List<String> coordinates = Arrays.asList(stringbuffer.toString().split(","));
		latitudes = new float[coordinates.size()];
		longitudes = new float[coordinates.size()];
		System.out.println(coordinates);
		System.out.println(coordinates.size()-2);
		for (int i=1;i<coordinates.size()-2;i++){
			
			System.out.println(i);
			System.out.println(coordinates.get(i).toString());
			if( (i & 1) == 0 ){//even
				latitudes[i] = Float.parseFloat(coordinates.get(i).toString());
			}else{
				longitudes[i] = Float.parseFloat(coordinates.get(i).toString());
			}
		}
		
		return 0;
		
	}
}
