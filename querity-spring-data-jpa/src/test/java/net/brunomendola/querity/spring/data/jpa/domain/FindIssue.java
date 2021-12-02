package net.brunomendola.querity.spring.data.jpa.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindIssue extends AbstractPersistable<Long> {

    @NonNull
    private String taaaac;
}
