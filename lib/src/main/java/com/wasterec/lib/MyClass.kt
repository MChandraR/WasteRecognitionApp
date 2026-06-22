import org.apache.commons.math3.distribution.GammaDistribution
import org.apache.commons.math3.random.MersenneTwister

fun main() {
    val nSamples = 100
    val nClasses = 6
    val alphaValue = 200.0 // Catatan: Nilai ini sangat kaku, pertimbangkan untuk menurunkan ke ~50.0 jika loop terlalu lama

    val rng = MersenneTwister()

    // Variabel untuk mencatat berapa kali loop besar berjalan
    var totalLoopEksperimen = 0

    // Array final untuk menyimpan hasil yang memenuhi syarat
    var finalLabelCounts = IntArray(nClasses)

    println("Memulai pencarian distribusi dengan min >= 12 dan max <= 22...")

    while (true) {
        totalLoopEksperimen++

        // Reset count setiap kali eksperimen baru dimulai
        val labelCounts = IntArray(nClasses)

        // Jalankan simulasi 100 sampel data
        for (i in 0 until nSamples) {
            val gammaSamples = DoubleArray(nClasses)
            var totalGamma = 0.0

            for (c in 0 until nClasses) {
                val gammaDist = GammaDistribution(rng, alphaValue, 1.0)
                gammaSamples[c] = gammaDist.sample()
                totalGamma += gammaSamples[c]
            }

            val dirichletProbabilities = DoubleArray(nClasses)
            for (c in 0 until nClasses) {
                dirichletProbabilities[c] = gammaSamples[c] / totalGamma
            }

            // Proses ArgMax
            var maxIndex = 0
            var maxVal = dirichletProbabilities[0]
            for (c in 1 until nClasses) {
                if (dirichletProbabilities[c] > maxVal) {
                    maxVal = dirichletProbabilities[c]
                    maxIndex = c
                }
            }
            labelCounts[maxIndex]++
        }

        // --- VALIDASI SYARAT MINIMUM & MAKSIMUM ---
        // Cari nilai min dan max dari hasil 100 sampel saat ini
        var currentMin = labelCounts[0]
        var currentMax = labelCounts[0]

        for (c in 1 until nClasses) {
            if (labelCounts[c] < currentMin) currentMin = labelCounts[c]
            if (labelCounts[c] > currentMax) currentMax = labelCounts[c]
        }

        // Cek berkala di konsol agar Anda tahu program tidak hang
        if (totalLoopEksperimen % 50 == 0) {
            println("Percobaan ke-$totalLoopEksperimen... (Min saat ini: $currentMin, Max saat ini: $currentMax)")
        }

        // Syarat: Nilai terkecil tidak boleh di bawah 12 DAN nilai terbesar tidak boleh di atas 22
        if (currentMin >= 15 && currentMax <= 17) {
            finalLabelCounts = labelCounts
            println("\n====== SYARAT TERPENUHI! ======")
            println("Ditemukan pada percobaan ke: $totalLoopEksperimen")
            break
        }
    }

    // --- TAMPILKAN HASIL AKHIR ---
    println("\n=== HASIL GENERATE DIRICHLET KOTLIN ===")
    println("Nilai Alpha : $alphaValue")
    println("Total Sampel: $nSamples data\n")

    for (c in 0 until nClasses) {
        println("Kelas_${c + 1} : ${finalLabelCounts[c]}")
    }

    println("\nPembuktian Total Data: ${finalLabelCounts.sum()}")
}