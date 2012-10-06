package com.timer.timer_frag;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

public class RunClockFragment extends Fragment {

	final static String ARG_POSITION = "position";
	int mCurrentPosition = -1;
	OnTimerSelectedListener mCallback;

	private static MyCountDownTimer CountDownTimer;
	private static boolean TimerHasStarted = false;
	private static boolean TimerHasPaused = false;
	private static boolean RestHasStarted = false;
	private static boolean RestHasPaused = false;
	private static boolean startTimer = false;
	private static boolean startRest = false;
	private static boolean TimerSoundIsOn = true;
	private static ArrayList<String> TimeListArray;

	private static int TimeHours;
	private static int TimeMinutes;
	private static int TimeSeconds;
	private static int RestSeconds;
	private static int SetSetRemaining;
	private static int SetRepRemaining;
	private static int SetSetBase;
	private static int SetRepBase;
	private static int TimesToRun;

	private static long StartTime;
	private static int Interval;
	private static long TimeElapsed;
	private static long TimeRemaining;
	private static long RestRemaining;
	private static TextView TvTextName;
	private static TextView TvTextTime;
	private static TextView TvTextSet;
	private static TextView TvTextRep;

	private static final String FormatString = "H:mm:ss";
	private static final SimpleDateFormat Formatter = new SimpleDateFormat(
			FormatString);
	private static Button BtnPause;
	private static Button BtnStart;
	private static Button BtnReset;
	private static Button BtnSound;
	private static Button BtnSoundUp;
	private static Button BtnSoundDown;
	private static String Name;

	static RunClockFragment newInstance(int num) {
		RunClockFragment f = new RunClockFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt(ARG_POSITION, num);
		f.setArguments(args);

		return f;
	}

	// Used to talk to the Main Activity
	public interface OnTimerSelectedListener {
		public void onTimerSelected(int position);

		public void onButtonClicked(View v);

		public void setRCFArgs(boolean Paused, boolean Started,
				boolean SoundIsOn, long Remaining, int Set, int Rep,
				int TimesToRun, boolean RestHasStarted, boolean RestHasPaused,
				long RestRemaining, String TvTextSet, String TvTextRep);

		public boolean getTimerHasPaused();

		public boolean getTimerHasStarted();

		public boolean getTimerSoundIsOn();

		public long getTimeRemaining();

		public int getSetSetRemaining();

		public int getSetRepRemaining();

		public int getTimesToRun();

		public boolean getRestHasStarted();

		public boolean getRestHasPaused();

		public long getRestRemaining();

		public String getTvTextSet();

		public String getTvTextRep();

		public MediaPlayer getMpFinished();

		public MediaPlayer getMpRested();

		public ArrayList<String> getTimeListNameArray();

		public ArrayList<ArrayList<String>> getTimeListArray();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnTimerSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTimerSelectedListener");
		}
	}

	// Inflate with the layout xml file 'run_clock_fragment'
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.run_clock_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		Bundle args = getArguments();
		if (args != null) {
			updateTimerView(args.getInt(ARG_POSITION));
		} else if (mCurrentPosition != -1) {
			updateTimerView(0);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		TimerHasPaused = mCallback.getTimerHasPaused();
		TimerHasStarted = mCallback.getTimerHasStarted();
		TimerSoundIsOn = mCallback.getTimerSoundIsOn();
		TimeRemaining = mCallback.getTimeRemaining();
		SetSetRemaining = mCallback.getSetSetRemaining();
		SetRepRemaining = mCallback.getSetRepRemaining();
		TimesToRun = mCallback.getTimesToRun();
		RestHasStarted = mCallback.getRestHasStarted();
		RestHasPaused = mCallback.getRestHasPaused();
		RestRemaining = mCallback.getRestRemaining();
		TvTextSet.setText(mCallback.getTvTextSet());
		TvTextRep.setText(mCallback.getTvTextRep());

		if (!TimerHasStarted && !TimerHasPaused && !RestHasStarted
				&& !RestHasPaused) {
			// Was in RESET mode
			resetChronometer();
		} else if (TimerHasStarted && !TimerHasPaused && !RestHasStarted
				&& !RestHasPaused) {
			// Was in TIMER RUNNING mode
			TvTextTime.setTextColor(Color.GREEN);
			TvTextTime.setText("" + Formatter.format(TimeRemaining));
			CountDownTimer.cancel();
			CountDownTimer = new MyCountDownTimer(TimeRemaining, Interval);
			CountDownTimer.start();
			BtnStart.setEnabled(false);
			BtnPause.setEnabled(true);
			BtnReset.setEnabled(true);
		} else if (!TimerHasStarted && !TimerHasPaused && RestHasStarted
				&& !RestHasPaused) {
			// Was in REST RUNNING mode
			TvTextTime.setTextColor(Color.CYAN);
			TvTextTime.setText("" + Formatter.format(RestRemaining));
			CountDownTimer.cancel();
			CountDownTimer = new MyCountDownTimer(RestRemaining, Interval);
			CountDownTimer.start();
			BtnStart.setEnabled(false);
			BtnPause.setEnabled(true);
			BtnReset.setEnabled(true);
		} else if (TimerHasStarted && TimerHasPaused && !RestHasStarted
				&& !RestHasPaused) {
			// Was in TIMER PAUSED mode
			TimeElapsed = StartTime - TimeRemaining;
			pauseChronometer();
		} else if (!TimerHasStarted && !TimerHasPaused && RestHasStarted
				&& RestHasPaused) {
			// Was in REST PAUSED mode
			TimeElapsed = StartTime - RestRemaining;
			pauseChronometer();
		}
		if (!TimerSoundIsOn) {
			BtnSound.setBackgroundResource(R.drawable.btn_sound_off);
		}
	}

	public void updateTimerView(int position) {
		mCurrentPosition = position;
		TvTextName = (TextView) getActivity().findViewById(
				R.id.textNameRunClock);

		TvTextTime = (TextView) getActivity().findViewById(
				R.id.textTimeRunClock);

		TvTextSet = (TextView) getActivity().findViewById(
				R.id.textCountSetRunClock);
		TvTextRep = (TextView) getActivity().findViewById(
				R.id.textCountRepRunClock);
		BtnPause = (Button) getActivity().findViewById(R.id.buttonPause);
		BtnStart = (Button) getActivity().findViewById(R.id.buttonStart);
		BtnReset = (Button) getActivity().findViewById(R.id.buttonReset);
		BtnSound = (Button) getActivity().findViewById(R.id.buttonSound);
		BtnSoundUp = (Button) getActivity().findViewById(R.id.buttonSoundUp);
		BtnSoundDown = (Button) getActivity()
				.findViewById(R.id.buttonSoundDown);
		BtnStart.setEnabled(true);
		BtnPause.setEnabled(false);
		BtnReset.setEnabled(false);
		BtnSound.setEnabled(true);
		BtnSound.setBackgroundResource(R.drawable.btn_sound);
		BtnSoundUp.setEnabled(true);
		BtnSoundUp.setBackgroundResource(R.drawable.btn_sound_up);
		BtnSoundDown.setEnabled(true);
		BtnSoundDown.setBackgroundResource(R.drawable.btn_sound_down);

		TimeListArray = mCallback.getTimeListArray().get(mCurrentPosition);

		Formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Interval = 10;
		TimeElapsed = 0;
		Name = (mCurrentPosition + 1) + ". " + TimeListArray.get(0);
		TimeHours = Integer.parseInt(TimeListArray.get(1));
		TimeMinutes = Integer.parseInt(TimeListArray.get(2));
		TimeSeconds = Integer.parseInt(TimeListArray.get(3));
		StartTime = (long) (TimeHours * 60 * 60 * 1000);
		StartTime += (long) (TimeMinutes * 60 * 1000);
		StartTime += (long) (TimeSeconds * 1000);
		TimeRemaining = StartTime;
		CountDownTimer = new MyCountDownTimer(StartTime, Interval);

		SetSetBase = Integer.parseInt(TimeListArray.get(4));
		SetRepBase = Integer.parseInt(TimeListArray.get(5));
		SetSetRemaining = Integer.parseInt(TimeListArray.get(4));
		SetRepRemaining = Integer.parseInt(TimeListArray.get(5));

		String[] restStrArr = TimeListArray.get(6).split(" ");
		if (restStrArr[1].equals("sec")) {
			RestSeconds = (int) Integer.parseInt(restStrArr[0]);
		} else {
			RestSeconds = 0;
		}

		setTimerName();
		setTimerTime();
		setTimerCount();
		resetTextTimeBG();
	}

	public void setTimerName() {
		TvTextName.setText(Name);
	}

	public void setTimerTime() {
		TvTextTime.setText("" + Formatter.format(StartTime));
	}

	public void resetTextTimeBG() {
		LayerDrawable d = (LayerDrawable) TvTextTime.getBackground();
		Drawable d0 = d.getDrawable(0);
		Drawable d1 = d.getDrawable(1);
		Drawable d2 = d.getDrawable(2);
		d0.setBounds(0, 0, 0, 0);
		d1.setBounds(0, 0, 0, 0);
		d2.setBounds(0, 0, TvTextTime.getWidth(), TvTextTime.getHeight());
	}

	public void setTimerCount() {
		int i = SetSetBase - SetSetRemaining + 1;
		TvTextSet.setText("" + i + " of " + SetSetBase);
		i = SetRepBase - SetRepRemaining + 1;
		TvTextRep.setText("" + i + " of " + SetRepBase);
	}

	public void resetTimerCount() {
		// Used by MainActivity
		SetSetBase = Integer.parseInt(TimeListArray.get(4));
		SetRepBase = Integer.parseInt(TimeListArray.get(5));
		SetSetRemaining = Integer.parseInt(TimeListArray.get(4));
		SetRepRemaining = Integer.parseInt(TimeListArray.get(5));
		setTimerCount();
	}

	public void decreaseTimerCount() {
		if (SetRepRemaining < 2) {
			SetRepRemaining = Integer.parseInt(TimeListArray.get(5));
			if (SetSetRemaining < 2) {
				SetSetRemaining = Integer.parseInt(TimeListArray.get(4));
			} else {
				SetSetRemaining--;
			}
		} else {
			SetRepRemaining--;
		}
	}

	public void startChronometer() {
		if (!TimerHasStarted && !TimerHasPaused && !RestHasStarted
				&& !RestHasPaused) {
			// Was in RESET mode
			// Start button, Timer clicked, onFinish()
			if (startTimer) {
				// Not first run. From onfinish()
				setTimerCount();
				TvTextTime.setTextColor(Color.GREEN);
				TvTextTime.setText("" + Formatter.format(StartTime));
				TimerHasStarted = true;
				RestHasStarted = false;
				CountDownTimer.cancel();
				CountDownTimer = new MyCountDownTimer(StartTime, Interval);
			} else if (startRest) {
				// Not first run. From onfinish()
				TvTextTime.setTextColor(Color.CYAN);
				TvTextTime.setText("" + Formatter.format(RestSeconds * 1000));
				TimerHasStarted = false;
				RestHasStarted = true;
				CountDownTimer.cancel();
				CountDownTimer = new MyCountDownTimer(RestSeconds * 1000,
						Interval);
			} else {
				// First time run
				setTimerCount();
				TimesToRun = Integer.parseInt(TimeListArray.get(4))
						* Integer.parseInt(TimeListArray.get(5));
				TvTextTime.setTextColor(Color.GREEN);
				TvTextTime.setText("" + Formatter.format(StartTime));
				TimerHasStarted = true;
				RestHasStarted = false;
				CountDownTimer.cancel();
				CountDownTimer = new MyCountDownTimer(StartTime, Interval);
			}
			CountDownTimer.start();
			BtnStart.setEnabled(false);
			BtnPause.setEnabled(true);
			BtnReset.setEnabled(true);
			TimerHasPaused = false;
			RestHasPaused = false;
			startTimer = false;
			startRest = false;
		} else if (TimerHasStarted && !TimerHasPaused && !RestHasStarted
				&& !RestHasPaused) {
			// Was in TIMER RUNNING mode
			// Timer clicked
			pauseChronometer();
		} else if (!TimerHasStarted && !TimerHasPaused && RestHasStarted
				&& !RestHasPaused) {
			// Was in REST RUNNING mode
			// Timer clicked
			pauseChronometer();
		} else if (TimerHasStarted && TimerHasPaused && !RestHasStarted
				&& !RestHasPaused) {
			// Was in TIMER PAUSED mode
			// Timer clicked, Start button
			TvTextTime.setTextColor(Color.GREEN);
			TvTextTime.setText("" + Formatter.format(TimeRemaining));
			CountDownTimer.cancel();
			CountDownTimer = new MyCountDownTimer(TimeRemaining, Interval);
			CountDownTimer.start();
			BtnStart.setEnabled(false);
			BtnPause.setEnabled(true);
			BtnReset.setEnabled(true);
			TimerHasPaused = false;
		} else if (!TimerHasStarted && !TimerHasPaused && RestHasStarted
				&& RestHasPaused) {
			// Was in REST PAUSED mode
			// Timer clicked, Start button
			TvTextTime.setTextColor(Color.CYAN);
			TvTextTime.setText("" + Formatter.format(RestRemaining));
			CountDownTimer.cancel();
			CountDownTimer = new MyCountDownTimer(RestRemaining, Interval);
			CountDownTimer.start();
			BtnStart.setEnabled(false);
			BtnPause.setEnabled(true);
			BtnReset.setEnabled(true);
			RestHasPaused = false;
		}
	}

	public void pauseChronometer() {
		BtnPause.setEnabled(false);
		BtnStart.setEnabled(true);
		BtnReset.setEnabled(true);
		CountDownTimer.cancel();
		TvTextTime.setTextColor(Color.YELLOW);
		TimeRemaining = StartTime - TimeElapsed;
		RestRemaining = StartTime - TimeElapsed;
		if (TimerHasStarted) {
			TimerHasStarted = true;
			TimerHasPaused = true;
			RestHasStarted = false;
			RestHasPaused = false;
			TimeElapsed = 0;
			TvTextTime.setText("" + Formatter.format(TimeRemaining));
		} else if (RestHasStarted) {
			TimerHasStarted = false;
			TimerHasPaused = false;
			RestHasStarted = true;
			RestHasPaused = true;
			TimeElapsed = 0;
			TvTextTime.setText("" + Formatter.format(RestRemaining));
		}
	}

	public void resetChronometer() {
		clearChronometer();
		resetTimerCount();
		resetTextTimeBG();
	}

	public void clearChronometer() {
		CountDownTimer.cancel();
		TimerHasStarted = false;
		TimerHasPaused = false;
		RestHasStarted = false;
		RestHasPaused = false;
		startTimer = false;
		startRest = false;
		TimeRemaining = StartTime;
		RestRemaining = RestSeconds * 1000;
		TimeElapsed = 0;
		TvTextTime.setTextColor(Color.WHITE);
		TvTextTime.setText("" + Formatter.format(StartTime));
		BtnStart.setEnabled(true);
		BtnPause.setEnabled(false);
		BtnReset.setEnabled(false);
	}

	public void changeSoundOnOff() {
		if (TimerSoundIsOn) {
			TimerSoundIsOn = false;
			BtnSound.setBackgroundResource(R.drawable.btn_sound_off);
		} else {
			TimerSoundIsOn = true;
			BtnSound.setBackgroundResource(R.drawable.btn_sound);
		}
	}

	// CountDownTimer class
	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			resetTextTimeBG();
			if (RestHasStarted) {
				// Came from rest
				clearChronometer();
				if (TimerSoundIsOn) {
					mCallback.getMpRested().start();
				}
				// Will run again
				startTimer = true;
				startChronometer();
			} else {
				// Came from regular timer
				if (TimerSoundIsOn) {
					mCallback.getMpFinished().start();
				}
				clearChronometer();
				TimesToRun--;
				decreaseTimerCount();
				// setTimerCount();
				if (TimesToRun > 0) {
					// Run Again
					if (RestSeconds > 0) {
						// Need to rest
						startRest = true;

						startChronometer();
					} else {
						// No Rest
						startTimer = true;

						startChronometer();
					}
				}
			}
		}

		@Override
		public void onTick(long millSecsLeft) {
			TvTextTime.setText("" + Formatter.format(millSecsLeft));
			TimeElapsed = StartTime - millSecsLeft;
			TimeRemaining = StartTime - TimeElapsed;
			RestRemaining = StartTime - TimeElapsed;

			int w = TvTextTime.getWidth();
			int h = TvTextTime.getHeight();
			double percent = 0;
			if (TimerHasStarted && !RestHasStarted) {
				percent = (double) TimeRemaining / (double) StartTime;
			} else if (!TimerHasStarted && RestHasStarted) {
				percent = (double) RestRemaining
						/ (double) (RestSeconds * 1000);
			}

			int wper = (int) (w * percent);

			LayerDrawable d = (LayerDrawable) TvTextTime.getBackground();
			Drawable d0 = d.getDrawable(0);
			Drawable d1 = d.getDrawable(1);
			Drawable d2 = d.getDrawable(2);
			d0.setBounds(0, 0, w - wper, h);
			d1.setBounds(w - wper, 0, w, h);
			d2.setBounds(0, 0, 0, 0);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		mCallback.setRCFArgs(TimerHasPaused, TimerHasStarted, TimerSoundIsOn,
				TimeRemaining, SetSetRemaining, SetRepRemaining, TimesToRun,
				RestHasStarted, RestHasPaused, RestRemaining,
				(String) TvTextSet.getText(), (String) TvTextRep.getText());
		resetChronometer();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mCallback.setRCFArgs(TimerHasPaused, TimerHasStarted, TimerSoundIsOn,
				TimeRemaining, SetSetRemaining, SetRepRemaining, TimesToRun,
				RestHasStarted, RestHasPaused, RestRemaining,
				(String) TvTextSet.getText(), (String) TvTextRep.getText());
		resetChronometer();
	}
}
