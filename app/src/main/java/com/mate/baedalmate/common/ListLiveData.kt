package com.mate.baedalmate.common

import androidx.lifecycle.MutableLiveData

class ListLiveData<T> : MutableLiveData<ArrayList<T>>() {
    init {
        value = ArrayList()
    }

    fun at(pos: Int): T {
        val items: ArrayList<T>? = value
        return items!![pos]
    }

    fun add(item: T) {
        val items: ArrayList<T>? = value
        items!!.add(item)
        value = items
    }

    fun addAll(list: List<T>) {
        val items: ArrayList<T>? = value
        items!!.addAll(list)
        value = items
    }

    fun clear(notify: Boolean) {
        val items: ArrayList<T>? = value
        items!!.clear()
        if (notify) {
            value = items
        }
    }

    fun size(): Int {
        return value?.size ?: 0
    }

    fun replaceAt(pos: Int, item: T) {
        val items: ArrayList<T>? = value
        items!![pos] = item
        value = items
    }

    fun replaceAll(list: List<T>) {
        val items: ArrayList<T> = arrayListOf()
        items.addAll(list)
        value = items
    }

    fun removeAt(pos: Int) {
        val items: ArrayList<T>? = value
        items!!.removeAt(pos)
        value = items
    }

    fun remove(item: T) {
        val items: ArrayList<T>? = value
        items!!.remove(item)
        value = items
    }

    fun notifyChange() {
        val items: ArrayList<T>? = value
        value = items
    }

    fun contains(item: T): Boolean {
        val items: ArrayList<T>? = value
        return items!!.contains(item)
    }
}