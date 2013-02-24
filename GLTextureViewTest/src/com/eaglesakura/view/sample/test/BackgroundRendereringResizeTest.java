package com.eaglesakura.view.sample.test;

import android.test.ActivityInstrumentationTestCase2;

import com.eaglesakura.view.sample.renderer.GL11RandomClearRenderer;
import com.eaglesakura.view.sample.renderer.GL20RandomClearRenderer;

public class BackgroundRendereringResizeTest extends ActivityInstrumentationTestCase2<RenderingActivityCC> {

    public BackgroundRendereringResizeTest() {
        super(RenderingActivityCC.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * 正常にレンダリングループが行われることをチェック
     * @throws Throwable
     */
    public void test_GL11_renderingResizeLoop() throws Throwable {
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
                TestUtil.toggleOrientationFixed(getActivity());
            }
        });
        TestUtil.sleep(1000);

        // TextureView Resize only
        assertTrue(renderer.created == 1);
        assertTrue(renderer.changed == 2);

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
    public void test_GL20_renderingResizeLoop() throws Throwable {
        assertNotNull(getActivity());

        final GL20RandomClearRenderer renderer = new GL20RandomClearRenderer();

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
                TestUtil.toggleOrientationFixed(getActivity());
            }
        });
        TestUtil.sleep(1000);

        // TextureView Resize only
        assertTrue(renderer.created == 1);
        assertTrue(renderer.changed == 2);

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
