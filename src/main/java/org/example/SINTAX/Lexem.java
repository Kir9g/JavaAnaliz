package org.example.SINTAX;

import org.example.STATES.typid;

public class Lexem {
    public Integer id;
    public typid typid;
    public Boolean init;
    public Boolean declaration;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public org.example.STATES.typid getTypid() {
        return typid;
    }

    public void setTypid(org.example.STATES.typid typid) {
        this.typid = typid;
    }

    public Boolean getInit() {
        return init;
    }

    public void setInit(Boolean init) {
        this.init = init;
    }

    public Boolean getDescription() {
        return declaration;
    }

    public void setDescription(Boolean description) {
        this.declaration = description;
    }

    public Lexem(Integer id, org.example.STATES.typid typid, Boolean init, Boolean declaration) {
        this.id = id;
        this.typid = typid;
        this.init = init;
        this.declaration = declaration;
    }

    @Override
    public String toString() {
        return "table{" +
                "id=" + id +
                ", typid=" + typid +
                ", init=" + init +
                ", declaration=" + declaration +
                '}';
    }
}
