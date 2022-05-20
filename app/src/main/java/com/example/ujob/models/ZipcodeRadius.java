package com.example.ujob.models;

import com.example.ujob.utilities.ZipcodesApiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

//Used to gather an array of zipcodes provided a given zipcode and radius
public class ZipcodeRadius {
    private final String zipcode;
    private final int radius;


    public ZipcodeRadius(String zipcode, int radius) {
        this.zipcode = zipcode;
        this.radius = radius;
    }

    public void getZipcodes(ZipcodesApiCallback zipcodesApiCallback) {
        ArrayList<String> zipcodes = new ArrayList<>();
        new Thread(() -> {

            try {
                URL url = new URL(String.format("https://zipcodedistance.herokuapp.com/api/getRadius?zipcode=%s&limit=%s&unit=M", zipcode, radius));

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                //Make sure response code is good
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    throw new RuntimeException("HttpsResponseCode: " + responseCode);
                }
                //Grab the response as a StringBuilder
                StringBuilder siteString = new StringBuilder();
                Scanner sc = new Scanner(url.openStream());
                while (sc.hasNext())
                    siteString.append(sc.nextLine());
                sc.close();

                //Convert and read Json
                JSONObject json = new JSONObject(siteString.toString());
                if (!json.isNull("data")) {
                    zipcodes.add(zipcode);
                    JSONArray jsonArray = new JSONArray(json.getString("data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.get(i) != null) {
                            JSONObject zip = new JSONObject(String.valueOf(jsonArray.get(i)));
                            zipcodes.add(zip.getString("zipcode"));
                        }
                    }
                }
                else
                    zipcodes.add(zipcode);
                zipcodesApiCallback.onZipcodeData(zipcodes);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
