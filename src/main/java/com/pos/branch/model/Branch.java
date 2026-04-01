package com.pos.branch.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "branch")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(nullable = false, length = 300)
    private String address;

    @OneToMany(mappedBy = "branch")
    private List<POSTerminal> terminals;

    public Branch() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<POSTerminal> getTerminals() { return terminals; }
    public void setTerminals(List<POSTerminal> terminals) { this.terminals = terminals; }
}
