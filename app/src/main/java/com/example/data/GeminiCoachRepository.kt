package com.example.data

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.model.HabitWithStats
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class HabitBreakdownSuggestion(
    val title: String,
    val triggerCue: String,
    val action: String,
    val category: String = "Health",
    val suggestedReminder: String = "08:00 AM",
    val targetDays: Int = 30
)

data class CoachInsight(
    val title: String,
    val message: String,
    val tips: List<String> = emptyList(),
    val actionLabel: String? = null
)

class GeminiCoachRepository(private val context: Context) {

    companion object {
        private const val TAG = "GeminiCoachRepository"
        private const val GEMINI_MODEL = "gemini-2.5-flash"
        private const val REST_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
    }

    /**
     * Break down a broad aspiration or goal (e.g. "get fit") into tiny, actionable micro-habits.
     */
    suspend fun breakdownGoal(goal: String): List<HabitBreakdownSuggestion> = withContext(Dispatchers.IO) {
        val trimmedGoal = goal.trim()
        if (trimmedGoal.isBlank()) return@withContext emptyList()

        val prompt = """
            You are Spark AI Habit Coach. Break down the user's broad goal into 3 specific, atomic micro-habits using behavioral science (BJ Fogg / James Clear tiny habit formula: "After [Current Anchor Cue], I will [Tiny 2-minute Action]").
            Goal: "$trimmedGoal"

            Respond ONLY in valid raw JSON array format with no markdown blocks:
            [
              {
                "title": "Short punchy habit title (e.g., Morning Pushups)",
                "triggerCue": "Anchor cue (e.g., After morning coffee)",
                "action": "Tiny specific action (e.g., Do 10 pushups on the rug)",
                "category": "Health / Fitness / Mindfulness / Learning / Productivity",
                "suggestedReminder": "08:00 AM",
                "targetDays": 30
              }
            ]
        """.trimIndent()

        val rawResponse = callModel(prompt)
        parseBreakdownSuggestions(rawResponse, trimmedGoal)
    }

    /**
     * Provide motivational streak-recovery coaching when a streak has dropped or user is struggling.
     */
    suspend fun getStreakRecoveryEncouragement(
        habitTitle: String,
        previousStreak: Int
    ): CoachInsight = withContext(Dispatchers.IO) {
        val prompt = """
            You are Spark AI Habit Coach. The user just missed their streak for their habit "$habitTitle" (previous best streak was $previousStreak days).
            Write an empathetic, energizing streak-recovery coaching message.
            Include:
            1. An encouraging, non-judgmental reframing (e.g., "The 2-Day Rule: Never miss twice").
            2. The "Emergency 2-Minute Version" they can do today to keep their flame alive.
            3. 3 practical momentum recovery tips.

            Keep it concise, energetic, and empowering.
        """.trimIndent()

        val response = callModel(prompt)
        if (response.isNotBlank() && !response.startsWith("ERROR:")) {
            CoachInsight(
                title = "Restart Your $habitTitle Flame",
                message = response,
                tips = listOf(
                    "Never miss twice: Doing it today preserves 90% of your neural pathway momentum.",
                    "Shrink to 2 minutes: Even 60 seconds counts as a win to rebuild the streak.",
                    "Anchor it to an existing routine you never skip."
                ),
                actionLabel = "Log Today's Recovery Check-in"
            )
        } else {
            getHeuristicRecoveryInsight(habitTitle, previousStreak)
        }
    }

    /**
     * Generate personalized weekly insights based on current habits and performance.
     */
    suspend fun getWeeklyInsightSummary(
        habits: List<HabitWithStats>,
        userName: String
    ): CoachInsight = withContext(Dispatchers.IO) {
        if (habits.isEmpty()) {
            return@withContext CoachInsight(
                title = "Welcome to Your Weekly Spark!",
                message = "You don't have any active habits yet. Add your first streak to receive weekly personalized AI coaching analysis, momentum scores, and flame insights.",
                tips = listOf(
                    "Start with 1-2 small habits to build consistent momentum.",
                    "Set reminders for consistent daily times."
                )
            )
        }

        val totalHabits = habits.size
        val activeStreaks = habits.count { it.currentStreak > 0 }
        val maxStreak = habits.maxOfOrNull { it.currentStreak } ?: 0
        val habitSummary = habits.joinToString("; ") {
            "${it.habit.title}: current streak ${it.currentStreak}d, total ${it.totalCompletedDays} days"
        }

        val prompt = """
            You are Spark AI Habit Coach. Provide a weekly progress insight report for ${userName.ifBlank { "User" }}.
            Stats:
            - Total Habits: $totalHabits
            - Active Streak Habits: $activeStreaks
            - Longest Active Streak: $maxStreak days
            - Habits: $habitSummary

            Write a motivating 3-sentence weekly summary highlighting what went well, identifying where to focus, and giving a motivating closing thought for the week.
        """.trimIndent()

        val response = callModel(prompt)
        if (response.isNotBlank() && !response.startsWith("ERROR:")) {
            CoachInsight(
                title = "Weekly Spark Momentum Report",
                message = response,
                tips = listOf(
                    "Consistency over intensity: Your longest active streak is $maxStreak days!",
                    "Anchor challenging habits to your most reliable morning or evening triggers."
                )
            )
        } else {
            getHeuristicWeeklyInsight(habits, userName, maxStreak)
        }
    }

    /**
     * Orchestrates model invocation:
     * 1. Firebase AI (if Firebase is initialized)
     * 2. Direct REST via BuildConfig.GEMINI_API_KEY (if key configured)
     * 3. Fallback heuristic response
     */
    private suspend fun callModel(prompt: String): String {
        // 1. Try Firebase AI if initialized
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                val app = FirebaseApp.getInstance()
                val model = FirebaseAI.getInstance(app).generativeModel(GEMINI_MODEL)
                val result = model.generateContent(prompt)
                val text = result.text
                if (!text.isNullOrBlank()) {
                    return text
                }
            }
        } catch (e: Throwable) {
            Log.d(TAG, "Firebase AI not available or error: ${e.message}")
        }

        // 2. Try Direct REST if GEMINI_API_KEY is available and not default placeholder
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val restResult = callGeminiRestApi(prompt, apiKey)
                if (restResult.isNotBlank()) {
                    return restResult
                }
            } catch (e: Throwable) {
                Log.w(TAG, "Gemini REST API error: ${e.message}")
            }
        }

        return ""
    }

    private fun callGeminiRestApi(prompt: String, apiKey: String): String {
        val url = URL("$REST_API_URL?key=$apiKey")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            connectTimeout = 30000
            readTimeout = 30000
            doOutput = true
        }

        val requestBody = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", prompt)
                        }
                        put(partObj)
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)
        }

        OutputStreamWriter(connection.outputStream).use { writer ->
            writer.write(requestBody.toString())
            writer.flush()
        }

        val responseCode = connection.responseCode
        if (responseCode in 200..299) {
            val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
            val json = JSONObject(response)
            val candidates = json.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            return parts?.optJSONObject(0)?.optString("text") ?: ""
        } else {
            val errorResponse = BufferedReader(InputStreamReader(connection.errorStream ?: connection.inputStream)).use { it.readText() }
            Log.w(TAG, "REST call failed code $responseCode: $errorResponse")
            return ""
        }
    }

    private fun parseBreakdownSuggestions(rawResponse: String, goal: String): List<HabitBreakdownSuggestion> {
        val cleanJson = rawResponse
            .replace("```json", "")
            .replace("```", "")
            .trim()

        if (cleanJson.startsWith("[")) {
            try {
                val array = JSONArray(cleanJson)
                val list = mutableListOf<HabitBreakdownSuggestion>()
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    list.add(
                        HabitBreakdownSuggestion(
                            title = item.optString("title", "Daily $goal"),
                            triggerCue = item.optString("triggerCue", "After waking up"),
                            action = item.optString("action", "Spend 5 minutes on $goal"),
                            category = item.optString("category", "General"),
                            suggestedReminder = item.optString("suggestedReminder", "08:00 AM"),
                            targetDays = item.optInt("targetDays", 30)
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                Log.w(TAG, "Failed to parse JSON response: ${e.message}")
            }
        }

        // Behavioral science heuristic fallback tailored to the goal
        return getHeuristicBreakdown(goal)
    }

    private fun getHeuristicBreakdown(goal: String): List<HabitBreakdownSuggestion> {
        val lower = goal.lowercase()
        return when {
            lower.contains("fit") || lower.contains("workout") || lower.contains("exercise") || lower.contains("gym") -> listOf(
                HabitBreakdownSuggestion(
                    title = "Morning Pushups",
                    triggerCue = "Right after pouring morning coffee",
                    action = "Do 10 pushups on the carpet",
                    category = "Health",
                    suggestedReminder = "07:30 AM",
                    targetDays = 30
                ),
                HabitBreakdownSuggestion(
                    title = "Post-Lunch Walk",
                    triggerCue = "Immediately after finishing lunch",
                    action = "Take a brisk 10-minute walk outside",
                    category = "Health",
                    suggestedReminder = "01:00 PM",
                    targetDays = 30
                ),
                HabitBreakdownSuggestion(
                    title = "Evening Mobility Stretch",
                    triggerCue = "Before brushing teeth at night",
                    action = "Do 5 minutes of hip and hamstring stretches",
                    category = "Health",
                    suggestedReminder = "09:30 PM",
                    targetDays = 21
                )
            )
            lower.contains("read") || lower.contains("book") -> listOf(
                HabitBreakdownSuggestion(
                    title = "Breakfast Reading",
                    triggerCue = "While eating breakfast",
                    action = "Read 5 pages of a non-fiction book",
                    category = "Learning",
                    suggestedReminder = "08:15 AM",
                    targetDays = 30
                ),
                HabitBreakdownSuggestion(
                    title = "Bedtime Chapter",
                    triggerCue = "After climbing into bed and setting alarm",
                    action = "Read 10 pages instead of scrolling phone",
                    category = "Learning",
                    suggestedReminder = "10:00 PM",
                    targetDays = 30
                )
            )
            lower.contains("meditat") || lower.contains("mindful") || lower.contains("stress") -> listOf(
                HabitBreakdownSuggestion(
                    title = "3-Deep Breath Reset",
                    triggerCue = "Right after sitting at my desk",
                    action = "Take 3 deep diaphragm breaths with eyes closed",
                    category = "Mindfulness",
                    suggestedReminder = "09:00 AM",
                    targetDays = 21
                ),
                HabitBreakdownSuggestion(
                    title = "Sunset Stillness",
                    triggerCue = "After closing my work computer",
                    action = "Sit in quiet meditation for 5 minutes",
                    category = "Mindfulness",
                    suggestedReminder = "05:30 PM",
                    targetDays = 30
                )
            )
            else -> listOf(
                HabitBreakdownSuggestion(
                    title = "Micro Step: $goal",
                    triggerCue = "After finishing morning coffee",
                    action = "Dedicate 5 uninterrupted minutes to $goal",
                    category = "General",
                    suggestedReminder = "08:30 AM",
                    targetDays = 30
                ),
                HabitBreakdownSuggestion(
                    title = "Midday Check-in: $goal",
                    triggerCue = "Immediately after lunch break",
                    action = "Complete 1 small milestone for $goal",
                    category = "Productivity",
                    suggestedReminder = "01:30 PM",
                    targetDays = 30
                )
            )
        }
    }

    private fun getHeuristicRecoveryInsight(habitTitle: String, previousStreak: Int): CoachInsight {
        return CoachInsight(
            title = "Restart Your $habitTitle Flame",
            message = "A broken streak is just data, not defeat. Research in habit formation proves that missing a single day does not erase your neurological habit pathways. The only danger is missing twice. Use the '2-Minute Rule' today: do an ultra-small version of $habitTitle to restart the flame immediately!",
            tips = listOf(
                "The 2-Day Rule: Never allow two consecutive missed days.",
                "Shrink the resistance: Do a 60-second version right now.",
                "Keep your identity: You are still someone who practices $habitTitle."
            ),
            actionLabel = "Do 2-Minute Recovery Check-in"
        )
    }

    private fun getHeuristicWeeklyInsight(
        habits: List<HabitWithStats>,
        userName: String,
        maxStreak: Int
    ): CoachInsight {
        val totalCompletions = habits.sumOf { it.totalCompletedDays }
        val name = if (userName.isNotBlank()) userName else "Champion"
        return CoachInsight(
            title = "Weekly Spark Momentum Report",
            message = "Great effort this week, $name! Across your ${habits.size} habits, you've completed $totalCompletions total check-ins, keeping your longest active flame strong at $maxStreak days. Keep building consistency by protecting your morning anchor routines.",
            tips = listOf(
                "Current Best Flame: $maxStreak consecutive days!",
                "Stack your habits: Connect challenging habits to your easiest automatic daily routine.",
                "Celebrate small wins: Every check-in reinforces your habit identity."
            )
        )
    }
}
