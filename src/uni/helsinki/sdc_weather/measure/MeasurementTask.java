package uni.helsinki.sdc_weather.measure;

import java.util.Date;

import uni.helsinki.sdc_weather.R;
import uni.helsinki.sdc_weather.model.Measurement;

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

	Context context;
	LocationManager lm;
	String provider;
	ViewGroup root;
	
	public MeasurementTask(Context context, ViewGroup root) {
		this.context = context;
		this.root = root;
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
