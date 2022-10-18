package com.example.numberstesttask.numbers.presentation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

class NumbersViewModelTest {

    private lateinit var communications: TestNumbersCommunications
    private lateinit var interactor: TestNumbersInteractor
    private lateinit var viewModel: NumbersViewModel

    @BeforeEach
    fun setUp() {
        communications = TestNumbersCommunications()
        interactor = TestNumbersInteractor()
        viewModel = NumbersViewModel(communications, interactor)
    }

    @Test
    fun testFirstInitAppShowAndHideProgressBarSuccessful() {
        viewModel.init(isFirstRun = true)
        assertEquals(1, communications.progressCalledList.size)
        assertEquals(true, communications.progressCalledList[0])
        assertEquals(2, communications.progressCalledList.size)
        assertEquals(false, communications.progressCalledList[1])
    }

    @Test
    fun testFirstInitAppShowEmptyNumberListSuccessful() {
        viewModel.init(isFirstRun = true)
        assertEquals(1, communications.stateCalledList.size)
        assertEquals(UIstate.Success(), communications.stateCalledList[0])

        assertEquals(0, communications.numbersList.size)
        assertEquals(1, communications.timesShowNumbersList)
    }

    @Test
    fun testFirstInitAppAndTapRandomButtonSuccessful() {
        viewModel.init(isFirstRun = true)
        viewModel.fetchRandomNumberData()
        assertEquals(3, communications.progressCalledList.size)
        assertEquals(true, communications.progressCalledList[2])

        assertEquals(1, interactor.fetchAboutRandomNumberCalledList.size)

        assertEquals(4, communications.progressCalledList.size)
        assertEquals(false, communications.progressCalledList[3])

        assertEquals(2, communications.stateCalledList.size)
        assertEquals(UIstate.Error(/* todo message */), communications.stateCalledList[1])

        assertEquals(1, communications.timesShowNumbersList)
    }

    @Test
    fun testInitAppWithNotEmptyNumbersListSuccessful() {
        viewModel.init(isFirstRun = true)
        viewModel.fetchRandomNumberData()
        viewModel.init(isFirstRun = false)
        assertEquals(4, communications.progressCalledList.size)
        assertEquals(2, communications.stateCalledList.size)
        assertEquals(1, communications.timesShowNumbersList)
    }

    @Test
    fun testInitAppAndTapGetFactWithEmptyNumber() {
        viewModel.fetchFact("")
        assertEquals(0, interactor.fetchAboutNumberCalledList.size)
        assertEquals(0, communications.progressCalledList.size)
        assertEquals(1, communications.stateCalledList.size)
        assertEquals(UIstate.Error("entered number is empty"), communications.stateCalledList[0])
        assertEquals(0, communications.timesShowNumbersList)
    }

    @Test
    fun testInitAppAndTapGetFactWithSomeNumber() {
        val randomNumber = Random.nextInt(Int.MAX_VALUE)

        interactor.changeExpectedResult(NumbersResult.Success(listOf(Number("$randomNumber", "fact about $randomNumber"))))
        viewModel.fetchFact(randomNumber)

        assertEquals(1, communications.progressCalledList.size)
        assertEquals(true, communications.progressCalledList[0])

        assertEquals(1, interactor.fetchAboutNumberCalledList.size)
        assertEquals(
            Number("$randomNumber", "fact about $randomNumber"),
            interactor.fetchAboutNumberCalledList[0]
        )

        assertEquals(2, communications.progressCalledList.size)
        assertEquals(false, communications.progressCalledList[1])

        assertEquals(1, communications.stateCalledList.size)
        assertEquals(UIstate.Success(), communications.stateCalledList[0])

        assertEquals(1, communications.timesShowNumbersList)
        assertEquals(
            NumberUI("$randomNumber", "fact about $randomNumber"),
            communications.numbersList[0]
        )
    }

    class TestNumbersCommunications : NumbersCommunications {
        val progressCalledList = mutableListOf<Boolean>()
        val stateCalledList = mutableListOf<Boolean>()
        val numbersList = mutableListOf<NumberUI>()
        var timesShowNumbersList = 0

        override fun showProgress(show: Boolean) {
            progressCalledList.add(show)
        }

        override fun showState(state: UIstate) {
            stateCalledList.add(state)
        }

        override fun showList(list: List<NumberUI>) {
            timesShowNumbersList++
            numbersList.addAll(list)
        }
    }

    class TestNumbersInteractor : NumbersInteraction {
        var result: NumbersResult = NumbersResult.Success()
        val initCalledList = mutableListOf<NumbersResult>()
        val fetchAboutNumberCalledList = mutableListOf<NumbersResult>()
        val fetchAboutRandomNumberCalledList = mutableListOf<NumbersResult>()

        fun changeExpectedResult(newResult: NumbersResult) {
            result = newResult
        }

        override suspend fun init(): NumbersResult {
            initCalledList.add(result)
            return result
        }

        override suspend fun factAboutNumber(number: String): NumbersResult {
            fetchAboutNumberCalledList.add(result)
            return result
        }

        override suspend fun factAboutRandomNumber(): NumbersResult {
            fetchAboutRandomNumberCalledList.add(result)
            return result
        }
    }
}