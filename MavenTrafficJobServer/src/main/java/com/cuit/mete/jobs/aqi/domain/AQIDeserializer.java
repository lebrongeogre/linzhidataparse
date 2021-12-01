package com.cuit.mete.jobs.aqi.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class AQIDeserializer extends StdDeserializer<AQIData> {


    public AQIDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AQIData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        AQIData aqiData = new AQIData();
        while (!jsonParser.isClosed()) {
            JsonToken jsonToken = jsonParser.nextToken();

            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                String fieldName = jsonParser.getCurrentName();
                //System.out.println(fieldName);

                jsonToken = jsonParser.nextToken();

                if ("AQI".equals(fieldName)) {
                    aqiData.setAQI(jsonParser.getValueAsDouble());
                } else if ("CITYCODE".equals(fieldName)) {
                    aqiData.setCITYCODE(jsonParser.getValueAsString());
                } else if ("CITYNAME".equals(fieldName)) {
                    aqiData.setCITYNAME(jsonParser.getValueAsString());
                } else if ("CO".equals(fieldName)) {
                    aqiData.setCO(jsonParser.getValueAsDouble());
                } else if ("CO_MARK".equals(fieldName)) {
                    aqiData.setCO_MARK(jsonParser.getValueAsBoolean());
                } else if ("INDEX_MARK".equals(fieldName)) {
                    aqiData.setINDEX_MARK(jsonParser.getValueAsBoolean());
                } else if ("NO2".equals(fieldName)) {
                    aqiData.setNO2(jsonParser.getValueAsDouble());
                } else if ("NO2_MARK".equals(fieldName)) {
                    aqiData.setNO2_MARK(jsonParser.getValueAsBoolean());
                } else if ("O3".equals(fieldName)) {
                    aqiData.setO3(jsonParser.getValueAsDouble());
                } else if ("O3_MARK".equals(fieldName)) {
                    aqiData.setO3_MARK(jsonParser.getValueAsBoolean());
                } else if ("PM10".equals(fieldName)) {
                    aqiData.setPM10(jsonParser.getValueAsDouble());
                } else if ("PM2_5".equals(fieldName)) {
                    aqiData.setPM2_5(jsonParser.getValueAsDouble());
                } else if ("PM2_5_MARK".equals(fieldName)) {
                    aqiData.setPM2_5_MARK(jsonParser.getValueAsBoolean());
                } else if ("PRIMARYPOLLUTANT".equals(fieldName)) {
                    aqiData.setPRIMARYPOLLUTANT(jsonParser.getValueAsString());
                } else if ("SO2".equals(fieldName)) {
                    aqiData.setSO2(jsonParser.getValueAsDouble());
                } else if ("TIMEPOINT".equals(fieldName)) {
                    aqiData.setTIMEPOINT(jsonParser.getValueAsLong());
                }
            }
        }
        return aqiData;
    }
}
