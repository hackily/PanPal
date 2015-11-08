package com.unkani.panpal;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class PantryListActivity extends ActionBarActivity implements OnClickListener {
    private Button generator;
    private Button selector;
    private ListView mainListView;
    private ArrayAdapter mArrayAdapter;
    private ArrayList<String> pantryList = new ArrayList<String>();
    private int position;
    private int counter;
    private ArrayList<String> itemList = new ArrayList<String>();
    private Bundle b;

    
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_list);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        selector = (Button) findViewById(R.id.pantry_select);
        selector.setOnClickListener(this);
        generator= (Button) findViewById(R.id.pantry_generator);
        generator.setOnClickListener(this);
        selector.setEnabled(false);

        mainListView = (ListView) findViewById(R.id.pantry_list);
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, itemList);
        mainListView.setAdapter(mArrayAdapter);
        b = new Bundle();
        b = getIntent().getExtras();
        Log.d("Bundle", b.toString());
        if(b != null){
        	Log.d("Debug onstart", "Inside bundle");
    		
			itemList.addAll(b.getStringArrayList("itemList"));

    		mArrayAdapter.notifyDataSetChanged();
        }

        
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            position = pos;
            }
        });
        

	}
	public void onStart(){
		super.onStart();
		Log.d("Debug onstart", "Started");
        
	}
/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pan_pal_main, menu);
        return true;
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        onBackPressed();
        finish();

        return super.onOptionsItemSelected(item);
    }
    
    public void onClick(View v) {
        if (v==generator) {

            itemList.add("somestring"+ counter++);
            mArrayAdapter.notifyDataSetChanged();
            selector.setEnabled(true);
        }
        else if (v==mainListView) {
        }
        else if (v == selector){
        	try{
	            AlertDialog alert = new AlertDialog.Builder(this)
	                    .setTitle("Quest Dialogue")
	                    .setMessage("Are you sure you want to remove "+ itemList.get(mainListView.getCheckedItemPosition()) + "?")
	                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int which) {
	                            //String read = pantryList.get(mainListView.getCheckedItemPosition());
	                            itemList.remove(mainListView.getCheckedItemPosition());
	                            mArrayAdapter.notifyDataSetChanged();
	                            if(itemList.isEmpty()){
	                            	selector.setEnabled(false);
	                            }
	                        }
	                    })
	                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int which) {
	                            // cancel button
	                        }
	                    }).create();
	            alert.show();
        	}
        	catch(Exception e) {
        		Log.e("Exception", e.toString());
        	}

        }
    }
    
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent();
        b = new Bundle();
		b.putStringArrayList("itemList", itemList);
		i.putExtras(b);
		Log.d("Debug in back", "eyy");
		for(int m = 0; m < itemList.size(); m++){
			Log.d("Debug return", itemList.get(m));
		}
		
        setResult(1, i);
        finish();
    }
   
    
}
