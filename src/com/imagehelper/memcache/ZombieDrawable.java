package com.imagehelper.memcache;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public abstract class ZombieDrawable extends BitmapDrawable implements Cloneable {

	static class Brain {
		/**
		 * Indicates the number of zombies with the same drawable.
		 */
		int mRefCounter;
		/**
		 * Indicates whether the drawable should be discarded or not. If true,
		 * the drawable should be removed from both memory cache and disk cache.
		 */
		boolean mHeadshot;
	}

	private String mUrl;
	private Brain mBrain;
	private MemCache mCache;

	public ZombieDrawable(Resources resources, String url, Bitmap bitmap, MemCache cache) {
		super(resources, bitmap);
		mUrl = url;
		mBrain = new Brain();
		mBrain.mRefCounter++;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// An new instance will be generated, so increase the counter size.
		mBrain.mRefCounter++;
		return this;
	}

	/**
	 * Kill the zombie forever. And it's an irreversible operation.
	 */
	public void headshot() {
		mBrain.mHeadshot = true;
	}
}
