package uni.helsinki.sdc_weather.heatmap;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import uni.helsinki.sdc_weather.model.Measurement;
import uni.helsinki.sdc_weather.model.MeasurementDataService;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

public class HeatMapTask extends AsyncTask<Void, Void, List<Measurement>> {
	private static final String TAG = "SDC-Weather";
	private static final String URL =  "http://sdc-weather.herokuapp.com/measurement";

	private GoogleMap googleMap;
	
	public HeatMapTask(GoogleMap googleMap) {
		this.googleMap = googleMap;
	}
	
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	@Override
	protected List<Measurement> doInBackground(Void... arg0) {
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
			return null;
		}

	}

	@Override
	protected void onPostExecute(List<Measurement> result) {
		List<WeightedLatLng> llList = new ArrayList<WeightedLatLng>();
		//List<LatLng> llList = new ArrayList<LatLng>();
		
		for (Measurement m: result) {
			llList.add(new WeightedLatLng(new LatLng(m.getLatitude(), m.getLongitude()), m.getTemperatureCelsius()));		
			//llList.add(new LatLng(m.getLatitude(), m.getLongitude()));
		}
		
		
		int[] colors = {
				Color.rgb(0, 0, 255),
				Color.rgb(0, 255, 0),
				Color.rgb(255, 0, 0),
		};
		float[] startPoints = {0.0f, 0.5f, 1.0f};
		
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
        .weightedData(llList)
        //.data(llList)
        //.gradient(new Gradient(colors, startPoints))
        .build();
        
        

        googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
	}

}
