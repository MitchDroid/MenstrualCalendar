package com.bloomcycle.app.domain.model

/** Menstrual flow intensity levels */
enum class FlowIntensity {
    NONE,
    SPOTTING,
    LIGHT,
    MEDIUM,
    HEAVY
}

/** Mood options for daily tracking */
enum class Mood {
    HAPPY,
    SAD,
    ANXIOUS,
    IRRITABLE,
    CALM,
    ENERGETIC
}

/** Trackable symptoms (multi-select) */
enum class Symptom {
    CRAMPS,
    HEADACHE,
    BLOATING,
    ACNE,
    MOOD_SWINGS,
    FATIGUE,
    TENDER_BREASTS,
    BACK_PAIN,
    NAUSEA,
    FOOD_CRAVINGS
}

/** Sexual activity types */
enum class SexualActivity {
    PROTECTED,
    UNPROTECTED,
    SOLO
}

/** Cervical mucus consistency */
enum class CervicalMucus {
    DRY,
    STICKY,
    CREAMY,
    WATERY,
    EGG_WHITE
}

/** Phases of the menstrual cycle */
enum class CyclePhase {
    MENSTRUAL,
    FOLLICULAR,
    OVULATION,
    LUTEAL
}

/** Fertility status indicators */
enum class FertilityStatus {
    LOW,
    MEDIUM,
    HIGH,
    PEAK
}

/** User's primary goal for using the app */
enum class UserGoal {
    TRACK_CYCLE,
    TRYING_TO_CONCEIVE,
    AVOID_PREGNANCY,
    MONITOR_HEALTH
}
