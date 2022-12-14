package com.aakash.imageandvideopicker.videofilter.model;

import android.content.res.AssetFileDescriptor;

public class AssetsMetadataExtractor extends MetadataExtractor {

    public Metadata extract(AssetFileDescriptor assetFileDescriptor) {
        retriever.setDataSource(
                assetFileDescriptor.getFileDescriptor(),
                assetFileDescriptor.getStartOffset(),
                assetFileDescriptor.getLength()
        );
        return extractMetadata();
    }

}
