package uni.helsinki.sdc_weather;

import uni.helsinki.sdc_weather.measure.MeasureHandler;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MeasurementFragment extends Fragment {
	
	private Button buttonMeasure;
	private Button buttonPost;
	private MeasureHandler measureHandler;
	
	public static Fragment newInstance(Context context) {
		MeasurementFragment frag = new MeasurementFragment();
		
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_measurement, null);

		measureHandler = new MeasureHandler(root);
		
		buttonMeasure = (Button)root.findViewById(R.id.measureButton);
		buttonMeasure.setOnClickListener(measureHandler);
		
		
		//buttonPost = (Button)root.findViewById(R.id.postButton);
		
		return root;
	}

	
}
