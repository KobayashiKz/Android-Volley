package com.kk.weatherwidgetapp.weatherapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NetworkImageView

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG: String = "MainActivity"
        const val URL_WEATHER: String = "http://weather.livedoor.com/forecast/webservice/json/v1"
        const val URL_WEATHER_CITY:String = "?city"
        const val EQUAL: String = "="
        const val ID: String = "130010"

        // Jsonレスポンスをパースする際のキー
        const val KEY_WEATHER_TITLE = "title"
        const val KEY_WEATHER_JSONOBJECT_DESCRIPTION = "description"
        const val KEY_WEATHER_DESCRIPTION = "text"
        const val KEY_WEATHER_JSONARRAY_FORECASTS = "forecasts"
        const val KEY_WEATHER_DATELABEL = "dateLabel"
        const val KEY_WEATHER_TELOP = "telop"
        const val KEY_WEATHER_IMAGE = "image"
        const val KEY_WEATHER_URL = "url"
    }

    private var mTitle: TextView? = null
    private var mDescription: TextView? = null
    private var mTodayLavel: TextView? = null
    private var mTomorrowLavel: TextView? = null
    private var mTelopToday: TextView? = null
    private var mTelopTomorrow: TextView? = null

    private var mWeatherImageToday: NetworkImageView? = null
    private var mWeatherImageTomorrow: NetworkImageView? = null

    var mImageLoader: ImageLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTitle = findViewById(R.id.title_text)
        mTelopToday = findViewById(R.id.today_weather_telop_text)
        mTodayLavel = findViewById(R.id.today_weather_date_text)
        mTelopTomorrow = findViewById(R.id.tomorrow_weather_telop_text)
        mTomorrowLavel = findViewById(R.id.tomorrow_weather_date_text)
        mDescription = findViewById(R.id.description_text)
        mWeatherImageToday = findViewById(R.id.weather_image_view_today)
        mWeatherImageTomorrow = findViewById(R.id.weather_image_view_tomorrow)

        mImageLoader = RequestSingleQueue.getImageLoader()

        request()
    }

    /**
     * WebAPIへのリクエストを作り、リクエストキューに追加する
     */
    private fun request() {
        val request: JsonObjectRequest = JsonObjectRequest(
            createUrl(ID),
            null,
            Response.Listener {response ->
                // 正常にレスポンスが返ってきた場合はUI上へデータを反映させる
                // タイトル
                mTitle?.text = response.getString(KEY_WEATHER_TITLE)
                // 本日テキスト
                mTodayLavel?.text = response.getJSONArray(KEY_WEATHER_JSONARRAY_FORECASTS)
                    .getJSONObject(0).getString(KEY_WEATHER_DATELABEL)
                // 本日の天気テキスト
                mTelopToday?.text = response.getJSONArray(KEY_WEATHER_JSONARRAY_FORECASTS)
                    .getJSONObject(0).getString(KEY_WEATHER_TELOP)
                // 明日テキスト
                mTomorrowLavel?.text = response.getJSONArray(KEY_WEATHER_JSONARRAY_FORECASTS)
                    .getJSONObject(1).getString(KEY_WEATHER_DATELABEL)
                // 明日の天気テキスト
                mTelopTomorrow?.text = response.getJSONArray(KEY_WEATHER_JSONARRAY_FORECASTS)
                    .getJSONObject(1).getString(KEY_WEATHER_TELOP)
                // 詳細テキスト
                mDescription?.text = response.getJSONObject(KEY_WEATHER_JSONOBJECT_DESCRIPTION)
                    .getString(KEY_WEATHER_DESCRIPTION)

                // 本日の天気アイコン
                val urlToday: String = response.getJSONArray(KEY_WEATHER_JSONARRAY_FORECASTS)
                    .getJSONObject(0)
                    .getJSONObject(KEY_WEATHER_IMAGE)
                    .getString(KEY_WEATHER_URL)
                mWeatherImageToday?.setImageUrl(urlToday, mImageLoader)

                // 明日の天気アイコン
                val urlTomorrow: String = response.getJSONArray(KEY_WEATHER_JSONARRAY_FORECASTS)
                    .getJSONObject(1)
                    .getJSONObject(KEY_WEATHER_IMAGE)
                    .getString(KEY_WEATHER_URL)
                mWeatherImageTomorrow?.setImageUrl(urlTomorrow, mImageLoader)
            },
            Response.ErrorListener {error ->
                // エラーレスポンスだった場合の処理
                Log.d(TAG, error.toString())
            }
        )

        RequestSingleQueue.addToRequestQueue(request, applicationContext)
    }

    /**
     * WebAPIのURLを作成する
     *
     * @param id 取得したい地域のID
     */
    private fun createUrl(id: String): String {
        return URL_WEATHER + URL_WEATHER_CITY + EQUAL + id
    }
}
