package com.kk.weatherwidgetapp.weatherapp

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

/**
 * シングルトンのリクエストキュークラス
 */
object RequestSingleQueue {

    // リクエストキュークラス
    private var mRequestQueue: RequestQueue? = null

    // Volley: 画像の読み込みとキャッシュ処理を行ってくれるクラス
    // LruCacheに画像のキャッシュがあればそれを使用する
    // キャッシュの処理を書かずにXMLに追加しておけばいいので負担がほぼない
    private var mImageLoader: ImageLoader? = null

    init {
        // do nothing
    }

    /**
     * リクエストキュー(singleTon)を取得
     */
    fun getRequestQueue(context: Context): RequestQueue? {
        if (mRequestQueue == null) {
            // Volley: newRequestQueue()でリクエスト送信用のキューを取得
            mRequestQueue = Volley.newRequestQueue(context.applicationContext)
        }
        return mRequestQueue
    }

    fun getImageLoader(): ImageLoader? {

        mImageLoader = ImageLoader(mRequestQueue,
            object : ImageLoader.ImageCache {
                // キャッシュは最大20件
                private val cache: LruCache<String, Bitmap>? = LruCache<String, Bitmap>(20)

                override fun getBitmap(url: String): Bitmap? {
                    // 画像をキャッシュから取り出してreturn
                    return cache?.get(url)
                }

                override fun putBitmap(url: String, bitmap: Bitmap) {
                    // 画像をキャッシュに格納
                    cache?.put(url, bitmap)
                }
            })

        return mImageLoader
    }

    /**
     * リクエストキューにリクエストを追加する処理
     */
    fun <T> addToRequestQueue(request: Request<T>, context: Context) {
        // Volley: .add()でリクエスト送信用のキューにリクエストを追加
        getRequestQueue(context)?.add(request)
    }
}