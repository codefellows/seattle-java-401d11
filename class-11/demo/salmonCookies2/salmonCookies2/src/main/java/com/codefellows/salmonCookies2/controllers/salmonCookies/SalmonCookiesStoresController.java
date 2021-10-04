package com.codefellows.salmonCookies2.controllers.salmonCookies;

import com.codefellows.salmonCookies2.models.SalmonCookiesStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class SalmonCookiesStoresController
{
    @GetMapping("/")
    public String getSalmonCookiesStores(Model m)
    {
        SalmonCookiesStore bestStore = new SalmonCookiesStore("Best Salmon Cookies", 5);
        SalmonCookiesStore worstStore = new SalmonCookiesStore("Worst Salmon Cookies Ever", 5000);

        ArrayList<SalmonCookiesStore> stores = new ArrayList<>();
        stores.add(bestStore);
        stores.add(worstStore);
        m.addAttribute("stores", stores);

        return "salmon-cookies/salmon-cookies-stores";
    }
}
