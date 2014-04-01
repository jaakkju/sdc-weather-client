package uni.helsinki.sdc_weather.model;

import java.util.Date;

public class Measurement {
	private Date timestamp;
	private Double longitude;
	private Double latitude;
	
	private double temperatureCelsius;
	private double pressureAtmospheres;
	private double humidity;
	
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public double getTemperatureCelsius() {
		return temperatureCelsius;
	}
	public void setTemperatureCelsius(double temperatureCelsius) {
		this.temperatureCelsius = temperatureCelsius;
	}
	public double getPressureAtmospheres() {
		return pressureAtmospheres;
	}
	public void setPressureAtmospheres(double pressureAtmospheres) {
		this.pressureAtmospheres = pressureAtmospheres;
	}
	public double getHumidity() {
		return humidity;
	}
	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(humidity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
		temp = Double.doubleToLongBits(pressureAtmospheres);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(temperatureCelsius);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Measurement other = (Measurement) obj;
		if (Double.doubleToLongBits(humidity) != Double
				.doubleToLongBits(other.humidity))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (Double.doubleToLongBits(pressureAtmospheres) != Double
				.doubleToLongBits(other.pressureAtmospheres))
			return false;
		if (Double.doubleToLongBits(temperatureCelsius) != Double
				.doubleToLongBits(other.temperatureCelsius))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Measurement [timestamp=" + timestamp + ", longitude="
				+ longitude + ", latitude=" + latitude
				+ ", temperatureCelsius=" + temperatureCelsius
				+ ", pressureAtmospheres=" + pressureAtmospheres
				+ ", humidity=" + humidity + "]";
	}
	
}
