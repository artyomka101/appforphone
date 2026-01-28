package com.example.myapplication.ui.state

import com.example.myapplication.domain.model.Habit
import com.example.myapplication.domain.model.UserProfile
import com.example.myapplication.domain.model.SimpleNotification
import java.time.LocalDate

// UiState для главного экрана
data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val habitCompletions: Map<Long, Boolean> = emptyMap(),
    val completionCounts: Map<Long, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// UiState для профиля
data class ProfileUiState(
    val profile: UserProfile? = null,
    val totalHabits: Int = 0,
    val activeHabits: Int = 0,
    val completedToday: Int = 0,
    val totalCompletions: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

// UiState для уведомлений
data class NotificationsUiState(
    val notifications: List<SimpleNotification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// UiEvent для главного экрана
sealed interface HabitUiEvent {
    data class ToggleHabitCompletion(val habitId: Long) : HabitUiEvent
    data class DeleteHabit(val habit: Habit) : HabitUiEvent
    data class SetSelectedDate(val date: LocalDate) : HabitUiEvent
    data class AddHabit(val habit: Habit) : HabitUiEvent
    data class UpdateHabit(val habit: Habit) : HabitUiEvent
    object LoadHabits : HabitUiEvent
    object ClearError : HabitUiEvent
}

// UiEvent для профиля
sealed interface ProfileUiEvent {
    data class UpdateUserName(val name: String) : ProfileUiEvent
    object LoadProfile : ProfileUiEvent
    object ClearError : ProfileUiEvent
}

// UiEvent для уведомлений
sealed interface NotificationsUiEvent {
    data class DeleteNotification(val notificationId: Long) : NotificationsUiEvent
    object LoadNotifications : NotificationsUiEvent
    object ClearAllNotifications : NotificationsUiEvent
    object ClearError : NotificationsUiEvent
}




