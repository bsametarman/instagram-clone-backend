package com.instaclone.InstagramClone.dto.hashtag;

public class HashtagDto {
	private Long id;
    private String name;
    private int postCount;

    public HashtagDto() {
    }

    public HashtagDto(Long id, String name, int postCount) {
        this.id = id;
        this.name = name;
        this.postCount = postCount;
    }

    

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPostCount() {
        return postCount;
    }


    
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }
}
