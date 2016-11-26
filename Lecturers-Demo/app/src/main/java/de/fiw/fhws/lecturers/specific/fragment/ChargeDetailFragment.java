package de.fiw.fhws.lecturers.specific.fragment;

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

import java.util.Map;

import de.fiw.fhws.lecturers.R;
import de.fiw.fhws.lecturers.fragment.DeleteDialogFragment;
import de.fiw.fhws.lecturers.network.NetworkCallback;
import de.fiw.fhws.lecturers.network.NetworkClient;
import de.fiw.fhws.lecturers.network.NetworkRequest;
import de.fiw.fhws.lecturers.network.NetworkResponse;
import de.fiw.fhws.lecturers.util.FragmentHandler;
import de.fiw.fhws.lecturers.util.GensonBuilder;
import de.marcelgross.lecturer_lib.specific.customView.ChargeDetailView;
import de.marcelgross.lecturer_lib.specific.model.Charge;
import de.marcelgross.lecturer_lib.model.Link;

public class ChargeDetailFragment extends Fragment implements DeleteDialogFragment.DeleteDialogListener {

	private final Genson genson = new GensonBuilder().getDateFormatter();
	private Charge currentCharge;
	private String chargeUrl;
	private String mediaType;
	private Link deleteLink;
	private Link updateLink;
	private ChargeDetailView chargeDetailView;


	@Override
	public void onDialogClosed(final boolean successfullyDeleted) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (successfullyDeleted) {
					getFragmentManager().popBackStackImmediate();
				} else {
					Toast.makeText(getActivity(), R.string.charge_delete_error, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (savedInstanceState == null) {
			Bundle bundle = getArguments();
			chargeUrl = bundle.getString("url", "");
			mediaType = bundle.getString("mediaType", "");
		} else {
			chargeUrl = savedInstanceState.getString("url", "");
			mediaType = savedInstanceState.getString("mediaType", "");
		}

		loadCharge();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("url", chargeUrl);
		outState.putString("mediaType", mediaType);
		super.onSaveInstanceState(outState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_charge_detail, container, false);

		chargeDetailView = (ChargeDetailView) view.findViewById(R.id.charge_detail_view);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.detail_menu, menu);
		MenuItem deleteItem = menu.findItem(R.id.delete_item);
		MenuItem editItem = menu.findItem(R.id.edit_item);

		deleteItem.setVisible(deleteLink != null);
		editItem.setVisible(updateLink != null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.edit_item:
				Bundle editBundle = new Bundle();
				editBundle.putString("url", updateLink.getHref());
				editBundle.putString("mediaType", updateLink.getType());
				Fragment fragment = new EditChargeFragment();
				fragment.setArguments(editBundle);
				FragmentHandler.replaceFragmentPopBackStack(getFragmentManager(), fragment);
				break;
			case R.id.delete_item:
				Bundle bundle = new Bundle();
				bundle.putString("url", deleteLink.getHref());
				bundle.putString("name", currentCharge.getTitle());
				DeleteDialogFragment deleteDialogFragment = new DeleteDialogFragment();
				deleteDialogFragment.setArguments(bundle);
				deleteDialogFragment.setTargetFragment(ChargeDetailFragment.this, 0);
				deleteDialogFragment.show(getFragmentManager(), null);
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setUp(Charge charge) {
		getActivity().invalidateOptionsMenu();
		chargeDetailView.setUpView(charge);
	}

	private void loadCharge() {
		NetworkClient client = new NetworkClient(getActivity(), new NetworkRequest().acceptHeader(mediaType).url(chargeUrl));
		client.sendRequest(new NetworkCallback() {
			@Override
			public void onFailure() {
			}

			@Override
			public void onSuccess(NetworkResponse response) {
				currentCharge = genson.deserialize(response.getResponseReader(), Charge.class);
				Map<String, Link> linkHeader =response.getLinkHeader();

				deleteLink = linkHeader.get(getActivity().getString(R.string.rel_type_delete_charge));
				updateLink = linkHeader.get(getActivity().getString(R.string.rel_type_update_charge));

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setUp(currentCharge);
					}
				});
			}
		});
	}
}