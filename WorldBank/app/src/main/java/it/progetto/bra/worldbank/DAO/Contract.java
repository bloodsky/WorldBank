package it.progetto.bra.worldbank.DAO;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public final class Contract {

    Contract() {}
    public static abstract class SearchEntry {
        public static final String COUNTRY = "country";
        public static final String TOPIC = "topic";
        public static final String INDICATOR = "indicator";
        public static final String URL = "url";
        public static final String DATA = "data";
        public static final String TABLE_NAME = "search_table";
    }
}
