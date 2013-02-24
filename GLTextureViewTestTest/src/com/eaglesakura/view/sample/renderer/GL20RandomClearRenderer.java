package com.eaglesakura.view.sample.renderer;

import static junit.framework.Assert.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.util.Log;

import com.eaglesakura.view.GLTextureView.Renderer;

public class GL20RandomClearRenderer implements Renderer {

    static final String TAG = GL20RandomClearRenderer.class.getSimpleName();

    public int created = 0;
    public int changed = 0;
    public int drawing = 0;
    public int destroyed = 0;

    private int shader_object = 0;

    public GL20RandomClearRenderer() {
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");
        ++created;

        // call ES 2.0 API
        shader_object = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        assertTrue(GLES20.glGetError() == GLES20.GL_NO_ERROR);
        assertTrue(shader_object != 0);

        assertNull(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, String.format("onSurfaceChanged(%d x %d)", width, height));
        ++changed;

        assertNull(gl);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0, (float) Math.random(), (float) Math.random(), 1.0f);
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        ++drawing;

        assertNull(gl);
    }

    @Override
    public void onSurfaceDestroyed(GL10 gl) {
        Log.d(TAG, String.format("onSurfaceDestroyed"));

        GLES20.glDeleteShader(shader_object);
        assertTrue(GLES20.glGetError() == GLES20.GL_NO_ERROR);
        ++destroyed;

        assertNull(gl);
    }

}
