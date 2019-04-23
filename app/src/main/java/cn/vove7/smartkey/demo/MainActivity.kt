package cn.vove7.smartkey.demo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import cn.vove7.smartkey.PrintListener
import cn.vove7.smartkey.R
import cn.vove7.smartkey.SmartKey
import cn.vove7.smartkey.Vog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val lis: PrintListener = {
        log_text.append(it + "\n")
        scrollView.smoothScrollTo(0, log_text.height + 100)
    }

    override fun onDestroy() {
        super.onDestroy()
        Vog.removeListener(lis)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartKey.init(this)
        Vog.init(0)//输出日志
        Vog.addListener(lis)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Vog.d("nullableInt: " + AppConfig.nullableInt)
        AppConfig.nullableInt = 9
        Vog.d("nullableInt: " + AppConfig.nullableInt)
        /**
         * 测试 set get
         */
        SmartKey["app2", "key"] = 1
        val s = SmartKey["app2", "text", "de"]//config, key, default
        Vog.d("app2[text] : $s")

        SmartKey["text"] = "234"

        val t = SmartKey[R.string.key_text, "d_s"]//key, default
        Vog.d("默认配置[text]:$t")


        //number_picker
        number_picker.apply {
            minValue = 0
            maxValue = 100
            setOnValueChangedListener { _, oldVal, newVal ->
                AppConfig.number = newVal
            }
            value = AppConfig.number
        }

        //输入框
        edit_text.setText(AppConfig.text)
        edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s ?: return
                AppConfig.text = s.toString()
                AppConfig.userInfo = UserInfo(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //数组 TextView
        arr_text.text = Arrays.toString(AppConfig.intArr)
        //add按钮
        add_arr.setOnClickListener {
            AppConfig.intArr = arrayOf(*AppConfig.intArr, Random().nextInt(100))
            arr_text.text = Arrays.toString(AppConfig.intArr)
        }
        //remove按钮
        remove_arr.setOnClickListener {
            try {
                val l = mutableListOf(*AppConfig.intArr)
                l.removeAt(0)
                AppConfig.intArr = l.toTypedArray()
            } catch (e: Exception) {
                Vog.d("已无元素")
            }
            arr_text.text = Arrays.toString(AppConfig.intArr)
        }
    }
}
