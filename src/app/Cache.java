package app;

import com.google.gson.Gson;

import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;

/**
 * Involves map which informs us if data which is included in web page had been got earlier so that we do not have to get request
 */

public class Cache {
    public static long CACHE_RETENTION = 10*60*1000;
    private HashMap<String, CacheEntry> entries = new HashMap<>();
    private static Gson gson = new Gson();
    private String cacheDir;


    static class CacheEntry {
        String url;
        Long timestamp;
        String value;

        public CacheEntry() {}

        /**
         *
         * @param url exact url address
         * @param timestamp time in miliseconds when CacheEntry is created
         * @param value a content of page with a given url address
         */
        public CacheEntry(String url, Long timestamp, String value) {
            this.url = url;
            this.timestamp = timestamp;
            this.value = value;
        }
    }

    /**
     * Checks if files included in directory are valid when it comes to a date and adds them to map
     * @param cacheDir is a name of directory
     */

    public Cache(String cacheDir) {
        this.cacheDir = cacheDir;
        File folder = new File(cacheDir);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                try(Reader reader = new FileReader(cacheDir+"/"+listOfFiles[i].getName())) {
                    CacheEntry entry = gson.fromJson(reader, CacheEntry.class);
                    if(!isValid(entry.timestamp)) {
                        listOfFiles[i].delete();
                        continue;
                    }
                    entries.put(entry.url, entry);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Checks if file is not too old to take data from it
     * @param time time in miliseconds when file was created
     * @return true if file was created recently
     */
    public boolean isValid(long time) {
        return System.currentTimeMillis() - time < CACHE_RETENTION && (int)(System.currentTimeMillis()/(1000*60*60)) == (int)(time/(1000*60*60));
    }

    /**
     * Returns content of page which was recently processed and is saved in local files
     * @param key url address of page which has to be processed
     * @return content of given page or null if page was not processed recently
     */
    public Optional<String> get(String key) {
        if(this.entries.containsKey(key)) {
            CacheEntry cached = entries.get(key);
            if(isValid(cached.timestamp)) {
                return Optional.of(cached.value);
            }
        }
        return Optional.empty();
    }

    /**
     * Creates an entry which contains url address of particular page, current time in miliseconds and content of that page
     * and adds it to map
     * @param key url address of particular web page
     * @param value content of a particular web page
     */
    public void put(String key, String value) {
        CacheEntry entry = new CacheEntry(key, System.currentTimeMillis(), value);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(cacheDir+"/"+key.hashCode()+".json"))) {
            String raw = gson.toJson(entry);
            bw.write(raw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        entries.put(key, entry);
    }
}
