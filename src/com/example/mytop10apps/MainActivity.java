package com.example.mytop10apps;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.textView1);
		
		
		new DownloadData().execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"); //executes the class DownloadData
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	// What actually happens
	//1. Starts do_in_background
	//2. Finds the first URl and calls downloadXML (function)
	//3.Open connection, find contents of the file
	//4.returns data from the method donwnloadXML and gets stored into myXMLdata
	//5. Logs what's happening in OnPostExecute
	//6. Data needs to be put on screen
	
	
	
	//creating an inner class to download the data
	// three variables inside ASync class, string is the URl, void is the progress of the download
	// and string is the returned contents of the XML file
	private class DownloadData extends AsyncTask<String, Void, String> {
		
		//creates a variable to store data
		String myXmlData;
		//... means it will accept many URLS, we can process more than one thing
		protected String doInBackground(String...urls) {
			//try block is trying to catch an error
			try {
				myXmlData = downloadXML(urls[0]); //downloadXML is a function we are going to write
				// urls[0] is the url parameter incoming and the very first element
				
			} catch(IOException e) {
				return "Unable to donwload XML file";      // when processing files, need to catch errors
			}
			
			return "";
		}
		//need another method that is executed when the above method is finally executed
		//void is not returning anything
		protected void onPostExecute(String result) {
			Log.d("OnPostExecute", myXmlData); //logs output
			textView.setText(myXmlData);
			
		}
		
		
		// creating the donwloadXML method that is utilized above
		//throws = if there is an error in method, throws the error back to the calling method
		// (try, catch is calling this throw
		private String  downloadXML(String theURl) throws IOException {
			int BUFFER_SIZE = 2000; //hold characters download
			InputStream is = null; //memory of input stream is cleared before starting
			
			String xmlContents = ""; //temporary container for our data
			try {
				URL url = new URL(theURl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				int response = conn.getResponseCode();
				Log.d("DownloadXML", "The response returned is: " + response);
				is = conn.getInputStream();
				//create a reader to retrieve data 2000 characters at a time
				InputStreamReader isr = new InputStreamReader(is);
				int charRead; //variable checks each character
				
				char[] inputBuffer = new char[BUFFER_SIZE]; //creates an array of size BUFFER_SIZE
				try {
					while((charRead = isr.read(inputBuffer))>0) //while the number of characters > 0
					{
						String readString = String.copyValueOf(inputBuffer, 0, charRead); //starts position 0 and goes through
						//to number of characters read. readString contains contents
						xmlContents += readString; // adds readString to the xmlContents variable
						inputBuffer = new char[BUFFER_SIZE]; //empties buffer again to size 2000
						//this loop will keep going until it returns 0!
						
					}
					
					// Finally need to return xmlContents
					return xmlContents;
					
				} catch(IOException e) {
					e.printStackTrace(); //shows where it crashed and which methods called
					return null; //tells us there was a problem and don't return anything
				}
				
			
			//finally says no matter what if there's an error, still execute this code and
			//close the input stream	
			} finally {
				if(is != null)
					is.close();
			}
		
		}
		

	}
}
