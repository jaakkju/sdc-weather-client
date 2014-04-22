package uni.helsinki.sdc_weather;

import uni.helsinki.sdc_weather.heatmap.HeatMapTask;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

public class HeatMapFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks{
	
	MapView mapView;
	GoogleMap map;
	LocationClient googleLocationClient;
	
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
	
	private void CenterMapOnMyLocation() {
		if(googleLocationClient == null) {
			googleLocationClient = new LocationClient(getActivity(), this, null);
		}
		
		if(googleLocationClient.isConnected() ||
				googleLocationClient.isConnecting()) {
			Log.e("fragment", "connection");
			return;
		}
			
		googleLocationClient.connect();
	}

	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
		CenterMapOnMyLocation();
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

	/**
	 * When the location service connects, this method is called.
	 * Zooms the map into your current location.
	 */
	@Override
	public void onConnected(Bundle arg0) {
		setDebug();
		Location location = googleLocationClient.getLastLocation();
		
		if(location != null) {
			LatLng myLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
			
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
					10.0f));
		} else {
			Log.i("fragment", "No location");
		}
	}

	@Override
	public void onDisconnected() {
		googleLocationClient = null;
	}
	
	private void setDebug() {
		googleLocationClient.setMockMode(true);
		Log.e("Fragment", "SetDebug");
		
		Location mock = new Location("test");
		mock.setAltitude(36.0582);
		mock.setLatitude(60.4681);
		mock.setLongitude(25.8508);
		mock.setAccuracy(0.0f);
		mock.setTime(0L);
		/*
		LatLng myLocation = new LatLng(mock.getLatitude(),
				mock.getLongitude());
		
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
				10.0f));
		*/
		googleLocationClient.setMockLocation(mock);
		
	}

}
