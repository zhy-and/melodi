package com.extraordinary.weaves.tendency.bean;

public interface Sort extends Comparable<Sort> {
    void setSortLetter(String sortLetter);
    String getSortLetter();
}
