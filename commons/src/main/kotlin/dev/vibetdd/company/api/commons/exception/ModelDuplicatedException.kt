package dev.vibetdd.api.commons.exception

import kotlin.reflect.KClass

class ModelDuplicatedException(val model: KClass<*>, val params: Map<String, Any> = mapOf()) :
    RuntimeException("Model '${model.simpleName} is duplicated" + addParams(params))

private fun addParams(params: Map<String, Any>): String =
    if (params.isEmpty()) ""
    else " params: $params"