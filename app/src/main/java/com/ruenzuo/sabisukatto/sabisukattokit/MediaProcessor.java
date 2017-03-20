package com.ruenzuo.sabisukatto.sabisukattokit;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.ruenzuo.sabisukatto.sabisukattokit.gif.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Single;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by ruenzuo on 19/03/2017.
 */

public class MediaProcessor {

    private static final String TAG = MediaProcessor.class.getName();

    private Context context;

    public MediaProcessor(Context context) {
        this.context = context;
    }

    public Single<Pair<String, ByteArrayOutputStream>> processMedia(final File file) {
        return Single.fromCallable(new Callable<Pair<String, ByteArrayOutputStream>>() {
            @Override
            public Pair<String, ByteArrayOutputStream> call() throws Exception {
                Uri fileUri = Uri.parse(file.toString());
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(context, fileUri);
                ArrayList<Bitmap> frames = new ArrayList<>();
                MediaPlayer player = MediaPlayer.create(context, fileUri);
                int fps = 24;
                int quality = 5;
                int length = player.getDuration();
                double lengthInSeconds = (double)length / 1000.0;
                Log.v(TAG, String.format("Video length (value): %.3f seconds", lengthInSeconds));
                int requiredFrames = (int) (fps * lengthInSeconds);
                Log.v(TAG, String.format("Required frames: %d frames", requiredFrames));
                double stepInUSeconds = length * 1000.0 / requiredFrames;
                Log.v(TAG, String.format("Step: %.3f microseconds", stepInUSeconds));
                int currentTime = 0;
                for (int i = 0; i < requiredFrames; i++) {
                    Bitmap bitmap = retriever.getFrameAtTime(currentTime, MediaMetadataRetriever.OPTION_CLOSEST);
                    if (bitmap != null) {
                        frames.add(bitmap);
                    }
                    currentTime += stepInUSeconds;
                }
                Encoder encoder = new Encoder();
                encoder.setRepeat(0);
                encoder.setQuality(quality);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                if (!encoder.start(outputStream)) {
                    throw new Exception();
                }
                for (Bitmap bitmap : frames) {
                    if (!encoder.addFrame(bitmap)) {
                        throw new Exception();
                    }
                    encoder.setDelay((int) (stepInUSeconds / 1000));
                }
                encoder.finish();
                return new Pair<String, ByteArrayOutputStream>(fileUri.getLastPathSegment().replace(".mp4", ".gif"), outputStream);
            }
        });
    }

    public Single<File> saveFile(final Pair<String, ByteArrayOutputStream> pair) {
        return Single.fromCallable(new Callable<File>() {
            @Override
            public File call() throws Exception {
                File externalDirectory = context.getExternalFilesDir(DIRECTORY_PICTURES);
                File file = new File(externalDirectory + File.separator + pair.first);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(pair.second.toByteArray());
                fileOutputStream.close();
                return file;
            }
        });
    }

}
