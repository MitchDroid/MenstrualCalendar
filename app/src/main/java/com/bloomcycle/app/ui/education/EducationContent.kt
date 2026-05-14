package com.bloomcycle.app.ui.education

import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.Symptom

// ── Data Models ─────────────────────────────────────────────────

data class PhaseGuide(
    val phase: CyclePhase,
    val title: String,
    val emoji: String,
    val durationInfo: String,
    val description: String,
    val whatHappens: List<String>,
    val commonSymptoms: List<String>,
    val selfCareTips: List<String>,
    val nutritionTips: List<String>,
    val exerciseTips: List<String>
)

data class SymptomGuide(
    val symptom: Symptom,
    val displayName: String,
    val emoji: String,
    val description: String,
    val whyItHappens: String,
    val managementTips: List<String>,
    val whenToSeeDoctor: String
)

data class HealthTip(
    val title: String,
    val emoji: String,
    val content: String,
    val category: TipCategory,
    val applicablePhases: List<CyclePhase>
)

enum class TipCategory {
    NUTRITION,
    EXERCISE,
    SELF_CARE,
    SLEEP,
    MENTAL_HEALTH
}

// ── Static Content Provider ─────────────────────────────────────

object EducationContentProvider {

    // ── Cycle Phase Guides ──────────────────────────────────────

    val phaseGuides: List<PhaseGuide> = listOf(
        PhaseGuide(
            phase = CyclePhase.MENSTRUAL,
            title = "Menstrual Phase",
            emoji = "\uD83C\uDF3A",
            durationInfo = "Days 1–5 (typically 3–7 days)",
            description = "The menstrual phase marks the beginning of your cycle. The uterine lining sheds, resulting in your period. Hormone levels (estrogen and progesterone) are at their lowest.",
            whatHappens = listOf(
                "The uterine lining (endometrium) sheds through the cervix and vagina",
                "Estrogen and progesterone levels drop to their lowest point",
                "The pituitary gland begins releasing FSH to stimulate new follicle growth",
                "Your body temperature is typically at its lowest"
            ),
            commonSymptoms = listOf(
                "Menstrual cramps (due to uterine contractions)",
                "Lower back pain",
                "Fatigue and low energy",
                "Bloating and water retention",
                "Headaches or migraines",
                "Mood changes"
            ),
            selfCareTips = listOf(
                "Rest and prioritize sleep — your body is working hard",
                "Use a heating pad on your lower abdomen for cramp relief",
                "Stay hydrated — aim for 8+ glasses of water daily",
                "Practice gentle stretching or yoga",
                "Take warm baths to soothe muscle tension",
                "Be kind to yourself — it's okay to slow down"
            ),
            nutritionTips = listOf(
                "Iron-rich foods (spinach, lentils, red meat) to replenish lost iron",
                "Omega-3 fatty acids (salmon, walnuts) to reduce inflammation",
                "Dark chocolate (in moderation) for magnesium and mood boost",
                "Ginger tea to help with nausea and cramps",
                "Avoid excessive caffeine and salt, which can worsen bloating"
            ),
            exerciseTips = listOf(
                "Light walking or gentle yoga",
                "Stretching exercises for the lower back and hips",
                "Swimming (if comfortable)",
                "Avoid high-intensity workouts if fatigued",
                "Listen to your body — movement should feel good, not forced"
            )
        ),
        PhaseGuide(
            phase = CyclePhase.FOLLICULAR,
            title = "Follicular Phase",
            emoji = "\uD83C\uDF31",
            durationInfo = "Days 1–13 (overlaps with menstruation)",
            description = "The follicular phase begins on day 1 and lasts until ovulation. After menstruation ends, rising estrogen levels stimulate the uterine lining to thicken and prepare for a potential pregnancy. Energy and mood typically improve during this time.",
            whatHappens = listOf(
                "FSH stimulates ovarian follicles to develop",
                "One dominant follicle emerges and begins producing estrogen",
                "Rising estrogen thickens the uterine lining",
                "Energy levels, mood, and creativity often increase",
                "Cervical mucus changes from dry to sticky to creamy"
            ),
            commonSymptoms = listOf(
                "Increasing energy and motivation",
                "Improved mood and sociability",
                "Higher pain tolerance",
                "Clearer skin as estrogen rises",
                "Increased cervical mucus"
            ),
            selfCareTips = listOf(
                "Channel your rising energy into projects and social activities",
                "This is a great time to try new things and set goals",
                "Start or intensify exercise routines",
                "Focus on creative projects — estrogen boosts cognitive function",
                "Schedule important meetings or presentations during this phase"
            ),
            nutritionTips = listOf(
                "Fresh, vibrant foods — salads, fermented foods, lean proteins",
                "Phytoestrogen-rich foods (flaxseed, soy) support estrogen metabolism",
                "Probiotics for gut health",
                "Light, energizing meals that match your rising energy",
                "Vitamin E-rich foods (almonds, sunflower seeds) for follicle health"
            ),
            exerciseTips = listOf(
                "High-intensity interval training (HIIT)",
                "Running, cycling, or dance classes",
                "Strength training — your body recovers faster now",
                "Try new or challenging workouts",
                "Group fitness classes to match your social energy"
            )
        ),
        PhaseGuide(
            phase = CyclePhase.OVULATION,
            title = "Ovulation Phase",
            emoji = "\u2728",
            durationInfo = "Around day 14 (lasts 24–48 hours)",
            description = "Ovulation is when a mature egg is released from the ovary. This is the peak of your fertility window. Estrogen peaks, triggering a surge of LH (luteinizing hormone), and you may feel your most energetic, social, and confident.",
            whatHappens = listOf(
                "A surge in LH triggers the release of a mature egg from the ovary",
                "The egg travels down the fallopian tube",
                "Estrogen reaches its peak level",
                "Basal body temperature rises slightly (0.5–1°F)",
                "This is the most fertile time — the egg survives 12–24 hours"
            ),
            commonSymptoms = listOf(
                "Peak energy and libido",
                "Egg-white cervical mucus (stretchy, clear)",
                "Mild pelvic pain or twinge (mittelschmerz)",
                "Slight temperature rise",
                "Heightened senses",
                "Increased confidence and sociability"
            ),
            selfCareTips = listOf(
                "If trying to conceive, this is your most fertile window",
                "If avoiding pregnancy, use reliable contraception",
                "Take advantage of peak energy for important tasks",
                "Track cervical mucus changes for cycle awareness",
                "Note your basal body temperature for pattern recognition"
            ),
            nutritionTips = listOf(
                "Anti-inflammatory foods to support the ovulation process",
                "Zinc-rich foods (pumpkin seeds, oysters) for egg quality",
                "B-vitamins (leafy greens, eggs) for energy metabolism",
                "Fiber-rich foods to help metabolize the estrogen surge",
                "Stay well-hydrated"
            ),
            exerciseTips = listOf(
                "Peak performance time — go for personal records",
                "High-intensity training and competitive sports",
                "Strength training with heavier weights",
                "Power yoga or Pilates",
                "Take advantage of natural energy and endurance peaks"
            )
        ),
        PhaseGuide(
            phase = CyclePhase.LUTEAL,
            title = "Luteal Phase",
            emoji = "\uD83C\uDF19",
            durationInfo = "Days 15–28 (typically 12–14 days)",
            description = "After ovulation, the empty follicle transforms into the corpus luteum, producing progesterone to maintain the uterine lining. If no pregnancy occurs, hormone levels drop, triggering menstruation. PMS symptoms are most common in the late luteal phase.",
            whatHappens = listOf(
                "The corpus luteum produces progesterone and some estrogen",
                "The uterine lining becomes rich with blood and nutrients",
                "Body temperature remains slightly elevated",
                "If the egg isn't fertilized, the corpus luteum breaks down",
                "Dropping hormones trigger PMS symptoms in the late luteal phase"
            ),
            commonSymptoms = listOf(
                "PMS symptoms: bloating, breast tenderness, mood swings",
                "Food cravings (especially carbs and sweets)",
                "Fatigue and lower energy",
                "Acne breakouts",
                "Anxiety or irritability",
                "Difficulty concentrating"
            ),
            selfCareTips = listOf(
                "Prioritize sleep — aim for 8+ hours",
                "Practice stress-reducing activities (meditation, journaling)",
                "Be patient with yourself — PMS is real and valid",
                "Use magnesium supplements (consult your doctor first)",
                "Warm baths and comfort activities in the late luteal phase",
                "Reduce commitments if possible during the last few days"
            ),
            nutritionTips = listOf(
                "Complex carbohydrates (sweet potatoes, brown rice) for serotonin",
                "Magnesium-rich foods (dark chocolate, nuts) for mood and cramps",
                "Calcium-rich foods (dairy, fortified plant milk) ease PMS",
                "Reduce salt intake to minimize bloating",
                "Avoid excessive sugar, caffeine, and alcohol",
                "Vitamin B6 (bananas, chickpeas) supports progesterone"
            ),
            exerciseTips = listOf(
                "Moderate exercise: walking, yoga, swimming",
                "Reduce intensity as energy drops in late luteal phase",
                "Stretching and flexibility work",
                "Pilates or barre classes",
                "Listen to your body — skip high-intensity if fatigued"
            )
        )
    )

    // ── Symptom Guides ──────────────────────────────────────────

    val symptomGuides: List<SymptomGuide> = listOf(
        SymptomGuide(
            symptom = Symptom.CRAMPS,
            displayName = "Cramps",
            emoji = "\uD83E\uDD1F",
            description = "Menstrual cramps (dysmenorrhea) are throbbing or cramping pains in the lower abdomen. They're one of the most common menstrual symptoms, affecting up to 80% of people who menstruate.",
            whyItHappens = "During menstruation, the uterus contracts to help shed its lining. Prostaglandins — hormone-like substances involved in pain and inflammation — trigger these contractions. Higher prostaglandin levels are associated with more severe cramps.",
            managementTips = listOf(
                "Apply heat (heating pad or warm water bottle) to the lower abdomen",
                "Over-the-counter pain relief (ibuprofen works best when taken early)",
                "Gentle exercise increases blood flow and releases endorphins",
                "Try anti-inflammatory foods: turmeric, ginger, omega-3 rich fish",
                "Magnesium supplements may reduce cramp severity",
                "Practice deep breathing and progressive muscle relaxation"
            ),
            whenToSeeDoctor = "See a doctor if cramps are severe enough to interfere with daily activities, aren't relieved by over-the-counter medication, or are accompanied by heavy bleeding, fever, or unusual discharge."
        ),
        SymptomGuide(
            symptom = Symptom.HEADACHE,
            displayName = "Headache",
            emoji = "\uD83E\uDD15",
            description = "Menstrual headaches and migraines are triggered by hormonal fluctuations, particularly the drop in estrogen that occurs just before and during menstruation.",
            whyItHappens = "The sudden drop in estrogen levels before your period can trigger headaches and migraines. Estrogen influences serotonin and other brain chemicals that affect pain perception. Some people are more sensitive to these hormonal shifts than others.",
            managementTips = listOf(
                "Stay hydrated — dehydration worsens headaches",
                "Maintain consistent sleep schedules",
                "Manage stress with relaxation techniques",
                "Consider magnesium supplementation (400mg daily)",
                "Apply cold compresses to the forehead or neck",
                "Limit triggers: caffeine withdrawal, alcohol, processed foods"
            ),
            whenToSeeDoctor = "Seek medical attention if headaches are sudden and severe (\"worst headache of your life\"), accompanied by vision changes, weakness, or confusion, or if they significantly worsen from your usual pattern."
        ),
        SymptomGuide(
            symptom = Symptom.BLOATING,
            displayName = "Bloating",
            emoji = "\uD83C\uDF88",
            description = "Menstrual bloating is a feeling of fullness or swelling in the abdomen, commonly experienced before and during menstruation. It's caused by hormonal changes affecting water retention and digestion.",
            whyItHappens = "Rising progesterone in the luteal phase slows digestion and causes water retention. Estrogen fluctuations also affect fluid balance. Additionally, prostaglandins can affect the digestive tract, leading to gas and bloating.",
            managementTips = listOf(
                "Reduce sodium intake to minimize water retention",
                "Drink plenty of water (counterintuitively, this helps reduce bloating)",
                "Eat smaller, more frequent meals",
                "Avoid carbonated drinks and gas-producing foods (beans, cruciferous veggies)",
                "Light exercise like walking can help move gas through the digestive tract",
                "Potassium-rich foods (bananas, avocado) help balance sodium"
            ),
            whenToSeeDoctor = "Consult a doctor if bloating is severe, persistent beyond your period, accompanied by significant pain, or if you notice unusual changes in your digestion patterns."
        ),
        SymptomGuide(
            symptom = Symptom.ACNE,
            displayName = "Acne",
            emoji = "\uD83D\uDCA2",
            description = "Hormonal acne typically flares up in the week before your period. It tends to appear on the lower face — jawline, chin, and cheeks — and consists of deep, tender bumps rather than surface-level pimples.",
            whyItHappens = "Before your period, estrogen and progesterone drop while androgens (like testosterone) remain relatively stable. This shift stimulates the sebaceous glands to produce more sebum (oil), which can clog pores and lead to breakouts.",
            managementTips = listOf(
                "Maintain a consistent skincare routine (gentle cleanser, moisturizer)",
                "Use products with salicylic acid or benzoyl peroxide for breakouts",
                "Avoid touching your face and change pillowcases frequently",
                "Reduce sugar and dairy intake, which may worsen hormonal acne",
                "Zinc supplements may help reduce inflammatory acne",
                "Don't pick or squeeze — this causes scarring and spreading"
            ),
            whenToSeeDoctor = "See a dermatologist if acne is severe, painful, leaves scars, or doesn't respond to over-the-counter treatments. Hormonal treatments may be appropriate."
        ),
        SymptomGuide(
            symptom = Symptom.MOOD_SWINGS,
            displayName = "Mood Swings",
            emoji = "\uD83C\uDFA2",
            description = "Mood swings during your menstrual cycle involve rapid shifts between emotions — feeling happy one moment and tearful or irritable the next. They're most common in the late luteal phase (PMS).",
            whyItHappens = "Fluctuating estrogen and progesterone levels directly affect neurotransmitters like serotonin (the \"feel-good\" chemical) and GABA. The drop in estrogen before your period decreases serotonin production, contributing to mood instability, anxiety, and irritability.",
            managementTips = listOf(
                "Regular aerobic exercise boosts serotonin and endorphins",
                "Practice mindfulness meditation or deep breathing",
                "Maintain stable blood sugar with regular, balanced meals",
                "Prioritize 7–9 hours of quality sleep",
                "Limit caffeine and alcohol, especially in the luteal phase",
                "Journal your feelings to identify patterns and triggers",
                "Connect with supportive friends or a therapist"
            ),
            whenToSeeDoctor = "Seek help if mood swings are severe enough to affect relationships or work, if you experience thoughts of self-harm, or if symptoms align with PMDD (premenstrual dysphoric disorder), which is a more severe form of PMS."
        ),
        SymptomGuide(
            symptom = Symptom.FATIGUE,
            displayName = "Fatigue",
            emoji = "\uD83D\uDE34",
            description = "Cycle-related fatigue is an overwhelming feeling of tiredness that goes beyond normal sleepiness. It's especially common during menstruation and the late luteal phase.",
            whyItHappens = "Progesterone has a natural sedating effect and is at its highest during the luteal phase. Iron loss during menstruation can contribute to fatigue. Poor sleep quality (due to hormone-related temperature changes and discomfort) and the energy expenditure of hormonal processes all play a role.",
            managementTips = listOf(
                "Prioritize consistent sleep schedules (even on weekends)",
                "Eat iron-rich foods during and after your period",
                "Take short power naps (15–20 minutes) if needed",
                "Stay physically active — movement actually increases energy",
                "Reduce sugar spikes that cause energy crashes",
                "Consider vitamin B12 and iron levels with your doctor"
            ),
            whenToSeeDoctor = "See a doctor if fatigue is severe, persistent throughout your entire cycle, or interferes with daily activities. This could indicate anemia, thyroid issues, or other conditions."
        ),
        SymptomGuide(
            symptom = Symptom.TENDER_BREASTS,
            displayName = "Tender Breasts",
            emoji = "\uD83E\uDE77",
            description = "Breast tenderness (mastalgia) is a common premenstrual symptom involving swelling, sensitivity, and aching in the breast tissue. It typically occurs in the luteal phase and resolves once menstruation begins.",
            whyItHappens = "Rising progesterone after ovulation causes the breast ducts to enlarge and milk glands to swell. Estrogen causes breast ducts to expand. Together, these hormonal changes lead to fluid retention in the breast tissue, creating tenderness and sensitivity.",
            managementTips = listOf(
                "Wear a well-fitting, supportive bra (especially during exercise)",
                "Apply cold compresses to reduce swelling",
                "Reduce caffeine intake, which may worsen breast pain",
                "Evening primrose oil may help (consult your doctor)",
                "Gentle massage can improve circulation",
                "Reduce salt intake to minimize fluid retention"
            ),
            whenToSeeDoctor = "See a doctor if you find a new lump, notice nipple discharge, experience pain in one specific area, or if tenderness is severe and doesn't follow your menstrual cycle pattern."
        ),
        SymptomGuide(
            symptom = Symptom.BACK_PAIN,
            displayName = "Back Pain",
            emoji = "\uD83D\uDECB",
            description = "Lower back pain during menstruation is caused by the same uterine contractions that cause cramps. Pain radiates from the abdomen to the lower back and thighs.",
            whyItHappens = "Prostaglandins released during menstruation cause uterine contractions that can radiate pain to the lower back. The uterus sits close to the spine, and inflammation can affect surrounding nerves and muscles. Relaxin hormone can also make ligaments more lax.",
            managementTips = listOf(
                "Apply heat to the lower back (heating pad, warm bath)",
                "Gentle stretches: cat-cow, child's pose, knee-to-chest",
                "Anti-inflammatory medications (ibuprofen, naproxen)",
                "Gentle walking to improve blood flow",
                "Sleep with a pillow between your knees for alignment",
                "Practice good posture throughout the day"
            ),
            whenToSeeDoctor = "See a doctor if back pain is severe, radiates down your legs, is accompanied by numbness or tingling, or persists throughout your entire cycle."
        ),
        SymptomGuide(
            symptom = Symptom.NAUSEA,
            displayName = "Nausea",
            emoji = "\uD83E\uDD22",
            description = "Some people experience nausea during their period, often alongside cramps. It can range from mild queasiness to significant discomfort that affects eating.",
            whyItHappens = "Prostaglandins not only cause uterine contractions but can also affect the stomach and intestines, leading to nausea, diarrhea, or vomiting. Hormonal fluctuations can also slow gastric emptying, contributing to the queasy feeling.",
            managementTips = listOf(
                "Eat small, frequent meals to keep blood sugar stable",
                "Ginger (tea, supplements, or ginger ale) is a natural anti-nausea remedy",
                "Peppermint tea can soothe the stomach",
                "Avoid fatty, spicy, or heavily processed foods",
                "Take anti-inflammatory medications with food",
                "Acupressure on the inner wrist (P6 point) may help"
            ),
            whenToSeeDoctor = "See a doctor if nausea is severe, causes frequent vomiting, leads to dehydration, or prevents you from keeping food or medication down."
        ),
        SymptomGuide(
            symptom = Symptom.FOOD_CRAVINGS,
            displayName = "Food Cravings",
            emoji = "\uD83C\uDF69",
            description = "Intense food cravings — especially for carbs, chocolate, and salty foods — are a hallmark of PMS. They typically peak in the late luteal phase (the week before your period).",
            whyItHappens = "Dropping serotonin levels in the late luteal phase drive cravings for carbohydrates and sugar, which temporarily boost serotonin. Magnesium levels also dip (which may explain chocolate cravings). Additionally, your metabolic rate slightly increases, requiring more calories.",
            managementTips = listOf(
                "Satisfy cravings mindfully — a small portion of what you want is okay",
                "Choose complex carbs over refined sugars for lasting satisfaction",
                "Dark chocolate satisfies cravings while providing magnesium",
                "Eat balanced meals with protein, fiber, and healthy fats",
                "Stay hydrated — thirst is sometimes mistaken for hunger",
                "Plan satisfying snacks in advance for the luteal phase"
            ),
            whenToSeeDoctor = "See a doctor if cravings are compulsive, lead to binge eating, cause distress, or are accompanied by significant weight changes."
        )
    )

    // ── Phase-Aware Health Tips ──────────────────────────────────

    val healthTips: List<HealthTip> = listOf(
        // ── Menstrual Phase Tips ────────────────────────────────
        HealthTip(
            title = "Iron-Rich Recovery Meals",
            emoji = "\uD83E\uDD57",
            content = "During your period, replenish lost iron with spinach salads, lentil soup, or lean red meat. Pair with vitamin C foods (citrus, bell peppers) to boost absorption.",
            category = TipCategory.NUTRITION,
            applicablePhases = listOf(CyclePhase.MENSTRUAL)
        ),
        HealthTip(
            title = "Cozy Movement",
            emoji = "\uD83E\uDDD8",
            content = "Gentle yoga poses like child's pose, reclined butterfly, and cat-cow can ease cramps and boost mood without depleting your energy.",
            category = TipCategory.EXERCISE,
            applicablePhases = listOf(CyclePhase.MENSTRUAL)
        ),
        HealthTip(
            title = "Rest Without Guilt",
            emoji = "\uD83D\uDCA4",
            content = "Your body is doing important work. Allow yourself extra rest — an early bedtime, a warm bath, or a slow morning can make a big difference.",
            category = TipCategory.SELF_CARE,
            applicablePhases = listOf(CyclePhase.MENSTRUAL)
        ),
        HealthTip(
            title = "Sleep Sanctuary",
            emoji = "\uD83C\uDF19",
            content = "Period discomfort can disrupt sleep. Try a heating pad on your abdomen, magnesium before bed, and keep your room cool (65–68°F / 18–20°C).",
            category = TipCategory.SLEEP,
            applicablePhases = listOf(CyclePhase.MENSTRUAL)
        ),
        HealthTip(
            title = "Emotional Check-In",
            emoji = "\uD83D\uDCDD",
            content = "Low hormones can bring low moods. Journal your feelings, practice gratitude, or simply acknowledge that this phase is temporary and that's okay.",
            category = TipCategory.MENTAL_HEALTH,
            applicablePhases = listOf(CyclePhase.MENSTRUAL)
        ),

        // ── Follicular Phase Tips ───────────────────────────────
        HealthTip(
            title = "Fresh & Fermented Foods",
            emoji = "\uD83E\uDD66",
            content = "Your digestion is at its best! Enjoy fresh salads, fermented foods (kimchi, yogurt, sauerkraut), and light proteins to match your rising energy.",
            category = TipCategory.NUTRITION,
            applicablePhases = listOf(CyclePhase.FOLLICULAR)
        ),
        HealthTip(
            title = "Challenge Yourself",
            emoji = "\uD83C\uDFC3",
            content = "Rising estrogen means faster recovery and higher motivation. Try HIIT, running, or a new fitness class. Your body is primed for peak performance.",
            category = TipCategory.EXERCISE,
            applicablePhases = listOf(CyclePhase.FOLLICULAR)
        ),
        HealthTip(
            title = "Creative Energy Boost",
            emoji = "\uD83C\uDFA8",
            content = "Estrogen enhances verbal fluency and creativity. Channel this energy into brainstorming, writing, art, or tackling complex problems at work.",
            category = TipCategory.SELF_CARE,
            applicablePhases = listOf(CyclePhase.FOLLICULAR)
        ),
        HealthTip(
            title = "Optimize Your Schedule",
            emoji = "\u2B50",
            content = "With increasing energy, this is the best time for job interviews, presentations, and social gatherings. Plan important events during this phase.",
            category = TipCategory.MENTAL_HEALTH,
            applicablePhases = listOf(CyclePhase.FOLLICULAR)
        ),

        // ── Ovulation Phase Tips ────────────────────────────────
        HealthTip(
            title = "Peak Performance Fuel",
            emoji = "\u26A1",
            content = "Support your energy peak with anti-inflammatory foods. Zinc-rich pumpkin seeds, omega-3 fish, and colorful vegetables keep you at your best.",
            category = TipCategory.NUTRITION,
            applicablePhases = listOf(CyclePhase.OVULATION)
        ),
        HealthTip(
            title = "Go For Your Goals",
            emoji = "\uD83C\uDFC6",
            content = "You're at peak strength and endurance. Attempt personal records, compete, or try intense workouts. Your body recovers fastest right now.",
            category = TipCategory.EXERCISE,
            applicablePhases = listOf(CyclePhase.OVULATION)
        ),
        HealthTip(
            title = "Social Connection",
            emoji = "\uD83D\uDC9B",
            content = "Estrogen peaks make you naturally more social and confident. Plan dates, meetups, and team activities. Your communication skills are at their best.",
            category = TipCategory.MENTAL_HEALTH,
            applicablePhases = listOf(CyclePhase.OVULATION)
        ),

        // ── Luteal Phase Tips ───────────────────────────────────
        HealthTip(
            title = "Complex Carb Comfort",
            emoji = "\uD83C\uDF60",
            content = "Craving carbs? Choose complex ones: sweet potatoes, brown rice, oats. They boost serotonin naturally and provide sustained energy without sugar crashes.",
            category = TipCategory.NUTRITION,
            applicablePhases = listOf(CyclePhase.LUTEAL)
        ),
        HealthTip(
            title = "Gentle Is Powerful",
            emoji = "\uD83E\uDD3E",
            content = "Switch to moderate workouts: Pilates, yoga, swimming, or brisk walks. Your body needs recovery more than intensity in this phase.",
            category = TipCategory.EXERCISE,
            applicablePhases = listOf(CyclePhase.LUTEAL)
        ),
        HealthTip(
            title = "PMS Self-Care Kit",
            emoji = "\uD83C\uDF3F",
            content = "Prepare your PMS toolkit: heating pad, herbal tea, dark chocolate, a cozy playlist, and a journal. Having comfort items ready reduces stress.",
            category = TipCategory.SELF_CARE,
            applicablePhases = listOf(CyclePhase.LUTEAL)
        ),
        HealthTip(
            title = "Wind-Down Routine",
            emoji = "\uD83D\uDECC",
            content = "Progesterone's sedating effect can cause daytime drowsiness but nighttime restlessness. Set a consistent bedtime, avoid screens, and try lavender essential oil.",
            category = TipCategory.SLEEP,
            applicablePhases = listOf(CyclePhase.LUTEAL)
        ),
        HealthTip(
            title = "Mood Management",
            emoji = "\uD83E\uDDD8\u200D\u2640\uFE0F",
            content = "PMS mood shifts are real and valid. Practice box breathing (4-4-4-4), limit stressors where possible, and remind yourself this is temporary — your hormones are the cause, not you.",
            category = TipCategory.MENTAL_HEALTH,
            applicablePhases = listOf(CyclePhase.LUTEAL)
        ),

        // ── Universal Tips ──────────────────────────────────────
        HealthTip(
            title = "Hydration Matters Always",
            emoji = "\uD83D\uDCA7",
            content = "Aim for 8–10 glasses of water daily throughout your cycle. Proper hydration reduces bloating, headaches, fatigue, and cramps.",
            category = TipCategory.NUTRITION,
            applicablePhases = listOf(CyclePhase.MENSTRUAL, CyclePhase.FOLLICULAR, CyclePhase.OVULATION, CyclePhase.LUTEAL)
        ),
        HealthTip(
            title = "Track to Learn",
            emoji = "\uD83D\uDCCA",
            content = "The more consistently you track your symptoms, moods, and flow, the better you'll understand your unique patterns. Knowledge is empowerment.",
            category = TipCategory.SELF_CARE,
            applicablePhases = listOf(CyclePhase.MENSTRUAL, CyclePhase.FOLLICULAR, CyclePhase.OVULATION, CyclePhase.LUTEAL)
        )
    )

    /** Get tips filtered by the current cycle phase */
    fun getTipsForPhase(phase: CyclePhase): List<HealthTip> =
        healthTips.filter { phase in it.applicablePhases }

    /** Get the guide for a specific cycle phase */
    fun getPhaseGuide(phase: CyclePhase): PhaseGuide? =
        phaseGuides.find { it.phase == phase }

    /** Get the guide for a specific symptom */
    fun getSymptomGuide(symptom: Symptom): SymptomGuide? =
        symptomGuides.find { it.symptom == symptom }
}
