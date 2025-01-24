package temu.monitorzdrowia.ui.build

enum class ProfileField(val displayName: String, val genitiveName: String) {
    Name("Imię", "imienia"),
    Subname("Nazwisko", "nazwiska"),
    BirthDate("Data urodzenia", "daty urodzenia"),
    Sex("Płeć", "płci"),
    Address("Adres", "adresu"),
    CitySize("Rozmiar Aglomeracji", "rozmiaru aglomeracji")
}
