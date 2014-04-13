package uni.helsinki.sdc_weather.measure;

import com.sensorcon.sensordrone.android.Drone;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MeasureHandler implements OnClickListener {
	private static final String TAG = "SDC-Weather";

	private static final String MAC[] = {"00:17:EC:11:C0:0F", "00:17:EC:11:C0:0F"};
	Drone drone;
	
	private ViewGroup root;
	
	private void enableDrone(String mac) {
		drone = new Drone();
		drone.btConnect(mac);
		
		// For Debug
		Log.i(TAG, "Connected to SensorDrone - " + String.valueOf(drone.isConnected));
		Log.i(TAG, "Sensor: Temperature - " + String.valueOf(drone.enableTemperature()));
		Log.i(TAG, "Sensor: Humidy - " + String.valueOf(drone.enableHumidity()));
		Log.i(TAG, "Sensor: Pressure - " + String.valueOf(drone.enablePressure()));
	}	
	
	
	public MeasureHandler(ViewGroup root) {
		this.root = root;

		enableDrone(MAC[0]);
		
		
	}

	@Override
	public void onClick(View view) {
		MeasurementTask task = new MeasurementTask(root.getContext(), root);

		task.execute(drone);
		
	}

}
