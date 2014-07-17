package com.lava.shadowrunner;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;

import android.widget.Toast;

public class files {
	private final static String STORETEXT="storetext.txt";
	File file = new File(STORETEXT);
	FileInputStream fis;
	final StringBuffer storedString = new StringBuffer();
	
	public void saveClicked() {
	    try{
	        OutputStreamWriter out=
	                new OutputStreamWriter(openFileOutput(STORETEXT, MODE_APPEND));
	        out.write(value1);
	        out.close();

	        Toast
	    .makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG)
	    .show();

	    }
	    catch(Throwable t){
	        Toast.makeText(this, "Exception: "+ t.toString(), Toast.LENGTH_LONG)
	        .show();

	    }
	}
	
	public void load(){
		FileInputStream fis;
		final StringBuffer storedString = new StringBuffer();

		try {
		    fis = openFileInput("storetext.txt");
		    DataInputStream dataIO = new DataInputStream(fis);
		    String strLine = null;

		    if ((strLine = dataIO.readLine()) != null) {
		        storedString.append(strLine);
		    }

		    dataIO.close();
		    fis.close();
		    Toast.makeText(this, storedString, Toast.LENGTH_LONG)
	        .show();
		}
		catch  (Exception e) {  
		}
		}	
	
}
