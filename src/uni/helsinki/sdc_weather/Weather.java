package uni.helsinki.sdc_weather;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import uni.helsinki.sdc_weather.model.Measurement;
import uni.helsinki.sdc_weather.model.MeasurementDataService;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sensorcon.sensordrone.Humidity_V1;
import com.sensorcon.sensordrone.android.Drone;

public class Weather extends Activity {
	private static final String TAG = "SDC-Weather";
	private static final String URL =  "http://sdc-weather.herokuapp.com/measurement";
	private static final String MAC[] = {"00:17:EC:11:C0:0F", "00:17:EC:11:C0:0F"};
	
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
		enableDrone(MAC[0]);

		buttonGet = (Button) findViewById(R.id.ButtonGet);
		buttonGet.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	new DoGet(){

					@Override
					protected void onPostExecute(List<Measurement> result) {
						TextView resultView = (TextView) findViewById(R.id.textView_result);
						resultView.setText(result.toString());
					}
		    	}.execute();
		    }
		});
		
		final Context thisContext = this;
		
		buttonPost = (Button) findViewById(R.id.ButtonPost);
		buttonPost.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	
		    	LocationManager lm = (LocationManager)thisContext.getSystemService(Context.LOCATION_SERVICE);
		    	
		    	Location loc = lm.getLastKnownLocation(LOCATION_SERVICE);
		    	
				// Using HTTP Post with sample data					
				Measurement m = new Measurement();
				m.setTimestamp(new Date());
				if (loc != null) {
					m.setLatitude(loc.getLatitude());
					m.setLongitude(loc.getLongitude());
				} else {
					m.setLatitude(60.60);
					m.setLongitude(24.24);
				}
				m.setPressureAtmospheres(0.99);
				m.setTemperatureCelsius(37.0);
				m.setHumidity(42.42);
					
				new DoPost().execute(m);
		    }
		});
		
		buttonMeasure = (Button) findViewById(R.id.ButtonMeasure);
		buttonMeasure.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	new DoMeasure() {
		    		@Override
		    		protected void onPostExecute(Measurement result) {
		    			// TODO Auto-generated method stub
		    			super.onPostExecute(result);
		    			
		    			new DoPost().execute(result);
		    		}
		    	}.execute();
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
	
	private class DoMeasure extends AsyncTask<Void, Void, Measurement> {

		@Override
		protected Measurement doInBackground(Void... arg0) {
			Log.i(TAG, "Measuring Temperature: " + String.valueOf(drone.measureTemperature()));
			Log.i(TAG, "Measuring Humididy: " + String.valueOf(drone.measureHumidity()));
			Log.i(TAG, "Measuring Pressure: " + String.valueOf(drone.measurePressure()));
			
			Log.i(TAG, "Temperature value: " + String.valueOf(drone.temperature_Celsius));
			Log.i(TAG, "Humidity value: " + String.valueOf(drone.humidity_Percent));
			Log.i(TAG, "Pressure value: " + String.valueOf(drone.pressure_Atmospheres));
			
			Measurement m = new Measurement();
			m.setHumidity(drone.humidity_Percent);
			m.setPressureAtmospheres(drone.pressure_Atmospheres);
			m.setTemperatureCelsius(drone.temperature_Celsius);
			
			m.setTimestamp(new Date());
			m.setLatitude(60.60);
			m.setLongitude(24.24);
			return m;
		}
	}
	
	private class DoPost extends AsyncTask<Measurement, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Measurement... params) {
			Boolean result = true;

			MeasurementDataService mds = new MeasurementDataService();
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			
			// Post all JSON objects
			for (int i = 0; i < params.length; i++) {
			    try {

			    	HttpPost httpPostRequest = new HttpPost(URL);

					StringEntity se = new StringEntity(mds.toJson(params[i]));

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
	
	private class DoGet extends AsyncTask<Void, Void, List<Measurement>> {

		@Override
		protected List<Measurement> doInBackground(Void... params) {
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
			try {
				return new MeasurementDataService().fromResultJson(result);
			} catch (Exception e) {
				return new ArrayList<Measurement>();
			}
		}
	}

	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
}
