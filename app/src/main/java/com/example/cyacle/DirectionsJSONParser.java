package com.example.cyacle;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser {
    public List<List<HashMap<String, String>>> parse(JSONObject jObject)
    {
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        String polyline;
        try
        {
            jRoutes = jObject.getJSONArray("routes");
            List<HashMap<String, String>> path;
            if (jRoutes != null && jRoutes.length() > 0)
            {
                for (int i = 0; i < jRoutes.length(); i++)
                {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    path = new ArrayList<>();
                    if (jLegs != null && jLegs.length() > 0)
                    {
                        for (int j = 0; j < jLegs.length(); j++)
                        {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                            if (jSteps != null && jSteps.length() > 0)
                            {
                                for (int k = 0; k < jSteps.length(); k++)
                                {
                                    polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                    List<LatLng> list = null;
                                    if (polyline != null)
                                    {
                                        list = decodePoly(polyline);
                                    }

                                    /* Traversing all points */
                                    if (list != null && list.size() > 0)
                                    {
                                        for (int l = 0; l < list.size(); l++)
                                        {
                                            HashMap<String, String> hm = new HashMap<>();
                                            hm.put("lat", Double.toString(list.get(l).latitude));
                                            hm.put("lng", Double.toString(list.get(l).longitude));
                                            path.add(hm);
                                        }
                                    }
                                }
                            }
                            routes.add(path);
                        }
                    }
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return routes;
    }

    private List<LatLng> decodePoly(String encoded)
    {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len)
        {
            int b, shift = 0, result = 0;
            do
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
