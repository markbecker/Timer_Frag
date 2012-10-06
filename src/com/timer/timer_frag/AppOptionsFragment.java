package com.timer.timer_frag;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AppOptionsFragment extends Fragment {

	int mCurrentPosition = -1;
	OnOptionsUpdateListener mCallback;

	static int OrientationIndex = 0;
	static int FinishSoundIndex = 0;
	static int RestSoundIndex = 0;

	static AppOptionsFragment newInstance(int orientationIndex,
			int finishSoundIndex, int restSoundIndex) {
		AppOptionsFragment f = new AppOptionsFragment();
		OrientationIndex = 0;
		FinishSoundIndex = 0;
		RestSoundIndex = 0;
		return f;
	}

	// Used to talk to the Main Activity
	public interface OnOptionsUpdateListener {
		public void onTimerSelected(int position);

		public void onButtonClicked(View v);

		public int getOrientationSetting();

		public int getFinishSoundIndex();

		public int getRestSoundIndex();

		public ArrayList<String> getTimeListNameArray();

		public ArrayList<ArrayList<String>> getTimeListArray();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnOptionsUpdateListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnOptionsUpdateListener");
		}
	}

	// Inflate with the layout xml file 'edit_extra_fragment'
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater
				.inflate(R.layout.app_options_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();

		OrientationIndex = mCallback.getOrientationSetting();
		Button btnOrientation = (Button) getActivity().findViewById(
				R.id.buttonOrientationAppOptions);
		Resources res = getResources();
		String[] orientation = res.getStringArray(R.array.orientation_array);
		btnOrientation.setText("" + orientation[OrientationIndex]);

		FinishSoundIndex = mCallback.getFinishSoundIndex();
		Button btnFinishSound = (Button) getActivity().findViewById(
				R.id.buttonFinishSoundAppOptions);
		String[] finishSound = res.getStringArray(R.array.sound_array);
		btnFinishSound.setText("" + finishSound[FinishSoundIndex]);

		RestSoundIndex = mCallback.getRestSoundIndex();
		Button btnRestSound = (Button) getActivity().findViewById(
				R.id.buttonRestSoundAppOptions);
		String[] restSound = res.getStringArray(R.array.sound_array);
		btnRestSound.setText("" + restSound[RestSoundIndex]);		
	}

	public void updateOptions() {
		OrientationIndex = mCallback.getOrientationSetting();
		Button btnOrientation = (Button) getActivity().findViewById(
				R.id.buttonOrientationAppOptions);
		Resources res = getResources();
		String[] orientation = res.getStringArray(R.array.orientation_array);
		btnOrientation.setText("" + orientation[OrientationIndex]);

		FinishSoundIndex = mCallback.getFinishSoundIndex();
		Button btnFinishSound = (Button) getActivity().findViewById(
				R.id.buttonFinishSoundAppOptions);
		String[] finishSound = res.getStringArray(R.array.sound_array);
		btnFinishSound.setText("" + finishSound[FinishSoundIndex]);

		RestSoundIndex = mCallback.getRestSoundIndex();
		Button btnRestSound = (Button) getActivity().findViewById(
				R.id.buttonRestSoundAppOptions);
		String[] restSound = res.getStringArray(R.array.sound_array);
		btnRestSound.setText("" + restSound[RestSoundIndex]);
	}
}
