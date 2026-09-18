package com.example

import com.example.data.KaamGoRepository
import com.example.model.PaymentMethod
import com.example.model.PaymentStatus
import com.example.model.TaskCategory
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testCommissionSplit_15PercentOwner_85PercentRunner() {
    val repo = KaamGoRepository()

    // Create a task for Rs. 1,000 (Rs. 700 budget + Rs. 300 reward)
    val taskId = repo.createTask(
      title = "Groceries & Cold Drinks",
      description = "Fresh milk and juice",
      category = TaskCategory.BUY_FOR_ME,
      pickupShopName = "Al-Fatah",
      pickupAddress = "Johar Town, Lahore",
      deliveryAddress = "Block G, Johar Town, Lahore",
      productBudget = 700.0,
      helperReward = 300.0,
      deadlineTime = "45 mins",
      specialNotes = "Receipt required",
      paymentMethod = PaymentMethod.EASYPAISA
    )

    val task = repo.tasks.value.first { it.id == taskId }
    assertEquals(1000.0, task.totalCustomerCost, 0.001)
    assertEquals(150.0, task.ownerCommissionAmount, 0.001) // 15%
    assertEquals(850.0, task.runnerEarningAmount, 0.001)   // 85%
    assertEquals(PaymentStatus.PAID, task.paymentStatus)
  }

  @Test
  fun testDuplicateCallback_Idempotency() {
    val repo = KaamGoRepository()
    val initialLedgerCount = repo.ownerLedger.value.size

    val result = repo.testDuplicateCallback("EP-942817")
    assertTrue(result.contains("Duplicate callback ignored"))
    assertEquals(initialLedgerCount, repo.ownerLedger.value.size)
  }
}

