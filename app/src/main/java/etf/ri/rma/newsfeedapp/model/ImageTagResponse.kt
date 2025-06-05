package etf.ri.rma.newsfeedapp.model

data class ImageTagResponse(
    val result: Result
)

data class Result(
    val tags: List<Tag>
)

data class Tag(
    val tag: TagValue?
)

data class TagValue(
    val en: String?
)
