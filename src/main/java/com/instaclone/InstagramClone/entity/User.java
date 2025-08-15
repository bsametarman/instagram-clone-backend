package com.instaclone.InstagramClone.entity;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false)
	private String passwordHash;
	
	@Column(nullable = false)
	private String firstName;
	
	@Column(nullable = false)
	private String lastName;
	
	@Column(nullable = true)
	private String bio;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedDate;
	
	@Column(nullable = false)
	private boolean isActive = true;
	
	@Column(name = "post_count", nullable = false)
	private int postCount = 0;
	
	@Column(name = "follower_count", nullable = false)
	private int followerCount = 0;
	
	@Column(name = "following_count", nullable = false)
	private int followingCount = 0;
	
	@JdbcTypeCode(Types.LONGVARBINARY)
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "profile_picture", nullable = true)
	private byte[] profilePicture;
	
	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
	@JsonIgnore
    private Set<Role> roles = new HashSet<>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonManagedReference("user-posts")
    private Set<Post> posts = new HashSet<>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonManagedReference("user-likes")
    private Set<Like> likes = new HashSet<>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonManagedReference("user-comments")
    private Set<Comment> comments = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "user_followers",
			joinColumns = @JoinColumn(name = "follower_id"),
			inverseJoinColumns = @JoinColumn(name = "following_id")
	)
	@JsonIgnoreProperties("followers")
	private Set<User> following = new HashSet<>();
	
	@ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("following")
	private Set<User> followers = new HashSet<>();
	
	
	public User(String username, String passwordHash, String email, String firstName, String lastName) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof User)) return false;
		
		User other = (User) o;
		return id != null && id.equals(other.id);
	}
	
	@Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + "]";
    }
}
