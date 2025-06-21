package com.ea.utils;

import com.ea.config.GlobalVariables;
import lombok.Getter;
import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class ScreenRecorderUtil extends ScreenRecorder {

    public static ScreenRecorder screenRecorder;
    @Getter
    private static String filename;
    @Getter
    private static String filePath;

    public ScreenRecorderUtil(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
                              Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder, String name)
            throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
        this.filename = name;
    }

    @Override
    protected File createMovieFile(Format fileFormat) {

        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        } else if (!movieFolder.isDirectory()) {
            try {
                throw new IOException("\"" + movieFolder + "\" is not a directory.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        File f = new File(movieFolder,
                filename + "-" + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));
        filePath = f.getAbsolutePath();
        return f;
    }

    public static void stopRecord() {
        stopRecord(true);
    }

    public static void startRecord(String relativeFolderInReport, String methodName, boolean canRecordTest) {
        if (GlobalVariables.CONFIG.recordingFlag() && canRecordTest) {
            File file = new File(GlobalVariables.CONFIG.reportPath()+GlobalVariables.CONFIG.evidenceFolder()+relativeFolderInReport);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = screenSize.width;
            int height = screenSize.height;

            Rectangle captureSize = new Rectangle(0, 0, width, height);

            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
                    getDefaultScreenDevice()
                    .getDefaultConfiguration();
            try {
                screenRecorder = new ScreenRecorderUtil(gc, captureSize,
                        new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                                CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
                                Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                        null, file, methodName);
                screenRecorder.start();
            } catch (IOException | AWTException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void stopRecord(boolean canRecordTest) {
        if (GlobalVariables.CONFIG.recordingFlag() && canRecordTest) {
            try {
                screenRecorder.stop();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
