package com.lava.shadowrunner;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	GPSTracker gps;
	
	
	

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
		StringBuilder testrunstringbuilder;
		String[] testrun;
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		//start GPSTracker class service
		//gps = new GPSTracker(MenuActivity.this);
		Intent intent = new Intent(this, LocationActivity.class);
		
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
			
			case R.id.run:
				testrunstringbuilder = loadtest ();
				testrun = testrunstringbuilder.toString().split(",");
				System.out.println(testrun);
				return true;
			
			case R.id.calculate:
				StringBuffer Distances = load(file);
				double [] distance = convert(Distances);
				System.out.println(distance);
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
	public double[] convert (StringBuffer stringbuffer){
		double [] distance;
		List<String> distanceArray = Arrays.asList(stringbuffer.toString().split(","));
		distance = new double[distanceArray.size()];
		System.out.println(distanceArray);
		System.out.println(distanceArray.size());
		for (int i=0;i<distanceArray.size();i++){
				distance[i] = Double.parseDouble(distanceArray.get(i).toString());
		}
		
		return distance;
	}
	
	//Method to load test runs
	public StringBuilder loadtest (){
		InputStream ins = getResources().openRawResource(
				getResources().getIdentifier("raw/test1",
						"raw", getPackageName()));
		BufferedReader r = new BufferedReader(new InputStreamReader(ins));
		StringBuilder total = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null) {
				total.append(line);
	    
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total;
	}
}
