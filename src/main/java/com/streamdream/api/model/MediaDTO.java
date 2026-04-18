package com.streamdream.api.model;

public class MediaDTO {
    private String title;
    private Integer releaseYear;
    private String genre;
    private String posterUrl;
    private String description;
    public MediaDTO() {}

    public void setTitle(String title) { this.title = title; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setDescription(String description) { this.description = description; }

    public String getTitle() { return title; }
    public Integer getReleaseYear() { return releaseYear; }
    public String getGenre() { return genre; }
    public String getPosterUrl() { return posterUrl; }
    public String getDescription() { return description; }
}