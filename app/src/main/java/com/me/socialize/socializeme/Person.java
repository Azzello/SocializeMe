package com.me.socialize.socializeme;

/**
 * Created by Toni on 9.6.2015..
 */
public class Person {
    String m_Firstname;
    String m_Lastname;
    String m_Country;
    String m_Email;
    public Person(String Firstname, String Lastname, String Country, String Email)
    {
        m_Firstname = Firstname;
        m_Lastname = Lastname;
        m_Country = Country;
        m_Email = Email;
    }
    public Person()
    {
        //default
    }
    static Person ParsePerson(String PersonString)
    {
        String[] PersonData = PersonString.split(" ");
        Person TempPerson = new Person(PersonData[0],PersonData[1],PersonData[2],PersonData[3]);
        return TempPerson;
    }
}
