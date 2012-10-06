package com.timer.timer_frag;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EditClockFragment extends Fragment {

	final static String ARG_POSITION = "position";
	int mCurrentPosition = -1;
	OnTimerSelectedListener mCallback;

	private static ArrayList<String> TimeListArray;

	private static int TimeHours;
	private static int TimeMinutes;
	private static int TimeSeconds;
	private static int SetSet;
	private static int SetRep;
	private static TextView TvTextName;
	private static TextView TvTextTime;
	private static String Name;

	static EditClockFragment newInstance(int num) {
		EditClockFragment f = new EditClockFragment();

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

		public ArrayList<String> getTimeListNameArray();

		public ArrayList<ArrayList<String>> getTimeListArray();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	// Inflate with the layout xml file 'edit_clock_fragment'
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.edit_clock_fragment, container, false);
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (OnTimerSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTimerSelectedListener");
		}

	}

	public void updateTimerView(int position) {
		mCurrentPosition = position;
		TvTextName = (TextView) getActivity().findViewById(R.id.textNameEditClock);
		TvTextTime = (TextView) getActivity().findViewById(R.id.textTimeEditClock);

		if (mCurrentPosition < 0) {
			TimeListArray = new ArrayList<String>();
			String line = "\t0\t0\t0\t0\t0\tno time\tno sound\t0\t0\t\n";
			String[] lineArr = line.split("\t");
			ArrayList<String> tempAL = new ArrayList<String>();
			for (String s : lineArr)
				tempAL.add(s);
			TimeListArray = tempAL;
		} else {
			TimeListArray = mCallback.getTimeListArray().get(mCurrentPosition);
		}

		Name = TimeListArray.get(0);
		TimeHours = Integer.parseInt(TimeListArray.get(1));
		TimeMinutes = Integer.parseInt(TimeListArray.get(2));
		TimeSeconds = Integer.parseInt(TimeListArray.get(3));
		SetSet = Integer.parseInt(TimeListArray.get(4));
		SetRep = Integer.parseInt(TimeListArray.get(5));
		
		setTimerName();
		setTimerTime();
		setTimerCount();
	}

	public void setTimerName() {
		TvTextName.setText("" + Name);
	}

	public void setTimerTime() {
		String out = "";
		out += TimeHours + ":";
		if (TimeMinutes < 10)
			out += 0;
		out += TimeMinutes + ":";
		if (TimeSeconds < 10)
			out += 0;
		out += TimeSeconds;
		TvTextTime.setText(out);
	}

	public void setTimerCount() {
		TextView tv = null;
		tv = (TextView) getActivity().findViewById(R.id.textCountSetEditClock);
		tv.setText("" + SetSet);
		tv = (TextView) getActivity().findViewById(R.id.textCountRepEditClock);
		tv.setText("" + SetRep);
	}

	public void timeChange(View view) {
		int rollover = 59;
		switch (view.getId()) {
		case R.id.buttonAddHour:
			rollover = 9;
			if (TimeHours < rollover)
				TimeHours++;
			else
				TimeHours = 0;
			break;
		case R.id.buttonAddMinute:
			if (TimeMinutes < rollover)
				TimeMinutes++;
			else
				TimeMinutes = 0;
			break;
		case R.id.buttonAddSecond:
			if (TimeSeconds < rollover)
				TimeSeconds++;
			else
				TimeSeconds = 0;
			break;
		case R.id.buttonSubtractHour:
			rollover = 9;
			if (TimeHours > 0)
				TimeHours--;
			else
				TimeHours = rollover;
			break;
		case R.id.buttonSubtractMinute:
			if (TimeMinutes > 0)
				TimeMinutes--;
			else
				TimeMinutes = rollover;
			break;
		case R.id.buttonSubtractSecond:
			if (TimeSeconds > 0)
				TimeSeconds--;
			else
				TimeSeconds = rollover;
			break;
		}
		setTimerTime();
	}

	public void setChange(View view) {
		int rollover = 99;
		switch (view.getId()) {
		case R.id.buttonAddSet:
			if (SetSet < rollover)
				SetSet++;
			else
				SetSet = 1;
			break;
		case R.id.buttonAddRep:
			if (SetRep < rollover)
				SetRep++;
			else
				SetRep = 1;
			break;
		case R.id.buttonSubtractSet:
			if (SetSet > 1)
				SetSet--;
			else
				SetSet = rollover;
			break;
		case R.id.buttonSubtractRep:
			if (SetRep > 1)
				SetRep--;
			else
				SetRep = rollover;
			break;
		}
		setTimerCount();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
