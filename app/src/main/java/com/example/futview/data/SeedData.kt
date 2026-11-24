package com.example.futview.data

/**
 * Dataset inicial de la Liga de Primera 2026 (equipos y estadios reales,
 * según Wikipedia/ANFP) para poblar Firestore desde el panel admin.
 *
 * No son datos de UI: se escriben a Firestore y de ahí se muestran a la app.
 * Cualquier dato se puede editar después desde el panel de administración.
 */
object SeedData {

    data class ClubSeed(
        val name: String,
        val short: String,
        val city: String,
        val stadiumName: String,
        val stadiumAddress: String,
        val latitude: Double,
        val longitude: Double,
        val capacity: Int
    )

    data class ResultSeed(
        val date: String,          // yyyy-MM-dd
        val home: String,          // nombre del club (según SeedData.clubs)
        val away: String,
        val homeGoals: Int,
        val awayGoals: Int
    )

    /** Los 16 clubes de la Liga de Primera 2026. */
    val clubs = listOf(
        ClubSeed(
            "Audax Italiano", "AUD", "La Florida",
            "Bicentenario de La Florida", "Av. Walker Martínez 2150, La Florida",
            -33.4971, -70.5872, 11637
        ),
        ClubSeed(
            "Cobresal", "COB", "El Salvador",
            "El Cobre", "Av. Manuel Rodríguez 117, El Salvador",
            -26.2444, -69.6542, 11240
        ),
        ClubSeed(
            "Colo-Colo", "CCO", "Macul",
            "Monumental David Arellano", "Av. Marathon 5300, Macul",
            -33.5066, -70.6054, 43667
        ),
        ClubSeed(
            "Coquimbo Unido", "COQ", "Coquimbo",
            "Francisco Sánchez Rumoroso", "Av. Costanera s/n, Coquimbo",
            -29.9584, -71.3386, 15809
        ),
        ClubSeed(
            "Deportes Concepción", "DCO", "Concepción",
            "Ester Roa Rebolledo", "Av. Paicaví 1885, Concepción",
            -36.8224, -73.0329, 30448
        ),
        ClubSeed(
            "Deportes La Serena", "LSE", "La Serena",
            "La Portada", "Av. Amunátegui 2950, La Serena",
            -29.9152, -71.2561, 17134
        ),
        ClubSeed(
            "Deportes Limache", "LIM", "Quillota",
            "Lucio Fariña Fernández", "Av. Prat 380, Quillota",
            -32.8794, -71.2422, 7680
        ),
        ClubSeed(
            "Everton", "EVE", "Viña del Mar",
            "Sausalito", "Av. Jorge Washington 100, Viña del Mar",
            -33.0246, -71.4962, 21754
        ),
        ClubSeed(
            "Huachipato", "HUA", "Talcahuano",
            "Huachipato-CAP Acero", "Av. Guillermo Julián 500, Talcahuano",
            -36.7342, -73.1556, 10032
        ),
        ClubSeed(
            "Ñublense", "NUB", "Chillán",
            "Nelson Oyarzún Arenas", "Av. Argentina 200, Chillán",
            -36.6017, -72.1008, 11319
        ),
        ClubSeed(
            "O'Higgins", "OHG", "Rancagua",
            "El Teniente", "Av. Pardo 1301, Rancagua",
            -34.1703, -70.7408, 12476
        ),
        ClubSeed(
            "Palestino", "PAL", "La Cisterna",
            "Municipal de La Cisterna", "Av. El Parrón 999, La Cisterna",
            -33.5233, -70.6644, 8000
        ),
        ClubSeed(
            "Unión La Calera", "ULC", "La Calera",
            "Nicolás Chahuán Nazar", "La Calera, s/n",
            -32.7856, -71.2014, 8353
        ),
        ClubSeed(
            "Universidad Católica", "UCA", "Las Condes",
            "Claro Arena", "Av. Padre Hurtado 1000, Las Condes",
            -33.4141, -70.5166, 20249
        ),
        ClubSeed(
            "Universidad de Chile", "UCH", "Ñuñoa",
            "Nacional Julio Martínez Prádanos", "Av. Grecia 2001, Ñuñoa",
            -33.4646, -70.6106, 46190
        ),
        ClubSeed(
            "Universidad de Concepción", "UDC", "Concepción",
            "Ester Roa Rebolledo", "Av. Paicaví 1885, Concepción",
            -36.8224, -73.0329, 30448
        )
    )

    /** Resultados reales de la temporada 2026 (destacados de la página oficial de Wikipedia/ESPN). */
    val results = listOf(
        ResultSeed("2026-02-13", "Everton", "Huachipato", 0, 3),
        ResultSeed("2026-03-15", "Cobresal", "Deportes Limache", 2, 5),
        ResultSeed("2026-03-31", "Huachipato", "Universidad Católica", 0, 3),
        ResultSeed("2026-05-17", "Colo-Colo", "Ñublense", 6, 2),
        ResultSeed("2026-10-12", "Universidad de Chile", "Ñublense", 6, 0)
    )
}
