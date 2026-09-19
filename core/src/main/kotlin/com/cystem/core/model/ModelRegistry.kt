package com.cystem.core.model

object ModelRegistry {
    // NVIDIA NIM Models (Verified Current IDs)
    const val NEMOTRON_3_SUPER = "nvidia/nemotron-3-super-120b-a12b"
    const val NEMOTRON_3_NANO_OMNI = "nvidia/nemotron-3-nano-omni-30b-a3b-reasoning"
    const val NEMOTRON_3_ULTRA = "nvidia/nemotron-3-ultra-550b-a55b"
    const val LLAMA_3_1_NEMOTRON_70B = "nvidia/llama-3.1-nemotron-70b-instruct"

    // GOOGLE GEMINI MODELS (Sept 2026 - Post 1.5/2.5 Shutdown)
    // Gemini 3.0 Series is current flagship.
    const val GEMINI_3_FLASH = "gemini-3.0-flash"       // Main workhorse: Search, Tools, 1M+ Context
    const val GEMINI_3_PRO = "gemini-3.0-pro"           // Higher reasoning
    const val GEMINI_3_FLASH_IMAGE = "gemini-3.1-flash-image" // Native Image Generation (Replaces Imagen)

    const val DEFAULT_MAIN_MODEL = NEMOTRON_3_SUPER

    data class ModelInfo(
        val id: String,
        val displayName: String,
        val provider: Provider,
        val capabilities: Set<Capability>,
        val maxContext: Int,
        val supportsReasoning: Boolean,
        val supportsVision: Boolean,
        val supportsSearchGrounding: Boolean = false
    )

    enum class Provider { NVIDIA, GOOGLE }
    enum class Capability { 
        Chat, Reasoning, Vision, ToolCalling, 
        SearchGrounding, ImageGeneration, CodeExecution 
    }

    val ALL_MODELS = mapOf(
        NEMOTRON_3_SUPER to ModelInfo(
            NEMOTRON_3_SUPER, "Nemotron 3 Super", Provider.NVIDIA, 
            setOf(Chat, Reasoning, ToolCalling), 128_000, true, false
        ),
        NEMOTRON_3_NANO_OMNI to ModelInfo(
            NEMOTRON_3_NANO_OMNI, "Nemotron 3 Nano Omni", Provider.NVIDIA, 
            setOf(Chat, Reasoning, Vision, ToolCalling), 64_000, true, true
        ),
        NEMOTRON_3_ULTRA to ModelInfo(
            NEMOTRON_3_ULTRA, "Nemotron 3 Ultra", Provider.NVIDIA, 
            setOf(Chat, Reasoning, ToolCalling), 128_000, true, false
        ),
        LLAMA_3_1_NEMOTRON_70B to ModelInfo(
            LLAMA_3_1_NEMOTRON_70B, "Llama 3.1 Nemotron 70B", Provider.NVIDIA, 
            setOf(Chat, ToolCalling), 128_000, false, false
        ),
        GEMINI_3_FLASH to ModelInfo(
            GEMINI_3_FLASH, "Gemini 3.0 Flash", Provider.GOOGLE, 
            setOf(Chat, Vision, ToolCalling, SearchGrounding, CodeExecution), 
            2_000_000, false, true, true
        ),
        GEMINI_3_PRO to ModelInfo(
            GEMINI_3_PRO, "Gemini 3.0 Pro", Provider.GOOGLE, 
            setOf(Chat, Vision, ToolCalling, SearchGrounding, CodeExecution), 
            2_000_000, true, true, true
        ),
        GEMINI_3_FLASH_IMAGE to ModelInfo(
            GEMINI_3_FLASH_IMAGE, "Gemini 3.1 Flash Image", Provider.GOOGLE, 
            setOf(ImageGeneration), 0, false, false
        )
    )

    fun getModelInfo(id: String): ModelInfo? = ALL_MODELS[id]
    
    fun getModelsByProvider(provider: Provider): List<ModelInfo> = 
        ALL_MODELS.values.filter { it.provider == provider }.toList()
    
    fun getMainModels(): List<ModelInfo> = 
        ALL_MODELS.values.filter { Chat in it.capabilities && it.id != GEMINI_3_FLASH_IMAGE }.toList()
    
    fun getVisionModels(): List<ModelInfo> = 
        ALL_MODELS.values.filter { Vision in it.capabilities }.toList()
    
    fun getSearchModels(): List<ModelInfo> = 
        ALL_MODELS.values.filter { SearchGrounding in it.capabilities }.toList()
    
    fun getImageGenModels(): List<ModelInfo> = 
        ALL_MODELS.values.filter { ImageGeneration in it.capabilities }.toList()

    /**
     * Returns the default model for a specific capability.
     * Used by the Router when user doesn't explicitly select.
     */
    fun getDefaultForCapability(capability: Capability): ModelInfo = when (capability) {
        SearchGrounding -> ALL_MODELS[GEMINI_3_FLASH]!!
        Vision -> ALL_MODELS[NEMOTRON_3_NANO_OMNI]!!
        ImageGeneration -> ALL_MODELS[GEMINI_3_FLASH_IMAGE]!!
        Reasoning -> ALL_MODELS[NEMOTRON_3_ULTRA]!!
        else -> ALL_MODELS[DEFAULT_MAIN_MODEL]!!
    }
}
