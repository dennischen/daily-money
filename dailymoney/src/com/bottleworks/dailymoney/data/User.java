package com.bottleworks.dailymoney.data;
/**
 * 
 * @author dennis
 *
 */
public class User {

    String name;
    
    String dbname;
    
    String note;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbname == null) ? 0 : dbname.hashCode());
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
        User other = (User) obj;
        if (dbname == null) {
            if (other.dbname != null)
                return false;
        } else if (!dbname.equals(other.dbname))
            return false;
        return true;
    }
    
    
}
