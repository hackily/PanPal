package com.unkani.panpal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class PanPalMainActivity extends ActionBarActivity implements OnClickListener{

	private String apiKey = "551dec40bf00d0bdc331ec26af3bd01e";
	private Button scanBtn;
	private Button pantryBtn;
	private TextView contentTxt;
	protected ProgressBar progressBar;
    protected TextView responseView;
    private ArrayList<String> itemList = new ArrayList<String>();
    Bundle b;

	private String combinedURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panpal_main);

        contentTxt = (TextView)findViewById(R.id.scan_content);
        scanBtn = (Button)findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(this);
        pantryBtn = (Button)findViewById(R.id.pantry_button);
        pantryBtn.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        responseView = (TextView) findViewById(R.id.responseView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pan_pal_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onClick(View v){
    	if(v.getId()==R.id.scan_button){
    		IntentIntegrator scanIntegrator = new IntentIntegrator(this);
    		//can choose to scan for types of barcodes.
    		
    		scanIntegrator.initiateScan();
    		//combinedURL = "http://api.upcdatabase.org/json/551dec40bf00d0bdc331ec26af3bd01e/0049000027624";
    		//new RetrieveData().execute();
    	}
    	if(v.getId()==R.id.pantry_button){
    		Intent pantryIntent = new Intent(this, PantryListActivity.class);
    		b = new Bundle();
    		b.putStringArrayList("itemList", itemList);
    		for(int i = 0; i < itemList.size(); i++){
    			Log.d("Debug send to pantry", itemList.get(i));
    		}
    		pantryIntent.putExtras(b);
    		startActivityForResult(pantryIntent, 1);

    		Log.d("Debug", "Pantry button clicked");

    	}
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
    	super.onActivityResult(requestCode, resultCode, intent);
    	if(resultCode == 1){
    		Log.d("Debug in main", "Result 1. Bundle?");
    		b = new Bundle();
    		b = intent.getExtras();
            if(!b.isEmpty()){
                itemList = b.getStringArrayList("itemList");
        		for(int m = 0; m < itemList.size(); m++){
        			Log.d("Debug pantry received", itemList.get(m));
        		}
            }
    	}
    	else{
    		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        	Log.d("debug", resultCode+"");
        	if(scanningResult != null){
        		String scanContent = scanningResult.getContents();
        		String scanFormat = scanningResult.getFormatName();
        		contentTxt.setText("Content: " + scanContent);
        		scanContent = normalizeScan(scanContent);
        		combinedURL = "http://api.upcdatabase.org/json/" + apiKey + "/" + scanContent;
        		new RetrieveData().execute();
        	}
        	else{
        		Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
        		toast.show();
        	}
    	}
    	
    }
    
    public String normalizeScan(String scanContent){
    	if(scanContent != null && scanContent.length() < 13){
			while(scanContent.length() < 13){
				scanContent = "0" + scanContent;
			}
		}
    	return scanContent;
    }
    
    class RetrieveData extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(combinedURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    JSONObject obj = new JSONObject(stringBuilder.toString());
                    Log.d("Debug desc", obj.getString("description"));
                    Log.d("Debug name", obj.getString("itemname"));

                    
                	if(!obj.getString("itemname").isEmpty()){
                		itemList.add(obj.getString("itemname"));
                	}
                	else if(!obj.getString("description").isEmpty()){
                		itemList.add(obj.getString("description"));
                	}
                	else{
                		throw new Exception();
                	}
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(response);
        }
    }
    
}



