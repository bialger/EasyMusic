package com.bigri239.easymusic

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_recovery.*

@Suppress("DEPRECATION")
class RecoveryActivity : AppCompatActivity() {
    private var itemsList : List<String> = arrayListOf()
    private var itemsList1 : List<String> = arrayListOf()
    private var itemsList2 : List<String> = arrayListOf()
    private lateinit var webRequester : WebRequester
    private lateinit var customAdapter1: CustomAdapter
    private lateinit var customAdapter: CustomAdapter
    private lateinit var customAdapter2: CustomAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        webRequester = WebRequester(this@RecoveryActivity)

    }

    override fun onStart() {
        super.onStart()
        val info = webRequester.getInfo()
        if (!info.contentEquals(Array (5) {arrayOf("")})) {
            username.text = "Username: " + info[0][0]
            editInfo.setText(info[1][0])
            itemsList = info[2]
            itemsList1 = info[3]
            itemsList2 = info[4]

        }
        else {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.backrec).setOnClickListener {
            startActivity(intent)
        }
        customAdapter = if (itemsList != arrayListOf("")) CustomAdapter(itemsList)
        else CustomAdapter(arrayListOf())
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter

        customAdapter1 = if (itemsList1 != arrayListOf("")) CustomAdapter(itemsList1)
        else CustomAdapter(arrayListOf())
        val layoutManager1 = LinearLayoutManager(applicationContext)
        recyclerView2.layoutManager = layoutManager1
        recyclerView2.adapter = customAdapter1

        customAdapter2 = if (itemsList2 != arrayListOf("")) CustomAdapter(itemsList2)
        else CustomAdapter(arrayListOf())
        val layoutManager2 = LinearLayoutManager(applicationContext)
        recyclerView3.layoutManager = layoutManager2
        recyclerView3.adapter = customAdapter2
    }

    private fun showFriendDialog() {
        val dialog = Dialog(this, R.style.ThemeOverlay_Material3_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_new_project)
        dialog.findViewById<EditText>(R.id.newname).setHint("Friend`s email")
        dialog.findViewById<Button>(R.id.create).text = "Add friend"
        dialog.findViewById<Button>(R.id.create).setOnClickListener {
            val newFriend = dialog.findViewById<EditText>(R.id.newname).text.toString()
            if (newFriend != "" && !itemsList.contains(newFriend) && newFriend.contains('@')) {
                if (webRequester.changeInfo("friends", newFriend)) {
                    val friends = mutableListOf<String>()
                    if (itemsList != arrayListOf("")) friends.addAll(itemsList)
                    friends.add(newFriend)
                    itemsList = friends
                    recyclerView.adapter =  CustomAdapter(itemsList)
                    (recyclerView.adapter as CustomAdapter).notifyDataSetChanged()
                    dialog.dismiss()
                }
                else {
                    Toast.makeText(this, "No such user!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun showSoundDialog() {
        val dialog = Dialog(this, R.style.ThemeOverlay_Material3_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_new_project)
        dialog.findViewById<EditText>(R.id.newname).setHint("New sound name")
        dialog.findViewById<Button>(R.id.create).text = "Add sound"
        dialog.findViewById<Button>(R.id.create).setOnClickListener {
            val newSound = dialog.findViewById<EditText>(R.id.newname).text.toString()
            if (newSound != "" && !itemsList1.contains(newSound) && newSound.contains("http")) {
                newSound.replace("&", "AMPERSAND")
                if (webRequester.changeInfo("sounds", newSound)) {
                    val sounds = mutableListOf<String>()
                    if (itemsList1 != arrayListOf("")) sounds.addAll(itemsList1)
                    sounds.add(newSound)
                    itemsList1 = sounds
                    recyclerView2.adapter =  CustomAdapter(itemsList1)
                    (recyclerView2.adapter as CustomAdapter).notifyDataSetChanged()
                    dialog.dismiss()
                }
                else {
                    Toast.makeText(this, "No such user!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    fun logOff (view: View) {
        if (webRequester.logOff()) {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }

    fun changeAbout (view: View) {
        val newInfo = editInfo.text.toString()
        webRequester.changeInfo("about", newInfo)
    }

    fun addFriend (view: View) {
        showFriendDialog()
    }

    fun addSound (view: View) {
        showSoundDialog()
    }

    fun uploadProject (view: View) {

    }
}