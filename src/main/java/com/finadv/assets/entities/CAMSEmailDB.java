package com.finadv.assets.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author atanu
 *
 */
@Entity
@Table(name = "cams_email")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CAMSEmailDB {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cams_email_id")
	private int id;
	
	@Column(name = "user_id")
	private long userId;

	@Column(name = "password")
	private String password;

	@Column(name = "email")
	private String email;

	@Column(name = "created_on")
	private Date createdAt;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
