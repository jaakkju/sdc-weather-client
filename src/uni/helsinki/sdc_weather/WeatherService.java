package uni.helsinki.sdc_weather;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class WeatherService extends IntentService  {
	private static final String TAG = "WeatherService";

	public WeatherService() {
		super("WeatherService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
        // Gets data from the incoming Intent
//        String str = intent.getDataString();
        Log.i(TAG, "Service started");
		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

}
