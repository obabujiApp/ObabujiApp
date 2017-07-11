package com.obabuji

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class KotlinDemoActivity : AppCompatActivity() {

    val NAME:String ="satish";
    var surname:String="";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_demo)
        val btn = findViewById(R.id.button2) as Button
        val name = findViewById(R.id.name) as EditText


        btn.setOnClickListener {

            surname = name.text.toString();
            Toast.makeText(this@KotlinDemoActivity,""+surname,Toast.LENGTH_LONG).show();
            Log.e("tets","name "+NAME+" sname="+surname);

        }

    }

}
