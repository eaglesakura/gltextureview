package com.eaglesakura.view.sample.test;

import android.test.ActivityInstrumentationTestCase2;

import com.eaglesakura.view.GLTextureView;
import com.eaglesakura.view.GLTextureView.GLESVersion;
import com.eaglesakura.view.sample.renderer.GL11RandomClearRenderer;
import com.eaglesakura.view.sample.renderer.GL20RandomClearRenderer;

public class BackgroundRendereringTest extends ActivityInstrumentationTestCase2<RenderingActivity> {

    public BackgroundRendereringTest() {
        super(RenderingActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * 正常にレンダリングループが行われることをチェック
     * @throws Throwable
     */
    public void test_GLES11_renderingLoop() throws Throwable {
        assertNotNull(getActivity());

        final GL11RandomClearRenderer renderer = new GL11RandomClearRenderer();

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().getGLTextureView().setRenderer(renderer);
                getActivity().setupView();
            }
        });

        TestUtil.sleep(1000);

        assertTrue(renderer.created == 1);
        assertTrue(renderer.changed == 1);
        assertTrue(renderer.drawing > 0);

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
     * 正常にレンダリングループが行われることをチェック
     * @throws Throwable
     */
    public void test_GLES20_renderingLoop() throws Throwable {
        assertNotNull(getActivity());

        final GL20RandomClearRenderer renderer = new GL20RandomClearRenderer();

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                GLTextureView view = getActivity().getGLTextureView();
                view.setVersion(GLESVersion.OpenGLES20);
                view.setRenderer(renderer);
                getActivity().setupView();
            }
        });

        TestUtil.sleep(1000);

        assertTrue(renderer.created == 1);
        assertTrue(renderer.changed == 1);
        assertTrue(renderer.drawing > 0);

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
