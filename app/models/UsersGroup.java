package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class UsersGroup extends Model {
	
	//Champs relatifs au group
	public String name;
	public String desc;
	public Long owner;
	

    //Liste des membres
	@Lob
    public ArrayList<Long> members;
	
	public ArrayList<Long> getMembers(){
		return this.members;
	}
	public void setMembers(ArrayList<Long> members){
		this.members = members;
	}
	public void addMember(Long id){
		this.members.add(id);
	}
	public void deleteMember(Long id){
	    this.members.remove(this.members.indexOf(id));
	}
	
	public UsersGroup(String name, String desc, Long id) {
	    this.name = name;
	    this.desc = desc;
	    this.owner = id;
	    members = new ArrayList<Long>(600);
	    this.addMember(id);
	}
	
	public void deleteGroup(Long id){
	    User user = User.find("byId", id).first();
	    if(this.owner.equals(id) || user.isAdmin ==1){
	        this.finalize();
	    }
	}
	public void finalize(){
	    members.clear();
	}
}
