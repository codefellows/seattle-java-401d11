package com.codefellows.salmonCookies2.models;

public class SalmonCookiesStore
{
    String name;
    int averageCookiesPerDay;

    public SalmonCookiesStore(String name, int averageCookiesPerDay)
    {
        this.name = name;
        this.averageCookiesPerDay = averageCookiesPerDay;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAverageCookiesPerDay()
    {
        return averageCookiesPerDay;
    }

    public void setAverageCookiesPerDay(int averageCookiesPerDay)
    {
        this.averageCookiesPerDay = averageCookiesPerDay;
    }
}
