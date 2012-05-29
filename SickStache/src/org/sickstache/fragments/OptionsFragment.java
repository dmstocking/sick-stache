/*
 * 	SickStache is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStache is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sickstache.fragments;

import org.sickstache.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.Button;
import android.widget.EditText;

public class OptionsFragment extends PreferenceFragment {

	private EditText host;
	private EditText port;
	private EditText api;
	
	private Button save;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		host = (EditText)getActivity().findViewById(R.id.hostEditText);
//		port = (EditText)getActivity().findViewById(R.id.portEditText);
//		api = (EditText)getActivity().findViewById(R.id.apiEditText);
//		save = (Button)getActivity().findViewById(R.id.saveButton);
//		host.setText(Prefrences.singleton.getHost());
//		port.setText(Prefrences.singleton.getPort());
//		api.setText(Prefrences.singleton.getAPI());
//		save.setOnClickListener( new View.OnClickListener() {
//			public void onClick(View v) {
//				Prefrences.singleton.setSickBeard(host.getText().toString(), port.getText().toString(), api.getText().toString());
//				Intent intent = new Intent(OptionsFragment.this.getActivity(), HomeActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				OptionsFragment.this.startActivity(intent);
//			}
//		});
		this.addPreferencesFromResource(R.xml.preferences);
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.preferences, container);
//	}

}
