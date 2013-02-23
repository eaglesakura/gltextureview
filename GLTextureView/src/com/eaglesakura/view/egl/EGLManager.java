package com.eaglesakura.view.egl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.graphics.SurfaceTexture;
import android.os.Looper;

public class EGLManager {
    /**
     * ロックオブジェクト
     */
    final private Object lock = new Object();

    /**
     * EGLオブジェクト
     */
    EGL10 egl = null;

    /**
     * レンダリング用ディスプレイ
     */
    EGLDisplay eglDisplay = null;

    /**
     * レンダリング用サーフェイス
     */
    EGLSurface eglSurface = null;

    /**
     * レンダリング用コンテキスト
     */
    EGLContext eglContext = null;

    /**
     * config情報
     */
    EGLConfig eglConfig = null;

    /**
     * システムがデフォルトで使用しているEGLDisplayオブジェクト
     */
    EGLDisplay defDisplay = null;

    /**
     * システムがデフォルトで使用しているEGLSurface(Read)
     */
    EGLSurface defReadSurface = null;

    /**
     * システムがデフォルトで使用しているEGLSurface(Draw)
     */
    EGLSurface defDrawSurface = null;

    /**
     * システムがデフォルトで使用しているコンテキスト
     */
    EGLContext defContext = null;

    public EGLManager() {
    }

    private int[] getConfigSpec(SurfaceColorSpec color, int depth_bits, int stencil_bits) {
        List<Integer> result = new ArrayList<Integer>();
        // レンダラーをES2.0に設定
        {
            result.add(EGL10.EGL_RENDERABLE_TYPE);
            result.add(4); /* EGL_OPENGL_ES2_BIT */
        }

        switch (color) {
            case RGBA8:
                result.add(EGL10.EGL_RED_SIZE);
                result.add(8);
                result.add(EGL10.EGL_GREEN_SIZE);
                result.add(8);
                result.add(EGL10.EGL_BLUE_SIZE);
                result.add(8);
                result.add(EGL10.EGL_ALPHA_SIZE);
                result.add(8);
                break;
            case RGB8:
                result.add(EGL10.EGL_RED_SIZE);
                result.add(8);
                result.add(EGL10.EGL_GREEN_SIZE);
                result.add(8);
                result.add(EGL10.EGL_BLUE_SIZE);
                result.add(8);
                break;
            case RGB565:
                result.add(EGL10.EGL_RED_SIZE);
                result.add(5);
                result.add(EGL10.EGL_GREEN_SIZE);
                result.add(6);
                result.add(EGL10.EGL_BLUE_SIZE);
                result.add(5);
                break;
            default:
                throw new UnsupportedOperationException(color.toString());
        }

        if (depth_bits > 0) {
            result.add(EGL10.EGL_DEPTH_SIZE);
            result.add(depth_bits);
        }

        if (stencil_bits > 0) {
            result.add(EGL10.EGL_STENCIL_SIZE);
            result.add(stencil_bits);
        }

        // 終端
        result.add(EGL10.EGL_NONE);

        int[] result_array = new int[result.size()];
        for (int i = 0; i < result.size(); ++i) {
            result_array[i] = result.get(i);
        }
        return result_array;
    }

    private int getConfigAttrib(EGLConfig eglConfig, int attr) {
        int[] value = new int[1];
        egl.eglGetConfigAttrib(eglDisplay, eglConfig, attr, value);
        return value[0];
    }

    /**
     * RGB各色、深度、ステンシルそれぞれが指定に近いconfigを取り出す
     * @param color
     * @param depth_bits
     * @param stencil_bits
     * @return
     */
    private EGLConfig chooseConfig(SurfaceColorSpec color, int depth_bits, int stencil_bits) {
        //! コンフィグを全て取得する
        EGLConfig[] configs = new EGLConfig[32];
        // コンフィグ数がeglChooseConfigから返される
        int[] config_num = new int[1];
        if (!egl.eglChooseConfig(eglDisplay, getConfigSpec(color, depth_bits, stencil_bits), configs, configs.length,
                config_num)) {
            throw new RuntimeException("eglChooseConfig");
        }

        final int CONFIG_NUM = config_num[0];
        int r_bits = 0;
        int g_bits = 0;
        int b_bits = 0;
        int a_bits = 0;

        switch (color) {
            case RGBA8:
                r_bits = g_bits = b_bits = a_bits = 8;
                break;
            case RGB8:
                r_bits = g_bits = b_bits = 8;
                break;
            case RGB565:
                r_bits = 5;
                g_bits = 6;
                b_bits = 5;
                break;
            default:
                throw new UnsupportedOperationException(color.toString());
        }

        // 指定したちょうどのconfigを探す
        for (int i = 0; i < CONFIG_NUM; ++i) {
            final EGLConfig checkConfig = configs[i];

            final int config_r = getConfigAttrib(checkConfig, EGL10.EGL_RED_SIZE);
            final int config_g = getConfigAttrib(checkConfig, EGL10.EGL_GREEN_SIZE);
            final int config_b = getConfigAttrib(checkConfig, EGL10.EGL_BLUE_SIZE);
            final int config_a = getConfigAttrib(checkConfig, EGL10.EGL_ALPHA_SIZE);
            final int config_d = getConfigAttrib(checkConfig, EGL10.EGL_DEPTH_SIZE);
            final int config_s = getConfigAttrib(checkConfig, EGL10.EGL_STENCIL_SIZE);

            // RGBが指定サイズジャスト、ADSが指定サイズ以上あれば合格とする
            if (config_r == r_bits && config_g == g_bits && config_b == b_bits && config_a >= a_bits
                    && config_d >= depth_bits && config_s >= stencil_bits) {
                return checkConfig;
            }
        }

        // 先頭のコンフィグを返す
        return configs[0];
    }

    /**
     * 初期化を行う
     */
    public void initialize() {
        synchronized (lock) {
            if (egl != null) {
                throw new RuntimeException("initialized");
            }

            egl = (EGL10) EGLContext.getEGL();

            // システムのデフォルトオブジェクトを取り出す
            {
                defDisplay = egl.eglGetCurrentDisplay();
                defReadSurface = egl.eglGetCurrentSurface(EGL10.EGL_READ);
                defDrawSurface = egl.eglGetCurrentSurface(EGL10.EGL_DRAW);
                defContext = egl.eglGetCurrentContext();
            }

            // ディスプレイ作成
            {
                eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
                    throw new RuntimeException("EGL_NO_DISPLAY");
                }

                if (!egl.eglInitialize(eglDisplay, new int[2])) {
                    throw new RuntimeException("eglInitialize");
                }
            }
            // コンフィグ取得
            {
                eglConfig = chooseConfig(SurfaceColorSpec.RGBA8, 16, 8);
            }

            // コンテキスト作成
            {
                int[] attributes = {
                        0x3098 /* EGL_CONTEXT_CLIENT_VERSION */, 2, EGL10.EGL_NONE
                };
                eglContext = egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attributes);

                if (eglContext == EGL10.EGL_NO_CONTEXT) {
                    throw new RuntimeException("eglCreateContext");
                }
            }
        }
    }

    /**
     * リサイズを行う
     * @param view
     */
    public void resize(SurfaceTexture surface) {
        synchronized (lock) {
            // 既にサーフェイスが存在する場合は廃棄する
            if (eglSurface != null) {
                egl.eglDestroySurface(eglDisplay, eglSurface);
            }

            // レンダリング用サーフェイスを再度生成
            {
                eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, surface, null);
                if (eglSurface == EGL10.EGL_NO_SURFACE) {
                    throw new RuntimeException("eglCreateWindowSurface");
                }
            }
        }
    }

    /**
     * 解放処理を行う
     */
    public void dispose() {
        synchronized (lock) {
            if (egl == null) {
                return;
            }

            if (eglSurface != null) {
                egl.eglDestroySurface(eglDisplay, eglSurface);
                eglSurface = null;
            }
            if (eglContext != null) {
                egl.eglDestroyContext(eglDisplay, eglContext);
                eglContext = null;
            }
            eglConfig = null;
            egl = null;
        }
    }

    /**
     * ES20コンテキストを専有する
     */
    public void bind() {
        synchronized (lock) {
            egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
        }
    }

    /**
     * UIスレッドで呼び出された場合trueを返す。
     * @return
     */
    public boolean isUIThread() {
        return Thread.currentThread().equals(Looper.getMainLooper().getThread());
    }

    /**
     * コンテキストの専有を解除する
     */
    public void unbind() {
        synchronized (lock) {
            if (isUIThread()) {
                // UIスレッドならばシステムのデフォルトへ返す
                egl.eglMakeCurrent(defDisplay, defDrawSurface, defReadSurface, defContext);
            } else {
                // それ以外ならば、null状態に戻す
                egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            }
        }
    }

    /**
     * レンダリング内容をフロントバッファへ転送する
     */
    public void postFrontBuffer() {
        synchronized (lock) {
            egl.eglSwapBuffers(eglDisplay, eglSurface);
        }
    }
}
