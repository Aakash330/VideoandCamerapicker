package com.aakash.imageandvideopicker.videofilter.model;

import android.content.res.AssetFileDescriptor;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.M)
public class AssetsConverter extends Converter {

    public AssetsConverter(AssetFileDescriptor assetFileDescriptor) {
        setMetadata(assetFileDescriptor);
        try {
            videoExtractor.setDataSource(
                    assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength());
            audioExtractor.setDataSource(
                    assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMetadata(AssetFileDescriptor assetFileDescriptor) {
        Metadata metadata = new AssetsMetadataExtractor().extract(assetFileDescriptor);
        if (metadata != null) {
            width = (int) metadata.getWidth();
            height = (int) metadata.getHeight();
            bitrate = metadata.getBitrate();
        }
    }
}
