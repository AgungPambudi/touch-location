package com.drocode.android.touchlocation.geo;

public class GeoCodeResult {
	public String street;
	public String neighborhood;
	public String city;
	public String postal;
	public String state;
	public String country;

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		if (street != null) {
			builder.append("Street: " + street);
		} else {
			builder.append("Street: Unknown");
		}

		if (neighborhood != null) {
			builder.append("\nArea: " + neighborhood);
		} else {
			if (city != null) {
				builder.append("\nArea: " + city);
			} else {
				builder.append("\nArea: Unknown");
			}
		}

		if (postal != null) {
			builder.append("\nPostal Code: " + postal);
		} else {
			builder.append("\nPostal Code: Unknown");
		}

		if (state != null) {
			builder.append("\nState: " + state);
		} else {
			builder.append("\nState: Unknown");
		}

		if (country != null) {
			builder.append("\nCountry: " + country);
		} else {
			builder.append("\nCountry: Unknown");
		}

		return builder.toString();
	}
}
