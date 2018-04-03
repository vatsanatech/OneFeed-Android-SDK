package com.wittyfeed.sdk.onefeed;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;


/**
 * Created by aishwarydhare on 09/11/17.
 */

@GlideModule(glideName = "WittyGlide")
public class WittyFeedSDKGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
//        builder.setLogLevel(Log.ERROR);
        int memoryCacheSizeBytes = 1024 * 1024 * 50; // 50mb
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context));
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
        super.applyOptions(context, builder);
    }
}
