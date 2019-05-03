package app;

import ACAExceptions.*;
import com.google.gson.*;

import models.AirConditionIndex;
import models.Data;
import models.Sensor;
import models.Station;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Includes all functionalities of application
 */
public class AirConditionApplication {
    private Service service;
    private static final Gson gson = new Gson();

    public AirConditionApplication(Service service) {
        this.service = service;
    }

    /**
     * Seeks for information about air condition index of particular station
     * @param nameOfGivenStation name of station which data we want to receive
     * @return air condition index of particular data as a string
     */
    public String displayActualAirConditionIndex(String nameOfGivenStation) throws LackOfIndexException {

        AirConditionIndex index;
        try {
            index = service.getIndex(getIdOfGivenStation(nameOfGivenStation));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new LackOfIndexException();
        }
        return "Station: " + nameOfGivenStation + "\n" + index.toString();
    }

    /**
     * Seeks for a value of parameter at given time
     * @param nameOfGivenStation name of station which we are interested about
     * @param dateAndTime particular date and time in format YYYY-MM-DD HH:MM:SS
     * @param parameter name of parameter which we are interested about
     * @return Full information about value and parametres which were aplied as a string
     * @throws LackOfStationException
     * @throws LackOfParameterException
     * @throws LackOfDataException
     */
    public String displayActualValueOfParameter(String nameOfGivenStation, String dateAndTime, String parameter) throws LackOfStationException, LackOfParameterException, LackOfDataException {
        Sensor[] sensors = service.getSensors(getIdOfGivenStation(nameOfGivenStation));
        int idOfSensor = -1;
        for (int i = 0; i < sensors.length; i++) {
            if (sensors[i].getParam().getParamCode().equalsIgnoreCase(parameter)) {
                idOfSensor = sensors[i].getId();
            }
        }
        if (idOfSensor == -1) {
            throw new LackOfParameterException(parameter);
        }

        Data data = service.getData(idOfSensor);
        double actualValueOfParameter = -1;
        for (int i = 0; i < data.getValues().length; i++) {
            if (data.getValues()[i].getDate().equalsIgnoreCase(dateAndTime)) {
                actualValueOfParameter = data.getValues()[i].getValue();
            }
        }
        if (actualValueOfParameter == -1) {
            throw new LackOfDataException();
        }
        return "Station: " + nameOfGivenStation + "\nParameter: " + parameter + "\nDate and time: " + dateAndTime + "\nValue: " + String.valueOf(actualValueOfParameter);

    }

    /**
     * Counts the average value of given parameter in particular period
     * @param nameOfGivenStation name of particular station
     * @param startTime start time of period in format YYYY-MM-DD HH:MM:SS
     * @param endTime end time of period in format YYYY-MM-DD HH:MM:SS
     * @param givenParameter name of particular parameter
     * @return full information about average value and applied parameters
     * @throws Exception
     */
    public String displayAverageValueOfParameter(String nameOfGivenStation, String startTime, String endTime, String givenParameter) throws Exception {
        Sensor[] sensors = service.getSensors(getIdOfGivenStation(nameOfGivenStation));
        int id = -1;
        for (int i = 0; i < sensors.length; i++) {
            if (sensors[i].getParam().getParamCode().equalsIgnoreCase(givenParameter)) {
                id = sensors[i].getId();
            }
        }
        if (id == -1) {
            throw new LackOfParameterException(givenParameter);
        } else {
            int counter = 0;
            double sum = 0;
            Date start = getDate(startTime);
            Date finish = getDate(endTime);
            StringBuilder result = new StringBuilder();
            result.append("Stacja: " + nameOfGivenStation + "\n");
            Data data = service.getData(id);
            for (int i = 0; i < data.getValues().length; i++) {
                Date currentDate = getDate(data.getValues()[i].getDate());
                if ((currentDate.after(start) && currentDate.before(finish)) || currentDate.equals(start) || currentDate.equals(finish)) {
                    if(data.getValues()[i].getValue()!=0.0) {
                        counter++;
                        sum += data.getValues()[i].getValue();
                    }
                }
            }
            result.append("From: " + startTime + "\nTo: " + endTime);
            sum /= counter;
            result.append("\nParameter: " + givenParameter + "\n");
            result.append("Average value: " + String.valueOf(sum));
            return result.toString();
        }
    }

    /**
     * Looks for a parameter which has reached the most amplitude in measured values from particular time
     * @param givenNamesOfStations names of particular stations
     * @param startTime start of period where the amplitude will be looked for in format YYYY-MM-DD HH:MM:SS
     * @return full information about an amplitude and applied parametres
     * @throws Exception
     */
    public String findTheMostAmplitude(String[] givenNamesOfStations, String startTime) throws Exception {
        int id;
        Date startDate = getDate(startTime);
        double amplitude = -1;
        String parameter="null";
        String nameOfStationWithAmplitude="null";
        for (int i = 0; i < givenNamesOfStations.length; i++) {
            id = getIdOfGivenStation(givenNamesOfStations[i]);
            Sensor[] sensors = service.getSensors(id);
            for (int j = 0; j < sensors.length; j++) {
                Data data = service.getData(sensors[i].getId());
                double min = Double.MAX_VALUE;
                double max = -1;
                for (int k = 0; k < data.getValues().length; k++) {
                    if (data.getValues()[k].getValue() != 0) {
                        if ((getDate(data.getValues()[k].getDate()).after(startDate) || getDate(data.getValues()[k].getDate()).equals(startDate)) && getDate(data.getValues()[k].getDate()).getDay() == startDate.getDay()) {
                            min = Double.min(min, data.getValues()[k].getValue());
                            max = Double.max(max, data.getValues()[k].getValue());
                        }
                    }
                    if (amplitude < (max - min)) {
                        amplitude = Double.max(amplitude, max - min);
                        parameter = data.getKey();
                        nameOfStationWithAmplitude = givenNamesOfStations[i];
                    }
                }
            }
        }
        return "Parameter with the most amplitude: " + parameter + "\nAmplitude: " + String.valueOf(amplitude) + "\nStation: " + nameOfStationWithAmplitude;
    }

    /**
     * Looks for the smallest value of a parameter of particular station
     * @param nameOfGivenStation name of particular station
     * @param givenDateAndTime particular time in format YYYY-MM-DD HH:MM:SS
     * @return full information about applied parameters the parameter which has reacher the smallest value
     * @throws Exception
     */
    public String findTheSmallestValueOfParameterForParticularStation(String nameOfGivenStation, String givenDateAndTime) throws Exception {
        Date date = getDate(givenDateAndTime);
        int stationId = getIdOfGivenStation(nameOfGivenStation);

        Sensor[] sensors = service.getSensors(stationId);

        double minimalValue = Double.MAX_VALUE;
        String nameOfParameter = "lack of parameter";
        for (int i = 0; i < sensors.length; i++) {
            Data data = service.getData(sensors[i].getId());
            for (int j = 0; j < data.getValues().length; j++) {
                if (date.equals(getDate(data.getValues()[j].getDate()))) {
                    if (data.getValues()[j].getValue() != 0) {
                        if (minimalValue > data.getValues()[j].getValue()) {
                            minimalValue = data.getValues()[j].getValue();
                            nameOfParameter = data.getKey();
                        }
                    }
                }
            }

        }
        return "Station: " + nameOfGivenStation + "\nTime: " + givenDateAndTime + "\nParameter: " + nameOfParameter + "\nMinimal value is: " + String.valueOf(minimalValue);
    }

    /**
     * Looks for the smallest value of parameter at particular time
     * @param givenDateAndTime time in format YYYY-MM-DD HH:MM:SS which we want to receive an information about
     * @return Full information about parameter and the smallest value which it has reached and station where it was measured
     * @throws LackOfDataException
     */
    public String findTheSmallestValueOfParameter(String givenDateAndTime) throws LackOfDataException {
        Date date = getDate(givenDateAndTime);
        Station[] stations =service.getAllStations();
        String nameOfParameter = new String();
        double minimalValue = Double.MAX_VALUE;
        for (int i = 0; i < stations.length; i++) {
            Sensor[] sensors = service.getSensors(stations[i].getId());
            for (int j = 0; j < sensors.length; j++) {
                Data data = service.getData(sensors[j].getId());
                for (int k = 0; k < data.getValues().length; k++) {
                    if (date.equals(getDate(data.getValues()[k].getDate()))) {
                        if (data.getValues()[k].getValue() != 0) {
                            if (minimalValue > data.getValues()[k].getValue()) {
                                minimalValue = data.getValues()[k].getValue();
                                nameOfParameter = data.getKey();
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (nameOfParameter.isEmpty()) {
            throw new LackOfDataException();
        } else
            return "Time: " + givenDateAndTime + "\nParameter: " + nameOfParameter + "\nMinimal value is: " + String.valueOf(minimalValue);
    }

    /**
     * Looks for parametres which has reached the limit value at particular station
     * @param givenNameOfStation name of particular station
     * @param dateAndTime particular time in format YYYY-MM-DD HH:MM:SS
     * @param howMany number of parametres which we want to see
     * @return an information about parameters in descending order considering ratio which have reached the limit
     * @throws Exception
     */
    public String displaySensorsWithTheMostValueOfParameter(String givenNameOfStation, String dateAndTime, int howMany) throws Exception {

        Sensor[] sensors = service.getSensors(getIdOfGivenStation(givenNameOfStation));
        if (howMany < 1) {
            howMany = 1;
        }
        Date givenDate = getDate(dateAndTime);
        Map<Double, String> mapOfSensors = new TreeMap<>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o1.doubleValue() < o2.doubleValue() ? -1 : o1.doubleValue() == o2.doubleValue() ? 0 : 1;
            }
        });
        Map<String, Double> mapOfParameterLimits = new TreeMap<>();
        mapOfParameterLimits.put("NO2", 200.0);
        mapOfParameterLimits.put("PM10", 50.0);
        mapOfParameterLimits.put("SO2", 350.0);
        mapOfParameterLimits.put("C6H6", 5.0);
        mapOfParameterLimits.put("CO", 10000.0);
        mapOfParameterLimits.put("PM2.5", 25.0);

        for (int i = 0; i < sensors.length; i++) {
            int sensorId = sensors[i].getId();
            Data data = service.getData(sensorId);
            for (int j = 0; j < data.getValues().length; j++) {
                if (data.getKey().equals("O3")) {
                    break;
                }
                if (getDate(data.getValues()[j].getDate()).equals(givenDate)) {
                    if (mapOfParameterLimits.get(data.getKey()) <= data.getValues()[j].getValue()) {
                        mapOfSensors.put((data.getValues()[j].getValue()-mapOfParameterLimits.get(data.getKey()))/mapOfParameterLimits.get(data.getKey())*100, data.getKey());
                    }
                }
            }
        }
        if (mapOfSensors.isEmpty()) {
            throw new ParametersUnderLimitException();
        }
        StringBuilder result = new StringBuilder(dateAndTime + " " + "Station: " + givenNameOfStation + "\n");
        int beginner = 0;
        for (Double current : mapOfSensors.keySet()) {
            if (beginner >= mapOfSensors.size() - howMany && beginner < mapOfSensors.size()) {
                result.append(mapOfSensors.get(current) + " " + String.valueOf(Double.valueOf(current)) + "%\n");
                beginner++;
            } else {
                beginner++;
            }
        }
        return result.toString();
    }

    /**
     * Looks for maximal and minimal value of particular parameter considering all stations
     * @param givenNameOfParameter name of particular parameter
     * @return information about maximal and minimal value of parameter
     * @throws Exception
     */
    public String getMaximalAndMinimalValueOfParameter(String givenNameOfParameter) throws Exception {
        Station[] stations = service.getAllStations();
        double min = Double.MAX_VALUE;
        double max = -1;
        String nameOfStationMin = "lack of station";
        String nameOfStationMax = "lack of station";
        String dateMin = "lack of date";
        String dateMax = "lack of date";
        for (int i = 0; i < stations.length; i++) {
            Sensor[] sensors = service.getSensors(stations[i].getId());
            for (int j = 0; j < sensors.length; j++) {
                if (sensors[j].getParam().getParamCode().equalsIgnoreCase(givenNameOfParameter)) {
                    Data data = service.getData(sensors[j].getId());
                    for (int k = 0; k < data.getValues().length; k++) {
                        if (min > data.getValues()[k].getValue() && data.getValues()[k].getValue() != 0) {
                            min = data.getValues()[k].getValue();
                            nameOfStationMin = stations[i].getStationName();
                            dateMin = data.getValues()[k].getDate();
                        }
                        if (max < data.getValues()[k].getValue()) {
                            max = data.getValues()[k].getValue();
                            nameOfStationMax = stations[i].getStationName();
                            dateMax = data.getValues()[k].getDate();
                        }
                    }
                }
            }
        }
        if(nameOfStationMin.equals("lack of station") || nameOfStationMax.equals("lack of station")){
            throw new LackOfParameterException(givenNameOfParameter);
        }
        return new String("Parameter: " + givenNameOfParameter + "\n" + "Minimal value: " + String.valueOf(min) + " Date: " + dateMin + " Station: " + nameOfStationMin + "\nMaximal value: " + String.valueOf(max) + " Date: " + dateMax + " Station: " + nameOfStationMax);
    }

    /**
     * Looks for the maximal value of parameter
     * @param givenNameOfParameter name of particular parameter
     * @return maximal value of parameter
     */
    private double getMaximalValueOfParameter(String givenNameOfParameter) {


        Station[] stations = service.getAllStations();
        double max = -1;
        for (int i = 0; i < stations.length; i++) {
            Sensor[] sensors = service.getSensors(stations[i].getId());
            for (int j = 0; j < sensors.length; j++) {
                if (sensors[j].getParam().getParamCode().equalsIgnoreCase(givenNameOfParameter)) {
                    Data data = service.getData(sensors[j].getId());
                    for (int k = 0; k < data.getValues().length; k++) {
                        if (max < data.getValues()[k].getValue()) {
                            max = data.getValues()[k].getValue();
                        }
                    }
                }
            }
        }
        return max;
    }

    /**
     * Looks for values of particular parameter for particular station and creates a graph on the basis of found data
     * @param givenNameOfParameter name of particular parameter
     * @param givenNamesOfStations names of particular stations
     * @param startDate start of period in format YYYY-MM-DD HH:MM:SS
     * @param endDate end of period in format YYYY-MM-DD HH:MM:SS
     * @param sign graphs' bars will be created by that character
     * @return graph which shows information about changes of particular parameter for particular stations and particular period
     * @throws Exception
     */
    public String displayGraph(String givenNameOfParameter, String[] givenNamesOfStations, String startDate, String endDate, String sign) throws Exception {


        TreeMap<Date, ArrayList<String>> dataSortedByHours = new TreeMap<>();
        Date start = getDate(startDate);
        Date end = getDate(endDate);
        if(start.after(end)){
            throw new IncorrectPeriodException();
        }
        double maxValue = getMaximalValueOfParameter(givenNameOfParameter);
        for (int i = 0; i < givenNamesOfStations.length; i++) {
            Sensor[] sensors = service.getSensors(getIdOfGivenStation(givenNamesOfStations[i]));
            int sensorId = -1;
            for (int j = 0; j < sensors.length; j++) {
                if (sensors[j].getParam().getParamCode().equalsIgnoreCase(givenNameOfParameter)) {
                    sensorId = sensors[j].getId();
                    break;
                }
            }
            if (sensorId == -1) {
                throw new LackOfParameterException(givenNameOfParameter);
            } else {
                Data data = service.getData(sensorId);
                for (int k = 0; k < data.getValues().length; k++) {
                    Date currentDate = getDate(data.getValues()[k].getDate());
                    if ((currentDate.after(start) && currentDate.before(end)) || currentDate.equals(start) || currentDate.equals(end)) {
                        if (data.getValues()[k].getValue() != 0) {
                            addDataToGraph(currentDate, createBar(maxValue, givenNamesOfStations[i], data.getValues()[k].getValue(), sign), dataSortedByHours);
                        }
                    }
                }
            }
        }
        StringBuilder result = new StringBuilder("Parameter: " + givenNameOfParameter + "\n");
        for (Date currentDate : dataSortedByHours.keySet()) {
            ArrayList<String> currentBars = dataSortedByHours.get(currentDate);
            for (int i = 0; i < currentBars.size(); i++) {
                result.append(currentDate.toLocaleString() + " " + currentBars.get(i) + "\n");
            }
        }
        return result.toString();
    }

    private String createBar(double maxValue, String nameOfStation, double currentValue, String sign) {
        double ratio = currentValue / maxValue;
        ratio *= 100;
        StringBuilder bar = new StringBuilder(String.format("%-30s", nameOfStation));
        for (int i = 0; i < ratio; i++) {
            bar.append(sign);
        }
        bar.append(" " + currentValue);
        return bar.toString();
    }

    private void addDataToGraph(Date givenDate, String data, TreeMap<Date, ArrayList<String>> dataSortedByHours) {
        if (dataSortedByHours.containsKey(givenDate)) {
            dataSortedByHours.get(givenDate).add(data);
        } else {
            ArrayList<String> listWithData = new ArrayList<>();
            listWithData.add(data);
            dataSortedByHours.put(givenDate, listWithData);
        }
    }

    private int getIdOfGivenStation(String nameOfGivenStation) throws LackOfStationException {
        Station[] stations = service.getAllStations();
        for (int i = 0; i < stations.length; i++) {
            if (stations[i].getStationName().equalsIgnoreCase(nameOfGivenStation)) {
                return stations[i].getId();
            }
        }
        throw new LackOfStationException(nameOfGivenStation);
    }

    private Date getDate(String dateAndTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = dateFormat.parse(dateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
