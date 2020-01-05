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
package se.kth.id1212.appserv.bank.presentation.acct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.kth.id1212.appserv.bank.application.BankService;
import se.kth.id1212.appserv.bank.domain.ConvertCurrencyForm;
import se.kth.id1212.appserv.bank.domain.Currency;
import se.kth.id1212.appserv.bank.domain.ExchangeRate;
import se.kth.id1212.appserv.bank.repository.CurrencyRepository;
import se.kth.id1212.appserv.bank.repository.ExchangeRateRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all HTTP requests to context root.
 */
@Controller
@Scope("session")
public class ExchangeController {
    static final java.lang.String DEFAULT_PAGE_URL = "/";
    static final java.lang.String CONVERTER_HOME_URL = "converter";
    static final java.lang.String CONVERTED_URL = "converted";

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private List<Currency> currencies;




    @Autowired
    private BankService service;

    public ExchangeController(CurrencyRepository currencyRepository, ExchangeRateRepository exchangeRateRepository){
        this.currencyRepository = currencyRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        currencies = (ArrayList<Currency>)currencyRepository.findAll();
    }

    /**
     * No page is specified, redirect to the welcome page.
     *
     * @return A response that redirects the browser to the welcome page.
     */
    @GetMapping(DEFAULT_PAGE_URL)
    public String showDefaultView() {
        System.out.println("Call to default page");

        return "redirect:" + CONVERTER_HOME_URL;
    }


    @GetMapping("/" + CONVERTER_HOME_URL)
    public String showConverterHomeView(Model model, ConvertCurrencyForm convertCurrencyForm) {
        model.addAttribute("currencies",currencies);
        System.out.println("Call to home page");
        return CONVERTER_HOME_URL;
    }

    @PostMapping("/" + "converted")
    public String showConvertedResultView(@ModelAttribute ConvertCurrencyForm convertCurrencyForm, BindingResult bindingResult, Model model) {
        System.out.println("Call to convert page");
        Currency fromCurrency = currencyRepository.findByName(convertCurrencyForm.getFromCurrency());
        Currency toCurrency =  currencyRepository.findByName(convertCurrencyForm.getToCurrency());
        double amount = convertCurrencyForm.getAmount();
        ExchangeRate rate = exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
        double result = rate.getRate() * amount;
        System.out.println("Conversion amount calculated as " + result);
        ExchangeValueForm  exchangeValueForm = new ExchangeValueForm(convertCurrencyForm.getFromCurrency(),convertCurrencyForm.getToCurrency(),rate.getRate(),result);
        model.addAttribute("exchangeValueForm",exchangeValueForm);
        return CONVERTED_URL;
    }


    @ModelAttribute("allCurrencies")
    public List<Currency> allCurrencies() {
        return currencies;
    }


}
