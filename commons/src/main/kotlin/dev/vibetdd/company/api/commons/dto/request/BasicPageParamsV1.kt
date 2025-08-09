package dev.vibetdd.api.commons.dto.request

data class BasicPageParamsV1(
    override val page: PageQueryV1 = PageQueryV1(),
    override val sort: SortQueryV1 = SortQueryV1(),
) : PageParamsV1
