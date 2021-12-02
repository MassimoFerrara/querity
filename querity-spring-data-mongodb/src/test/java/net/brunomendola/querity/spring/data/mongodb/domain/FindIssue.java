package net.brunomendola.querity.spring.data.mongodb.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindIssue {

    @NonNull
    private String taaaac;
}
