package uni.helsinki.sdc_weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

public class Weather extends FragmentActivity {
	private static final String TAG = "Weather";
	
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    
    private GoogleMap mMap;
//    private MapFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        
     	Intent intent = new Intent(this, WeatherService.class);
    	startService(intent);
    	
    	Log.i(TAG, "testi");
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        addHeatMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather, menu);
        return true;
    }
    
    
    private void addHeatMap() {
        List<LatLng> list = null;

        // Get the data: latitude/longitude positions of police stations.
        try {
            list = readItems(R.raw.police_stations);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        mProvider = new HeatmapTileProvider.Builder()
            .data(list)
            .build();
        setupMap();
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
    
    /**
     * Get a GoogleMap if needed
     */
    private void setupMap() {
    	if (mMap != null) {
            return;
        }
    	SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.heatmap);
    	
        mMap = fragment.getMap();
    }
    
    /**
     * Read items from a file containing a JSON array.
     * @param rawResource
     * @return
     * @throws JSONException
     */
    private List<LatLng> readItems(int rawResource) throws JSONException {
    	Scanner scanner = new Scanner(getResources().openRawResource(rawResource));
    	String contents = "";
		try {
			while (scanner.hasNextLine()) {
				contents += scanner.nextLine();
			}
		} finally {
			scanner.close();
		}
		
		ArrayList<LatLng> list = new ArrayList<LatLng>();
		
		JSONArray jArray = new JSONArray(contents);
		int len = jArray.length();
		for(int i = 0; i < len; ++i) {
			JSONObject jo = jArray.getJSONObject(i);
			list.add(new LatLng(jo.getDouble("lat"), jo.getDouble("lng")));
		}
		
    	return list;
    }

}
