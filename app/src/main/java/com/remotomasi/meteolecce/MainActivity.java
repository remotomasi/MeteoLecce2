package com.remotomasi.meteolecce;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    final String metcheck = "http://ws1.metcheck.com/ENGINE/v9_0/json.asp?lat=40.45&lon=18.1&lid=22553&Fc=No"; // metcheck json site
    TextView txtDate = null, txtTemp = null, txtPress = null, txtHum = null, txtWPow = null, txtClouds = null,
            txtPhenomen = null;
    String temp = null, press = null, hum = null, wPow = null, wDir = null, clouds = null, phenomenon = null, phNext = null,
            dp = null, dateJson = null, hourJson = null;
            //txtWDir = null
    Double fogVis = 0.0;
    Date date = Calendar.getInstance().getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss"); // hh:mm:ss
    SimpleDateFormat sdfm = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfmN = new SimpleDateFormat("dd/MM/yyyy");
    String today = sdf.format(date);    // data odierna
    long ltime = date.getTime(); // - 1*60*60*1000; // subtract an hour or two hours for UTC time
    String today1 = sdf.format(ltime);
    String todaym = sdfm.format(ltime);
    String todaymN = sdfmN.format(date);
    ImageView imgIco = null, imgFog = null, imgWindDir = null; //, imgNext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new readWeatherLecce().execute();

    }

    /**
     * Called when the user taps the Update button
     */
    public void update(View view) {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class readWeatherLecce extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            txtDate = findViewById(R.id.textView7);
            txtTemp = findViewById(R.id.textView2);
            txtHum = findViewById(R.id.textView5);
            txtWPow = findViewById(R.id.textView3);
            //txtWDir = findViewById(R.id.textView4);
            imgIco = findViewById(R.id.imageViewIcon);
            imgFog = findViewById(R.id.imageViewFog);
            imgWindDir = findViewById(R.id.imageViewWDir);

            /*
            txtClouds = (TextView) findViewById(R.id.textView13);
            txtPhenomen = (TextView) findViewById(R.id.textView14);
            imgNext = (ImageView) findViewById(R.id.imageView7);
            */

            String str = "";
            HttpResponse response;
            DefaultHttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(metcheck);

            try {
                response = myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            str = str.replace("} {", "}, {");

            try {
                dateJson = null;
                JSONObject json = new JSONObject(str);

                //for (int i = 0; i < 1; i++) {
                int i = 1;
                    dateJson = json.getJSONObject("metcheckData").getJSONObject("forecastLocation").getJSONArray("forecast").getJSONObject(i).getString("utcTime").substring(0, 10);
                    hourJson = json.getJSONObject("metcheckData").getJSONObject("forecastLocation").getJSONArray("forecast").getJSONObject(i).getString("utcTime").substring(11, 13);
                    Log.i(">>>>>>> 4.5", todaym.substring(0, 10) + " " + (today1.substring(11,13)) + " " + hourJson);

                    //if (todaym.substring(0, 10).equals(dateJson) && (today1.substring(11,13).equals(hourJson))) {
                        //Log.i("VALORI: ", today1.substring(11,13).concat(" ").concat(hourJson).concat(" ").concat(dateJson).concat(" ").concat(today1).concat(" ").concat(json.getJSONObject("metcheckData").getJSONObject("forecastLocation").getJSONArray("forecast").getJSONObject(i).getString("iconName")));
                        Log.i(">>>>>>> 5", todaym.substring(0, 10));
                        temp = json.getJSONObject("metcheckData").getJSONObject("forecastLocation").getJSONArray("forecast").getJSONObject(i).getString("temperature");
                        hum = json.getJSONObject("metcheckData").getJSONObject("forecastLocation").getJSONArray("forecast").getJSONObject(i).getString("humidity");
                        wPow = json.getJSONObject("metcheckData").getJSONObject("forecastLocation").getJSONArray("forecast").getJSONObject(i).getString("windspeed");
                        wDir = json.getJSONObject("metcheckData").getJSONObject("forecastLocation").getJSONArray("forecast").getJSONObject(i).getString("winddirection");
                        phenomenon = json.getJSONObject("metcheckData").getJSONObject("forecastLocation").getJSONArray("forecast").getJSONObject(i).getString("iconName");
                        Log.i("DIRECTION: ", json.getJSONObject("metcheckData").getJSONObject("forecastLocation").getJSONArray("forecast").getJSONObject(i).getString("iconName"));
                        dp = json.getJSONObject("metcheckData").getJSONObject("forecastLocation").getJSONArray("forecast").getJSONObject(i).getString("dewpoint");
                        fogVis = Double.parseDouble(temp) - Double.parseDouble(dp);
                    //}
                //}
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Double tt = null;
            Double wp = null;
            if (temp!= null) tt = Double.parseDouble(temp);
            if (wPow != null) {
                wp = Double.parseDouble(wPow);
                wp = wp / 1.619; // miglia // m/s * 3.6;
            }

            txtDate.setText(new StringBuilder().append(getResources().getString(R.string.lastUpdate)).append(today1.substring(11, 16)).toString());

            txtTemp.setText(String.format("%.1f", tt).concat(" °C"));
            if (hum != null) {
                txtHum.setText(hum.concat("%"));
                txtHum.setBackgroundColor(humidityColor(Integer.parseInt(hum)));
            }
            txtWPow.setText(String.format("%.1f", wp).concat(" Km/h"));
            if (wDir != null) {
                //txtWDir.setText(windDirection((int) Double.parseDouble(wDir)));
                imgWindDir.setVisibility(View.VISIBLE);
                imgWindDir.setImageResource(windDirection((int) Double.parseDouble(wDir)));
            }

            if (phenomenon != null) {
                imgIco.setVisibility(View.VISIBLE);
                //txtPhenomen.setText(skyConversion(phenomenon));
                skyIcon(phenomenon, imgIco);


                if (fogVis < 2.5
                        && Integer.parseInt(hum) > 70 && (!phenomenon.equals("Rain")
                        && !phenomenon.equals("Intermittent Rain") && !phenomenon.equals("Drizzle")
                        && !phenomenon.equals("Light Rain") && !phenomenon.equals("Showers")
                        && !phenomenon.equals("Rain Showers") && !phenomenon.equals("Heavy Rain")
                        && !phenomenon.equals("Thunderstorm")))
                    imgFog.setVisibility(View.VISIBLE);
                else imgFog.setVisibility(View.INVISIBLE);
            } else {
                imgIco.setVisibility(View.INVISIBLE);
            }

            Button button = findViewById(R.id.button);
            if (button.getVisibility() == View.INVISIBLE) button.setVisibility(View.VISIBLE);
            /*
            if (phNext != null) {
                imgNext.setVisibility(View.VISIBLE);
                skyIcon(phNext, imgNext);
            } else {
                imgNext.setVisibility(View.INVISIBLE);
            }
            */

            super.onPostExecute(result);
        }

        /**
         * Convert degree in a human comprehensible thing
         */
        public int humidityColor(int value) {

            if (value <= 50) {
                return Color.WHITE;
            } else if (value > 50 && value <= 70) {
                return Color.YELLOW;
            } else if (value > 80) {
                return Color.RED;
            } else
                return 1;
        }


        /**
         * Convert degree in a human comprehensible thing
         */
        public int windDirection(int deg) {
            String dir = null;
            int idir = 0;

            if (deg > 335 || deg <= 25) {
                dir = "Nord";
                idir = R.drawable.n;
            } else if (deg > 25 && deg <= 65) {
                dir = "Nord-Est";
                idir = R.drawable.ne;
            } else if (deg > 65 && deg <= 155) {
                dir = "Est";
                idir = R.drawable.e;
            } else if (deg > 115 && deg <= 155) {
                dir = "Sud-Est";
                idir = R.drawable.se;
            } else if (deg > 155 && deg <= 205) {
                dir = "Sud";
                idir = R.drawable.s;
            } else if (deg > 205 && deg <= 245) {
                dir = "Sud-Ovest";
                idir = R.drawable.sw;
            } else if (deg > 245 && deg <= 295) {
                dir = "Ovest";
                idir = R.drawable.w;
            } else if (deg > 295 && deg <= 335) {
                dir = "Nord-Ovest";
                idir = R.drawable.nw;
            }

            return idir;
        }

        /**
         * Translation weather strings
         */
        public String skyConversion(String value) {
            String sky = null;

            switch (value) {
                case "Sunny":
                    sky = "Sereno";
                    break;
                case "Fair":
                    sky = "Poche nubi";
                    break;
                case "Partly Cloudy":
                    sky = "Parzialmente nuvoloso";
                    break;
                case "Mostly Cloudy":
                    sky = "Molto nuvoloso";
                    break;
                case "Cloudy":
                    sky = "Nuvoloso";
                    break;
                case "Mist":
                    sky = "nebbia";
                    break;
                case "Intermittent Rain":
                    sky = "Pioggia intermittente";
                    break;
                case "Drizzle":
                    sky = "Pioggerella";
                    break;
                case "Light Rain":
                    sky = "Pioggia leggera";
                    break;
                case "Showers":
                    sky = "Pioggia";
                    break;
                case "Rain Showers":
                    sky = "Rovescio";
                    break;
                case "Heavy Rain":
                    sky = "Pioggia forte";
                    break;
                case "Light Snow":
                    sky = "Neve leggera";
                    break;
                case "Light Sleet":
                    sky = "Nevischio";
                    break;
                case "Heavy Snow":
                    sky = "Forte nevicata";
                    break;
                case "Heavy Sleet":
                    sky = "Pesante nevischio";
                    break;
                case "Thunderstorms":
                    sky = "Temporale";
                    break;
                case "Wet & Windy":
                    sky = "Umido e ventoso";
                    break;
                case "Hail":
                    sky = "Grandine";
                    break;
                case "Snow Showers":
                    sky = "Scarica di neve";
                    break;
                case "Dry & Windy":
                    sky = "Secco e ventoso";
                    break;
            }
            return sky;
        }

        /**
         * Convert string to an icon
         */
        private void skyIcon(String value, ImageView imgV) {

            Date date = Calendar.getInstance().getTime();
            long htime = date.getTime();
            String hsunset = sdf.format(htime).substring(11, 13);

            switch (value) {
                case "Sunny":
                    if (Integer.parseInt(hsunset) < 18 && Integer.parseInt(hsunset) > 6) {
                        imgV.setImageResource(R.drawable.sun);
                    }else {
                        imgV.setImageResource(R.drawable.moon);
                    }
                    break;
                case "Fair":
                    if (Integer.parseInt(hsunset) < 18 && Integer.parseInt(hsunset) > 6) {
                        imgV.setImageResource(R.drawable.few_clouds);
                    }else {
                        imgV.setImageResource(R.drawable.moon_fewclouds);
                    }
                    break;
                case "Partly Cloudy":
                    if (Integer.parseInt(hsunset) < 18 && Integer.parseInt(hsunset) > 6) {
                        imgV.setImageResource(R.drawable.partly_cloudy);
                    }else {
                        imgV.setImageResource(R.drawable.moon_cloudy);
                    }
                    break;
                case "Cloudy":
                    imgV.setImageResource(R.drawable.cloudy);
                    break;
                case "Light Rain":
                    imgV.setImageResource(R.drawable.light_rain);
                    break;
                case "Intermittent Rain":
                    imgV.setImageResource(R.drawable.light_rain);
                    break;
                case "Showers":
                    imgV.setImageResource(R.drawable.rain);
                    break;
                case "Drizzle":
                    imgV.setImageResource(R.drawable.light_rain);
                    break;
                case "Rain Showers":
                    imgV.setImageResource(R.drawable.heavy_rain);
                    break;
                case "Thunderstorms":
                    imgV.setImageResource(R.drawable.thunderstorm);
                    break;
                case "Light Sleet":
                    imgV.setImageResource(R.drawable.sleet);
                    break;
                case "Light Snow":
                    imgV.setImageResource(R.drawable.sleet);
                    break;
                case "Heavy Snow":
                    imgV.setImageResource(R.drawable.snow);
                    break;
                case "Heavy Sleet":
                    imgV.setImageResource(R.drawable.snow);
                    break;
                case "Mist":
                    imgV.setImageResource(R.drawable.fog);
                    break;
            }
        }

    }

}
