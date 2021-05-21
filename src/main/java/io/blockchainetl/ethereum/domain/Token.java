package io.blockchainetl.ethereum.domain;

import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

import java.io.Serializable;
import java.util.Objects;

@DefaultCoder(AvroCoder.class)
public class Token  implements Serializable {
    String address;
    String symbol;
    String name;
    int decimals;

    public Token(String address, String symbol, String name, int decimals) {
        this.address = address;
        this.symbol = symbol;
        this.name = name;
        this.decimals = decimals;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return decimals == token.decimals &&
                Objects.equals(address, token.address) &&
                Objects.equals(symbol, token.symbol) &&
                Objects.equals(name, token.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, symbol, name, decimals);
    }

    @Override
    public String toString() {
        return "Token{" +
                "address='" + address + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", decimals=" + decimals +
                '}';
    }
}
