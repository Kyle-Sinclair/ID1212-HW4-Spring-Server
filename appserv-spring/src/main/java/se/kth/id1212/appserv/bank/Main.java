/*
 * The MIT License
 *
 * Copyright 2018 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id1212.appserv.bank;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import se.kth.id1212.appserv.bank.domain.Currency;
import se.kth.id1212.appserv.bank.domain.ExchangeRate;
import se.kth.id1212.appserv.bank.repository.CurrencyRepository;
import se.kth.id1212.appserv.bank.repository.ExchangeRateRepository;

import okhttp3.*;

import java.io.IOException;

/**
 * Starts the bank application.
 */
@SpringBootApplication
public class Main implements CommandLineRunner {


    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private static final Gson gson = new Gson();


    @Autowired
    public Main(CurrencyRepository currencyRepository, ExchangeRateRepository rateRepository) {
        this.currencyRepository = currencyRepository;
        this.exchangeRateRepository = rateRepository;
    }


    private void saveCurrencies() {
        java.lang.String[] currencyNames = {
                "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "EUR", "GBP", "HKD", "HRK",
                "HUF", "IDR", "INR", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN",
                "RON", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"};

        for (java.lang.String name : currencyNames) {
            currencyRepository.save(new Currency(name));
            //System.out.print(currencyRepository.findByName(name).getName() + " ");
        }
    }

    public static void main(java.lang.String[] args) {
        SpringApplication.run(Main.class);
    }


    @Override
    public void run(java.lang.String... args) throws Exception {
        saveCurrencies();
        saveRates();

    }

    private void saveRates() throws IOException {
        for (Currency currency : currencyRepository.findAll()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://api.ratesapi.io/api/2010-01-12?base=" + currency.getName()).build();
            //https://data.fixer.io/api/latest?access_key=YOUR_ACCESS_KEY
            Response response = client.newCall(request).execute();
            java.lang.String rates = response.body().string();
            System.out.println(rates);
            JSONObject obj = new JSONObject(rates);
            JSONObject rateMap = obj.getJSONObject("rates");
            for (java.lang.String key: rateMap.keySet()) {
                if(currencyRepository.findByName(key) != null)
                    exchangeRateRepository.save(new ExchangeRate(rateMap.getDouble(key), currency, currencyRepository.findByName(key)));
            }
        }
        exchangeRateRepository.save(new ExchangeRate(1.0,currencyRepository.findByName("EUR"),currencyRepository.findByName("EUR")));

        for(ExchangeRate rate: exchangeRateRepository.findAll()){
            System.out.println(rate.toString());
        }
    }
}

//Access Token for Fixer API - d82e758c14e2871cffb873bfa8adcaa9

//Base Sek : {"base":"SEK","rates":{"GBP":0.0877757668,"HKD":1.0986743629,"IDR":1299.3337572763,"PHP":6.4673482366,"LVL":0.0693929462,"INR":6.4775228685,"CHF":0.1442351905,"MXN":1.8098615663,"SGD":0.196967177,"CZK":2.5688988896,"THB":4.6802328425,"BGN":0.1913417796,"EUR":0.0978329991,"MYR":0.4737465147,"NOK":0.8005185149,"CNY":0.9672063787,"HRK":0.7117644181,"PLN":0.3995304016,"LTL":0.3377977792,"TRY":0.2062710952,"ZAR":1.0591791811,"CAD":0.1463483833,"BRL":0.2476055373,"RON":0.4050775327,"DKK":0.7279264296,"NZD":0.1914885291,"EEK":1.5307538033,"JPY":12.9540674069,"RUB":4.1772146945,"KRW":159.2134226875,"USD":0.141671966,"AUD":0.1532847429,"HUF":26.2368536907,"SEK":1.0},"date":"2010-01-12"}
//Base GBP : {"base":"GBP","rates":{"GBP":1.0,"HKD":12.5168301382,"IDR":14802.8756130183,"PHP":73.6803388319,"LVL":0.7905706643,"INR":73.7962550156,"CHF":1.6432233616,"MXN":20.6191484619,"SGD":2.2439812751,"CZK":29.2666072225,"THB":53.3203299153,"BGN":2.1798930004,"EUR":1.1145786893,"MYR":5.3972358449,"NOK":9.1200401248,"CNY":11.0190592956,"HRK":8.1088943379,"PLN":4.5517164512,"LTL":3.8484172983,"TRY":2.3499777084,"ZAR":12.0668747214,"CAD":1.6672982613,"BRL":2.8208872046,"RON":4.6149130629,"DKK":8.2930227374,"NZD":2.1815648685,"EEK":17.4393669193,"JPY":147.5813642443,"RUB":47.5896121266,"KRW":1813.8653588943,"USD":1.6140213999,"AUD":1.7463218903,"HUF":298.9077128845,"SEK":11.3926660722},"date":"2010-01-12"}
//Base AUD : {"base":"AUD","rates":{"GBP":0.5726321164,"HKD":7.1675389329,"IDR":8476.6019913199,"PHP":42.1917283635,"LVL":0.4527061527,"INR":42.2581056931,"CHF":0.9409624713,"MXN":11.8071866224,"SGD":1.2849757467,"CZK":16.7589992341,"THB":30.5329333674,"BGN":1.2482767424,"EUR":0.6382435537,"MYR":3.0906305846,"NOK":5.2224278785,"CNY":6.3098672453,"HRK":4.6434133265,"PLN":2.6064590248,"LTL":2.2037273424,"TRY":1.3456727087,"ZAR":6.9098800102,"CAD":0.954748532,"BRL":1.6153306102,"RON":2.6426474343,"DKK":4.7488511616,"NZD":1.2492341077,"EEK":9.9863415879,"JPY":84.5098289507,"RUB":27.2513403115,"KRW":1038.6775593567,"USD":0.9242404902,"AUD":1.0,"HUF":171.164156242,"SEK":6.5238064846},"date":"2010-01-12"}
//Base EUR : {"base":"EUR","rates":{"GBP":0.8972,"HKD":11.2301,"IDR":13281.14,"PLN":4.0838,"DKK":7.4405,"LVL":0.7093,"INR":66.21,"CHF":1.4743,"MXN":18.4995,"CZK":26.258,"SGD":2.0133,"THB":47.839,"BGN":1.9558,"MYR":4.8424,"NOK":8.1825,"CNY":9.8863,"HRK":7.2753,"PHP":66.106,"SEK":10.2215,"LTL":3.4528,"ZAR":10.8264,"CAD":1.4959,"BRL":2.5309,"RON":4.1405,"EEK":15.6466,"NZD":1.9573,"TRY":2.1084,"JPY":132.41,"RUB":42.6974,"KRW":1627.4,"USD":1.4481,"HUF":268.18,"AUD":1.5668},"date":"2010-01-12"}