package com.wasterec.app.utils

import org.apache.commons.math3.distribution.GammaDistribution
import org.apache.commons.math3.random.MersenneTwister

class DirichletSampler(private val alpha: Double) {
    private val random = MersenneTwister()

    /**
     * Menghasilkan array proporsi yang berjumlah total 1.0
     * @param k Jumlah kategori atau klien (bins)
     */
    fun sample(k: Int): DoubleArray {
        // Dirichlet dapat disimulasikan menggunakan Gamma Distribution
        // Jika X_i ~ Gamma(alpha, 1), maka Y_i = X_i / sum(X) ~ Dirichlet(alpha)
        val gamma = GammaDistribution(random, alpha, 1.0)

        val samples = DoubleArray(k) { gamma.sample() }
        val sum = samples.sum()

        // Normalisasi agar totalnya menjadi 1.0
        return samples.map { it / sum }.toDoubleArray()
    }
}