package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.KaamGoRepository
import com.example.model.TaskCategory
import com.example.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("KaamGo", appName)
  }

  @Test
  fun `repository initial state and task creation`() {
    val repo = KaamGoRepository()
    val initialTasks = repo.tasks.value
    assertTrue(initialTasks.isNotEmpty())

    val newTaskId = repo.createTask(
      title = "Panadol and Milk",
      description = "Get from Imtiaz Supermarket",
      category = TaskCategory.BUY_FOR_ME,
      pickupShop = "Imtiaz Store",
      pickupAddress = "Johar Town, Lahore",
      deliveryAddress = "House 12, Block R",
      budget = 350.0,
      reward = 150.0,
      deadline = "Within 1 hour",
      notes = "Receipt required"
    )

    assertNotNull(newTaskId)
    val created = repo.tasks.value.find { it.id == newTaskId }
    assertNotNull(created)
    assertEquals("Panadol and Milk", created?.title)
  }
}

