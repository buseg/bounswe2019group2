package tk.traiders.models;

import java.io.PipedReader;
import java.io.Serializable;

public class Article implements Serializable {

    private String url;
    private String title;
    private String createdAt;
    private String content;
    private String image;
    private User author;
    private String id;
    private String likeCount;
    private Like like;

    public Article(String url, String title, String createdAt, String content, String image, User author, String id, String likeCount, Like like) {
        this.url = url;
        this.title = title;
        this.createdAt = createdAt;
        this.content = content;
        this.image = image;
        this.author = author;
        this.id = id;
        this.likeCount = likeCount;
        this.like = like;
    }

    public String getUrl() {
        return url;
    }

    public Article setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Article setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Article setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Article setContent(String content) {
        this.content = content;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Article setImage(String image) {
        this.image = image;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public Article setAuthor(User author) {
        this.author = author;
        return this;
    }

    public String getId() {
        return id;
    }

    public Article setId(String id) {
        this.id = id;
        return this;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public Article setLikeCount(String likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public Like getLike() {
        return like;
    }

    public Article setLike(Like like) {
        this.like = like;
        return this;
    }

    @Override
    public String toString() {
        return "Article{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", author=" + author +
                ", id='" + id + '\'' +
                ", likeCount='" + likeCount + '\'' +
                ", like=" + like +
                '}';
    }
}
