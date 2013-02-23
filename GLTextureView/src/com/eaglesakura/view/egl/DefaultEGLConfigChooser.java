package com.eaglesakura.view.egl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import com.eaglesakura.view.GLTextureView.EGLConfigChooser;
import com.eaglesakura.view.GLTextureView.GLESVersion;

public class DefaultEGLConfigChooser implements EGLConfigChooser {

    /**
     * 
     */
    SurfaceColorSpec mColorSpec = SurfaceColorSpec.RGBA8;

    /**
     * use depth buffer
     */
    boolean mDepthEnable = true;

    /**
     * use stencil buffer
     */
    boolean mStencilEnable = false;

    public DefaultEGLConfigChooser() {
    }

    /**
     * 
     * @param colorSpec
     */
    public void setColorSpec(SurfaceColorSpec colorSpec) {
        this.mColorSpec = colorSpec;
    }

    /**
     * 
     * @param depthEnable
     */
    public void setDepthEnable(boolean depthEnable) {
        this.mDepthEnable = depthEnable;
    }

    /**
     * 
     * @param stencilEnable
     */
    public void setStencilEnable(boolean stencilEnable) {
        this.mStencilEnable = stencilEnable;
    }

    @Override
    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, GLESVersion version) {
        return null;
    }

    private int[] getConfigSpec(GLESVersion version) {
        final int redSize = mColorSpec.getRedSize();
        final int blueSize = mColorSpec.getBlueSize();
        final int greenSize = mColorSpec.getGreenSize();
        final int alphaSize = mColorSpec.getAlphaSize();
        final int depthSize = mDepthEnable ? 16 : 0;
        final int stencilSize = mStencilEnable ? 8 : 0;
        List<Integer> result = new ArrayList<Integer>();

        if (version == GLESVersion.OpenGLES20) {
            // set ES 2.0
            result.add(EGL10.EGL_RENDERABLE_TYPE);
            result.add(4); /* EGL_OPENGL_ES2_BIT */
        }

        result.add(EGL10.EGL_RED_SIZE);
        result.add(redSize);
        result.add(EGL10.EGL_GREEN_SIZE);
        result.add(greenSize);
        result.add(EGL10.EGL_BLUE_SIZE);
        result.add(blueSize);

        if (alphaSize > 0) {
            result.add(EGL10.EGL_ALPHA_SIZE);
            result.add(alphaSize);
        }
        if (depthSize > 0) {
            result.add(EGL10.EGL_DEPTH_SIZE);
            result.add(depthSize);
        }

        if (stencilSize > 0) {
            result.add(EGL10.EGL_STENCIL_SIZE);
            result.add(stencilSize);
        }

        // End
        result.add(EGL10.EGL_NONE);

        int[] result_array = new int[result.size()];
        for (int i = 0; i < result.size(); ++i) {
            result_array[i] = result.get(i);
        }
        return result_array;
    }

    private static int getConfigAttrib(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig, int attr) {
        int[] value = new int[1];
        egl.eglGetConfigAttrib(eglDisplay, eglConfig, attr, value);
        return value[0];
    }

}
