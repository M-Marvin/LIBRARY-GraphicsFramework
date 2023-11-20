package de.m_marvin.ln2cs;

import de.m_marvin.simplelogging.printing.Logger;

public class ParameterDataSet {
	public static final String PARAM_EVAP_TEMP = "EVT";		// Evaporator Temperature [C°]
	public float evaporatorTemperature;

	public static final String PARAM_EVAP_PRESS = "EVP";	// Evaporator Pressure [mbar]
	public float evaportatorPressure;

	public static final String PARAM_COND_TEMP = "CDT";		// Condenser Temperature [C°]
	public float condesserTemperature;
	
	public static final String PARAM_COND_FAN_RPM = "CDF";	// Condenser Fan Speed [rpm]
	public float condenserFanSpeed;
	
	public static final String PARAM_COMP_POWER = "CMP";	// Compressor Power [%]
	public float compressorPower;

	public static final String PARAM_EXPV_STATE = "EXP";	// Expansion Valve State [%]
	public float expansionValveState;
	
	public static float parseField(String dataString, String fieldName) throws NumberFormatException {
		for (String s : dataString.split("\n")) {
			if (s.startsWith(fieldName)) {
				return Float.parseFloat(s.substring(fieldName.length()));
			}
		}
		return -1;
	}

	public boolean parseData(String dataString) {
		
		try {
			
			this.evaporatorTemperature = 	parseField(dataString, PARAM_EVAP_TEMP);
			this.evaportatorPressure = 		parseField(dataString, PARAM_EVAP_PRESS);
			this.condesserTemperature = 	parseField(dataString, PARAM_COND_TEMP);
			this.condenserFanSpeed = 		parseField(dataString, PARAM_COND_FAN_RPM);
			this.compressorPower = 			parseField(dataString, PARAM_COMP_POWER);
			this.expansionValveState =		parseField(dataString, PARAM_EXPV_STATE);
			return true;
			
		} catch (NumberFormatException e) {
			Logger.defaultLogger().logWarn("Could not parse data string!");
			return false;
		}
		
	}
	
}
