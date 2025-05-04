package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem

object NewsData {
    fun getAllNews(): List<NewsItem> {
        return listOf(
            NewsItem("1", "Izbori se bliže 2025", "Aktuelne informacije o izborima 2025. god.", null, "Politika", false, "Dnevni Avaz", "01-04-2025"),
            NewsItem("2", "Rast ekonomije izvještaj", "BDP značajno porastao u prvom kvartalu godine.", null, "Politika", true, "Oslobođenje", "02-04-2025"),
            NewsItem("3", "Rezultati fudbalskog prvenstva", "Najzanimljiviji trenuci utakmice koju pamtimo.", null, "Sport", false, "Klix.ba", "03-04-2025"),
            NewsItem("4", "Tehnologija 2025 inovacije", "Nova dostignuća u oblasti AI i robotike.", null, "Nauka/tehnologija", false, "TechRadar", "04-04-2025"),
            NewsItem("5", "Parlament raspravlja o reformama", "Diskusije o ključnim zakonima u tijeku.", null, "Politika", false, "N1", "05-04-2025"),
            NewsItem("6", "Finale košarke donijelo dramu", "Navijači svjedočili uzbudljivom meču.", null, "Sport", true, "Sportske.net", "06-04-2025"),
            NewsItem("7", "SpaceX lansira novi rover", "Uspješno lansiranje misije za Mars.", null, "Nauka/tehnologija", true, "NASA News", "07-04-2025"),
            NewsItem("8", "Građani masovno glasali", "Rekordan odziv na lokalnim izborima.", null, "Politika", false, "BHT1", "08-04-2025"),
            NewsItem("9", "Pripreme za Olimpijadu počele", "Sportisti treniraju širom svijeta.", null, "Sport", false, "OlympicChannel", "09-04-2025"),
            NewsItem("10", "Vještačka inteligencija u zdravstvu", "AI poboljšava dijagnoze i tretmane.", null, "Nauka/tehnologija", false, "ScienceDaily", "10-04-2025"),
            NewsItem("11", "Premijer najavio budžet", "Planovi za narednu fiskalnu godinu predstavljeni.", null, "Politika", true, "BBC", "11-04-2025"),
            NewsItem("12", "Transferi u fudbalu šokirali sve", "Nevjerovatne promjene u klubovima.", null, "Sport", true, "ESPN", "12-04-2025"),
            NewsItem("13", "Klimatske promjene u fokusu", "Nova istraživanja zabrinjavaju stručnjake.", null, "Nauka/tehnologija", false, "Greenpeace", "13-04-2025"),
            NewsItem("14", "Nova imena u Vladi", "Ministri zvanično imenovani u novoj strukturi.", null, "Politika", false, "Reuters", "14-04-2025"),
            NewsItem("15", "Obilježen Dan sporta širom BiH", "Sportske aktivnosti širom zemlje.", null, "Sport", false, "Sport Centar", "15-04-2025"),
            NewsItem("16", "MIT najavio kvantni napredak", "Revolucija u kvantnom računarstvu.", null, "Nauka/tehnologija", true, "MIT News", "16-04-2025"),
            NewsItem("17", "Stranke postigle koaliciju", "Zvanično potpisan dogovor o saradnji.", null, "Politika", false, "Al Jazeera", "17-04-2025"),
            NewsItem("18", "Pregled polufinala Lige prvaka", "Napetost raste pred veliko finale.", null, "Sport", false, "UEFA", "18-04-2025"),
            NewsItem("19", "Nova vakcina odobrena", "Ispitivanja u trećoj fazi dala rezultate.", null, "Nauka/tehnologija", false, "WHO", "19-04-2025"),
            NewsItem("20", "Derbi bez golova ali uzbuđenje", "Sarajevo i Željo podijelili bodove pred punim tribinama.", null, "Sport", false, "SportSport.ba", "20-04-2025")
        )
    }
}
