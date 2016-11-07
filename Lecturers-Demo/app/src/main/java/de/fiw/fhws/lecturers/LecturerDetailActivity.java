package de.fiw.fhws.lecturers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.owlike.genson.Genson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.fiw.fhws.lecturers.adapter.LecturerDetailAdapter;
import de.fiw.fhws.lecturers.customView.ProfileImageView;
import de.fiw.fhws.lecturers.fragment.DeleteDialogFragment;
import de.fiw.fhws.lecturers.model.Lecturer;
import de.fiw.fhws.lecturers.model.Link;
import de.fiw.fhws.lecturers.network.util.HeaderParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LecturerDetailActivity extends AppCompatActivity implements View.OnClickListener {
	private final Genson genson = new Genson();
	private Link deleteLink;
	private Toolbar toolbar;
	private ProfileImageView imageView;
	private RecyclerView recyclerView;
	private Lecturer currentLecturer;
	private static LecturerDetailActivity activity;

	public static void startMainActivity() {
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
		activity.finish();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lecturer_detail);

		activity = this;

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		imageView = (ProfileImageView) findViewById(R.id.ivLecturerPicture);
		recyclerView = (RecyclerView) findViewById(R.id.rvLecturerDetails);

		setUpToolbar();

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setHasFixedSize(true);

		loadLecturer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.lecturer_menu, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
				return true;
			case R.id.edit_lecturer:
				Intent intent = new Intent(LecturerDetailActivity.this, EditLecturerActivity.class);
				intent.putExtra("url", currentLecturer.getSelf().getHref());
				intent.putExtra("mediaType", currentLecturer.getSelf().getType());
				startActivityForResult(intent, 1);
				return true;
			case R.id.delete_lecturer:
				Bundle bundle = new Bundle();
				bundle.putString("url", deleteLink.getHref());
				bundle.putString("name", currentLecturer.getFirstName() + " " + currentLecturer.getLastName());
				DeleteDialogFragment deleteDialogFragment = new DeleteDialogFragment();
				deleteDialogFragment.setArguments(bundle);
				deleteDialogFragment.show(getSupportFragmentManager(), null);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	private void setUpToolbar() {
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		ActionBar actionBar = getSupportActionBar();

		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		String fullName = getIntent().getExtras().getString("fullName");
		setTitle(fullName);
	}

	private void loadLecturer() {
		Intent intent = getIntent();
		String selfUrl = intent.getExtras().getString("selfUrl");
		String mediaType = intent.getExtras().getString("mediaType");


		final Request request = new Request.Builder()
				.header("Accept", mediaType)
				.url(selfUrl)
				.build();

		OkHttpClient client = new OkHttpClient();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				if (!response.isSuccessful()) {
					throw new IOException("Unexpected code " + response);
				}
				final Lecturer lecturer = genson.deserialize(response.body().charStream(), Lecturer.class);
				currentLecturer = lecturer;
				Map<String, List<String>> headers = response.headers().toMultimap();
				Map<String, Link> linkHeader = HeaderParser.getLinks(headers.get("link"));
				deleteLink = linkHeader.get("deleteLecturer");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setUp(lecturer);
					}
				});
			}
		});
	}

	private void setUp(Lecturer lecturer) {
		LecturerDetailAdapter adapter = new LecturerDetailAdapter(LecturerDetailActivity.this);
		adapter.addLecturer(lecturer);
		if (lecturer.getProfileImageUrl() != null)
			displayLecturerPicture(lecturer.getProfileImageUrl().getHrefWithoutQueryParams());
		recyclerView.setAdapter(adapter);
	}

	private void displayLecturerPicture(String pictureUrl) {
		/*Target target = new Target() {
			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {
			}

			@Override
			public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
				Bitmap editedBitmap = Bitmap.createBitmap(bitmap, 0, 50, bitmap.getWidth(), 300);
				BitmapDrawable drawable = new BitmapDrawable(getResources(), editedBitmap);
				imageView.setImageDrawable(drawable);
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {
			}
		};
		Picasso.with(this).load(pictureUrl).into(target);*/
		imageView.loadImage(currentLecturer.getProfileImageUrl());
		//Picasso.with(this).load(pictureUrl).into(imageView);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tvEmailValue:
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + currentLecturer.getEmail()));
				startActivity(Intent.createChooser(intent, "Send Email"));
				break;

			case R.id.tvPhoneValue:
				Intent intent1 = new Intent(Intent.ACTION_DIAL);
				String p = "tel:" + currentLecturer.getPhone();
				intent1.setData(Uri.parse(p));
				startActivity(intent1);
				break;

			case R.id.tvWebsiteValue:
				Intent intent2 = new Intent(Intent.ACTION_VIEW);
				intent2.setData(Uri.parse(currentLecturer.getUrlWelearn()));
				startActivity(intent2);
				break;

			case R.id.tvAddressValue:
				Uri location = Uri.parse("geo:0,0?q=" + currentLecturer.getAddress());
				Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
				mapIntent.setPackage("com.google.android.apps.maps");
				startActivity(mapIntent);
				break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			loadLecturer();
		}
	}
}