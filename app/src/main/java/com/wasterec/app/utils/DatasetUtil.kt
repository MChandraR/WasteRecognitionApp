package com.wasterec.app.utils
import org.apache.commons.math3.distribution.GammaDistribution
import org.apache.commons.math3.random.MersenneTwister

class DatasetUtil {

    fun main() {
        val k = 5 // Misal membagi data ke 5 klien
        val alpha = 0.5 // Parameter konsentrasi

        val sampler = DirichletSampler(alpha)
        val proportions = sampler.sample(k)

        println("Proporsi Pembagian Data (Alpha=$alpha):")
        proportions.forEachIndexed { index, prop ->
            val percent = (prop * 100).format(2)
            println("Klien $index: $percent%")
        }

        println("Total: ${proportions.sum()}")
    }

    // Helper untuk format desimal
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)
