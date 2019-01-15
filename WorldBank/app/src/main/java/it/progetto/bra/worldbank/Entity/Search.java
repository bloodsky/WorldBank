package it.progetto.bra.worldbank.Entity;

import java.util.List;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class Search {

    private String country;
    private String topic;
    private String indicator;
    private String URL;
    private List<Chart> data;

    public Search(String country, String topic, String indicator, String URL, List<Chart> data) {
        this.country = country;
        this.topic = topic;
        this.indicator = indicator;
        this.URL = URL;
        this.data = data;
    }

    public List<Chart> getData() {
        return data;
    }

    public void setData(List<Chart> data) {
        this.data = data;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
