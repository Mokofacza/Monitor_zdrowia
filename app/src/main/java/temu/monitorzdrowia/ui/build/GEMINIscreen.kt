package temu.monitorzdrowia.ui.build

import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel

class GEMINIscreen {
    val generativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.apiKey
    )
}