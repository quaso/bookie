package org.bookie.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "Organization")
public class Organization extends AbstractEntity {

	@Column(name = "name", nullable = false, unique = true)
	private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

	@Column(name = "phone", nullable = true)
	private String phone;

	@Column(name = "email", nullable = true)
	private String email;

	public void setName(final String value) {
		this.name = value;
	}

	public String getName() {
		return this.name;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

	@Override
	public String toString() {
		return "Organization [name=" + this.name + "]";
	}
}
