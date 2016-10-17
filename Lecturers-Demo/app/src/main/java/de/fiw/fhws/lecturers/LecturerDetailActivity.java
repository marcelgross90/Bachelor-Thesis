package de.fiw.fhws.lecturers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import de.fiw.fhws.lecturers.adapter.LecturerDetailAdapter;
import de.fiw.fhws.lecturers.model.Lecturer;
import de.fiw.fhws.lecturers.network.ObjectRequest;
import de.fiw.fhws.lecturers.network.VolleySingleton;

public class LecturerDetailActivity extends AppCompatActivity implements View.OnClickListener {
	private Toolbar toolbar;
	private ImageView imageView;
	private RecyclerView recyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lecturer_detail);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		imageView = (ImageView) findViewById(R.id.ivLecturerPicture);
		recyclerView = (RecyclerView) findViewById(R.id.rvLecturerDetails);

		setUpToolbar();


		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setHasFixedSize(true);

		loadLecturer();
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
		String selfUrl = getIntent().getExtras().getString("selfUrl");
		ObjectRequest objectRequest = new ObjectRequest(selfUrl, Request.Method.GET, null, this, new ObjectRequest.ObjectRequestListener() {
			@Override
			public void onResponse(Lecturer lecturer) {
				setUp(lecturer);
			}

			@Override
			public void onError(VolleyError error) {
				//todo extract string
				Toast.makeText(LecturerDetailActivity.this, "Fehler", Toast.LENGTH_LONG).show();
			}
		});

		objectRequest.sendRequest();
	}

	private void setUp(Lecturer lecturer) {
		displayLecturerPicture(lecturer.getUrlProfileImage());
		recyclerView.setAdapter(new LecturerDetailAdapter(lecturer, LecturerDetailActivity.this));
	}

	private void displayLecturerPicture(String pictureUrl) {
		ImageRequest request = new ImageRequest(pictureUrl,
				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap bitmap) {
						Bitmap editedBitmap = Bitmap.createBitmap(bitmap, 0, 50, bitmap.getWidth(), 300);
						BitmapDrawable drawable = new BitmapDrawable(getResources(), editedBitmap);
						imageView.setImageDrawable(drawable);
					}
				}, 0, 0, null, null,
				new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						//todo show error
					}
				});
		VolleySingleton.getInstance(this).addToRequestQueue(request);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				//todo animation
			//	overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View view) {
		/*switch (view.getId()) {
			case R.id.tvEmailValue:
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + lecturer.getEmail()));
				startActivity(Intent.createChooser(intent, "Send Email"));
				break;

			case R.id.tvPhoneValue:
				Intent intent1 = new Intent(Intent.ACTION_DIAL);
				String p = "tel:" + lecturer.getPhone();
				intent1.setData(Uri.parse(p));
				startActivity(intent1);
				break;

			case R.id.tvWebsiteValue:
				Intent intent2 = new Intent(Intent.ACTION_VIEW);
				intent2.setData(Uri.parse(lecturer.getUrlWelearn()));
				startActivity(intent2);
				break;

			case R.id.tvAddressValue:
				Uri location = Uri.parse("geo:0,0?q=" + lecturer.getAddress());
				Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
				mapIntent.setPackage("com.google.android.apps.maps");
				startActivity(mapIntent);
				break;

		}*/
	}
}