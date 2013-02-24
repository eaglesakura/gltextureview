package com.eaglesakura.view.sample.test;

import javax.microedition.khronos.opengles.GL10;

import android.test.ActivityInstrumentationTestCase2;

import com.eaglesakura.view.GLTextureView;
import com.eaglesakura.view.GLTextureView.GLESVersion;
import com.eaglesakura.view.GLTextureView.RenderingThreadType;
import com.eaglesakura.view.sample.renderer.GL11RandomClearRenderer;
import com.eaglesakura.view.sample.renderer.GL20RandomClearRenderer;

public class SyncRendereringTest extends ActivityInstrumentationTestCase2<RenderingActivity> {

    public SyncRendereringTest() {
        super(RenderingActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        GLTextureView view = getActivity().getGLTextureView();
        view.setRenderingThreadType(RenderingThreadType.RequestThread);
    }

    /**
     * 正常にリクエストタイミングでレンダリングが行われることをチェック
     * @throws Throwable
     */
    public void test_GLES11_renderingRequest() throws Throwable {
        assertNotNull(getActivity());

        final Thread testThread = Thread.currentThread();
        final GL11RandomClearRenderer renderer = new GL11RandomClearRenderer() {
            @Override
            public void onDrawFrame(GL10 gl) {
                super.onDrawFrame(gl);

                // drawing on Test Thread
                assertEquals(testThread, Thread.currentThread());
            }
        };
        final GLTextureView view = getActivity().getGLTextureView();

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setRenderer(renderer);
                getActivity().setupView();
            }
        });

        while (!view.isInitialized()) {
            TestUtil.sleep(1);
        }

        assertTrue(renderer.created == 1);
        assertTrue(renderer.changed == 1);
        assertTrue(renderer.drawing == 0);

        final int DRAWING_NUM = 100;

        // request sync rendering
        for (int i = 0; i < DRAWING_NUM; ++i) {
            view.requestRender();
        }

        assertTrue(renderer.created == 1);
        assertTrue(renderer.changed == 1);
        assertTrue(renderer.drawing == DRAWING_NUM);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        });
        TestUtil.sleep(1000);

        assertTrue(renderer.destroyed == 1);
    }

    /**
     * 正常にリクエストタイミングでレンダリングが行われることをチェック
     * @throws Throwable
     */
    public void test_GLES20_renderingRequest() throws Throwable {
        assertNotNull(getActivity());

        final Thread testThread = Thread.currentThread();
        final GL20RandomClearRenderer renderer = new GL20RandomClearRenderer() {
            @Override
            public void onDrawFrame(GL10 gl) {
                super.onDrawFrame(gl);

                // drawing on Test Thread
                assertEquals(testThread, Thread.currentThread());
            }
        };
        final GLTextureView view = getActivity().getGLTextureView();

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVersion(GLESVersion.OpenGLES20);
                view.setRenderer(renderer);
                getActivity().setupView();
            }
        });

        while (!view.isInitialized()) {
            TestUtil.sleep(1);
        }

        assertTrue(renderer.created == 1);
        assertTrue(renderer.changed == 1);
        assertTrue(renderer.drawing == 0);

        final int DRAWING_NUM = 100;

        // request sync rendering
        for (int i = 0; i < DRAWING_NUM; ++i) {
            view.requestRender();
        }

        assertTrue(renderer.created == 1);
        assertTrue(renderer.changed == 1);
        assertTrue(renderer.drawing == DRAWING_NUM);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        });
        TestUtil.sleep(1000);

        assertTrue(renderer.destroyed == 1);
    }
}
