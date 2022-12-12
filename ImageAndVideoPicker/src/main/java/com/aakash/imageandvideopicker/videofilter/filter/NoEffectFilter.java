package com.aakash.imageandvideopicker.videofilter.filter;

import android.util.Log;

import com.aakash.imageandvideopicker.videofilter.Constants;
import com.aakash.imageandvideopicker.videofilter.interfaces.Filter;

public class NoEffectFilter implements Filter {

    private static final String TAG = "NoEffectFilter";

    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;\n"
            + "varying vec2 vTextureCoord;\n"
            + "uniform samplerExternalOES sTexture;\n" + "void main() {\n"
            + "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n"
            + "}\n";

    @Override
    public String getVertexShader() {
        return Constants.DEFAULT_VERTEX_SHADER;
    }

    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    @Override
    public void setIntensity(float intensity) {
        Log.d(TAG,"No effect");
    }
}