package com.dipak.audiorecording;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AudioRecordingActivity extends Activity {
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private static final int RESULT_SETTINGS = 0;

	private MediaRecorder recorder = null;
	private int currentFormat = 0;
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,
			MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4,
			AUDIO_RECORDER_FILE_EXT_3GP };

	private void displayPrefsDialog() {
	      SharedPreferences sharedPrefs = PreferenceManager
	                .getDefaultSharedPreferences(this);
	 
	        StringBuilder str_builder = new StringBuilder();
	 
	        str_builder.append("\n\n Username: "
	                + sharedPrefs.getString("prefUsername", "NULL"));
	 
	        str_builder.append("\n Send report:"
	                + sharedPrefs.getBoolean("prefSendReport", false));
	 
	        str_builder.append("\n Sync Frequency: "
	                + sharedPrefs.getString("prefSyncFrequency", "NULL"));
	        
	        str_builder.append("\n----------\n Start Automaticaly: "
	                + sharedPrefs.getBoolean("prefStartAuto", false));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getString(R.string.choose_format_title))
		        .setMessage (str_builder.toString())
		        .setPositiveButton("OK", 
		        		new DialogInterface.OnClickListener () {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
		        	
		        }
		        		)
				.show();
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case RESULT_SETTINGS:
            displayPrefsDialog();
            break;
 
        }
 
    }
	// Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
 
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
 
        switch (item.getItemId())
        {
//        case R.id.menu_bookmark:
//            Toast.makeText(AudioRecordingActivity.this, "Bookmark is Selected", Toast.LENGTH_SHORT).show();
//            return true;
// 
//        case R.id.menu_save:
//            Toast.makeText(AudioRecordingActivity.this, "Save is Selected", Toast.LENGTH_SHORT).show();
//            return true;
// 
//        case R.id.menu_search:
//            Toast.makeText(AudioRecordingActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
//            return true;
// 
//        case R.id.menu_share:
//            Toast.makeText(AudioRecordingActivity.this, "Share is Selected", Toast.LENGTH_SHORT).show();
//            return true;
// 
//        case R.id.menu_delete:
//            Toast.makeText(AudioRecordingActivity.this, "Delete is Selected", Toast.LENGTH_SHORT).show();
//            return true;
// 
        case R.id.menu_about:
            Toast.makeText(AudioRecordingActivity.this, "About is Selected", Toast.LENGTH_SHORT).show();
            return true;
 
        case R.id.menu_preferences:
            Toast.makeText(AudioRecordingActivity.this, "Preferences is Selected", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, UserSettingActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
            return true;
 
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setButtonHandlers();
		enableButtons(false);
		setFormatButtonCaption();
		
		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
		if (sharedPrefs.getBoolean("prefStartAuto", false)) {
			enableButtons(true);
			startRecording();
		}
		
	}

	private void setButtonHandlers() {
		((Button) findViewById(R.id.btnStart)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnStop)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnFormat)).setOnClickListener(btnClick);
	}

	private void enableButton(int id, boolean isEnable) {
		((Button) findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {
		enableButton(R.id.btnStart, !isRecording);
		enableButton(R.id.btnFormat, !isRecording);
		enableButton(R.id.btnStop, isRecording);
	}

	private void setFormatButtonCaption() {
		((Button) findViewById(R.id.btnFormat))
				.setText(getString(R.string.audio_format) + " ("
						+ file_exts[currentFormat] + ")");
	}

	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
	}

	private void startRecording() {
		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());

		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();

			recorder = null;
		}
	}

	private void displayFormatDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String formats[] = { "MPEG 4", "3GPP" };

		builder.setTitle(getString(R.string.choose_format_title))
				.setSingleChoiceItems(formats, currentFormat,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								currentFormat = which;
								setFormatButtonCaption();

								dialog.dismiss();
							}
						}).show();
	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(AudioRecordingActivity.this,
					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(AudioRecordingActivity.this,
					"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
					.show();
		}
	};

	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnStart: {
				Toast.makeText(AudioRecordingActivity.this, "Start Recording",
						Toast.LENGTH_SHORT).show();

				enableButtons(true);
				startRecording();

				break;
			}
			case R.id.btnStop: {
				Toast.makeText(AudioRecordingActivity.this, "Stop Recording",
						Toast.LENGTH_SHORT).show();
				enableButtons(false);
				stopRecording();

				break;
			}
			case R.id.btnFormat: {
				displayFormatDialog();

				break;
			}
			}
		}
	};
}