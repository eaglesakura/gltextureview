package com.eaglesakura.view;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.TextureView;

import com.eaglesakura.view.egl.EGLManager;

public class GLTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    /**
     * callback object
     */
    protected Renderer renderer = null;

    /**
     * OpenGL ES Version.
     */
    GLESVersion version = GLESVersion.OpenGLES11;

    /**
     * 
     */
    EGLManager eglManager = new EGLManager();

    /**
     * ConfigChooser
     */
    EGLConfigChooser eglConfigChooser = null;

    /**
     * Rendering Thread
     */
    Handler renderingHandler = null;

    public GLTextureView(Context context) {
        super(context);
    }

    public GLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GLTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 
     * @param renderer
     */
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    /**
     * 
     * @param version
     */
    public void setVersion(GLESVersion version) {
        this.version = version;
    }

    /**
     * Config Chooser
     * @param eglConfigChooser
     */
    public void setEglConfigChooser(EGLConfigChooser eglConfigChooser) {
        this.eglConfigChooser = eglConfigChooser;
    }

    /**
     * change Rendering Thread
     * @param renderingHandler
     */
    public void setRenderingThread(Handler renderingHandler) {
        this.renderingHandler = renderingHandler;
    }

    /**
     * post Rendering thread
     */
    public void requestRendering() {
        // TODO 実装する
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // TODO Auto-generated method stub

        // auto release
        return true;
    }

    /**
     * 
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    /**
     * 
     */
    protected void onRendering() {
        // TODO 実装
    }

    /**
     * OpenGL ES Version
     */
    public enum GLESVersion {
        /**
         * OpenGL ES 1.0
         */
        OpenGLES11,

        /**
         * OpenGL ES 2.0
         */
        OpenGLES20,
    }

    /**
     * 
     */
    public interface EGLConfigChooser {

        /**
         * 
         * @return
         */
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, GLESVersion version);
    }

    /**
     * 
     */
    public interface Renderer extends GLSurfaceView.Renderer {
        /**
         * created EGLSurface.
         * {@link #onSurfaceChanged(GL10, int, int)}
         */
        public void onSurfaceCreated(GL10 gl, EGLConfig config);

        /**
         * remake EGLSurface.
         */
        public void onSurfaceChanged(GL10 gl, int width, int height);

        /**
         * rendering.
         */
        public void onDrawFrame(GL10 gl);
    }

}
