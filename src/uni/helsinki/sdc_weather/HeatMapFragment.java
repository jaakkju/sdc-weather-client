package uni.helsinki.sdc_weather;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HeatMapFragment extends Fragment {
	public static Fragment newInstance(Context context) {
		MeasurementFragment frag = new MeasurementFragment();
		
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_heatmap, null);
		
		return root;
	}

}
