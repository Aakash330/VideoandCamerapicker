package com.aakash.videobasesamplegpuv;

import com.daasuu.gpuv.egl.filter.GlFilter;

public interface FilterAdjuster {
    public void adjust(GlFilter filter, int percentage);
}
