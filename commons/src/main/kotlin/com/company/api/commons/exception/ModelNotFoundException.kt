package com.company.api.commons.exception

import kotlin.reflect.KClass

class ModelNotFoundException(val model: KClass<*>, val params: Map<String, Any?> = mapOf()) :
    RuntimeException("Model '${model.simpleName} not found" + addParams(params))

private fun addParams(params: Map<String, Any?>): String =
    if (params.isEmpty()) ""
    else " params: $params"