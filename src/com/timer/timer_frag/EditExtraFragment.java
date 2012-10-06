package com.timer.timer_frag;

import java.util.ArrayList;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditExtraFragment extends Fragment {

	final static String ARG_POSITION = "position";
	int mCurrentPosition = -1;
	OnTimerSelectedListener mCallback;

	private static ArrayList<String> TimeListArray;
	public static ArrayList<String> SoundListNameArray;

	private static String Name;
	private static String Rest;
	private static int Weight;
	private static int Seat;
	private static String Notes;

	private static Spinner RestBtwnSpinner;
	private static TextView TvTextName;
	private static TextView TvTextWeight;
	private static TextView TvTextSeat;
	private static TextView TvTextNotes;

	static EditExtraFragment newInstance(int num) {
		EditExtraFragment f = new EditExtraFragment();

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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnTimerSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTimerSelectedListener");
		}
	}

	// Inflate with the layout xml file 'edit_extra_fragment'
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.edit_extra_fragment, container, false);
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

	public void updateTimerView(int position) {
		mCurrentPosition = position;
		TimeListArray = mCallback.getTimeListArray().get(mCurrentPosition);

		RestBtwnSpinner = (Spinner) getActivity().findViewById(
				R.id.rest_btwn_spinner);
		ArrayAdapter<CharSequence> timeBtwnRepsAdapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.rest_btwn_array,
						R.layout.spinner_layout);
		timeBtwnRepsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		RestBtwnSpinner.setAdapter(timeBtwnRepsAdapter);

		Name = TimeListArray.get(0);
		Rest = TimeListArray.get(6);
		if (Integer.parseInt(TimeListArray.get(7)) > -1) {
			Weight = Integer.parseInt(TimeListArray.get(7));
		} else {
			Weight = 0;
		}
		if (Integer.parseInt(TimeListArray.get(8)) > -1) {
			Seat = Integer.parseInt(TimeListArray.get(8));
		} else {
			Seat = 0;
		}
		Notes = TimeListArray.get(9);

		TvTextName = (TextView) getActivity().findViewById(
				R.id.textNameEditExtra);
		TvTextWeight = (TextView) getActivity().findViewById(
				R.id.textWeightEditExtra);
		TvTextSeat = (TextView) getActivity().findViewById(
				R.id.textSeatEditExtra);
		TvTextNotes = (TextView) getActivity().findViewById(
				R.id.textNotesEditExtra);

		setTimerName();
		setTimerExtras();

	}

	public void setTimerName() {
		TvTextName.setText(Name);
	}

	@SuppressWarnings("unchecked")
	public void setTimerExtras() {
		ArrayAdapter<String> myAdap;
		myAdap = (ArrayAdapter<String>) RestBtwnSpinner.getAdapter();
		int spinnerPosition = myAdap.getPosition(Rest);
		RestBtwnSpinner.setSelection(spinnerPosition);
		myAdap.notifyDataSetChanged();

		if (Weight >= 0){
			TvTextWeight.setText("" + Weight);
		}else{
			TvTextWeight.setText("" + 0);
		}
		if (Seat != 0){
			TvTextSeat.setText("" + Seat);
		}else{
			TvTextSeat.setText("" + 0);
		}
		if (Notes.length() > 0){
			TvTextNotes.setText(Notes);
		}else{
			TvTextNotes.setText("");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
