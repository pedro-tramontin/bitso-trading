package br.com.pedront.bitsotrading;

public class Subscribe {
    private String action;

    private String book;

    private String type;

    public Subscribe(String action, String book, String type) {
        this.action = action;
        this.book = book;
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Subscribe{" +
                "action='" + action + '\'' +
                ", book='" + book + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
