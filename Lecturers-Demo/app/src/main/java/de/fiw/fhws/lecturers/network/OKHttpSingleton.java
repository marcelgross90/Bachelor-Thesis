package de.fiw.fhws.lecturers.network;


import android.content.Context;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class OKHttpSingleton {

	private static OKHttpSingleton instance;

	private final OkHttpClient client;

	public static OKHttpSingleton getInstance(Context context) {
		if (instance == null) {
			instance = new OKHttpSingleton(context);
		}

		return instance;
	}

	public OkHttpClient getClient() {
		return client;
	}

	private OKHttpSingleton(Context context) {
		int cacheSize = (int) ((Runtime.getRuntime().maxMemory() / 1024) / 8);
		Cache cache = new Cache(context.getCacheDir(), cacheSize);

		client = new OkHttpClient.Builder()
				.cache(cache)
				.build();
	}

}
