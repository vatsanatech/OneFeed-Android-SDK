package com.wittyfeed.sdk.onefeed;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * Manages the cached data as saved by DataStoreManager, and performs following actions -
 *      1) check whether active cache is available and return true if available
 *      2) fetch cache and return if available,
 *
 * Read about: DataStoreManager, DataStore, DataParser
 */

final class DataStoreCacheManager {

    static boolean checkIfCacheAvailable(Context context){
        File fileSize = new File(context.getCacheDir(), "OneFeedCache.json");
        long fileSizeInKB = fileSize.length() / 1024;
        return fileSizeInKB > 5;
    }

    static String readCachedJSON(Context context){
        try {
            File fileRead = null;
            BufferedReader input = null;

            fileRead = new File(context.getCacheDir(), "OneFeedCache.json"); // Pass getFilesDir() and "MyFile" to read file

            input = new BufferedReader(new InputStreamReader(new FileInputStream(fileRead)));
            String line;
            StringBuilder buffer = new StringBuilder();

            while ((line = input.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        }
        catch (Exception e) {
            OFLogger.log(OFLogger.DEBUG ,OFLogger.CacheLoadError);
        }
        return "";
    }

    static void createCachedJSON(String stringToCache, Context context) {
        /*http://codetheory.in/android-saving-files-on-internal-and-external-storage/*/
        File file;
        FileOutputStream outputStream;
        try {
            file = new File(context.getCacheDir(), "OneFeedCache.json");
            outputStream = new FileOutputStream(file);
            outputStream.write(stringToCache.getBytes());
            outputStream.close();
            OFLogger.log(OFLogger.DEBUG ,OFLogger.CacheRefreshSuccess);
        } catch (IOException e) {
            OFLogger.log(OFLogger.ERROR ,OFLogger.CacheRefreshERROR, e);
        }
    }

}
