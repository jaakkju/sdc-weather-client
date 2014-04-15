package uni.helsinki.sdc_weather;

import uni.helsinki.sdc_weather.heatmap.HeatMapTask;
import uni.helsinki.sdc_weather.measure.MeasurementTask;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.TileOverlayOptions;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HeatMapFragment extends Fragment {
	
	MapView mapView;
	GoogleMap map;
	
	public static Fragment newInstance(Context context) {
		MeasurementFragment frag = new MeasurementFragment();
		
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_heatmap, null);
		
		mapView = (MapView)root.findViewById(R.id.heatMapView);
		mapView.onCreate(savedInstanceState);
	
		MapsInitializer.initialize(this.getActivity());
		
		map = mapView.getMap();
		
		map.getUiSettings().setMyLocationButtonEnabled(true);

		HeatMapTask task = new HeatMapTask(map);
		task.execute();
		
		return root;
	}

	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
	}
 
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

}
