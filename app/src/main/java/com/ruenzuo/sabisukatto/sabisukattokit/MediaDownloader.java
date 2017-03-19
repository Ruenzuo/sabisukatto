package com.ruenzuo.sabisukatto.sabisukattokit;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Pair;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.VideoInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.os.Environment.DIRECTORY_MOVIES;

/**
 * Created by ruenzuo on 19/03/2017.
 */

public class MediaDownloader {

    private Context context;

    public MediaDownloader(Context context) {
        this.context = context;
    }

    public Completable downloadMedia(Uri uri) {
        return Completable.fromSingle(getTweet(uri).flatMap(new Function<Tweet, SingleSource<Uri>>() {
            @Override
            public SingleSource<Uri> apply(@NonNull Tweet tweet) throws Exception {
                return getMediaUri(tweet).subscribeOn(Schedulers.io());
            }
        }).flatMap(new Function<Uri, SingleSource<Pair<String, InputStream>>>() {
            @Override
            public SingleSource<Pair<String, InputStream>> apply(@NonNull Uri uri) throws Exception {
                return getFile(uri).subscribeOn(Schedulers.io());
            }
        }).flatMap(new Function<Pair<String, InputStream>, SingleSource<File>>() {
            @Override
            public SingleSource<File> apply(@NonNull Pair<String, InputStream> pair) throws Exception {
                return saveFile(pair).subscribeOn(Schedulers.io());
            }
        }));
    }

    private Single<Tweet> getTweet(final Uri tweetUri) {
        return Single.create(new SingleOnSubscribe<Tweet>() {
            @Override
            public void subscribe(final SingleEmitter<Tweet> emitter) throws Exception {
                String tweetID = tweetUri.getLastPathSegment();
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                final Call<Tweet> call = twitterApiClient.getStatusesService().show(new Long(tweetID), true, false, true);
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        call.cancel();
                    }
                });
                call.enqueue(new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        emitter.onSuccess(result.data);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        emitter.onError(exception);
                    }
                });
            }
        });
    }

    private Single<Uri> getMediaUri(final Tweet tweet) {
        return Single.fromCallable(new Callable<Uri>() {
            @Override
            public Uri call() throws Exception {
                List<MediaEntity> mediaEntities = tweet.extendedEtities.media;
                for (MediaEntity mediaEntity : mediaEntities) {
                    if (mediaEntity.type.equalsIgnoreCase("animated_gif")) {
                        List<VideoInfo.Variant> variants = mediaEntity.videoInfo.variants;
                        String contentType = variants.get(0).contentType;
                        if (contentType.equalsIgnoreCase("video/mp4")) {
                            String url = variants.get(0).url;
                            return Uri.parse(url);
                        }
                    }
                }
                throw new Resources.NotFoundException();
            }
        });
    }

    private Single<Pair<String, InputStream>> getFile(final Uri videoUri) {
        return Single.create(new SingleOnSubscribe<Pair<String, InputStream>>() {
            @Override
            public void subscribe(final SingleEmitter<Pair<String, InputStream>> emitter) throws Exception {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.rubesty.com/")
                        .build();
                final String identifier = videoUri.getLastPathSegment();
                MediaDownloadService mediaDownloadService = retrofit.create(MediaDownloadService.class);
                final Call<ResponseBody> call = mediaDownloadService.downloadMedia(videoUri.toString());
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        call.cancel();
                    }
                });
                call.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        emitter.onSuccess(new Pair<String, InputStream>(identifier, response.body().byteStream()));
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        emitter.onError(t);
                    }
                });
            }
        });
    }

    private Single<File> saveFile(final Pair<String, InputStream> pair) {
        return Single.fromCallable(new Callable<File>() {
            @Override
            public File call() throws Exception {
                File externalDirectory = context.getExternalFilesDir(DIRECTORY_MOVIES);
                File file = new File(externalDirectory + File.separator + pair.first);
                byte[] fileReader = new byte[4096];
                InputStream inputStream = pair.second;
                OutputStream outputStream = new FileOutputStream(file);
                int read;
                while ((read = inputStream.read(fileReader)) != -1) {
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return file;
            }
        });
    }

}
