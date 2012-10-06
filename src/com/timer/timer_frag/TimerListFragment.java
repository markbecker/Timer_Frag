package com.timer.timer_frag;

import java.util.ArrayList;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TimerListFragment extends ListFragment {

	private static final String ARG_ACTIVATED_POSITION = "activated_position";	

	OnTimerSelectedListener mCallback;
    private int mActivatedPosition = ListView.INVALID_POSITION; 
    static ArrayAdapter<String> myArrayAdapter;

	// Used to talk to the Main Activity
	public interface OnTimerSelectedListener {
		public void onTimerSelected(int position);
		
		public int getCurrentPosition();
		
		public ArrayList<ArrayList<String>> getTimeListArray();

		public ArrayList<String> getTimeListNameArray();
		
	}

	static TimerListFragment newInstance(int num) {
		TimerListFragment f = new TimerListFragment();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt(ARG_ACTIVATED_POSITION, num);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Use a different list item layout for devices older than Honeycomb
		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1
				: android.R.layout.simple_list_item_1;
		myArrayAdapter = new ArrayAdapter<String>(getActivity(), layout,
				mCallback.getTimeListNameArray());
		// Create the list based on the ArrayList from the Main Activity
		setListAdapter(myArrayAdapter);	
	}
	
	// Inflate with the layout xml file 'timer_list_fragment'
	// that contains 'layout_main_left' and 'layout_main_right'
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.list_fragment, container, false);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Show item selected
        setActivateOnItemClick(true);
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

	@Override
	public void onResume() {
		super.onResume();
		mActivatedPosition = mCallback.getCurrentPosition();
		setActivatedPosition(mActivatedPosition);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,	long id) {
		super.onListItemClick(listView, view, position, id);
		// Notify the parent activity of selected item
		mCallback.onTimerSelected(position);		
	}

	public void updateTimerView(int position) {
		onResume();
	}

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }	

	/** Called when the user clicks the 'New Timer' button */
	public void newTime(View view) {
		// Intent intent = new Intent(this, EditActivity.class);
		// String message = "-1";
		// intent.putExtra(EXTRA_MESSAGE, message);
		// startActivity(intent);
	}
	
	public void removeListTimer(int position){
		mCallback.getTimeListNameArray().remove(position);
		mCallback.getTimeListArray().remove(position);
		myArrayAdapter.notifyDataSetChanged();
		onResume();		
	}
	
	public ArrayAdapter<String> getArrayAdapter(){
		return myArrayAdapter;
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
    }
}
