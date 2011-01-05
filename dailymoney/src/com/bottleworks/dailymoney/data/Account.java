/**
 * 
 */
package com.bottleworks.dailymoney.data;

/**
 * @author dennis
 *
 */
public class Account {

    String id;
    
    String name;
    
    String accountType;
    
    double initialValue;
    
    
    public Account(String name,String accountType, Double initialValue){
        this.accountType = accountType;
        this.name = name==null?"":name.trim();
        this.initialValue = initialValue;
        id = this.name;
    }
    

    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name==null?"":name.trim();
    }


    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }



    public double getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(double initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        Account other = (Account) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    
    
    
}
