package com.ibm.security.appscan.altoromutual.model;

import java.util.Hashtable;

public class MoneyObject {

    static class Money implements Expression {
        protected double amount;
        protected String currency;

        public boolean equals(Object object) {
            Money money = (Money) object;
            return amount == money.amount
                    && currency().equals(money.currency());
        }
        static Money dollar(double amount) {
            return new Money(amount, "USD");
        }
        static Money franc(double amount) {
            return new Money(amount, "CHF");
        }
        
        Money(double amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }
        public Expression times(int multiplier) {
            return new Money(amount * multiplier, currency);
        }
        String currency() {
            return currency;
        }
        public String toString() {
            return amount + " " + currency;
        }
        public Expression plus(Expression addend) {
            return new Sum(this, addend);
        }
        public Money reduce(Bank bank, String to) {
            int rate = bank.rate(currency, to);
            return new Money(amount / rate, to);
        }
    }

    interface Expression{
        Money reduce(Bank bank, String to);
        Expression plus(Expression addend);
        Expression times(int multiplier);
    }

    class Bank{
        Money reduce(Expression source, String to) {
            return source.reduce(this, to);
        }
        int rate(String from, String to) {
            if (from.equals(to)) return 1;
            Integer rate= (Integer) rates.get(new Pair(from, to));
            return rate.intValue();
        }
        private Hashtable rates= new Hashtable();
        void addRate(String from, String to, int rate) {
            rates.put(new Pair(from, to), new Integer(rate));
        }
    }

    static class Sum implements Expression{
        Expression augend;
        Expression addend;
        Sum(Expression augend, Expression addend) {
            this.augend= augend;
            this.addend= addend;
        }
        public Money reduce(Bank bank, String to) {
            double amount= augend.reduce(bank, to).amount
                    + addend.reduce(bank, to).amount;
            return new Money(amount, to);
        }
        public Expression plus(Expression addend) {
            return new Sum(this, addend);
        }
        public Expression times(int multiplier) {
            return new Sum(augend.times(multiplier),addend.times(multiplier));
        }
    }

    private class Pair {
        private String from;
        private String to;
        Pair(String from, String to) {
            this.from= from;
            this.to= to;
        }
        public boolean equals(Object object) {
            Pair pair= (Pair) object;
            return from.equals(pair.from) && to.equals(pair.to);
        }
        public int hashCode() {
            return 0;
        }
    }
}


