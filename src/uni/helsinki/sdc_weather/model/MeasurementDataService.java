package uni.helsinki.sdc_weather.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MeasurementDataService {
	
	public final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	public List<Measurement> fromResultJson(String json) throws JSONException, ParseException {
		List<Measurement> res = new ArrayList<Measurement>();
		
		JSONObject root = (JSONObject) new JSONTokener(json).nextValue();

		JSONArray arr = root.getJSONArray("result");
		
		for (int i = 0; i < arr.length(); ++i) {
			res.add(fromJsonObject(arr.getJSONObject(i)));
		}
		
		return res;
	}
	
	public Measurement fromString(String json) throws JSONException, ParseException {
		JSONObject o = (JSONObject) new JSONTokener(json).nextValue();
		return fromJsonObject(o);
	}
	
	public Measurement fromJsonObject(JSONObject o) throws JSONException, ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ROOT);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		Measurement m = new Measurement();
		
		m.setTimestamp(sdf.parse(o.getString("timestamp")));
		m.setLongitude(o.getDouble("longitude"));
		m.setLatitude(o.getDouble("latitude"));
		m.setHumidity(o.getDouble("humidity"));
		m.setPressureAtmospheres(o.getDouble("pressureAtmospheres"));
		m.setTemperatureCelsius(o.getDouble("temperatureCelsius"));
		
		return m; 
	}
	
	public String toJson(Measurement m) throws JSONException {
		SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ROOT);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		JSONObject o = new JSONObject();
			
		o.put("timestamp", sdf.format(m.getTimestamp()));
		
		o.put("longitude", m.getLongitude());
		o.put("latitude", m.getLatitude());
		o.put("humidity", m.getHumidity());
		o.put("pressureAtmospheres", m.getPressureAtmospheres());
		o.put("temperatureCelsius", m.getTemperatureCelsius());
		
		return o.toString();
	}

}
