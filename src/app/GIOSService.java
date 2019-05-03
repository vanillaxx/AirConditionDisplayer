package app;

import models.AirConditionIndex;
import models.Data;
import models.Sensor;
import models.Station;

import java.io.IOException;

/**
 * A mediator which creates objects from data stored in GIOS web pages
 */
public class GIOSService extends Service {

    /**
     * Gets an information about station
     * @return All stations as an array of objects
     */
    @Override
    public Station[] getAllStations() {
        return processRequest("http://api.gios.gov.pl/pjp-api/rest/station/findAll", Station[].class);
    }

    /**
     * Gets an information about sensors of particular station
     * @param stationId id of station which sensors have to be received
     * @return All sensors as an array of objects
     */
    @Override
    public Sensor[] getSensors(int stationId) {
        return processRequest("http://api.gios.gov.pl/pjp-api/rest/station/sensors/" + String.valueOf(stationId), Sensor[].class);
    }

    /**
     * Gets an information about data measured by given sensor
     * @param sensorId Data of sensor with given id will be received
     * @return Data of given senor as an object
     */
    @Override
    public Data getData(int sensorId) {
        return processRequest("http://api.gios.gov.pl/pjp-api/rest/data/getData/" + String.valueOf(sensorId), Data.class);
    }

    /**
     * Gets an information about air condition index of given station
     * @param stationId id of station which index we have to acquire
     * @return Air Condition Index of given station as an object
     */
    @Override
    public AirConditionIndex getIndex(int stationId) {
        return processRequest("http://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/" + String.valueOf(stationId), AirConditionIndex.class);
    }

}
