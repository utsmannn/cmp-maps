package entity.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponse(
    @SerialName("items")
    val items: List<Item?>? = null
) {
    @Serializable
    data class Item(
        @SerialName("access")
        val access: List<Acces?>? = null,
        @SerialName("address")
        val address: Address? = null,
        @SerialName("categories")
        val categories: List<Category?>? = null,
        @SerialName("distance")
        val distance: Int? = null,
        @SerialName("id")
        val id: String? = null,
        @SerialName("language")
        val language: String? = null,
        @SerialName("position")
        val position: Position? = null,
        @SerialName("references")
        val references: List<Reference?>? = null,
        @SerialName("resultType")
        val resultType: String? = null,
        @SerialName("title")
        val title: String? = null
    ) {
        @Serializable
        data class Acces(
            @SerialName("lat")
            val lat: Double? = null,
            @SerialName("lng")
            val lng: Double? = null
        )

        @Serializable
        data class Address(
            @SerialName("city")
            val city: String? = null,
            @SerialName("countryCode")
            val countryCode: String? = null,
            @SerialName("countryName")
            val countryName: String? = null,
            @SerialName("county")
            val county: String? = null,
            @SerialName("countyCode")
            val countyCode: String? = null,
            @SerialName("district")
            val district: String? = null,
            @SerialName("label")
            val label: String? = null,
            @SerialName("postalCode")
            val postalCode: String? = null,
            @SerialName("street")
            val street: String? = null,
            @SerialName("subdistrict")
            val subdistrict: String? = null
        )

        @Serializable
        data class Category(
            @SerialName("id")
            val id: String? = null,
            @SerialName("name")
            val name: String? = null,
            @SerialName("primary")
            val primary: Boolean? = null
        )

        @Serializable
        data class Position(
            @SerialName("lat")
            val lat: Double? = null,
            @SerialName("lng")
            val lng: Double? = null
        )

        @Serializable
        data class Reference(
            @SerialName("id")
            val id: String? = null,
            @SerialName("supplier")
            val supplier: Supplier? = null
        ) {
            @Serializable
            data class Supplier(
                @SerialName("id")
                val id: String? = null
            )
        }
    }
}