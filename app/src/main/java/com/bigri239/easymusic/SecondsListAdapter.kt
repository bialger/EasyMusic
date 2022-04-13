package com.bigri239.easymusic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

class SecondsListAdapter(val connector : MainActivity.Connector) : RecyclerView.Adapter<SecondsListAdapter.SecondsListViewHolder>() {


    private val sounds: MutableList<Sound> = mutableListOf()
    private val soundSeconds: MutableList<SoundSecond> = mutableListOf()
    init {
        eraseSounds()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecondsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_second, parent, false)
        return SecondsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SecondsListViewHolder, position: Int) {
        holder.bind(soundSeconds[position])
    }

    override fun getItemCount(): Int {
        return soundSeconds.size
    }

    fun addSound(newSound: Sound) {
        sounds.add(newSound)
        initSecondSounds()
    }

    private fun initSecondSounds() {
        soundSeconds.clear()
        for (sound in sounds) {
            for (i in 0 until sound.shift) {
                soundSeconds.add(SoundSecond())
            }
            for (i in 0 until sound.length) {
                soundSeconds.add(SoundSecond(getColor(sound.type), sound))
            }
        }
        for (i in 0..700) {
            soundSeconds.add(SoundSecond())
        }
        notifyDataSetChanged()
    }

    fun fillTrack (soundList: List<Sound>) {
        sounds.clear()
        sounds.addAll(soundList)
        initSecondSounds()
    }

    fun editSound (newSound : Sound) {
        sounds[newSound.number] = newSound
        initSecondSounds()
    }

    fun deleteSound (j : Int) {
        if (j != sounds.size - 1) {
            val sound = sounds[j + 1]
            sounds[j + 1].shift += sounds[j].length + sounds[j].shift
        }
        sounds.removeAt(j)
        initSecondSounds()
    }

    private fun getColor(type: SoundType): Int {
        return when (type){
            SoundType.SOUND1 -> R.color.yellow
            SoundType.SOUND2 -> R.color.pink
            SoundType.SOUND3 -> R.color.blue
            SoundType.SOUND4 -> R.color.cyan
            SoundType.SOUND5 -> R.color.red
        }
    }

    fun eraseSounds() {
        sounds.clear()
        initSecondSounds()
    }

    fun removeSound(sound: Sound) {
        if (sounds.size > 0) {
            connector.function(sound.track)
            sounds.removeAt(sounds.size - 1)
            initSecondSounds()
        }
    }

    inner class SecondsListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val viewSquare: View = itemView.findViewById(R.id.viewSquare)
        fun bind(second: SoundSecond) {
            viewSquare.setBackgroundResource(second.color)
            viewSquare.setOnClickListener {
                second.sound?.let { try {connector.function2(it.track, it.number)} catch (e: Exception) {} }
            }
        }
    }
}