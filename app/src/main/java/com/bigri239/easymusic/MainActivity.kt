package com.bigri239.easymusic

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.media.AudioManager
import android.media.SoundPool
import android.os.*
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.System.currentTimeMillis


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    data class SoundInfo(
        var res: String = "",
        var id: Int = 0,
        var delay: Long = 0,
        var volume: Float = 0.0F,
        var loop: Int = 0,
        var ratio: Float = 1.0F,
    )

    private var newProject = ""
    private var state: String = "unready"
    private var played = 0
    private var projectName = "projectDefault"
    private val projects = mutableListOf<String>()
    private val tracks: Array<SoundPool> =
        Array(100) { SoundPool(10, AudioManager.STREAM_MUSIC, 0) }
    private var countTracks = 0
    private val countSounds: Array<Int> = Array(100) { 0 }
    private val sounds: Array<Array<SoundInfo>> =
        Array(100) { Array(1000) { i -> SoundInfo("", i + 1, 0, 1.0F, 0, 1.0F) } }
    private var viewClickListener = View.OnClickListener { v -> create_select_project_popup_menu(v) }
    private var mAdapter: RecyclerAdapter? = null
    private var mRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val textView = findViewById<TextView>(R.id.txt)
        textView.setOnClickListener(viewClickListener)
        create_horizontal_list()
        create_sounds_list()
        try {
            val path = filesDir
            val file = File(path, "projects.conf")
            val content: String = file.readText()
            projects.addAll(content.split("\n").toTypedArray())
            openProject()
        }
        catch (e: IOException) {
            projects.add("projectDefault")
            val path = filesDir
            val file = File(path, "projects.conf")
            var content = ""
            for (i in projects.indices) {
                content += projects[i]
                if (i != projects.size - 1) content += "\n"
            }
            FileOutputStream(file).use {
                it.write(content.toByteArray())
            }
            setExample()
            saveProject()
        }
    }

    private fun showProjectDialog() {
        val dialog = Dialog(this, R.style.ThemeOverlay_Material3_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_new_project)
        dialog.findViewById<Button>(R.id.create).setOnClickListener {
            newProject = dialog.findViewById<EditText>(R.id.newname).text.toString()
            saveProject()
            projectName = newProject
            projects.add(projectName)
            txt.text = projectName
            Toast.makeText(applicationContext, "You created $projectName", Toast.LENGTH_SHORT).show()
            val path = filesDir
            val file = File(path, "projects.conf")
            val content = file.readText() + "\n" + projectName
            FileOutputStream(file).use {
                it.write(content.toByteArray())
            }

            /*for (i in 0..countTracks) { // очистка данных по звукам
                tracks[i].release()
                tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
                for (j in 0..countSounds[i]) {
                    sounds[i][j] = SoundInfo("", i + 1, 0, 1.0F, 0, 1.0F)
                }
            }*/

            dialog.dismiss()
        }
        dialog.show()
    }

    private fun project_select_popup_menu_click_listener(menuItem: MenuItem) {
        val itemTitle = menuItem.title.toString()
        if (itemTitle == "New project") {
            showProjectDialog()
        }
        else { // TODO: после написания рабочего recycler, надо вставить его обновление в конец и в диалог
            saveProject()
            projectName = itemTitle
            txt.text = projectName
            openProject()
        }
    }

    private fun create_sounds_list() {
        val sound_list = mutableListOf(
            "Kick", "Type1", "Snare",
            "Type1", "Hihat", "Type1",
            "Loop", "Type1", "Bass",
            "Type1", "808", "Type1",
            "+", "Type1", "+",
            "Type1"
        )
        val on_sound_click =  { position: Int, text_item: TextView ->
            sound_list[position] = "changed"
            text_item.text = sound_list[position]
            Log.d("MYMSG vert_recyc: ", sound_list[position])
            Unit
        }
        GridLayoutManager(
            this, // context
            2, // span count
            RecyclerView.VERTICAL, // orientation
            false // reverse layout
        ).apply {
            // specify the layout manager for recycler view
            recyclerView.layoutManager = this
        }
        recyclerView.adapter = RecyclerViewAdapter(sound_list, on_sound_click)
    }

    private fun create_select_project_popup_menu(v: View) {
        val popupMenu = PopupMenu(this, v)
        for (i in projects.indices) popupMenu.menu.add(projects[i])
        popupMenu.inflate(R.menu.popupmenu)
        popupMenu.setOnMenuItemClickListener { it -> project_select_popup_menu_click_listener(it); true }
        popupMenu.show()
    }

    private fun create_horizontal_list() {
        mRecyclerView = findViewById(R.id.recyclerViewhor)
        mRecyclerView?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        val dataset = arrayOfNulls<String>(50)
        for (i in dataset.indices) {
            dataset[i] = "item$i"
        }
        mAdapter = RecyclerAdapter(dataset, this)
        mRecyclerView?.adapter = mAdapter
    }

    // ниже создание переходов между экранами
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, HelpActivity::class.java)
        findViewById<TextView>(R.id.help).setOnClickListener {
            startActivity(intent)
        }
        val intent1 = Intent(this, AddingfilesActivity::class.java)
        findViewById<TextView>(R.id.file).setOnClickListener {
            startActivity(intent1)
        }
        val intent2 = Intent(this, SettingsActivity::class.java)
        findViewById<TextView>(R.id.settings).setOnClickListener {
            startActivity(intent2)
        }
        val intent3 = Intent(this, InstrumentsActivity::class.java)
        findViewById<TextView>(R.id.instruments).setOnClickListener {
            startActivity(intent3)
        }
        val intent4 = Intent(this, TutorialActivity::class.java)
        findViewById<TextView>(R.id.tutorial).setOnClickListener {
            startActivity(intent4)
        }
        val intent14 = Intent(this, RecoveryActivity::class.java)
        findViewById<TextView>(R.id.account).setOnClickListener {
            startActivity(intent14)
        }
    }

    override fun onCreateDialog(id: Int): Dialog {
        val activity = null
        return activity?.let {
            AlertDialog.Builder(it).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun bytesArrayPart4ToInt(arr: ByteArray, start: Int = 0): Int {
        return arr[start].toInt() + arr[start + 1].toInt() * 256 + arr[start + 2].toInt() * 256 * 256 + arr[start + 3].toInt() * 256 * 256 * 256
    }

    fun getSoundLength(res1: Int): Int {
        val res: Resources = resources
        val inStream: InputStream = res.openRawResource(res1)
        val wavdata = ByteArray(45)
        inStream.read(wavdata, 0, 45)
        inStream.close()
        if (wavdata.size > 44) {
            val byteRate = bytesArrayPart4ToInt(wavdata, 28)
            val waveSize = bytesArrayPart4ToInt(wavdata, 40)
            if (byteRate != 0) return (waveSize * 1000.0 / byteRate).toInt()
        }
        return 0
    }

    fun playTrack(i: Int, j: Int, delay: Long = 0) {
        val started = played
        object : CountDownTimer(sounds[i][j].delay + delay, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                val sound = sounds[i][j]
                if (state != "pause" && started == played) {
                    tracks[i].play(sound.id, sound.volume, sound.volume, 0, sound.loop, sound.ratio)
                    Log.d(TAG, "MYMSG play: $i $j " + sounds[i][0].id.toString())
                }
                if (j < countSounds[i] && started == played) playTrack(i, j + 1)
                if (j == countSounds[i] && i == countTracks) {
                    object : CountDownTimer(
                        (getSoundLength(
                            resources.getIdentifier(sound.res, "raw", packageName)
                        ) * (sound.loop + 1) / sound.ratio).toLong(), 1000) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            state = "ready"
                        }
                    }.start()
                }
            }
        }.start()
    }

    private fun setExample() { //демонстрация работы как одного трека, так и нескольких звуков (что все работает)
        sounds[0][0].ratio = 0.5F // по фану, для демонстрации
        sounds[0][0].res = "file1"
        countTracks = 1 // к следующей дорожке

        sounds[1][0].res = "file1"
        sounds[1][0].delay = (getSoundLength(resources.getIdentifier(sounds[0][0].res, "raw", packageName)) /
                sounds[0][0].ratio).toLong() // задержка перед следующим звуком - длина этого, деленное на ratio
        countSounds[1] = 1 // к следующему звуку
        sounds[1][1].res = "file2"
        sounds[1][1].delay = (getSoundLength(resources.getIdentifier(sounds[1][0].res, "raw", packageName)) /
                sounds[1][0].ratio).toLong() - 3000 // для демонстрации
        sounds[1][1].loop = 1
        state = "ready"
    }

    fun pause(view: View) {
        if (state == "playing") {
            Toast.makeText(this, "Music paused", Toast.LENGTH_SHORT).show()
            for (i in 0..countTracks) tracks[i].autoPause()
            state = "pause"
        }
    }

    fun playSound(view: View) {
        if (state == "ready") {
            played += 1
            Toast.makeText(this, "Playing compiled music...", Toast.LENGTH_SHORT).show()
            saveProject()

            for (i in 0..countTracks) { // очищение и перезаполнение, если играем еще раз
                tracks[i].release()
                tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
            }

            for (i in 0..countTracks) {
                for (j in 0..countSounds[i]) sounds[i][j].id = tracks[i].load(
                    baseContext,
                    resources.getIdentifier(sounds[i][j].res, "raw", packageName),
                    0
                ) // загрузить i трек, j звук
            }

            if (state != "playing") {
                val start: Long = currentTimeMillis() + 300
                state = "playing"
                for (i in 0..countTracks) playTrack(i, 0, start - currentTimeMillis())
            }
        } else if (state == "pause") {
            Toast.makeText(this, "Music unpaused", Toast.LENGTH_SHORT).show()
            state = "playing"
            for (i in 0..countTracks) tracks[i].autoResume()
        }
    }

    private fun openProject() {
        try {
            Toast.makeText(this, "Opening project $projectName...", Toast.LENGTH_SHORT).show()
            val path = filesDir
            val file = File(path, "$projectName.emproj")
            val content: String = file.readText()
            val tracksContent = content.split("\n").toTypedArray()
            countTracks = tracksContent.size - 1
            for (i in tracksContent.indices) {
                val soundsContent = tracksContent[i].split(";").toTypedArray()
                countSounds[i] = soundsContent.size - 1
                for (j in soundsContent.indices) {
                    val params = soundsContent[j].split(" ").toTypedArray()
                    sounds[i][j] = SoundInfo(
                        params[0],
                        params[1].toInt(),
                        params[2].toLong(),
                        params[3].toFloat(),
                        params[4].toInt(),
                        params[5].toFloat()
                    )
                }
            }
            state = "ready"
        } catch (e: IOException) {
            Toast.makeText(this, "No such file!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProject() {
        if (state != "unready") {
            val path = filesDir
            val file = File(path, "$projectName.emproj")
            var content = ""
            for (i in 0..countTracks) {
                for (j in 0..countSounds[i]) {
                    content += sounds[i][j].res + " "
                    content += sounds[i][j].id.toString() + " "
                    content += sounds[i][j].delay.toString() + " "
                    content += sounds[i][j].volume.toString() + " "
                    content += sounds[i][j].loop.toString() + " "
                    content += sounds[i][j].ratio.toString()
                    if (j != countSounds[i]) content += ";"
                }
                if (i != countTracks) content += "\n"
            }
            FileOutputStream(file).use {
                it.write(content.toByteArray())
            }
        }
    }

    fun saveProjectUI (view: View) {
        saveProject()
        if (state != "unready") Toast.makeText(this, "Saving project...", Toast.LENGTH_SHORT).show()
    }

    fun saveMusic(view: View) {
        if (state != "unready") {
            Toast.makeText(this, "Saving music...", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "Oops! Not ready yet! :(", Toast.LENGTH_SHORT).show()
        }
    }

    fun resetPlaying(view: View) {
        if (state == "playing" || state == "ready") {
            for (i in 0..countTracks) { // очищение и перезаполнение, если играем еще раз
                Toast.makeText(this, "Playing halted", Toast.LENGTH_SHORT).show()
                tracks[i].autoPause()
                tracks[i].release()
                tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
                state = "ready"
            }
        }
    }
}