package com.pos.branch.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pos_terminal")
public class POSTerminal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false, length = 50)
    private String name;

    public POSTerminal() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
