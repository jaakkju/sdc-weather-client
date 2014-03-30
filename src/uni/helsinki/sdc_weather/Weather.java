package uni.helsinki.sdc_weather;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sensorcon.sensordrone.android.Drone;

public class Weather extends Activity {
	private static final String TAG = "SDC-Weather";
	private static final String URL =  "http://sdc-weather.herokuapp.com/measurement";
	private static final String MAC = "XX:XX:XX:XX:XX:XX";
	
	private static final String SAMPLE_JSON = "{ \"latitude\": 60.123, \"longitude\": 24.456, \"timestamp\": \"2014-03-21T12:34:56Z\", \"temperatureCelsius\": -5.0, \"pressureAtmospheres\": 1.0, \"humidity\": 69.96 }";
	
	Button buttonGet;
	Button buttonPost;
	Button buttonMeasure;
	Drone drone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		// Connecting to Drone and enabling sensors 
		enableDrone(MAC);

		buttonGet = (Button) findViewById(R.id.ButtonGet);
		buttonGet.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	new DoGet(){

					@Override
					protected void onPostExecute(String result) {
						TextView resultView = (TextView) findViewById(R.id.textView_result);
						resultView.setText(result);
					}
		    	}.execute();
		    }
		});
		
		buttonPost = (Button) findViewById(R.id.ButtonPost);
		buttonPost.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
				try {
					// Using HTTP Post with sample data
					JSONObject jsonObj = new JSONObject(SAMPLE_JSON);
					new DoPost().execute(jsonObj);
				} catch (JSONException e) {
					Log.i(TAG, "JSON Parsing failed", e);
				}
		    }
		});
		
		buttonMeasure = (Button) findViewById(R.id.ButtonMeasure);
		buttonMeasure.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	new DoMeasure().execute();
		    }
		});		
	}
	
	private void enableDrone(String mac) {
		drone = new Drone();
		drone.btConnect(mac);
		
		// For Debug
		Log.i(TAG, "Connected to SensorDrone - " + String.valueOf(drone.isConnected));
		Log.i(TAG, "Sensor: Temperature - " + String.valueOf(drone.enableTemperature()));
		Log.i(TAG, "Sensor: Humidy - " + String.valueOf(drone.enableHumidity()));
		Log.i(TAG, "Sensor: Pressure - " + String.valueOf(drone.enablePressure()));
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}
	
	private class DoMeasure extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			Log.i(TAG, "Measuring Temperature: " + String.valueOf(drone.measureTemperature()));
			Log.i(TAG, "Measuring Humididy: " + String.valueOf(drone.measureHumidity()));
			Log.i(TAG, "Measuring Pressure: " + String.valueOf(drone.measurePressure()));
			
			Log.i(TAG, "Temperature value: " + String.valueOf(drone.temperature_Celsius));
			Log.i(TAG, "Humidity value: " + String.valueOf(drone.humidity_Percent));
			Log.i(TAG, "Pressure value: " + String.valueOf(drone.pressure_Atmospheres));
			
			return true;
		}
	}
	
	private class DoPost extends AsyncTask<JSONObject, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(JSONObject... params) {
		Boolean result = true;

			// Post all JSON objects
			for (int i = 0; i < params.length; i++) {
			    try {
					DefaultHttpClient httpclient = new DefaultHttpClient();
					HttpPost httpPostRequest = new HttpPost(URL);

					StringEntity se = new StringEntity(params[i].toString());

					// Set HTTP parameters
					httpPostRequest.setEntity(se);
					
					// It might be that we don't need this - httpPostRequest.setHeader("Accept", "application/json");
					httpPostRequest.setHeader("Content-type", "application/json");

					// Handles what is returned from the page and get data
					HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
					Log.i(TAG, response.getStatusLine().toString());
					
					if (response.getStatusLine().hashCode() != 200) {
						Log.w(TAG, "HTTP Post - returned HTTP Code " + response.getStatusLine().getStatusCode());
						result = false;
					}
					
				} catch (Exception e) {
					Log.e(TAG, "HTTP post failed: ", e);
				}				
			}
			return result;
		}
	}
	
	private class DoGet extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(URL);
				
				HttpResponse response = httpclient.execute(httpget);

				Log.i(TAG, response.getStatusLine().toString());
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					InputStream inStream = entity.getContent();
					result = convertStreamToString(inStream);
					inStream.close();
					
				} else {
					Log.e(TAG, "HTTP Get received null entity");
				}

			} catch (Exception e) {
				Log.e(TAG, "Connection failed: ", e);
			}
			return result;
		}
	}

	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
}
