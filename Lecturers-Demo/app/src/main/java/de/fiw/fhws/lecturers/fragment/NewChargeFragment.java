package de.fiw.fhws.lecturers.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.owlike.genson.Genson;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.fiw.fhws.lecturers.util.FragmentHandler;
import de.fiw.fhws.lecturers.R;
import de.fiw.fhws.lecturers.network.OKHttpSingleton;
import de.fiw.fhws.lecturers.network.util.HeaderParser;
import de.fiw.fhws.lecturers.util.GensonBuilder;
import de.marcelgross.lecturer_lib.customView.ChargeInputView;
import de.marcelgross.lecturer_lib.customView.DateTimeView;
import de.marcelgross.lecturer_lib.fragment.DateTimePickerFragment;
import de.marcelgross.lecturer_lib.model.Charge;
import de.marcelgross.lecturer_lib.model.Link;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewChargeFragment extends Fragment implements View.OnClickListener, DateTimePickerFragment.OnDateTimeSetListener {

	private final Genson genson = new GensonBuilder().getDateFormater();
	private ChargeInputView chargeInputView;
	private DateTimeView startDateView;
	private DateTimeView endDateView;
	private int state;

	private String url;
	private String mediaType;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		this.url = bundle.getString("url");
		this.mediaType = bundle.getString("mediaType");
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_new_charge, container, false);

		chargeInputView = (ChargeInputView) view.findViewById(R.id.input_view);
		startDateView = (DateTimeView) view.findViewById(R.id.startDate);
		endDateView = (DateTimeView) view.findViewById(R.id.endDate);

		startDateView.setOnClickListener(this);
		endDateView.setOnClickListener(this);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.save_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.saveItem:
				saveCharge();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		this.state = view.getId();

		DateTimePickerFragment dateTimePickerFragment = new DateTimePickerFragment();
		dateTimePickerFragment.setTargetFragment(NewChargeFragment.this, 0);

		dateTimePickerFragment.show(getFragmentManager(), "dateTime");

	}

	@Override
	public void onDateTimeSet(Date date) {
		switch (state) {
			case R.id.startDate:
				startDateView.setDate(date);
				break;
			case R.id.endDate:
				endDateView.setDate(date);
		}
	}

	private void saveCharge() {
		Charge charge = chargeInputView.getCharge();

		if (charge != null) {
			String chargeString = genson.serialize(charge);

			RequestBody body = RequestBody.create(MediaType.parse(mediaType), chargeString);
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.build();

			OkHttpClient client = OKHttpSingleton.getCacheInstance(getActivity()).getClient();

			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					e.printStackTrace();
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (!response.isSuccessful()) {
						throw  new IOException("Unexpected code " + response);
					}

					Map<String, List<String>> headers = response.headers().toMultimap();
					Map<String, Link> linkHeader = HeaderParser.getLinks(headers.get("link"));
					final Link allChargesLink = linkHeader.get(getActivity().getString(R.string.rel_type_get_all_charges));

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getActivity(), R.string.charge_saved, Toast.LENGTH_SHORT).show();
							Bundle bundle = new Bundle();
							bundle.putString("url", allChargesLink.getHrefWithoutQueryParams());
							bundle.putString("mediaType", allChargesLink.getType());
							Fragment fragment = new ChargeListFragment();
							fragment.setArguments(bundle);

							FragmentHandler.replaceFragmentPopBackStack(getFragmentManager(), fragment);
						}
					});
				}
			});

		}
	}


}
