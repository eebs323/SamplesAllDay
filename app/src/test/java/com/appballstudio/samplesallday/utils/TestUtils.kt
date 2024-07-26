package com.appballstudio.samplesallday.utils

import android.util.Log
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
fun FunSpec.initTest() {
    mockkStatic(Log::class)
    every { Log.v(any(), any()) } returns 0
    every { Log.d(any(), any()) } returns 0
    every { Log.i(any(), any()) } returns 0
    every { Log.e(any(), any()) } returns 0
    every { Log.e(any(), any(), any()) } returns 0

    listeners(object : TestListener {
        override suspend fun beforeTest(testCase: TestCase) {
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }

        override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            Dispatchers.resetMain()
        }
    })
}