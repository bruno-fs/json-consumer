package eu.jsonconsumer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/**
 program built based on this tutorial
 http://www.androidhive.info/2012/01/android-json-parsing-tutorial/

 */




public class MainActivity extends ListActivity {

    // URL to get the JSON
    private static String url = "https://s3-sa-east-1.amazonaws.com/pontotel-docs/data.json";


    // JSON Node names
    private static final String DATA = "data";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PWD = "pwd";

    /**
     readUrl (http://stackoverflow.com/a/7467629)
     */
    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Calling async task to get json
        new GetData().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetData extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> dataList;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                dataList = ParseJSON(readUrl(url));

            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, dataList,
                    R.layout.list_item,
                    new String[]{
                            ID,
                            NAME,
                            PWD},
                    new int[]{
                            R.id.id,
                            R.id.name,
                            R.id.pwd})
                    ;

            setListAdapter(adapter);
        }

    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

                JSONObject jsonObj = new JSONObject(json);

                // Getting JSON Array node
                JSONArray dataArray = jsonObj.getJSONArray(DATA);

                // looping through all keys
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject c = dataArray.getJSONObject(i);

                    String id = c.getString(ID);
                    String name = c.getString(NAME);
                    String pwd = c.getString(PWD);

                    // tmp hashmap for single item
                    HashMap<String, String> item = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    item.put(ID, "ID: " + id);
                    item.put(NAME, "Name: " + name);
                    item.put(PWD, "Pwd: " + pwd);

                    // adding item to data list
                    dataList.add(item);
                }
                return dataList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
            return null;
        }
    }

}
