package de.marcelgross.lecturer_lib.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import de.marcelgross.lecturer_lib.R;
import de.marcelgross.lecturer_lib.adapter.RessourceListAdapter;
import de.marcelgross.lecturer_lib.customView.LecturerCardView;
import de.marcelgross.lecturer_lib.customView.AttributeView;
import de.marcelgross.lecturer_lib.customView.ProfileImageView;
import de.marcelgross.lecturer_lib.model.Lecturer;
import de.marcelgross.lecturer_lib.model.Ressource;

public class LecturerListViewHolder extends RessourceViewHolder implements View.OnClickListener {

	private Lecturer lecturer;
	private final Context context;
	private final RessourceListAdapter.OnRessourceClickListener onLecturerClickListener;

	private final LecturerCardView cardView;
	private final AttributeView email;
	private final AttributeView phone;
	private final AttributeView address;
	private final AttributeView welearn;

	private final ProfileImageView profileImg;

	@Override
	public void onClick(View view) {
		int i = view.getId();
		if (i == R.id.email) {
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + lecturer.getEmail()));
			context.startActivity(Intent.createChooser(intent, "Send Email"));

		} else if (i == R.id.phone) {
			Intent dialIntent = new Intent(Intent.ACTION_DIAL);
			dialIntent.setData(Uri.parse("tel:" + lecturer.getPhone()));
			context.startActivity(dialIntent);

		} else if (i == R.id.address) {
			Uri location = Uri.parse("geo:0,0?q=" + lecturer.getAddress());
			Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
			mapIntent.setPackage("com.google.android.apps.maps");
			context.startActivity(mapIntent);

		} else if (i == R.id.welearn) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(lecturer.getUrlWelearn()));
			context.startActivity(browserIntent);
		}
	}

	public LecturerListViewHolder(View itemView, RessourceListAdapter.OnRessourceClickListener onLecturerClickListener) {
		super(itemView);
		this.context = itemView.getContext();
		this.onLecturerClickListener = onLecturerClickListener;
		cardView = (LecturerCardView) itemView.findViewById(R.id.lecturer_card);

		email = (AttributeView) itemView.findViewById(R.id.email);
		phone = (AttributeView) itemView.findViewById(R.id.phone);
		address = (AttributeView) itemView.findViewById(R.id.address);
		welearn = (AttributeView) itemView.findViewById(R.id.welearn);

		profileImg = (ProfileImageView) itemView.findViewById(R.id.profileImg);
	}

	@Override
	public void assignData(final Ressource ressource) {
		final Lecturer lecturer = (Lecturer) ressource;
		this.lecturer = lecturer;
		cardView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (onLecturerClickListener != null) {
					onLecturerClickListener.onResourceClickWithView(lecturer, profileImg);
					onLecturerClickListener.onResourceClick(lecturer);
				}
			}
		});

		cardView.setUpView(lecturer);

		email.setOnClickListener(this);
		phone.setOnClickListener(this);
		address.setOnClickListener(this);
		welearn.setOnClickListener(this);
	}

}