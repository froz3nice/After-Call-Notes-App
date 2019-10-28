package com.braz.prod.DankMemeStickers.VideoMaker;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;

import com.braz.prod.DankMemeStickers.Interfaces.Callback;
import com.braz.prod.DankMemeStickers.util.StorageUtils;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import static com.braz.prod.DankMemeStickers.util.StorageUtils.deleteFile;
import static com.braz.prod.DankMemeStickers.util.StorageUtils.getTempMp3Path;
import static com.braz.prod.DankMemeStickers.util.StorageUtils.writeMp3ToStorage;
import static com.braz.prod.DankMemeStickers.util.Utils.getPath;
import static com.braz.prod.DankMemeStickers.util.Utils.getRealPathFromURI;
import static com.braz.prod.DankMemeStickers.util.Utils.getScreenHeight;
import static com.braz.prod.DankMemeStickers.util.Utils.getScreenWidth;
import static com.braz.prod.DankMemeStickers.util.Utils.getTimeStamp;
import static com.braz.prod.DankMemeStickers.util.VideoUtils.getTimeString;
import static com.braz.prod.DankMemeStickers.util.VideoUtils.getTotalVideoMillis;
import static com.braz.prod.DankMemeStickers.util.VideoUtils.getVideoHeight;


public class VideoMaker {
    private Context context;
    private FFmpeg ffmpeg;
    private WindowManager windowManager;

    public VideoMaker(Context context, WindowManager windowManager) {
        this.context = context;
        this.windowManager = windowManager;
        if (FFmpeg.getInstance(context).isSupported()) {
            ffmpeg = FFmpeg.getInstance(context);
        } else {
            Log.d("FFmpeg message", "not supported");
        }
    }

    public void mergeAudioWithVideo(String fileName, int thugLifeSound, Callback callback) {
        Log.d("merge file name", fileName);
        writeMp3ToStorage(context, thugLifeSound);
        String newFilePath = getPath() + "/" + getTimeStamp() + ".mp4";
        File outputFile = new File(newFilePath);
        //ffmpeg -i video.avi -i audio.mp3 -codec copy -shortest output.avi

        String[] cmd = {"-i", fileName, "-i", getTempMp3Path(), "-codec", "copy", "-shortest", outputFile.getPath()};
        try {
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onProgress(String message) {
                    Log.d("FFmpeg message", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("FFmpeg message", message);

                }

                @Override
                public void onSuccess(String message) {
                    Log.d("FFmpeg message", message);
                    deleteFile(fileName, context);
                    callback.onFinished(outputFile.getPath());
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    public void trimVideo(Integer minValue, Integer maxValue, Uri videoUri, VideoMakerCallback callback) {
        try {
            Log.d("merge file name", videoUri.toString());
            String newFilePath = getPath() + "/" + getTimeStamp() + ".mp4";
            File outputFile = new File(newFilePath);
            File file = new File(getRealPathFromURI(context, videoUri));

            //ffmpeg -i test.wmv -ss 00:03:00 -to 00:03:05 -c copy 1.wmv

            String[] cmd = {"-i", file.getPath(), "-ss", getTimeString(minValue, true), "-to",
                    getTimeString(maxValue, true), "-async",
                    "1", "-c", "copy", outputFile.getPath()};
            Log.d("starterr", getTimeString(minValue, true));
            Log.d("enderr", getTimeString(maxValue, true));

            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onProgress(String message) {
                    Log.d("FFmpeg message", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("FFmpeg message", message);
                    callback.onError();
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("FFmpeg message", message);
                    callback.onSuccess(outputFile.getPath());
                }

            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    public void loadLastFrameOfVideo(String path, VideoMakerCallback callback) {
        try {
            Log.d("file name", path);
            String newFilePath = getPath() + "/" + getTimeStamp() + ".jpg";
            File outputFile = new File(newFilePath);
            //File file = new File(getRealPathFromURI(context,videoUri));

            //ffmpeg -sseof -3 -i input -update 1 -q:v 1 last.jpg

            String[] cmd = {"-sseof", "-3", "-i", path, "-update", "1", "-q:v",
                    "1", outputFile.getPath()};
            Log.d("ipnut file", path);
            Log.d("output file", outputFile.getPath());

            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onProgress(String message) {
                    Log.d("FFmpeg message", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("FFmpeg message", message);
                    callback.onError();
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("FFmpeg message", message);
                    callback.onSuccess(outputFile.getPath());
                }

            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    public void concatenate(String inputFile1, String inputFile2, VideoProgressListener progressListener) {
        Log.d("file1 name", inputFile1);
        Log.d("file2 name", inputFile2);
        Integer input1Height = 0,input2Height =0 ;
        try {
            input1Height = getVideoHeight(inputFile1);
            input2Height = getVideoHeight(inputFile2);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            progressListener.onFailed();
            return ;
        }
        Integer w = getScreenWidth(windowManager);
        Integer h = (input1Height > input2Height) ? input1Height : input2Height;

        String croppedFilePath = getPath() + "/" + getTimeStamp() + "b" + ".mp4";
        //ffmpeg -i YourMovie.mp4 -vf "crop=640:256:0:36" YourCroppedMovie.mp4
        String[] cmd2 = {"-i", inputFile1, "-vf", "crop=" + w + ":" + h + ":0:50", croppedFilePath};

        //File file = new File(getRealPathFromURI(context,videoUri));
        //Log.d("concat", "Concatenating " + inputFile1 + " and " + inputFile2 + " to " + outputFile);
        float density = context.getResources().getDisplayMetrics().density;
        float p = 58.18f * density;
        Log.d("file1height", String.valueOf(getVideoHeight(inputFile1)));
        Log.d("file2height", String.valueOf(getVideoHeight(inputFile2)));

        float p2 = (getScreenHeight(windowManager) - getVideoHeight(inputFile2)) / 2;
        String newFilePath = getPath() + "/" + getTimeStamp() + "a" + ".mp4";
        File outputFile = new File(newFilePath);
        String[] cmd = {"-i",
                inputFile2, "-i", inputFile1, "-filter_complex",
                "[0:v]scale=" + w + ":" + h + ":force_original_aspect_ratio=decrease,pad=" + w + ":" + h + ":0:(oh-ih)/2,setsar=1[v0]; " +
                        "[1:v]scale=" + w + ":" + h + ":force_original_aspect_ratio=decrease,pad=" + w + ":" + h + ":0:" + p + ",setsar=1[v1]; " +
                        " [v0] [0:a] [v1] [1:a] concat=n=2:v=1:a=1 [v] [a]",
                "-map", "[v]", "-map", "[a]", outputFile.getPath()
        };

        try {
            int totalDur = (getTotalVideoMillis(context, Uri.parse(inputFile1)) / 1000) + (getTotalVideoMillis(context, Uri.parse(inputFile2)) / 1000);
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onProgress(String message) {
                    Log.d("FFmpeg progress", message);
                    if(getProgressValue(message,totalDur) != 0f) {
                        float progress = getProgressValue(message, totalDur) / 2;
                        if (progress <= 100)
                            progressListener.onProgress(progress);
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.d("FFmpeg failed", message);
                    progressListener.onFailed();
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("FFmpeg success", message);
                    StorageUtils.deleteFile(inputFile1, context);
                    StorageUtils.deleteFile(inputFile2, context);
                    cropMedia(outputFile.getPath(),".mp4", progressListener,totalDur);
                }

            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    private float getProgressValue(String message,Integer totalDur){
        Pattern timePattern = Pattern.compile("(?<=time=)[\\d:.]*");
        Scanner sc = new Scanner(message);

        String match = sc.findWithinHorizon(timePattern, 0);
        if (match != null) {
            String[] matchSplit = match.split(":");
            if (totalDur != 0) {
                float progress = (Integer.parseInt(matchSplit[0]) * 3600 +
                        Integer.parseInt(matchSplit[1]) * 60 +
                        Float.parseFloat(matchSplit[2])) / totalDur ;
                Log.d("progresss", String.valueOf(progress));
                return (progress * 100) ;
            }
        }
        return 0f;
    }

    public void cropMedia(String path,String mediaSufix, VideoProgressListener callback, int totalDur) {
        try {
            String[] cmd = {"-ss", "0", "-i", path, "-vframes", "10", "-vf", "cropdetect",
                    "-f", "null", "-"};
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d("FFmpeg message", message);
                    Pattern timePattern = Pattern.compile("(?<=crop=)[\\d:.]*");
                    Scanner sc = new Scanner(message);

                    String match = sc.findWithinHorizon(timePattern, 0);
                    if (match != null) {
                        String[] matchSplit = match.split(":");
                        int w = Integer.parseInt(matchSplit[0]);
                        int h = Integer.parseInt(matchSplit[1]);
                        int x = Integer.parseInt(matchSplit[2]);
                        int y = Integer.parseInt(matchSplit[3]);
                        String newFilePath = getPath() + "/" + getTimeStamp() + "c" + mediaSufix;
                        File outputFile = new File(newFilePath);
                        Log.d("output file", outputFile.getPath());
                        // -i input.mp4 -vf crop=1280:720:0:0 -c:a copy output.mp4
                        String[] cmd2 = {"-i", path, "-vf", "crop="+w+":"+h+":"+x+":"+y,
                                "-c:a", "copy", outputFile.getPath()};
                        try {
                            ffmpeg.execute(cmd2,new ExecuteBinaryResponseHandler(){
                                @Override
                                public void onProgress(String message) {
                                    Log.d("FFmpeg message", message);
                                    if(getProgressValue(message,totalDur) != 0f) {
                                        float progress = (getProgressValue(message, totalDur) / 2) + 58;//random 58
                                        if (progress <= 100)
                                            callback.onProgress(progress);
                                    }
                                }

                                @Override
                                public void onFailure(String message) {
                                    Log.d("FFmpeg message", message);
                                }

                                @Override
                                public void onSuccess(String message) {
                                    Log.d("FFmpeg message", message);
                                    StorageUtils.deleteFile(path, context);
                                    callback.onFinished(outputFile.getPath());
                                }
                            });
                        } catch (FFmpegCommandAlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
}