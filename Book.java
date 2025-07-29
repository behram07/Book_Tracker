package com.paydex.book_tracker;

public class Book {
    private String author;
    private String title;
    private int totalPage;
    private int pagesRead;

    public Book(String author, String title, int totalPage, int pagesRead){
        this.author = author;
        this.title = title;
        this.totalPage = totalPage;
        this.pagesRead = pagesRead;
    }
    public String getAuthor(){
        return author;
    }
    public String getTitle(){
        return title;
    }
    public int getTotalPage(){
        return totalPage;
    }
    public int getPagesRead(){
        return pagesRead;
    }
    public void setPagesRead(int pagesRead){
        this.pagesRead = pagesRead;
    }
}
