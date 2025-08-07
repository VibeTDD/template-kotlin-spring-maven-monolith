## Query Parameters and Filtering

### Common Objects

There are common objects that must be used. Use the package `base_package.api.commons.dto.request` Never duplicate them.

```kotlin
data class PageQueryV1(
    val number: Int = 0,
    val size: Int = 50
)

data class SortQueryV1(
    val field: String,
    val order: String
)

data class RangeV1<T>(
    val from: T?,
    val to: T?,
)

// extend the interface for page request
interface PageParamsV1 {
    val page: PageQueryV1
    val sort: SortQueryV1
}
```


### Simple Queries: GET with Query Parameters
For simple queries, use GET requests with query parameters.

```kotlin
data class GetUsersQueryV1(
    override val page: PageQueryV1 = PageQueryV1(),
    override val sort: SortQueryV1 = SortQueryV1(),
    val statuses: Set<String> = setOf()
) : PageParamsV1

class UserControllerV1 {
    fun list(query: GetUsersQueryV1): PageV1<ModelV1<UserV1>> {
        // URL: /v1/users?page.number=1&page.size=50&sort.field=createdAt&sort.order=ASC&statuses=active
        return processGetUsers(query)
    }
}
```
Note: Always use arrays for simple filters that are empty by default, examples:
- `statuses: Set<String>` NOT `status: String`
- `ageRange: Range<Int>?` it is ok, the filter is an object

### 5.2 Complex Queries: POST with JSON Body
For complex filtering, use POST requests with JSON body.

```kotlin
data class SearchUsersParamsV1(
    override val page: PageQueryV1 = PageQueryV1(),
    override val sort: SortQueryV1 = SortQueryV1(),
    val filters: UserFiltersV1
) : PageParamsV1

data class UserFiltersV1(
    val dateRange: RangeV1<LocalDate>? = null,
    val statuses: List<String> = emptyList(),
    val ageRange: Range<Int>? = null
)
```