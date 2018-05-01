package com.beeblebroxlabs.storytime.logic;

/**
 * Created by devgr on 18-Jan-18.
 */

public class Story {

  private int id;
  private String title;
  private String content;
  private String photoUrl;
  private Boolean isVisible;



  public Story() {
  }

  public Story(int id, String title, String content, String photoUrl, Boolean isVisible) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.photoUrl = photoUrl;
    this.isVisible = isVisible;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }

  public Boolean getIsVisible() {
    return isVisible;
  }

  public void setIsVisible(Boolean visible) {
    isVisible = visible;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String toString(){
    return  "id:"+id+" title:"+title+" isVisible:"+isVisible;
  }
}
