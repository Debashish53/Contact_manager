package mypack.contact.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="PHONENUMBERS")
public class PhoneNumbers {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int pId;
	private int numbers;
	@ManyToOne
	private Contact contact;
	public PhoneNumbers(int pId, int numbers, Contact contact) {
		super();
		this.pId = pId;
		this.numbers = numbers;
		this.contact = contact;
	}
	public PhoneNumbers() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getpId() {
		return pId;
	}
	public void setpId(int pId) {
		this.pId = pId;
	}
	public int getNumbers() {
		return numbers;
	}
	public void setNumbers(int numbers) {
		this.numbers = numbers;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	
}
