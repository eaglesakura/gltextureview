package com.eaglesakura.view;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.TextureView;

import com.eaglesakura.view.egl.DefaultEGLConfigChooser;
import com.eaglesakura.view.egl.EGLManager;

/**
 * {@link SurfaceView} -> {@link TextureView} OpenGL ES 1.1 or OpenGL ES 2.0
 */
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
    EGLManager eglManager = null;

    /**
     * ConfigChooser
     */
    EGLConfigChooser eglConfigChooser = null;

    /**
     * rendering thread
     */
    RenderingThreadType renderingThreadType = RenderingThreadType.BackgroundThread;

    /**
     * lock object
     */
    protected final Object lock = new Object();

    /**
     * GL Object
     */
    GL11 gl11;

    /**
     * 
     */
    Thread backgroundThread = null;

    /**
     * Surface Destroyed
     */
    boolean destroyed = false;

    /**
     * surface texture width
     */
    int surfaceWidth = 0;

    /**
     * surface texture height
     */
    int surfaceHeight = 0;

    public GLTextureView(Context context) {
        super(context);
        setSurfaceTextureListener(this);
    }

    public GLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
    }

    public GLTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSurfaceTextureListener(this);
    }

    protected boolean isInitialized() {
        return eglManager != null;
    }

    /**
     * 
     * @param renderer
     */
    public void setRenderer(Renderer renderer) {
        synchronized (lock) {
            if (isInitialized()) {
                throw new UnsupportedOperationException("GLTextureView Initialized");
            }
            this.renderer = renderer;
        }
    }

    /**
     * 
     * @param version
     */
    public void setVersion(GLESVersion version) {
        synchronized (lock) {
            if (isInitialized()) {
                throw new UnsupportedOperationException("GLTextureView Initialized");
            }
            this.version = version;
        }
    }

    /**
     * Config Chooser
     * @param eglConfigChooser
     */
    public void setEglConfigChooser(EGLConfigChooser eglConfigChooser) {
        synchronized (lock) {
            if (isInitialized()) {
                throw new UnsupportedOperationException("GLTextureView Initialized");
            }
            this.eglConfigChooser = eglConfigChooser;
        }
    }

    /**
     * 
     * @param renderingThreadType
     */
    public void setRenderingThreadType(RenderingThreadType renderingThreadType) {
        synchronized (lock) {
            if (isInitialized()) {
                throw new UnsupportedOperationException("GLTextureView Initialized");
            }

            this.renderingThreadType = renderingThreadType;
        }
    }

    /**
     * start rendering
     * call {@link GLTextureView#onRendering()}
     * 
     */
    public void requestRender() {
        synchronized (lock) {
            if (!isInitialized()) {
                throw new UnsupportedOperationException("GLTextureView not initialized");
            }

            onRendering();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        synchronized (lock) {
            if (!isInitialized()) {
                eglManager = new EGLManager();

                if (eglConfigChooser == null) {
                    // make default spec
                    // RGBA8 hasDepth hasStencil
                    eglConfigChooser = new DefaultEGLConfigChooser();
                }

                eglManager.initialize(eglConfigChooser, version);

                if (renderingThreadType != RenderingThreadType.BackgroundThread) {
                    // UIThread || request
                    renderer.onSurfaceCreated(gl11, eglManager.getConfig());
                }
            }

            surfaceWidth = width;
            surfaceHeight = height;

            eglManager.resize(surface);
            if (renderingThreadType != RenderingThreadType.BackgroundThread) {
                // UIThread || request
                eglManager.bind();
                renderer.onSurfaceChanged(gl11, width, height);
                eglManager.unbind();
            }

        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        synchronized (lock) {

            surfaceWidth = width;
            surfaceHeight = height;

            eglManager.resize(surface);

            if (renderingThreadType != RenderingThreadType.BackgroundThread) {
                // UIThread || request
                eglManager.bind();
                renderer.onSurfaceChanged(gl11, width, height);
                eglManager.unbind();
            }
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        try {
            synchronized (lock) {
                destroyed = true;

                if (renderingThreadType != RenderingThreadType.BackgroundThread) {
                    // UIThread || request
                    eglManager.bind();
                    renderer.onSurfaceDestroyed(gl11);
                    eglManager.unbind();
                }
            }

            if (backgroundThread != null) {
                try {
                    // wait background thread
                    backgroundThread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            eglManager.destroy();
        }

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
        eglManager.bind();
        {
            renderer.onDrawFrame(gl11);
        }
        eglManager.swapBuffers();
        eglManager.unbind();
    }

    /**
     * OpenGL ES Version
     */
    public enum GLESVersion {
        /**
         * OpenGL ES 1.0
         */
        OpenGLES11 {
            @Override
            public int[] getContextAttributes() {
                return null;
            }
        },

        /**
         * OpenGL ES 2.0
         */
        OpenGLES20 {
            @Override
            public int[] getContextAttributes() {
                return new int[] {
                        0x3098 /* EGL_CONTEXT_CLIENT_VERSION */, 2, EGL10.EGL_NONE
                };
            }
        };

        public abstract int[] getContextAttributes();
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

        /**
         * destroyed
         * @param gl
         */
        public void onSurfaceDestroyed(GL10 gl);
    }

    public enum RenderingThreadType {
        /**
         * Rendering on Background Loop
         */
        BackgroundThread,

        /**
         * Rendering on {@link GLTextureView#requestRendering()}
         */
        RequestThread,
    }

    protected Thread createRenderingThread() {
        return new Thread() {
            int width = 0;
            int height = 0;

            @Override
            public void run() {
                // created
                renderer.onSurfaceCreated(gl11, eglManager.getConfig());

                while (!destroyed) {
                    synchronized (lock) {
                        eglManager.bind();

                        if (width != surfaceWidth || height != surfaceHeight) {
                            width = surfaceWidth;
                            height = surfaceHeight;
                            renderer.onSurfaceChanged(gl11, width, height);
                        }

                        renderer.onDrawFrame(gl11);

                        // post
                        if (!destroyed) {
                            eglManager.swapBuffers();
                        }
                        eglManager.unbind();
                    }
                }

                // destroy
                synchronized (lock) {
                    eglManager.bind();
                    renderer.onSurfaceDestroyed(gl11);
                    eglManager.unbind();
                }
            }
        };
    }
}
