package de.fiw.fhws.lecturers.customView;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import de.fiw.fhws.lecturers.R;
import de.fiw.fhws.lecturers.model.Link;

public class ProfileImageView extends ImageView {

	private final Context context;

	public ProfileImageView(Context context) {
		super(context);
		this.context = context;
		init(context, null, 0);

	}

	public ProfileImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context, attrs, 0);
	}

	public ProfileImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init(context, attrs, defStyle);
	}


	private void init(Context context, AttributeSet attrs, int defStyle) {

		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PhoneView, defStyle, 0);
		typedArray.recycle();
	}

	public void loadImage(Link profileImage, int width, int height) {
		String profileImageUrl = getValidUrl(profileImage);

		Picasso.with(context).load(profileImageUrl).resizeDimen(width, height).error(R.drawable.user_picture).into(this);
	}

	public void loadImage(Link profileImage) {
		String profileImageUrl = getValidUrl(profileImage);

		Picasso.with(context).load(profileImageUrl).error(R.drawable.user_picture).into(this);
	}

	public void loadCuttedImage(Link profileImage) {
		String profileImageUrl = getValidUrl(profileImage);
		Target target = new Target()
		{
			@Override
			public void onPrepareLoad( Drawable placeHolderDrawable ) {}

			@Override
			public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from )
			{
				Bitmap editedBitmap = Bitmap.createBitmap( bitmap, 0, 50, bitmap.getWidth(), 300 );
				BitmapDrawable drawable = new BitmapDrawable( getResources(), editedBitmap );
				setImageDrawable( drawable );
			}

			@Override
			public void onBitmapFailed( Drawable errorDrawable ) {}
		};

		Picasso.with(context).load(profileImageUrl).error(R.drawable.user_picture).into(target);
	}

	private String getValidUrl(Link profileImageLink) {
		if (profileImageLink != null)
			return profileImageLink.getHrefWithoutQueryParams();
		return "empty";
	}
}
