package de.fiw.fhws.lecturers.specific.fragment;

import android.content.Intent;
import android.view.View;

import de.fiw.fhws.lecturers.R;
import de.fiw.fhws.lecturers.fragment.NewResourceFragment;
import de.fiw.fhws.lecturers.network.NetworkCallback;
import de.fiw.fhws.lecturers.network.NetworkResponse;
import de.fiw.fhws.lecturers.specific.LecturerDetailActivity;
import de.marcelgross.lecturer_lib.specific.customView.LecturerInputView;

public class NewLecturerFragment extends NewResourceFragment {
	@Override
	protected int getLayout() {
		return R.layout.fragment_lecturer_input;
	}

	@Override
	protected void initializeView(View view) {
		inputView = (LecturerInputView) view.findViewById(R.id.lecturer_input);
	}

	@Override
	protected NetworkCallback getCallback() {
		return new NetworkCallback() {
			@Override
			public void onFailure() {

			}

			@Override
			public void onSuccess(final NetworkResponse response) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(getActivity(), LecturerDetailActivity.class);
						intent.putExtra("selfUrl", response.getHeader().get("location").get(0));
						getActivity().startActivity(intent);
						getFragmentManager().popBackStack();
					}
				});
			}
		};
	}
}