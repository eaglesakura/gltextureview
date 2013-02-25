# GLTextureView
=============

GLSurfaceView相当の機能のTextureView版です。

## プロジェクトのセットアップ

Android Library Project、もしくはjarを追加することで利用可能です。

* 方法A. EclipseプロジェクトとしてGLTextureViewをimport後、Androidのライブラリプロジェクトとして追加してください。
* 方法B. Androidプロジェクトのlibsフォルダに、release/GLTextureView.jarを追加してください。

## 初期化

GLTextureViewの初期化は、onSurfaceTextureAvailable()が呼ばれる前に行わなければなりません。

* 基本的にはnewを行った直後か、setContentView(GLTextureView)を呼び出す前で問題ありません。
	
	
## サポートしているOpenGL ESバージョン

setVersion()を呼び出すことで、利用するOpenGL ESのバージョンを切り替えることができます。
デフォルトはGLSurfaceViewと同じく、OpenGL ES 1.1です。

1. OpenGL ES 1.1(デフォルト)
1. OpenGL ES 2.0


## 動作スレッド

GLTextureViewは自動的に作成したレンダリングスレッド、もしくは任意のスレッドから描画を行うことができます。

### 裏スレッドでの描画
=============

GLTextureViewは、デフォルトでGLSurfaceViewとおおまかに同じ動作を行います。<BR>
setRenderingThreadType()を呼び出さない限り、GLTextureViewはレンダリング専用のスレッドを作成し、Rendererのコールバックを行い続けます。<BR>
この場合でも、requestRender()を呼び出すことで任意のスレッドから描画を行うことができます。<BR>

### 任意スレッドでの描画
=============

setRenderingThreadType(RequestThread)を設定した場合、GLTextureViewはレンダリングスレッドを作成しません。<BR>
この場合、レンダリングしたい任意タイミングでrequestRender()を呼び出すことでレンダリングを行えます。<BR>
requestRender()を呼び出すスレッドは一つである必要は無く、自由なスレッドから呼び出すことができます。

### requestRender()の動作
=============
requestRender()は特定のスレッドへpostされるのではなく、呼び出したスレッドでレンダリングが行われます。

* requestRender()を抜けた時点で、レンダリングと画面への反映が完了していることになります。



## Rendering Callback

GLTextureViewはGLTextureView#Rendererインターフェースを通じて描画を行います。<BR>
実装すべき内容はGLSurfaceViewとほぼ変わりありません。

* onSurfaceDestroyed()はTextureViewが廃棄されるタイミングで呼び出されます。
	* 確保したリソースの解放等、release処理が必要であれば実装を行なってください。
	* OpenGL ES 2.0で初期化した場合、GL10インターフェースは常にnullが渡されます。



<pre>
    public interface Renderer {
        public void onSurfaceCreated(GL10 gl, EGLConfig config);
        public void onSurfaceChanged(GL10 gl, int width, int height);
        public void onDrawFrame(GL10 gl);
        public void onSurfaceDestroyed(GL10 gl);
    }
</pre>


=============


## ソースコードライセンス

* ソースコードは自由に利用してもらって構いません。
* NYSLに従います。

<pre>
A. 本ソフトウェアは Everyone'sWare です。このソフトを手にした一人一人が、
   ご自分の作ったものを扱うのと同じように、自由に利用することが出来ます。

  A-1. フリーウェアです。作者からは使用料等を要求しません。
  A-2. 有料無料や媒体の如何を問わず、自由に転載・再配布できます。
  A-3. いかなる種類の 改変・他プログラムでの利用 を行っても構いません。
  A-4. 変更したものや部分的に使用したものは、あなたのものになります。
       公開する場合は、あなたの名前の下で行って下さい。

B. このソフトを利用することによって生じた損害等について、作者は
   責任を負わないものとします。各自の責任においてご利用下さい。

C. 著作者人格権は @eaglesakura に帰属します。著作権は放棄します。

D. 以上の３項は、ソース・実行バイナリの双方に適用されます。
</pre>

### LICENSE(en)

<pre>

A. This software is "Everyone'sWare". It means:
  Anybody who has this software can use it as if he/she is
  the author.

  A-1. Freeware. No fee is required.
  A-2. You can freely redistribute this software.
  A-3. You can freely modify this software. And the source
      may be used in any software with no limitation.
  A-4. When you release a modified version to public, you
      must publish it with your name.

B. The author is not responsible for any kind of damages or loss
  while using or misusing this software, which is distributed
  "AS IS". No warranty of any kind is expressed or implied.
  You use AT YOUR OWN RISK.

C. Copyrighted to @eaglesakura

D. Above three clauses are applied both to source and binary
  form of this software.

</pre>