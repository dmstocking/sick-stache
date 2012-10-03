package org.sickstache.app;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragment;

public class SickFragment extends SherlockFragment {

	private boolean retainedLifecycle = false;

	protected boolean isInRetainLifecycle() {
		return retainedLifecycle;
	}

	protected boolean isRetainInstance() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(isRetainInstance());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// this is a gay hidden state that exists because of the FragmentTransaction.detach
		if ( isDetached() ) {
			this.retainedLifecycle = true;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.retainedLifecycle = true; // past this point if this is retained then this value will stay true
		// if it goes back to false then we recreated the fragment
	}
}
