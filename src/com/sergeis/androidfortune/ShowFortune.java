package com.sergeis.androidfortune;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowFortune extends Activity {
	
	private static final int SETTINGS_ID = Menu.FIRST;
	private static final String CURR_TEXT = "CurrentText";
    
	private TextView mTextView;
	private FortuneFileReader mFortuneFileReader;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTextView = (TextView)findViewById(R.id.fortuneText);
        mFortuneFileReader = new FortuneFileReader();
        try {
        	mFortuneFileReader.init(this);
        	if (savedInstanceState != null)
        		mTextView.setText((String)savedInstanceState.getSerializable(CURR_TEXT));
        	else
        		mTextView.setText(mFortuneFileReader.getNextFortune());
        }
        catch (IOException ioe)
        {
        	mTextView.setText("Couldn't read any fortunes!");
        }
        
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        
        Button confirmButton = (Button)findViewById(R.id.nextFortune);
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	try {
					mTextView.setText(mFortuneFileReader.getNextFortune());
				} catch (IOException ioe) {
					mTextView.setText("Couldn't read any fortunes!");
				}
            }

        });
    }
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		menu.add(0, SETTINGS_ID, 0, R.string.settings_menu_item);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SETTINGS_ID:
			{
				// TODO
			}
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(CURR_TEXT, mTextView.getText().toString());
	}
	
}
