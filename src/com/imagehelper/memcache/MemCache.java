package com.imagehelper.memcache;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.imagehelper.diskcache.DiskLruCache;

/**
 * There are three states for one image life cycle :
 * <ul>
 * <li>Live in memory
 * <dd>If there are reference in the memory, the image is live in the memory in
 * {@link ZombieDrawable} style.</dd></li>
 * <li>Dead
 * <dd>There are two states for a image bitmap dead state.If the image is
 * invaluable, like out of data, it is dead forever, and should be removed from
 * both disk and memory cache. Here, we're only concerned the memory cache;
 * otherwise, in the future it maybe used once more, so we just move it to dead
 * cache</dd></li>
 * <li>Store on disk.
 * <dd>There are also two states for a image bitmap. As the Dead state, if a
 * image is invaluable, it should be removed from disk, And new image should
 * replace it or not.</dd></li>
 * </ul>
 * 
 * @see {@link DiskLruCache}
 */
public class MemCache {
	/**
	 * Add the specific drawable to live cache.
	 * 
	 * @param url
	 *            the drawable's url.
	 * @param d
	 *            the drawable, and it should not be null object.
	 */
	private void addDrawableToLiveCache(String url, Drawable d) {
	}

	/**
	 * According the drawable's url, delete it from memory.
	 * 
	 * @param url
	 *            the drawable's url.
	 */
	private void deleteDrawableFromLiveCache(String url) {
	}

	/**
	 * Add the specific bitmap with url to dead cache.
	 * 
	 * @param url
	 *            the bitmap's url.
	 * @param b
	 *            the bitmap.
	 */
	private void addBitmapToDeadCache(String url, Bitmap b) {
	}

	public class ZombieDrawable extends BitmapDrawable implements Cloneable {

		class Brain {
			/**
			 * Indicates the number of zombies with the same drawable.
			 */
			int mRefCounter;
			/**
			 * Indicates whether the drawable should be discarded or not. If
			 * true, the drawable should be removed from both memory cache and
			 * disk cache.
			 */
			boolean mHeadshot;
		}

		private String mUrl;
		private Brain mBrain;

		public ZombieDrawable(Resources resources, String url, Bitmap bitmap) {
			super(resources, bitmap);
			mUrl = url;
			mBrain = new Brain();
			mBrain.mRefCounter++;
			// When an object of Zombie is instantiated, it should be added to
			// the memory cache.
			addDrawableToLiveCache(url, this);
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			// An new instance will be generated, so increase the counter size.
			mBrain.mRefCounter++;
			return this;
		}

		/**
		 * Kill the zombie forever. And it's an irreversible operation. One
		 * condition to call {{@link #headshot()} is that the drawable is out of
		 * date.
		 */
		public void headshot() {
			mBrain.mHeadshot = true;
			deleteDrawableFromLiveCache(mUrl);
		}

		@Override
		protected void finalize() throws Throwable {
			super.finalize();

			mBrain.mRefCounter--;
			if (mBrain.mRefCounter <= 0) {
				// If there are no reference for current drawable and the GC
				// wants to recycle this memory, and the drawable is not been
				// head shot, the drawable is likely to be removed from the
				// cache.
				if (!mBrain.mHeadshot) {
					// TODO: One thing should be clear, when the gc is going to
					// recycle the bitmap drawable memory, how does the memory
					// is allocated to store bitmap, or say does the bitmap
					// exists after gc recycle this drawable. Here, we assume
					// that the bitmap will continue exist. So, the bitmap
					// should be put into DeadCache.
					addBitmapToDeadCache(mUrl, getBitmap());
				}
				// The drawable should be deleted from live cache.
				deleteDrawableFromLiveCache(mUrl);
			}
		}
	}
}
