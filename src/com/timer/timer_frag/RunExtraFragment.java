package com.timer.timer_frag;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RunExtraFragment extends Fragment {

	final static String ARG_POSITION = "position";
	int mCurrentPosition = -1;
	OnTimerSelectedListener mCallback;

	private static ArrayList<String> TimeListArray;

	private static String Name;
	private static String Rest;
	private static int Weight;
	private static int Seat;
	private static String Notes;

	private static TextView TvTextName;
	private static TextView TvTextRest;
	private static TextView TvTextWeight;
	private static TextView TvTextSeat;
	private static TextView TvTextNotes;

	static RunExtraFragment newInstance(int num) {
		RunExtraFragment f = new RunExtraFragment();

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

	// Inflate with the layout xml file 'run_fragment' that contains
	// 'run_fragment_left' and 'run_fragment_right'
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.run_extra_fragment, container, false);
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
		try {
			mCallback = (OnTimerSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTimerSelectedListener");
		}

	}

	public void updateTimerView(int position) {
		mCurrentPosition = position;
		TvTextName = (TextView) getActivity().findViewById(
				R.id.textNameRunExtra);
		TvTextRest = (TextView) getActivity().findViewById(
				R.id.textRestRunExtra);
		TvTextWeight = (TextView) getActivity().findViewById(
				R.id.textWeightRunExtra);
		TvTextSeat = (TextView) getActivity().findViewById(
				R.id.textSeatRunExtra);
		TvTextNotes = (TextView) getActivity().findViewById(
				R.id.textNotesRunExtra);

		TimeListArray = mCallback.getTimeListArray().get(mCurrentPosition);

		Name = (mCurrentPosition + 1) + ". " + TimeListArray.get(0);
		Rest = TimeListArray.get(6);
		Weight = Integer.parseInt(TimeListArray.get(7));
		Seat = Integer.parseInt(TimeListArray.get(8));
		Notes = TimeListArray.get(9);
		
		setTimerExtras();
	}

	public void setTimerExtras() {
		TvTextName.setText(Name);
		TvTextRest.setText(Rest);
		TvTextWeight.setText("" + Weight);
		TvTextSeat.setText("" + Seat);
		TvTextNotes.setText(" " + Notes);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
