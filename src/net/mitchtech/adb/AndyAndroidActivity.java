package net.mitchtech.adb;

import java.io.IOException;

import net.mitchtech.adb.andyandroid.R;

import org.microbridge.server.Server;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class AndyAndroidActivity extends Activity {
	private final String TAG = AndyAndroidActivity.class.getSimpleName();

	private final byte PIN_OFF = 0x0;
	private final byte PIN_ON = 0x1;

	private ToggleButton mLed1Toggle;
	private ToggleButton mLed2Toggle;

	private SeekBar mServoBar;

	private OnCheckedChangeListener mStateChangeListener = new StateChangeListener();
	private OnSeekBarChangeListener mSeekBarChangeListener = new SeekBarChangeListener();

	Server mServer = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mLed1Toggle = (ToggleButton) findViewById(R.id.ToggleButton1);
		mLed1Toggle.setTag((byte) 0x3);
		mLed1Toggle.setOnCheckedChangeListener(mStateChangeListener);

		mLed2Toggle = (ToggleButton) findViewById(R.id.ToggleButton2);
		mLed2Toggle.setTag((byte) 0x4);
		mLed2Toggle.setOnCheckedChangeListener(mStateChangeListener);

		mServoBar = (SeekBar) findViewById(R.id.SeekBarServo1);
		mServoBar.setTag((byte) 0x5);
		mServoBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

		// Create new TCP Server
		try {
			mServer = new Server(4567);
			mServer.start();
		} catch (IOException e) {
			Log.e(TAG, "Unable to start TCP server", e);
			System.exit(-1);
		}
	}

	private class StateChangeListener implements OnCheckedChangeListener {

		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			byte portByte = (Byte) buttonView.getTag();
			if (isChecked) {
				try {
					mServer.send(new byte[] { portByte, PIN_ON });
				} catch (IOException e) {
					Log.e(TAG, "problem sending TCP message", e);
				}
			} else {
				try {
					mServer.send(new byte[] { portByte, PIN_OFF });
				} catch (IOException e) {
					Log.e(TAG, "problem sending TCP message", e);
				}
			}
		}
	}

	private class SeekBarChangeListener implements OnSeekBarChangeListener {

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			byte portByte = (Byte) seekBar.getTag();
			try {
				mServer.send(new byte[] { portByte, (byte) progress });
			} catch (IOException e) {
				Log.e(TAG, "problem sending TCP message", e);
			}
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	}

}