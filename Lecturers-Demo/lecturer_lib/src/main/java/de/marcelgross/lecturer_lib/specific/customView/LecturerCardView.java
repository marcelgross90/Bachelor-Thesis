package de.marcelgross.lecturer_lib.specific.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import de.marcelgross.lecturer_lib.R;
import de.marcelgross.lecturer_lib.generic.customView.AttributeView;
import de.marcelgross.lecturer_lib.generic.customView.ProfileImageView;
import de.marcelgross.lecturer_lib.generic.customView.ResourceCardView;
import de.marcelgross.lecturer_lib.generic.model.Resource;
import de.marcelgross.lecturer_lib.specific.model.Lecturer;


public class LecturerCardView extends ResourceCardView {

	private ProfileImageView imageView;
	private AttributeView titleView;
	private AttributeView nameView;
	private AttributeView mailView;
	private AttributeView phoneView;
	private AttributeView roomView;
	private AttributeView addressView;
	private AttributeView welearnView;

	public LecturerCardView(Context context) {
		super(context);
	}

	public LecturerCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LecturerCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected int[] getStyleable() {
		return R.styleable.LecturerCardView;
	}

	@Override
	protected int getLayout() {
		return R.layout.view_lecturer_card;
	}

	@Override
	protected void initializeView() {
		imageView = (ProfileImageView) findViewById(R.id.profileImg);
		titleView = (AttributeView) findViewById(R.id.title);
		nameView = (AttributeView) findViewById(R.id.name);
		mailView = (AttributeView) findViewById(R.id.email);
		phoneView = (AttributeView) findViewById(R.id.phone);
		roomView = (AttributeView) findViewById(R.id.room);
		addressView = (AttributeView) findViewById(R.id.address);
		welearnView = (AttributeView) findViewById(R.id.welearn);
	}

	@Override
	public void setUpView(Resource resource) {
		Lecturer lecturer = (Lecturer) resource;
		hideUnnecessaryViews(lecturer);
		titleView.setText(lecturer.getTitle());
		nameView.setText(lecturer.getFirstName() + " " + lecturer.getLastName());
		mailView.setText(lecturer.getEmail());
		phoneView.setText(lecturer.getPhone());
		roomView.setText(lecturer.getRoomNumber());
		addressView.setText(lecturer.getAddress());
		welearnView.setText(getResources().getText(R.string.welearn));
		imageView.loadImage(lecturer.getProfileImageUrl(), R.dimen.picture_width, R.dimen.picture_height);
	}

	private void hideUnnecessaryViews(Lecturer lecturer) {
		if (lecturer.getTitle().trim().isEmpty()) {
			titleView.setVisibility(View.GONE);
		} else {
			titleView.setVisibility(View.VISIBLE);
		}
		if (lecturer.getPhone().trim().isEmpty()) {
			phoneView.setVisibility(View.GONE);
		} else {
			phoneView.setVisibility(View.VISIBLE);
		}
		if (lecturer.getHomepage() == null) {
			welearnView.setVisibility(View.GONE);
		} else {
			welearnView.setVisibility(View.VISIBLE);
		}
		if (lecturer.getRoomNumber().trim().isEmpty()) {
			roomView.setVisibility(View.GONE);
		} else {
			roomView.setVisibility(View.VISIBLE);
		}
	}
}
