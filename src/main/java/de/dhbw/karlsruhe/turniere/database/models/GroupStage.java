package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OrderBy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;


@Entity
@Table(name = "group_stages")
@Getter
@Setter
@NoArgsConstructor

public class GroupStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    @OrderBy(clause = "position ASC")
    private List<Group> groups;
    private int playoffSize;

    public GroupStage(List<Group> groups) {
        this.groups = groups;
    }

}
