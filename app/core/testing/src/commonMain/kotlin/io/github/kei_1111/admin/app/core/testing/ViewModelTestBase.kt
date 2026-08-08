package io.github.kei_1111.admin.app.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * `viewModelScope` が参照する `Dispatchers.Main` を、ViewModel 生成より前に(`init` が
 * コルーチンを起動するため)テスト用ディスパッチャへ差し替え、テスト後に戻す。
 * commonTest では JUnit4 Rule が使えないため `@BeforeTest` / `@AfterTest` で行う
 * (`.claude/rules/mvi-testing.md` — Coroutine Setup)。
 */
abstract class ViewModelTestBase {

    @BeforeTest
    fun setUpMainDispatcher() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDownMainDispatcher() {
        Dispatchers.resetMain()
    }
}
