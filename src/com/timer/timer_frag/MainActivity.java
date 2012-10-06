package com.timer.timer_frag;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		TimerListFragment.OnTimerSelectedListener,
		RunClockFragment.OnTimerSelectedListener,
		RunExtraFragment.OnTimerSelectedListener,
		EditClockFragment.OnTimerSelectedListener,
		EditExtraFragment.OnTimerSelectedListener,
		AppOptionsFragment.OnOptionsUpdateListener {

	public ArrayList<ArrayList<String>> TimeListArray = new ArrayList<ArrayList<String>>();
	public ArrayList<String> TimeListNameArray = new ArrayList<String>();
	final static String FILENAME = "timeListFile";
	final static String EXTFILENAME = "extTimeListFile.txt";
	final static String OPTIONSFILENAME = "optionsFile";
	final static String EXTOPTIONSFILENAME = "extOptionsFile.txt";
	final static int DIALOG_DELETE_ID = 0;
	static boolean isOnePager = false;

	final static String ARG_POSITION = "position";
	final static String ARG_PAGE = "page";
	static int mCurrentPosition = 0;
	static int mCurrentPage = 0;
	// Device = 0, Landscape = 1, Portrait = 2
	static int mOrientationSetting = 0;
	// no sound=0, dingling=1, deskbell=2, ding=3, doorbell=4, startrek=5
	static int mFinishSoundIndex = 4;
	static int mRestSoundIndex = 5;
	static int mCopyIndex = 0;

	// From RunClockFragment
	final static String ARG_PAUSED = "paused";
	final static String ARG_STARTED = "started";
	final static String ARG_SOUND = "sound";
	final static String ARG_REMAINING = "remaining";
	public static boolean mTimerHasPaused_RCF = false;
	public static boolean mTimerHasStarted_RCF = false;
	public static boolean mTimerSoundIsOn_RCF = true;
	public static boolean mRestHasStarted_RCF = false;
	public static boolean mRestHasPaused_RCF = false;
	public static long mTimeRemaining_RCF = 0;
	public static int mSetSetRemaining_RFC = 0;
	public static int mSetRepRemaining_RFC = 0;
	public static int mTimesToRun_RFC = 0;
	public static long mRestRemaining_RFC = 0;
	public static String mTvTextSet_RFC = "";
	public static String mTvTextRep_RFC = "";
	private AudioManager MpAudio;
	private MediaPlayer MpFinished;
	private MediaPlayer MpRested;

	// ////////////////////////////////////////////////////
	static final int NUM_ITEMS_ALL = 6;
	static MyAdapterAll mAdapterAll;
	static ViewPager mPagerAll;
	static final int NUM_ITEMS_LIST = 1;
	static MyAdapterList mAdapterList;
	static ViewPager mPagerList;
	static final int NUM_ITEMS_RUN = 5;
	static MyAdapterRun mAdapterRun;
	static ViewPager mPagerRun;

	// ////////////////////////////////////////////////////

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		populateTimeListArray();
		populateOptions();

		if (findViewById(R.id.onePager) != null) {
			isOnePager = true;

			mAdapterAll = new MyAdapterAll(getSupportFragmentManager());
			mAdapterAll.mAllFragments.clear();
			mAdapterAll.mAllFragments.add(TimerListFragment
					.newInstance(mCurrentPosition));
			mAdapterAll.mAllFragments.add(RunClockFragment
					.newInstance(mCurrentPosition));
			mAdapterAll.mAllFragments.add(RunExtraFragment
					.newInstance(mCurrentPosition));
			mAdapterAll.mAllFragments.add(EditClockFragment
					.newInstance(mCurrentPosition));
			mAdapterAll.mAllFragments.add(EditExtraFragment
					.newInstance(mCurrentPosition));
			mAdapterAll.mAllFragments.add(AppOptionsFragment.newInstance(
					mOrientationSetting, mFinishSoundIndex, mRestSoundIndex));
			mPagerAll = (ViewPager) super.findViewById(R.id.onePager);
			mPagerAll.setAdapter(mAdapterAll);
			mPagerAll.setOffscreenPageLimit(NUM_ITEMS_ALL);
			mPagerAll.setCurrentItem(mCurrentPage);
			mPagerAll.setOnPageChangeListener(new MyPageChangeListener());

		} else if (findViewById(R.id.leftPager) != null) {
			isOnePager = false;

			mAdapterList = new MyAdapterList(getSupportFragmentManager());
			mAdapterList.mListFragments.clear();
			mAdapterList.mListFragments.add(0,
					TimerListFragment.newInstance(mCurrentPosition));
			mPagerList = (ViewPager) super.findViewById(R.id.leftPager);
			mPagerList.setAdapter(mAdapterList);
			mPagerList.setOffscreenPageLimit(NUM_ITEMS_LIST);

			mAdapterRun = new MyAdapterRun(getSupportFragmentManager());
			mAdapterRun.mRunFragments.clear();
			mAdapterRun.mRunFragments.add(RunClockFragment
					.newInstance(mCurrentPosition));
			mAdapterRun.mRunFragments.add(RunExtraFragment
					.newInstance(mCurrentPosition));
			mAdapterRun.mRunFragments.add(EditClockFragment
					.newInstance(mCurrentPosition));
			mAdapterRun.mRunFragments.add(EditExtraFragment
					.newInstance(mCurrentPosition));
			mAdapterRun.mRunFragments.add(AppOptionsFragment.newInstance(
					mOrientationSetting, mFinishSoundIndex, mRestSoundIndex));
			mPagerRun = (ViewPager) super.findViewById(R.id.rightPager);
			mPagerRun.setAdapter(mAdapterRun);
			mPagerRun.setOffscreenPageLimit(NUM_ITEMS_RUN);
			mPagerRun.setCurrentItem(mCurrentPage);
			mPagerRun.setOnPageChangeListener(new MyPageChangeListener());
		}

		setSounds();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public static class MyAdapterAll extends FragmentPagerAdapter {
		private final ArrayList<Fragment> mAllFragments = new ArrayList<Fragment>();

		public MyAdapterAll(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS_ALL;
		}

		@Override
		public Fragment getItem(int position) {
			return mAllFragments.get(position);
		}
	}

	public static class MyAdapterList extends FragmentPagerAdapter {
		private final ArrayList<Fragment> mListFragments = new ArrayList<Fragment>();

		public MyAdapterList(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS_LIST;
		}

		@Override
		public Fragment getItem(int position) {
			return mListFragments.get(position);
		}
	}

	public static class MyAdapterRun extends FragmentPagerAdapter {
		private final ArrayList<Fragment> mRunFragments = new ArrayList<Fragment>();

		public MyAdapterRun(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS_RUN;
		}

		@Override
		public Fragment getItem(int position) {
			return mRunFragments.get(position);
		}
	}

	public void setOrientation() {
		int id = 0;
		switch (mOrientationSetting) {
		case 0:
			id = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
			break;
		case 1:
			id = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			break;
		case 2:
			id = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			break;
		}
		setRequestedOrientation(id);
	}

	public void setSounds() {
		MpAudio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		MpFinished = MediaPlayer.create(getBaseContext(), R.raw.doorbell);
		MpRested = MediaPlayer.create(getBaseContext(), R.raw.dingling);
		if (mFinishSoundIndex > 0) {
			int id = 0;
			switch (mFinishSoundIndex) {
			case 1:
				id = R.raw.dingling;
				break;
			case 2:
				id = R.raw.deskbell;
				break;
			case 3:
				id = R.raw.ding;
				break;
			case 4:
				id = R.raw.doorbell;
				break;
			case 5:
				id = R.raw.startrek;
				break;
			}
			// setDataFromResource(getResources(), MpFinished, id);
			MpFinished = MediaPlayer.create(getBaseContext(), id);
		}

		if (mRestSoundIndex > 0) {
			int id = 0;
			switch (mRestSoundIndex) {
			case 1:
				id = R.raw.dingling;
				break;
			case 2:
				id = R.raw.deskbell;
				break;
			case 3:
				id = R.raw.ding;
				break;
			case 4:
				id = R.raw.doorbell;
				break;
			case 5:
				id = R.raw.startrek;
				break;
			}
			MpRested = MediaPlayer.create(getBaseContext(), id);
		}
	}

	public void onTimerSelected(int timerIndex) {
		mCurrentPosition = timerIndex;
		TimerListFragment newTimerListFragment;
		RunClockFragment newRunClockFragment;
		RunExtraFragment newRunExtraFragment;
		EditClockFragment newEditClockFragment;
		EditExtraFragment newEditExtraFragment;

		if (isOnePager) {
			newTimerListFragment = (TimerListFragment) mAdapterAll.getItem(0);
			newTimerListFragment.updateTimerView(timerIndex);
			newRunClockFragment = (RunClockFragment) mAdapterAll.getItem(1);
			newRunClockFragment.resetChronometer();
			newRunClockFragment.updateTimerView(timerIndex);
			newRunExtraFragment = (RunExtraFragment) mAdapterAll.getItem(2);
			newRunExtraFragment.updateTimerView(timerIndex);
			newEditClockFragment = (EditClockFragment) mAdapterAll.getItem(3);
			newEditClockFragment.updateTimerView(timerIndex);
			newEditExtraFragment = (EditExtraFragment) mAdapterAll.getItem(4);
			newEditExtraFragment.updateTimerView(timerIndex);
			mPagerAll.setCurrentItem(1);
		} else {
			newTimerListFragment = (TimerListFragment) mAdapterList.getItem(0);
			newTimerListFragment.updateTimerView(timerIndex);
			newRunClockFragment = (RunClockFragment) mAdapterRun.getItem(0);
			newRunClockFragment.resetChronometer();
			newRunClockFragment.updateTimerView(timerIndex);
			newRunExtraFragment = (RunExtraFragment) mAdapterRun.getItem(1);
			newRunExtraFragment.updateTimerView(timerIndex);
			newEditClockFragment = (EditClockFragment) mAdapterRun.getItem(2);
			newEditClockFragment.updateTimerView(timerIndex);
			newEditExtraFragment = (EditExtraFragment) mAdapterRun.getItem(3);
			newEditExtraFragment.updateTimerView(timerIndex);
		}
	}

	public void onButtonClicked(View v) {
		TimerListFragment newTimerListFragment;
		RunClockFragment newRunClockFragment;
		EditClockFragment newEditClockFragment;
		EditExtraFragment newEditExtraFragment;
		AppOptionsFragment newAppOptionsFragment;
		if (isOnePager) {
			newTimerListFragment = (TimerListFragment) mAdapterAll.getItem(0);
			newRunClockFragment = (RunClockFragment) mAdapterAll.getItem(1);
			newEditClockFragment = (EditClockFragment) mAdapterAll.getItem(3);
			newEditExtraFragment = (EditExtraFragment) mAdapterAll.getItem(4);
			newAppOptionsFragment = (AppOptionsFragment) mAdapterAll.getItem(5);
		} else {
			newTimerListFragment = (TimerListFragment) mAdapterList.getItem(0);
			newRunClockFragment = (RunClockFragment) mAdapterRun.getItem(0);
			newEditClockFragment = (EditClockFragment) mAdapterRun.getItem(2);
			newEditExtraFragment = (EditExtraFragment) mAdapterRun.getItem(3);
			newAppOptionsFragment = (AppOptionsFragment) mAdapterRun.getItem(4);
		}

		switch (v.getId()) {
		case R.id.buttonNew:
			newRunClockFragment.resetChronometer();
			newRunClockFragment.resetTimerCount();
			addNewTimer();
			newTimerListFragment.getArrayAdapter().notifyDataSetChanged();
			// Toast.makeText(getApplicationContext(), "buttonNew",
			// Toast.LENGTH_LONG).show();
			break;
		case R.id.buttonReload:
			reloadBaseList();
			newTimerListFragment.getArrayAdapter().notifyDataSetChanged();
			reloadOptions();
			newAppOptionsFragment.updateOptions();
			onTimerSelected(mCurrentPosition);
			break;
		case R.id.buttonPause:
			newRunClockFragment.pauseChronometer();
			break;
		case R.id.buttonReset:
			newRunClockFragment.resetChronometer();
			newRunClockFragment.resetTimerCount();
			break;
		case R.id.buttonStart:
			newRunClockFragment.startChronometer();
			break;
		case R.id.textTimeRunClock:
			newRunClockFragment.startChronometer();
			break;
		case R.id.buttonSound:
			newRunClockFragment.changeSoundOnOff();
			break;
		case R.id.buttonSoundUp:
			volumeUp();
			break;
		case R.id.buttonSoundDown:
			volumeDown();
			break;
		case R.id.buttonSaveEditClock:
			saveEditClock();
			newTimerListFragment.getArrayAdapter().notifyDataSetChanged();
			onTimerSelected(mCurrentPosition);
			break;
		case R.id.buttonSaveEditExtra:
			saveEditExtra();
			newTimerListFragment.getArrayAdapter().notifyDataSetChanged();
			onTimerSelected(mCurrentPosition);
			break;
		case R.id.buttonDeleteEditClock:
			createDeleteDialog();
			break;
		case R.id.buttonDeleteEditExtra:
			createDeleteDialog();
			break;
		case R.id.buttonCancelEditClock:
			newEditClockFragment.updateTimerView(mCurrentPosition);
			break;
		case R.id.buttonCancelEditExtra:
			newEditExtraFragment.updateTimerView(mCurrentPosition);
			break;
		case R.id.buttonAddHour:
			newEditClockFragment.timeChange(v);
			break;
		case R.id.buttonSubtractHour:
			newEditClockFragment.timeChange(v);
			break;
		case R.id.buttonAddMinute:
			newEditClockFragment.timeChange(v);
			break;
		case R.id.buttonSubtractMinute:
			newEditClockFragment.timeChange(v);
			break;
		case R.id.buttonAddSecond:
			newEditClockFragment.timeChange(v);
			break;
		case R.id.buttonSubtractSecond:
			newEditClockFragment.timeChange(v);
			break;
		case R.id.buttonAddSet:
			newEditClockFragment.setChange(v);
			break;
		case R.id.buttonSubtractSet:
			newEditClockFragment.setChange(v);
			break;
		case R.id.buttonAddRep:
			newEditClockFragment.setChange(v);
			break;
		case R.id.buttonSubtractRep:
			newEditClockFragment.setChange(v);
			break;
		case R.id.buttonOrientationAppOptions:
			createOrientationDialog();
			newAppOptionsFragment.updateOptions();
			break;
		case R.id.buttonFinishSoundAppOptions:
			createFinishSoundDialog();
			newAppOptionsFragment.updateOptions();
			break;
		case R.id.buttonRestSoundAppOptions:
			createRestSoundDialog();
			newAppOptionsFragment.updateOptions();
			break;
		case R.id.buttonCopyTimerAppOptions:
			createCopyTimerDialog();
			newAppOptionsFragment.updateOptions();
			break;
		}
	}

	public void chooseOrientation(int index) {
		Button btnOrientation = (Button) findViewById(R.id.buttonOrientationAppOptions);
		Resources res = getResources();
		String[] orientation = res.getStringArray(R.array.orientation_array);
		btnOrientation.setText("" + orientation[index]);
		mOrientationSetting = index;
		saveOptionsToFile();
		setOrientation();
	}

	public void chooseFinishSound(int index) {
		Button btnFinishSound = (Button) findViewById(R.id.buttonFinishSoundAppOptions);
		Resources res = getResources();
		String[] sound = res.getStringArray(R.array.sound_array);
		btnFinishSound.setText("" + sound[index]);
		mFinishSoundIndex = index;
		saveOptionsToFile();
		setSounds();
	}

	public void chooseRestSound(int index) {
		Button btnRestSound = (Button) findViewById(R.id.buttonRestSoundAppOptions);
		Resources res = getResources();
		String[] sound = res.getStringArray(R.array.sound_array);
		btnRestSound.setText("" + sound[index]);
		mRestSoundIndex = index;
		saveOptionsToFile();
		setSounds();
	}

	public void copyTimer() {
		int index = mCopyIndex;
		Toast.makeText(getApplicationContext(), "mCopyIndex: " + index,
				Toast.LENGTH_LONG).show();
	}

	public void addNewTimer() {
		// name, hour, minute, second, set, rep, rest, sound, weight,
		// seat number, notes, extra
		ArrayList<String> newArr = new ArrayList<String>();
		newArr.add("New Timer");
		newArr.add("0");
		newArr.add("0");
		newArr.add("0");
		newArr.add("1");
		newArr.add("1");
		newArr.add("no rest");
		newArr.add("0");
		newArr.add("0");
		newArr.add("");
		newArr.add("extra");

		TimeListArray.add(newArr);
		int newPos = TimeListArray.size() - 1;
		saveArrayListToFile();
		mCurrentPosition = newPos;
		onTimerSelected(mCurrentPosition);
	}

	public void reloadBaseList() {
		InputStream ins;
		try {
			ins = this.getAssets().open(EXTFILENAME);
			InputStreamReader in = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(in, 8192);
			TimeListArray.clear();
			TimeListNameArray.clear();
			String line = br.readLine();
			int cnt = 1;
			while (line != null) {
				String[] lineArr = line.split("\t");
				ArrayList<String> tempAL = new ArrayList<String>();
				for (String s : lineArr)
					tempAL.add(s);
				TimeListArray.add(tempAL);
				TimeListNameArray.add(cnt++ + ". " + lineArr[0]);
				line = br.readLine();
			}
			ins.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCurrentPosition = 0;
	}

	public void populateTimeListArray() {
		boolean fnfe = false;
		FileInputStream fis;
		try {
			fis = openFileInput(FILENAME);
			InputStreamReader in = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(in, 8192);
			TimeListArray.clear();
			TimeListNameArray.clear();
			String line = br.readLine();
			int cnt = 1;
			while (line != null) {
				String[] lineArr = line.split("\t");
				ArrayList<String> tempAL = new ArrayList<String>();
				for (String s : lineArr)
					tempAL.add(s);
				TimeListArray.add(tempAL);
				TimeListNameArray.add(cnt++ + ". " + lineArr[0]);
				line = br.readLine();
			}
			fis.close();
		} catch (FileNotFoundException e) {
			fnfe = true;
			e.printStackTrace();
		} catch (IOException e) {
			fnfe = true;
			e.printStackTrace();
		}

		if (fnfe) {
			InputStream ins;
			try {
				ins = this.getAssets().open(EXTFILENAME);
				InputStreamReader in = new InputStreamReader(ins);
				BufferedReader br = new BufferedReader(in, 8192);
				TimeListArray.clear();
				TimeListNameArray.clear();
				String line = br.readLine();
				int cnt = 1;
				while (line != null) {
					String[] lineArr = line.split("\t");
					ArrayList<String> tempAL = new ArrayList<String>();
					for (String s : lineArr)
						tempAL.add(s);
					TimeListArray.add(tempAL);
					TimeListNameArray.add(cnt++ + ". " + lineArr[0]);
					line = br.readLine();
				}
				ins.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void reloadOptions() {
		InputStream ins;
		try {
			ins = this.getAssets().open(EXTOPTIONSFILENAME);
			InputStreamReader in = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(in, 8192);
			String line = br.readLine();
			String[] lineArr = line.split("\t");
			ins.close();
			mOrientationSetting = Integer.parseInt(lineArr[0]);
			mFinishSoundIndex = Integer.parseInt(lineArr[1]);
			mRestSoundIndex = Integer.parseInt(lineArr[2]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void populateOptions() {
		boolean fnfe = false;
		FileInputStream fis;
		try {
			fis = openFileInput(OPTIONSFILENAME);
			InputStreamReader in = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(in, 8192);
			String line = br.readLine();
			String[] lineArr = line.split("\t");
			fis.close();
			mOrientationSetting = Integer.parseInt(lineArr[0]);
			mFinishSoundIndex = Integer.parseInt(lineArr[1]);
			mRestSoundIndex = Integer.parseInt(lineArr[2]);
		} catch (FileNotFoundException e) {
			fnfe = true;
			e.printStackTrace();
		} catch (IOException e) {
			fnfe = true;
			e.printStackTrace();
		}

		if (fnfe) {
			InputStream ins;
			try {
				ins = this.getAssets().open(EXTOPTIONSFILENAME);
				InputStreamReader in = new InputStreamReader(ins);
				BufferedReader br = new BufferedReader(in, 8192);
				String line = br.readLine();
				String[] lineArr = line.split("\t");
				ins.close();
				mOrientationSetting = Integer.parseInt(lineArr[0]);
				mFinishSoundIndex = Integer.parseInt(lineArr[1]);
				mRestSoundIndex = Integer.parseInt(lineArr[2]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteCurrentTimer() {
		TimerListFragment newTimerListFragment;
		if (isOnePager) {
			newTimerListFragment = (TimerListFragment) mAdapterAll.getItem(0);
		} else {
			newTimerListFragment = (TimerListFragment) mAdapterList.getItem(0);
		}
		int TLACount = TimeListArray.size();
		if (TLACount > 1) {
			newTimerListFragment.removeListTimer(mCurrentPosition);
			saveArrayListToFile();
			mCurrentPosition = 0;
			onTimerSelected(mCurrentPosition);
		} else {
			createCannotDeleteDialog();
		}
	}

	public void saveEditExtra() {
		ArrayList<String> tempArr = getTimeListArray().get(mCurrentPosition);

		Spinner tempSpin = (Spinner) findViewById(R.id.rest_btwn_spinner);
		String tempStr = tempSpin.getSelectedItem().toString();
		tempArr.set(6, tempStr);

		TextView tempTv = (TextView) findViewById(R.id.textWeightEditExtra);
		tempStr = tempTv.getText().toString();
		tempArr.set(7, tempStr);

		tempTv = (TextView) findViewById(R.id.textSeatEditExtra);
		tempStr = tempTv.getText().toString();
		tempArr.set(8, tempStr);

		tempTv = (TextView) findViewById(R.id.textNotesEditExtra);
		tempStr = tempTv.getText().toString();
		tempArr.set(9, tempStr);

		tempTv = (TextView) findViewById(R.id.textNameEditExtra);
		tempStr = tempTv.getText().toString();
		tempArr.set(0, tempStr);

		TimeListArray.set(mCurrentPosition, tempArr);

		saveArrayListToFile();
	}

	public void saveEditClock() {
		ArrayList<String> tempArr = getTimeListArray().get(mCurrentPosition);

		TextView tempTv = (TextView) findViewById(R.id.textTimeEditClock);
		String tempStr = tempTv.getText().toString();
		String[] tempStrArr = tempStr.split(":");
		int tempInt = Integer.parseInt(tempStrArr[0]);
		tempArr.set(1, "" + tempInt);
		tempInt = Integer.parseInt(tempStrArr[1]);
		tempArr.set(2, "" + tempInt);
		tempInt = Integer.parseInt(tempStrArr[2]);
		tempArr.set(3, "" + tempInt);

		tempTv = (TextView) findViewById(R.id.textCountSetEditClock);
		tempStr = tempTv.getText().toString();
		tempArr.set(4, tempStr);

		tempTv = (TextView) findViewById(R.id.textCountRepEditClock);
		tempStr = tempTv.getText().toString();
		tempArr.set(5, tempStr);

		tempTv = (TextView) findViewById(R.id.textNameEditClock);
		tempStr = tempTv.getText().toString();
		tempArr.set(0, tempStr);

		TimeListArray.set(mCurrentPosition, tempArr);

		saveArrayListToFile();
	}

	public void saveArrayListToFile() {
		// name, hour, minute, second, set, rep, rest, sound, weight,
		// seat number, notes, extra
		String outStr = "";
		int arrLen = 0;
		for (ArrayList<String> arr : TimeListArray) {
			arrLen = arr.size();
			for (int i = 0; i < arrLen; i++) {
				if (i < arrLen - 1) {
					outStr += arr.get(i) + "\t";
				} else {
					outStr += arr.get(i) + "\n";
				}
			}
		}

		FileOutputStream fos;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(outStr.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		populateTimeListArray();
	}

	public void saveOptionsToFile() {
		// Orientation, Finish Sound, Rest Sound
		String outStr = "";
		outStr += mOrientationSetting + "\t";
		outStr += mFinishSoundIndex + "\t";
		outStr += mRestSoundIndex + "\n";

		FileOutputStream fos;
		try {
			fos = openFileOutput(OPTIONSFILENAME, Context.MODE_PRIVATE);
			fos.write(outStr.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		populateOptions();
	}

	public void setRCFArgs(boolean Paused, boolean Started, boolean SoundIsOn,
			long Remaining, int SetRemaining, int RepRemaining, int TimesToRun,
			boolean RestHasStarted, boolean RestHasPaused, long RestRemaining,
			String TvTextSet, String TvTextRep) {
		mTimerHasPaused_RCF = Paused;
		mTimerHasStarted_RCF = Started;
		mTimerSoundIsOn_RCF = SoundIsOn;
		mTimeRemaining_RCF = Remaining;
		mSetSetRemaining_RFC = SetRemaining;
		mSetRepRemaining_RFC = RepRemaining;
		mTimesToRun_RFC = TimesToRun;
		mRestHasStarted_RCF = RestHasStarted;
		mRestHasPaused_RCF = RestHasPaused;
		mRestRemaining_RFC = RestRemaining;
		mTvTextSet_RFC = TvTextSet;
		mTvTextRep_RFC = TvTextRep;
	}

	public void createDeleteDialog() {
		AlertDialog.Builder builderDelete = new AlertDialog.Builder(this);
		builderDelete.setTitle("Delete Timer?");
		builderDelete
				.setMessage("Are you sure you want to delete this timer?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								deleteCurrentTimer();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builderDelete.show();
	}

	public void createCannotDeleteDialog() {
		AlertDialog.Builder builderCannotDelete = new AlertDialog.Builder(this);
		builderCannotDelete.setTitle("Cannot Delete Timer!");
		builderCannotDelete
				.setMessage("You cannot delete this timer!\nThere needs to be at least one timer.");
		builderCannotDelete.setCancelable(false);
		builderCannotDelete.setNeutralButton("OK", null);
		builderCannotDelete.show();
	}

	public void createOrientationDialog() {
		AlertDialog.Builder builderOrientation = new AlertDialog.Builder(this);
		builderOrientation.setTitle("Choose Orientation");
		builderOrientation.setSingleChoiceItems(R.array.orientation_array,
				mOrientationSetting, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						chooseOrientation(item);
						dialog.dismiss();
					}
				});
		builderOrientation.show();
	}

	public void createFinishSoundDialog() {
		AlertDialog.Builder builderFinishSound = new AlertDialog.Builder(this);
		builderFinishSound.setTitle("Choose Finish Sound");
		builderFinishSound.setSingleChoiceItems(R.array.sound_array,
				mFinishSoundIndex, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						chooseFinishSound(item);
						dialog.dismiss();
					}
				});
		builderFinishSound.show();
	}

	public void createRestSoundDialog() {
		AlertDialog.Builder builderRestSound = new AlertDialog.Builder(this);
		builderRestSound.setTitle("Choose Rest Sound");
		builderRestSound.setSingleChoiceItems(R.array.sound_array,
				mRestSoundIndex, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						chooseRestSound(item);
						dialog.dismiss();
					}
				});
		builderRestSound.show();
	}

	public void createCopyTimerDialog() {
		AlertDialog.Builder builderCopyTimer = new AlertDialog.Builder(this);
		builderCopyTimer.setTitle("Copy Timer");
		String[] timers = new String[TimeListNameArray.size()];
		int arrSize = TimeListNameArray.size();
		for (int i = 0; i < arrSize; i++) {
			timers[i] = (String) TimeListNameArray.get(i);
		}
		builderCopyTimer.setItems(timers,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						createCopyTimerConfirmDialog(item);
						dialog.dismiss();
					}
				});
		builderCopyTimer.show();
	}

	public void createCopyTimerConfirmDialog(int item) {
		mCopyIndex = item;
		AlertDialog.Builder builderConfirm = new AlertDialog.Builder(this);
		builderConfirm.setTitle("Confirm Timer Copy?");
		builderConfirm
				.setMessage(
						"Are you sure you want to copy this timer?\n"
								+ (String) TimeListNameArray.get(item))
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								copyTimer();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builderConfirm.show();
	}

	public void volumeUp() {
		MpAudio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
				AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	}

	public void volumeDown() {
		MpAudio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
				AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
	}

	public boolean getTimerHasPaused() {
		return mTimerHasPaused_RCF;
	}

	public boolean getTimerHasStarted() {
		return mTimerHasStarted_RCF;
	}

	public boolean getTimerSoundIsOn() {
		return mTimerSoundIsOn_RCF;
	}

	public long getTimeRemaining() {
		return mTimeRemaining_RCF;
	}

	public int getCurrentPosition() {
		return mCurrentPosition;
	}

	public int getOrientationSetting() {
		return mOrientationSetting;
	}

	public int getFinishSoundIndex() {
		return mFinishSoundIndex;
	}

	public int getRestSoundIndex() {
		return mRestSoundIndex;
	}

	public MediaPlayer getMpFinished() {
		return MpFinished;
	}

	public MediaPlayer getMpRested() {
		return MpRested;
	}

	public int getSetSetRemaining() {
		return mSetSetRemaining_RFC;
	}

	public int getSetRepRemaining() {
		return mSetRepRemaining_RFC;
	}

	public int getTimesToRun() {
		return mTimesToRun_RFC;
	}

	public boolean getRestHasStarted() {
		return mRestHasStarted_RCF;
	}

	public boolean getRestHasPaused() {
		return mRestHasPaused_RCF;
	}

	public long getRestRemaining() {
		return mRestRemaining_RFC;
	}

	public String getTvTextSet() {
		return mTvTextSet_RFC;
	}

	public String getTvTextRep() {
		return mTvTextRep_RFC;
	}

	public ArrayList<ArrayList<String>> getTimeListArray() {
		return TimeListArray;
	}

	public ArrayList<String> getTimeListNameArray() {
		return TimeListNameArray;
	}

	private class MyPageChangeListener extends
			ViewPager.SimpleOnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			mCurrentPage = position;
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			mCurrentPage = position;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		if (MpFinished != null) {
			MpFinished.stop();
			MpFinished.release();
			MpFinished = null;
		}
		if (MpRested != null) {
			MpRested.stop();
			MpRested.release();
			MpRested = null;
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		if (MpFinished != null) {
			MpFinished.stop();
			MpFinished.release();
			MpFinished = null;
		}
		if (MpRested != null) {
			MpRested.stop();
			MpRested.release();
			MpRested = null;
		}
		super.onStop();
	}

	@Override
	public void onDestroy() {
		if (MpFinished != null) {
			MpFinished.stop();
			MpFinished.release();
			MpFinished = null;
		}
		if (MpRested != null) {
			MpRested.stop();
			MpRested.release();
			MpRested = null;
		}
		super.onDestroy();
	}
}
