package com.eaglesakura.view.sample.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.eaglesakura.view.GLTextureView;
import com.eaglesakura.view.sample.R;
import com.eaglesakura.view.sample.R.id;
import com.eaglesakura.view.sample.R.layout;

public class RenderingActivity extends Activity {

    static final String TAG = RenderingActivity.class.getSimpleName();

    View contentView;
    GLTextureView glTextureView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentView = View.inflate(this, R.layout.layout_gltextureview, null);
        glTextureView = (GLTextureView) contentView.findViewById(R.id.view);
    }

    public GLTextureView getGLTextureView() {
        return glTextureView;
    }

    public void setupView() {
        setContentView(contentView);
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
