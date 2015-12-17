//ANDY CULLEN
//ASSIGNMENT 4
//DUE 12/16/15

package edu.kvcc.cis298.cis298assignment4;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class BeverageFetcher {
/**
 * Created by Andy on 12/15/2015.
 */

    //SET CONSTANTS FOR THE SERVER ADDRESS AND THE CONNECTION TIMEOUT LENGTH (30sec)
    public static final String SERVER_ADDRESS = "http://barnesbrothers.homeserver.com/beverageapi";
    public static final int CONNECTION_TIMEOUT = 30000;

    ProgressDialog mProgressDialog; //DECLARE THE DIALOG

    //constructor
    public BeverageFetcher(Context context) {

        //ROB DEMONSTRATED PROGRESS DIALOGS IN CLASS (THESE ARE ALSO USED IN OUR TEAM PROJECT):
        mProgressDialog = new ProgressDialog(context);

        //SET DIALOG VALUES
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("Connecting to server");
        mProgressDialog.setMessage("Please wait...");
    }

    public void fetchBeveragesAsyncTask(Callbacks callback) {
        mProgressDialog.show();
        new FetchBeveragesAsyncTask(callback).execute();
    }

    //THIS CLASS GETS LIST OF BEVERAGESS FROM SERVER
    private class FetchBeveragesAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private BeverageCollection mBeverageCollection;
        private Callbacks mCallback;

        //constructor
        public FetchBeveragesAsyncTask(Callbacks callback) {
            mBeverageCollection = BeverageCollection.get();
            mCallback = callback;
        }

        //HTTP REQUESTS THAT GET LIST FROM SERVER
        protected Boolean doInBackground(Void... voids) {

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

            HttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpGet httpGet = new HttpGet(SERVER_ADDRESS);

            try {
                //TRY TO GET A HTTP RESPONSE
                HttpResponse httpResponse = httpClient.execute(httpGet);

                //GET THE JSON STRING AND PARSE IT INTO A JSON ARRAY
                HttpEntity httpEntity = httpResponse.getEntity();
                String resultString = EntityUtils.toString(httpEntity);
                JSONArray jArray = new JSONArray(resultString);

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);

                    //SET FIELD VALUES FOR EACH OBJECT BY READING FROM STRING
                    boolean isActive;

                    String id = jObject.getString("id");
                    String name = jObject.getString("name");
                    String pack = jObject.getString("pack");
                    Double price = Double.parseDouble(jObject.getString("price"));
                    int active = Integer.parseInt(jObject.getString("isActive"));

                    switch (active) {
                        case 0:
                            isActive = false;
                            break;
                        default:
                            isActive = true;
                            break;
                    }

                    //CREATE AND ADD NEW BEVERAGE
                    Beverage newBeverage = new Beverage(id, name, pack, price, isActive);
                    mBeverageCollection.addBeverage(newBeverage);
                }
            } catch (Exception e) { //CATCH EXCEPTIONS
                e.printStackTrace();
                return false;
            }

            return true; //SUCCESS
        }

        @Override   //CLOSE DIALOG, CALLBACK
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            mProgressDialog.dismiss();
            mCallback.beverageCallback(bool);
        }
    }
}
