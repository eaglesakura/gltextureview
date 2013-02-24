package com.eaglesakura.view.sample;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.eaglesakura.view.GLTextureView;
import com.eaglesakura.view.GLTextureView.Renderer;

public class RenderingActivity extends Activity {

    static final String TAG = RenderingActivity.class.getSimpleName();

    GLTextureView glTextureView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_gltextureview);

        glTextureView = (GLTextureView) findViewById(R.id.view);
        glTextureView.setRenderer(new Renderer() {

            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                Log.d(TAG, "onSurfaceCreated");
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                Log.d(TAG, String.format("onSurfaceChanged(%d x %d)", width, height));
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                gl.glClearColor(0, 1.0f, 1.0f, 1.0f);
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            }

            @Override
            public void onSurfaceDestroyed(GL10 gl) {
                Log.d(TAG, String.format("onSurfaceDestroyed"));
            }

        });
    }

    @Override
    protected void onPause() {
        glTextureView.onPause();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        glTextureView.onResume();
    }
}
