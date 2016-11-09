package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.io.Serializable;

import edu.uncfsu.softwaredesign.f16.r2.util.State;

public class Customer implements Serializable {
	
	private static final long serialVersionUID = -8355089713745721830L;
	
	private final String name;
	private final long phone;
	private final String email;
	private final String address;
	private final long zipCode;
	private final String city;
	private final State state;
	
	public Customer(String name, long phone, String email, String address, long zipCode, String city, State state) {
		super();
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.address = address;
		this.zipCode = zipCode;
		this.city = city;
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public long getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public String getAddress() {
		return address;
	}
	
	public long getZipCode() {
		return zipCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (phone ^ (phone >>> 32));
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + (int) (zipCode ^ (zipCode >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone != other.phone)
			return false;
		if (state != other.state)
			return false;
		if (zipCode != other.zipCode)
			return false;
		return true;
	}

	public String getCity() {
		return city;
	}

	public State getState() {
		return state;
	}
	
}
