package temu.monitorzdrowia.ui.build

sealed class ProfileField {
    object Name : ProfileField()
    object Subname : ProfileField()
    object BirthDate : ProfileField()
    object Sex : ProfileField()
    object Address : ProfileField()
    object CitySize : ProfileField()
}
// Pomocnicza funkcja do czytelnego wyświetlania nazwy pola
fun fieldToString(field: ProfileField): String {
    return when (field) {
        ProfileField.Name -> "Imię"
        ProfileField.Subname -> "Nazwisko"
        ProfileField.BirthDate -> "Data urodzenia"
        ProfileField.Sex -> "Płeć"
        ProfileField.Address -> "Adres"
        ProfileField.CitySize -> "Wielkość aglomeracji"
    }
}