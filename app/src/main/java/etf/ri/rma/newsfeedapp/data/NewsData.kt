package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem

object NewsData {
    fun getAllNews(): List<NewsItem> {
        return listOf(
            NewsItem("1", "Izbori se bliže", "Aktuelne informacije o nadolazećim izborima...", null, "Politika", false, "Dnevni Avaz", "2025-04-01"),
            NewsItem("2", "Izvještaj o ekonomskom rastu", "BDP raste u prvom kvartalu...", null, "Politika", true, "Oslobođenje", "2025-04-02"),
            NewsItem("3", "Rezultati prvenstva", "Najzanimljiviji trenuci sa sinoćnje utakmice...", null, "Sport", false, "Klix.ba", "2025-04-03"),
            NewsItem("4", "Nova tehnologija u 2025.", "Inovacije u AI i robotici mijenjaju svijet...", null, "Nauka/tehnologija", false, "TechRadar", "2025-04-04"),
            NewsItem("5", "Rasprave u parlamentu", "Ključne diskusije o reformama...", null, "Politika", false, "N1", "2025-04-05"),
            NewsItem("6", "Finale košarkaške lige", "Dramatičan završetak sezone...", null, "Sport", true, "Sportske.net", "2025-04-06"),
            NewsItem("7", "Lansiranje SpaceX-a", "Rover za Mars uspješno lansiran...", null, "Nauka/tehnologija", true, "NASA News", "2025-04-07"),
            NewsItem("8", "Lokalni izbori", "Građani u velikom broju izašli na birališta...", null, "Politika", false, "BHT1", "2025-04-08"),
            NewsItem("9", "Pripreme za Olimpijadu", "Otvoreni treninzi širom svijeta...", null, "Sport", false, "OlympicChannel", "2025-04-09"),
            NewsItem("10", "AI u zdravstvu", "Revolucija u dijagnostici bolesti...", null, "Nauka/tehnologija", false, "ScienceDaily", "2025-04-10"),
            NewsItem("11", "Govor premijera", "Planovi za novi budžet predstavljeni...", null, "Politika", true, "BBC", "2025-04-11"),
            NewsItem("12", "Transferi u fudbalu", "Najveći transferi ove sezone...", null, "Sport", true, "ESPN", "2025-04-12"),
            NewsItem("13", "Podaci o klimatskim promjenama", "Zabrinjavajući novi izvještaji...", null, "Nauka/tehnologija", false, "Greenpeace", "2025-04-13"),
            NewsItem("14", "Rekonstrukcija vlade", "Imenovani novi ministri...", null, "Politika", false, "Reuters", "2025-04-14"),
            NewsItem("15", "Dan sporta", "Manifestacije širom zemlje u čast sporta...", null, "Sport", false, "Sport Centar", "2025-04-15"),
            NewsItem("16", "Kvantno računarstvo", "MIT objavio veliko otkriće...", null, "Nauka/tehnologija", true, "MIT News", "2025-04-16"),
            NewsItem("17", "Koalicioni dogovor", "Stranke postigle sporazum...", null, "Politika", false, "Al Jazeera", "2025-04-17"),
            NewsItem("18", "Pregled Lige prvaka", "Svi detalji polufinala...", null, "Sport", false, "UEFA", "2025-04-18"),
            NewsItem("19", "Odobrena nova vakcina", "Pozitivni rezultati treće faze ispitivanja...", null, "Nauka/tehnologija", false, "WHO", "2025-04-19"),
            NewsItem("20", "Sarajevo i Željo bez pobjednika", "Derbi završen bez golova, ali sa odličnom atmosferom na stadionu Koševo.", null, "Sport", false, "SportSport.ba", "2025-04-20")
        )
    }
}
