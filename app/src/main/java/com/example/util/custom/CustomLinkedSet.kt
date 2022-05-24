package com.example.util.custom

class CustomLinkedSet<E> : AbstractSet<E>() {

    private class LinkedElement<E> {
        var value: E? = null
        var exists = false
        var prev: LinkedElement<E>? = null
        var next: LinkedElement<E>? = null
    }

    private val map: Map<E, LinkedElement<E>?> = HashMap()
    private val placeholder = LinkedElement<E>()
    private val head = placeholder
    override fun isEmpty(): Boolean {
        return head === placeholder
    }


    override fun contains(element: E): Boolean {
        return map.containsKey(element)
    } // здесь будут методы для добавления, удаления, итерирования

    override val size: Int = map.size

    override fun iterator(): Iterator<E> {
        TODO("Not yet implemented")
    }


}