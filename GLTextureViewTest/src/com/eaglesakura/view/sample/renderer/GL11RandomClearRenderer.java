package com.eaglesakura.view.sample.renderer;

import static junit.framework.Assert.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.eaglesakura.view.GLTextureView.Renderer;

public class GL11RandomClearRenderer implements Renderer {

    static final String TAG = GL11RandomClearRenderer.class.getSimpleName();

    public int created = 0;
    public int changed = 0;
    public int drawing = 0;
    public int destroyed = 0;

    public GL11RandomClearRenderer() {
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");
        ++created;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, String.format("onSurfaceChanged(%d x %d)", width, height));
        ++changed;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(0, (float) Math.random(), (float) Math.random(), 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // call ES 1.1 API
        assertTrue(gl.glGetError() == GL10.GL_NO_ERROR);
        gl.glRotatef(0, 1, 0, 0);
        assertTrue(gl.glGetError() == GL10.GL_NO_ERROR);

        ++drawing;
    }

    @Override
    public void onSurfaceDestroyed(GL10 gl) {
        Log.d(TAG, String.format("onSurfaceDestroyed"));

        ++destroyed;
    }

}
