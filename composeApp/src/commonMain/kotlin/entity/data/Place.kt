package entity.data

data class Place(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val coordinate: Coordinate = Coordinate(),
    val distance: Int = -1
) {

    companion object {
        val Empty: Place = Place()
    }

    fun distanceOnKm(): String {
        val kilo = distance.toDouble() / 1000.0
        val kiloString = kilo.toString()
            .replace(".", ",") // 2.999832 -> 2,998923

        val kiloValueSplit = kiloString.split(",")
        val kiloBeforeComma = kiloValueSplit[0]
        val kiloAfterComma = (kiloValueSplit.getOrNull(1) ?: "0")
            .take(1)

        return "$kiloBeforeComma,$kiloAfterComma km"
    }
}