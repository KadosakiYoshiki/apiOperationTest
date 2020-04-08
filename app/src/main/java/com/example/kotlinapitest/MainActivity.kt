package com.example.kotlinapitest

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //表示ボタンであるButtonオブジェクトを取得。
        val btClick = findViewById<Button>(R.id.btClick)
        //リスナクラスのインスタンスを生成。
        val listener = HelloListener()
        //表示ボタンにリスナを設定。
        btClick.setOnClickListener(listener)
    }
    /**
     * ボタンをクリックしたときのリスナクラス。
     */
    private inner class HelloListener : View.OnClickListener {
        override fun onClick(view: View) {
            //名前入力欄であるEditTextオブジェクトを取得。
            val input = findViewById<EditText>(R.id.etName)
            //メッセージを表示するTextViewオブジェクトを取得。
            val output = findViewById<TextView>(R.id.tvOutput)
            //入力された名前文字列を取得。
            val inputStr = input.text.toString()
            //idのR値に応じて処理を分岐。
            when(view.id) {
                //表示ボタンの場合…
                R.id.btClick -> {
                    //入力された名前文字列を取得。
                    val inputStr = input.text.toString()
                    output.text = inputStr
                    val st = inputStr //"1240004"
                    //画面部品ListViewを取得
                    val lvCityList = findViewById<ListView>(R.id.lvCityList)
                    //SimpleAdapterで使用するMutableListオブジェクトを用意。
                    val cityList: MutableList<MutableMap<String, String>> = mutableListOf()
                    //都市データを格納するMutableMapオブジェクトの用意とcityListへのデータ登録。

                    //val st = "1240004"
                    var city = mutableMapOf("name" to "郵便番号; " + st, "id" to st)
                    cityList.add(city)
                    //SimpleAdapterで使用するfrom-to用変数の用意。
                    val from = arrayOf("name")
                    val to = intArrayOf(android.R.id.text1)
                    //SimpleAdapterを生成。
                    val adapter = SimpleAdapter(
                            applicationContext,
                            cityList,
                            android.R.layout.simple_expandable_list_item_1,
                            from,
                            to
                    )
                    //ListViewにSimpleAdapterを設定。
                    lvCityList.adapter = adapter
                    //リストタップのリスナクラス登録。
                    lvCityList.onItemClickListener = ListItemClickListener()
                }
            }
        }
    }
    /**
     * リストがタップされたときの処理が記述されたメンバクラス。
     */
    private inner class ListItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            //ListViewでタップされた行の都市名と都市IDを取得。
            val item = parent.getItemAtPosition(position) as Map<String, String>
            val cityName = item["name"]
            val cityId = item["id"]
            //取得した都市名をtvCityNameに設定。
            val tvCityName = findViewById<TextView>(R.id.tvCityName)
            tvCityName.setText(cityId + "の住所: ")
            //選択されたラジオボタングループの値を取得。
            //ラジオボタングループで選択されている値を取得
            val rgRequest: RadioGroup = findViewById(R.id.rgRequest)
            val id: Int = rgRequest.checkedRadioButtonId
            val radioButton: RadioButton = rgRequest.findViewById(id)
            //WeatherInfoReceiverインスタンスを生成。
            val receiver = WeatherInfoReceiver()
            //WeatherInfoReceiverを実行。
            receiver.execute(cityId, radioButton.text.toString())
        }
    }
    /**
     * 非同期でお天気データを取得するクラス。
     */
    private inner class WeatherInfoReceiver() : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String): String {
            //可変長引数の1個目(インデックス0)を取得。これが都市ID
            val id = params[0]
            //都市IDを使って接続URL文字列を作成。
            //val urlStr = "http://weather.livedoor.com/forecast/webservice/json/v1?city=${id}"
            val urlStr = "https://limitless-refuge-12748.herokuapp.com/api/v1/posts" + id
            //URLオブジェクトを生成。
            val url = URL(urlStr)
            //URLオブジェクトからHttpURLConnectionオブジェクトを取得。
            val con = url.openConnection() as HttpURLConnection
            //http接続メソッドを設定。
            con.requestMethod = params[1]
            //接続。
            con.connect()
            //HttpURLConnectionオブジェクトからレスポンスデータを取得。天気情報が格納されている。
            val stream = con.inputStream
            //レスポンスデータであるInputStreamオブジェクトを文字列(JSON文字列)に変換。
            val result = is2String(stream)
            //HttpURLConnectionオブジェクトを解放。
            con.disconnect()
            //InputStreamオブジェクトを解放。
            stream.close()
            //JSON文字列を返す。
            return result

        }
        override fun onPostExecute(result: String) {
            val tvWeatherDesc = findViewById<TextView>(R.id.tvWeatherDesc)
            //tvWeatherTelop.text = telop
            tvWeatherDesc.text = result //desc
        }
        /**
         * InputStreamオブジェクトを文字列に変換するメソッド。変換文字コードはUTF-8。
         * @param stream 変換対象のInputStreamオブジェクト。
         * @return 変換された文字列。
         */
        private fun is2String(stream: InputStream): String {
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var line = reader.readLine()
            while(line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            return sb.toString()
        }
    }
}
