/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myRS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author taha-m
 */
@Path("exchangeRate")
public class RateRS {

    /**
     * Creates a new instance of RateRS
     */
    public RateRS() {
    }

    /**
     * Retrieves representation of an instance of myRS.RateRS
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    public String getText(@QueryParam("toCur")String toCur) {
        
        double cRate=0.0;
        
        JSONArray ja = new JSONArray();

        for (CurCodesRS.ExRate exr : CurCodesRS.ExRate.values()) {
            JSONObject job = new JSONObject();
            job.put("name", exr.curName());
            job.put("code", exr.name());
            job.put("rate", exr.rateInUSD());
            ja.put(job);
        }
        
        JSONObject message = new JSONObject();
        
        String tempUrlString = "https://api.exchangerate.host/latest?base=USD";
        try {
            //setup URL connection for HTTP GET
            final String urlString = tempUrlString;
            
            URL url = new URL(urlString);
            HttpURLConnection connURL = (HttpURLConnection) url.openConnection();
            connURL.setRequestMethod("GET");
            //connURL.setRequestProperty("Content-Type", "application/json");
            connURL.connect();
            
            //read response direcgly into a String buffer
            BufferedReader ins = new BufferedReader(new InputStreamReader(connURL.getInputStream()));
            String inString;
            StringBuilder sb = new StringBuilder();
            while ((inString = ins.readLine()) != null) {
                sb.append(inString);
            }
            //make sure you close the stream and the connection
            ins.close();
            connURL.disconnect();

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONObject object = jsonObject.getJSONObject("rates");

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jaObject = ja.getJSONObject(i);

                Iterator<String> keys = object.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    if(key.equals(jaObject.getString("code")))
                        ja.getJSONObject(i).put("rate" , object.get(key));
                }
            }
            
            JSONObject returnObject = new JSONObject();
            
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jaObject = ja.getJSONObject(i);
                if(toCur.equals(jaObject.get("code")))
                    returnObject = jaObject;
            }
        

            returnObject.put("lastupdated" ,new Date().toString());
        
            return returnObject.toString();
           
        } catch (MalformedURLException me) {
            return new JSONObject("{\"message\":\"MalformedURLException"+me.toString()+"\"}").toString();
        } catch (IOException ioe) {
            return new JSONObject("{\"message\":\"IOException"+ioe.toString()+"\"}").toString();
        }
        
        
    }

    @PUT
    @Consumes("text/plain")
    public void putText(String content) {
    }
    
        
        public enum ExRate {
        AED ("UAE Dirham", 3.672476),
        ARS ("Argentine Peso", 102.759851),
        AUD ("Australian Dollar", 1.392364),
        BGN ("Bulgarian Lev", 1.72431),
        BRL ("Brazilian Real", 5.634533),
        BWP ("Botswana Pula", 11.601793),
        CAD ("Canadian Dollar", 1.264828),
        CHF ("Swiss Franc", 0.918823),
        CLP ("Chilean Peso", 829.675792),
        CNY ("Chinese Yuan", 6.376409),
        COP ("Colombian Peso", 4016.115006),
        DKK ("Danish Krone", 6.550998),
        EEK ("Estonian kroon", 14.2337),
        EGP ("Egypt Pounds", 15.722619),
        EUR ("Euro", 0.881118),
        GBP ("British pound", 0.735844),
        HKD ("Hong Kong Dollar", 7.796139),
        HRK ("Croatian Kuna", 6.619026),
        HUF ("Hungarian Forint", 316.005504),
        ILS ("Israeli New Shekel", 3.113796),
        INR ("Indian Rupee", 74.236522),
        ISK ("Iceland Krona", 128.487558),
        JPY ("Japanese Yen", 115.618604),
        KRW ("South Korean Won", 1197.316338),
        KZT ("Kazakhstani Tenge", 433.195991),
        LKR ("Sri Lanka Rupee", 201.824814),
        LTL ("Lithuanian Litas", 3.14932),
        LVL ("Latvian Lat", 0.63786115),
        LYD ("Libyan Dinar", 4.580031),
        MXN ("Mexican Peso", 20.383287),
        MYR ("Malaysian Ringgit", 4.208613),
        NOK ("Norwegian Kroner", 8.840235),
        NPR ("Nepalese Rupee", 118.276327),
        NZD ("New Zealand Dollar", 1.476705),
        OMR ("Omani Rial", 0.385146),
        PKR ("Pakistan Rupee", 175.938785),
        QAR ("Qatari Rial", 3.637354),
        RON ("Romanian Leu", 4.355012),
        RUB ("Russian Ruble", 75.523685),
        SAR ("Saudi Riyal", 3.753795),
        SDG ("Sudanese Pound",437.382663),
        SEK ("Swedish Krona", 9.063068),
        SGD ("Singapore Dollar", 1.35687),
        THB ("Thai Baht", 33.613439),
        TRY ("Turkish Lira", 13.792124),
        TTD ("Trinidad/Tobago Dollar", 6.752436),
        TWD ("Taiwan Dollar", 27.611834),
        UAH ("Ukrainian hryvnia", 27.353673),
        USD ("United States Dollar", 1),
        VED ("Venezuelan Bolivar", 426343),
        ZAR ("South African Rand", 15.573683);

        private final double rateInUSD;
        private final String curName;
        ExRate(String curName,double rateInUSD) {
            this.rateInUSD = rateInUSD;
            this.curName = curName;
        }
        double rateInUSD()   { return rateInUSD; }
        String curName()   { return curName; }
    }

}
