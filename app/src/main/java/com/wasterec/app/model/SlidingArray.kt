package com.wasterec.app.model

class SlidingArray<T>(private val maxSize: Int) {
    // ArrayDeque lebih efisien daripada ArrayList untuk operasi tambah/hapus di ujung
    private val deque = ArrayDeque<T>(maxSize)

    fun add(item: T) {
        // Jika ukuran sudah mencapai batas maksimal, hapus elemen pertama (index 0)
        if (deque.size == maxSize) {
            deque.removeFirst()
        }
        // Tambahkan elemen baru di paling belakang
        deque.addLast(item)
    }

    // Fungsi tambahan untuk melihat isi array saat ini
    fun getList(): List<T> {
        return deque.toList()
    }
}

// --- Cara Penggunaan ---
fun main() {
    val mySlidingArray = SlidingArray<Int>(3)

    mySlidingArray.add(10)
    mySlidingArray.add(20)
    mySlidingArray.add(30)
    println(mySlidingArray.getList()) // Output: [10, 20, 30]

    // Angka 40 masuk, angka 10 (index 0) tergeser/hilang
    mySlidingArray.add(40)
    println(mySlidingArray.getList()) // Output: [20, 30, 40]

    // Angka 50 masuk, angka 20 tergeser
    mySlidingArray.add(50)
    println(mySlidingArray.getList()) // Output: [30, 40, 50]
}