package com.example.trafficcontrol.custom;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.trafficcontrol.utils.GenericUtility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;

public final class Geocoder_custom
{

    private static final String PREFERENCES_GEOCODER = Geocoder_custom.class.getName()
            + ".GEOCODER";
    private static final String KEY_ALLOW = Geocoder_custom.class.getName()
            + ".KEY_ALLOW";

    /*
     * Status codes which we handle
     */

    /**
     * Indicates that no errors occurred; the address was successfully parsed
     * and at least one geocode was returned.
     */
    private static final String STATUS_OK = "OK";

    /**
     * Indicates that you are over your quota.
     */
    private static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";

    private final Context context;

    /**
     * Constructs a Geocoder whose responses will be localized for the default
     * system Locale.
     * 
     * @param context
     *            the Context of the calling Activity
     */
    public Geocoder_custom(Context context)
    {
        this.context = context;
    }

    /**
     * Returns an array of Addresses that are known to describe the area
     * immediately surrounding the given latitude and longitude. The returned
     * addresses will be localized for the locale provided to this class's
     * constructor.
     * 
     * <p>
     * The returned values may be obtained by means of a network lookup. The
     * results are a best guess and are not guaranteed to be meaningful or
     * correct. It may be useful to call this method from a thread separate from
     * your primary UI thread.
     * 
     * @param latitude
     *            the latitude a point for the search
     * @param longitude
     *            the longitude a point for the search
     * @param maxResults
     *            max number of addresses to return. Smaller numbers (1 to 5)
     *            are recommended
     * 
     * @return a list of Address objects. Returns null or empty list if no
     *         matches were found or there is no backend service available.
     * 
     * @throws IllegalArgumentException
     *             if latitude is less than -90 or greater than 90
     * @throws IllegalArgumentException
     *             if longitude is less than -180 or greater than 180
     * @throws IOException
     *             if the network is unavailable or any other I/O problem occurs
     */
    public List<Address> getFromLocation(double latitude, double longitude,
            int maxResults) throws IOException, LimitExceededException
    {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("latitude == " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("longitude == " + longitude);
        }

        if (isLimitExceeded(context)) {
            throw new LimitExceededException();
        }

        final List<Address> results = new ArrayList<Address>();

        final StringBuilder url = new StringBuilder(
                "http://maps.googleapis.com/maps/api/geocode/json?sensor=true&latlng=");
        url.append(latitude);
        url.append(',');
        url.append(longitude);
        url.append("&language=");
        url.append(Locale.getDefault().getLanguage());

        final byte[] data = GenericUtility.download(url.toString());
        if (data != null) {
            this.parseJson(results, maxResults, data);
        }
        return results;
    }

    /**
     * Returns an array of Addresses that are known to describe the named
     * location, which may be a place name such as "Dalvik,
     * Iceland", an address such as "1600 Amphitheatre Parkway, Mountain View,
     * CA", an airport code such as "SFO", etc.. The returned addresses will be
     * localized for the locale provided to this class's constructor.
     * 
     * <p>
     * The query will block and returned values will be obtained by means of a
     * network lookup. The results are a best guess and are not guaranteed to be
     * meaningful or correct. It may be useful to call this method from a thread
     * separate from your primary UI thread.
     * 
     * @param locationName
     *            a user-supplied description of a location
     * @param maxResults
     *            max number of results to return. Smaller numbers (1 to 5) are
     *            recommended
     * 
     * @return a list of Address objects. Returns null or empty list if no
     *         matches were found or there is no backend service available.
     * 
     * @throws IllegalArgumentException
     *             if locationName is null
     * @throws IOException
     *             if the network is unavailable or any other I/O problem occurs
     */
    public List<Address> getFromLocationName(String locationName, int maxResults)
            throws IOException, LimitExceededException
    {
        if (locationName == null) {
            throw new IllegalArgumentException("locationName == null");
        }

        if (isLimitExceeded(context)) {
            throw new LimitExceededException();
        }

        final List<Address> results = new ArrayList<Address>();

        final StringBuilder request = new StringBuilder(
                "http://maps.googleapis.com/maps/api/geocode/json?sensor=false");
        request.append("&language=").append(Locale.getDefault().getLanguage());
        request.append("&address=").append(
                URLEncoder.encode(locationName, "UTF-8"));

        byte[] data = GenericUtility.download(request.toString());
        if (data != null) {
            try {
                this.parseJson(results, maxResults, data);
            } catch (LimitExceededException e) {
                //LimitExceededException could be thrown if too many calls per second
                //If after two seconds, it is thrown again - then it means there are too much calls per 24 hours
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    return results;
                }
                data = GenericUtility.download(request.toString());
                if (data != null) {
                    try {
                        this.parseJson(results, maxResults, data);
                    } catch (LimitExceededException lee) {
                        // available in 24 hours
                        setAllowedDate(context, System.currentTimeMillis() + 86400000L);
                        throw lee;
                    }
                }
            }
        }
        return results;
    }

    private void parseJson(List<Address> address, int maxResults, byte[] data)
            throws LimitExceededException
    {
        try {
            final String json = new String(data, "UTF-8");
            final JSONObject o = new JSONObject(json);
            final String status = o.getString("status");
            if (status.equals(STATUS_OK)) {

                final JSONArray a = o.getJSONArray("results");

                for (int i = 0; i < maxResults && i < a.length(); i++) {
                    final Address current = new Address(Locale.getDefault());
                    final JSONObject item = a.getJSONObject(i);

                    current.setFeatureName(item.getString("formatted_address"));
                    final JSONObject location = item.getJSONObject("geometry")
                            .getJSONObject("location");
                    current.setLatitude(location.getDouble("lat"));
                    current.setLongitude(location.getDouble("lng"));

                    address.add(current);
                }

            } else if (status.equals(STATUS_OVER_QUERY_LIMIT)) {

                throw new LimitExceededException();

            }
        } 
        catch (LimitExceededException e) 
        {
            throw e;
        } 
        catch (Exception  e) 
        {
        }

    }

    /**
     * Returns true if limit is exceeded and next query is not allowed
     * 
     * @param context
     *            Current context
     * @return true if limit is exceeded and next query is not allowed; false
     *         otherwise
     */
    private static boolean isLimitExceeded(Context context)
    {
        return System.currentTimeMillis() <= getAllowedDate(context);
    }

    /**
     * Sets date after which next geocoding query is allowed
     * 
     * @param context
     *            Current context
     * @param date
     *            the date after which next geocoding query is allowed
     */
    private static void setAllowedDate(Context context, long date)
    {
        final SharedPreferences p = context.getSharedPreferences(
                PREFERENCES_GEOCODER, Context.MODE_PRIVATE);
        final Editor e = p.edit();
        e.putLong(KEY_ALLOW, date);
        e.commit();
    }

    /**
     * Returns date after which the next geocoding query is allowed
     * 
     * @param context
     *            Current context
     * @return date after which the next geocoding query is allowed
     */
    private static long getAllowedDate(Context context)
    {
        final SharedPreferences p = context.getSharedPreferences(
                PREFERENCES_GEOCODER, Context.MODE_PRIVATE);
        return p.getLong(KEY_ALLOW, 0);
    }

    /**
     * Is thrown when the query was over limit before 24 hours
     */
    public static final class LimitExceededException
            extends Exception
    {
        private static final long serialVersionUID = -1243645207607944474L;
    }

}
