package app;

import app.Cache;
import com.google.gson.Gson;
import models.AirConditionIndex;
import models.Data;
import models.Sensor;
import models.Station;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * Created to get information from web pages and transform it into objects, tailored to every service which uses json
 */
public abstract class Service {
    protected static Gson gson = new Gson();

    private Cache cache = new Cache("./cache");

    /**
     * Gets a content of particular web page as string
     * @param address url address of particular web page
     * @return content of given web page
     * @throws IOException
     */
    public String getStringFromURLAddress(String address) throws IOException {
        URL url = new URL(address);
        Optional<String> cached = cache.get(address);
        if(cached.isPresent()) {
            return cached.get();
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder json;
        String value = new String();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            json = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                json.append(line);
            }
            bufferedReader.close();
            value = json.toString();
            cache.put(address, value);
        } catch (UnknownHostException e){
            System.out.println(e.getMessage() + ": there is no connection with the Internet.");
        }

        return value;
    }

    /**
     * Gets an information from a web page and puts it into an object
     * @param url url address of particular web page
     * @param mapTo
     * @param <T> a class which we want to create
     * @return an instance of class with information from given web page
     * @throws RuntimeException
     */
    public <T> T processRequest(String url, Class<T> mapTo) throws RuntimeException {
        String str = null;
        try {
            str = getStringFromURLAddress(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gson.fromJson(str, mapTo);
    }

    public abstract Station[] getAllStations();

    public abstract Sensor[] getSensors(int stationId);

    public abstract Data getData(int sensorId);

    public abstract AirConditionIndex getIndex(int stationId);
}
