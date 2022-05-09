data class Resource(
    val key: String,
    val value: String
) {
    override fun toString(): String {
        return "$key=$value"
    }
}
