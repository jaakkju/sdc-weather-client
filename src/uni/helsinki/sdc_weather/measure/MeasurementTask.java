package uni.helsinki.sdc_weather.measure;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import uni.helsinki.sdc_weather.R;
import uni.helsinki.sdc_weather.model.Measurement;
import uni.helsinki.sdc_weather.model.MeasurementDataService;

import com.sensorcon.sensordrone.android.Drone;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MeasurementTask extends AsyncTask<Drone, Void, Measurement> {
	private static final String TAG = "SDC-Weather";
	private static final String URL =  "http://sdc-weather.herokuapp.com/measurement";

	Context context;
	LocationManager lm;
	String provider;
	ViewGroup root;
	
	public MeasurementTask(Context context, ViewGroup root) {
		this.context = context;
		this.root = root;
	}
	
	private void postMeasurement(Measurement measurement) {
		try {
			MeasurementDataService mds = new MeasurementDataService();
			DefaultHttpClient httpclient = new DefaultHttpClient();
			
	    	HttpPost httpPostRequest = new HttpPost(URL);
	
			StringEntity se = new StringEntity(mds.toJson(measurement));
	
			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			
			// It might be that we don't need this - httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");
	
			// Handles what is returned from the page and get data
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
			Log.i(TAG, response.getStatusLine().toString());
			
			if (response.getStatusLine().hashCode() != 200) {
				Log.w(TAG, "HTTP Post - returned HTTP Code " + response.getStatusLine().getStatusCode());
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getClass().getName() + ": " + e.getMessage(), e);
		} catch (JSONException e) {
			Log.e(TAG, e.getClass().getName() + ": " + e.getMessage(), e);
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getClass().getName() + ": " + e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}
	
	@Override
	protected Measurement doInBackground(Drone... drones) {
		Drone drone = drones[0];
		
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

		
    	lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		
    	Criteria crit = new Criteria();
    	crit.setAccuracy(Criteria.NO_REQUIREMENT);
    	crit.setCostAllowed(true);
    	crit.setPowerRequirement(Criteria.NO_REQUIREMENT);
    	crit.setAltitudeRequired(false);
    	crit.setBearingRequired(false);
    	crit.setSpeedRequired(false);

    	String provider = lm.getBestProvider(new Criteria(), false);
    	Log.i(TAG, "getBestProvider = " + provider);
		
    	Location loc = null;

		m.setTimestamp(new Date());
    	if (provider != null && (loc = lm.getLastKnownLocation(provider)) != null) {
			if (loc != null) {
				Log.i(TAG, "location = " + loc.toString());
				
				m.setLatitude(loc.getLatitude());
				m.setLongitude(loc.getLongitude());
			}
    	} else {
			Log.i(TAG, "sending dummy location");
			
			m.setLatitude(60.60);
			m.setLongitude(24.24);
    	}
    	
    	postMeasurement(m);
		
		return m;
	}

	@Override
	protected void onPostExecute(Measurement result) {
		((TextView)root.findViewById(R.id.pressureTextView)).setText(Double.toString(result.getPressureAtmospheres()));
		((TextView)root.findViewById(R.id.humidityTextView)).setText(Double.toString(result.getHumidity()));
		((TextView)root.findViewById(R.id.temperatureTextView)).setText(Double.toString(result.getTemperatureCelsius()));
		((TextView)root.findViewById(R.id.latitudeTextView)).setText(Double.toString(result.getLatitude()));
		((TextView)root.findViewById(R.id.longitudeTextView)).setText(Double.toString(result.getLongitude()));
		((TextView)root.findViewById(R.id.timestampTextView)).setText(result.getTimestamp().toString());
	}

}
